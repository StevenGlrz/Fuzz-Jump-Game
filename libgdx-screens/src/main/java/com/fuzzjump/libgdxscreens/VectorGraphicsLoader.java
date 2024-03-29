package com.fuzzjump.libgdxscreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.fuzzjump.libgdxscreens.graphics.ColorGroup;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class VectorGraphicsLoader {

    protected final ExecutorService cacheService;

    private final String cacheLocation;

    public VectorGraphicsLoader(String cacheLocation) {
        this.cacheLocation = cacheLocation;
        this.cacheService = Executors.newSingleThreadExecutor();
    }

    public TextureRegion load(final VectorDetail vectorDetails, ColorGroup replaceGroup, ColorGroup baseGroup, final boolean cache) {
        if (cache) {
            if (Gdx.files.isLocalStorageAvailable()) {
                FileHandle file = Gdx.files.local("pngcache/" + vectorDetails.atlas + ".png");
                try {
                    if (file.exists())
                        return new TextureRegion(new Texture(file));
                } catch (Exception e) {
                    e.printStackTrace();
                    file.delete();
                }
            }
        }

        final String name = vectorDetails.filename;
        float targetWidth = getValue(vectorDetails.width);
        float targetHeight = getValue(vectorDetails.height);

        String svgString = Gdx.files.internal(name).readString();

        if (replaceGroup != null) {
            for (int i = 0; i < replaceGroup.colors.length; i++) {
                ColorGroup.IndexColor colorToReplace = replaceGroup.colors[i];
                ColorGroup.IndexColor baseColor = baseGroup.colors[colorToReplace.index];

                svgString = svgString.replaceAll(baseColor.colorString, colorToReplace.colorString);
                svgString = svgString.replaceAll(baseColor.colorString.toUpperCase(), colorToReplace.colorString);
            }
        }
        FileHandle file = Gdx.files.internal(name);
        switch(vectorDetails.type) {
            case "svg":
                TextureRegion region = null;
                try {
                    String svgMarkup = file.readString();
                    region = load(vectorDetails, svgMarkup, targetWidth, targetHeight, cache);
                } catch (Exception e) {
                    System.out.println("Failed to load " + vectorDetails.filename);
                    e.printStackTrace();
                }
                return new TextureRegion(region);
            case "png":
            case "jpg":
                Texture texture = new Texture(file);
                return new TextureRegion(texture);
            default:
                throw new RuntimeException("Error loading TextureRegion, invalid file type");
        }
    }

    public TextureRegion load(final VectorDetail vectorDetail, final boolean cache) {
        return load(vectorDetail, null, null, cache);
    }

    public Vector2 calculateSize(float targetWidth, float targetHeight, float aspectRatio) {
        Vector2 size = new Vector2(targetWidth, targetHeight);
        if (targetWidth == Float.MIN_VALUE) {
            size.x = targetHeight * aspectRatio;
        } else if (targetHeight == Float.MIN_VALUE) {
            size.y = targetWidth / aspectRatio;
        }
        return size;
    }

    protected void cache(final byte[] data, final VectorDetail vectorDetail) {
        cacheService.submit(new Runnable() {
            public void run() {
                FileHandle file = Gdx.files.local(cacheLocation + "/pngcache/" + vectorDetail.getAtlasName() + ".png");
                Pixmap pixmap = new Pixmap(data, 0, data.length);
                try {
                    PixmapIO.writePNG(file, pixmap);
                    pixmap.dispose();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private float getValue(String value) {
        if (value.equals("asp"))
            return Float.MIN_VALUE;
        String[] params = value.split(":");
        float floatValue = Float.parseFloat(params[1]);
        switch (params[0]) {
            case "screen_width":
                return Gdx.graphics.getWidth() * floatValue;
            case "screen_height":
                return Gdx.graphics.getHeight() * floatValue;
            case "absolute":
            default:
                return floatValue;
        }
    }

    public abstract TextureRegion load(VectorDetail vectorDetail, String svgMarkup, float targetWidth, float targetHeight, boolean cache);

    public static class VectorDetail {

        protected String filename;
        protected String atlas;
        protected String width;
        protected String height;
        protected String type;

        public VectorDetail(String filename, String atlas, String width, String height) {
            this.filename = filename;
            this.atlas = atlas;
            this.width = width;
            this.height = height;
            this.type = filename.substring(filename.lastIndexOf('.')+1).trim();
        }

        public String getAtlasName() {
            return atlas;
        }
    }
}
