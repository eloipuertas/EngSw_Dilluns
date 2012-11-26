package controlador;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.CameraNode;
import com.jme3.scene.control.CameraControl;
import java.util.ArrayList;
import model.Audio;
import model.ComandosCoche;
import model.Rival;
import model.VehicleProtagonista;
import model.WorldCreator;
import vista.Display;


public class Main extends SimpleApplication{

    private BulletAppState bulletAppState;   
    private VehicleProtagonista car;
    private Rival rival;
    private WorldCreator world;
    private CameraNode camNode;    
    private MenuController menu;
    private Display display;
    private boolean gameStarted = false; 
    private RigidBodyControl landscape;
    private Vector3f initialPos;
    private Quaternion initialRot;
    private ComandosCoche comandos;
    
    private Audio menu_music;
    private Audio starting_car_sound;
    private Audio rain_sound;
    private Audio must_destroy;
        
    
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

    @Override
    public void simpleInitApp() {
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);

        /*if (settings.getRenderer().startsWith("LWJGL")) {
            BasicShadowRenderer bsr = new BasicShadowRenderer(assetManager, 512);
            bsr.setDirection(new Vector3f(-0.5f, -0.3f, -0.3f).normalizeLocal());
            viewPort.addProcessor(bsr);
        }
        cam.setFrustumFar(150f);
         * 
         */             
        
