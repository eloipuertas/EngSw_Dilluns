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
    private int numWaitPoints; //Num de waitPoints de l'escenari
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
    
    private int estat=1;
    private float angle;                /*utilitzat per a calcular direccions*/
    private boolean errorDreta= false;       //per a controlar el error de les rodes en el vector direccio
    private boolean errorEsquerra=false;
    private boolean enMoviment= false;  //per a controlar els resets
    private boolean pasPuntFinal= false;        //per a controlar que segueixi recitifcant quan toca
    private boolean rectificarRectaAEsquerra=false;
    private boolean rectificarRectaADreta=false;
    private Vector3f puntInici;
    private Quaternion rotInici;
    private int nivellIA;
    private Vector3f puntFinal;
    private Vector3f puntSeguent;
    private boolean partidaComensada=false;
    private String IAdata;
    private int idEscenari;
    
    //Constructor
    public Rival(AssetManager asset, PhysicsSpace phy,int idCircuit,Vector3f punt, Quaternion rot,int nivell){          /*la idea es passar el world on contingi a la llarga les coordenades del mon*/
        llistaWaitPoints = new ArrayList<Vector3f>();
        assetManager = asset;
        physicsSpace = phy;
        idEscenari = idCircuit;
        rotInici=rot;
        Vector3f x = punt;
        x.setZ(x.getZ()+5f);
        x.setY(-4.8f);
        puntInici= x;
        nivellIA=nivell;
        buildCar();
        situar_graella(x,rot);      /* inclueix el build car i situarlo correctament*/
        //Carreguem l'arxiu que toca segons el nivell de dificultat aceptat
        
        if (nivellIA==1){IAdata = "IAWaitPoints/"+String.valueOf(idEscenari)+"/IAdata1.txt";}
        if (nivellIA==2){IAdata = "IAWaitPoints/"+String.valueOf(idEscenari)+"/IAdata2.txt";}
        numWaitPoints = llegirPunts(IAdata);              //Llegim els punts dels escenaris i els carreguem
        puntSeguent=buscaPunt(2);
        puntFinal=buscaPunt(1);
    }
      
