/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.Quaternion;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Iterator;

public class Rival {
    //Atributs de la classe, s'ha d'anar ampliant
    private ArrayList<Vector3f> llistaWaitPoints;
    private ArrayList<Float> llistaAccioWaitPoints;  
    private ArrayList<Vector3f> llistaControlVolta;
    private int numVoltes;
    private int numWaitPoints; //Num de waitPoints de l'escenari
    private int numPuntsControlVolta;
    private float mass;
    private VehicleControl vehicle;
    private Geometry chasis1;
    private Geometry wheel1;
    private Geometry wheel3;
    private Geometry wheel2;
    private Geometry wheel4;
    private float wheelRadius;
    private AssetManager assetManager;
    private CameraNode camNode;
    public Node vehicleNode;
    private PhysicsSpace physicsSpace;
    
    public WorldCreator world;
    
    private int estat;
    private int estatControlVolta;
    private float angle;                /*utilitzat per a calcular direccions*/
    private boolean errorDreta= false;       //per a controlar el error de les rodes en el vector direccio
    private boolean errorEsquerra=false;
    private boolean errorRectaDreta= false;       //per a controlar el error de les rodes en el vector direccio
    private boolean errorRectaEsquerra=false;
    private boolean enMoviment= false;  //per a controlar els resets
    private boolean pasPuntFinal= false;        //per a controlar que segueixi recitifcant quan toca
    private boolean rectificarRectaAEsquerra=false;
    private boolean rectificarRectaADreta=false;
    private Vector3f puntInici;
    private Quaternion rotInici;
    private int nivellIA;
    private Vector3f puntFinal;
    private Vector3f puntSeguent;
    private Vector3f puntAnterior;
    private Vector3f puntControlVolta;
    private boolean partidaComensada=false;
    private String IAdata;
    private String puntsVoltaData;
    private int idEscenari;
    private float tipusRecta; /* 1 accelerar-frenar 2 manten velocitat*/
    private float velocitatRecta; /* depen de la velocitat anterior i el itpus de recta variara a vel. de curva i vel. de recta*/
    private int posicioCarrera;
    
    //Constructor 
    public Rival(AssetManager asset, PhysicsSpace phy,int idCircuit, Quaternion rot,int nivell){          /*la idea es passar el world on contingi a la llarga les coordenades del mon*/
        llistaWaitPoints = new ArrayList<Vector3f>();
        llistaAccioWaitPoints = new ArrayList<Float>();
        llistaControlVolta = new ArrayList<Vector3f>();
        numVoltes=-1;
        posicioCarrera=2;
        assetManager = asset;
        physicsSpace = phy;
        idEscenari = idCircuit;
        rotInici=rot;
        nivellIA=nivell;
        buildCar();
        //Carreguem l'arxiu que toca segons el nivell de dificultat aceptat     
        if (nivellIA==1){IAdata = "IAWaitPoints/"+String.valueOf(idEscenari)+"/IAdata1.txt";}
        if (nivellIA==2){IAdata = "IAWaitPoints/"+String.valueOf(idEscenari)+"/IAdata2.txt";}
        if (nivellIA==3){IAdata = "IAWaitPoints/"+String.valueOf(idEscenari)+"/IAdata3.txt";}
        puntsVoltaData="IAWaitPoints/"+String.valueOf(idEscenari)+"/puntsControlVolta.txt";
        numWaitPoints = llegirPunts(IAdata,puntsVoltaData);              //Llegim els punts dels escenaris i els carreguem
        situar_graella(puntInici,rot);      /* inclueix el build car i situarlo correctament*/

        canviaEstat(1);
        canviaEstatControlVolta(1);
    }
  
//Metode per llegir els punts d'un fitxer i guardar-los en la llista corresponent
    private int llegirPunts(String IAdata,String puntsVolta) {
    
        try {
            float[] punt;
            punt = new float[3];
            FileReader fr = new FileReader(IAdata);
            FileReader fr2 = new FileReader(puntsVolta);

            Scanner scanner = new Scanner(fr);
            BufferedReader bf = new BufferedReader(fr);
            if (scanner.hasNextLine()) {
                    Scanner linia = new Scanner(scanner.nextLine());
                    linia.useDelimiter(" ");
                        
                    if(linia.hasNext()){
                        for(int i=0;i<=2;i++){
                            punt[i]=Float.valueOf(linia.next());
                        }                     
                        puntInici =new Vector3f(punt[0],punt[1],punt[2]);
                   } 
            }
            while (scanner.hasNextLine()) {
                    Scanner linia = new Scanner(scanner.nextLine());
                    linia.useDelimiter(" ");
                        
                    if(linia.hasNext()){
                        for(int i=0;i<=2;i++){
                            punt[i]=Float.valueOf(linia.next());
                        }
                        llistaAccioWaitPoints.add(Float.valueOf(linia.next()));                       
                        Vector3f p =new Vector3f(punt[0],punt[1],punt[2]);
                        llistaWaitPoints.add(p); 
                    } 
            }
            fr.close();
            Scanner scanner2 = new Scanner(fr2);
            BufferedReader bf2 = new BufferedReader(fr2);
            while (scanner2.hasNextLine()) {
                    Scanner linia = new Scanner(scanner2.nextLine());
                    linia.useDelimiter(" ");
                        
                    if(linia.hasNext()){
                        for(int i=0;i<=2;i++){
                            punt[i]=Float.valueOf(linia.next());
                        }
                        Vector3f p =new Vector3f(punt[0],punt[1],punt[2]);
                        llistaControlVolta.add(p); 
                    } 
            }
            numPuntsControlVolta=llistaControlVolta.size();
            return llistaWaitPoints.size();
            
        } catch (FileNotFoundException fnfe){
                fnfe.printStackTrace();
                return -1;
        } catch (IOException ioe){
                ioe.printStackTrace();
                return -1;
        }
}

