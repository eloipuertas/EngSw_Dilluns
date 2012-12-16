/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;

/**
 *
 * @author davidsanchezpinsach
 */
public class ComandosCoche implements ActionListener {
    private VehicleProtagonista car;
    
    public ComandosCoche(VehicleProtagonista car){
        this.car = car;
    }
    
    public void setupKeys(int left, int right, int up, int down, int space, int returN,
            InputManager inputManager) {
        inputManager.addMapping("Lefts", new KeyTrigger(left));
        inputManager.addMapping("Rights", new KeyTrigger(right));
        inputManager.addMapping("Ups", new KeyTrigger(up));
        inputManager.addMapping("Downs", new KeyTrigger(down));
        inputManager.addMapping("Space", new KeyTrigger(space));
        inputManager.addMapping("Reset", new KeyTrigger(returN));
        inputManager.addListener(this, "Lefts");
        inputManager.addListener(this, "Rights");
        inputManager.addListener(this, "Ups");
        inputManager.addListener(this, "Downs");
        inputManager.addListener(this, "Space");
        inputManager.addListener(this, "Reset");
    }
    
    /*
     * Metodo que gestiona las teclas del coche
     */    
    public void onAction(String binding, boolean value, float tpf) {
        if (binding.equals("Lefts")) {
            car.turnLeft(value);
        } else if (binding.equals("Rights")) {
            car.turnRight(value);
        } else if (binding.equals("Ups")) {
            car.forward(value);
        } else if (binding.equals("Downs")) {
            car.back(value);
        } else if (binding.equals("Reset")) {
            car.reset(value, car.initialPos, car.initialRot);
        }else if (binding.equals("Space")) {
            car.handBrake(value);
        }
        
    }
}
