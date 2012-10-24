package mygame;

import java.util.Properties;
import java.util.logging.Logger;
 
import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingSphere;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.effect.ParticleMesh.Type;
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
 
 
 
 
/**
 * a point particle effect for rain
 * @author galun
 * @version $Id: Rain.java,v 1.4 2006/11/11 22:23:29 galun Exp $
 */
public class Rain extends Node {
 
    private static final long serialVersionUID = -1057124936652689175L;
    private static Logger log = Logger.getLogger(Rain.class.getCanonicalName());
    private ParticleEmitter points;
    //private SimpleParticleInfluenceFactory.BasicGravity gravity;
    private boolean useGravity = false;
    private Node _rootNode;
    private AssetManager assetManager;
    private Camera cam;
    private int height=400;
    private Spatial target;
 
    public Rain(AssetManager assetManager,Camera cam, int weather) {
        super("rain");
        this.assetManager=assetManager;
        this.cam=cam;
        applyParameters(weather);
        attachChild(points);
        Sphere ball = new Sphere(32, 32, 20f);
        Geometry ballGeom = new Geometry("Ball Name", ball);
        Material mat3 = new Material(assetManager, "Common/MatDefs/Misc/SolidColor.j3md");
        mat3.setColor("m_Color", new ColorRGBA(1, 1, 0, 0.2f));
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
        points.setImagesX(1);
        points.setImagesY(1);
      //  points.setGravity(1159.9f*weather);
        points.setGravity(1,1,1);
       // points.setLowLife(1626.0f);
        points.setLowLife(2);
        points.setHighLife(5);
        points.setStartSize(1.8f);
        points.setEndSize(1.6f);
        points.setStartColor(new ColorRGBA(0.0f, 0.0f, 1.0f, 0.8f));
        points.setEndColor(new ColorRGBA(0.8f, 0.8f, 1.0f, 0.6f));
        //points.setRandomAngle(randomAngle)Mod(0.0f);
        points.setFacingVelocity(false);
        //points.setFaceNormal(new Vector3f(0,0,1));
        points.setParticlesPerSec(80000*weather);
        //points.setVelocityVariation(20.0f);
        //points.setInitialVelocity(0.58f);
        points.setRotateSpeed(0.0f);
        points.setShadowMode(ShadowMode.CastAndReceive);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        mat.setTexture("m_Texture", assetManager.loadTexture("textures/raindrop.png"));
        points.setMaterial(mat);
        points.setQueueBucket(Bucket.Transparent);
        points.updateLogicalState(0);
        points.updateGeometricState();
    }
 
    private ColorRGBA parseColor(String s) {
        ColorRGBA color = new ColorRGBA(ColorRGBA.White);
        try {
            float r = 1;
            float g = 1;
            float b = 1;
            float a = 1;
            String[] p = s.split(",s+");
            r = Float.parseFloat(p[0]);
            if (p.length > 1)
                g = Float.parseFloat(p[1]);
            if (p.length > 2)
                b = Float.parseFloat(p[2]);
            if (p.length > 3)
                a = Float.parseFloat(p[3]);
            color.set(r, g, b, a);
        } catch (Exception ex) {
            log.warning("unparsable color: " + s + " (" + ex.toString() + ")");
        }
        return color;
    }
    public void updateLogicalState(float tpf){
        //if(points.getMesh()==null)return;
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
        //intersection.x=cam.getLocation().x+cam.getDirection().x*50f;
        //intersection.z=cam.getLocation().z+cam.getDirection().z*50f;
        this.setLocalTranslation(intersection);
        System.out.println("pos rain="+this.getLocalTranslation());
        /*System.out.println("pos rain="+points.getParticles()[0].position);
        float x=(int) (Math.random()*far)-far/2;
        float z=(int) (Math.random()*far)-far/2;
        if(points.getParticles()[0].position.y==height){
            points.setLocalTranslation(new Vector3f(x, height, z));
        }
        else if(points.getLocalTranslation().y<0)
            points.killAllParticles();*/
 
    }
 
    public Spatial getTarget() {
        return target;
    }
 
    public void setTarget(Spatial target) {
        this.target = target;
    }
 
}