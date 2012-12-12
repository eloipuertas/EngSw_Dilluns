package vista;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.AppSettings;
import com.jme3.system.Timer;
import com.jme3.ui.Picture;
import controlador.MenuController;

public class Display{
    
    private Node displayNode;
    private Node minimapNode;
    private BitmapText pos;
    private BitmapText lap;
    private BitmapText chronograph;
    private BitmapText posText;
    private BitmapText lapText;
    private BitmapFont numbersFont;
    private BitmapFont textFont;
    private BitmapFont chronographFont;
    private AssetManager assetManager;
    private AppSettings settings;
    private Node guiNode;
    private Timer timer;
    private Camera camTrasera;
    private float totalSecondsPause;    
    private float offsetChronograph;
    private Geometry marcaVermella;  
    private MenuController menu;
    public Display(AssetManager assetManager, AppSettings settings,Node guiNode,Timer timer, MenuController menu){
        
        this.assetManager = assetManager;
        this.settings = settings;
        this.guiNode = guiNode;        
        this.displayNode = new Node("Display");
        this.minimapNode = new Node("Minimap");
        this.timer = timer;
        this.totalSecondsPause = 0f;      
        this.offsetChronograph = 0f;        
        this.menu = menu;
    }       
    
    public void addDisplay(int xDisplay,int yDisplay,float scaleValueDisplay,int xPosText, int yPosText, float scaleValuePosText,int xPos, int yPos,float scaleValuePos, int xChronograph, int yChronograph, float scaleValueChronograph,int xLapText, int yLapText, float scaleValueLapText,int xLap, int yLap,float scaleValueLap){
        
        float minDimension = Math.min(settings.getWidth(),settings.getHeight());      
        
        //añadimos el cronoemtro
        chronographFont = assetManager.loadFont("Interface/Fonts/DS-Digital.fnt");
        chronograph = new BitmapText(chronographFont, false);        
        chronograph.setSize(minDimension/scaleValueChronograph);      // font size
        chronograph.setColor(ColorRGBA.White);                            // font color        
        chronograph.setText("00:00");                                    // the text
        chronograph.setLocalTranslation(xChronograph-(chronograph.getLineWidth()/2),yChronograph,0);     // position
        guiNode.attachChild(chronograph);
        
        //añadimos el numero de la posicion
        numbersFont = assetManager.loadFont("Interface/Fonts/MotorOil1937M54.fnt");
        pos = new BitmapText(numbersFont, false);        
        pos.setSize(minDimension/scaleValuePos);      // font size
        pos.setColor(ColorRGBA.Yellow);                            // font color
        pos.setText("1");                                    // the text
        pos.setLocalTranslation(xPos,yPos,0);     // position
        guiNode.attachChild(pos);
        
        //añadimos el texto POS
        textFont = assetManager.loadFont("Interface/Fonts/DejaVuSansCondensed.fnt");
        posText = new BitmapText(textFont, false);       
        posText.setSize(minDimension/scaleValuePosText);      // font size
        posText.setColor(ColorRGBA.White);                            // font color
        posText.setText("POS");                                    // the text
        posText.setLocalTranslation(xPosText,yPosText,0);     // position
        guiNode.attachChild(posText);
        
        //añadimos el numero de las vueltas        
        lap = new BitmapText(numbersFont, false);        
        lap.setSize(minDimension/scaleValuePos);      // font size
        lap.setColor(ColorRGBA.Orange);                            // font color
        lap.setText("5");                                    // the text
        lap.setLocalTranslation(xLap,yLap,0);     // position
        guiNode.attachChild(lap);
        
        //añadimos el texto VUELTA
        lapText = new BitmapText(textFont, false);       
        lapText.setSize(minDimension/scaleValuePosText);      // font size
        lapText.setColor(ColorRGBA.White);                            // font color
        lapText.setText("VUELTA");                                    // the text
        lapText.setLocalTranslation(xLapText,yLapText,0);     // position
        guiNode.attachChild(lapText);
        
        //Agregar fondo marcador        
        Picture display = new Picture("display");
        display.setImage(assetManager, "Textures/Display/gauge.png", true);        
          
        display.setWidth(minDimension/scaleValueDisplay);
        display.setHeight(minDimension/scaleValueDisplay);        
        display.setPosition(0,0);
        display.center();
        display.move(xDisplay,yDisplay, -1); //-1 para estar debajo de la aguja        
        guiNode.attachChild(display);
        
        //Agregar minimap
        Picture minimap = new Picture("minimap");
        if(menu.getIdCircuit() == 0){
            minimap.setImage(assetManager, "Textures/Display/minimapa.png", true);
            //Dibujamos el minimapa a razon de altura = 2*anchura
            minimap.setWidth((settings.getWidth()/(6.75f)));
            minimap.setHeight((settings.getWidth()/(6.75f))*2);
            minimap.setPosition(0,0);
            minimap.center();
            //Situamos el minimapa en el extremo izquierdo inferior de la pantalla, sea cual sea la resolucion
            //minimap.move((settings.getWidth()/6.75f)/2,((settings.getWidth()/(6.75f))*2)/2,-1);
            minimap.move(((settings.getWidth()/(6.75f)))/2,((settings.getWidth()/(6.75f))*2)/2,-1);
            
        }else if(menu.getIdCircuit() ==1){
            minimap.setImage(assetManager,"Textures/Display/minimapa2.png", true);
            //Dibujamos el minimapa a razon de altura = 2*anchura
            minimap.setWidth((settings.getWidth()/(6.75f)));
            minimap.setHeight((settings.getWidth()/(6.75f))*1.46f);
            
            minimap.setPosition(0,0);
            minimap.center();
            minimap.move(((settings.getWidth()/(6.75f)))/2,((settings.getWidth()/(6.75f))*1.46f)/2,-1);
        }else if(menu.getIdCircuit() ==2){
            minimap.setImage(assetManager, "Textures/Display/minimapa3.png", true);
            minimap.setHeight((settings.getHeight()/(3f)));
            minimap.setWidth((settings.getHeight()/(3f))*1.12f);
            minimap.setPosition(0,0);
            minimap.center();
            //Situamos el minimapa en el extremo izquierdo inferior de la pantalla, sea cual sea la resolucion
            //minimap.move((settings.getWidth()/6.75f)/2,((settings.getWidth()/(6.75f))*2)/2,-1);
            minimap.move(((settings.getHeight()/(3f))*1.12f)/2,((settings.getHeight()/(3f)))/2,-1);
        }
        minimapNode.attachChild(minimap);
        guiNode.attachChild(minimapNode);
        
        //Agregar aguja
        Picture arrow = new Picture("arrow");        
        arrow.setImage(assetManager, "Textures/Display/arrow.png", true);        
        arrow.setWidth(minDimension/scaleValueDisplay);
        arrow.setHeight(minDimension/scaleValueDisplay);
        arrow.setPosition(0,0);
        arrow.center();
        arrow.move(0, 0, 1); //1 para poner por encima del marcador                
        
        displayNode.attachChild(arrow);        
              
        guiNode.attachChild(displayNode);
        this.displayNode.move(xDisplay,yDisplay,0);
        
        //Añadimos punto del coche    
        Sphere sphere = new Sphere(30, 30, 3f);
        marcaVermella = new Geometry("cotxe", sphere);
        Material marcaVermella_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        marcaVermella_mat.setColor("Color", ColorRGBA.Red);
        marcaVermella.setMaterial(marcaVermella_mat);
        guiNode.attachChild(marcaVermella);
        
        this.startChronograph();
        this.updatePosition(0);
    }
    
