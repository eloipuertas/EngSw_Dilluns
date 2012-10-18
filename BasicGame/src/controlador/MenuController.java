package controlador;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

public class MenuController extends AbstractAppState implements ScreenController {

  private Nifty nifty;
  private Application app;
  private Screen screen;
  private boolean isGameStarted;

  /** custom methods */
  public MenuController() {
    isGameStarted = false;
    /** You custom constructor, can accept arguments */
  }

  public void startGame(String nextScreen) {
    isGameStarted = true;
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
