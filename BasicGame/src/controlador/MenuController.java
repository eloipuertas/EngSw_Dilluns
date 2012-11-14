package controlador;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioRenderer;
import com.jme3.input.InputManager;
import com.jme3.math.ColorRGBA;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import de.lessvoid.nifty.Nifty;
import vista.Menu;


public class MenuController {
    
    private Menu controller=null;
    
    public MenuController(AppSettings settings,AppStateManager stateManager,AssetManager assetManager,Node rootNode,ViewPort guiViewPort,InputManager inputManager, AudioRenderer audioRenderer,SimpleApplication main,boolean debugInfo,int initNumEnemies,int minNumEnemies, int maxNumEnemies,int initNumLaps, int minNumLaps, int maxNumLaps,int initNumVolume, int minNumVolume, int maxNumVolume,int initCar,int initCarColor,int initClima,int initCircuit){
        //Añadimos el menu creado con nifty
        controller = new Menu(settings,assetManager,rootNode,main,debugInfo,initNumEnemies,minNumEnemies,maxNumEnemies,initNumLaps,minNumLaps,maxNumLaps,initNumVolume,minNumVolume,maxNumVolume,initCar,initCarColor,initClima,initCircuit);
        stateManager.attach(controller);
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
        Nifty nifty = niftyDisplay.getNifty();
        guiViewPort.addProcessor(niftyDisplay);
        nifty.fromXml("Interface/Menu/menu.xml", "start", controller);       
    }
    
    public boolean isMenuFinished(){        
        if(controller == null){
            return false;
        } 
        else {
            return controller.isMenuFinished();
        }        
    }
    
    public String getMode(){
        return controller.getMode();
    }
    
    public String getCarColorName(){
        return controller.getCarColorName();
    }
    
    public ColorRGBA getCarColorRGBA(){
        return controller.getCarColorRGBA();
    }
    
    public String getCarName(){
        return controller.getCarName();
    }
    
    public int getNumLaps(){
        return controller.getNumLaps();
    }
    
    public int getNumEnemies(){
        return controller.getInitNumEnemies();
    }
    
    public String getWeatherName(){
        return controller.getWeatherName();
    }
}
