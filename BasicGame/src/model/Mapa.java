/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import com.jme3.asset.AssetManager;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import java.util.ArrayList;

/**
 *
 * @author JAC
 */
public class Mapa {
    private Vector3f origen;
    private Quaternion rotacionInicial;
    private String sceneModel;
    private String paretsInvisibles;
    private ArrayList<Vector3f> listaLuces;
    private ArrayList<Vector3f> listaCajas;
    private ArrayList<Vector3f> listaMuros;
    private ArrayList<Vector3f> medidas;
    private AssetManager assetManager;
    private float escala;
    
    public Mapa(Vector3f origen, Quaternion rotacionInicial, String sceneModel, String parets, ArrayList<Vector3f> listaLuces, ArrayList<Vector3f> listaCajas, ArrayList<Vector3f> listaMuros, ArrayList<Vector3f> medidas, float escala) {
        this.sceneModel = sceneModel;
        this.paretsInvisibles = parets;
        this.origen = origen;
        this.rotacionInicial = rotacionInicial;
        this.listaCajas = listaCajas;
        this.listaLuces = listaLuces;
        this.listaMuros = listaMuros;
        this.medidas = medidas;
        this.escala = escala;
    }
    
    public Vector3f getOrigen() {
        return origen;
    }
    
    public ArrayList<Vector3f> getMedidas() {
        return medidas;
    }
    
    public Quaternion getRotacionInicial() {
        return rotacionInicial;
    }
    
    public String getSceneModel() {
        return sceneModel;
    }
    
    public String getParets() {
        return paretsInvisibles;
    }
    
    public ArrayList<Vector3f> getListaLuces() {
        return listaLuces;
    }
    
    public ArrayList<Vector3f> getListaCajas() {
        return listaCajas;
    }
    
    public ArrayList<Vector3f> getListaMuros() {
        return listaMuros;
    }
    
    public float getEscala(){
        return escala;
    }
    
    public void mostrarMapa() {
        
    }
    
}
