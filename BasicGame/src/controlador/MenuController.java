package controlador;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioRenderer;
import com.jme3.input.InputManager;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import de.lessvoid.nifty.Nifty;
import vista.Menu;


public class MenuController {
    
    private Menu controller=null;
    
    public MenuController(AppSettings settings,AppStateManager stateManager,AssetManager assetManager,Node rootNode,ViewPort guiViewPort,InputManager inputManager, AudioRenderer audioRenderer,SimpleApplication main,boolean debugInfo,int initNumEnemies,int minNumEnemies, int maxNumEnemies,int initNumLaps, int minNumLaps, int maxNumLaps,int initNumVolume, int minNumVolume, int maxNumVolume){
        //Añadimos el menu creado con nifty
        controller = new Menu(settings,assetManager,rootNode,main,debugInfo,initNumEnemies,minNumEnemies,maxNumEnemies,initNumLaps,minNumLaps,maxNumLaps,initNumVolume,minNumVolume,maxNumVolume);
        stateManager.attach(controller);
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
        Nifty nifty = niftyDisplay.getNifty();
        guiViewPort.addProcessor(niftyDisplay);
        nifty.fromXml("Interface/Menu/menu.xml", "start", controller);       
    }
    
    public boolean isGameStarted(){        
        if(controller == null){
            return false;
        } 
        else {
            return controller.isGameStarted();
        }        
    }
    
    public String getMode(){
        return controller.getMode();
    }
}