    public void addMirror(int xMirror,int yMirror,float scaleValueMirror,RenderManager renderManager,Camera cam,Vector3f lookAtRear,Node rootNode){
        float minDimension = Math.min(settings.getWidth(),settings.getHeight());      
        
        //Agregar retrovisor        
        Picture mirror = new Picture("mirror");
        mirror.setImage(assetManager, "Textures/Display/retrovisor.png", true);
        
        mirror.setWidth(minDimension/scaleValueMirror);
        mirror.setHeight(minDimension/scaleValueMirror);        
        mirror.setPosition(0,0);
        mirror.center();
        mirror.move(xMirror,yMirror,0);                
        guiNode.attachChild(mirror);
        
        camTrasera = cam.clone();
        camTrasera.setViewPort(0.39f,0.61f ,0.76f,0.94f);
        camTrasera.setLocation(new Vector3f(0, 3, 0));
        camTrasera.lookAt(lookAtRear, Vector3f.UNIT_Y);      

        ViewPort view2 = renderManager.createMainView("Camara trasera", camTrasera);
        view2.setClearFlags(true, true, true);        
        view2.attachScene(rootNode);        
    }    
    
    private void startChronograph(){
        this.chronograph.setText("00:00");
        this.timer.reset();        
    }
    
    public void resumeChronograph(){
        float totalSeconds = this.timer.getTimeInSeconds();       
        float offsetChronographAux = totalSeconds - totalSecondsPause;            
        offsetChronograph = offsetChronograph + offsetChronographAux;
    }
    
    public void pauseChronograph(){
       totalSecondsPause = this.timer.getTimeInSeconds();       
    } 
    
    private void updateChronograph(){
        float totalSeconds = this.timer.getTimeInSeconds();        
        totalSeconds = totalSeconds - offsetChronograph;        
        int seconds = (int)totalSeconds%60;
        int minutes = (int)totalSeconds/60;
        if (seconds < 10 && minutes < 10){
            this.chronograph.setText("0"+minutes+":0"+seconds);            
        }
        else if(seconds < 10){
            this.chronograph.setText(minutes+":0"+seconds);     
        }
        else if (minutes < 10){
            this.chronograph.setText("0"+minutes+":"+seconds);     
        }
        else{
            this.chronograph.setText(minutes+":"+seconds);     
        }
    }
    
