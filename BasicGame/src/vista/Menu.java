package vista;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.effects.EffectEventId;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import java.util.ArrayList;
import model.Audio;

public class Menu extends AbstractAppState implements ScreenController {
    
  private boolean paused = false;  
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
  private boolean music;
  private boolean effects;
  private String mode;  
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
  private ArrayList<DayState> dayStates = new ArrayList<DayState>();
  private int actualDayState;
  private ArrayList<Position> qualifying = new ArrayList<Position>();
  private Audio menu_music;
  
  private class Position{      
      private String name;
      private String time;
      
      public Position(String name,String time){         
          this.name=name;
          this.time=time;
      }
  }
  
  private class Car{
      private String carName;
      private String carImageFileName;
      private String imageExtension;
      private int idCar;
      private String brandName;
      private String brandExtension;
      
      public Car(int idCar,String carName,String carImageFileName, String imageExtension,String brandName,String brandExtension){
          this.idCar = idCar;
          this.carName = carName;
          this.carImageFileName = carImageFileName;
          this.imageExtension = imageExtension;
          this.brandName = brandName;
          this.brandExtension = brandExtension;                  
      } 
  }
  
  private class Weather{
      private String weather;     
      
      public Weather(String weather){
          this.weather = weather;          
      }
  }
  
  private class DayState{
      private String dayState;      
      
      public DayState(String dayState){
          this.dayState=dayState;
      }
  }
  
  private class CarColor{
      private String colorNameSPA;
      private String colorNameENG;          
      
      public CarColor(String colorNameSPA,String colorNameENG){
          this.colorNameSPA = colorNameSPA;
          this.colorNameENG = colorNameENG;         
      }  
  }
  
  private class Circuit{
      private String circuitName;
      private String circuitImageFileName;
      private String imageExtension;
      private int idCircuit;
      
      public Circuit(String circuitName,String circuitImageFileName,int idCircuit,String imageExtension){
          this.circuitName = circuitName;
          this.circuitImageFileName = circuitImageFileName;
          this.imageExtension = imageExtension;
          this.idCircuit = idCircuit;
      }              
  }  

  public Menu(AppSettings settings,AssetManager manager, Node rootNode, SimpleApplication main,boolean debugInfo,int initNumEnemies,int minNumEnemies, int maxNumEnemies,int initNumLaps, int minNumLaps, int maxNumLaps,boolean music,boolean effects,int initCar, int initCarColor,int initWeather,int initCircuit) {
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
      this.music = music;
      this.effects = effects;
      
      mode = null;
      this.main = main;
      this.debugInfo = debugInfo;            
      
      //Quitamos la informacion de debug por defecto
      this.main.setDisplayFps(this.debugInfo); // to hide the FPS
      this.main.setDisplayStatView(this.debugInfo); // to hide the statistics 
  
      cars.add(new Car(1,"coche1","coche1",".png","ferrari",".png"));
      cars.add(new Car(2,"coche2","coche2",".png","golf",".png"));
     
      colors.add(new CarColor("Rojo","Red"));
      colors.add(new CarColor("Blanco","White"));
      colors.add(new CarColor("Rosa","Pink"));
      colors.add(new CarColor("Naranja","Orange"));
      colors.add(new CarColor("Marrón","Brown"));
      colors.add(new CarColor("Amarillo","Yellow"));
      colors.add(new CarColor("Gris","Gray"));
      colors.add(new CarColor("Verde","Green"));
      colors.add(new CarColor("Turquesa","Cyan"));
      colors.add(new CarColor("Azul","Blue"));
      colors.add(new CarColor("Lila","Violet"));         
      
      weathers.add(new Weather("Despejado"));
      weathers.add(new Weather("Lluvioso"));
      weathers.add(new Weather("Nevado"));
      weathers.add(new Weather("Nebuloso"));
      
      dayStates.add(new DayState("Dia"));
      dayStates.add(new DayState("Noche"));     
              
      circuits.add(new Circuit("Montmelo","circuito1",0,".png"));
      circuits.add(new Circuit("Jerez","circuito2",1,".png"));
      circuits.add(new Circuit("Tsukuba","circuito3",2,".png"));
      
      qualifying = new ArrayList<Position>();
      qualifying.add(0,new Position("",""));
      qualifying.add(1,new Position("",""));
      
      actualCar = initCar;
      actualColor = initCarColor;
      actualWeather = initWeather;
      actualCircuit = initCircuit;
      actualDayState = 0;
      initAudio(rootNode, manager);
  }
  
  private void initAudio(Node rootNode, AssetManager manager) {
      menu_music = new Audio(rootNode, manager, "song_menu.wav", true);
      menu_music.play();
  }

