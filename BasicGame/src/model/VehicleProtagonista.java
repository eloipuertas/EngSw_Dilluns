/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.util.CollisionShapeFactory;
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
    private Geometry wheel_fr;
    private Geometry wheel_br;
    private Geometry wheel_fl;
    private Geometry wheel_bl;
    private float wheelRadius;
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
        mass = 400;
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.setColor("Color", ColorRGBA.Red);


        //create a compound shape and attach the BoxCollisionShape for the car body at 0,1,0
        //this shifts the effective center of mass of the BoxCollisionShape to 0,-1,0
        //CompoundCollisionShape compoundShape = new CompoundCollisionShape();
        
        Node meshNode = (Node) assetManager.loadModel("Models/tempCar/Car.scene");
        
        chasis1 = findGeom(meshNode, "Car");
        chasis1.rotate(0, 3.135f, 0);
        
        CollisionShape carHull = CollisionShapeFactory.createDynamicMeshShape(chasis1);
        BoundingBox box = (BoundingBox) chasis1.getModelBound();
        //BoxCollisionShape box = new BoxCollisionShape(new Vector3f(1.2f, 0.5f, 2.4f));
        //compoundShape.addChildShape(box, new Vector3f(0, 1, 0));

        //create vehicle node
        vehicleNode = new Node("vehicleNode");
        vehicle = new VehicleControl(carHull, mass);
        vehicleNode.addControl(vehicle);
        
        
        //Geometry glass = findGeom(meshNode, "Cube2");
        
        //Spatial chasis = (Spatial)assetManager.loadModel("Models/Cube.mesh.xml");
        vehicleNode.attachChild(chasis1);
        //vehicleNode.attachChild(glass);

        
        //setting suspension values for wheels, this can be a bit tricky
        //see also https://docs.google.com/Doc?docid=0AXVUZ5xw6XpKZGNuZG56a3FfMzU0Z2NyZnF4Zmo&hl=en
        float stiffness = 120.0f;//200=f1 car
        float compValue = .2f; //(should be lower than damp)
        float dampValue = .3f;
        vehicle.setSuspensionCompression(compValue * 2.0f * FastMath.sqrt(stiffness));
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
        wheel_fr = findGeom(meshNode, "WheelFrontRight");
        node1.attachChild(wheel_fr);
        wheel_fr.center();
        box = (BoundingBox) wheel_fr.getModelBound();
        wheelRadius = box.getYExtent();
        float back_wheel_h = (wheelRadius * 1.7f) - 1f;
        float front_wheel_h = (wheelRadius * 1.9f) - 1f;
        vehicle.addWheel(wheel_fr.getParent(), box.getCenter().add(0, -front_wheel_h, 0),
                wheelDirection, wheelAxle, 0.2f, wheelRadius, false);

        Node node2 = new Node("wheel 2 node");
        wheel_fl = findGeom(meshNode, "WheelFrontLeft");
        node2.attachChild(wheel_fl);
        wheel_fl.center();
        box = (BoundingBox) wheel_fl.getModelBound();
        vehicle.addWheel(wheel_fl.getParent(), box.getCenter().add(0, -front_wheel_h, 0),
                wheelDirection, wheelAxle, 0.2f, wheelRadius, false);

        Node node3 = new Node("wheel 3 node");
        wheel_br = findGeom(meshNode, "WheelBackRight");
        node3.attachChild(wheel_br);
        wheel_br.center();
        box = (BoundingBox) wheel_br.getModelBound();
        vehicle.addWheel(wheel_br.getParent(), box.getCenter().add(0, -back_wheel_h, 0),
                wheelDirection, wheelAxle, 0.2f, wheelRadius, true);

        Node node4 = new Node("wheel 4 node");
        wheel_bl = findGeom(meshNode, "WheelBackLeft");
        node4.attachChild(wheel_bl);
        wheel_bl.center();
        box = (BoundingBox) wheel_bl.getModelBound();
        vehicle.addWheel(wheel_bl.getParent(), box.getCenter().add(0, -back_wheel_h, 0),
                wheelDirection, wheelAxle, 0.2f, wheelRadius, true);

        vehicleNode.attachChild(node1);
        vehicleNode.attachChild(node2);
        vehicleNode.attachChild(node3);
        vehicleNode.attachChild(node4);
        
        vehicle.getWheel(0).setFrictionSlip(4);
        vehicle.getWheel(1).setFrictionSlip(4);
        
        
        //rootNode.attachChild(vehicleNode);
        
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