        display = new Display(assetManager,settings,guiNode,this.timer);        
        menu = new MenuController(settings,stateManager,assetManager,rootNode,guiViewPort,inputManager,audioRenderer,this,false,1,0,5,2,1,10,1,0,1,0,0,0,0);   
        //initAudio();
    }
    
    private PhysicsSpace getPhysicsSpace() {
        return bulletAppState.getPhysicsSpace();
    }

    public void initAudio() {
      menu_music = new Audio(rootNode, assetManager, "song_menu.wav", true);
      menu_music.play();
      
      starting_car_sound = new Audio(rootNode, assetManager, "starting_car.wav");
      
      rain_sound = new Audio(rootNode, assetManager, "rain_sound.wav", true);
      
      must_destroy = new Audio(rootNode, assetManager, "must_destroy.ogg", true);
      must_destroy.setVolume(0.4f);
    }
    
    public void audioGameStarted() {
     // menu_music.stop();
      //starting_car_sound.play();
      if (menu.getWeatherName().equals("Lluvioso")) {
          //rain_sound.play();
      }
      //must_destroy.play();
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
        
        if(menu.isMenuFinished() && !gameStarted){            
            addWorld();            
            addProtagonista();
            addRival();
            addDisplay();
            gameStarted = true;
            audioGameStarted();
        }
        
        if(gameStarted){
            
            flyCam.setEnabled(false);
            
            camNode.lookAt(car.getSpatial().getWorldTranslation(), Vector3f.UNIT_Y);
            
            camNode.setLocalTranslation(car.getSpatial().localToWorld( new Vector3f( 0, 4, -15), null));
            //System.out.println(car.getVehicle().getPhysicsLocation().getX());
            /*Codi per a moure el rival, cal moure-ho d'aqui*/
            switch (estado) {
                case 1:
                    if(comprovaMoviment()==true) {
                        estado = 2;
                    }
                    break;
                case 2:

                    if (rival.getVehicle().getPhysicsLocation().getZ()>=30) {
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

                    } else {
                        rival.girarCurva1();
                    }
                    break;
                case 4:
                    if (rival.getVehicle().getPhysicsLocation().getX()<=-40.f) {
                         estado = 5;
                    }                
                    rival.moureEndavant();
                    break;
                case 5:
                    r.setX(rival.getVehicle().getLinearVelocity().getX());
                    r.setY(rival.getVehicle().getLinearVelocity().getZ());
                    r = r.normalize();


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
                    rival.moureEndavant();
                    break;
                case 7:
                    r.setX(rival.getVehicle().getLinearVelocity().getX());
                    r.setY(rival.getVehicle().getLinearVelocity().getZ());
                    r = r.normalize();
                    /*System.out.println("eeee");
                    System.out.println(r);*/
                    if (r.getX()>+.9f && r.getY()>+0.f) {
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
                    rival.moureEndavant();
                    break;
                case 9:
                    r.setX(rival.getVehicle().getLinearVelocity().getX());
                    r.setY(rival.getVehicle().getLinearVelocity().getZ());
                    r = r.normalize();
                    /*System.out.println("eeee");
                    System.out.println(r);*/
                    if (r.getX()<-.2f && r.getY()>+.9f) {

                        estado = 2;
                        rival.getVehicle().steer(0);
                    } else {
                        rival.girarCurva1();
                    }
                    break;    
                default:
            }
            
            display.updateDisplay(car.getSpeed(),1);      
            display.updateMirror(car.getSpatial().localToWorld(new Vector3f(0,3,-15), null),car.getSpatial().localToWorld( new Vector3f( 0, 3, 0), null));
        }

    }
    
    // Añadir aqui los gets necesarios que cada uno necesite para su constructor
    // ej : menu.getCarColorRGBA()
    
    private void addWorld(){
        //Cargamos la escena
        world = new WorldCreator(rootNode, assetManager, bulletAppState, this.viewPort);
        initialPos = world.getInitialPos();
        initialRot = world.getInitialRot();
    }
    
    private void addDisplay(){        
        float minDimension = Math.min(settings.getWidth(),settings.getHeight());
        display.addDisplay((int)(settings.getWidth()-(minDimension/2.5f)/2),(int)((minDimension/2.5f)/2),2.5f,(int)(settings.getWidth()-(minDimension/40f)-(minDimension/11.42f)-10),(int)(settings.getHeight()*0.975f),40,(int)(settings.getWidth()-(minDimension/9.23f)-10),(int)(settings.getHeight()*0.95f),9.23f,(int)(settings.getWidth()-(minDimension/11.42f)-10),(int)(settings.getHeight()*0.85f),11.42f);        
        display.addMirror(settings.getWidth()/2, (int)(settings.getHeight()*0.88f), 3f,renderManager,cam,car.getSpatial().localToWorld(new Vector3f(0,3,-15), null),rootNode);        
    }        
    
    private void addProtagonista(){
        /*DEBUG BOUNDING BOXES*/bulletAppState.getPhysicsSpace().enableDebug(assetManager);        
        car = new VehicleProtagonista(getAssetManager(), getPhysicsSpace(), cam);
        car.setCocheProtagonista(1, "Red");
        car.getVehicle().setPhysicsLocation(initialPos);
        car.getVehicle().setPhysicsRotation(initialRot);
        //Guardamos la posicion inicial y la rotacion del coche
        car.setInitialPos(initialPos);
        car.setInitialRot(initialRot);
        
        //Ponemos los controles para el protagonista para que se mueva
        addControlesToProtagonist();
        
        //Añadimos el coche protagonista
        rootNode.attachChild(car.getSpatial());
        
        //Settejem la camera
        camNode = new CameraNode("CamNode", cam);
        camNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
        camNode.setLocalTranslation(new Vector3f(0, 4, -15));
        //camNode.setLocalTranslation(new Vector3f(-15, 15, -15));
        //camNode.setLocalTranslation(new Vector3f(-15, 15, -15));
        camNode.lookAt(car.getSpatial().getLocalTranslation(), Vector3f.UNIT_Y);
        
        rootNode.attachChild(camNode);
    }
    
    /*
     * Metodo para poner controles al protagonista
     */
    private void addControlesToProtagonist(){
        this.comandos = new ComandosCoche(car);
        ArrayList<Integer> controles = new ArrayList<Integer>();
        controles.add(KeyInput.KEY_LEFT);
        controles.add(KeyInput.KEY_RIGHT);
        controles.add(KeyInput.KEY_UP);
        controles.add(KeyInput.KEY_DOWN);
        controles.add(KeyInput.KEY_SPACE);
        controles.add(KeyInput.KEY_RETURN);
        setControlesToProgatonist(controles);
    }
    
    /*
     * Metodo para cambiar los controles del protagonista
     */
    private void setControlesToProgatonist(ArrayList<Integer> controles){
        int left, right, up, down, space, returN;
        left = controles.get(0);
        right = controles.get(1);
        up = controles.get(2);
        down = controles.get(3);
        space = controles.get(4);
        returN = controles.get(5);
        comandos.setupKeys(left, right, up, down, space, returN, inputManager);
    }
    
    private void addRival(){
        //Aqui creem la classe rival i la afegim al rootNode
        rival = new Rival(getAssetManager(), getPhysicsSpace());
        rival.buildCar();
        rival.getVehicle().setPhysicsLocation(new Vector3f(5.f,-4.f,0.f));
        //Añadimos Rival
        rootNode.attachChild(rival.getSpatial());
    }
}