//Metode per llegir els punts d'un fitxer i guardar-los en la llista corresponent
    private int llegirPunts(String IAdata) {
    
        try {
            float[] punt;
            punt = new float[3];
            FileReader fr = new FileReader(IAdata);
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
                        llistaWaitPoints.add(p); 
                    } 
            }
            fr.close();
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
         /*
         Material matW = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
         matW.getAdditionalRenderState().setWireframe(true);
         matW.setColor("Color", ColorRGBA.Black);
*/
        Node meshNode = (Node) assetManager.loadModel("Models/tempCar/Car.scene");
        
        chasis1 = findGeom(meshNode,"Car");
        chasis1.rotate(0, 3.135f, 0);
        chasis1.setMaterial(mat);
        
        CollisionShape carHull = CollisionShapeFactory.createDynamicMeshShape(chasis1);
        BoundingBox box = (BoundingBox) chasis1.getModelBound();


        //create vehicle node
        vehicleNode = new Node("vehicleNode");
        vehicle = new VehicleControl(carHull, mass);
        vehicleNode.addControl(vehicle);
        //vehicleNode.setMaterial(mat);
        
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
        
        //rootNode.attachChild(vehicleNode);
        
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
    
    private void moureEndavant(){           /* si no te cap desviacio a la direccio utilitzarem aquesta funcio*/
        vehicle.brake(0f);
        if (getVelocitat()<15 && getVelocitat()>2) {      /*si el cotxe va mes lent de 15 accelerem*/
            vehicle.accelerate(800.0f);
            enMoviment=true;
        } else if (getVelocitat()<2){
            vehicle.accelerate(800.0f);
        } else {
            //System.out.println(vehicle.getLinearVelocity().length());
            vehicle.accelerate(0);
        }   
    }
    

    private void girarCurva(int curva){ /*eskerra 1 dreta 2*/
        if (getVelocitat()>2f) {
                enMoviment=true;
        }
        if (this.getVelocitat()<70 && this.getVelocitat()>15) {
           //System.out.println("frenant");
            vehicle.accelerate(0);
            vehicle.brake(100.0f);
            //System.out.println(vehicle.getLinearVelocity().length());
        } else if (this.getVelocitat()>=4 && this.getVelocitat()<=15){
            //System.out.println("girant");
            if(curva==1) {
                vehicle.steer(+.5f);
            } else if (curva==2) {
                vehicle.steer(-.5f);
            } else {
                System.err.println("No hackees porfavor");
            }
            vehicle.brake(0f);
        }else{
            //System.out.println("accelerant");
            vehicle.accelerate(800f);
            vehicle.brake(0f);
        }  
    }
    
    public float getDistancia (Vector3f pto) { /*busquem la distancia del rival al pto del parametre*/
        Vector3f posRival= this.getPosicio();
        float distancia= pto.distance(posRival);
        return distancia;
    }
    public void rectificarDesviacioEsquerra (int estatAnterior,Vector3f pto,boolean seguent) { /* a la llarga es podra unificar els dos metodes de rectificar afegint per parametre dreta o eskerra*/
        float angleActual = calcular_angle_direccions(pto); /*nou angle de desviament mentre's girem*/
        //System.out.println("angleeeee="+angleActual);
        if ((errorEsquerra==false) && (angleActual<355.f && angleActual > 5.f)){ 
        /*el error es un error imposible d'evitar ja que quan calcules la direccio del cotxe medeix la direccio de les rodes i si estic girant s'incrementa inevitablement amb 10º aproximadament*/
                //System.out.println("angleeeee ="+angleActual);
                girarCurva(2); //dreta   
        /*per lo tant girarem fins a arribar a 0º i despres continuarem girant fins a aconseguir el error calculat a ull*/
        } else {
            /*aqui es suposa que està rectificant el error de les rodes ja que està casi encarat del tot*/
            //System.out.println("ANGLEEEEE error ="+angleActual);
                errorEsquerra=true;
            if (angleActual<353.f && angleActual > 7.f){           
                /*si hem aconseguit corregir l'error parem de girar i avisem que ja no estem modificant la direccio*/
                rectificarRectaAEsquerra=false; /*estiguem rectificant una recta o girant una curva avisem que ja estem*/
                rectificarRectaADreta=false;
                if (seguent==true) {    /*si es tracta d'una curva anem al estat seguent*/
                    pasPuntFinal= false;    /*aquesta variable controla si hem pogut arribar al punt de desti*/
                   // System.out.println("error ULTIIIIIM");
                   canviaEstat(estatAnterior+1);
                    
                 
                }
                vehicle.steer(0.f);
                errorEsquerra=false;
            }
            //System.out.println("rectificant error de rodeeees");        
        }  
    }
    
    private Vector3f buscaPunt(int numPunt){
        Iterator it = llistaWaitPoints.iterator();
        int e=0;
       
        Vector3f p=new Vector3f();
        if(numPunt == llistaWaitPoints.size()+1){
            numPunt=1;
        }
        if (numPunt == llistaWaitPoints.size()+2){
            numPunt=2;
        }
        while(it.hasNext() && e!=numPunt){
            p=(Vector3f)it.next();
            e++;
        }
        return p; 
    }
    //Mètode per canviar l'estat i recalcular els punts del mateix
    private void canviaEstat(int estatFutur){
            estat = estatFutur;
            puntFinal = buscaPunt(estat);
            puntSeguent = buscaPunt(estat+1);
            if(estat ==9){estat =1;}          
        
    }
    public void rectificarDesviacioDreta (int estatAnterior,Vector3f pto,boolean seguent) {
        float angleActual = calcular_angle_direccions(pto); /*nou angle despres de girar*/
        //System.out.println("angleeeee="+angleActual);
        if ((errorDreta==false) && (angleActual<355.f && angleActual > 5.f)){
                //System.out.println("angleeeee ="+angleActual);
                girarCurva(1); //eskerra    
        } else {
            //System.out.println("Rectificant l'error que  provoca les rodes al estar girades");    
            //System.out.println("Angle dins derror ="+angleActual);
            errorDreta=true;       
            if (angleActual<353.f && angleActual > 7.f){  
                rectificarRectaAEsquerra=false; /*estiguem rectificant una recta o girant una curva avisem que ja estem*/
                rectificarRectaADreta=false;
                if (seguent==true) {    /*si es tracta d'una curva anem al estat seguent*/
                    pasPuntFinal= false;    /*aquesta variable controla si hem pogut arribar al punt de desti*/
                    //System.out.println("Ultima lectura d'angle.");
                    canviaEstat(estatAnterior+1); 
                    
                }
                vehicle.steer(0.f); /*deixem de girar i reinciciem el error de rodes*/
                errorDreta=false;
            }        
        }    
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
        //System.out.println("Reset Rival");
    }
    
    private void rutina() {  /* cada cas representa una recta*/
         if(getVelocitat()>30f) {
            reset_rival();
        }
        angle = calcular_angle_direccions(puntFinal);
        //System.out.println(puntFinal);
        //System.out.println(getVelocitat());
        if (vehicle.getLinearVelocity().length()<2 && enMoviment==true) {
            reset_rival();
        }
        if (this.getDistancia(puntFinal)<=10.f || pasPuntFinal==true) {     /*si hem arribat a la curva o pto de control girem al seguent punt*/
            rectificarRectaADreta=false;    /*si estem rectificant pero arribaem ala curva ja no ho posem a false perke no rectiki a la cuirva actual sino a la seguent*/
            rectificarRectaAEsquerra=false;
            pasPuntFinal= true;         /*hem arribat  ala curva si ens passem de larea del pto de control seguirem girant*/
            angle = calcular_angle_direccions(puntSeguent);
            //System.out.println("Hem arribat al punt de control del punt "+estat+" i començarem a girar");
            if ((angle<=180 && errorDreta==false)|| errorEsquerra==true) {
                //System.out.println("Girem a la dreta al punt "+estat+" cap al punt "+(estat+1));
                rectificarDesviacioEsquerra(estat,puntSeguent,true);
            } else if ((angle > 180 && errorEsquerra==false)|| errorDreta==true) {
                //System.out.println("Girem a la esquerra al punt "+estat+" cap al punt "+(estat+1));
                rectificarDesviacioDreta(estat,puntSeguent,true);
            }    
        } else if ((angle>11.f && pasPuntFinal == false && angle<179.f) || (rectificarRectaADreta==true)) { /*si encara no hem arribat al pto de control i ens desviem rectifiquem*/
            rectificarRectaADreta=true;
            //System.out.println("Rectifiquem a la recta del punt "+estat+"  girant a la dreta");
            rectificarDesviacioEsquerra(estat,puntFinal,false);
        } else if ((angle<349.f && pasPuntFinal == false && angle>=181.f) || (rectificarRectaAEsquerra==true)) {
            rectificarRectaAEsquerra=true;
            //System.out.println("Rectifiquem a la recta del punt "+estat+"  girant a la esquerra");
            rectificarDesviacioDreta(estat,puntFinal,false);
        } else {            /*sino hem arribat al pto de control i anem amb la direccio correcta ens movem endavant*/
            moureEndavant();
        }
        
    }
    


}
