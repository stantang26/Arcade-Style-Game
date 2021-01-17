package cpt;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author stant
 */
public class PowerUp {
    Random num = new Random(); //initializes a new random number generator
    private int x = 10000; // position along x axis
    private int y = 10000; //position along y axis
    private int type; //determines type of powerup
    private BufferedImage image; //image of the powerup
    
    public void tick() throws InterruptedException{
        //every tick, the class generates a random number from 0 -200, if that number is 0, it will run the code
        if(num.nextInt(200)==0){
            //generates another random number, either 0 or 1;
            type = num.nextInt(2);
            //generates random x and y coordinates for the powerup
            x = num.nextInt(470)+230;
            y = num.nextInt(440);
            Thread.sleep(5000);//stops ticking for 5000 milliseconds after spawning, causes the powerup to stay onscreen
            
        } else {
            //if the number generated was not 0, the coordinates of the powerup are set outisde the bounds of the game so that they do not render
            x = 10000;
            y = 10000;
        }
    }
    public void render(Graphics g,Player p, CPT game){
        //if the player and the powerup collide, the powerup disappears
        if(p.getX()-15 < x && p.getX()+50 > x && p.getY()-15 < y && p.getY()+50 > y){
            g.clearRect(x,y,20,20);
            
            switch(type){
                //if the type of the powerup is 0, the player gains 30 health when colliding with it, although they cannot go over 100 health 
                case 0:p.setHealth(p.getHealth()+30);
                       if(p.getHealth() > 100){
                           p.setHealth(100);
                       }
                       break;
                //if the type of the powerup is 1, the player gains 30 special when colliding with it, although they cannot go over 100 special       
                case 1:p.setSpecial(p.getSpecial()+30);
                       if(p.getSpecial() > 100){
                           p.setSpecial(100);
                       }
                       break;
            }
            //after collision, the powerup's location is set outside the bounds of the game
            x = 10000;
            y = 10000;
            
        }else{
            //if the player does not collide with the powerup, it is rendered based on the type
            switch(type){
                case 0: 
                    try {
                        image = ImageIO.read(new File ("Health Orb.gif"));
                    } catch (IOException ex) {
                        Logger.getLogger(PowerUp.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                case 1: 
                    try {
                        image = ImageIO.read(new File ("Special Orb.gif"));
                    } catch (IOException ex) {
                        Logger.getLogger(PowerUp.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;  
            }
            g.drawImage(image,x,y,20,20,game);
        }
    }
}
