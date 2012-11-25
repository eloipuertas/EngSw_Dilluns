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

package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.light.SpotLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.shape.Box;

public class Main extends SimpleApplication {

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }
    
    Node shootables;
    Geometry mark;
    private BulletAppState bulletAppState;


    @Override
    public void simpleInitApp() {
        flyCam.setEnabled(true);
        cam.setFrustumFar(150f);
        flyCam.setMoveSpeed(20);
        
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        
        /*DirectionalLight sun = new DirectionalLight();
        Vector3f lightDir=new Vector3f(-0.37352666f, -0.50444174f, -0.7784704f);
        sun.setDirection(lightDir);
        sun.setColor(ColorRGBA.White.clone().multLocal(2));
        rootNode.addLight(sun);*/
        
        inputManager.addMapping("shoot", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(actionListener, "shoot");
        
        addScene("Models/StraightRoad/Ciutat/StraightRoad.j3o");
        initCrossHairs();
        
        addLight(12.589932f, 6.6392827f, 10.735586f);
        addLight(-12.614846f, 8.3450775f, 11.513504f);
        addLight(-37.408188f, 9.942276f, 12.544489f);
        
    }

    @Override
    public void simpleUpdate(float tpf){
        
    }

    public void addScene(String path){
        Spatial sceneModel = assetManager.loadModel(path);
        sceneModel.setLocalTranslation(0, -5, 0);
        sceneModel.scale(20,20,20);
        CollisionShape sceneShape =
                CollisionShapeFactory.createMeshShape((Node) sceneModel);
        RigidBodyControl landscape = new RigidBodyControl(sceneShape, 0);
        sceneModel.addControl(landscape);
        sceneModel.setShadowMode(RenderQueue.ShadowMode.Receive);
        rootNode.attachChild(sceneModel);
        bulletAppState.getPhysicsSpace().add(sceneModel);
    }
    
    protected void initCrossHairs() {
        //guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText ch = new BitmapText(guiFont, false);
        ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
        ch.setText("+"); // crosshairs
        ch.setLocalTranslation( // center
                settings.getWidth() / 2 - guiFont.getCharSet().getRenderedSize() / 3 * 2,
                settings.getHeight() / 2 + ch.getLineHeight() / 2, 0);
        guiNode.attachChild(ch);
    }
    
    public void getCoordinates() {
        Vector3f origin    = cam.getWorldCoordinates(new Vector2f(settings.getWidth() / 2, settings.getHeight() / 2), 0.0f);
        Vector3f direction = cam.getWorldCoordinates(new Vector2f(settings.getWidth() / 2, settings.getHeight() / 2), 0.3f);
        direction.subtractLocal(origin).normalizeLocal();

        Ray ray = new Ray(origin, direction);
        CollisionResults results = new CollisionResults();
        rootNode.collideWith(ray, results);
        System.out.println("----- Collisions? " + results.size() + "-----");
        if (results.size() > 0) {
            CollisionResult closest = results.getClosestCollision();
            // For each hit, we know distance, impact point, name of geometry.
            float dist = closest.getDistance();
            Vector3f pt = closest.getContactPoint();
            String hit = closest.getGeometry().getName();
            System.out.println("  You shot " + hit + " at " + pt + ", " + dist + " wu away.");
        }
    }
    
    private void addLight(float x, float y, float z){
        SpotLight spot = new SpotLight();
        spot.setSpotRange(100f);                           // distance
        spot.setSpotInnerAngle(15f * FastMath.DEG_TO_RAD); // inner light cone (central beam)
        spot.setSpotOuterAngle(35f * FastMath.DEG_TO_RAD); // outer light cone (edge of the light)
        spot.setColor(ColorRGBA.White.mult(1.3f));         // light color
        spot.setPosition(new Vector3f(x,y,z));               // shine from camera loc
        spot.setDirection(new Vector3f(0,-1,0));             // shine forward from camera loc
        rootNode.addLight(spot);
        
    }
        
    private ActionListener actionListener = new ActionListener() {

        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("shoot") && !keyPressed) {
                getCoordinates();
            }
        }
    };
}