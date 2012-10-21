package vista;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.jme3.ui.Picture;

public class Display {
    
    private Node displayNode;    
    private BitmapText pos;
    private BitmapFont guiFont;
    private AssetManager assetManager;
    private AppSettings settings;
    private Node guiNode;    
    
    public Display(AssetManager assetManager, AppSettings settings,Node guiNode,BitmapFont guiFont){
        
        this.assetManager = assetManager;
        this.settings = settings;
        this.guiNode = guiNode;      
        this.guiFont = guiFont;
        this.displayNode = new Node("Display");               
    }
    
    public void addDisplay(int x,int y){
        
        pos = new BitmapText(guiFont, false);          
        pos.setSize(guiFont.getCharSet().getRenderedSize());      // font size
        pos.setColor(ColorRGBA.White);                            // font color
        pos.setText("Pos:");                                    // the text
        pos.setLocalTranslation(settings.getWidth()-50,y+100,0);     // position
        guiNode.attachChild(pos);
        
        //Agregar fondo marcador
        Picture display = new Picture("display");
        display.setImage(assetManager, "Textures/Display/gauge.png", true);        
        float maxDimension = Math.max(settings.getWidth(),settings.getHeight());
        display.setWidth(maxDimension/3f);
        display.setHeight(maxDimension/3f);        
        display.setPosition(0,0);
        display.center();
        display.move(x,y, -1); //-1 para estar debajo de la aguja        
        guiNode.attachChild(display);
        
        //Agregar aguja
        Picture arrow = new Picture("arrow");
        arrow.setImage(assetManager, "Textures/Display/arrow.png", true);        
        arrow.setWidth(maxDimension/3f);
        arrow.setHeight(maxDimension/3f);
        arrow.setPosition(0,0);
        arrow.center();
        arrow.move(0, 0, 1); //1 para poner por encima del marcador                
        
        displayNode.attachChild(arrow);        
              
        guiNode.attachChild(displayNode);
        this.displayNode.move(x,y,0);       
    }
    
    public void updateDisplay(float speed,int pos){
        
        if(isDisplayAdded()){ //comprobamos si el display se ha creado, en caso contratio no hacemos nada            
            this.pos.setText("Pos: "+pos);            
            
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
}