    private void updatePosition(int pos){
        if (pos > 0){
            this.pos.setText(""+pos);
        }
    }
    
    private void updateGauge(float speed){
        
        speed=Math.abs(speed);
        if(isDisplayAdded()){ //comprobamos si el display se ha creado, en caso contratio no hacemos nada            
                     
            if (speed > 200){
                speed=200;
            }
            else if (speed < 0){
                speed=0;
            }            
            float actual_gauge_speed = displayNode.getWorldRotation().getZ();          
            actual_gauge_speed = (float)(46.2606 + (-154.202f *actual_gauge_speed));
            float offset = actual_gauge_speed - speed;                           
            displayNode.rotate(0, 0,offset*0.022185f);  
        }
    }
    
    public boolean isDisplayAdded(){
        if(displayNode.getQuantity() >= 1){
            return true;
        }
        else{
            return false;
        }
    }
    
    public void updateDisplay(float speed,int position){        
        this.updateGauge(speed);
        this.updateChronograph();
        this.updatePosition(position);      
    }
    
    public void updateMirror(Vector3f lookAtRear, Vector3f cameraPos){
         //Actualizamos camara        
        camTrasera.lookAt(lookAtRear, Vector3f.UNIT_Y);
        camTrasera.setLocation(cameraPos);
    }
    
    //Funcion para actualizar la posicion del coche en el minimapa
    //Pasamos como parametro las coordenadas de mundo del coche
    public void updateMinimap(Vector3f posicion){
        float x_map;
        float z_map;
        //Calculamos las coordenadas de minimapa
        //Restamos un offset a la posicion del coche porque el mundo comienza en 36 en el eje x y -116 en el eje z
        //Multiplicamos esa coordenada por el factor de escala, entre el tamaño del minimapa y el tamaño del mapa real
        //Width del mapa = 105 Width del minimapa = Depende de la anchura de la pantalla
        //Height del mapa = 210 Height del minimapa = Depende de la altura de la pantalla
        //Heigth = 2*Width
        x_map = (Math.abs(posicion.x-35))*((settings.getWidth()/(6.75f))/103);
        z_map = (Math.abs(posicion.z+108))*(((settings.getWidth()/(6.75f))*2)/210);
        if(menu.getIdCircuit() == 0){
            x_map = (Math.abs(posicion.x-35))*((settings.getWidth()/(6.75f))/103);
            z_map = (Math.abs(posicion.z+108))*(((settings.getWidth()/(6.75f))*2)/210);
            if(x_map > ((settings.getWidth()/(6.75f)))){
                x_map = (settings.getWidth()/(6.75f));
            }
            if (posicion.x > 36){
                x_map = 0;
            }
            if(z_map > ((settings.getWidth()/(6.75f))*2)){
                z_map = ((settings.getWidth()/(6.75f))*2);
            }
            if (posicion.z < -109){
                z_map = 0;
            }
        }else if(menu.getIdCircuit() == 1){
            x_map = (Math.abs(posicion.x+107.2f))*(((settings.getWidth()/(6.75f)))/245.3f);
            z_map = (Math.abs(posicion.z-182.9f))*(((settings.getWidth()/(6.75f))*1.46f)/359.1f);
            if(x_map > ((settings.getWidth()/(6.75f)))){
                x_map = (settings.getWidth()/(6.75f));
            }
            if (posicion.x < -107.2){
                x_map = 0;
            }
            if(z_map > ((settings.getWidth()/(6.75f))*1.46f)){
                z_map = ((settings.getWidth()/(6.75f))*1.46f);
            }
            if (posicion.z > 182){
                z_map = 0;
            }
        }else if(menu.getIdCircuit() == 2){
            x_map = (Math.abs(posicion.x-177.7f))*(((settings.getHeight()/(3f))*1.12f)/240.1f);
            z_map = (Math.abs(posicion.z+112.5f))*(((settings.getHeight()/(3f)))/214.5f);
            if(x_map > ((settings.getHeight()/(3f))*1.12f)){
                x_map = (settings.getHeight()/(3f))*1.12f;
            }
            if (posicion.x > 177){
                x_map = 0;
            }
            if(z_map > ((settings.getHeight()/(3f)))){
                z_map = ((settings.getHeight()/(3f)));
            }
            if (posicion.z < -112){
                z_map = 0;
            }
        }
        
        //Transladamos el punto rojo a los coordenadas del minimapa, encima de el
        marcaVermella.setLocalTranslation(x_map,z_map,1);
    }
    
}
