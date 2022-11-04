package com.setting.service.excel;

import com.axelor.meta.MetaFiles;
import com.axelor.meta.db.MetaFile;
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlRead {
  private Document doc;
  private NodeList nodeList;

  public void initialize(MetaFile input, String nodName)
      throws IOException, ParserConfigurationException, SAXException {
    // File xmlFile = new File(filePath);
    File xmlFile = MetaFiles.getPath(input).toFile();
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder;
    dBuilder = dbFactory.newDocumentBuilder();
    doc = dBuilder.parse(xmlFile);
    doc.getDocumentElement().normalize();
    nodeList = doc.getElementsByTagName(nodName);
  }

  public String[] read(int item, String[] nodeName) {
    Element element = (Element) nodeList.item(item);
    String[] vals = new String[nodeName.length];
    for (int i = 0; i < nodeName.length; i++) {
      vals[i] = "";
      if (getTagValue(nodeName[i], element, 0) != null)
        vals[i] = getTagValue(nodeName[i], element, 0);
    }
    return vals;
  }

  public String[] read(int item, String[] nodeName, int indexNode) {
    Element element = (Element) nodeList.item(item);
    String[] vals = new String[nodeName.length];
    for (int i = 0; i < nodeName.length; i++) {
      vals[i] = "";
      if (getTagValue(nodeName[i], element, indexNode) != null)
        vals[i] = getTagValue(nodeName[i], element, indexNode);
    }
    return vals;
  }

  public int getLengthNod() {
    return nodeList.getLength();
  }

  private static String getTagValue(String tag, Element element, int indexNode) {
    try {
      NodeList nodeList = element.getElementsByTagName(tag).item(indexNode).getChildNodes();
      Node node = (Node) nodeList.item(0);
      return node.getNodeValue();
    } catch (Exception e) {
      return "";
    }
  }
}
