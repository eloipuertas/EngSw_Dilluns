
package controlador;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioRenderer;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import de.lessvoid.nifty.Nifty;
import vista.Menu;



public class MenuController implements ActionListener {
    
    private Menu controller=null;
    private Main main;
    private InputManager inputManager;
    
    public MenuController(AppSettings settings,AppStateManager stateManager,AssetManager assetManager,Node rootNode,ViewPort guiViewPort,InputManager inputManager, AudioRenderer audioRenderer,SimpleApplication simpleApp,boolean debugInfo,int initNumEnemies,int minNumEnemies, int maxNumEnemies,int initNumLaps, int minNumLaps, int maxNumLaps,boolean music,boolean effects,int initCar,int initCarColor,int initClima,int initCircuit,Main main){
        //Añadimos el menu creado con nifty
        this.main=main;
        this.inputManager=inputManager;        
        
        //creamos el menu
        controller = new Menu(settings,assetManager,rootNode,simpleApp,debugInfo,initNumEnemies,minNumEnemies,maxNumEnemies,initNumLaps,minNumLaps,maxNumLaps,music,effects,initCar,initCarColor,initClima,initCircuit);
        stateManager.attach(controller);
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager, this.inputManager, audioRenderer, guiViewPort);
        Nifty nifty = niftyDisplay.getNifty();
        guiViewPort.addProcessor(niftyDisplay);
        nifty.fromXml("Interface/Menu/menu.xml", "start", controller);
        
        //linkamos el boton escape
        this.inputManager.deleteMapping(SimpleApplication.INPUT_MAPPING_EXIT);
        this.inputManager.addMapping("Pause", new KeyTrigger(KeyInput.KEY_ESCAPE));
        this.inputManager.addListener(this, "Pause");
        
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
    
    public String getCarColorNameENG(){
        return controller.getCarColorNameENG();
    }    
        
    public int getIdCar(){
        return controller.getIdCar();
    }  
    
    
    public int getNumLaps(){
        return controller.getNumLaps();
    }
    
    public int getNumEnemies(){
        return controller.getNumEnemies();
    }    
    public String getWeatherName(){
        return controller.getWeatherName();
    }
    
    public int getIdCircuit(){
        return controller.getIdCircuit();
    }  
    public void gotoScreen(String screen){
        controller.gotoScreen(screen);
    }

    public boolean readyToUnPause(){
      return controller.readyToUnPause();
    }
  
    public void unPauseDone(){
        controller.unPauseDone();
    }

    public void onAction(String binding, boolean value, float tpf){
        if (binding.equals("Pause") && value){           
            if (main.isGamePaused()){                
                main.unPause();
            }
            else{                
                main.pause();
            }
        }
    }
}
