package model;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.scene.Node;

public class Audio {
    private Node rootNode;
    private AssetManager assetManager;
    private String audioName;
    private final AudioNode sound;
    
    public Audio(Node rootNode, AssetManager assetManager, String audioName) {
        this.rootNode = rootNode;
        this.assetManager = assetManager;
        this.audioName = audioName;
        this.sound = new AudioNode(assetManager, "Sounds/" + audioName, false);
        
        rootNode.attachChild(sound);
    }
    
    public Audio(Node rootNode, AssetManager assetManager, String audioName, boolean isLooping) {
        this.rootNode = rootNode;
        this.assetManager = assetManager;
        this.audioName = audioName;
        this.sound = new AudioNode(assetManager, "Sounds/" + audioName, false);
        sound.setLooping(isLooping);
        
        rootNode.attachChild(sound);
    }
    
    public String getName() {
        return audioName;
    }
    
    public float getVolume() {
        return sound.getVolume();
    }

    public void setVolume(float volume) {
        sound.setVolume(volume);
    }
    
    public boolean isLooping() {
        return sound.isLooping();
    }

    public void setLooping(boolean isLooping) {
        sound.setLooping(isLooping);  // activate continuous playing
    }
    
    public float getTimeOffset() {
        return sound.getTimeOffset();
    }
    
    public void setTimeOffset(float timeOffset) {
        sound.setTimeOffset(timeOffset);
    }
    
    public float getPitch() {
        return sound.getPitch();
    }
    
    public void setPitch(float pitch) {
        sound.setPitch(pitch);
    }
    
    public void play(float timeOffset) {
        sound.setTimeOffset(timeOffset);
        sound.play(); // play continuously!
    }
    
    public void play() {
        sound.play(); // play continuously!
    }
    
    public void pause() {
        sound.pause();
    }
    
    public void stop() {
        sound.stop();
    }
}
