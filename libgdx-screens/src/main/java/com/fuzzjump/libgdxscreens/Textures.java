package com.fuzzjump.libgdxscreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;
import java.util.HashMap;

public class Textures {

	private final VectorGraphicsLoader vectorGraphicsLoader;

	private HashMap<String, VectorGraphicsLoader.VectorDetails> details;

	public Textures(VectorGraphicsLoader graphicsLoader) {
		this.vectorGraphicsLoader = graphicsLoader;
		details = new HashMap<>();
	}

	public void add(ArrayList<VectorGraphicsLoader.VectorDetails> vectorDetails) {
		for (VectorGraphicsLoader.VectorDetails info: vectorDetails) {
			details.put(info.getAtlasName(), info);
		}
	}
	
	public TextureRegion getTexture(String name) {
        if (!details.containsKey(name))
            throw new IllegalArgumentException("No svg definition for " + name);
		return vectorGraphicsLoader.load(details.get(name), true);
	}

	public TextureRegion getTextureFromPath(String path) {
		return new TextureRegion(new Texture(Gdx.files.internal(path)));
	}
}
