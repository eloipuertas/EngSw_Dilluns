/*
 * Copyright (c) 2009-2010 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
//package controlador;
//
//import com.jme3.bullet.BulletAppState;
//import com.jme3.app.SimpleApplication;
//import com.jme3.asset.plugins.ZipLocator;
//import com.jme3.bounding.BoundingBox;
//import com.jme3.bullet.PhysicsSpace;
//import com.jme3.bullet.collision.shapes.BoxCollisionShape;
//import com.jme3.bullet.collision.shapes.CollisionShape;
//import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
//import com.jme3.bullet.collision.shapes.MeshCollisionShape;
//import com.jme3.bullet.control.RigidBodyControl;
//import com.jme3.bullet.control.VehicleControl;
//import com.jme3.bullet.objects.VehicleWheel;
//import com.jme3.bullet.util.CollisionShapeFactory;
//import com.jme3.input.KeyInput;
//import com.jme3.input.MouseInput;
//import com.jme3.input.controls.ActionListener;
//import com.jme3.input.controls.KeyTrigger;
//import com.jme3.input.controls.MouseAxisTrigger;
//import com.jme3.input.controls.MouseButtonTrigger;
//import com.jme3.light.AmbientLight;
//import com.jme3.light.DirectionalLight;
//import com.jme3.material.Material;
//import com.jme3.math.ColorRGBA;
//import com.jme3.math.FastMath;
//import com.jme3.math.Matrix3f;
//import com.jme3.math.Vector2f;
//import com.jme3.math.Vector3f;
//import com.jme3.renderer.queue.RenderQueue.ShadowMode;
//import com.jme3.scene.CameraNode;
//import com.jme3.scene.Geometry;
//import com.jme3.scene.Node;
//import com.jme3.scene.Spatial;
//import com.jme3.scene.control.CameraControl;
//import com.jme3.scene.control.CameraControl.ControlDirection;
//import com.jme3.scene.shape.Cylinder;
//import model.VehicleProtagonista;
//import model.WorldCreator;
//import vista.Display;
//
//
//public class Main extends SimpleApplication implements ActionListener {
//
//    private BulletAppState bulletAppState;
//    private float steeringValue = 0;
//    private float accelerationValue = 0;
//    private VehicleProtagonista car;
//    private Spatial sceneModel;
//    private final float accelerationForce = 1000.0f;
//    private final float brakeForce = 100.0f;
//    private CameraNode camNode;
//    
//    private MenuController menu;
//    private Display display;
//
//
//    public static void main(String[] args) {
//        Main app = new Main();
//        app.start();
//    }
//    
//    private RigidBodyControl landscape;
//
//    private void setupKeys() {
//        inputManager.addMapping("Lefts", new KeyTrigger(KeyInput.KEY_LEFT));
//        inputManager.addMapping("Rights", new KeyTrigger(KeyInput.KEY_RIGHT));
//        inputManager.addMapping("Ups", new KeyTrigger(KeyInput.KEY_UP));
//        inputManager.addMapping("Downs", new KeyTrigger(KeyInput.KEY_DOWN));
//        inputManager.addMapping("Space", new KeyTrigger(KeyInput.KEY_SPACE));
//        inputManager.addMapping("Reset", new KeyTrigger(KeyInput.KEY_RETURN));
//        inputManager.addListener(this, "Lefts");
//        inputManager.addListener(this, "Rights");
//        inputManager.addListener(this, "Ups");
//        inputManager.addListener(this, "Downs");
//        inputManager.addListener(this, "Space");
//        inputManager.addListener(this, "Reset");
//        
//        inputManager.addMapping("moveForward", new KeyTrigger(KeyInput.KEY_W));
//        inputManager.addMapping("moveBackward", new KeyTrigger(KeyInput.KEY_S));
//        inputManager.addMapping("moveRight", new KeyTrigger(KeyInput.KEY_D));
//        inputManager.addMapping("moveLeft", new KeyTrigger(KeyInput.KEY_A));
//        inputManager.addMapping("toggleRotate", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
//        inputManager.addMapping("rotateRight", new MouseAxisTrigger(MouseInput.AXIS_X, true));
//        inputManager.addMapping("rotateLeft", new MouseAxisTrigger(MouseInput.AXIS_X, false));
//        inputManager.addListener(this, "moveForward", "moveBackward", "moveRight", "moveLeft");
//        inputManager.addListener(this, "rotateRight", "rotateLeft", "toggleRotate");
//    }
//
//    @Override
//    public void simpleInitApp() {
//        bulletAppState = new BulletAppState();
//        stateManager.attach(bulletAppState);
//            
//        /*if (settings.getRenderer().startsWith("LWJGL")) {
//            BasicShadowRenderer bsr = new BasicShadowRenderer(assetManager, 512);
//            bsr.setDirection(new Vector3f(-0.5f, -0.3f, -0.3f).normalizeLocal());
//            viewPort.addProcessor(bsr);
//        }
//        cam.setFrustumFar(150f);
//         * 
//         */
//
//        setupKeys();
//        setUpLight();
//        
//        car = new VehicleProtagonista(getAssetManager(), getPhysicsSpace(), cam);
//        
//        car.buildCar();
//        display = new Display(assetManager,settings,guiNode,guiFont);
//        
//        //Añadimos el coche protagonista
//        rootNode.attachChild(car.getSpatial());
//        
//        //Settejem la camera
//        
//        camNode = new CameraNode("CamNode", cam);
//        camNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
//        camNode.setLocalTranslation(new Vector3f(0, 4, -15));
//        //camNode.setLocalTranslation(new Vector3f(-15, 15, -15));
//        //camNode.setLocalTranslation(new Vector3f(-15, 15, -15));
//        camNode.lookAt(car.getSpatial().getLocalTranslation(), Vector3f.UNIT_Y);
//        
//        rootNode.attachChild(camNode);
//        
//        //Cargamos la escena
//        WorldCreator.createWorld(rootNode, assetManager, bulletAppState);
//        
//        menu = new MenuController(stateManager,assetManager,rootNode,guiViewPort,inputManager,audioRenderer);   
//    }
//
//    private void setUpLight() {
//        // We add light so we see the scene
//        AmbientLight al = new AmbientLight();
//        al.setColor(ColorRGBA.White.mult(1.3f));
//        rootNode.addLight(al);
//
//        DirectionalLight dl = new DirectionalLight();
//        dl.setColor(ColorRGBA.White);
//        dl.setDirection(new Vector3f(2.8f, -2.8f, -2.8f).normalizeLocal());
//        rootNode.addLight(dl);
//    }
//    
//    private PhysicsSpace getPhysicsSpace() {
//        return bulletAppState.getPhysicsSpace();
//    }
//
//    public void onAction(String binding, boolean value, float tpf) {
//        if (binding.equals("Lefts")) {
//            if (value) {
//                steeringValue += .5f;
//            } else {
//                steeringValue += -.5f;
//            }
//            car.getVehicle().steer(steeringValue);
//        } else if (binding.equals("Rights")) {
//            if (value) {
//                steeringValue += -.5f;
//            } else {
//                steeringValue += .5f;
//            }
//            car.getVehicle().steer(steeringValue);
//        } else if (binding.equals("Ups")) {
//            if (value) {
//                accelerationValue += accelerationForce;
//            } else {
//                accelerationValue -= accelerationForce;
//            }
//            car.getVehicle().accelerate(accelerationValue);
//        } else if (binding.equals("Downs")) {
//            if (value) {
//                car.getVehicle().brake(brakeForce);
//            } else {
//                car.getVehicle().brake(0f);
//            }
//        } else if (binding.equals("Reset")) {
//            if (value) {
//                System.out.println("Reset");
//                car.getVehicle().setPhysicsLocation(Vector3f.ZERO);
//                car.getVehicle().setPhysicsRotation(new Matrix3f());
//                car.getVehicle().setLinearVelocity(Vector3f.ZERO);
//                car.getVehicle().setAngularVelocity(Vector3f.ZERO);
//                car.getVehicle().resetSuspension();
//            } else {
//            }
//        }
//    }
//    
//    private void updateDisplay(){
//        if (menu.isGameStarted() && !display.isDisplayAdded()){
//            display.addDisplay((int)(settings.getWidth()/1.28),(int)(settings.getHeight()/4.8));
//        }
//        else if (menu.isGameStarted()){
//            display.updateDisplay((float)Math.sqrt((Math.pow(car.getVehicle().getLinearVelocity().x,2)+Math.pow(car.getVehicle().getLinearVelocity().z,2)+Math.pow(car.getVehicle().getLinearVelocity().y,2))),1);
//        }
//    } 
//    
//    @Override
//    public void simpleUpdate(float tpf) {
//        flyCam.setEnabled(false);
//        
//        camNode.lookAt(car.getSpatial().getWorldTranslation(), Vector3f.UNIT_Y);
//        
//        camNode.setLocalTranslation(car.getSpatial().localToWorld( new Vector3f( 0, 4, -15), null));
//        updateDisplay();
//    }
//}

