/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author davidsanchezpinsach
 */
public class CarSettings {
    
    private org.w3c.dom.Document doc = null;
    
    public CarSettings(){
        
    }
    
    public void readXml(){
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(new File("src/model/carSettings.xml"));
            doc.getDocumentElement().normalize();
        } catch (ParserConfigurationException e) {
           e.printStackTrace();
        } catch (SAXException e) {
           e.printStackTrace();
        } catch (IOException e) {
           e.printStackTrace();
        }
    }
    
    public void readAtributes(){
        NodeList nList = doc.getElementsByTagName("car");
        Node nNode = nList.item(1); //idModel
        if (nNode.getNodeType() == Node.ELEMENT_NODE) {
           Element eElement = (Element) nNode;
           System.out.println("maxAccelerateVelocity: " + getTagValue("maxAccelerateVelocity",eElement));
        }
     }
    private static String getTagValue(String sTag, Element eElement) {
	NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
        Node nValue = (Node) nlList.item(0);
	return nValue.getNodeValue();
  }
}
