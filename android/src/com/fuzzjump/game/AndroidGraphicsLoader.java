package com.fuzzjump.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.caverock.androidsvg.SVG;
import com.fuzzjump.libgdxscreens.VectorGraphicsLoader;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class AndroidGraphicsLoader extends VectorGraphicsLoader {

    public AndroidGraphicsLoader(ExecutorService workerService, String cacheLocation) {
        super(workerService, cacheLocation);
    }

    public TextureRegion load(VectorGraphicsLoader.VectorDetails vectorDetails, final String svgMarkup, float targetWidth, float targetHeight, boolean cache) {
        Future<SVG> future = workerService.submit(new Callable<SVG>() {
            public SVG call() {
                SVG svg;
                try {
                    svg = SVG.getFromString(svgMarkup);
                } catch (Exception e) {
                    return null;
                }
                return svg;
            }
        });
        SVG svg = null;
        try {
            svg = future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        Vector2 size = calculateSize(targetWidth, targetHeight, svg.getDocumentAspectRatio());

        float scaleX = size.x / svg.getDocumentWidth();
        float scaleY = size.y / svg.getDocumentHeight();

        final Bitmap bitmap = Bitmap.createBitmap((int) size.x, (int) size.y, Bitmap.Config.ARGB_8888);
        Canvas bmcanvas = new Canvas(bitmap);

        bmcanvas.scale(scaleX, scaleY);

        bmcanvas.drawARGB(0, 255, 255, 255);
        svg.renderToCanvas(bmcanvas);

        Texture tex = new Texture(bitmap.getWidth(), bitmap.getHeight(), Format.RGBA8888);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tex.getTextureObjectHandle());
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] png = stream.toByteArray();
        if (cache)
            cache(png, vectorDetails);
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                bitmap.recycle();
            }
        });
        return new TextureRegion(tex);
    }

}
