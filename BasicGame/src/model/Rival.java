/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class Rival {
    //Atributs de la classe, s'ha d'anar ampliant
    
    private float mass;
    private VehicleControl vehicle;
    private Geometry chasis1;
    private Geometry wheel1;
    private Geometry wheel3;
    private Geometry wheel2;
    private Geometry wheel4;
    private float wheelRadius;
    private AssetManager assetManager;
    private CameraNode camNode;
    public Node vehicleNode;
    private PhysicsSpace physicsSpace;
    public float acceleracio;   //Variable de control de la velocitat màxima
    public float velocitat;   //Variable de control de la velocitat mínima
    public float gir;
    
    //Constructor
    public Rival(AssetManager asset, PhysicsSpace phy){
        assetManager = asset;
        physicsSpace = phy;
        velocitat = 0;
        gir = 0;
        
    }
    
    private Geometry findGeom(Spatial spatial, String name) {
        if (spatial instanceof Node) {
            Node node = (Node) spatial;
            for (int i = 0; i < node.getQuantity(); i++) {
                Spatial child = node.getChild(i);
                Geometry result = findGeom(child, name);
                if (result != null) {
                    return result;
                }
            }
        } else if (spatial instanceof Geometry) {
            if (spatial.getName().startsWith(name)) {
                return (Geometry) spatial;
            }
        }
        return null;
    }
    
    public void buildCar() {
        mass = 400;
         Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
         mat.getAdditionalRenderState().setWireframe(true);
         mat.setColor("Color", ColorRGBA.Blue);
         /*
         Material matW = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
         matW.getAdditionalRenderState().setWireframe(true);
         matW.setColor("Color", ColorRGBA.Black);
*/
        Node meshNode = (Node) assetManager.loadModel("Models/tempCar/Car.scene");
        
        chasis1 = findGeom(meshNode,"Car");
        chasis1.rotate(0, 3.135f, 0);
        chasis1.setMaterial(mat);
        
        CollisionShape carHull = CollisionShapeFactory.createDynamicMeshShape(chasis1);
        BoundingBox box = (BoundingBox) chasis1.getModelBound();


        //create vehicle node
        vehicleNode = new Node("vehicleNode");
        vehicle = new VehicleControl(carHull, mass);
        vehicleNode.addControl(vehicle);
        //vehicleNode.setMaterial(mat);
        
        vehicleNode.attachChild(chasis1);
        
        //setting suspension values for wheels, this can be a bit tricky
        //see also https://docs.google.com/Doc?docid=0AXVUZ5xw6XpKZGNuZG56a3FfMzU0Z2NyZnF4Zmo&hl=en
        float stiffness = 60.0f;//200=f1 car
        float compValue = .3f; //(should be lower than damp)
        float dampValue = .4f;
        vehicle.setSuspensionCompression(compValue * 10.0f * FastMath.sqrt(stiffness));
        vehicle.setSuspensionDamping(dampValue * 2.0f * FastMath.sqrt(stiffness));
        vehicle.setSuspensionStiffness(stiffness);
        vehicle.setMaxSuspensionForce(10000.0f);
        
        //Create four wheels and add them at their locations
        Vector3f wheelDirection = new Vector3f(0, -1, 0); // was 0, -1, 0
        Vector3f wheelAxle = new Vector3f(-1, 0, 0); // was -1, 0, 0
        float radius = 0.5f;
        float restLength = 0.3f;
        float yOff = 0.5f;
        float xOff = 1f;
        float zOff = 2f;

        Node node1 = new Node("wheel 1 node");
        wheel1 = findGeom(meshNode, "WheelFrontRight");
        wheel1.setMaterial(mat);
        node1.attachChild(wheel1);
        wheel1.center();
        vehicle.addWheel(node1, new Vector3f(-xOff, yOff, zOff),
                wheelDirection, wheelAxle, restLength, radius, true);

        Node node2 = new Node("wheel 2 node");
        wheel2 = findGeom(meshNode, "WheelFrontLeft");
        wheel2.setMaterial(mat);
        node2.attachChild(wheel2);
        wheel2.center();
        vehicle.addWheel(node2, new Vector3f(xOff, yOff, zOff),
                wheelDirection, wheelAxle, restLength, radius, true);

        Node node3 = new Node("wheel 3 node");
        wheel3 = findGeom(meshNode, "WheelBackRight");
        wheel3.setMaterial(mat);
        node3.attachChild(wheel3);
        wheel3.center();
        vehicle.addWheel(node3, new Vector3f(-xOff, yOff, -zOff),
                wheelDirection, wheelAxle, restLength, radius, false);

        Node node4 = new Node("wheel 4 node");
        wheel4 = findGeom(meshNode, "WheelBackLeft");
        wheel4.setMaterial(mat);
        node4.attachChild(wheel4);
        wheel4.center();
        vehicle.addWheel(node4, new Vector3f(xOff, yOff, -zOff),
                wheelDirection, wheelAxle, restLength, radius, false);

        vehicleNode.attachChild(node1);
        vehicleNode.attachChild(node2);
        vehicleNode.attachChild(node3);
        vehicleNode.attachChild(node4);
        
        vehicle.getWheel(0).setFrictionSlip(9.8f);
        vehicle.getWheel(1).setFrictionSlip(9.8f);
        
        //rootNode.attachChild(vehicleNode);
        
        physicsSpace.add(vehicle);
    }
    
    //Getters i setters :
    //#################################################################
    
    //Getter del cotxe en si, s'utilitza per a obtenir-lo desde el main
    public Spatial getSpatial(){
        return (Spatial)vehicleNode;
    }
    public VehicleControl getVehicle() {
        return vehicle;
    }
    
    public float getAcceleracio(){
        return acceleracio;
    }
    
    public float getVelocitat(){
        return velocitat;
    }
    //#################################################################
    //Metodes per a moure el cotxe de forma aleatoria (probablement no vagin aqui)
    //---------------------------------------------------
    
    //Mètodes de moviment bàsic, endavant, endarrere, esquerra i dreta :
    
    public void moureEndavant(){
        vehicle.brake(0f);
        if (vehicle.getLinearVelocity().length()<15) {
            vehicle.accelerate(800.0f);
        } else {
            vehicle.accelerate(0);
        }   
    }
    public void moureEndarrere(){
        velocitat = -10;
    }
    public void moureEsquerra(){
        gir = .5f;
    }
    public void girarCurva1(){
        //System.out.println(vehicle.getLinearVelocity().length());
        if (vehicle.getLinearVelocity().length()<70 && vehicle.getLinearVelocity().length()>10) {
            System.out.println("frenant");
            vehicle.brake(100f);
        } else if (vehicle.getLinearVelocity().length()>=3 && vehicle.getLinearVelocity().length()<=10){
            System.out.println("girant");
            vehicle.steer(-.5f);
            vehicle.brake(0f);
        }else{
            System.out.println("accelerar");
            vehicle.accelerate(100f);
            vehicle.brake(0f);
        }
    
    }
    
    //Mètode que s'alimenta dels mètodes de moviment bàsic per a moure el cotxe aleatoriament
    public void frenar(){
        velocitat = velocitat - acceleracio/2;
    }
}
