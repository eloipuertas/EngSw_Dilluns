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
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.InputListener;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.CameraControl;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

/**
*
* @author Sergi
*/
public class VehicleProtagonista{

    private VehicleControl vehicle;
    private MaterialsVehicle materials;
    private Geometry chasis1;
    private Geometry chasis12;
    private Geometry wheel1;
    private Geometry wheel3;
    private Geometry wheel2;
    private Geometry wheel4;
    private Geometry wheel12;
    private Geometry wheel32;
    private Geometry wheel22;
    private Geometry wheel42;
    private float wheelRadius;
    private AssetManager assetManager;
    private CameraNode camNode;
    private Node vehicleNode;
    private PhysicsSpace physicsSpace;
    private Camera cam;
    
    //Numero de vueltas del vehiculo protagonista
    private int numVueltas = 0;

    private boolean reverseMode = false;
    private boolean handBrakeMode  = false;
    private boolean forwardMode = false;
    
    private Audio accelerate_sound;
    private Audio decelerate_sound;
    private Audio max_velocity_sound;
    private Audio brake_sound;
    private Audio idling_car_sound;
    
    private ArrayList<Vector3f> llistaControlVolta;
    private String puntsVoltaData;
    private int numVoltes;
    private int numPuntsControlVolta;
    private int estat;
    private int estatControlVolta;
    private int posicioCarrera;
    private Vector3f puntControlVolta;
    
    
    //Objeto que encapsula la configuracion del coche
    private CarSettings carSettings;
    
    //Initial position and initial rotation of the car
    public Vector3f initialPos = new Vector3f(0f,0f,0f);
    public Quaternion initialRot = new Quaternion();
            float accelerationValue = 0;
        float accelerationForce = 1000;
        float accelerationFactor = 2;

    public VehicleProtagonista(AssetManager asset, PhysicsSpace phy, Camera cam, int idCircuit) {
        llistaControlVolta = new ArrayList<Vector3f>();
        assetManager = asset;
        physicsSpace = phy;
        numVoltes=0;
        posicioCarrera=1;
        puntsVoltaData="src/model//puntsControlVolta.txt";
        llegirPuntsControlVolta(puntsVoltaData);
        canviaEstatControlVolta(1);
    }  

