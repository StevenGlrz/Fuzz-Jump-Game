package com.fuzzjump.libgdxscreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;
import java.util.List;

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

	public void add(List<VectorGraphicsLoader.VectorDetail> vectorDetails) {
		for (VectorGraphicsLoader.VectorDetail info: vectorDetails) {
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