  public void startGame() {
      menu_music.stop();
      isMenuFinished = true;
      gotoScreen("null");
      //nifty.exit();
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
  
  public void setIsMenuFinished(boolean isMenuFinished){
      this.isMenuFinished=isMenuFinished;
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
    
  public void setDebugInfo(){
    this.debugInfo = !this.debugInfo;        
    // find the element with it's id
    Element element = screen.findElementByName("debug");
    
    if (debugInfo){
        // first load the new image
        NiftyImage newImage = nifty.getRenderEngine().createImage(this.imagesPath+"ON.png", false); // false means don't linear filter the image, true would apply linear filtering
        // change the image with the ImageRenderer
        element.getRenderer(ImageRenderer.class).setImage(newImage);
        
    }
    else{
       // first load the new image
       NiftyImage newImage = nifty.getRenderEngine().createImage(this.imagesPath+"OFF.png", false); // false means don't linear filter the image, true would apply linear filtering
       // change the image with the ImageRenderer
       element.getRenderer(ImageRenderer.class).setImage(newImage);
    }  
    this.main.setDisplayFps(this.debugInfo); 
    this.main.setDisplayStatView(this.debugInfo); 
  }
  
  public String getDebugInfoImagePath(){
      if(this.debugInfo){
          return this.imagesPath+"ON.png";
      }
      else{
          return this.imagesPath+"OFF.png";
      }
  }
  
  public void setMusic(){
    this.music = !this.music;        
    // find the element with it's id
    Element element = screen.findElementByName("music");
    
    if (music){
        menu_music.play();
        // first load the new image
        NiftyImage newImage = nifty.getRenderEngine().createImage(this.imagesPath+"ON.png", false); // false means don't linear filter the image, true would apply linear filtering
        // change the image with the ImageRenderer
        element.getRenderer(ImageRenderer.class).setImage(newImage);
    }
    else{
        menu_music.stop();
        // first load the new image
        NiftyImage newImage = nifty.getRenderEngine().createImage(this.imagesPath+"OFF.png", false); // false means don't linear filter the image, true would apply linear filtering
        // change the image with the ImageRenderer
        element.getRenderer(ImageRenderer.class).setImage(newImage);
    }    
  }
  
  public boolean getMusic(){
      return this.music;
  }
  
  public String getMusicImagePath(){
      if(this.music){
          return this.imagesPath+"ON.png";
      }
      else{
          return this.imagesPath+"OFF.png";
      }
  }
  
  public void setEffects(){
    this.effects = !this.effects;
    // find the element with it's id
    Element element = screen.findElementByName("effects");
    
    if (effects){
        // first load the new image
        NiftyImage newImage = nifty.getRenderEngine().createImage(this.imagesPath+"ON.png", false); // false means don't linear filter the image, true would apply linear filtering
        // change the image with the ImageRenderer
        element.getRenderer(ImageRenderer.class).setImage(newImage);
    }
    else{
        // first load the new image
        NiftyImage newImage = nifty.getRenderEngine().createImage(this.imagesPath+"OFF.png", false); // false means don't linear filter the image, true would apply linear filtering
        // change the image with the ImageRenderer
        element.getRenderer(ImageRenderer.class).setImage(newImage);
    }   
  }
  
  public boolean getEffects(){
      return this.effects;
  }
  
  public String getEffectsImagePath(){
      if(this.effects){
          return this.imagesPath+"ON.png";
      }
      else{
          return this.imagesPath+"OFF.png";
      }
  }
  
  public String getCarImagePath(){
      return imagesPath+cars.get(actualCar).carImageFileName+colors.get(actualColor).colorNameENG+cars.get(actualCar).imageExtension;       
  }
  
  public String getBrandImagePath(){
      return imagesPath+cars.get(actualCar).brandName+cars.get(actualCar).brandExtension;
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
      Element element = nifty.getCurrentScreen().findElementByName("carImage");

      // change the image with the ImageRenderer
      element.getRenderer(ImageRenderer.class).setImage(newImage);
      
      // first load the new image
      newImage = nifty.getRenderEngine().createImage(this.getBrandImagePath(), false); // false means don't linear filter the image, true would apply linear filtering

      // find the element with it's id
      element = nifty.getCurrentScreen().findElementByName("brandImage");

      // change the image with the ImageRenderer
      element.getRenderer(ImageRenderer.class).setImage(newImage);
      
      if (scroll.equals("+")){
        nifty.getCurrentScreen().findElementByName("panel_imagen_coche").startEffect(EffectEventId.onCustom,null,"moveRightIn");
      }
      else{
         nifty.getCurrentScreen().findElementByName("panel_imagen_coche").startEffect(EffectEventId.onCustom,null,"moveLeftIn");
      }
      nifty.getCurrentScreen().findElementByName("panel_marca").startEffect(EffectEventId.onCustom,null,"moveTopIn");

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
      
      nifty.getCurrentScreen().findElementByName("panel_imagen_coche").startEffect(EffectEventId.onCustom,null,"fadeIn");

      Element colorText = nifty.getCurrentScreen().findElementByName("colorText");
      colorText.getRenderer(TextRenderer.class).setText(this.getCarColorNameSPA());
      
      // first load the new image
      NiftyImage newImage = nifty.getRenderEngine().createImage(this.getCarImagePath(), false); // false means don't linear filter the image, true would apply linear filtering
      
      // find the element with it's id
      Element element = nifty.getCurrentScreen().findElementByName("carImage");
           
      // change the image with the ImageRenderer
      element.getRenderer(ImageRenderer.class).setImage(newImage);
      
      nifty.getCurrentScreen().findElementByName("panel_imagen_coche").startEffect(EffectEventId.onCustom,null,"fadeOut");
  }
  
  public boolean readyToUnPause(){
      return paused;
  }
  
  public void unPauseDone(){
      paused = false;
  }
  
  public void unPause(){
      paused = true;
  } 
  
  public String getCarColorNameSPA(){      
      return colors.get(actualColor).colorNameSPA;
  }
  
  public String getCarColorNameENG(){
      return colors.get(actualColor).colorNameENG;
  }
  
  public int getIdCar(){
      return cars.get(actualCar).idCar;              
  }
  
  public String getWeatherName(){
      return weathers.get(actualWeather).weather;
  }
  
  public String getDayStateName(){
      return dayStates.get(actualDayState).dayState;
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
      weatherText.getRenderer(TextRenderer.class).setText(weathers.get(actualWeather).weather);
      
       // first load the new image
      NiftyImage newImage = nifty.getRenderEngine().createImage(this.getCircuitImagePath(), false); // false means don't linear filter the image, true would apply linear filtering

      // find the element with it's id
      Element element = nifty.getCurrentScreen().findElementByName("circuitImage");

      // change the image with the ImageRenderer
      element.getRenderer(ImageRenderer.class).setImage(newImage);
 }
  
  public void setDayState(String scroll){
      if(scroll.equals("-")){
          actualDayState=actualDayState-1;
      }
      else{
          actualDayState=actualDayState+1;
      }
      
      if(actualDayState >= dayStates.size()){
          actualDayState=0;
      }
      else if(actualDayState < 0){
          actualDayState = dayStates.size()-1;
      }
      
      nifty.getCurrentScreen().findElementByName("panel_imagen_circuito").startEffect(EffectEventId.onCustom,null,"fadeIn");

      
      Element weatherText = nifty.getCurrentScreen().findElementByName("dayStateText");
      weatherText.getRenderer(TextRenderer.class).setText(dayStates.get(actualDayState).dayState);
      
       // first load the new image
      NiftyImage newImage = nifty.getRenderEngine().createImage(this.getCircuitImagePath(), false); // false means don't linear filter the image, true would apply linear filtering

      // find the element with it's id
      Element element = nifty.getCurrentScreen().findElementByName("circuitImage");

      // change the image with the ImageRenderer
      element.getRenderer(ImageRenderer.class).setImage(newImage);
      
      nifty.getCurrentScreen().findElementByName("panel_imagen_circuito").startEffect(EffectEventId.onCustom,null,"fadeOut");

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
      
      nifty.getCurrentScreen().findElementByName("panel_imagen_circuito").startEffect(EffectEventId.onCustom,null,"fadeIn");
      
      // first load the new image
      NiftyImage newImage = nifty.getRenderEngine().createImage(this.getCircuitImagePath(), false); // false means don't linear filter the image, true would apply linear filtering

      // find the element with it's id
      Element element = nifty.getCurrentScreen().findElementByName("circuitImage");

      // change the image with the ImageRenderer
      element.getRenderer(ImageRenderer.class).setImage(newImage);
      
      nifty.getCurrentScreen().findElementByName("panel_imagen_circuito").startEffect(EffectEventId.onCustom,null,"fadeOut");

  }
  
  public String getCircuitImagePath(){
      return imagesPath+circuits.get(actualCircuit).circuitImageFileName+dayStates.get(actualDayState).dayState+weathers.get(actualWeather).weather+circuits.get(actualCircuit).imageExtension;       
  }
  
  public String getCircuitName(){
      return circuits.get(actualCircuit).circuitName;
  }
  
  public int getIdCircuit(){
      return circuits.get(actualCircuit).idCircuit;
  }
  
  public void setQualifying(int pos,String name,String time){
      qualifying.add(pos-1, new Position(name,time));
      Element text = nifty.getScreen("qualifying").findElementByName("1NameText");
      text.getRenderer(TextRenderer.class).setText(qualifying.get(0).name);
      text = nifty.getScreen("qualifying").findElementByName("1TimeText");
      text.getRenderer(TextRenderer.class).setText(qualifying.get(0).time);
      text = nifty.getScreen("qualifying").findElementByName("2NameText");
      text.getRenderer(TextRenderer.class).setText(qualifying.get(1).name);
      text = nifty.getScreen("qualifying").findElementByName("2TimeText");
      text.getRenderer(TextRenderer.class).setText(qualifying.get(1).time);      
  }
  
  public String getQualifyingName(String pos){
      return qualifying.get(Integer.parseInt(pos)-1).name;
  }
  
  public String getQualifyingTime(String pos){
      return qualifying.get(Integer.parseInt(pos)-1).time;
  }
  
  public int getNumLaps(){
      return numLaps;
  }
  
  public int getNumEnemies(){
      return numEnemies;
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
