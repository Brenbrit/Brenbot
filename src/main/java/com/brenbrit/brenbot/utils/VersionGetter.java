package com.brenbrit.brenbot.utils;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class VersionGetter {
    public static String getVersion(String fileLoc) {
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document xmlDocument = builder.parse(fileLoc);
            XPath xPath = XPathFactory.newInstance().newXPath();

            String expression = "./project/version";
            NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
            return nodeList.item(0).getTextContent();

        } catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException e) {
            System.out.println("Failed to get version.");
            e.printStackTrace();
            return null;
        }
    }
}
