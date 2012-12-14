package controlador;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
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


public class Main extends SimpleApplication implements ActionListener {

    private BulletAppState bulletAppState;   
    private VehicleProtagonista car;
    private Rival rival;
    private WorldCreator world;
    private CameraNode camNode; 
    private CameraNode camNodeR; //Node de la càmara per al rival
    private MenuController menu;
    private boolean initScene = false;
    private Display display;
    private boolean gamePaused = true; 
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

        menu = new MenuController(settings,stateManager,assetManager,rootNode,guiViewPort,inputManager,audioRenderer,this,false,1,0,5,2,1,10,true,true,0,0,0,0,this);   
        initAudio();


    }
    
    private PhysicsSpace getPhysicsSpace() {
        return bulletAppState.getPhysicsSpace();
    }
    private void setupKeys() {
        
        inputManager.addMapping("num1", new KeyTrigger(KeyInput.KEY_1));
        inputManager.addMapping("ResetRival", new KeyTrigger(KeyInput.KEY_R));
        inputManager.addListener(this, "num1");
        inputManager.addListener(this, "ResetRival");
    }
    public void pause(){        
        gamePaused = true;
        display.pauseChronograph();
        bulletAppState.setSpeed(0); //paro el coche           
        menu.gotoScreen("pause");      
    }
    
    public void unPause(){
        display.resumeChronograph();
        menu.gotoScreen("null");
        bulletAppState.setSpeed(1.0f); //vuelvo a dejar mover el coche        
        gamePaused = false;
    }
    
    public boolean isGamePaused(){
        return this.gamePaused;
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
      menu_music.stop();
      starting_car_sound.play();
      if (menu.getWeatherName().equals("Lluvioso")) {
          rain_sound.play();
      }
      must_destroy.play();
    }


    public void onAction(String binding, boolean value, float tpf) {
        if (binding.equals("Lefts")) {
            car.turnLeft(value);
        } else if (binding.equals("Rights")) {
            car.turnRight(value);
        } else if (binding.equals("Ups")) {
            car.forward(value);
        } else if (binding.equals("Downs")) {
            car.back(value);
        } else if (binding.equals("Reset")) {
            car.reset(value, initialPos, initialRot);
        }else if (binding.equals("Space")) {
            car.handBrake(value);
        }else if (binding.equals("ResetRival")) {
            rival.reset_rival();
            
        }else if (binding.equals("num1") && value) {//Control de la càmara del rival. Mentre es mantingui la tecla 1 apretada, la càmara seguirà al rival.
            camNode.getControl(CameraControl.class).setEnabled(false);
            camNodeR.getControl(CameraControl.class).setEnabled(true);
        }else{
            camNodeR.getControl(CameraControl.class).setEnabled(false);
            camNode.getControl(CameraControl.class).setEnabled(true);
        }
        
    }
    

    
    /*Metode per comprovar que el cotxe protagonista esta en moviment*/
    public boolean comprovaMoviment (){
        if (car.getVehicle().getLinearVelocity().length()>=1) {
            return true;
        } else {
            return false;
        }
    }
    @Override
    public void simpleUpdate(float tpf) {
        
        flyCam.setEnabled(false);
        
        if(menu.isMenuFinished() && !initScene){            
            addWorld();            
            addProtagonista();
            addRival();
            setupKeys();
            addDisplay();            
            audioGameStarted();
            initScene = true;
            gamePaused=false;
/*
            addDisplay();
            gameStarted = true;
            setupKeys();
*/
        }
        
        if(!gamePaused){
            System.out.println(car.getVehicle().getPhysicsLocation());
            camNode.lookAt(car.getSpatial().getWorldTranslation(), Vector3f.UNIT_Y);
            
            camNode.setLocalTranslation(car.getSpatial().localToWorld( new Vector3f( 0, 4, -15), null));
            /*Codi per a moure el rival, cal moure-ho d'aqui*/
            if(comprovaMoviment()==true || rival.comprovaPartidaComensada()) {      /*depen de la tecla up del prota*/
                rival.setPartidaComensada(true);
                rival.moureRival();
                camNodeR.lookAt(rival.getSpatial().getWorldTranslation(), Vector3f.UNIT_Y);
                camNodeR.setLocalTranslation(rival.getSpatial().localToWorld( new Vector3f( 0, 4, -15), null));
            }
            world.updateMusic();
            display.updateDisplay(car.getSpeed(),1);      
            display.updateMirror(car.getSpatial().localToWorld(new Vector3f(0,3,-15), null),car.getSpatial().localToWorld( new Vector3f( 0, 3, 0), null));
            display.updateMinimap(car.getSpatial().localToWorld(new Vector3f(0,0,0),null));
        }
        else{
            if (menu.readyToUnPause()){
                this.unPause();
                menu.unPauseDone();
            }
        }
    }
    
    // Añadir aqui los gets necesarios que cada uno necesite para su constructor
    // ej : menu.getCarColorRGBA()
    
    private void addWorld(){
        //Cargamos la escena
        world = new WorldCreator(rootNode, assetManager, bulletAppState, this.viewPort, menu);
        initialPos = world.getInitialPos();
        initialRot = world.getInitialRot();
    }
    
    private void addDisplay(){        
        float minDimension = Math.min(settings.getWidth(),settings.getHeight());
        display.addDisplay((int)(settings.getWidth()-(minDimension/2.5f)/2),(int)((minDimension/2.5f)/2),2.5f,(int)(settings.getWidth()-(minDimension/40f)-(minDimension/11.42f)-10),(int)(settings.getHeight()*0.975f),40,(int)(settings.getWidth()-(minDimension/9.23f)-10),(int)(settings.getHeight()*0.95f),9.23f,(int)(settings.getWidth()-(minDimension/11.42f)-10),(int)(settings.getHeight()*0.85f),11.42f);        
        display.addMirror(settings.getWidth()/2, (int)(settings.getHeight()*0.88f), 3f,renderManager,cam,car.getSpatial().localToWorld(new Vector3f(0,3,-15), null),rootNode);        
    }        
    
    private void addProtagonista(){
        /*DEBUG BOUNDING BOXES*///bulletAppState.getPhysicsSpace().enableDebug(assetManager);        
        car = new VehicleProtagonista(getAssetManager(), getPhysicsSpace(), cam);
        car.setCocheProtagonista(menu.getIdCar(), menu.getCarColorNameENG());
        
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
        Quaternion initialRotRival = world.getInitialRot();
        //System.out.println(menu.getIdCircuit());
        rival = new Rival(getAssetManager(), getPhysicsSpace(),menu.getIdCircuit(),initialRotRival,2); /*Creacio del rival, incolu el buildcar i el situar-lo correctament*/       
        rootNode.attachChild(rival.getSpatial());
         //Creem un nou node de la camara per a enfocar al rival
        camNodeR = new CameraNode("camNodeR", cam);
        camNodeR.getControl(CameraControl.class).setEnabled(false);
        camNodeR.setLocalTranslation(new Vector3f(0, 4, -15));
        camNodeR.lookAt(rival.getSpatial().getLocalTranslation(), Vector3f.UNIT_Y);
        
        rootNode.attachChild(camNodeR);
        
    }
    private void updatePosicio() {
    /*   if (car.getNumVoltes>rival.getNumVoltes()){
            car.setPosicioCarrera(1);
            rival.setPosicioCarrera(2);
        } else if (car.getNumVoltes<rival.getNumVoltes()){
            car.setPosicioCarrera(2);
            rival.setPosicioCarrera(1);
        } else {
            if (car.getEstatControlVolta()>rival.getEstatControlVolta()) {
                car.setPosicioCarrera(1);
                rival.setPosicioCarrera(2);
            } else if (car.getEstatControlVolta()<rival.getEstatControlVolta()){
                car.setPosicioCarrera(2);
                rival.setPosicioCarrera(1);
            } else {
                if (car.getDistanciaEstatControlVolta()>rival.getDistanciaEstatControlVolta()) {
                    car.setPosicioCarrera(1);
                    rival.setPosicioCarrera(2);
                } else if (car.getDistanciaEstatControlVolta()<rival.getDistanciaEstatControlVolta()){
                    car.setPosicioCarrera(2);
                    rival.setPosicioCarrera(1);
                } else {
                    car.setPosicioCarrera(1);
                    rival.setPosicioCarrera(1);
                }
            }
        }*/
    }
}
