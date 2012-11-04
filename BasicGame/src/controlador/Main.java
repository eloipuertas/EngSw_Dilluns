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

import com.jme3.bullet.BulletAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.ZipLocator;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.objects.VehicleWheel;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.CameraControl;
import com.jme3.scene.control.CameraControl.ControlDirection;
import com.jme3.scene.shape.Cylinder;
import model.VehicleProtagonista;
import model.WorldCreator;
import vista.Display;
import model.Rival;


public class Main extends SimpleApplication implements ActionListener {

    private BulletAppState bulletAppState;
    private float steeringValue = 0;
    private float accelerationValue = 0;
    private VehicleProtagonista car;
    private Rival rival;
    private WorldCreator world;
    private Spatial sceneModel;
    private final float accelerationForce = 1000.0f;
    private final float brakeForce = 100.0f;
    private CameraNode camNode;
    
    private MenuController menu;
    private Display display;
    
    //Factores para disminuir y aumentar la acceleracion y la frenadas
    private int accelerationFactor = 2; //Factor multiplicativo
    private int brakeForceFactor = 2;   //Factor de division
    
/*Variables per a moure el rival per a fer el crcuit. Cal moure-ho en mesura del que es pugui 
 * a dins de la classe Rival*/
    int estado=1;
    public Vector3f direccioCar;
    public Vector3f direccioRival;
    public Vector2f r = new Vector2f(1.0f,0.1f);
    float angle;
    
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }
    
    private RigidBodyControl landscape;

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
        
        //Cargamos la escena
        world = new WorldCreator(rootNode, assetManager, bulletAppState, this.viewPort);
        world.createWorld();

            
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
        
        car = new VehicleProtagonista(getAssetManager(), getPhysicsSpace(), cam);
        
        car.buildCar();
         //Aqui creem la classe rival i la afegim al rootNode
        rival = new Rival(getAssetManager(), getPhysicsSpace());
        rival.buildCar();
        
        display = new Display(assetManager,settings,guiNode,this.timer);
        
        //Añadimos el coche protagonista
        rootNode.attachChild(car.getSpatial());
        
        //Añadimos Rival
        rootNode.attachChild(rival.getSpatial());
        
        //Settejem la camera
        
        camNode = new CameraNode("CamNode", cam);
        camNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
        camNode.setLocalTranslation(new Vector3f(0, 4, -15));
        //camNode.setLocalTranslation(new Vector3f(-15, 15, -15));
        //camNode.setLocalTranslation(new Vector3f(-15, 15, -15));
        camNode.lookAt(car.getSpatial().getLocalTranslation(), Vector3f.UNIT_Y);
        
        rootNode.attachChild(camNode);
        
        menu = new MenuController(settings,stateManager,assetManager,rootNode,guiViewPort,inputManager,audioRenderer,this,false,1,0,5,2,1,10,1,0,1);   
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

    public void onAction(String binding, boolean value, float tpf) {
        if (binding.equals("Lefts")) {
            if (value) {
                steeringValue += .5f;
            } else {
                steeringValue += -.5f;
            }
            car.getVehicle().steer(steeringValue);
        } else if (binding.equals("Rights")) {
            if (value) {
                steeringValue += -.5f;
            } else {
                steeringValue += .5f;
            }
            car.getVehicle().steer(steeringValue);
        } else if (binding.equals("Ups")) {
            if (value) {
                accelerationValue += (accelerationForce*accelerationFactor);
            } else {
                accelerationValue -= (accelerationForce*accelerationFactor);
            }
            System.out.println("Accelerar "+accelerationValue);
            System.out.println("AcceleraForce "+accelerationForce);
            car.getVehicle().accelerate(accelerationValue);
        } else if (binding.equals("Downs")) {
            if (value) {
                car.getVehicle().brake(brakeForce/brakeForceFactor);
            } else {
                car.getVehicle().brake(0f);
            }
        } else if (binding.equals("Reset")) {
            if (value) {
                System.out.println("Reset");
                car.getVehicle().setPhysicsLocation(Vector3f.ZERO);
                car.getVehicle().setPhysicsRotation(new Matrix3f());
                car.getVehicle().setLinearVelocity(Vector3f.ZERO);
                car.getVehicle().setAngularVelocity(Vector3f.ZERO);
                car.getVehicle().resetSuspension();
            } else {
            }
        }
    }
    
    private void updateDisplay(){
        if (menu.isGameStarted() && !display.isDisplayAdded()){
            float minDimension = Math.min(settings.getWidth(),settings.getHeight());
            display.addDisplay((int)(settings.getWidth()-(minDimension/2.5f)/2),(int)((minDimension/2.5f)/2),2.5f,(int)(settings.getWidth()-(minDimension/40f)-(minDimension/11.42f)-10),(int)(settings.getHeight()*0.975f),40,(int)(settings.getWidth()-(minDimension/9.23f)-10),(int)(settings.getHeight()*0.95f),9.23f,(int)(settings.getWidth()-(minDimension/11.42f)-10),(int)(settings.getHeight()*0.85f),11.42f);
            display.startChronograph();
            display.updatePosition(1);
        }
        else if (menu.isGameStarted()){
            display.updateDisplay((float)Math.sqrt((Math.pow(car.getVehicle().getLinearVelocity().x,2)+Math.pow(car.getVehicle().getLinearVelocity().z,2)+Math.pow(car.getVehicle().getLinearVelocity().y,2))));
            display.updateChronograph();
            display.updatePosition(1);
        }
    } 
     /*Metode per comprovar que el cotxe protagonista esta en moviment*/
    public boolean comprovaMoviment (){
        if (car.getVehicle().getLinearVelocity().length()>=5) {
            return true;
        } else {
            return false;
        }
    }
    @Override
    public void simpleUpdate(float tpf) {
        flyCam.setEnabled(false);
        
        camNode.lookAt(car.getSpatial().getWorldTranslation(), Vector3f.UNIT_Y);
        
        camNode.setLocalTranslation(car.getSpatial().localToWorld( new Vector3f( 0, 4, -15), null));
        System.out.println(car.getVehicle().getPhysicsLocation().getX());
        /*Codi per a moure el rival, cal moure-ho d'aqui*/
        switch (estado) {
            case 1:
                if(comprovaMoviment()==true) {
                    estado = 2;
                }
                break;
            case 2:
                
                if (rival.getVehicle().getPhysicsLocation().getZ()>=30) {
                    System.out.println("curva 1");
                    estado = 3;
                }
                if (rival.velocitat == 0) {
                    rival.moureEndavant();
                }
                break;
                
            case 3:
                r.setX(rival.getVehicle().getLinearVelocity().getX());
                r.setY(rival.getVehicle().getLinearVelocity().getZ());
                r = r.normalize();
                if (r.getX()<-0.9f && r.getY()<-0.2f) {
                    rival.getVehicle().steer(0);
                    estado = 4;
                    System.out.println("recta2");
                    
                } else {
                    rival.girarCurva1();
                }
                break;
            case 4:
                if (rival.getVehicle().getPhysicsLocation().getX()<=-40.f) {
                     System.out.println("curva 2");
                     estado = 5;
                }                
                rival.moureEndavant();
                break;
            case 5:
                r.setX(rival.getVehicle().getLinearVelocity().getX());
                r.setY(rival.getVehicle().getLinearVelocity().getZ());
                r = r.normalize();
                
                System.out.println("Vector :\t"+r+"\n");
                
                if (r.getX()>+.2f && r.getY()<-.9f) {
                    System.out.println(r);
                    System.out.println("recta 3");
                    estado = 6;
                    rival.getVehicle().steer(0);
                } else {
                    rival.girarCurva1();
                }
                break;
            case 6:
                if (rival.getVehicle().getPhysicsLocation().getZ()<=-54.f) {
                    estado = 7;
                    System.out.println("curva 3");
                }
                System.out.println(rival.getVehicle().getPhysicsLocation().getZ());
                rival.moureEndavant();
                break;
            case 7:
                r.setX(rival.getVehicle().getLinearVelocity().getX());
                r.setY(rival.getVehicle().getLinearVelocity().getZ());
                r = r.normalize();
                /*System.out.println("eeee");
                System.out.println(r);*/
                if (r.getX()>+.9f && r.getY()>+0.f) {
                    System.out.println("recta 4");
                    System.out.println(r);
                    estado = 8;
                    rival.getVehicle().steer(0);
                } else {
                    rival.girarCurva1();
                }
                break;
            case 8:
                if (rival.getVehicle().getPhysicsLocation().getX()>=0.f) {
                    estado = 9;
                    System.out.println("curva 4");
                }
                System.out.println(rival.getVehicle().getPhysicsLocation().getX());
                rival.moureEndavant();
                break;
            case 9:
                r.setX(rival.getVehicle().getLinearVelocity().getX());
                r.setY(rival.getVehicle().getLinearVelocity().getZ());
                r = r.normalize();
                /*System.out.println("eeee");
                System.out.println(r);*/
                if (r.getX()<-.2f && r.getY()>+.9f) {
                    System.out.println(r);
                    estado = 2;
                    rival.getVehicle().steer(0);
                } else {
                    rival.girarCurva1();
                }
                break;    
            default:
                
        }
        

        updateDisplay();
    }
}