    public void moureRival () {
            rutina();
    }
    public Vector3f getPuntInici () {
        return puntInici;
    }
    private Geometry findGeom(Spatial spatial, String name) {
        if (spatial instanceof Node) {
            Node node = (Node) spatial;
            for (int i = 0; i < node.getQuantity(); i++) {
                Spatial child = node.getChild(i);
                Geometry result = findGeom(child, name);
                if (result != null) {
                    return result;
                }
            }
        } else if (spatial instanceof Geometry) {
            if (spatial.getName().startsWith(name)) {
                return (Geometry) spatial;
            }
        }
        return null;
    }
    
    private void buildCar() {
        mass = 400;
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        //mat.getAdditionalRenderState().setWireframe(true);
        mat.setColor("Color", ColorRGBA.Blue);
<<<<<<< HEAD
        Node meshNode = (Node) assetManager.loadModel("Models/Cars/tempCar/Car.scene");
=======
        Node meshNode = (Node) assetManager.loadModel("Models/tempCar/Car.scene");
>>>>>>> origin/Grup-D
        
        chasis1 = findGeom(meshNode,"Car");
        chasis1.rotate(0, 3.135f, 0);
        chasis1.setMaterial(mat);
        
        CollisionShape carHull = CollisionShapeFactory.createDynamicMeshShape(chasis1);
        BoundingBox box = (BoundingBox) chasis1.getModelBound();


        //create vehicle node
        vehicleNode = new Node("vehicleNode");
        vehicle = new VehicleControl(carHull, mass);
        vehicleNode.addControl(vehicle);       
        vehicleNode.attachChild(chasis1);
        
        //setting suspension values for wheels, this can be a bit tricky
        //see also https://docs.google.com/Doc?docid=0AXVUZ5xw6XpKZGNuZG56a3FfMzU0Z2NyZnF4Zmo&hl=en
        float stiffness = 60.0f;//200=f1 car
        float compValue = .3f; //(should be lower than damp)
        float dampValue = .4f;
        vehicle.setSuspensionCompression(compValue * 10.0f * FastMath.sqrt(stiffness));
        vehicle.setSuspensionDamping(dampValue * 2.0f * FastMath.sqrt(stiffness));
        vehicle.setSuspensionStiffness(stiffness);
        vehicle.setMaxSuspensionForce(10000.0f);
        
        //Create four wheels and add them at their locations
        Vector3f wheelDirection = new Vector3f(0, -1, 0); // was 0, -1, 0
        Vector3f wheelAxle = new Vector3f(-1, 0, 0); // was -1, 0, 0
        float radius = 0.5f;
        float restLength = 0.3f;
        float yOff = 0.5f;
        float xOff = 1f;
        float zOff = 2f;

        Node node1 = new Node("wheel 1 node");
        wheel1 = findGeom(meshNode, "WheelFrontRight");
        wheel1.setMaterial(mat);
        node1.attachChild(wheel1);
        wheel1.center();
        vehicle.addWheel(node1, new Vector3f(-xOff, yOff, zOff),
                wheelDirection, wheelAxle, restLength, radius, true);

        Node node2 = new Node("wheel 2 node");
        wheel2 = findGeom(meshNode, "WheelFrontLeft");
        wheel2.setMaterial(mat);
        node2.attachChild(wheel2);
        wheel2.center();
        vehicle.addWheel(node2, new Vector3f(xOff, yOff, zOff),
                wheelDirection, wheelAxle, restLength, radius, true);

        Node node3 = new Node("wheel 3 node");
        wheel3 = findGeom(meshNode, "WheelBackRight");
        wheel3.setMaterial(mat);
        node3.attachChild(wheel3);
        wheel3.center();
        vehicle.addWheel(node3, new Vector3f(-xOff, yOff, -zOff),
                wheelDirection, wheelAxle, restLength, radius, false);

        Node node4 = new Node("wheel 4 node");
        wheel4 = findGeom(meshNode, "WheelBackLeft");
        wheel4.setMaterial(mat);
        node4.attachChild(wheel4);
        wheel4.center();
        vehicle.addWheel(node4, new Vector3f(xOff, yOff, -zOff),
                wheelDirection, wheelAxle, restLength, radius, false);

        vehicleNode.attachChild(node1);
        vehicleNode.attachChild(node2);
        vehicleNode.attachChild(node3);
        vehicleNode.attachChild(node4);
        
        vehicle.getWheel(0).setFrictionSlip(9.8f);
        vehicle.getWheel(1).setFrictionSlip(9.8f);
              
        physicsSpace.add(vehicle);
    }
    
