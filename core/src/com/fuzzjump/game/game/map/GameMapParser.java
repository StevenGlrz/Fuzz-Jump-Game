package com.fuzzjump.game.game.screen.game.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 *
 * @author Steven Galarza
 */
public class GameMapParser {

	public GameMap parse(String name) {
		GameMap map = new GameMap();
		try {

			DocumentBuilder bldr = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = bldr.parse(Gdx.files.internal("data/maps/" + name + ".xml").read());

			Element mapEl = document.getDocumentElement();

			NodeList mapBackgroundLayers = mapEl.getElementsByTagName("background").item(0).getChildNodes();
			for (int i = 0; i < mapBackgroundLayers.getLength(); i++) {
				Node layer = mapBackgroundLayers.item(i);
				if (layer instanceof Element) {
					Element layerEl = (Element) layer;

					float parallaxX = 1, parallaxY = 1,
							layerWidth = -1, layerHeight = -1,
							x = 0, y = 0, yOffset = 0, layerOffset = 0;

					if (layerEl.hasAttribute("x"))
						x = Float.parseFloat(layerEl.getAttribute("x"));
					if (layerEl.hasAttribute("y"))
						y = Float.parseFloat(layerEl.getAttribute("y"));
					if (layerEl.hasAttribute("parallaxX"))
						parallaxX = Float.parseFloat(layerEl.getAttribute("parallaxX"));
					if (layerEl.hasAttribute("parallaxY"))
						parallaxY = Float.parseFloat(layerEl.getAttribute("parallaxY"));
					if (layerEl.hasAttribute("width"))
						layerWidth = Float.parseFloat(layerEl.getAttribute("width"));
					if (layerEl.hasAttribute("height"))
						layerHeight = Float.parseFloat(layerEl.getAttribute("height"));
					if (layerEl.hasAttribute("yOffset"))
						yOffset = Float.parseFloat(layerEl.getAttribute("yOffset"));
					if (layerEl.hasAttribute("layerOffset"))
						layerOffset = Float.parseFloat(layerEl.getAttribute("layerOffset"));
					map.addBackgroundLayer(new GameMapBackground(layer.getTextContent(), new Vector2(x, y), new Vector2(layerWidth, layerHeight), new Vector2(parallaxX, parallaxY), yOffset, layerOffset));
				}
			}

			NodeList platforms = mapEl.getElementsByTagName("platforms");
			for (int i = 0; i < platforms.getLength(); i++) {
				Element platformsElement = (Element) platforms.item(i);

				int physicsY = 0;
				int physicsHeight = 0;
				if (platformsElement.hasAttribute("physicsHeight"))
					physicsHeight = Integer.parseInt(platformsElement.getAttribute("physicsHeight"));
				if (platformsElement.hasAttribute("physicsY"))
					physicsY = Integer.parseInt(platformsElement.getAttribute("physicsY"));

				NodeList segmentPlatforms = platformsElement.getElementsByTagName("platform");
				for (int j = 0; j < segmentPlatforms.getLength(); j++) {
					Element segmentPlatform = (Element) segmentPlatforms.item(j);

					String platformName = segmentPlatform.getAttribute("name");
					map.addPlatform(new GameMapPlatform(platformName, physicsY, physicsHeight));
				}

			}

			Element mapElGround = (Element) mapEl.getElementsByTagName("ground").item(0);
			GameMapGround ground = new GameMapGround(mapElGround.getAttribute("name"), Float.parseFloat(mapElGround.getAttribute("y")), Float.parseFloat(mapElGround.getAttribute("height")), Float.parseFloat(mapElGround.getAttribute("real-height")));
			
			map.setGround(ground);
			map.setWidth(Integer.parseInt(mapEl.getAttribute("width")));
			map.setHeight(Integer.parseInt(mapEl.getAttribute("height")));
			map.setClouds(mapEl.getAttribute("clouds").equalsIgnoreCase("on"));
            map.setSnowing(mapEl.getAttribute("snow").equalsIgnoreCase("on"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

}
