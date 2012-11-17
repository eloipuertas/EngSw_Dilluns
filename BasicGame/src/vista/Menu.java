package vista;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.button.ButtonControl;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import java.util.ArrayList;

public class Menu extends AbstractAppState implements ScreenController {

  private Nifty nifty;
  private Application app;
  private Screen screen;
  private boolean isMenuFinished;    
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
  private ArrayList<Car> cars = new ArrayList<Car>();  
  private int actualCar;
  private ArrayList<CarColor> colors = new ArrayList<CarColor>();
  private int actualColor;
  private ArrayList<Circuit> circuits = new ArrayList<Circuit>();
  private int actualCircuit;
  private int actualWeather;
  private ArrayList<Weather> weathers = new ArrayList<Weather>();
  private String imagesPath;
  
  private class Car{
      private String carName;
      private String carImageFileName;
      private String imageExtension;
      
      public Car(String carName,String carImageFileName, String imageExtension){
          this.carName = carName;
          this.carImageFileName = carImageFileName;
          this.imageExtension = imageExtension;
      }  
  }
  
  private class Weather{
      private String weatherName;
      private String weatherImageFilename;
      
      public Weather(String weatherName, String weatherImageFilename){
          this.weatherName = weatherName;
          this.weatherImageFilename = weatherImageFilename;
      }
  } 
  
  private class CarColor{
      private String colorName;
      private ColorRGBA colorRGBA;      
      
      public CarColor(String colorName,ColorRGBA colorRGBA){
          this.colorName = colorName;
          this.colorRGBA = colorRGBA;
      }  
  }
  
  private class Circuit{
      private String circuitName;
      private String circuitImageFileName;
      private String imageExtension;
      
      public Circuit(String circuitName,String circuitImageFileName,String imageExtension){
          this.circuitName = circuitName;
          this.circuitImageFileName = circuitImageFileName;
          this.imageExtension = imageExtension;
      }              
  }
  

  public Menu(AppSettings settings,AssetManager manager, Node rootNode, SimpleApplication main,boolean debugInfo,int initNumEnemies,int minNumEnemies, int maxNumEnemies,int initNumLaps, int minNumLaps, int maxNumLaps,int initNumVolume, int minNumVolume, int maxNumVolume,int initCar, int initCarColor,int initWeather,int initCircuit) {
      this.imagesPath = "Interface/Menu/"; 
      isMenuFinished = false;
      
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
      
      //Quitamos la informacion de debug por defecto
      this.main.setDisplayFps(this.debugInfo); // to hide the FPS
      this.main.setDisplayStatView(this.debugInfo); // to hide the statistics 
  
      cars.add(new Car("coche1","coche1",".jpg"));
      cars.add(new Car("coche2","coche2",".jpeg"));
     
      colors.add(new CarColor("Rojo",new ColorRGBA(255,0,0,128)));
      colors.add(new CarColor("Verde",new ColorRGBA(0,255,0,128)));
      colors.add(new CarColor("Azul",new ColorRGBA(0,0,255,128)));      
      
      weathers.add(new Weather("Soleado","sol"));
      weathers.add(new Weather("Lluvioso","lluvia"));
      
      circuits.add(new Circuit("Montmelo","circuito1",".jpg"));
      circuits.add(new Circuit("Jerez","circuito2",".jpg"));
      
      actualCar = initCar;
      actualColor = initCarColor;
      actualWeather = initWeather;
      actualCircuit = initCircuit;
  }

  public void startGame() {      
    isMenuFinished = true;      
    nifty.exit();      
  }
  
  public void gotoScreenCarSelect(String mode){
      this.mode=mode;
      nifty.gotoScreen("coches");
  }
    
  public void gotoScreen(String screen){
      nifty.gotoScreen(screen);      
  }  

  public void quitGame() {
    app.stop();
  }
  
