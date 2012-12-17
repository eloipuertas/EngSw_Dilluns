package model;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import java.util.Random;

public class LlistaReproduccio {
    private Node rootNode;
    private AssetManager assetManager;
    private String audioNames[];
    private Audio audios[];
    private int quantAudios;
    private int audioActual = -1;
    private boolean haReproduit[];
    private boolean permetRepeticio = false;
    private Random random;
    private boolean hanCarregat = false;
    private float volumes[];
    private boolean teVolums = false;
    private boolean streamed = false;
    
    public LlistaReproduccio(Node rootNode, AssetManager assetManager, String audioNames[]) {
        this.rootNode = rootNode;
        this.assetManager = assetManager;
        this.audioNames = audioNames;
        quantAudios = this.audioNames.length;
        random = new Random();
        audios = new Audio[quantAudios];
        initLlistaSenseRepeticio();
    }
    
    public LlistaReproduccio(boolean streamed, Node rootNode, AssetManager assetManager, String audioNames[]) {
        this.rootNode = rootNode;
        this.assetManager = assetManager;
        this.audioNames = audioNames;
        quantAudios = this.audioNames.length;
        random = new Random();
        audios = new Audio[quantAudios];
        initLlistaSenseRepeticio();
    }
    
    public LlistaReproduccio(Node rootNode, AssetManager assetManager, String audioNames[], boolean permetRepeticio) {
        this.rootNode = rootNode;
        this.assetManager = assetManager;
        this.audioNames = audioNames;
        this.permetRepeticio = permetRepeticio;
        quantAudios = this.audioNames.length;
        random = new Random();
        audios = new Audio[quantAudios];
        if (permetRepeticio) {
            initLlistaAmbRepeticio();
        }
        else {
            initLlistaSenseRepeticio();
        }
    }
    
    public LlistaReproduccio(Node rootNode, AssetManager assetManager, String audioNames[], boolean permetRepeticio, float volumes[]) {
        this.rootNode = rootNode;
        this.assetManager = assetManager;
        this.audioNames = audioNames;
        this.permetRepeticio = permetRepeticio;
        quantAudios = this.audioNames.length;
        random = new Random();
        audios = new Audio[quantAudios];
        teVolums = true;
        if (permetRepeticio) {
            initLlistaAmbRepeticio();
        }
        else {
            initLlistaSenseRepeticio();
        }
    }
    
    /*
     * Amb repeticio vol dir que son efectes de so (curts) i no biblioteca de
     * musica (llargs), llavors vol dir que s'han d'inicialitzar tots els sons
     * per a poder tenir-los ja carregats.
     */
    private void initLlistaAmbRepeticio() {
        for (int i = 0; i < quantAudios; i++) {
            audios[i] = new Audio(rootNode, assetManager, audioNames[i]);
            if (teVolums) {
                audios[i].setVolume(volumes[audioActual]);
            }
        }
        hanCarregat = true;
    }
    
    private void initLlistaSenseRepeticio() {
        haReproduit = new boolean[quantAudios];
        haReproduitToFalse();
    }
    
    private void haReproduitToFalse() {
        for (int i = 0; i < quantAudios; i++) {
            haReproduit[i] = false;
        }
    }
    
    public void setVolumes(float volumes[]) {
        this.volumes = volumes;
        teVolums = true;
    }
    
    private int shuffle() {
        return random.nextInt(quantAudios);
    }
    
    public void playNext() {
        if (permetRepeticio || audioActual == -1) {
            audioActual = shuffle();
            play();
        }
        else {
            audioActual = shuffle();
            int cerca = audioActual;
            while (cerca < quantAudios && haReproduit[cerca]) {
                cerca++;
            }
            if (cerca != quantAudios) {
                audioActual = cerca;
                play();
            }
            else {
                cerca = audioActual;
                while (cerca >= 0 && haReproduit[cerca]) {
                    cerca--;
                }
                if (cerca != -1) {
                    audioActual = cerca;
                    play();
                }
                else {
                    audioActual = -1;
                    hanCarregat = true;
                    haReproduitToFalse();
                    playNext();
                }
            }
        }
    }
    
    /* Es un metode privat */
    private void play() {
        if (hanCarregat) {
            audios[audioActual].play();
        }
        else {
            audios[audioActual] = new Audio(streamed, rootNode, assetManager, audioNames[audioActual]);
            if (teVolums) {
                audios[audioActual].setVolume(volumes[audioActual]);
            }
            audios[audioActual].play();
        }
    }
    
    public void pause() {
        audios[audioActual].pause();
    }
    
    public void stop() {
        audios[audioActual].stop();
    }
    
    public void unPause() {
        audios[audioActual].play();
    }
    
    public boolean isPlaying() {
        return audios[audioActual].isPlaying();
    }
    
    public boolean isPaused() {
        return audios[audioActual].isPaused();
    }
    
    public boolean isStopped() {
        return audios[audioActual].isStopped();
    }
}