    private void situar_graella (Vector3f pto,Quaternion rot) {
        this.reset_rival();
        this.getVehicle().accelerate(1.f);  /*necessari pk te velocitat negativa involuntariament i intrepreta k ha de rectificar girant*/
    }
    
    //Getters i setters :
    //#################################################################
    
    //Getter del cotxe en si, s'utilitza per a obtenir-lo desde el main
    public Spatial getSpatial(){           /*conte el cotxe en si com a conjunt d'objectes geometrics*/
        return (Spatial)vehicleNode;
    }
    public VehicleControl getVehicle() {       /* conte tota la llibreria de les fisiques que apliquem*/
        return vehicle;
    }
    
    public float getVelocitat(){                               /*retornem la velocitat de la direccio de les coordenades en valor unic*/
        return this.getVehicle().getLinearVelocity().length();
    }
    
    public Vector3f getPosicio() {
        return this.vehicle.getPhysicsLocation();
    }
    
    public void setPosicioCarrera(int p){
        posicioCarrera=p;
    }
    
    public int getNumVoltes(){
        return numVoltes;
    }
    public int getEstatControlVolta(){
        return estatControlVolta;
    }
    public float getDistanciaEstatControlVolta() {
        return getDistancia(puntControlVolta);
    }

     /*Metode per comprovar que el cotxe protagonista esta en moviment*/
    public void setPartidaComensada (boolean x){
        partidaComensada=x;  
    }
    public boolean comprovaPartidaComensada (){
        if (partidaComensada==true) {
            return true;
        }else{
            return false;
        }
    }
    //#################################################################
    //Metodes per a moure el cotxe de forma aleatoria (probablement no vagin aqui)
    //---------------------------------------------------
    
    //Mètodes de moviment bàsic, endavant, endarrere, esquerra i dreta :
    
    private void moureEndavant(float velocitat){           /* si no te cap desviacio a la direccio utilitzarem aquesta funcio*/
        vehicle.brake(0f);
        if (getVelocitat()<velocitat && getVelocitat()>2) {      /*si el cotxe va mes lent de 15 accelerem*/
            vehicle.accelerate(800.0f);
            enMoviment=true;
        } else if (getVelocitat()<2){
            vehicle.accelerate(800.0f);
        } else if (getVelocitat()+5>velocitat) {
            vehicle.accelerate(0);
            vehicle.brake(300.f);
        } else {
            vehicle.accelerate(0);
        }
    }
    

