
package cpt;


import java.awt.Graphics;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


public class Player{
    private int x; //indicates position of player along x axis
    private int y; //indicates position of player along y axis
    private int velx; //determines velocity of the player along x axis
    private int vely; //determines velocity of the player along y axis
    private int dir = 1; //determines which image of the player is used, what direction the player is facing
    //( 1 = North, 2 = South, 3 = West, 4 = East
    private int health = 100; //the health of the player, determines how many hits they can take before dying
    private int special = 100; //the special of the player, determines when they can use their special attack(airstrike)
    private long slowtick1 = System.currentTimeMillis(); //used with slowtick2 to track time/cooldown, only used for the SLOWED state of the movement enum
    private long slowtick2 = 0;
    private BufferedImage[] PlayerA; //declares a new array of BufferedImages named PlayerA, stores the different images of the player used 
    private BufferedImage Player; //image that is drawn by the program of the player, 
    CPT game; //new instance of the CPT class
    movement move; //new movement enum called move
    
    public Player(int x, int y, CPT game){ //when the player is created, it sets x, y , and game to the same variables/objects used as arguments
        this.x = x;
        this.y = y;
        this.game = game;
    }
    public enum movement{
        //enum that tracks what state the player's movement is. Moving is normal movement, Notmoving is used for dashes, Slowed means the player moves slower
        MOVING, NOTMOVING, SLOWED
    }
    
    public void init() throws IOException{
        //when called, initialized the PlayerA array with files in the CPT folder, for ease of access when drawing the images
        PlayerA = new BufferedImage[]{ImageIO.read(new File ("Player 1.gif")),ImageIO.read(new File ("Player 2.gif")),ImageIO.read(new File ("Player 3.gif")),ImageIO.read(new File ("Player 4.gif"))};
        
    }
    public void tick(){
        //Player moves in this method, based on the move enum
        switch(move){

            case SLOWED:x+=velx/3; //if move is in the SLOWED state, the player will move at 1/3 normal speed and after 15000 milliseconds, the enum will revert to MOVING
                        y+=vely/3;
                        slowtick1 = System.currentTimeMillis();
                        if(slowtick1 - slowtick2 > 15000){
                            move = move.MOVING;
                            slowtick2 = slowtick1;
                        }
                        break;
            default:x+=velx;y+=vely; break; //if move is MOVING OR NOTMOVING, the x variable changes each tick by the velx variable and the y variable changes each tick by the vely variable
                                            //if the player has a velx of 3, each tick their x variable will change by 3
                    
        }
        
        if(x < 200){ //prevents the player from leaving the bounds of the game
            x = 200;
            move = move.NOTMOVING;
        } else if(x > 650){
            x = 650;
            move = move.NOTMOVING;
        } else if(y < 0){
            y = 0;
            move = move.NOTMOVING;
        } else if( y > 425){
            y = 425;
            move = move.NOTMOVING;
        } else if (move != move.SLOWED){ //if the move enum is not slowed and the player is not at any of the boundaries, move will be set to MOVING
            move = move.MOVING;
        }

        
    }
    
    public void render(Graphics g) throws IOException{
        //uses the PlayerA array and the dir variable to determine what image to load as the BufferedImage Player, then draws Player
        Player = PlayerA[dir-1];
        g.drawImage(Player, this.x, this.y, 75,75,game);
     
        
    }
    //These get and set methods allow other classes to access the private variables in this method. Done to avoid confusion between variables
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    public void setX(int x){
        this.x = x;
    }
    public void setY(int y){
        this.y = y;
    }
    public int getVelX(){
        return velx;
    }
    public int getVelY(){
        return vely;
    }
    public void setVelX(int velx){
        this.velx = velx;
    }
    public void setVelY(int vely){
        this.vely = vely;
    }  
    public int getDir(){
        return dir;
    }
    public void setDir(int dir){
        
        this.dir = dir;
    }
    public void setHealth(int health){
        this.health = health;
    }
    public int getHealth(){
        return health;
    }
    public void setSpecial(int special){
        this.special = special;
    }
    public int getSpecial(){
        return special;
    }
    
}
        
    



