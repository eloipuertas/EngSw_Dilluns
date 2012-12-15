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
import org.w3c.dom.Document;
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
    
    //Propiedades del coche
    private float mass;
    private float steeringValue = 0;
    private float accelerationValue = 0;
    private float accelerationForce = 0f;
    private float brakeForce = 0f;
    //Factores para disminuir y aumentar la acceleracion y la frenadas
    private int accelerationFactor = 0; //Factor multiplicativo
    private int brakeForceFactor = 1;   //Factor de division
    private double reverseFactor = 1.5;    //Factor de multiplicacio
    
    private int maxAccelerateVelocity = 120;
    private int maxReverseVelocity = -50;
    
    private float stiffness = 200.0f;//200=f1 car
    private float compValue = .2f; //(should be lower than damp)
    private float dampValue = .3f;
    private float maxSuspensionForce = 10000.0f;
    
    private String patch;
    
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
    
    public void loadAtributes(int idModel){
        NodeList nList = doc.getElementsByTagName("car");
        Node nNode = nList.item(idModel-1); 
        if (nNode.getNodeType() == Node.ELEMENT_NODE) {
           Element eElement = (Element) nNode;
           maxAccelerateVelocity = Integer.parseInt(getTagValue("maxAccelerateVelocity",eElement));
           maxReverseVelocity = Integer.parseInt(getTagValue("maxReverseVelocity",eElement));
           reverseFactor = Double.parseDouble(getTagValue("reverseFactor",eElement));
           accelerationFactor = Integer.parseInt(getTagValue("accelerationFactor",eElement));
           steeringValue = Float.parseFloat(getTagValue("steeringValue",eElement));
           accelerationValue = Float.parseFloat(getTagValue("accelerationValue",eElement));
           accelerationForce = Float.parseFloat(getTagValue("accelerationForce",eElement));
           mass = Float.parseFloat(getTagValue("mass",eElement));
           brakeForce = Float.parseFloat(getTagValue("brakeForce",eElement));
           stiffness = Float.parseFloat(getTagValue("stiffness",eElement));
           compValue = Float.parseFloat(getTagValue("compValue",eElement));
           dampValue = Float.parseFloat(getTagValue("dampValue",eElement));
           maxSuspensionForce = Float.parseFloat(getTagValue("maxSuspensionForce", eElement));
           patch = getTagValue("patch",eElement);
        }
    }
        
        
    private static String getTagValue(String sTag, Element eElement) {
	NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
        Node nValue = (Node) nlList.item(0);
	return nValue.getNodeValue();
    }
    
    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    public float getSteeringValue() {
        return steeringValue;
    }

    public void setSteeringValue(float steeringValue) {
        this.steeringValue = steeringValue;
    }

    public float getAccelerationValue() {
        return accelerationValue;
    }

    public void setAccelerationValue(float accelerationValue) {
        this.accelerationValue = accelerationValue;
    }

    public float getAccelerationForce() {
        return accelerationForce;
    }

    public void setAccelerationForce(float accelerationForce) {
        this.accelerationForce = accelerationForce;
    }

    public float getBrakeForce() {
        return brakeForce;
    }

    public void setBrakeForce(float brakeForce) {
        this.brakeForce = brakeForce;
    }

    public int getAccelerationFactor() {
        return accelerationFactor;
    }

    public void setAccelerationFactor(int accelerationFactor) {
        this.accelerationFactor = accelerationFactor;
    }

    public int getBrakeForceFactor() {
        return brakeForceFactor;
    }

    public void setBrakeForceFactor(int brakeForceFactor) {
        this.brakeForceFactor = brakeForceFactor;
    }

    public double getReverseFactor() {
        return reverseFactor;
    }

    public void setReverseFactor(double reverseFactor) {
        this.reverseFactor = reverseFactor;
    }

    public int getMaxAccelerateVelocity() {
        return maxAccelerateVelocity;
    }

    public void setMaxAccelerateVelocity(int maxAccelerateVelocity) {
        this.maxAccelerateVelocity = maxAccelerateVelocity;
    }

    public int getMaxReverseVelocity() {
        return maxReverseVelocity;
    }

    public void setMaxReverseVelocity(int maxReverseVelocity) {
        this.maxReverseVelocity = maxReverseVelocity;
    }

    public float getStiffness() {
        return stiffness;
    }

    public void setStiffness(float stiffness) {
        this.stiffness = stiffness;
    }

    public float getCompValue() {
        return compValue;
    }

    public void setCompValue(float compValue) {
        this.compValue = compValue;
    }

    public float getDampValue() {
        return dampValue;
    }

    public void setDampValue(float dampValue) {
        this.dampValue = dampValue;
    }

    public float getMaxSuspensionForce() {
        return maxSuspensionForce;
    }

    public void setMaxSuspensionForce(float maxSuspensionForce) {
        this.maxSuspensionForce = maxSuspensionForce;
    }
    
    public String getPatch() {
        return patch;
    }

    public void setPatch(String patch) {
        this.patch = patch;
    }
}