    private void girarCurva(int curva){ /*eskerra 1 dreta 2*/
        if (getVelocitat()>2f) {
                enMoviment=true;
        }
        if (this.getVelocitat()<70 && this.getVelocitat()>15) {
            vehicle.accelerate(0);
            vehicle.brake(300.0f);
        } else if (this.getVelocitat()>=4 && this.getVelocitat()<=15){
            if(curva==1) {
                vehicle.steer(+.5f);
            } else if (curva==2) {
                vehicle.steer(-.5f);
            } else {
                System.err.println("No hackees porfavor");
            }
            vehicle.brake(0f);
        }else{
            vehicle.accelerate(800f);
            vehicle.brake(0f);
        }  
    }
    
    public float getDistancia (Vector3f pto) { /*busquem la distancia del rival al pto del parametre*/
        Vector3f posRival= this.getPosicio();
        float distancia= pto.distance(posRival);
        return distancia;
    }
    private void rectificarDesviacioEsquerra (int estatAnterior,Vector3f pto,boolean seguent) { /* a la llarga es podra unificar els dos metodes de rectificar afegint per parametre dreta o eskerra*/
        float angleActual = calcular_angle_direccions(pto); /*nou angle de desviament mentre's girem*/
        if ((errorEsquerra==false) && (angleActual<355.f && angleActual > 5.f)){ 
        /*el error es un error imposible d'evitar ja que quan calcules la direccio del cotxe medeix la direccio de les rodes i si estic girant s'incrementa inevitablement amb 10º aproximadament*/
                girarCurva(2); //dreta   
        /*per lo tant girarem fins a arribar a 0º i despres continuarem girant fins a aconseguir el error calculat a ull*/
        } else {
            /*aqui es suposa que està rectificant el error de les rodes ja que està casi encarat del tot*/
            errorEsquerra=true;
            girarCurva(2);
            if (angleActual<353.f && angleActual > 7.f){           
                /*si hem aconseguit corregir l'error parem de girar i avisem que ja no estem modificant la direccio*/
                rectificarRectaAEsquerra=false; /*estiguem rectificant una recta o girant una curva avisem que ja estem*/
                rectificarRectaADreta=false;
                if (seguent==true) {    /*si es tracta d'una curva anem al estat seguent*/
                    pasPuntFinal= false;    /*aquesta variable controla si hem pogut arribar al punt de desti*/
                    canviaEstat(estatAnterior+1);   
                }
                vehicle.steer(0.f);
                errorEsquerra=false;
            } 
        }  
    }

    private void rectificarDesviacioDreta (int estatAnterior,Vector3f pto,boolean seguent) {
        float angleActual = calcular_angle_direccions(pto); /*nou angle despres de girar*/
        if ((errorDreta==false) && (angleActual<355.f && angleActual > 5.f)){
                girarCurva(1); //eskerra    
        } else {
            errorDreta=true;
            girarCurva(1);
            if (angleActual<353.f && angleActual > 7.f){  
                rectificarRectaAEsquerra=false; /*estiguem rectificant una recta o girant una curva avisem que ja estem*/
                rectificarRectaADreta=false;
                if (seguent==true) {    /*si es tracta d'una curva anem al estat seguent*/
                    pasPuntFinal= false;    /*aquesta variable controla si hem pogut arribar al punt de desti*/
                    canviaEstat(estatAnterior+1);        
                }
                vehicle.steer(0.f); /*deixem de girar i reinciciem el error de rodes*/
                errorDreta=false;
            }        
        }    
    }
private void rectificarDesviacioRectaEsquerra (int estatAnterior,Vector3f pto,boolean seguent) { /* a la llarga es podra unificar els dos metodes de rectificar afegint per parametre dreta o eskerra*/
        float angleActual = calcular_angle_direccions(pto); /*nou angle de desviament mentre's girem*/
        if ((errorRectaEsquerra==false) && (angleActual<355.f && angleActual > 5.f)){ 
        /*el error es un error imposible d'evitar ja que quan calcules la direccio del cotxe medeix la direccio de les rodes i si estic girant s'incrementa inevitablement amb 10º aproximadament*/
                girarCurva(2); //dreta   
        /*per lo tant girarem fins a arribar a 0º i despres continuarem girant fins a aconseguir el error calculat a ull*/
        } else {
            /*aqui es suposa que està rectificant el error de les rodes ja que està casi encarat del tot*/
            errorRectaEsquerra=true;
            girarCurva(2);    
            if (angleActual<353.f && angleActual > 7.f){           
                /*si hem aconseguit corregir l'error parem de girar i avisem que ja no estem modificant la direccio*/
                rectificarRectaAEsquerra=false; /*estiguem rectificant una recta o girant una curva avisem que ja estem*/
                rectificarRectaADreta=false;
                if (seguent==true) {    /*si es tracta d'una curva anem al estat seguent*/
                    pasPuntFinal= false;    /*aquesta variable controla si hem pogut arribar al punt de desti*/
                    canviaEstat(estatAnterior+1);   
                }
                vehicle.steer(0.f);
                errorRectaEsquerra=false;
            } 
        }  
    }

