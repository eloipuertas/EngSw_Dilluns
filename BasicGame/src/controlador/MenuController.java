package controlador;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

public class MenuController extends AbstractAppState implements ScreenController {

  private Nifty nifty;
  private Application app;
  private Screen screen;
  private boolean isGameStarted;  
  private final AudioNode menu_sound;

  /** custom methods */
  public MenuController(AssetManager manager, Node rootNode) {
      isGameStarted = false;
      
      menu_sound = new AudioNode(manager, "Sounds/song_menu.wav", false);
      menu_sound.setLooping(true);  // activate continuous playing    
      menu_sound.setVolume(3);
      rootNode.attachChild(menu_sound);
      menu_sound.play(); // play continuously!       
  }

  public void startGame(String nextScreen) {
    isGameStarted = true;
    menu_sound.stop();
    nifty.gotoScreen(nextScreen);  // switch to another screen    
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
