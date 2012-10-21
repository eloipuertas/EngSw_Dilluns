package controlador;

import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioRenderer;
import com.jme3.input.InputManager;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import de.lessvoid.nifty.Nifty;
import vista.Menu;


public class MenuController {
    
    private Menu controller=null;
    
    public MenuController(AppStateManager stateManager,AssetManager assetManager,Node rootNode,ViewPort guiViewPort,InputManager inputManager, AudioRenderer audioRenderer){
        //Añadimos el menu creado con nifty
        controller = new Menu(assetManager,rootNode);
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
}