    private void rectificarDesviacioRectaDreta (int estatAnterior,Vector3f pto,boolean seguent) {
        float angleActual = calcular_angle_direccions(pto); /*nou angle despres de girar*/
        if ((errorRectaDreta==false) && (angleActual<355.f && angleActual > 5.f)){
                girarCurva(1); //eskerra    
        } else {
            errorRectaDreta=true;
            girarCurva(1);
            if (angleActual<353.f && angleActual > 7.f){  
                rectificarRectaAEsquerra=false; /*estiguem rectificant una recta o girant una curva avisem que ja estem*/
                rectificarRectaADreta=false;
                if (seguent==true) {    /*si es tracta d'una curva anem al estat seguent*/
                    pasPuntFinal= false;    /*aquesta variable controla si hem pogut arribar al punt de desti*/
                    canviaEstat(estatAnterior+1);        
                }
                vehicle.steer(0.f); /*deixem de girar i reinciciem el error de rodes*/
                errorRectaDreta=false;
            }        
        }    
    }
    
    private Vector3f buscaPunt(int numPunt){
        Iterator it = llistaWaitPoints.iterator();
        int i=0;
        Vector3f p=new Vector3f();
        if(numPunt == llistaWaitPoints.size()+1){
            numPunt = 1;
        }
        if (numPunt == llistaWaitPoints.size()+2){
            numPunt = 2;
        }
        if (numPunt == 0) {
            numPunt = llistaWaitPoints.size();
        }
        while(it.hasNext() && i!=numPunt){
            p=(Vector3f)it.next();
            i++;
        }
        return p; 
    }
    