    private void llegirPuntsControlVolta(String puntsVolta) {
        try {
            float[] punt;
            punt = new float[3];
            FileReader fr = new FileReader(puntsVolta);

            Scanner scanner = new Scanner(fr);
            BufferedReader bf = new BufferedReader(fr);
            while (scanner.hasNextLine()) {
                Scanner linia = new Scanner(scanner.nextLine());
                linia.useDelimiter(" ");

                if(linia.hasNext()){
                    for(int i=0;i<=2;i++){
                        punt[i]=Float.valueOf(linia.next());
                    }
                    Vector3f p =new Vector3f(punt[0],punt[1],punt[2]);
                    llistaControlVolta.add(p); 
                } 
            } 
        } catch (FileNotFoundException fnfe){
                fnfe.printStackTrace();
        } catch (IOException ioe){
                ioe.printStackTrace();      
        }
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
    
    public void setCocheProtagonista(int idModel, String idColor){
        materials = new MaterialsVehicle(assetManager, idColor);
        materials.initMaterials();
        carSettings = new CarSettings();
        carSettings.readXml();
        carSettings.loadAtributes(idModel);
        if(idModel==1){
            buildFerrari();
        }else if(idModel == 2){
            buildGolf();
        }
    }
    
    private Vector3f buscaPuntControlVolta (int numPunt) {
        Iterator it = llistaControlVolta.iterator();
        int i=0;
        Vector3f p=new Vector3f();
        if(numPunt == llistaControlVolta.size()+1){
            numPunt = 1;
        }
        while(it.hasNext() && i!=numPunt){
            p=(Vector3f)it.next();
            i++;
        }
        return p;
    }

    public void canviaEstatControlVolta(int estatFutur) {
        estatControlVolta = estatFutur;
        if(estatControlVolta==llistaControlVolta.size()+1){
            estatControlVolta =1;
            numVoltes++;
        }
        puntControlVolta = buscaPuntControlVolta(estatControlVolta);
        //System.out.println("estat control volta"+estatControlVolta +"          numvoltes      "+numVoltes);
    }
    
    private void buildGolf(){
        //create a compound shape and attach the BoxCollisionShape for the car body at 0,1,0
        //this shifts the effective center of mass of the BoxCollisionShape to 0,-1,0
        //CompoundCollisionShape compoundShape = new CompoundCollisionShape();

//        Node meshNode1 = (Node) assetManager.loadModel("Models/golfCar/Car.scene");
//        Node meshNode1 = (Node) assetManager.loadModel("Models/golfDeformed/Car.scene");
        Node meshNode1 = (Node) assetManager.loadModel("Models/meshesFerrari/Car.scene");
//        Node meshNode1 = (Node) assetManager.loadModel("Models/meshesFerrariDeformed/Car.scene");
        
        Node meshNode = (Node) assetManager.loadModel("Models/tempCar/Car.scene");

        chasis1 = findGeom(meshNode, "Car");
        chasis12 = findGeom(meshNode1, "Car");
        chasis12.setLocalTranslation(0, 0.8f, 0);
        chasis12.setLocalScale(chasis12.getWorldScale().mult(0.3f));
        chasis1.rotate(0, 3.135f, 0);
        chasis12.rotate(0, 3.135f, 0);
        
        
        chasis12.setMaterial(materials.getMatChasis());
        
        CollisionShape carHull = CollisionShapeFactory.createDynamicMeshShape(chasis1);
        BoundingBox carbox = (BoundingBox) chasis1.getModelBound();

        //create vehicle node
        vehicleNode = new Node("vehicleNode");
        vehicle = new VehicleControl(carHull, 400);
        vehicleNode.addControl(vehicle);
        vehicleNode.attachChild(chasis12);
        
        Geometry cristal = findGeom(meshNode1, "cristal");
        cristal.setLocalTranslation(cristal.getWorldTranslation().x*0.3f,
                                    cristal.getWorldTranslation().y*0.3f+1.4f,
                                    cristal.getWorldTranslation().z*0.3f);
        cristal.setLocalScale(cristal.getWorldScale().mult(0.3f));
        cristal.rotate(0, 3.135f, 0);
        vehicleNode.attachChild(cristal);
        
        vehicle.setSuspensionCompression(carSettings.getCompValue() * 2.0f * FastMath.sqrt(carSettings.getStiffness()));
        vehicle.setSuspensionDamping(carSettings.getDampValue() * 2.0f * FastMath.sqrt(carSettings.getStiffness()));
        vehicle.setSuspensionStiffness(carSettings.getStiffness());
        vehicle.setMaxSuspensionForce(carSettings.getMaxSuspensionForce());

        //Create four wheels and add them at their locations
        Vector3f wheelDirection = new Vector3f(0, -1, 0); // was 0, -1, 0
        Vector3f wheelAxle = new Vector3f(-1, 0, 0); // was -1, 0, 0
        float radius = 0.5f;
        float restLength = 0.3f;
        float yOff = 0.5f;
        float xOff = 1f;
        float zOff = 2f;


        Node node1 = new Node("wheel 1 node");
        wheel1 = findGeom(meshNode, "WheelFrontLeft");
        wheel12 = findGeom(meshNode1, "WheelFrontLeft");
        Vector3f wheelPos = wheel12.getWorldTranslation();
        wheel1.setMaterial(materials.getMatWheels());
        node1.attachChild(wheel1);
        wheel1.center();
        BoundingBox box = (BoundingBox) wheel1.getModelBound();
        wheelRadius = box.getYExtent();
        float back_wheel_h = (wheelRadius * 1.7f) - 1f;
        float front_wheel_h = (wheelRadius * 1.9f) - 1f;
        vehicle.addWheel(wheel1.getParent(), carbox.getCenter().add(wheelPos.x*0.3f, -back_wheel_h-0.6f, wheelPos.z*0.3f),
                wheelDirection, wheelAxle, 0.2f, wheelRadius, false);

        Node node2 = new Node("wheel 2 node");
        wheel2 = findGeom(meshNode, "WheelFrontRight");
        wheel22 = findGeom(meshNode1, "WheelFrontRight");
        wheelPos = wheel22.getWorldTranslation();
        node2.attachChild(wheel2);
        wheel2.setMaterial(materials.getMatWheels());
        wheel2.center();
        box = (BoundingBox) wheel2.getModelBound();
        vehicle.addWheel(wheel2.getParent(), carbox.getCenter().add(wheelPos.x*0.3f, -back_wheel_h-0.6f, wheelPos.z*0.3f),
                wheelDirection, wheelAxle, 0.2f, wheelRadius, false);

        Node node3 = new Node("wheel 3 node");
        wheel3 = findGeom(meshNode, "WheelBackLeft");
        wheel32 = findGeom(meshNode1, "WheelBackLeft");
        wheelPos = wheel32.getWorldTranslation();
        wheel3.setMaterial(materials.getMatWheels());
        node3.attachChild(wheel3);
        wheel3.center();
        box = (BoundingBox) wheel3.getModelBound();
        vehicle.addWheel(wheel3.getParent(), carbox.getCenter().add(wheelPos.x*0.3f, -front_wheel_h-0.38f, wheelPos.z*0.3f),
                wheelDirection, wheelAxle, 0.2f, wheelRadius, true);

        Node node4 = new Node("wheel 4 node");
        wheel4 = findGeom(meshNode, "WheelBackRight");
        wheel42 = findGeom(meshNode1, "WheelBackRight");
        wheelPos = wheel42.getWorldTranslation();
        wheel4.setMaterial(materials.getMatWheels());
        node4.attachChild(wheel4);
        wheel4.center();
        box = (BoundingBox) wheel4.getModelBound();
        vehicle.addWheel(wheel4.getParent(), carbox.getCenter().add(wheelPos.x*0.3f, -front_wheel_h-0.38f, wheelPos.z*0.3f),
                wheelDirection, wheelAxle, 0.2f, wheelRadius, true);

        vehicleNode.attachChild(node1);
        vehicleNode.attachChild(node2);
        vehicleNode.attachChild(node3);
        vehicleNode.attachChild(node4);

        vehicle.getWheel(0).setFrictionSlip(9.8f);
        vehicle.getWheel(1).setFrictionSlip(9.8f);
        physicsSpace.add(vehicle);
        
        initAudio();
    }
    
    private void buildFerrari() {
        Node meshNode = (Node) assetManager.loadModel("Models/tempCar/Car.scene");

        chasis1 = findGeom(meshNode, "Car");
        chasis1.rotate(0, 3.135f, 0);
        chasis1.setMaterial(materials.getMatChasis());
        
        CollisionShape carHull = CollisionShapeFactory.createDynamicMeshShape(chasis1);
        BoundingBox box = (BoundingBox) chasis1.getModelBound();

        //create vehicle node
        vehicleNode = new Node("vehicleNode");
        vehicle = new VehicleControl(carHull, 400);
        vehicleNode.addControl(vehicle);
        vehicleNode.attachChild(chasis1);
        
        vehicle.setSuspensionCompression(carSettings.getCompValue() * 2.0f * FastMath.sqrt(carSettings.getStiffness()));
        vehicle.setSuspensionDamping(carSettings.getDampValue() * 2.0f * FastMath.sqrt(carSettings.getStiffness()));
        vehicle.setSuspensionStiffness(carSettings.getStiffness());
        vehicle.setMaxSuspensionForce(carSettings.getMaxSuspensionForce());

        //Create four wheels and add them at their locations
        Vector3f wheelDirection = new Vector3f(0, -1, 0); // was 0, -1, 0
        Vector3f wheelAxle = new Vector3f(-1, 0, 0); // was -1, 0, 0
        float radius = 0.5f;
        float restLength = 0.3f;
        float yOff = 0.5f;
        float xOff = 1f;
        float zOff = 2f;


        Node node1 = new Node("wheel 1 node");
        wheel1 = findGeom(meshNode, "WheelFrontLeft");
        wheel1.setMaterial(materials.getMatWheels());
        node1.attachChild(wheel1);
        wheel1.center();
        box = (BoundingBox) wheel1.getModelBound();
        wheelRadius = box.getYExtent();
        float back_wheel_h = (wheelRadius * 1.7f) - 1f;
        float front_wheel_h = (wheelRadius * 1.9f) - 1f;
        vehicle.addWheel(wheel1.getParent(), box.getCenter().add(0, -back_wheel_h, -0.5f),
                wheelDirection, wheelAxle, 0.2f, wheelRadius, false);

        Node node2 = new Node("wheel 2 node");
        wheel2 = findGeom(meshNode, "WheelFrontRight");
        node2.attachChild(wheel2);
        wheel2.setMaterial(materials.getMatWheels());
        wheel2.center();
        box = (BoundingBox) wheel2.getModelBound();
        vehicle.addWheel(wheel2.getParent(), box.getCenter().add(0, -back_wheel_h, -0.5f),
                wheelDirection, wheelAxle, 0.2f, wheelRadius, false);

        Node node3 = new Node("wheel 3 node");
        wheel3 = findGeom(meshNode, "WheelBackLeft");
        wheel3.setMaterial(materials.getMatWheels());
        node3.attachChild(wheel3);
        wheel3.center();
        box = (BoundingBox) wheel3.getModelBound();
        vehicle.addWheel(wheel3.getParent(), box.getCenter().add(0, -front_wheel_h, -0.4f),
                wheelDirection, wheelAxle, 0.2f, wheelRadius, true);

        Node node4 = new Node("wheel 4 node");
        wheel4 = findGeom(meshNode, "WheelBackRight");
        wheel4.setMaterial(materials.getMatWheels());
        node4.attachChild(wheel4);
        wheel4.center();
        box = (BoundingBox) wheel4.getModelBound();
        vehicle.addWheel(wheel4.getParent(), box.getCenter().add(0, -front_wheel_h, -0.4f),
                wheelDirection, wheelAxle, 0.2f, wheelRadius, true);

        vehicleNode.attachChild(node1);
        vehicleNode.attachChild(node2);
        vehicleNode.attachChild(node3);
        vehicleNode.attachChild(node4);

        vehicle.getWheel(0).setFrictionSlip(9.8f);
        vehicle.getWheel(1).setFrictionSlip(9.8f);
        physicsSpace.add(vehicle);
        
        initAudio();
    }
        
        
    private void initAudio() {
         accelerate_sound = new Audio(vehicleNode, assetManager, "accelerate_sound.wav");
         decelerate_sound = new Audio(vehicleNode, assetManager, "decelerate_sound.wav");
         max_velocity_sound = new Audio(vehicleNode, assetManager, "max_velocity_sound.wav", true);
         brake_sound = new Audio(vehicleNode, assetManager, "brake_sound.wav");
         idling_car_sound = new Audio(vehicleNode, assetManager, "idling_car_sound.wav", true);
    }
    
    public Vector3f getPosicio() {
        return this.vehicle.getPhysicsLocation();
    }
    
    public void setPosicioCarrera(int p){
        posicioCarrera=p;
    }
    
    public int getPosicioCarrera(){
        return posicioCarrera;
    }
    
    public int getNumVoltes(){
        return numVoltes;
    }
    public int getEstatControlVolta(){
        return estatControlVolta;
    }
    public float getDistanciaEstatControlVolta() {
        return getDistancia(puntControlVolta);
    }
    
    public float getDistancia (Vector3f pto) { /*busquem la distancia del rival al pto del parametre*/
        Vector3f posRival= this.getPosicio();
        float distancia= pto.distance(posRival);
        return distancia;
    }
    
    public VehicleControl getVehicle() {
        return vehicle;
    }

    public Spatial getSpatial() {
        return (Spatial) vehicleNode;
    }

    public void turnLeft(boolean value) {
        if (value) {
            carSettings.setSteeringValue(carSettings.getSteeringValue() + .2f);
        } else {
            carSettings.setSteeringValue(carSettings.getSteeringValue() - .2f);
        }
        vehicle.steer(carSettings.getSteeringValue());
    }

    public void turnRight(boolean value) {
        if (value) {
            carSettings.setSteeringValue(carSettings.getSteeringValue() - .2f);
        } else {
            carSettings.setSteeringValue(carSettings.getSteeringValue() + .2f);
        }
        vehicle.steer(carSettings.getSteeringValue());
    }

    public void forward(boolean value) {
        soundForward(value);
        carSettings.setAccelerationValue(0);
        if (value) {
            forwardMode = true;
            if(!handBrakeMode){
                reverseMode = false;
                carSettings.setAccelerationValue(carSettings.getAccelerationValue()+
                        (carSettings.getAccelerationForce() * carSettings.getAccelerationFactor()));
            }
            vehicle.accelerate(carSettings.getAccelerationValue());
        } else {
            if(!handBrakeMode && carSettings.getAccelerationValue()!=0){
                carSettings.setAccelerationValue(carSettings.getAccelerationValue()-
                        (carSettings.getAccelerationForce() * carSettings.getAccelerationFactor()));
            } 
            vehicle.accelerate(carSettings.getAccelerationValue());
            forwardMode = false;
        }
    }
    
    
    public void soundForward(boolean value) {
        float speed = getSpeed();
        if (value && speed > 190) {
            decelerate_sound.stop();
            accelerate_sound.play(10.5f);
            max_velocity_sound.play();
        }
        if (value && speed > -5) {
            decelerate_sound.stop();
            if (speed < 1) {
                accelerate_sound.play(0.0f);
            } else {
                accelerate_sound.play(speed/20.0f);
            }
        } else if (!value) {
            accelerate_sound.stop();
            if (speed > 190) {
                decelerate_sound.play(0.0f);
            } else {
                decelerate_sound.play(10.5f - speed/20.0f);
            }
        }
    }

    public void back(boolean value) {
        brake_sound.play();
        float valueBrake;
        if(!handBrakeMode){
            if (value) {
                reverse();
            } else {
                reverseMode = false;
                vehicle.accelerate(0f);
                brake(0f);
            }
        }
    }

    public void reset(boolean value, Vector3f pos, Quaternion rot) {
        if (value) {
            vehicle.setPhysicsLocation(pos);
            vehicle.setPhysicsRotation(rot);
            vehicle.setLinearVelocity(Vector3f.ZERO);
            vehicle.setAngularVelocity(Vector3f.ZERO);
            vehicle.resetSuspension();
            carSettings.setAccelerationValue(0);
            carSettings.setSteeringValue(0);
            reverseMode = false;
            handBrakeMode = false;
            forwardMode = false;
            vehicle.accelerate(0f);
        } else {
        }
    }

    public void reverse() {
        idling_car_sound.play();
        float valueBrake;
        if (getSpeed() > 5) {
            valueBrake = carSettings.getBrakeForce() / carSettings.getBrakeForceFactor();
            brake(valueBrake);
        }
        reverseMode = true;
        carSettings.setAccelerationValue((float)(carSettings.getAccelerationValue()-
                        (carSettings.getAccelerationForce() * carSettings.getReverseFactor())));
        vehicle.accelerate(carSettings.getAccelerationValue());
    }

    public void brake(float valueBrake) {
        vehicle.brake(valueBrake);
    }
    
    public void handBrake(boolean value){
        brake_sound.play();
        float valueBrake;
        if(!reverseMode){
            if(value){
                handBrakeMode = true;
                if(forwardMode && carSettings.getAccelerationValue()!=0){
                    carSettings.setAccelerationValue(carSettings.getAccelerationValue()-
                        (carSettings.getAccelerationForce() * carSettings.getAccelerationFactor()));
                    vehicle.accelerate(carSettings.getAccelerationValue()); 
                }
                //valueBrake = brakeForce;
                //brake(valueBrake);
                //vehicle.brake(vehicle.getWheel(0)., brakeForce*100);
                vehicle.brake(0, carSettings.getBrakeForce()*5);
                vehicle.brake(1, carSettings.getBrakeForce()*5);
            } else {
                handBrakeMode = false; 
                //brake(0f); 
                vehicle.brake(0, 0);
                vehicle.brake(1, 0);
                if(forwardMode){
                    forward(true);
                }
            }
        }else{
            if(value){
                carSettings.setAccelerationValue((float)(carSettings.getAccelerationValue()+
                        (carSettings.getAccelerationForce() * carSettings.getReverseFactor())));
                vehicle.accelerate(carSettings.getAccelerationValue());
                valueBrake = carSettings.getBrakeForce();
                brake(valueBrake);
            } else {
                brake(0f);
                back(true);
            }
        }
    }
    
    public float getSpeed(){
        return vehicle.getCurrentVehicleSpeedKmHour();
    }

    public Vector3f getInitialPos() {
        return initialPos;
    }

    public void setInitialPos(Vector3f initialPos) {
        this.initialPos = initialPos;
    }

    public Quaternion getInitialRot() {
        return initialRot;
    }

    public void setInitialRot(Quaternion initialRot) {
        this.initialRot = initialRot;
    }
    
    public void upDateMaxSpeed(){
        float speed;
        speed = getSpeed();
        if((reverseMode) && (speed < carSettings.getMaxReverseVelocity())){
            vehicle.accelerate(1f);
        }else if((!reverseMode) && (speed > carSettings.getMaxAccelerateVelocity()) && (forwardMode)){
            vehicle.accelerate(1f);
        }
    }
    
    public Vector3f getPuntControlVolta(){
        return puntControlVolta;
    }
}