/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;

/**
 *
 * @author JAC
 */
public class ListaMapas {
    private ArrayList<Mapa> mapas;
    
    public ListaMapas() {
        mapas = new ArrayList<Mapa>();
    }
    
    public void añadirMapa(Mapa m) {
        mapas.add(m);
    }
    
    public Mapa getMapa(int i) {
        return mapas.get(i);
    }
    
    public void MostarMapa(int i) {
        mapas.get(i).mostrarMapa();
    }
}
