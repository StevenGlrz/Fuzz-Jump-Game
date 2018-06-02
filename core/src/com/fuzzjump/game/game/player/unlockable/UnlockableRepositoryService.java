package com.fuzzjump.game.game.player.unlockable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.IntMap;
import com.fuzzjump.game.util.Helper;
import com.fuzzjump.libgdxscreens.graphics.ColorGroup;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class UnlockableRepositoryService {

    private static final String DEFINITIONS_PATH = "data/unlockable-definitions.xml";
    public static final int FUZZLE_COUNT = 6;

    private IntMap<UnlockableDefinition> definitions = new IntMap<>();

    public void init() {
        try {

            DocumentBuilder bldr = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = bldr.parse(Gdx.files.internal(DEFINITIONS_PATH).read());

            Element rootElement = (Element)document.getChildNodes().item(0);
            Element baseElement = (Element) rootElement.getElementsByTagName("base").item(0);
            Element colorsElement = (Element) baseElement.getElementsByTagName("colors").item(0);

            ColorGroup replaceGroup = readColorBlock((Element) colorsElement.getElementsByTagName("color").item(0));

            NodeList entries = ((Element)rootElement.getElementsByTagName("entries").item(0)).getElementsByTagName("entry");
            for (int i = 0; i < entries.getLength(); i++) {
                Element entry = (Element) entries.item(i);

                int id = Integer.parseInt(entry.getAttribute("id"));
                int category = Integer.parseInt(entry.getAttribute("category"));
                int cost = Integer.parseInt(entry.getAttribute("cost"));
                String name = entry.getAttribute("name");
                String allowedTag = entry.getAttribute("allowedtag");

                String[] allowedTags = new String[1];
                if (!allowedTag.contains(",")) {
                    allowedTags[0] = allowedTag;
                } else {
                    allowedTags = allowedTag.split(",");
                }
                UnlockableDefinition def = new UnlockableDefinition(id, category, name, cost, allowedTags, replaceGroup);
                definitions.put(id, def);

                NodeList colorsNode = entry.getElementsByTagName("colors");
                if (colorsNode != null && colorsNode.getLength() > 0) {
                    NodeList colors = ((Element) colorsNode.item(0)).getElementsByTagName("color");
                    ColorGroup[] colorGroup = new ColorGroup[colors.getLength()];
                    for (int nodeIdx = 0; nodeIdx < colorGroup.length; nodeIdx++) {
                        colorGroup[nodeIdx] = readColorBlock((Element) colors.item(nodeIdx));
                    }
                    def.setColorGroups(colorGroup);
                }
                NodeList boundsNodes = entry.getElementsByTagName("bounds");
                if (boundsNodes != null && boundsNodes.getLength() > 0) {
                    NodeList bounds = ((Element) boundsNodes.item(0)).getElementsByTagName("bound");
                    Rectangle[] uBounds = new Rectangle[UnlockableRepositoryService.FUZZLE_COUNT];
                    for(int nodeIdx = 0; nodeIdx < bounds.getLength(); nodeIdx++) {
                        Element element = (Element) bounds.item(nodeIdx);
                        uBounds[Integer.parseInt(element.getAttribute("fuzzle"))] = createBound(element);
                    }
                    def.setBounds(uBounds);
                }
            }
            //System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(definitions.values()));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    private Rectangle createBound(Element element) {
        float x = Float.parseFloat(Helper.getNodeValue(element, "x"));
        float y = 1.0f - Float.parseFloat(Helper.getNodeValue(element, "y"));
        float w;
        float h;

        String width = Helper.getNodeValue(element, "w");
        String height = Helper.getNodeValue(element, "h");
        try {
            w = Float.parseFloat(width);
        } catch(Exception e) {
            w = width.equals("asp") ? -1 : 0;
        }
        try {
            h = Float.parseFloat(height);
        } catch(Exception e) {
            h = height.equals("asp") ? -1 : 0;
        }
        return new Rectangle(x, y, w, h);
    }

    private ColorGroup readColorBlock(Element node) {
        NodeList cElements = node.getElementsByTagName("c");
        ColorGroup group = new ColorGroup();
        group.colors = new ColorGroup.IndexColor[cElements.getLength()];
        for (int i = 0; i < cElements.getLength(); i++) {
            group.colors[i] = readIndexColor((Element) cElements.item(i));
        }
        return group;
    }

    private ColorGroup.IndexColor readIndexColor(Element item) {
        ColorGroup.IndexColor color = new ColorGroup.IndexColor();
        color.index = Integer.parseInt(item.getAttribute("id"));
        color.colorString = item.getTextContent();
        if (!color.colorString.startsWith("#")) {
            color.colorString = "#" + color.colorString;
        }
        return color;
    }

    public List<UnlockableDefinition> getDefinitions(int category, int definitionId) {
        List<UnlockableDefinition> defs = new ArrayList<>();
        UnlockableDefinition fuzzDef = definitions.get(definitionId);
        for (UnlockableDefinition check : definitions.values()) {
            if (check.getCategory() == category && check.validFuzzle(fuzzDef.getAllowedTags())) {
                defs.add(check);
            }
        }
        return defs;
    }

    public UnlockableDefinition getDefinition(int id) {
        return definitions.get(id);
    }
}
