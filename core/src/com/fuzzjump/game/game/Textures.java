package com.fuzzjump.game.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.fuzzjump.game.VectorGraphicsLoader;
import com.fuzzjump.game.model.character.UnlockableDefinition;

import java.util.ArrayList;
import java.util.HashMap;

public class Textures {

	private final VectorGraphicsLoader vectorGraphicsLoader;
	private final UnlockableColorizer colorizer;

	private HashMap<String, VectorGraphicsLoader.VectorDetails> infos;

	public Textures(VectorGraphicsLoader vectorGraphicsLoader, UnlockableColorizer colorizer) {
        this.vectorGraphicsLoader = vectorGraphicsLoader;
		this.colorizer = colorizer;
		infos = new HashMap<>();
        //add the logo svg info for the splash screen
        infos.put("logo", new VectorGraphicsLoader.VectorDetails("logo.svg", "logo", "screen_width:.8", "screen_width:.6"));
	}

	public void load() {
		XStream xstream = new XStream();
		xstream.alias("svginfo", VectorGraphicsLoader.VectorDetails.class);

		ArrayList<VectorGraphicsLoader.VectorDetails> infoList = (ArrayList<VectorGraphicsLoader.VectorDetails>) xstream.fromXML(Gdx.files.internal("data/svgs.xml").read());
		for (VectorGraphicsLoader.VectorDetails info: infoList) {
			infos.put(info.getAtlasName(), info);
		}
	}
	
	public TextureRegion getTexture(String name) {
        if (!infos.containsKey(name))
            throw new IllegalArgumentException("No svg definition for " + name);
        System.out.println(name);
		return vectorGraphicsLoader.load(infos.get(name), true);
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

    public static void addToAtlasFromFolder(TextureAtlas atlas, String path) {
        FileHandle folder = Gdx.files.internal(path);
        for (FileHandle textureFile : folder.list()) {
            if (textureFile.name().endsWith(".png")) {
                atlas.addRegion(textureFile.nameWithoutExtension(), new TextureRegion(new Texture(textureFile)));
            }
        }
    }

    public static Array<TextureRegion> findAnimationFrames(TextureAtlas atlas, String name, int start, int length) {
        Array<TextureRegion> frames = new Array<>();
        for(int i = start; i <= length; i++) {
            frames.add(atlas.findRegion(String.format("%s%04d", name, i)));
        }
        return frames;
    }

	public TextureRegion getColorized(UnlockableDefinition unlockableDefinition, int colorIndex) {
		return colorizer.colorize(unlockableDefinition, colorIndex);
	}

    public TextureRegion getTextureFromPath(String path) {
        return new TextureRegion(new Texture(Gdx.files.internal(path)));
    }
}
