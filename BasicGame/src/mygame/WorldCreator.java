/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.Application;
import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.collision.shapes.PlaneCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Plane;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Sphere.TextureMode;
import com.jme3.texture.Texture;

/**
 *
 * @author normenhansen
 */
public class WorldCreator {

    /**
     * creates a simple physics test world with a floor, an obstacle and some test boxes
     * @param rootNode
     * @param assetManager
     * @param space
     */
    
    public static void createWorld(Node rootNode, AssetManager assetManager, BulletAppState space) {
        AmbientLight light = new AmbientLight();
        light.setColor(ColorRGBA.LightGray);
        rootNode.addLight(light);
           
        //Road creation
        // We load the scene
        Spatial sceneModel = assetManager.loadModel("Models/AngularRoad.j3o");
        sceneModel.setLocalTranslation(0, -5, 0);
        sceneModel.scale(20,0.25f,20);
        
        Material mat = new Material( 
            assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", 
            assetManager.loadTexture("Textures/RoadTexture.jpg"));
        sceneModel.setMaterial(mat);
        
        // We set up collision detection for the scene by creating a
        // compound collision shape and a static RigidBodyControl with mass zero.
        CollisionShape sceneShape =
                CollisionShapeFactory.createMeshShape((Node) sceneModel);
        RigidBodyControl landscape = new RigidBodyControl(sceneShape, 0);
        sceneModel.addControl(landscape);

        // We attach the scene to the rootNode and the physics space,
        // to make them appear in the game world.
        rootNode.attachChild(sceneModel);
        
        
        space.getPhysicsSpace().add(landscape);

        //Obstacle creation
        Box obstacleBox = new Box(2,2,2);
        Geometry obstacleModel = new Geometry("Obstacle", obstacleBox);
        obstacleModel.setLocalTranslation(2, -2, -10);
        Material mat_obs = new Material( 
            assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_obs.setTexture("ColorMap", 
            assetManager.loadTexture("Textures/BoxTexture.jpg"));
        obstacleModel.setMaterial(mat_obs);
        obstacleModel.addControl(new RigidBodyControl(2));
        rootNode.attachChild(obstacleModel);
        space.getPhysicsSpace().add(obstacleModel);
        
        //Obstacle 2
        obstacleBox = new Box(2,2,2);
        obstacleModel = new Geometry("Obstacle", obstacleBox);
        obstacleModel.setLocalTranslation(2, -2, -100);
        obstacleModel.setMaterial(mat_obs);
        obstacleModel.addControl(new RigidBodyControl(2));
        rootNode.attachChild(obstacleModel);
        space.getPhysicsSpace().add(obstacleModel);
        
        //Obstacle 3
        obstacleBox = new Box(2,2,2);
        obstacleModel = new Geometry("Obstacle", obstacleBox);
        obstacleModel.setLocalTranslation(-50, -2, -100);
        obstacleModel.setMaterial(mat_obs);
        obstacleModel.addControl(new RigidBodyControl(2));
        rootNode.attachChild(obstacleModel);
        space.getPhysicsSpace().add(obstacleModel);
        
        //Obstacle 4
        obstacleBox = new Box(2,2,2);
        obstacleModel = new Geometry("Obstacle", obstacleBox);
        obstacleModel.setLocalTranslation(-100, -2, -100);
        obstacleModel.setMaterial(mat_obs);
        obstacleModel.addControl(new RigidBodyControl(2));
        rootNode.attachChild(obstacleModel);
        space.getPhysicsSpace().add(obstacleModel);
        
        //Obstacle 5
        obstacleBox = new Box(2,2,2);
        obstacleModel = new Geometry("Obstacle", obstacleBox);
        obstacleModel.setLocalTranslation(-100, -2, -50);
        obstacleModel.setMaterial(mat_obs);
        obstacleModel.addControl(new RigidBodyControl(2));
        rootNode.attachChild(obstacleModel);
        space.getPhysicsSpace().add(obstacleModel);
        
        //Obstacle 6
        obstacleBox = new Box(2,2,2);
        obstacleModel = new Geometry("Obstacle", obstacleBox);
        obstacleModel.setLocalTranslation(-100, -2, 0);
        obstacleModel.setMaterial(mat_obs);
        obstacleModel.addControl(new RigidBodyControl(2));
        rootNode.attachChild(obstacleModel);
        space.getPhysicsSpace().add(obstacleModel);
        
        //Obstacle 7
        obstacleBox = new Box(2,2,2);
        obstacleModel = new Geometry("Obstacle", obstacleBox);
        obstacleModel.setLocalTranslation(-100, -2, 100);
        obstacleModel.setMaterial(mat_obs);
        obstacleModel.addControl(new RigidBodyControl(2));
        rootNode.attachChild(obstacleModel);
        space.getPhysicsSpace().add(obstacleModel);
        
        //Obstacle 8
        obstacleBox = new Box(2,2,2);
        obstacleModel = new Geometry("Obstacle", obstacleBox);
        obstacleModel.setLocalTranslation(-50, -2, 100);
        obstacleModel.setMaterial(mat_obs);
        obstacleModel.addControl(new RigidBodyControl(2));
        rootNode.attachChild(obstacleModel);
        space.getPhysicsSpace().add(obstacleModel);
        
        //Obstacle 9
        obstacleBox = new Box(2,2,2);
        obstacleModel = new Geometry("Obstacle", obstacleBox);
        obstacleModel.setLocalTranslation(0, -2, 100);
        obstacleModel.setMaterial(mat_obs);
        obstacleModel.addControl(new RigidBodyControl(2));
        rootNode.attachChild(obstacleModel);
        space.getPhysicsSpace().add(obstacleModel);
    }
}