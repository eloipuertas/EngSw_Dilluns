package model;

import com.jme3.math.Vector3f;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

/**
 *
 * @author davidsanchezpinsach
 */
public class CheckPoints {
    private final int world1 = 0;
    private final int world2 = 1;
    private final int world3 = 2;
        
    private String puntsVoltaData;
    private ArrayList<Vector3f> llistaControlVolta = new ArrayList<Vector3f>();;
    
    public CheckPoints(int idWorld){
        loadTxt(idWorld);
        llegirPuntsControlVolta(puntsVoltaData);
    }
    
    private void loadTxt(int idWorld){
        switch(idWorld){
            case world1:
                puntsVoltaData="assets/CheckPoints/puntos_de_control_mapa1.txt";
                break;
            case world2:
                puntsVoltaData="assets/CheckPoints/puntos_de_control_mapa2.txt";
                break;
            case world3:
                puntsVoltaData="assets/CheckPoints/puntos_de_control_mapa3.txt";
                break;
        }
    }
    
    private void llegirPuntsControlVolta(String puntsVolta) {
        try {
            float[] punt;
            punt = new float[3];
            FileReader fr = new FileReader(puntsVolta);

            Scanner scanner = new Scanner(fr);
            BufferedReader bf = new BufferedReader(fr);
            while (scanner.hasNextLine()) {
                Scanner linia = new Scanner(scanner.nextLine());
                linia.useDelimiter(" ");

                if(linia.hasNext()){
                    for(int i=0;i<=2;i++){
                        punt[i]=Float.valueOf(linia.next());
                    }
                    Vector3f p =new Vector3f(punt[0],punt[1],punt[2]);
                    llistaControlVolta.add(p); 
                } 
            } 
        } catch (FileNotFoundException fnfe){
                fnfe.printStackTrace();
        } catch (IOException ioe){
                ioe.printStackTrace();      
        }
    }
    
    public Vector3f buscaPuntControlVolta (int numPunt) {
        Iterator it = llistaControlVolta.iterator();
        int i=0;
        Vector3f p=new Vector3f();
        if(numPunt == llistaControlVolta.size()+1){
            numPunt = 1;
        }
        while(it.hasNext() && i!=numPunt){
            p=(Vector3f)it.next();
            i++;
        }
        return p;
    }
    
    public ArrayList<Vector3f> getLlistaControlVolta(){
        return llistaControlVolta;
    }
}
