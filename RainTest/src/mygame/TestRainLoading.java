package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.TextureKey;
import com.jme3.asset.plugins.HttpZipLocator;
import com.jme3.asset.plugins.ZipLocator;
import com.jme3.bounding.BoundingBox;
import com.jme3.font.BitmapText;
import com.jme3.input.ChaseCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import com.jme3.shadow.PssmShadowRenderer;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.util.SkyFactory;
 
 
import java.io.File;
import java.util.ArrayList;
import java.util.List;
 
import jme3tools.converters.ImageToAwt;
 
public class TestRainLoading extends SimpleApplication {
 
    private Sphere sphereMesh = new Sphere(32, 32, 10, false, true);
    private Geometry sphere = new Geometry("Sky", sphereMesh);
    private Rain rain;
    private float angle1;
    private Geometry ballGeom;
    private ChaseCamera chaser;
    private TerrainQuad terrain;
    private Material matRock;
    private Material matWire;
    private BitmapText hintText;
    protected boolean wireframe;
    protected boolean rainTarget;
    private Vector3f lightDir = new Vector3f(.1f, -1, .1f).normalizeLocal();
 
    public static void main(String[] args) {
 
        TestRainLoading app = new TestRainLoading();
        app.start();
    }
 
    @Override
    public void simpleUpdate(float tpf){
        super.simpleUpdate(tpf);
        sphere.setLocalTranslation(cam.getLocation());
        angle1 += tpf * 1.25f;
        angle1 %= FastMath.TWO_PI;
 
        ballGeom.setLocalTranslation(new Vector3f(FastMath.cos(angle1) * 240f, 20.5f, FastMath.sin(angle1) * 240f));
 
    }
 