  public boolean isMenuFinished(){
      return isMenuFinished;
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
  
  public String getCarImagePath(){
      return imagesPath+cars.get(actualCar).carImageFileName+colors.get(actualColor).colorName+cars.get(actualCar).imageExtension;       
  }
  
  public String getCarName(){
      return cars.get(actualCar).carName;
  }
  
  public void setCar(String scroll){
      
      if (scroll.equals("-")){
          actualCar = actualCar -1;          
      }
      else{
          actualCar= actualCar +1;          
      }      
      
      if(actualCar >= cars.size()){
          actualCar = 0;
      }
      else if (actualCar < 0){
          actualCar = cars.size()-1;
      }      
      
      // first load the new image
      NiftyImage newImage = nifty.getRenderEngine().createImage(this.getCarImagePath(), false); // false means don't linear filter the image, true would apply linear filtering

      // find the element with it's id
      Element element = screen.findElementByName("carImage");

      // change the image with the ImageRenderer
      element.getRenderer(ImageRenderer.class).setImage(newImage); 
  }
  
  public void setCarColor(String scroll){
      if(scroll.equals("-")){
          actualColor=actualColor-1;
      }
      else{
          actualColor=actualColor+1;
      }
      
      if(actualColor >= colors.size()){
          actualColor=0;
      }
      else if(actualColor < 0){
          actualColor= colors.size()-1;
      }
      
      Element colorText = nifty.getCurrentScreen().findElementByName("colorText");
      colorText.getRenderer(TextRenderer.class).setText(colors.get(actualColor).colorName);
      
      // first load the new image
      NiftyImage newImage = nifty.getRenderEngine().createImage(this.getCarImagePath(), false); // false means don't linear filter the image, true would apply linear filtering

      // find the element with it's id
      Element element = screen.findElementByName("carImage");

      // change the image with the ImageRenderer
      element.getRenderer(ImageRenderer.class).setImage(newImage); 
      
  }
  
  public String getCarColorName(){      
      return colors.get(actualColor).colorName;
  }
  
  public ColorRGBA getCarColorRGBA(){
      return colors.get(actualColor).colorRGBA;
  }
  
  public String getWeatherName(){
      return weathers.get(actualWeather).weatherName;
  }
  
  public void setWeather(String scroll){
      if(scroll.equals("-")){
          actualWeather=actualWeather-1;
      }
      else{
          actualWeather=actualWeather+1;
      }
      
      if(actualWeather >= weathers.size()){
          actualWeather=0;
      }
      else if(actualWeather < 0){
          actualWeather= weathers.size()-1;
      }
      
      Element weatherText = nifty.getCurrentScreen().findElementByName("weatherText");
      weatherText.getRenderer(TextRenderer.class).setText(weathers.get(actualWeather).weatherName);
      
       // first load the new image
      NiftyImage newImage = nifty.getRenderEngine().createImage(this.getCircuitImagePath(), false); // false means don't linear filter the image, true would apply linear filtering

      // find the element with it's id
      Element element = screen.findElementByName("circuitImage");

      // change the image with the ImageRenderer
      element.getRenderer(ImageRenderer.class).setImage(newImage);
 }
  
  public void setCircuit(String scroll){
      if (scroll.equals("-")){
          actualCircuit = actualCircuit -1;          
      }
      else{
          actualCircuit = actualCircuit +1;          
      }      
      
      if(actualCircuit >= circuits.size()){
          actualCircuit = 0;
      }
      else if (actualCircuit < 0){
          actualCircuit = circuits.size()-1;
      }     
      
      // first load the new image
      NiftyImage newImage = nifty.getRenderEngine().createImage(this.getCircuitImagePath(), false); // false means don't linear filter the image, true would apply linear filtering

      // find the element with it's id
      Element element = screen.findElementByName("circuitImage");

      // change the image with the ImageRenderer
      element.getRenderer(ImageRenderer.class).setImage(newImage);
  }
  
  public String getCircuitImagePath(){
      return imagesPath+circuits.get(actualCircuit).circuitImageFileName+weathers.get(actualWeather).weatherImageFilename+circuits.get(actualCircuit).imageExtension;       
  }
  
  public String getCircuitName(){
      return circuits.get(actualCircuit).circuitName;
  }
  
  public int getNumLaps(){
      return numLaps;
  }
  
  public int getNumEnemies(){
      return numEnemies;
  } 
  
  public int getVolume(){
      return numVolume;
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
