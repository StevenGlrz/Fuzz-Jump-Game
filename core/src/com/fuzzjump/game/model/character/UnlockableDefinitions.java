package com.fuzzjump.game.model.character;

import android.util.SparseArray;

import com.badlogic.gdx.Gdx;
import com.steveadoo.customizetextures.CColorGroup;

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

/**
 * Kerpow Games, LLC
 * Created by stephen on 6/7/2015.
 */
public class UnlockableDefinitions {

    private static final String DEFINITIONS_PATH = "data/unlockable-definitions.xml";
    public static final int FUZZLE_COUNT = 6;

    private SparseArray<UnlockableDefinition> definitions = new SparseArray();
    private SparseArray<UnlockableDefinition>[] definitionsCategories = new SparseArray[5];

    private CColorGroup replaceGroup;

    public void init() {
        for (int i = 0; i < definitionsCategories.length; i++) {
            definitionsCategories[i] = new SparseArray();
        }
        try {

            DocumentBuilder bldr = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = bldr.parse(Gdx.files.internal(DEFINITIONS_PATH).read());

            Element rootElement = (Element)document.getChildNodes().item(0);
            Element baseElement = (Element) rootElement.getElementsByTagName("base").item(0);
            Element colorsElement = (Element) baseElement.getElementsByTagName("colors").item(0);

            replaceGroup = readColorBlock((Element) colorsElement.getElementsByTagName("color").item(0));

            NodeList entries = ((Element)rootElement.getElementsByTagName("entries").item(0)).getElementsByTagName("entry");
            for (int i = 0; i < entries.getLength(); i++) {
                Element entry = (Element) entries.item(i);

                int id = Integer.parseInt(entry.getAttribute("id"));
                int category = Integer.parseInt(entry.getAttribute("category"));
                int cost = Integer.parseInt(entry.getAttribute("cost"));
                String name = entry.getAttribute("name");
                String allowedTag = entry.getAttribute("allowedtag");

                String[] allowedTags = new String[1];
                if (!allowedTag.contains(","))
                    allowedTags[0] = allowedTag;
                else
                    allowedTags = allowedTag.split(",");
                UnlockableDefinition def = new UnlockableDefinition(id, category, name, cost, allowedTags, replaceGroup);
                definitionsCategories[category].put(id, def);
                definitions.put(id, def);

                NodeList colorsNode = entry.getElementsByTagName("colors");
                if (colorsNode != null && colorsNode.getLength() > 0) {
                    NodeList colors = ((Element) colorsNode.item(0)).getElementsByTagName("color");
                    CColorGroup[] colorGroup = new CColorGroup[colors.getLength()];
                    for (int nodeIdx = 0; nodeIdx < colorGroup.length; nodeIdx++) {
                        colorGroup[nodeIdx] = readColorBlock((Element) colors.item(nodeIdx));
                    }
                    def.setColorGroups(colorGroup);
                }
                NodeList boundsNodes = entry.getElementsByTagName("bounds");
                if (boundsNodes != null && boundsNodes.getLength() > 0) {
                    NodeList bounds = ((Element) boundsNodes.item(0)).getElementsByTagName("bound");
                    UnlockableBound[] uBounds = new UnlockableBound[UnlockableDefinitions.FUZZLE_COUNT];
                    for(int nodeIdx = 0; nodeIdx < bounds.getLength(); nodeIdx++) {
                        Element element = (Element)bounds.item(nodeIdx);
                        uBounds[Integer.parseInt(element.getAttribute("fuzzle"))] =
                                new UnlockableBound(Double.parseDouble(getNodeValue(element, "x")),
                                                    1.0 - Double.parseDouble(getNodeValue(element, "y")),
                                                    getNodeValue(element, "w"),
                                                    getNodeValue(element, "h"));
                    }
                    def.setBounds(uBounds);
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getNodeValue(Element element, String childTag) {
        return element.getElementsByTagName(childTag).item(0).getTextContent();
    }

    private CColorGroup readColorBlock(Element node) {
        NodeList cElements = node.getElementsByTagName("c");
        CColorGroup group = new CColorGroup();
        group.colors = new CColorGroup.IndexColor[cElements.getLength()];
        for (int i = 0; i < cElements.getLength(); i++)
            group.colors[i] = readIndexColor((Element) cElements.item(i));
        return group;
    }

    private CColorGroup.IndexColor readIndexColor(Element item) {
        CColorGroup.IndexColor color = new CColorGroup.IndexColor();
        color.index = Integer.parseInt(item.getAttribute("id"));
        color.colorString = item.getTextContent();
        if (!color.colorString.startsWith("#")) {
            color.colorString = "#" + color.colorString;
        }
        return color;
    }

    public List<UnlockableDefinition> getDefinitions(int category, int fuzz) {
        List<UnlockableDefinition> defs = new ArrayList<>();
        UnlockableDefinition fuzzDef = definitionsCategories[0].get(fuzz);
        System.out.println(fuzzDef);
        for(int i = 0; i < definitionsCategories[category].size(); i++) {
            UnlockableDefinition check = definitionsCategories[category].valueAt(i);
            if (check.validFuzzle(fuzzDef.getAllowedTags()))
                defs.add(check);
        }
        return defs;
    }

    public UnlockableDefinition getDefinition(int definitionId) {
        return definitions.get(definitionId);
    }

    public SparseArray<UnlockableDefinition> getDefinitions() {
        return definitions;
    }
}
