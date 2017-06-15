package com.fuzzjump.game.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.fuzzjump.libgdxscreens.VectorGraphicsLoader;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.svg.SVGDocument;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class DesktopGraphicsLoader extends VectorGraphicsLoader {

    private final SAXSVGDocumentFactory factory;

    public DesktopGraphicsLoader(ExecutorService workerService, String cacheLocation) {
        super(workerService, cacheLocation);
        this.factory = new SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName());
    }

    @Override
    public TextureRegion load(VectorDetails vectorDetails, String svgMarkup, float targetWidth, float targetHeight, boolean cache) {

        ByteArrayInputStream is = new ByteArrayInputStream(svgMarkup.getBytes());
        SVGDocument document = null;

        try {
            document = factory.createSVGDocument(null, is);
        } catch (Exception e) {
            return null;
        } finally {
            try {
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        BridgeContext ctx = new BridgeContext(new UserAgentAdapter());
        GraphicsNode gvtRoot = (new GVTBuilder()).build(ctx, document);
        //batik why :(
        float width = document.getRootElement().getWidth().getBaseVal().getValue();
        float height = document.getRootElement().getHeight().getBaseVal().getValue();
        Vector2 size = calculateSize(targetWidth, targetHeight, (float) (width / height));

        final TranscoderInput input = new TranscoderInput(document);
        final ImageTranscoder transcoder = new PNGTranscoder();
        transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, size.x);
        transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, size.y);
        //transcoder.addTranscodingHint(PNGTranscoder.KEY_AOI, new Rectangle2D.Float(x, y, width, height));

        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        final TranscoderOutput output = new TranscoderOutput(os);
        Future<byte[]> future = workerService.submit(new Callable<byte[]>() {
            public byte[] call() {
                byte[] pngData;
                try {
                    transcoder.transcode(input, output);
                    pngData = os.toByteArray();
                    os.close();
                } catch (Exception e) {
                    return null;
                }
                return pngData;
            }
        });
        byte[] pngData = null;
        try {
            pngData = future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Pixmap pixmap = new Pixmap(pngData, 0, pngData.length);
        if (cache && Gdx.files.isLocalStorageAvailable())
            cache(pngData, vectorDetails);
        return new TextureRegion(new Texture(pixmap));
    }
}
