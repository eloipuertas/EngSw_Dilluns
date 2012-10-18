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
import com.jme3.asset.plugins.ZipLocator;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.CameraControl.ControlDirection;
import com.jme3.scene.shape.Cylinder;
import com.jme3.ui.Picture;
import de.lessvoid.nifty.Nifty;


public class Main extends SimpleApplication implements ActionListener {

    private BulletAppState bulletAppState;
    private float steeringValue = 0;
    private float accelerationValue = 0;
    private VehicleControl vehicle;
    private Spatial sceneModel;
    private final float accelerationForce = 1000.0f;
    private final float brakeForce = 100.0f;
    private Vector3f jumpForce = new Vector3f(0, 3000, 0);
    private Node displayNode = new Node("Display");   
    private MenuController startScreen;
    private BitmapText pos;
    private Node vehicleNode;


    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }
    
    private RigidBodyControl landscape;
    private CameraNode camNode;

    private void setupKeys() {
        inputManager.addMapping("Lefts", new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("Rights", new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping("Ups", new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping("Downs", new KeyTrigger(KeyInput.KEY_DOWN));
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
        
        // We load the scene from the zip file and adjust its size.
        assetManager.registerLocator("town.zip", ZipLocator.class);
        sceneModel = assetManager.loadModel("main.scene");
        sceneModel.setLocalScale(2f);
        
        CollisionShape sceneShape =
        CollisionShapeFactory.createMeshShape((Node) sceneModel);
        landscape = new RigidBodyControl(sceneShape, 0);
        sceneModel.addControl(landscape);
        
            
        /*if (settings.getRenderer().startsWith("LWJGL")) {
            BasicShadowRenderer bsr = new BasicShadowRenderer(assetManager, 512);
            bsr.setDirection(new Vector3f(-0.5f, -0.3f, -0.3f).normalizeLocal());
            viewPort.addProcessor(bsr);
        }
        cam.setFrustumFar(150f);
         * 
         */

        setupKeys();
        setUpLight();
        buildPlayer();
        
        //Añadimos la scena
        rootNode.attachChild(sceneModel);
        
        //Añadimos el mundo en la colisiones
        bulletAppState.getPhysicsSpace().add(landscape);
        
        //Añadimos el menu creador con nifty
        startScreen = new MenuController();
        stateManager.attach(startScreen);
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay( assetManager, inputManager, audioRenderer, guiViewPort);
        Nifty nifty = niftyDisplay.getNifty();
        guiViewPort.addProcessor(niftyDisplay);
        nifty.fromXml("Interface/Menu/menu.xml", "start", startScreen);
    }

    private void setUpLight() {
        // We add light so we see the scene
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(1.3f));
        rootNode.addLight(al);

        DirectionalLight dl = new DirectionalLight();
        dl.setColor(ColorRGBA.White);
        dl.setDirection(new Vector3f(2.8f, -2.8f, -2.8f).normalizeLocal());
        rootNode.addLight(dl);
    }
    
    private PhysicsSpace getPhysicsSpace() {
        return bulletAppState.getPhysicsSpace();
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

    private void buildPlayer() {
        final float mass = 400;
        Material mat = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.setColor("Color", ColorRGBA.Red);
        Material chasisMat = new Material(getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        chasisMat.getAdditionalRenderState().setWireframe(true);
        chasisMat.setBoolean("UseMaterialColors",true);    
        chasisMat.setColor("Specular",ColorRGBA.White);
        chasisMat.setColor("Diffuse",ColorRGBA.White);
        chasisMat.setFloat("Shininess", 5f); // [1,128] 
        
        

        //create a compound shape and attach the BoxCollisionShape for the car body at 0,1,0
        //this shifts the effective center of mass of the BoxCollisionShape to 0,-1,0
        CompoundCollisionShape compoundShape = new CompoundCollisionShape();
        BoxCollisionShape box = new BoxCollisionShape(new Vector3f(1.2f, 0.5f, 2.4f));
        compoundShape.addChildShape(box, new Vector3f(0, 1, 0));

        //create vehicle node
        vehicleNode = new Node("vehicleNode");
        vehicle = new VehicleControl(compoundShape, 1000);
        vehicleNode.addControl(vehicle);
        
        Node meshNode = (Node) assetManager.loadModel("Models/simpleCar.scene");
        Geometry chasis1 = findGeom(meshNode, "Cube-geom-1");
        Geometry glass = findGeom(meshNode, "Cube-geom-2");
        
        //Spatial chasis = (Spatial)assetManager.loadModel("Models/Cube.mesh.xml");
        vehicleNode.attachChild(chasis1);
        vehicleNode.attachChild(glass);
        //chasis.setMaterial(chasisMat);
        //chasis.setLocalTranslation(0, 0.5f, 0);
        
        //Spatial glass = (Spatial)assetManager.loadModel("Models/");
        
         
        //Load model and get chassis Geometry
        /*Node vehicleNode = (Node)assetManager.loadModel("Models/cars/Cube.mesh.xml");
        vehicleNode.setShadowMode(ShadowMode.Cast);
        Geometry chasis = findGeom(vehicleNode, "Cube");
        BoundingBox box = (BoundingBox) chasis.getModelBound();

        //Create a hull collision shape for the chassis
        CollisionShape carHull = CollisionShapeFactory.createDynamicMeshShape(chasis);

        //Create a vehicle control
        vehicle = new VehicleControl(carHull, mass);
        vehicleNode.addControl(vehicle);
*/
        //setting suspension values for wheels, this can be a bit tricky
        //see also https://docs.google.com/Doc?docid=0AXVUZ5xw6XpKZGNuZG56a3FfMzU0Z2NyZnF4Zmo&hl=en
        float stiffness = 60.0f;//200=f1 car
        float compValue = .3f; //(should be lower than damp)
        float dampValue = .4f;
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

        Cylinder wheelMesh = new Cylinder(16, 16, radius, radius * 0.6f, true);

        Node node1 = new Node("wheel 1 node");
        //Geometry wheels1 = new Geometry("wheel 1", wheelMesh);
        Geometry wheels1 = findGeom(meshNode, "Cylinder-geom-1");
        node1.attachChild(wheels1);
        wheels1.rotate(0, 0, 1.55f);
        wheels1.scale(0.5f);
        //wheels1.setMaterial(mat);
        vehicle.addWheel(node1, new Vector3f(-xOff, yOff, zOff),
                wheelDirection, wheelAxle, restLength, radius, true);

        Node node2 = new Node("wheel 2 node");
        Geometry wheels2 = findGeom(meshNode, "Cylinder.001-geom-1");
        node2.attachChild(wheels2);
        wheels2.rotate(0, 0, 1.55f);
        wheels2.scale(0.5f);
        //wheels2.setMaterial(mat);
        vehicle.addWheel(node2, new Vector3f(xOff, yOff, zOff),
                wheelDirection, wheelAxle, restLength, radius, true);

        Node node3 = new Node("wheel 3 node");
        Geometry wheels3 = findGeom(meshNode, "Cylinder.002-geom-1");
        node3.attachChild(wheels3);
        wheels3.rotate(0, 0, 1.55f);
        wheels3.scale(0.5f);
        //wheels3.setMaterial(mat);
        vehicle.addWheel(node3, new Vector3f(-xOff, yOff, -zOff),
                wheelDirection, wheelAxle, restLength, radius, false);

        Node node4 = new Node("wheel 4 node");
        Geometry wheels4 = findGeom(meshNode, "Cylinder.003-geom-1");
        node4.attachChild(wheels4);
        wheels4.rotate(0, 0, 1.55f);
        wheels4.scale(0.5f);
        //wheels4.setMaterial(mat);
        vehicle.addWheel(node4, new Vector3f(xOff, yOff, -zOff),
                wheelDirection, wheelAxle, restLength, radius, false);

        vehicleNode.attachChild(node1);
        vehicleNode.attachChild(node2);
        vehicleNode.attachChild(node3);
        vehicleNode.attachChild(node4);
        
        rootNode.attachChild(vehicleNode);
        getPhysicsSpace().add(vehicle);
        
        //set forward camera node that follows the character
        camNode = new CameraNode("CamNode", cam);
        camNode.setControlDir(ControlDirection.SpatialToCamera);
        camNode.setLocalTranslation(new Vector3f(0, 4, -15));
        //camNode.setLocalTranslation(new Vector3f(-15, 15, -15));
        camNode.lookAt(vehicleNode.getLocalTranslation(), Vector3f.UNIT_Y);
        vehicleNode.attachChild(camNode);
       
         
        //disable the default 1st-person flyCam (don't forget this!!)
        flyCam.setEnabled(false);
    }

    public void onAction(String binding, boolean value, float tpf) {
        if (binding.equals("Lefts")) {
            if (value) {
                steeringValue += .5f;
            } else {
                steeringValue += -.5f;
            }
            vehicle.steer(steeringValue);
        } else if (binding.equals("Rights")) {
            if (value) {
                steeringValue += -.5f;
            } else {
                steeringValue += .5f;
            }
            vehicle.steer(steeringValue);
        } else if (binding.equals("Ups")) {
            if (value) {
                accelerationValue += accelerationForce;
            } else {
                accelerationValue -= accelerationForce;
            }
            vehicle.accelerate(accelerationValue);
        } else if (binding.equals("Downs")) {
            if (value) {
                vehicle.brake(brakeForce);
            } else {
                vehicle.brake(0f);
            }
        } else if (binding.equals("Space")) {
            if (value) {
                vehicle.applyImpulse(jumpForce, Vector3f.ZERO);
            }
        } else if (binding.equals("Reset")) {
            if (value) {
                System.out.println("Reset");
                vehicle.setPhysicsLocation(Vector3f.ZERO);
                vehicle.setPhysicsRotation(new Matrix3f());
                vehicle.setLinearVelocity(Vector3f.ZERO);
                vehicle.setAngularVelocity(Vector3f.ZERO);
                vehicle.resetSuspension();
            } else {
            }
        }
    }
    
    public void addDisplay(int x,int y){
        
        pos = new BitmapText(guiFont, false);          
        pos.setSize(guiFont.getCharSet().getRenderedSize());      // font size
        pos.setColor(ColorRGBA.White);                            // font color
        pos.setText("Pos:");                                    // the text
        pos.setLocalTranslation(settings.getWidth()-50,y+100,0);     // position
        guiNode.attachChild(pos);
        
        //Agregar fondo marcador
        Picture display = new Picture("display");
        display.setImage(assetManager, "Textures/Display/gauge.png", true);        
        float maxDimension = Math.max(settings.getWidth(),settings.getHeight());
        display.setWidth(maxDimension/3f);
        display.setHeight(maxDimension/3f);        
        display.setPosition(0,0);
        display.center();
        display.move(x,y, -1); //-1 para estar debajo de la aguja        
        guiNode.attachChild(display);
        
        //Agregar aguja
        Picture arrow = new Picture("arrow");
        arrow.setImage(assetManager, "Textures/Display/arrow.png", true);        
        arrow.setWidth(maxDimension/3f);
        arrow.setHeight(maxDimension/3f);
        arrow.setPosition(0,0);
        arrow.center();
        arrow.move(0, 0, 1); //1 para poner por encima del marcador                
        
        displayNode.attachChild(arrow);        
              
        guiNode.attachChild(displayNode);
        this.displayNode.move(x,y,0);       
    }
    
    public void updateDisplay(float speed,int pos){        
        if (startScreen.isGameStarted() && displayNode.getQuantity() < 1){
            this.addDisplay((int)(settings.getWidth()/1.28),(int)(settings.getHeight()/4.8));
            displayNode.rotate(0, 0,0.615f);            
        }
        else if (startScreen.isGameStarted()){
            
            this.pos.setText("Pos: "+pos);            
            
            if (speed > 200){
                speed=200;
            }
            else if (speed < 0){
                speed=0;
            }            
            float actual_gauge_speed = displayNode.getWorldRotation().getZ();          
            actual_gauge_speed = (float)(46.2606 + (-154.202f *actual_gauge_speed));
            float offset = actual_gauge_speed - speed;                           
            displayNode.rotate(0, 0,offset*0.022185f);
        }             
    }

    @Override
    public void simpleUpdate(float tpf) {
        cam.lookAt(vehicle.getPhysicsLocation(), Vector3f.UNIT_Y);
        updateDisplay((float)Math.sqrt((Math.pow(vehicle.getLinearVelocity().x,2)+Math.pow(vehicle.getLinearVelocity().z,2)+Math.pow(vehicle.getLinearVelocity().y,2))),1);
    }
}