/*
 * Copyright (c) 2009-2010 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package controlador;

import com.jme3.app.SimpleApplication;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.objects.VehicleWheel;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.shadow.BasicShadowRenderer;
import model.WorldCreator;

public class Main extends SimpleApplication implements ActionListener {

    private BulletAppState bulletAppState;
    private VehicleControl player;
    private VehicleWheel fr, fl, br, bl;
    private Node node_fr, node_fl, node_br, node_bl;
    private float wheelRadius;
    private float steeringValue = 0;
    private float accelerationValue = 0;
    private Node carNode;

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    private void setupKeys() {
        inputManager.addMapping("Lefts", new KeyTrigger(KeyInput.KEY_H));
        inputManager.addMapping("Rights", new KeyTrigger(KeyInput.KEY_K));
        inputManager.addMapping("Ups", new KeyTrigger(KeyInput.KEY_U));
        inputManager.addMapping("Downs", new KeyTrigger(KeyInput.KEY_J));
        inputManager.addMapping("Space", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Reset", new KeyTrigger(KeyInput.KEY_RETURN));
        inputManager.addListener(this, "Lefts");
        inputManager.addListener(this, "Rights");
        inputManager.addListener(this, "Ups");
        inputManager.addListener(this, "Downs");
        inputManager.addListener(this, "Space");
        inputManager.addListener(this, "Reset");
    }

    @Override
    public void simpleInitApp() {
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
//        bulletAppState.getPhysicsSpace().enableDebug(assetManager);
        if (settings.getRenderer().startsWith("LWJGL")) {
            BasicShadowRenderer bsr = new BasicShadowRenderer(assetManager, 512);
            bsr.setDirection(new Vector3f(-0.5f, -0.3f, -0.3f).normalizeLocal());
            viewPort.addProcessor(bsr);
        }
        cam.setFrustumFar(150f);
        flyCam.setMoveSpeed(10);

        setupKeys();
        WorldCreator.createWorld(rootNode, assetManager, bulletAppState, this.getViewPort(), this.getRenderer());
//        setupFloor();
        buildPlayer();

        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(-0.5f, -1f, -0.3f).normalizeLocal());
        rootNode.addLight(dl);

        dl = new DirectionalLight();
        dl.setDirection(new Vector3f(0.5f, -0.1f, 0.3f).normalizeLocal());
        rootNode.addLight(dl);
    }

    private PhysicsSpace getPhysicsSpace() {
        return bulletAppState.getPhysicsSpace();
    }

//    public void setupFloor() {
//        Material mat = assetManager.loadMaterial("Textures/Terrain/BrickWall/BrickWall.j3m");
//        mat.getTextureParam("DiffuseMap").getTextureValue().setWrap(WrapMode.Repeat);
////        mat.getTextureParam("NormalMap").getTextureValue().setWrap(WrapMode.Repeat);
////        mat.getTextureParam("ParallaxMap").getTextureValue().setWrap(WrapMode.Repeat);
//
//        Box floor = new Box(Vector3f.ZERO, 140, 1f, 140);
//        floor.scaleTextureCoordinates(new Vector2f(112.0f, 112.0f));
//        Geometry floorGeom = new Geometry("Floor", floor);
//        floorGeom.setShadowMode(ShadowMode.Receive);
//        floorGeom.setMaterial(mat);
//
//        PhysicsNode tb = new PhysicsNode(floorGeom, new MeshCollisionShape(floorGeom.getMesh()), 0);
//        tb.setLocalTranslation(new Vector3f(0f, -6, 0f));
////        tb.attachDebugShape(assetManager);
//        rootNode.attachChild(tb);
//        getPhysicsSpace().add(tb);
//    }

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

    private void buildPlayer() {
        float stiffness = 120.0f;//200=f1 car
        float compValue = 0.2f; //(lower than damp!)
        float dampValue = 0.3f;
        final float mass = 400;

        //Load model and get chassis Geometry
        carNode = (Node)assetManager.loadModel("Models/Ferrari/Car.scene");
        carNode.setShadowMode(ShadowMode.Cast);
        Geometry chasis = findGeom(carNode, "Car");
        BoundingBox box = (BoundingBox) chasis.getModelBound();

        //Create a hull collision shape for the chassis
        CollisionShape carHull = CollisionShapeFactory.createDynamicMeshShape(chasis);

        //Create a vehicle control
        player = new VehicleControl(carHull, mass);
        carNode.addControl(player);

        //Setting default values for wheels
        player.setSuspensionCompression(compValue * 2.0f * FastMath.sqrt(stiffness));
        player.setSuspensionDamping(dampValue * 2.0f * FastMath.sqrt(stiffness));
        player.setSuspensionStiffness(stiffness);
        player.setMaxSuspensionForce(10000);

        //Create four wheels and add them at their locations
        //note that our fancy car actually goes backwards..
        Vector3f wheelDirection = new Vector3f(0, -1, 0);
        Vector3f wheelAxle = new Vector3f(-1, 0, 0);

        Geometry wheel_fr = findGeom(carNode, "WheelFrontRight");
        wheel_fr.center();
        box = (BoundingBox) wheel_fr.getModelBound();
        wheelRadius = box.getYExtent();
        float back_wheel_h = (wheelRadius * 1.7f) - 1f;
        float front_wheel_h = (wheelRadius * 1.9f) - 1f;
        player.addWheel(wheel_fr.getParent(), box.getCenter().add(0, -front_wheel_h, 0),
                wheelDirection, wheelAxle, 0.2f, wheelRadius, true);

        Geometry wheel_fl = findGeom(carNode, "WheelFrontLeft");
        wheel_fl.center();
        box = (BoundingBox) wheel_fl.getModelBound();
        player.addWheel(wheel_fl.getParent(), box.getCenter().add(0, -front_wheel_h, 0),
                wheelDirection, wheelAxle, 0.2f, wheelRadius, true);

        Geometry wheel_br = findGeom(carNode, "WheelBackRight");
        wheel_br.center();
        box = (BoundingBox) wheel_br.getModelBound();
        player.addWheel(wheel_br.getParent(), box.getCenter().add(0, -back_wheel_h, 0),
                wheelDirection, wheelAxle, 0.2f, wheelRadius, false);

        Geometry wheel_bl = findGeom(carNode, "WheelBackLeft");
        wheel_bl.center();
        box = (BoundingBox) wheel_bl.getModelBound();
        player.addWheel(wheel_bl.getParent(), box.getCenter().add(0, -back_wheel_h, 0),
                wheelDirection, wheelAxle, 0.2f, wheelRadius, false);

        player.getWheel(2).setFrictionSlip(4);
        player.getWheel(3).setFrictionSlip(4);

        rootNode.attachChild(carNode);
        getPhysicsSpace().add(player);
    }

    public void onAction(String binding, boolean value, float tpf) {
        if (binding.equals("Lefts")) {
            if (value) {
                steeringValue += .5f;
            } else {
                steeringValue += -.5f;
            }
            player.steer(steeringValue);
        } else if (binding.equals("Rights")) {
            if (value) {
                steeringValue += -.5f;
            } else {
                steeringValue += .5f;
            }
            player.steer(steeringValue);
        } //note that our fancy car actually goes backwards..
        else if (binding.equals("Ups")) {
            if (value) {
                accelerationValue -= 800;
            } else {
                accelerationValue += 800;
            }
            player.accelerate(accelerationValue);
            player.setCollisionShape(CollisionShapeFactory.createDynamicMeshShape(findGeom(carNode, "Car")));
        } else if (binding.equals("Downs")) {
            if (value) {
                player.brake(40f);
            } else {
                player.brake(0f);
            }
        } else if (binding.equals("Reset")) {
            if (value) {
                System.out.println("Reset");
                player.setPhysicsLocation(Vector3f.ZERO);
                player.setPhysicsRotation(new Matrix3f());
                player.setLinearVelocity(Vector3f.ZERO);
                player.setAngularVelocity(Vector3f.ZERO);
                player.resetSuspension();
            } else {
            }
        }
    }

    @Override
    public void simpleUpdate(float tpf) {
        //cam.lookAt(carNode.getWorldTranslation(), Vector3f.UNIT_Y);
    }
}