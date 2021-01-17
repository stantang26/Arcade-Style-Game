/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cpt;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author stant
 */
public class Special {
    private int x; //position of attacking sprite on x axis;
    private int y = 10000;//position of attacking sprite on y axis
    public boolean spawn = false;//tracks whether or not the airstrike is currently spawned in/happening
    CPT game;//instance of CPT class
    private BufferedImage Plane; //Sprite of the attack
    private long atick1 = System.currentTimeMillis();//used to delay drawing of images to create an animation effect
    private long atick2 = 0;
    
    public Special(CPT game){
        game = this.game;
    }
    
    public void tick(Monster m){
        //if the y coordinate is less than the boundary of the game and is greater than -800, the y coordinate will decrement by 10
        //the -800 restriction meanst the attack will continue for a bit after the image leaves the game bounds
        if(y<750 &&  y > -800){
            y-=10;
        }else{
            spawn = false; //once the image is less than -800 y, the spawn variable changes to false and the y coordinate becomes much greater than the game bounds to keep the sprite out of view
            y = 100000;
        }
        
        
    }
    public void render(Graphics g, Monster m) throws IOException{
        Plane = ImageIO.read(new File ("Plane.png")); //sets the BufferedImage Plane as the Plane.png from the cpt folder
        g.setColor(Color.WHITE);
        if(y< m.getY()-400){ //explosion animation, ovals are drawn at a delay 
            g.fillOval(m.getX()+20,m.getY(),35,35);
            atick1 = System.currentTimeMillis();
            if(atick1 - atick2 > 50 && atick1 - atick2 < 250){
                g.fillOval(m.getX(),m.getY()+30,40,40);
                m.setHealth(m.getHealth()-1); //lowers monster's health by 1 each tick
            }
            if(atick1 - atick2 > 200 && atick1 - atick2 < 560){
               g.fillOval(m.getX()-13,m.getY()-5,40,40);
               m.setHealth(m.getHealth()-1); //lowers monster's health by 1 each tick
            }
            if(atick1 - atick2 > 360 && atick1 - atick2 < 790){
                g.fillOval(m.getX()+27,m.getY()+23,45,45);
                m.setHealth(m.getHealth()-1); //lowers monster's health by 1 each tick
            }
            if(atick1 - atick2 > 400&& atick1 - atick2 < 670){          
               g.fillOval(m.getX(),m.getY()-9,35,35);
            }
            if(atick1 - atick2 > 740 && atick1 - atick2 < 890){
                g.fillOval(m.getX()+27,m.getY()+23,45,45);
            }
            if(atick1 - atick2 > 670&& atick1 - atick2 < 1200){          
               g.fillOval(m.getX(),m.getY()-9,35,35);
               m.setHealth(m.getHealth()-1); //lowers monster's health by 1 each tick
            }
            if(atick1 - atick2 > 460 && atick1 - atick2 < 830){
                g.fillOval(m.getX()-7,m.getY()+23,35,35);
            }
            if(atick1 - atick2 > 460&& atick1 - atick2 < 1040){          
               g.fillOval(m.getX()-2,m.getY()+12,35,35);
            }      
            if(atick1 - atick2 > 640 && atick1 - atick2 < 990){
                g.fillOval(m.getX()+20,m.getY()+23,45,45);
            }
            if(atick1 - atick2 > 700&& atick1 - atick2 < 1200){          
               g.fillOval(m.getX()+3,m.getY()-8,35,35);
            }      
            
            if(atick1 - atick2 > 1500){
               
                atick2 = atick1;
                //after 1500 milliseconds, the variables atick1 and atick 2 reset
            }  
        }
        g.drawImage(Plane,x,y,400,400,game); //game draws plane image at x and y
       
        
    }
    public void spawn(Monster m){
        //when called, changes y to inside game bounds, x to the monster's x location (adjusted for image size) and sets spawn to true
        y=740;
        x=m.getX()-175;
        spawn = true;
    }  
}
