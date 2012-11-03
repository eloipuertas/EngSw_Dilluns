package vista;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.jme3.system.Timer;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.button.ButtonControl;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

public class Menu extends AbstractAppState implements ScreenController {

  private Nifty nifty;
  private Application app;
  private Screen screen;
  private boolean isGameStarted;  
  private final AudioNode menu_sound;
  private int numEnemies;
  private int initNumEnemies;
  private int minNumEnemies;
  private int maxNumEnemies;
  private int numLaps;
  private int initNumLaps;
  private int minNumLaps;
  private int maxNumLaps;
  private SimpleApplication main;
  private boolean debugInfo;
  private String mode;
  private int initNumVolume;
  private int minNumVolume;
  private int maxNumVolume;
  private int numVolume;
  private AppSettings settings;
  

  public Menu(AppSettings settings,AssetManager manager, Node rootNode, SimpleApplication main,boolean debugInfo,int initNumEnemies,int minNumEnemies, int maxNumEnemies,int initNumLaps, int minNumLaps, int maxNumLaps,int initNumVolume, int minNumVolume, int maxNumVolume) {
      isGameStarted = false;
      
      this.settings = settings;      
      
      this.initNumLaps = initNumLaps;
      this.minNumLaps = minNumLaps;
      this.maxNumLaps = maxNumLaps;
      this.numLaps = this.initNumLaps;
      this.initNumEnemies = initNumEnemies;
      this.minNumEnemies = minNumEnemies;
      this.maxNumEnemies = maxNumEnemies;
      this.numEnemies = this.initNumEnemies;
      this.initNumVolume = initNumVolume;
      this.minNumVolume = minNumVolume;
      this.maxNumVolume = maxNumVolume;
      this.numVolume=this.initNumVolume;
      
      mode = null;
      this.main = main;
      this.debugInfo = debugInfo;      
      menu_sound = new AudioNode(manager, "Sounds/song_menu.wav", false);
      menu_sound.setLooping(true);  // activate continuous playing    
      menu_sound.setVolume(this.numVolume);
      rootNode.attachChild(menu_sound);
      menu_sound.play(); // play continuously!
      
      //Quitamos la informacion de debug por defecto
      this.main.setDisplayFps(this.debugInfo); // to hide the FPS
      this.main.setDisplayStatView(this.debugInfo); // to hide the statistics 
  }

  public void startGame() {      
    isGameStarted = true;    
    menu_sound.stop();    
    nifty.exit();  // switch to another screen    
  }
  
  public void controlsScreen(String mode){
      this.mode=mode;
      gotoScreen("controls");
  }
  
  public void gotoScreen(String screen){
      nifty.gotoScreen(screen);      
  }  

  public void quitGame() {
    app.stop();
  }
  
  public boolean isGameStarted(){
      return isGameStarted;
  }

  public String getPlayerName() {
    return System.getProperty("user.name");
  }
  
  public void setEnemies(String value){
      
      Element enemies = nifty.getCurrentScreen().findElementByName("enemyText");      
      if (value.equals("+")){
          numEnemies = Integer.parseInt(enemies.getRenderer(TextRenderer.class).getOriginalText()); 
          if(numEnemies < this.maxNumEnemies){
            numEnemies = numEnemies + 1;
            enemies.getRenderer(TextRenderer.class).setText(String.valueOf(numEnemies));
          }
      }
      else if (value.equals("-")){
          numEnemies = Integer.parseInt(enemies.getRenderer(TextRenderer.class).getOriginalText()); 
          if (numEnemies > this.minNumEnemies){
            numEnemies = numEnemies - 1;
            enemies.getRenderer(TextRenderer.class).setText(String.valueOf(numEnemies));
          }
      }      
  }
  
  public void setLaps(String value){
      
      Element laps = nifty.getCurrentScreen().findElementByName("lapsText");      
      if (value.equals("+")){
          numLaps = Integer.parseInt(laps.getRenderer(TextRenderer.class).getOriginalText()); 
          if (numLaps < this.maxNumLaps){
            numLaps = numLaps + 1;
            laps.getRenderer(TextRenderer.class).setText(String.valueOf(numLaps));
          }
      }
      else if (value.equals("-")){
          numLaps = Integer.parseInt(laps.getRenderer(TextRenderer.class).getOriginalText()); 
          if (numLaps > this.minNumLaps){
            numLaps = numLaps - 1;
            laps.getRenderer(TextRenderer.class).setText(String.valueOf(numLaps));
          }
      }      
  }
  
  public void setVolume(String value){
      
      Element volume = nifty.getCurrentScreen().findElementByName("volumeText");      
      if (value.equals("+")){
          numVolume = Integer.parseInt(volume.getRenderer(TextRenderer.class).getOriginalText()); 
          if (numVolume < this.maxNumVolume){
            numVolume = numVolume + 1;
            volume.getRenderer(TextRenderer.class).setText(String.valueOf(numVolume));
          }
      }
      else if (value.equals("-")){
          numVolume = Integer.parseInt(volume.getRenderer(TextRenderer.class).getOriginalText()); 
          if (numVolume > this.minNumVolume){
            numVolume = numVolume - 1;
            volume.getRenderer(TextRenderer.class).setText(String.valueOf(numVolume));
          }
      }
      menu_sound.setVolume(this.numVolume);
  }
  
  public void setDebugInfo(){
    this.debugInfo = !this.debugInfo;        
    
    if (debugInfo){
        nifty.getCurrentScreen().findControl("debug",  ButtonControl.class).setText("ON");
    }
    else{
        nifty.getCurrentScreen().findControl("debug",  ButtonControl.class).setText("OFF");
    }  
    this.main.setDisplayFps(this.debugInfo); 
    this.main.setDisplayStatView(this.debugInfo); 
  }  
  
  public int getNumLaps(){
      return numLaps;
  }
  
  public int getNumEnemies(){
      return numEnemies;
  }
  
  public int getInitNumEnemies(){
      return initNumEnemies;
  }
  
  public int getInitNumLaps(){
      return initNumLaps;
  }
  
  public int getInitNumVolume(){
      return initNumVolume;
  }
  
  public String getMode(){
      return this.mode;
  }
  
  public String getHeight(){
      return ""+settings.getHeight();
  }
  
  public String getWidth(){
      return ""+settings.getWidth();
  }

  /** Nifty GUI ScreenControl methods */
  public void bind(Nifty nifty, Screen screen) {
    this.nifty = nifty;
    this.screen = screen;
  }

  public void onStartScreen() {      
  }

  public void onEndScreen() {
  }

  /** jME3 AppState methods */
  @Override
  public void initialize(AppStateManager stateManager, Application app) {
    this.app = app;
  }

  @Override
  public void update(float tpf) {    
  }
}
