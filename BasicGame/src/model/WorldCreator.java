/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.effect.shapes.EmitterBoxShape;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.SpotLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FogFilter;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.shadow.BasicShadowRenderer;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import controlador.MenuController;
import java.util.ArrayList;

/**
 *
 * @author normenhansen
 */
public class WorldCreator {
    private static ParticleEmitter rain;
    private static ParticleEmitter snow;
    
    private static Box brick;
    static float bLength = 0.6f;
    static float bWidth = 0.40f;
    static float bHeight = 0.2f;
    
    private Node rootNode;
    private Node mundo;
    private AssetManager assetManager;
    private BulletAppState space;
    private ViewPort viewPort;
    private ArrayList<Geometry> obstacleList;
    private MenuController menu;
    
    private Material mat_road;
    private Material mat_brick;
    private Material mat_box;
    private Material mat_snow;
    private Material mat_rain;
    private Material mat_bounds;
    private Spatial roadModel;
    private ListaMapas listaMapas;
    private Mapa mapaActual;
    private Audio rain_sound;
    private LlistaReproduccio game_music;

    /**
     * creates a simple physics test world with a floor, an obstacle and some test boxes
     * @param rootNode
     * @param assetManager
     * @param space
     */
    
    public WorldCreator(Node rootNode, AssetManager assetManager, BulletAppState space, ViewPort viewPort, MenuController menu) {
        this.rootNode = rootNode;
        mundo = new Node(); 
        this.assetManager = assetManager;
        this.space = space;
        this.viewPort = viewPort;
        this.menu = menu;
        obstacleList = new ArrayList<Geometry>();
        listaMapas = new ListaMapas();
        initMapas();
        initMaterial();
        createWorld();
        initAudio();
        
    }
    
    private void initAudio() {
        String musicNames[] = new String[3];
        musicNames[0] = "must_destroy.ogg";
        musicNames[1] = "3_point_1.ogg";
        musicNames[2] = "hot_ride.ogg";
        game_music = new LlistaReproduccio(true, rootNode, assetManager, musicNames);
        float volumes[] = new float[3];
        volumes[0] = 0.3f;
        volumes[1] = 0.5f;
        volumes[2] = 0.4f;
        game_music.setVolumes(volumes);
        game_music.playNext();
    }
    
    private void initAudioPluja() {
        rain_sound = new Audio(rootNode, assetManager, "rain_sound.wav", true);
        rain_sound.play();
    }
    
    public void updateMusic() {
        if (game_music.isStopped()) {
            game_music.playNext();
        }
    }
    
