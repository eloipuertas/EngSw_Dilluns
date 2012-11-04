/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.CameraControl;

/**
 *
 * @author Sergi
 */
public class VehicleProtagonista {
    
    private float mass;
    private VehicleControl vehicle;
    private Geometry chasis1;
    private Geometry wheels1;
    private Geometry wheels2;
    private Geometry wheels3;
    private Geometry wheels4;
    private AssetManager assetManager;
    private CameraNode camNode;
    private Node vehicleNode;
    private PhysicsSpace physicsSpace;
    private Camera cam;
    
    public VehicleProtagonista(AssetManager asset, PhysicsSpace phy, Camera cam){
        assetManager = asset;
        physicsSpace = phy;
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
        mass = 900;
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.setColor("Color", ColorRGBA.Red);


        //create a compound shape and attach the BoxCollisionShape for the car body at 0,1,0
        //this shifts the effective center of mass of the BoxCollisionShape to 0,-1,0
        CompoundCollisionShape compoundShape = new CompoundCollisionShape();
        BoxCollisionShape box = new BoxCollisionShape(new Vector3f(1.2f, 0.5f, 2.4f));
        compoundShape.addChildShape(box, new Vector3f(0, 1, 0));

        //create vehicle node
        vehicleNode = new Node("vehicleNode");
        vehicle = new VehicleControl(compoundShape, mass);
        vehicleNode.addControl(vehicle);
        
        Node meshNode = (Node) assetManager.loadModel("Models/tempCar/Car.scene");
        
        chasis1 = findGeom(meshNode, "Car");
        chasis1.rotate(0, 3.135f, 0);
        //Geometry glass = findGeom(meshNode, "Cube2");
        
        //Spatial chasis = (Spatial)assetManager.loadModel("Models/Cube.mesh.xml");
        vehicleNode.attachChild(chasis1);
        //vehicleNode.attachChild(glass);
        
        //chasis1.scale(1, 1, 1.8f);
        //glass.scale(1, 1, 1.8f);
        //chasis.setMaterial(chasisMat);
        //chasis.setLocalTranslation(0, 0.5f, 0);
        
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
        wheels1 = findGeom(meshNode, "WheelFrontRight");
        node1.attachChild(wheels1);
        wheels1.center();
        vehicle.addWheel(node1, new Vector3f(-xOff, yOff, zOff),
                wheelDirection, wheelAxle, restLength, radius, true);

        Node node2 = new Node("wheel 2 node");
        wheels2 = findGeom(meshNode, "WheelFrontLeft");
        node2.attachChild(wheels2);
        wheels2.center();
        vehicle.addWheel(node2, new Vector3f(xOff, yOff, zOff),
                wheelDirection, wheelAxle, restLength, radius, true);

        Node node3 = new Node("wheel 3 node");
        wheels3 = findGeom(meshNode, "WheelBackRight");
        node3.attachChild(wheels3);
        wheels3.center();
        vehicle.addWheel(node3, new Vector3f(-xOff, yOff, -zOff),
                wheelDirection, wheelAxle, restLength, radius, false);

        Node node4 = new Node("wheel 4 node");
        wheels4 = findGeom(meshNode, "WheelBackLeft");
        node4.attachChild(wheels4);
        wheels4.center();
        vehicle.addWheel(node4, new Vector3f(xOff, yOff, -zOff),
                wheelDirection, wheelAxle, restLength, radius, false);

        vehicleNode.attachChild(node1);
        vehicleNode.attachChild(node2);
        vehicleNode.attachChild(node3);
        vehicleNode.attachChild(node4);
        
        
        //rootNode.attachChild(vehicleNode);
        
        vehicle.getWheel(2).setFrictionSlip(10);
        vehicle.getWheel(3).setFrictionSlip(10);
        
        physicsSpace.add(vehicle);
        
        //set forward camera node that follows the character
        //camNode = new CameraNode("CamNode", cam);
        //camNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
        //camNode.setLocalTranslation(new Vector3f(0, 4, -15));
        //camNode.setLocalTranslation(new Vector3f(-15, 15, -15));
        //camNode.setLocalTranslation(new Vector3f(-15, 15, -15));
        //camNode.lookAt(vehicleNode.getLocalTranslation(), Vector3f.UNIT_Y);
        //vehicleNode.attachChild(camNode);
       
         
       
    }

    public VehicleControl getVehicle() {
        return vehicle;
    }

    public Spatial getSpatial() {
        return (Spatial)vehicleNode;
    }

    
}