    private Vector3f buscaPuntControlVolta (int numPunt) {
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
    
    //Mètode per canviar l'estat i recalcular els punts del mateix
    private void canviaEstat(int estatFutur){
        Iterator it;
        int i=0;
        float f=2;
        estat = estatFutur;
        puntAnterior = buscaPunt(estat-1);
        puntFinal = buscaPunt(estat);
        puntSeguent = buscaPunt(estat+1);
        if(estat==llistaWaitPoints.size()+1){estat =1;}
        
        it = llistaAccioWaitPoints.iterator();
        while(it.hasNext() && i != estatFutur){
            f=(Float)it.next();
            i++;
        }
        tipusRecta=f;
        errorEsquerra=false;
        errorDreta=false;
    }
    
    private void canviaEstatControlVolta(int estatFutur) {
        Iterator it;
        int i=0;
        float f=2;
        estatControlVolta = estatFutur;
        if(estatControlVolta==llistaControlVolta.size()+1){
            estatControlVolta =1;
            numVoltes++;
        }
        puntControlVolta = buscaPuntControlVolta(estatControlVolta);   
    }
    
    public float calcular_angle_direccions (Vector3f pto) { /*calcula el angle entre el rival i la direccio ideal*/
        Vector2f dirActual = new Vector2f(0.f,0.f); /*direccio del rival utilitzat per a ser mes precis*/
        Vector2f dirIdeal = new Vector2f(0.f,0.f);  //direccio a la que a de encararse
        dirActual.setX(this.getVehicle().getLinearVelocity().getX());
        dirActual.setY(this.getVehicle().getLinearVelocity().getZ());
        dirActual = dirActual.normalize();
        dirIdeal.setX(pto.getX()-this.getPosicio().getX()); 
        dirIdeal.setY(pto.getZ()-this.getPosicio().getZ());
        dirIdeal=dirIdeal.normalize();        
        float angleRadians= dirActual.angleBetween(dirIdeal);
        angleRadians= (angleRadians*180.f)/(float)Math.PI;
        if (angleRadians<0.f) {
            return 360.f+angleRadians;
        } else {
            return angleRadians;
        }
    }
    
    public void reset_rival() { /*resetejem el rival al punt de partida en cas de que es quedi bloquejat*/
        vehicle.setPhysicsLocation(puntInici);
        vehicle.setPhysicsRotation(rotInici);
        vehicle.setLinearVelocity(new Vector3f(0f,0f,0.1f));    /*al reiniciar el coche inevitablement tira enderrere molt molt poc i el vector direccio es 180 i no interesa i ho corregim*/
        vehicle.setAngularVelocity(Vector3f.ZERO);
        vehicle.resetSuspension();
        angle=0f;
        enMoviment=false;
        pasPuntFinal= false;
        errorDreta=false;
        errorEsquerra=false;
        canviaEstat(1);
        vehicle.steer(0.f);
        rectificarRectaAEsquerra=false;
        rectificarRectaADreta=false;
        canviaEstatControlVolta(1);
    }

    private void rutina() {  /* cada cas representa una recta*/
         if(getVelocitat()>60f) {
            reset_rival();
        }
        angle = calcular_angle_direccions(puntFinal);
        if (vehicle.getLinearVelocity().length()<2 && enMoviment==true) {
            reset_rival();
        }
<<<<<<< HEAD
        if (getDistancia(puntControlVolta)<=12) {
=======
        if (getDistancia(puntControlVolta)<=20) {
>>>>>>> origin/Grup-D
            canviaEstatControlVolta(estatControlVolta+1);
        }
        if (this.getDistancia(puntFinal)<=8.f || pasPuntFinal==true) {     /*si hem arribat a la curva o pto de control girem al seguent punt*/
            rectificarRectaADreta=false;    /*si estem rectificant pero arribaem ala curva ja no ho posem a false perke no rectiki a la cuirva actual sino a la seguent*/
            rectificarRectaAEsquerra=false;
            errorRectaDreta=false;
            errorRectaEsquerra=false;
            pasPuntFinal= true;         /*hem arribat  ala curva si ens passem de larea del pto de control seguirem girant*/
            angle = calcular_angle_direccions(puntSeguent);
            if ((angle<=180 && errorDreta==false)|| errorEsquerra==true) {
                rectificarDesviacioEsquerra(estat,puntSeguent,true);
            } else if ((angle > 180 && errorEsquerra==false)|| errorDreta==true) {
                rectificarDesviacioDreta(estat,puntSeguent,true);
            }    
        } else if ((angle>15.f && pasPuntFinal == false && angle<179.f) || (rectificarRectaADreta==true)) { /*si encara no hem arribat al pto de control i ens desviem rectifiquem*/
            rectificarRectaADreta=true;
            rectificarDesviacioRectaEsquerra(estat,puntFinal,false);
        } else if ((angle<345.f && pasPuntFinal == false && angle>=180.f) || (rectificarRectaAEsquerra==true)) {
            rectificarRectaAEsquerra=true;
            rectificarDesviacioRectaDreta(estat,puntFinal,false);
        } else {            /*sino hem arribat al pto de control i anem amb la direccio correcta ens movem endavant*/
            if (tipusRecta==2) {
                if (getDistancia(puntAnterior)<getDistancia(puntFinal)){
                    velocitatRecta=40; 
                } else {
                    velocitatRecta=15;    
                }
                moureEndavant(velocitatRecta);
            } else if (tipusRecta==1) {
                velocitatRecta=40;
                moureEndavant(40);
            }
            
        }     
    }
}