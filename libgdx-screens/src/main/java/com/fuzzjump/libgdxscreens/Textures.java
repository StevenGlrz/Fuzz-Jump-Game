package com.fuzzjump.libgdxscreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;

public class Textures {

    private final VectorGraphicsLoader vectorGraphicsLoader;

    private HashMap<String, VectorGraphicsLoader.VectorDetail> details;

    public Textures(VectorGraphicsLoader graphicsLoader) {
        this.vectorGraphicsLoader = graphicsLoader;
        details = new HashMap<>();
    }

    public void add(VectorGraphicsLoader.VectorDetail detail) {
        details.put(detail.getAtlasName(), detail);
    }

    public TextureRegion getTextureIgnoreMissing(String name) {
        VectorGraphicsLoader.VectorDetail detail = details.get(name);
        if (detail != null) {
            return vectorGraphicsLoader.load(details.get(name), true);
        }
        return null;
    }

    public TextureRegion getTexture(String name) {
        VectorGraphicsLoader.VectorDetail detail = details.get(name);
        if (detail != null) {
            return vectorGraphicsLoader.load(details.get(name), true);
        }
        throw new IllegalArgumentException("No svg definition for " + name);
    }

    public TextureRegion getTextureFromPath(String path) {
        return new TextureRegion(new Texture(Gdx.files.internal(path)));
    }

    public static TextureAtlas atlasFromFolder(String path) {
        TextureAtlas atlas = new TextureAtlas();
        FileHandle folder = Gdx.files.internal(path);
        for (FileHandle textureFile : folder.list()) {
            if (textureFile.name().endsWith(".png")) {
                atlas.addRegion(textureFile.nameWithoutExtension(), new TextureRegion(new Texture(textureFile)));
            }
        }
        return atlas;
    }

}
