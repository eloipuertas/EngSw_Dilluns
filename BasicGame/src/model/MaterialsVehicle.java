/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;

/**
 *
 * @author Sergi
 */
public class MaterialsVehicle {
    private String colorMatChasis;
    private Material matChasis;
    private Material matWheels;
    
    public MaterialsVehicle(AssetManager assetManager, String chasisColor){
        colorMatChasis = chasisColor;
        matChasis = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        matWheels = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
    }
    
    public Material getMatChasis(){
        return matChasis;
    }
    
    public Material getMatWheels(){
        return matWheels;
    }
    
    public void initMaterials(){
        matChasis.setBoolean("UseMaterialColors", true);
        matWheels.setBoolean("UseMaterialColors", true);
        matChasis.setColor("Ambient", getRGBA().mult(0.3f));
        matChasis.setColor("Specular",ColorRGBA.White.mult(0.6f));
        matChasis.setColor("Diffuse",getRGBA().mult(0.4f));
        matChasis.setFloat("Shininess", 40f); // [1,128] 
        
        matWheels.setColor("Ambient", ColorRGBA.Black.mult(0.3f));
        matWheels.setColor("Specular",ColorRGBA.Gray.mult(0.5f));
        matWheels.setColor("Diffuse",ColorRGBA.Black);
        matWheels.setFloat("Shininess", 2f); // [1,128] 
        
    }
    
    private ColorRGBA getRGBA(){
        ColorRGBA color = new ColorRGBA();
        //White Pink Red Orange Brown Yellow Gray Green Cyan Blue Violet
        if (colorMatChasis.length() > 0){
            if(colorMatChasis.equals("White")){
                color = ColorRGBA.White;
            } else if (colorMatChasis.equals("Pink")){
                color = ColorRGBA.Pink;
            } else if (colorMatChasis.equals("Red")){
                color = ColorRGBA.Red;
            } else if (colorMatChasis.equals("Orange")){
                color = ColorRGBA.Orange;
            } else if (colorMatChasis.equals("Brown")){
                color = ColorRGBA.Brown;
            } else if (colorMatChasis.equals("Yellow")){
                color = ColorRGBA.Yellow;
            } else if (colorMatChasis.equals("Gray")){
                color = ColorRGBA.Gray;
            } else if (colorMatChasis.equals("Green")){
                color = ColorRGBA.Green;
            } else if (colorMatChasis.equals("Cyan")){
                color = ColorRGBA.Cyan;
            } else if (colorMatChasis.equals("Blue")){
                color = ColorRGBA.Blue;
            } else if (colorMatChasis.equals("Violet")){
                color = ColorRGBA.Magenta;
            } else {
                color = ColorRGBA.Black;
            }
        }
        return color;
    }
}