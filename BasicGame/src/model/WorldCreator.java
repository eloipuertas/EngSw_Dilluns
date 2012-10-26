/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import com.bulletphysics.linearmath.Transform;
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
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.effect.shapes.EmitterBoxShape;
import com.jme3.input.ChaseCamera;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Plane;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.HDRRenderer;
import com.jme3.post.filters.BloomFilter;
import com.jme3.post.filters.FogFilter;
import com.jme3.post.filters.LightScatteringFilter;
import com.jme3.renderer.Camera;
import com.jme3.renderer.Caps;
import com.jme3.renderer.Renderer;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.WireFrustum;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Sphere.TextureMode;
import com.jme3.shadow.BasicShadowRenderer;
import com.jme3.shadow.PssmShadowRenderer;
import com.jme3.shadow.PssmShadowRenderer.CompareMode;
import com.jme3.shadow.PssmShadowRenderer.FilterMode;
import com.jme3.shadow.ShadowUtil;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;

/**
 *
 * @author normenhansen
 */
public class WorldCreator {
    private static ParticleEmitter rain;
    private static ParticleEmitter snow;

    /**
     * creates a simple physics test world with a floor, an obstacle and some test boxes
     * @param rootNode
     * @param assetManager
     * @param space
     */
    
    public static void createWorld(Node rootNode, AssetManager assetManager, BulletAppState space, ViewPort viewPort) {        
        //Afegim la llum
        DirectionalLight sun = new DirectionalLight();
        Vector3f lightDir=new Vector3f(-0.37352666f, -0.50444174f, -0.7784704f);
        sun.setDirection(lightDir);
        sun.setColor(ColorRGBA.White.clone().multLocal(2));
        //AmbientLight sun = new AmbientLight();
        //sun.setColor(ColorRGBA.LightGray);
        rootNode.addLight(sun);
        
        //Afegim boira
        /*FilterPostProcessor fpp=new FilterPostProcessor(assetManager);
        FogFilter fog=new FogFilter();
        fog.setFogColor(new ColorRGBA(0.9f, 0.9f, 0.9f, 1.0f));
        fog.setFogDistance(100);
        fog.setFogDensity(2.0f);
        fpp.addFilter(fog);
        viewPort.addProcessor(fpp);*/
        
        /*FilterPostProcessor fpp=new FilterPostProcessor(assetManager);
        BloomFilter bloom=new BloomFilter();
        //bloom.setBlurScale(0.5f);
        bloom.setBloomIntensity(1.25f);
        fpp.addFilter(bloom);
        viewPort.addProcessor(fpp);*/
        
        //Afegim ombres
        BasicShadowRenderer bsr = new BasicShadowRenderer(assetManager, 256);
        bsr.setDirection(new Vector3f(-0.37352666f, -0.50444174f, -0.7784704f)); // light direction
        viewPort.addProcessor(bsr);
        
        //Afegim el cel
        Node sky = new Node();
        sky.attachChild(SkyFactory.createSky(assetManager, "Textures/Sky/Bright/BrightSky.dds", false));
        rootNode.attachChild(sky);
        
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
        sceneModel.setShadowMode(ShadowMode.Receive);

        // We attach the scene to the rootNode and the physics space,
        // to make them appear in the game world.
        rootNode.attachChild(sceneModel);
        
        
        space.getPhysicsSpace().add(landscape);

        //Obstacle creation
        Box obstacleBox = new Box(2,2,2);
        Geometry obstacleModel = new Geometry("Obstacle", obstacleBox);
        obstacleModel.setLocalTranslation(2, -2, -10);
        Material mat_obs = new Material( 
            assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat_obs.setTexture("DiffuseMap", 
            assetManager.loadTexture("Textures/BoxTexture.jpg"));
        obstacleModel.setMaterial(mat_obs);
        obstacleModel.addControl(new RigidBodyControl(2));
        obstacleModel.setShadowMode(ShadowMode.CastAndReceive);
        rootNode.attachChild(obstacleModel);
        space.getPhysicsSpace().add(obstacleModel);
        
        //Obstacle 2
        obstacleBox = new Box(2,2,2);
        obstacleModel = new Geometry("Obstacle", obstacleBox);
        obstacleModel.setLocalTranslation(2, -2, -100);
        obstacleModel.setMaterial(mat_obs);
        obstacleModel.addControl(new RigidBodyControl(2));
        obstacleModel.setShadowMode(ShadowMode.CastAndReceive);
        rootNode.attachChild(obstacleModel);
        space.getPhysicsSpace().add(obstacleModel);
        
        //Obstacle 3
        obstacleBox = new Box(2,2,2);
        obstacleModel = new Geometry("Obstacle", obstacleBox);
        obstacleModel.setLocalTranslation(-50, -2, -100);
        obstacleModel.setMaterial(mat_obs);
        obstacleModel.addControl(new RigidBodyControl(2));
        obstacleModel.setShadowMode(ShadowMode.CastAndReceive);
        rootNode.attachChild(obstacleModel);
        space.getPhysicsSpace().add(obstacleModel);
        
        //Obstacle 4
        obstacleBox = new Box(2,2,2);
        obstacleModel = new Geometry("Obstacle", obstacleBox);
        obstacleModel.setLocalTranslation(-100, -2, -100);
        obstacleModel.setMaterial(mat_obs);
        obstacleModel.addControl(new RigidBodyControl(2));
        obstacleModel.setShadowMode(ShadowMode.CastAndReceive);
        rootNode.attachChild(obstacleModel);
        space.getPhysicsSpace().add(obstacleModel);
        
        //Obstacle 5
        obstacleBox = new Box(2,2,2);
        obstacleModel = new Geometry("Obstacle", obstacleBox);
        obstacleModel.setLocalTranslation(-100, -2, -50);
        obstacleModel.setMaterial(mat_obs);
        obstacleModel.addControl(new RigidBodyControl(2));
        obstacleModel.setShadowMode(ShadowMode.CastAndReceive);
        rootNode.attachChild(obstacleModel);
        space.getPhysicsSpace().add(obstacleModel);
        
        //Obstacle 6
        obstacleBox = new Box(2,2,2);
        obstacleModel = new Geometry("Obstacle", obstacleBox);
        obstacleModel.setLocalTranslation(-100, -2, 0);
        obstacleModel.setMaterial(mat_obs);
        obstacleModel.addControl(new RigidBodyControl(2));
        obstacleModel.setShadowMode(ShadowMode.CastAndReceive);
        rootNode.attachChild(obstacleModel);
        space.getPhysicsSpace().add(obstacleModel);
        
        //Obstacle 7
        obstacleBox = new Box(2,2,2);
        obstacleModel = new Geometry("Obstacle", obstacleBox);
        obstacleModel.setLocalTranslation(-100, -2, 100);
        obstacleModel.setMaterial(mat_obs);
        obstacleModel.addControl(new RigidBodyControl(2));
        obstacleModel.setShadowMode(ShadowMode.CastAndReceive);
        rootNode.attachChild(obstacleModel);
        space.getPhysicsSpace().add(obstacleModel);
        
        //Obstacle 8
        obstacleBox = new Box(2,2,2);
        obstacleModel = new Geometry("Obstacle", obstacleBox);
        obstacleModel.setLocalTranslation(-50, -2, 100);
        obstacleModel.setMaterial(mat_obs);
        obstacleModel.addControl(new RigidBodyControl(2));
        obstacleModel.setShadowMode(ShadowMode.CastAndReceive);
        rootNode.attachChild(obstacleModel);
        space.getPhysicsSpace().add(obstacleModel);
        
        //Obstacle 9
        obstacleBox = new Box(2,2,2);
        obstacleModel = new Geometry("Obstacle", obstacleBox);
        obstacleModel.setLocalTranslation(0, -2, 100);
        obstacleModel.setMaterial(mat_obs);
        obstacleModel.addControl(new RigidBodyControl(2));
        obstacleModel.setShadowMode(ShadowMode.CastAndReceive);
        rootNode.attachChild(obstacleModel);
        space.getPhysicsSpace().add(obstacleModel);

        //Creem el efecte de neu
       /* snow = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 1000);
        Material mat_snow = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        mat_snow.setTexture("Texture", assetManager.loadTexture("Textures/snow.png"));
        snow.setMaterial(mat_snow);
        snow.setImagesX(2); snow.setImagesY(2); // 2x2 texture animation
        snow.setShape(new EmitterBoxShape(new Vector3f(-100f,10f,-100f),new Vector3f(100f,10f,100f)));
        snow.setStartColor(ColorRGBA.White);
        snow.getParticleInfluencer().setInitialVelocity(new Vector3f(0,-2,0));
        snow.setStartSize(10.11f);
        snow.setEndSize(10.11f);
        snow.setGravity(0,2,0);
        snow.setLowLife(6.5f);
        snow.setHighLife(6.5f);
        snow.getParticleInfluencer().setVelocityVariation(0.3f);
        snow.setLocalTranslation(0f, 0f, 0f);
        snow.setParticlesPerSec(200);
        rootNode.attachChild(snow);*/
        
        //Creem el efecte de pluja
        rain = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 100000);
        Material mat_rain = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        mat_rain.setTexture("Texture", assetManager.loadTexture("Textures/teardrop.png"));
        rain.setMaterial(mat_rain);
        //rain.setParticlesPerSec(50);
        rain.setImagesX(2); rain.setImagesY(2); // 2x2 texture animation
        rain.setShape(new EmitterBoxShape(new Vector3f(-100f,10f,-100f),new Vector3f(100f,10f,100f)));
        rain.setStartColor(new ColorRGBA(192f,192f,192f,0.2f));
        //rain.setStartColor(new ColorRGBA(0f,0f,255f,1f));
        rain.getParticleInfluencer().setInitialVelocity(new Vector3f(0,-25,0));
        rain.getParticleInfluencer().setVelocityVariation(0f);
        rain.setStartSize(0.50f);
        rain.setEndSize(0.50f);
        rain.setGravity(0,1,0);
        rain.setLowLife(6.5f);
        rain.setHighLife(6.5f);
        rain.setParticlesPerSec(2000);
        rootNode.attachChild(rain);
    }

    public static void updateWorld(Vector3f localToWorld) {
//        snow.setLocalTranslation(localToWorld);
    }
}