    public void simpleInitApp() {
        loadHintText();
        setupKeys();
        setupBasicShadow();
        this.flyCam.setMoveSpeed(10);
        flyCam.setEnabled(false);
        cam.setLocation(new Vector3f(0,1000,0));
        //buildTerrain();
        // load sky        
        Node sky = new Node();
        sky.attachChild(SkyFactory.createSky(assetManager, "Textures/BrightSky.dds", false));
        rootNode.attachChild(sky);
 
 
 
        setupLighting();
 
        rain =new Rain(assetManager,cam,1);
           rootNode.attachChild(rain);
            Sphere ball = new Sphere(32, 32, 2f);
            ballGeom = new Geometry("Ball Name", ball);
            Material mat3 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat3.setColor("Color", new ColorRGBA(0, 0, 1, 0.6f));
            mat3.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
            ballGeom.setMaterial(mat3);
            ballGeom.setQueueBucket(Bucket.Transparent);
            rootNode.attachChild(ballGeom);
            chaser = new ChaseCamera(cam,ballGeom,inputManager);
            chaser.setMaxDistance(1200);
            chaser.setSmoothMotion(true);
            rain.setTarget(ballGeom);
    }
    public void loadHintText() {
        hintText = new BitmapText(guiFont, false);
        hintText.setSize(guiFont.getCharSet().getRenderedSize());
        hintText.setLocalTranslation(0, getCamera().getHeight(), 0);
        hintText.setText("Hit R to switch rain targetrnHit T to switch to wireframe");
        guiNode.attachChild(hintText);
    }
    public void setupLighting() {
        // boolean hdr = hdrRender.isEnabled();
 
        // flourescent main light
        PointLight pl = new PointLight();
        pl.setColor(new ColorRGBA(1f, 1f, 1f, 1f));
        pl.setRadius(320);
        // pl.setPosition(new Vector3f(0f,400,0f));
        rootNode.addLight(pl);
 
        DirectionalLight dl = new DirectionalLight();
        // sunset light
 
        dl.setDirection(new Vector3f(-0.1f, -0.7f, .5f));
        dl.setColor(new ColorRGBA(0.44f, 0.40f, 0.40f, .25f));
        rootNode.addLight(dl);
 
        // skylight
        DirectionalLight dl1 = new DirectionalLight();
        dl1.setDirection(new Vector3f(-0.6f, -1, -0.6f).normalizeLocal());
        dl1.setColor(new ColorRGBA(0.20f, 0.22f, 0.24f, .25f));
        rootNode.addLight(dl1);
 
        // white ambient light
        DirectionalLight dl2 = new DirectionalLight();
        dl2.setDirection(new Vector3f(0, -1, -0));
        dl2.setColor(new ColorRGBA(.9f, .9f, .9f, 1f));
        rootNode.addLight(dl2);
    }
    public void setupBasicShadow() {
 
     /*PssmShadowRenderer pssmRenderer = new PssmShadowRenderer(assetManager, 1024,4,PssmShadowRenderer.EDGE_FILTERING_PCF);
     pssmRenderer.setDirection(lightDir);
     pssmRenderer.setLambda(0.3f);
     pssmRenderer.setShadowIntensity(0.6f);
     pssmRenderer.setCropShadows(false);
     pssmRenderer.setPcfFilter(PssmShadowRenderer.FILTERING.PCF16X16);
     pssmRenderer.setEdgesThickness(5);
     viewPort.addProcessor(pssmRenderer);*/
    }
    private void setupKeys() {
        flyCam.setMoveSpeed(50);
        inputManager.addMapping("rain", new KeyTrigger(KeyInput.KEY_R));
        inputManager.addListener(actionListener, "rain");
        inputManager.addMapping("wireframe", new KeyTrigger(KeyInput.KEY_T));
        inputManager.addListener(actionListener, "wireframe");
    }
    private ActionListener actionListener = new ActionListener() {
 
        public void onAction(String name, boolean pressed, float tpf) {
            if (name.equals("wireframe") && !pressed) {
                wireframe = !wireframe;
                if (!wireframe) {
                        terrain.setMaterial(matWire);
                } else {
                    terrain.setMaterial(matRock);
                }
 
            }
            if (name.equals("rain") && !pressed) {
                rainTarget = !rainTarget;
                if (!rainTarget) {
                    rain.setTarget(ballGeom);
                    chaser.setEnabled(true);
                    flyCam.setEnabled(false);
                } else {
                    rain.setTarget(null);
                    chaser.setEnabled(false);
                    flyCam.setEnabled(true);
                }
 
            }
        }
    };
    private void buildTerrain(){
        // First, we load up our textures and the heightmap texture for the terrain
 
        // TERRAIN TEXTURE material
        matRock = new Material(assetManager, "Common/MatDefs/Terrain/Terrain.j3md");
 
        // ALPHA map (for splat textures)
        matRock.setTexture("Alpha", assetManager.loadTexture("Textures/raindrop.jpg"));
 
        // HEIGHTMAP image (for the terrain heightmap)
        Texture heightMapImage = assetManager.loadTexture("Textures/raindrop.jpg");
 
        // GRASS texture
        Texture grass = assetManager.loadTexture("Textures/raindrop.jpg");
        grass.setWrap(WrapMode.Repeat);
        matRock.setTexture("Tex1", grass);
        matRock.setFloat("Tex1Scale", 64f);
 
        // DIRT texture
        Texture dirt = assetManager.loadTexture("Textures/raindrop.jpg");
        dirt.setWrap(WrapMode.Repeat);
        matRock.setTexture("Tex2", dirt);
        matRock.setFloat("Tex2Scale", 32f);
 
        // ROCK texture
        Texture rock = assetManager.loadTexture("Textures/raindrop.jpg");
        rock.setWrap(WrapMode.Repeat);
        matRock.setTexture("Tex3", rock);
        matRock.setFloat("Tex3Scale", 128f);
 
        // WIREFRAME material
        matWire = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matWire.setColor("Color", ColorRGBA.Green);
 
 
        // CREATE HEIGHTMAP
       /* AbstractHeightMap heightmap = null;
        try {
            //heightmap = new HillHeightMap(1025, 1000, 50, 100, (byte) 3);
 
            heightmap = new ImageBasedHeightMap(heightMapImage.getImage(), 1f);
            heightmap.load();
 
        } catch (Exception e) {
            e.printStackTrace();
        }
 */
        /*
         * Here we create the actual terrain. The tiles will be 65x65, and the total size of the
         * terrain will be 513x513. It uses the heightmap we created to generate the height values.
         */
        /**
         * Optimal terrain patch size is 65 (64x64)
         * If you go for small patch size, it will definitely slow down because the depth of
         * the quad tree will increase, and more is done on the CPU then to traverse it.
         * I plan to give each node in the tree a reference to its neighbours so that should
         * resolve any of these slowdowns. -Brent
         *
         * The total size is up to you. At 1025 it ran fine for me (200+FPS), however at
         * size=2049, it got really slow. But that is a jump from 2 million to 8 million triangles...
         */
       /* terrain = new TerrainQuad("terrain", 65, 513, heightmap.getHeightMap());
        List cameras = new ArrayList();
        cameras.add(getCamera());
        TerrainLodControl control = new TerrainLodControl(terrain, cameras);
        terrain.addControl(control);
        terrain.setMaterial(matRock);
        terrain.setModelBound(new BoundingBox());
        terrain.updateModelBound();
        terrain.setLocalTranslation(0, -100, 0);
        terrain.setLocalScale(2f, 1f, 2f);
        rootNode.attachChild(terrain);*/
    }
}