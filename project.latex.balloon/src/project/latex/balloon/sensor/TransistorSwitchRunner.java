/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.latex.balloon.sensor;

import com.pi4j.io.gpio.RaspiPin;
import java.util.Scanner;

public class TransistorSwitchRunner {
    public static void main(String[] args) {
        TransistorSwitch mySwitch = new TransistorSwitch(RaspiPin.GPIO_01);
        Scanner userInput = new Scanner(System.in);
        String input;
        while (true) {
            System.out.println("Enter 'o' to open switch or 'c' to close: ");
            input = userInput.next();
            switch (input) {
                case "o":
                    mySwitch.open();
                    break;
                case "c":
                    mySwitch.close();
                    break;
            }
        }
    }
}