    private void createWorld() {
        //Afegim la iluminacio
        if(menu.getDayStateName() == "Noche"){
            //Mode nit
            AmbientLight ambient = new AmbientLight();
            ambient.setColor(ColorRGBA.DarkGray);
            mundo.addLight(ambient);
            
            //streetlamps creation
            //mostrarLlums();
        }else{
            //Mode dia
            DirectionalLight sun = new DirectionalLight();
            Vector3f lightDir=new Vector3f(-0.37352666f, -0.50444174f, -0.7784704f);
            sun.setDirection(lightDir);
            sun.setColor(ColorRGBA.LightGray.clone().multLocal(2));
            mundo.addLight(sun);

            /*AmbientLight ambient = new AmbientLight();
            ambient.setColor(ColorRGBA.LightGray);
            rootNode.addLight(ambient);*/
        }
        
        brick = new Box(Vector3f.ZERO, bLength, bHeight, bWidth);
        brick.scaleTextureCoordinates(new Vector2f(1f, .5f));
        
        //Afegim ombres
        BasicShadowRenderer bsr = new BasicShadowRenderer(assetManager, 256);
        bsr.setDirection(new Vector3f(-0.37352666f, -0.50444174f, -0.7784704f)); // light direction
        viewPort.addProcessor(bsr); 
        
        //Afegim el cel
        
        Node sky = new Node();
        if(menu.getDayStateName() == "Noche"){
            //Mode nit
            sky.attachChild(SkyFactory.createSky(assetManager, assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_west.jpg"), assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_east.jpg"), assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_north.jpg"), assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_south.jpg"), assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_up.jpg"), assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_down.jpg")));
        }else{
            //Mode dia
            sky.attachChild(SkyFactory.createSky(assetManager, "Textures/Sky/Bright/BrightSky.dds", false));
        }
        mundo.attachChild(sky);
        
        //Mirem quin circuit ha estat seleccionat en el menu
        mapaActual = listaMapas.getMapa(menu.getIdCircuit());
        
        //Road creation
        // We load the scene
        Spatial sceneModel = assetManager.loadModel(mapaActual.getSceneModel());
        sceneModel.setLocalTranslation(0, -5, 0);
        sceneModel.scale(mapaActual.getEscala());
        //sceneModel.setMaterial(mat_road);
        
        // We set up collision detection for the scene by creating a
        // compound collision shape and a static RigidBodyControl with mass zero.
        CollisionShape sceneShape =
                CollisionShapeFactory.createMeshShape((Node) sceneModel);
        RigidBodyControl landscape = new RigidBodyControl(sceneShape, 0);
        sceneModel.addControl(landscape);
        sceneModel.setShadowMode(ShadowMode.CastAndReceive);

        // We attach the scene  and its limits to the rootNode and the physics space,
        // to make them appear in the game world.
        mundo.attachChild(sceneModel);
        space.getPhysicsSpace().add(sceneModel);
        
        //We load the limits of the scene
        Spatial boundsModel = assetManager.loadModel(mapaActual.getParets());
        boundsModel.setLocalTranslation(0, -5, 0);
        boundsModel.scale(mapaActual.getEscala());
        boundsModel.setMaterial(mat_bounds);

        // We set up collision detection for the walls.
        CollisionShape boundsShape =
                CollisionShapeFactory.createMeshShape((Node) boundsModel);
        RigidBodyControl limits = new RigidBodyControl(boundsShape, 0);
        boundsModel.addControl(limits);
        boundsModel.setQueueBucket(RenderQueue.Bucket.Transparent);

        mundo.attachChild(boundsModel);
        space.getPhysicsSpace().add(boundsModel);
        
        
        //wall creation
        mostrarMurs();

        //Obstacle creation
        mostrarCaixes();
        
        if(menu.getIdCircuit() == 0 || menu.getIdCircuit()==2) {
            mostarCono();
        }
        
        if(menu.getDayStateName() == "Noche"){
            mostrarLlums();
        }

        //Creem el efecte de clima que s'hagi seleccionat al menu
        initClima(menu.getWeatherName());
        rootNode.attachChild(mundo);
    }
    
    
    private void initClima(String weatherName) {
        if(weatherName == "Despejado"){
            
        }else if(weatherName == "Lluvioso"){
            initPluja();
        }else if(weatherName == "Nevado"){
            initNeu();
        }else if(weatherName == "Nebuloso"){
            initBoira();
        }else{
            System.out.println("El clima " + weatherName + " es desconegut!");
        }
    }
    
    private void initNeu() {
        snow = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 1000);
        snow.setMaterial(mat_snow);
        snow.setImagesX(2); snow.setImagesY(2); // 2x2 texture animation
        switch(menu.getIdCircuit()){
            case 0:
                snow.setShape(new EmitterBoxShape(new Vector3f(-100f,10f,-100f),new Vector3f(100f,10f,100f)));
                break;
            case 1:
                snow.setShape(new EmitterBoxShape(new Vector3f(-300f,10f,-300f),new Vector3f(300f,10f,300f)));
                break;
            case 2:
                snow.setShape(new EmitterBoxShape(new Vector3f(-200f,10f,-200f),new Vector3f(200f,10f,200f)));
                break;
        }
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
        mundo.attachChild(snow);
    }
    
    private void initPluja() {
        initAudioPluja();
        rain = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 100000);
        rain.setMaterial(mat_rain);
        //rain.setParticlesPerSec(50);
        rain.setImagesX(2); rain.setImagesY(2); // 2x2 texture animation
        switch(menu.getIdCircuit()){
            case 0:
                rain.setShape(new EmitterBoxShape(new Vector3f(-100f,10f,-100f),new Vector3f(100f,10f,100f)));
                break;
            case 1:
                rain.setShape(new EmitterBoxShape(new Vector3f(-300f,10f,-300f),new Vector3f(300f,10f,300f)));
                break;
            case 2:
                rain.setShape(new EmitterBoxShape(new Vector3f(-200f,10f,-200f),new Vector3f(200f,10f,200f)));
                break;
        }
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
        mundo.attachChild(rain);
    }
    
    private void initBoira() {
        FilterPostProcessor fpp=new FilterPostProcessor(assetManager);
        FogFilter fog=new FogFilter();
        fog.setFogColor(new ColorRGBA(0.9f, 0.9f, 0.9f, 1.0f));
        fog.setFogDistance(100);
        fog.setFogDensity(2.0f);
        fpp.addFilter(fog);
        viewPort.addProcessor(fpp);
        
        /*FilterPostProcessor fpp=new FilterPostProcessor(assetManager);
        BloomFilter bloom=new BloomFilter();
        //bloom.setBlurScale(0.5f);
        bloom.setBloomIntensity(1.25f);
        fpp.addFilter(bloom);
        viewPort.addProcessor(fpp);*/
    }
    
    private void crearMur(int x, int y, int z) {
        float startpt = bLength / 4;
        float height = 0;
        for (int j = 0; j < 10; j++) {
            for (int i = 0; i < 6; i++) {
                Vector3f vt = new Vector3f(i * bLength * 2 + startpt+x, bHeight + height+y, z);
                addBrick(vt);
            }
            startpt = -startpt;
            height += 2 * bHeight;
        }
    }
    
    private void crearCaixa(int x, int y, int z) {
        Box obstacleBox = new Box(1,1,1);
        Geometry obstacleModel = new Geometry("Obstacle", obstacleBox);
        obstacleModel.setLocalTranslation(x, y, z);
        obstacleModel.setMaterial(mat_box);
        obstacleModel.addControl(new RigidBodyControl(5));
        obstacleModel.setShadowMode(ShadowMode.CastAndReceive);
        mundo.attachChild(obstacleModel);
        space.getPhysicsSpace().add(obstacleModel);
        obstacleList.add(obstacleModel);
    }
    
    private void crearCaixaFracturada(float x, float y, float z) {
        Spatial caja_parte1 = assetManager.loadModel("Models/caixa_fracturada/superior.j3o");
        caja_parte1.setLocalTranslation(x,y,z);
        caja_parte1.addControl(new RigidBodyControl(5));
        caja_parte1.getControl(RigidBodyControl.class).setFriction(2f);
        mundo.attachChild(caja_parte1);
        space.getPhysicsSpace().add(caja_parte1);
        Spatial caja_parte2 = assetManager.loadModel("Models/caixa_fracturada/medio.j3o");
        caja_parte2.setLocalTranslation(x,y,z);
        caja_parte2.addControl(new RigidBodyControl(5));
        caja_parte2.getControl(RigidBodyControl.class).setFriction(2f);
        mundo.attachChild(caja_parte2);
        space.getPhysicsSpace().add(caja_parte2);
        Spatial caja_parte3 = assetManager.loadModel("Models/caixa_fracturada/inferior.j3o");
        caja_parte3.setLocalTranslation(x,y,z);
        caja_parte3.addControl(new RigidBodyControl(5));
        caja_parte3.getControl(RigidBodyControl.class).setFriction(2f);
        mundo.attachChild(caja_parte3);
        space.getPhysicsSpace().add(caja_parte3);
    }
    
    private void crearCono(float x, float y, float z) {
        Spatial cono = assetManager.loadModel("Models/cone_joined/cone_joined.j3o");
        cono.setLocalTranslation(x,y,z);
        cono.addControl(new RigidBodyControl(3000));
        cono.getControl(RigidBodyControl.class).setFriction(2f);
        mundo.attachChild(cono);
        space.getPhysicsSpace().add(cono);
    }
    
    private void initMapas() {
        ArrayList<Vector3f> luces = new ArrayList<Vector3f>();
        ArrayList<Vector3f> cajas = new ArrayList<Vector3f>();
        ArrayList<Vector3f> muros = new ArrayList<Vector3f>();
        ArrayList<Vector3f> medidas = new ArrayList<Vector3f>();
        cajas.add(new Vector3f(-33, (float)-5.5, 69));
        cajas.add(new Vector3f(-40, (float)-5.5, 61));
        cajas.add(new Vector3f(-35, (float)-5.5, 49));
        cajas.add(new Vector3f(-40, (float)-5.5, 38));
        cajas.add(new Vector3f(-39, (float)-5.5, 31));
        cajas.add(new Vector3f(-43, (float)-5.5, 23));
        cajas.add(new Vector3f(-38, (float)-5.5, -28));
        luces.add(new Vector3f((float)11.057709,(float) 3.6817183,(float) 70.557945));
        luces.add(new Vector3f((float)-24.116646, (float)3.365127, (float)70.52438));
        luces.add(new Vector3f((float)-23.936317, (float)3.7200565, (float)49.05581));
        luces.add(new Vector3f((float)-25.270641, (float)3.7790525, (float)26.0042));
        luces.add(new Vector3f((float)-46.638527, (float)3.7798347, (float)14.29488));
        luces.add(new Vector3f((float)-27.151728, (float)3.6546752, (float)3.2493808));
        luces.add(new Vector3f((float)-7.360218, (float)3.7280383, (float)2.8712435));
        luces.add(new Vector3f((float)-7.360218, (float)3.7280383, (float)2.8712435));
        luces.add(new Vector3f((float)11.829636, (float)3.719032, (float)2.9689732));
        luces.add(new Vector3f((float)10.426862, (float)3.3665524, (float)25.892347));
        luces.add(new Vector3f((float)10.071195, (float)3.7138824, (float)47.38325));
        luces.add(new Vector3f((float)30.038078, (float)3.3638551, (float)-8.739162));
        luces.add(new Vector3f((float)30.82807, (float)3.7592447, (float)-30.193073));
        luces.add(new Vector3f((float)30.162527, (float)3.7674172, (float)-53.28356));
        luces.add(new Vector3f((float)29.575834, (float)3.75111, (float)-76.80468));
        luces.add(new Vector3f((float)-47.46072, (float)3.7272272, (float)-75.62149));
        luces.add(new Vector3f((float)-47.46072, (float)3.7272272, (float)-75.62149));
        luces.add(new Vector3f((float)-47.78743, (float)3.3517253, (float)-53.92456));
        luces.add(new Vector3f((float)-48.309227,(float) 3.7637587, (float)-32.44741));
        medidas.add(new Vector3f(36, -5, -116));
        medidas.add(new Vector3f(36, -5, 103));
        medidas.add(new Vector3f(-62, -5, 103));
        medidas.add(new Vector3f(-62, -5, -116));
        Mapa m = new Mapa(new Vector3f(-10,-6,80),new Quaternion().fromAngles(0, (float)Math.toRadians(-90), 0),"Models/World1/World1.j3o","Models/World1/InvisibleWall/InvisibleWall1.j3o",luces,cajas,muros,medidas,2f);
        listaMapas.añadirMapa(m);
        luces = new ArrayList<Vector3f>();
        cajas = new ArrayList<Vector3f>();
        muros = new ArrayList<Vector3f>();
        medidas = new ArrayList<Vector3f>();
        cajas.add(new Vector3f(-18, -6, 138));
        cajas.add(new Vector3f(-53, -6, 116));
        cajas.add(new Vector3f(-72, -6, 102));
        cajas.add(new Vector3f(-69, -6, 72));
        cajas.add(new Vector3f(-56, -6, 67));
        cajas.add(new Vector3f(-52, -6, 28));
        cajas.add(new Vector3f(-68, -6, 25));
        cajas.add(new Vector3f(-75, -6, -24));
        cajas.add(new Vector3f(-54, -6, -39));
        cajas.add(new Vector3f(-54, -6, -39));
        cajas.add(new Vector3f(-54, -6, -39));
        muros.add(new Vector3f(86, -6, 135));
        luces.add(new Vector3f((float)20.77127, (float)7.9598684, (float)-5.7752357));
        luces.add(new Vector3f((float)20.672327, (float)8.051395, (float)-37.470642));
        luces.add(new Vector3f((float)21.844162, (float)8.54894, (float)-52.538074));
        luces.add(new Vector3f((float)44.51233, (float)8.111047, (float)-115.19701));
        luces.add(new Vector3f((float)45.2996, (float)8.091122, (float)-80.16784));
        luces.add(new Vector3f((float)46.49883, (float)8.101867, (float)-45.55256));
        luces.add(new Vector3f((float)45.153137, (float)7.5676866, (float)-13.354753));
        luces.add(new Vector3f((float)41.73249, (float)7.508127, (float)21.711227));
        luces.add(new Vector3f((float)41.957577, (float)7.5864325, (float)51.709457));
        luces.add(new Vector3f((float)42.617905, (float)7.606007, (float)94.684235));
        luces.add(new Vector3f((float)42.749813, (float)7.5224075, (float)121.49106));
        luces.add(new Vector3f((float)-40.74521, (float)8.480071, (float)-51.351215));
        luces.add(new Vector3f((float)-4.843434, (float)8.437189, (float)-51.560776));
        luces.add(new Vector3f((float)-4.9933906, (float)7.8712263, (float)-4.7589498));
        luces.add(new Vector3f((float)-40.743893, (float)7.9526534, (float)-5.7472534));
        luces.add(new Vector3f((float)76.69355, (float)2.3765824, (float)-84.52637));
        luces.add(new Vector3f((float)124.79213, (float)3.1658466, (float)59.367165));
        medidas.add(new Vector3f(138, -7, 183));
        medidas.add(new Vector3f(-107, -7, 183));
        medidas.add(new Vector3f(-107, -5, -176));
        medidas.add(new Vector3f(138, -5, -176));
        m = new Mapa(new Vector3f(11, -7, 139),new Quaternion().fromAngles(0, (float)Math.toRadians(-90), 0),"Models/World2/World2.j3o","Models/World2/InvisibleWall/InvisibleWall.j3o",luces,cajas,muros,medidas,3f);
        listaMapas.añadirMapa(m);
        luces = new ArrayList<Vector3f>();
        cajas = new ArrayList<Vector3f>();
        muros = new ArrayList<Vector3f>();
        medidas = new ArrayList<Vector3f>();
        cajas.add(new Vector3f(-33, (float)-3.5, 69));
        cajas.add(new Vector3f(-40, (float)-3.5, 61));
        cajas.add(new Vector3f(-35, (float)-3.5, 49));
        cajas.add(new Vector3f(-40, (float)-3.5, 38));
        cajas.add(new Vector3f(-39, (float)-3.5, 31));
        cajas.add(new Vector3f(-43, (float)-3.5, 23));
        cajas.add(new Vector3f(-38, (float)-3.5, -28));
        cajas.add(new Vector3f(147, (float)-3.5, -28));
        muros.add(new Vector3f(148, (float)-3.5, 3));
        cajas.add(new Vector3f(135, (float)-3.5, 80));
        cajas.add(new Vector3f(130, (float)-3.5, 79));
        luces.add(new Vector3f((float)10.994606, (float)4.4260845, (float)70.593994));
        luces.add(new Vector3f((float)-24.038723, (float)4.4453, (float)70.53151));
        medidas.add(new Vector3f(36, -5, -116));
        medidas.add(new Vector3f(36, -5, 103));
        medidas.add(new Vector3f(-62, -5, 103));
        medidas.add(new Vector3f(-62, -5, -116));
        m = new Mapa(new Vector3f(-10,-4,80),new Quaternion().fromAngles(0, (float)Math.toRadians(-90), 0),"Models/World3/World3.j3o","Models/World3/InvisibleWall/InvisibleWall3.j3o",luces,cajas,muros,medidas,2f);
        listaMapas.añadirMapa(m);
    }
    
    private void afegirLlum(float x, float y, float z){
        SpotLight spot = new SpotLight();
        spot.setSpotRange(100f);                           // distance
        spot.setSpotInnerAngle(15f * FastMath.DEG_TO_RAD); // inner light cone (central beam)
        spot.setSpotOuterAngle(35f * FastMath.DEG_TO_RAD); // outer light cone (edge of the light)
        spot.setColor(ColorRGBA.White.mult(5f));         // light color
        spot.setPosition(new Vector3f(x,y,z));               // shine from camera loc
        spot.setDirection(new Vector3f(0,-1,0));             // shine forward from camera loc
        mundo.addLight(spot);   
    }

    
    public void mostrarCaixes() {
        ArrayList<Vector3f> cajas = mapaActual.getListaCajas();
        for(int i = 0; i < cajas.size();i++) {
            Vector3f v = cajas.get(i);
            //crearCaixa((int)v.x,(int)v.y,(int)v.z);
            System.out.println("POSICIONES "+(int)v.x+ " "+v.y+" " + (int)v.z);
            crearCaixaFracturada(v.x,v.y,v.z);
        }
    }
    
    public void mostarCono() {
        
        if(menu.getIdCircuit() == 0) {
            crearCono((float)-28.723972, (float)-6.0622463, (float)18.410522);
            crearCono((float)-28.372177, (float)-6.0387764, (float)13.971759);
            crearCono((float)-28.034878, (float)-6.0236855, (float)11.280115);
            crearCono((float)-28.43544, (float)-6.006256, (float)7.009533); 
            crearCono((float)14.614142, (float)-5.7505264, (float)16.295588);
            crearCono((float)14.610315, (float)-5.738961, (float)13.846985);
            crearCono((float)14.657426, (float)-5.7277317, (float)11.543973);
            crearCono((float)14.029064, (float)-5.7092752, (float)6.7220063);
        }
        if(menu.getIdCircuit() == 2) {
            crearCono((float)13.465805, (float)-4.340721, (float)18.19282);
            crearCono((float)13.465248, (float)-4.3407173, (float)14.394462);
            crearCono((float)13.460812, (float)-4.3407135, (float)9.763995);
            crearCono((float)13.713562, (float)-4.3407135, (float)5.9770856);
            crearCono((float)-27.897057, (float)-4.3407173, (float)5.5633893);
            crearCono((float)-28.199787, (float)-4.3407173, (float)8.728756);
            crearCono((float)-28.092594, (float)-4.3407154, (float)13.223505);
            crearCono((float)-27.631493, (float)-4.340719, (float)17.281307);
            crearCono((float)28.767302, (float)-4.3407536, (float)71.20535);
            crearCono((float)24.597708, (float)-4.3407516, (float)71.60649);
            crearCono((float)21.288177, (float)-4.3407516, (float)71.67496);
            crearCono((float)16.876785, (float)-4.3407497, (float)72.198204);
            crearCono((float)29.017439, (float)-4.3406706, (float)-65.76872);
            crearCono((float)24.469894, (float)-4.3406715, (float)-65.52343);
            crearCono((float)20.667267, (float)-4.3406715, (float)-65.538124);
            crearCono((float)17.250578, (float)-4.3406715, (float)-66.23939);
        }
    }
    
    public void mostrarMurs() {
        ArrayList<Vector3f> murs = mapaActual.getListaMuros();
        for(int i = 0; i < murs.size();i++) {
            Vector3f v = murs.get(i);
            crearMur((int)v.x,(int)v.y,(int)v.z);
        }
    }
    
    public void mostrarLlums() {
        ArrayList<Vector3f> llums = mapaActual.getListaLuces();
        for(int i = 0; i < llums.size();i++) {
            Vector3f v = llums.get(i);
            afegirLlum((float)v.x,(float)v.y,(float)v.z);
        }
    }

    private void initMaterial() {
        
        mat_road = new Material( 
            assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_road.setTexture("ColorMap", 
            assetManager.loadTexture("Textures/RoadTexture.jpg"));
        
        mat_bounds = new Material(
                assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_bounds.setTexture("ColorMap",
                assetManager.loadTexture("Textures/transparentTexture.png"));
        mat_bounds.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        mat_bounds.setTransparent(true);

        mat_brick = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        TextureKey key2 = new TextureKey("Textures/ladrillo2.jpg");
        key2.setGenerateMips(true);
        Texture tex2 = assetManager.loadTexture(key2);
        mat_brick.setTexture("ColorMap", tex2);
        
        mat_box = new Material( 
            assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat_box.setTexture("DiffuseMap", 
            assetManager.loadTexture("Textures/BoxTexture.jpg"));
        
        mat_snow = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        mat_snow.setTexture("Texture", assetManager.loadTexture("Textures/snow.png"));
        
        mat_rain = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        mat_rain.setTexture("Texture", assetManager.loadTexture("Textures/teardrop.png"));

    }

    private void addBrick(Vector3f ori) {

        Geometry reBoxg = new Geometry("brick", brick);
        reBoxg.setMaterial(mat_brick);
        reBoxg.setLocalTranslation(ori);
        //for geometry with sphere mesh the physics system automatically uses a sphere collision shape
        reBoxg.addControl(new RigidBodyControl(1.5f));
        reBoxg.setShadowMode(ShadowMode.CastAndReceive);
        reBoxg.getControl(RigidBodyControl.class).setFriction(0.6f);
        mundo.attachChild(reBoxg);
        space.getPhysicsSpace().add(reBoxg);
        obstacleList.add(reBoxg);
    }
    
    public ArrayList<Geometry> getObstacles(){
        return obstacleList;
    }
    
    public Spatial getCarretera(){
        return roadModel;
    }

    public Vector3f getInitialPos(){
        return mapaActual.getOrigen();
    }
    
    public Quaternion getInitialRot(){
        return mapaActual.getRotacionInicial();
    }

    public Node getNodoMundo() {
        return mundo;
    }
}