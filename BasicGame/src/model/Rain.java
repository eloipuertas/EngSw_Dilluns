package model;
 
 
 
import java.util.Properties;
import java.util.logging.Logger;
 
import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingSphere;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.effect.ParticleMesh.Type;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Plane;
import com.jme3.math.Ray;
import com.jme3.math.Ring;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
 
 
 
 
 
public class Rain extends Node {
 
    private static final long serialVersionUID = -1057124936652689175L;
    private static Logger log = Logger.getLogger(Rain.class.getCanonicalName());
    private ParticleEmitter points;
    private boolean useGravity = false;
    private Node _rootNode;
    private AssetManager assetManager;
    private Camera cam;
    private int height=440;
    private Spatial target;
 
    public Rain(AssetManager assetManager,Camera cam, int weather) {
        super("rain");
        this.assetManager=assetManager;
        this.cam=cam;
        applyParameters(weather);
 
 
        Sphere ball = new Sphere(32, 32, 20f);
        Geometry ballGeom = new Geometry("Ball Name", ball);
        Material mat3 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat3.setColor("Color", new ColorRGBA(1, 1, 0, 0.2f));
        mat3.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        ballGeom.setMaterial(mat3);
        ballGeom.setQueueBucket(Bucket.Transparent);
        this.attachChild(ballGeom);
    }
 
    public void applyParameters(int weather) {
        points = new ParticleEmitter("rainPoints", Type.Triangle, 800*weather);
        points.setShape(new EmitterSphereShape(Vector3f.ZERO, 600f));
       // points.setLocalTranslation(new Vector3f(0f, height, 0f));
        points.setInitialVelocity(new Vector3f(0.0f, -1.0f, 0.0f));
        //points.setMaximumAngle(3.1415927f);
        //points.setMinimumAngle(0.0f);
        //points.setImagesX(1);
        //points.setImagesY(1);
  //      points.setGravity(1159.9f*weather);
        points.setGravity(1,1,1);
       // points.setLowLife(1626.0f);
        points.setLowLife(2);
        points.setHighLife(5);
        points.setStartSize(1.8f);
        points.setEndSize(1.6f);
        points.setStartColor(new ColorRGBA(0.8f, 0.8f, 1.0f, 0.8f));
        points.setEndColor(new ColorRGBA(0.8f, 0.8f, 1.0f, 0.6f));
        //points.setRandomAngle(randomAngle)Mod(0.0f);
        points.setFacingVelocity(false);
        //points.setFaceNormal(new Vector3f(0,0,1));
        points.setParticlesPerSec(80000*weather);
        points.setVelocityVariation(20.0f);
        //points.setInitialVelocity(0.58f);
        points.setRotateSpeed(0.0f);
        points.setShadowMode(ShadowMode.CastAndReceive);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        mat.setTexture("Texture", assetManager.loadTexture("Textures/raindrop.jpg"));
        points.setMaterial(mat);
        points.setQueueBucket(Bucket.Transparent);
        points.updateLogicalState(0);
        points.updateGeometricState();
    }
 
 
    public void updateLogicalState(float tpf){
         
        super.updateLogicalState(tpf);
        float far=800f;
        Vector3f intersection=Vector3f.ZERO;
        if(target==null){
            Vector3f loc=new Vector3f(cam.getLocation());
            Plane piano=new Plane(loc,far);
            Ray ray=new Ray(loc,cam.getDirection());
            intersection=new Vector3f(cam.getLocation());
            ray.intersectsWherePlane(piano, intersection);
             
        }
        else{
            intersection=new Vector3f(target.getLocalTranslation());
        }
        intersection.y=height;
        this.setLocalTranslation(intersection);
         
 
    }
 
    public Spatial getTarget() {
        return target;
    }
 
    public void setTarget(Spatial target) {
        this.target = target;
    }
 
}
