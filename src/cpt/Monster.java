

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
import java.util.Random;


import javax.imageio.ImageIO;

/**
 *
 * @author tangs6732
 */
public class Monster {
    private int x; //tracks position on x axis
    private int y; //tracks position on y axis
    private int ax; //tracks position of projectile on x axis
    private int ay; //tracks position of projectile on y axis
    private int mspeed; //tracks the default speed of the monster spawned
    private int speed; //used for the actual speed of the monster 
    private int dir = 1; //direction of monster
    private int health = 0; //health of monster
    private int damage; //damage of monster to player
    private boolean attacking = false; //used inside the object for it to know when to render an attack
    private long atick1 = System.currentTimeMillis(); //used with atick2 to delay images in order to animate
    private long atick2 = 0;
    public int type = 0; //determines which monster is spawned
    public String name = ""; //flavortext name, corresponds to each type
    BufferedImage MonsterA[][]; //2d array of images used for ease of access when drawing images of the monster 
    BufferedImage Monster; //image drawn by the program
    Random gen = new Random(); //random number generator object
    CPT game; //instance of the CPT class

    public Monster(int x, int y, CPT game){ //when Object is created, sets the x,y and game values to the given parameters
        this.x = x;
        this.y = y;
        this.game = game;
        
      
    }
    public void init() throws IOException{
        //method called when creating the object, assigns images to the array MonsterA using assets from the CPT file, a set of 8 images for each of 5 monster types
        MonsterA = new BufferedImage[][]{{ImageIO.read(new File ("Crawler 1.gif") ),ImageIO.read(new File ("Crawler 2.gif") ),ImageIO.read(new File ("Crawler 3.gif") ),ImageIO.read(new File ("Crawler 4.gif") ),ImageIO.read(new File ("C attack 1.gif") ),ImageIO.read(new File ("C attack 2.gif") ),ImageIO.read(new File ("C attack 3.gif") ),ImageIO.read(new File ("C attack 4.gif") )},
        {ImageIO.read(new File("Creeper 1.gif")),ImageIO.read(new File("Creeper 2.gif")),ImageIO.read(new File("Creeper 3.gif")),ImageIO.read(new File("Creeper 4.gif")),ImageIO.read(new File("Creeper 1.gif")),ImageIO.read(new File("Creeper 2.gif")),ImageIO.read(new File("Creeper 3.gif")),ImageIO.read(new File("Creeper 4.gif"))},
        {ImageIO.read(new File("Mage.gif")),ImageIO.read(new File("Mage.gif")),ImageIO.read(new File("Mage.gif")),ImageIO.read(new File("Mage.gif")),ImageIO.read(new File("Mage.gif")),ImageIO.read(new File("Mage.gif")),ImageIO.read(new File("Mage.gif")),ImageIO.read(new File("Mage.gif"))},
        {ImageIO.read(new File ("Jockey 1.gif") ),ImageIO.read(new File ("Jockey 2.gif") ),ImageIO.read(new File ("Jockey 3.gif") ),ImageIO.read(new File ("Jockey 4.gif")),ImageIO.read(new File ("Jockey 1.gif") ),ImageIO.read(new File ("Jockey 2.gif") ),ImageIO.read(new File ("Jockey 3.gif") ),ImageIO.read(new File ("Jockey 4.gif") ) },
        {ImageIO.read(new File ("Warper 1.gif") ),ImageIO.read(new File ("Warper 2.gif") ),ImageIO.read(new File ("Warper 3.gif") ),ImageIO.read(new File ("Warper 4.gif")),ImageIO.read(new File ("Warper 1.gif") ),ImageIO.read(new File ("Warper 2.gif") ),ImageIO.read(new File ("Warper 3.gif") ),ImageIO.read(new File ("Warper 4.gif") ) }};
    
    }
    public void monstergen() throws InterruptedException{
        
        Thread.sleep(1000);
        //generates a random monster using random number generator and then changes each of the attribute values of the monster
        //monsters increment in difficulty based on level and have differing stats
        //monsters also spawn at different locations based on type.
        switch(gen.nextInt(5)){
            case 0: type = 0;name = "Crawler";health = 75; mspeed = 4; x = 350; y = 0; damage = 20+(game.level/3); break;
                
            case 1: type = 1;name = "Creeper";health = 100; mspeed = 3 + (game.level/5); x = 350; y = 500; damage = 0; break;
                
            case 2: type = 2;name = "Mage";health = 50; mspeed = 2 + (game.level/5); x = 450; y = 0;damage = 10+game.level; break;
                
            case 3: type = 3;name = "Jockey";health = 50; mspeed = 8 + (game.level/5); x = 700; y = 250; damage = 2+(game.level/10); break;
                    
            case 4: type = 4;name = "Warper";health = 100; mspeed = 1; x = 200; y = 250; damage = 2+(game.level/5); break;
        }
            
    }
    public void tick(Player p) throws InterruptedException{
        
            //every tick, checks whether the monster is within range of the player using the withinRange method
            //if it is within range, the monster's speed will be set to 0 and the attack method will be called
            if(withinRange(p) == true){
                speed = 0;
                p.setVelX(0);
                p.setVelY(0);
                attack();
                //if the monster is a type 3, or a jockey, the speed, direction and mspeed will be inverted
                //this causes the monster to run away after attacking
                if(type ==3){
                    mspeed = 0-mspeed;
                    dir = 5-dir;
                    speed = mspeed;
                }
             
            } else {
                //if the monster is not within range, the speed of the monster is set to the default speed
                speed = mspeed;
                //if the monster is type 3 or jocky, and it has a negative mspeed(it is running away) and it reaches the bounds of the game, 
                //the speed and direction variables are reverted
                if(type == 3 && mspeed < 0){
                    if(x<230 || x > 730 || y < 30 || y > 480){
                        mspeed = 0-mspeed;
                        dir = 5-dir+1;
                    }
                }
            }
            
            switch(type){
                //if the monster is a type 2, a Mage and its projectile is within the bounds of the game, the x value of the attack
                //will be incremented by the a fraction of the difference between the player's x coordinate and the monster's x coordinate
                //essentially the projectile will follow the player to a certain extent in the x-axis. The speed of this following increases with level
                case 2:if(ax>200 && ay >= 0 && ax < 700 && ay < 500){
                            
                            ax+=(p.getX()+30-x)/(10 - game.level/10);
                            //if the player's y value is greater than y value of the monster + 40 and the y value of the attack, the 
                            //y value of the attack will increment by 12 + the game level
                            if(p.getY()> y+40 || ay < p.getY()){
                                ay+=12 + game.level;
                            }
                            
                        }else {
                        //if the attack of the mage is not within the bounds of the game, its x coordinate becomes the x coordinate of the monster
                        //and the y value becomes the y coordinate of the monster +20
                            ax = x;
                            ay = y+20;
                        }
                        //if the player's x value + 25 is greater than the monster's x value, the monster's x value will change by the speed value
                        if (p.getX()+25 > x ){
                                x+=speed;
                        }else if(p.getX()+15 < x){
                                x-=speed;
                        }
                        //if the projectile collides with the player, the player loses health equal to the damage value of the monster
                        if(ay> p.getY() && ay < p.getY()+35 && ax > p.getX() && ax< p.getX()+35 ){
                            p.setHealth(p.getHealth()-damage);
                        }
                        
                      break;
                      //if the monster is a type 4, a warper, the default speed is equal to a random number from -2 to 5
                case 4: mspeed = gen.nextInt(8)-2;
                               
                        if(gen.nextInt(200)== 0 ){
                           x = p.getX()+gen.nextInt(100)+50;
                        }
                        if(gen.nextInt(200) == 10){
                               y = p.getY()+gen.nextInt(100)+50;
                        }                            
                        if(gen.nextInt(200)== 5 ){
                           x = p.getX()-(gen.nextInt(100)+50);
                        }
                        if(gen.nextInt(200) == 4){
                           y = p.getY()-(gen.nextInt(100)+50);
                        }       
                        
                default:if (p.getX()+15 > x ){
                            x+=speed;
                            dir = 4;
                        }else if(p.getX()+25 < x){
                            x-=speed;
                            dir = 3;
                                    }
                        if (p.getY() > y){
                            y+=speed;
                            dir = 2;
                        }else if(p.getY()+25 < y){
                            y-=speed;
                            dir = 1;
                        }
                            break;
            }
        
    }
    public void render(Graphics g, Player p) throws InterruptedException, IOException{
        if(health > 0){
            
            Monster = MonsterA[type][dir-1];
        }
        if(attacking == true){
            switch(dir){
                case 1: 
                    Monster = MonsterA[type][4];
                    if(y<p.getY()+50){
                        if(p.getX() < x && p.getX()+50 > x){
                            p.setHealth(p.getHealth()-damage);
                            if(type == 0){
                                p.setY(p.getY()-15);
                                p.move = p.move.SLOWED;
                            }
                        }
                    }
                    break;
                case 2:  
                    Monster = MonsterA[type][5];
                    if(y>p.getY()-50){
                        if(p.getX()-30 < x && p.getX()+30 > x){
                            p.setHealth(p.getHealth()-damage);
                            if(type == 0){
                                p.setY(p.getY()+15);
                                p.move = p.move.SLOWED;
                            }
                        }
                    }
                    break;
                case 3:  
                    Monster = MonsterA[type][6];
                    if(x<p.getX()+50){
                        if(p.getY()-30 < y && p.getY()+30 > y){
                            p.setHealth(p.getHealth()-damage);
                            if(type == 0){
                                p.setX(p.getX()-15);
                                p.move = p.move.SLOWED;
                            }
                        }
                    }
                    break;
                    
                case 4:  
                    Monster = MonsterA[type][7];
                    if(x>p.getX()-50){
                        if(p.getY()-30 < y && p.getY()+30 > y){
                            p.setHealth(p.getHealth()-damage);
                            if(type == 0){
                                p.setX(p.getX()+15);
                                p.move = p.move.SLOWED;

                            }
                        }
                    }
                    break;
                
             }
        if(type == 1){
            g.setColor(Color.WHITE);
            g.fillRect(x+12,y+12,35,35);
            atick1 = System.currentTimeMillis();
            if(atick1 - atick2 > 450 && atick1 - atick2 < 950){

                g.drawImage(Monster,x,y,60,60,game);
            }
            if(atick1 - atick2 > 950 && atick1 - atick2 < 1250){
                g.fillRect(x+12,y+12,35,35);
            }
            if(atick1 - atick2 > 1250 && atick1 - atick2 < 1375){

                g.drawImage(Monster,x,y,60,60,game);
            }
            if(atick1 - atick2 > 1375&& atick1 - atick2 < 1450){          
                g.fillRect(x+12,y+12,35,35);
            }               
            if(atick1 - atick2 > 1450 && atick1 - atick2 < 1500){    
 
                g.drawImage(Monster,x,y,60,60,game);
            }                    
            if(atick1 - atick2 >1500 && atick1 - atick2 < 1550){
                g.fillRect(x+12,y+12,35,35);
            }                                           
            if(atick1 - atick2 > 1550 && atick1 - atick2 < 1700){
                
                g.fillRect(x-80,y-80,200,200);
                if(p.getY() < y+75 && p.getY() > y-75 && p.getX() < x+75 && p.getX() > x-75){
                    p.setHealth(p.getHealth()-5-(game.level/5));
                }
            }
            if(atick1 - atick2 > 1950){
                atick2 = atick1;
                attacking = false;
            
            }
         
        }   

        }
        if(attacking ==true && type == 1){
           
        }else{
             g.drawImage(Monster,x,y,60,60,game);
        }
        if(type == 2){
            BufferedImage Fireball = ImageIO.read(new File("Fireball.png"));
            
            g.drawImage(Fireball,ax,ay,40,40, game);
        }
        
        
    }
    public boolean withinRange(Player p){
        boolean range = false;
        if(p.getY()-40<y && p.getY()+40 > y ){
            if(p.getX()-30<x && p.getX()+40 > x ){
                range = true;
                
            }
        }
        
        return range;
    }
    public boolean withinRange(Player p,int pdir){
        boolean range = false;
        switch(pdir){
            case 1:
                if(p.getY()-35 < y && p.getY()> y ){
                    if(p.getX()-30<x && p.getX()+30 > x ){
                        range = true;

                    }
                }
                break;
            case 2:
                if(p.getY()+35 > y && p.getY()< y ){
                    if(p.getX()-30<x && p.getX()+30 > x ){
                        range = true;

                    }
                }  
                break;
            case 3:
                if(p.getX()-35 < x && p.getX()> x ){
                    if(p.getY()-30<y && p.getY()+30 > y ){
                        range = true;

                    }
                }  
                break;
            case 4:
                if(p.getX()+35 > x && p.getX()< x ){
                    if(p.getY()-30<y && p.getY()+30 > y ){
                        range = true;

                    }
                }  
                break;
        }
        return range;
    }
    public void attack() throws InterruptedException{
        switch(type){
            case 0: Thread.sleep(300);attacking = true;Thread.sleep(400); attacking = false;Thread.sleep(300);break;
            case 1: attacking = true;break;
            case 2:break;
            case 3:Thread.sleep(200);attacking = true;Thread.sleep(100); attacking = false;Thread.sleep(300);break;
            case 4:Thread.sleep(200);attacking = true;Thread.sleep(100); attacking = false;
                switch(gen.nextInt(4)){
                    case 0: x += 100;break;
                    case 1: x -= 60;break;
                    case 2: y += 100;break;
                    case 3: y -=60;break;
                }
            break;
        }
        
    }
    
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
    public int getMSpeed(){
        return mspeed;
    }
    public void setMSpeed(int speed){
        this.mspeed = speed;
    }
    public void setHealth(int health){
        this.health = health;
    }
    public int getHealth(){
        return health;
    }
    
}


