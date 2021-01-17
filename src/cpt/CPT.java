package cpt;

import cpt.Player;
import java.awt.*;
import java.awt.Graphics;
import java.awt.Canvas;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class CPT extends Canvas implements Runnable, KeyListener, MouseListener{
    public int level = 20; //indicates what level the player on, which influences the difficulty
    long thispress = System.currentTimeMillis(); //used to run things at a certain speed(for shooting and dashing, used as a cooldown);
    long lastpress = 0; //used with thispress
    state state;//declares a new state enum with the name state
    public static JFrame f = new JFrame("CPT"); //intializes a new JFrame which allows the program to run graphics and names it CPT
    public Player p = new Player(400,200,this);//Initalizes new objects for each of the other classes, allows their methods to be called
    public Monster m = new Monster(0,0,this);  //^
    public Special s = new Special(this);      //^
    Controller c = new Controller(this);       //^
    public PowerUp U = new PowerUp();          //^
    private BufferedImage image; //declares a new BufferedImage object called image, used for graphics
    Graphics g;//declares a new graphics object used to render images and shapes
    
    
    public enum state{ // enum tells the program what state the game is currently in, mostly for the render method to display the correct graphics
        GAME, MENU, INSTRUCTIONS, DEATH
    }
    
    private void reset() { //called when the player dies and chooses to play again, resets all the values
        state = state.MENU;
        level = 1;
        p.setX(400);
        p.setY(200);
        p.setHealth(100);
        p.setSpecial(0);
        c.shots = 0;
        try {
            m.monstergen(); //spawns a new monster
        } catch (InterruptedException ex) {
            Logger.getLogger(CPT.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
   
    public void init() throws InterruptedException, IOException{ 
        addKeyListener(this); //adds the keylistener that the class implements to the game
        addMouseListener(this); //adds the mouselistener that the class implements to the game
        m.init(); //calls the init method of the Monster m object class
        p.init(); //calls the init method of the Player p object class
        state = state.MENU; //changes the enum state to MENU, telling the game that the player is on the menu screen
        
        while(1 == 1){ //continuosly renders images and graphics onto the screen
            render();
        }
    }
    
    public void run(){
        
        Thread a = new Thread(){ //creates a new thread that will run simultaneos to other threads, allows multiple classes and methods to be running at once
            public void run(){
                long tick1 = System.currentTimeMillis();//first tick variable(current time),along with tick2, allows the program to run a certain task at a pace
                long tick2= 0; //second tick variable (previous time)
                
                while (state == state.GAME){ //continuosly attempts to tick the game while it is in the state game
            
                    try {
                        tick1 = System.currentTimeMillis(); //continously sets the tick1 variable to the current time in milliseconds
                        
                        if(tick1 - tick2 > 10){ //if the current time minus the previous time is greater than 10 milliseconds, the tick method will be called
                            tick();
                            tick2 = tick1; //sets previous time to currentime
                        }
                        
                    } catch (Exception g) {
                       //since tick() throws an interrupted exception, calling it requires a try-catch block.
                    }
                }
            }
           
        };
        
        Thread b = new Thread(){
            public void run(){
                long tick1 = System.currentTimeMillis();
                long tick2= 0;
                
                while (state == state.GAME){
            
                    try {
                        
                        tick1 = System.currentTimeMillis();
                        
                        if(tick1 - tick2 > 40){
                            
                            if(s.spawn == false && c.shots > 0){ //if there is so special spawned and the controller class has shot once, the tick() method of the Monster m class will be called
                                m.tick(p);
                            }
                            
                            if(m.getHealth() <= 0){ //if the health variable of the m class is equal to or less than 0, it will increase the level and spawn a new monster
                                 Thread.sleep(5);
                                 level++;
                                    m.monstergen();
                                 }
                            
                            tick2 = tick1;
                        }
                    } catch (Exception g) {

                    }
                }
            }
           
        };
         Thread d = new Thread(){
            public void run(){
                long tick1 = System.currentTimeMillis();
                long tick2= 0;
                
                while (state == state.GAME){
                    
                        tick1 = System.currentTimeMillis();
                        
                        if(tick1 - tick2 > 10){ 
                            
                            try {
                                U.tick(); //causes the power up class to tick
                            } catch (InterruptedException ex) {
                                Logger.getLogger(CPT.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            
                            tick2 = tick1;
                        }
                   

                    
                }
            }
           
        };
          
        a.start(); //starts each of the threads
        b.start();
        d.start();
       
    }

    
    private void tick() throws InterruptedException{
        
        if(c.shots > 0){ //if the controller has shot once(the player presses J), it will call the tick methods of the player and special class.
           p.tick();
           s.tick(m); //ticks the special class, which allows the player to use their airstrike
        } 
        
        c.tick(m); //ticks the controller class, which allows the user to shoot bullets
        
        if(p.getHealth() <= 0){
            state = state.DEATH; //if the health variable of the Player class is less than or equal to 0, the state of the game changes to death
        }

        
    }
    
    private void render() throws InterruptedException, IOException{
        BufferStrategy bs = this.getBufferStrategy() ; //attempts to get a buffer strategy(how the program creates images) from the class
        if(bs==null){
            /*since no bufferstrategy exists at first, if the value gotten is null, it will create a new bufferstrategy that creates three images before loading them
            on each subsequent calling of the method, the program will use the created buffer strategy and load the images prior to running them*/
            createBufferStrategy(3);
            return;
        }
        
        Graphics g = bs.getDrawGraphics(); //assigns the graphics from the bufferstrategy to the graphics g object and uses it to draw images
        
        
        if(null != state)switch (state) { //switch statement using the state enum to tell the program which graphics to run
            case GAME:
                image = ImageIO.read(new File("Ground.png"));
                g.drawImage(image, 200, 0, 530, 500, this);
                p.render(g); //during the game state, the program will run the render method in all the objects, using the graphics g object
                m.render(g,p); // the render method in each of these is responsible for drawing the object to the game
                c.render(g);
                U.render(g,p,this);
                s.render(g,m);
                g.setFont(new Font("",Font.PLAIN, 30));
                
                if(c.shots>0){ //creates the name and health bar of the monster at bottom of the screen
                    g.setColor(Color.WHITE);
                    g.drawString(m.name, 325,400);
                    g.fillRect(250, 425, m.getHealth()*4, 20);
                }
                
                if(c.shots == 0){ //draws the string press J to start when the player starts in order to add one to shots, allowing the rest of the game to tick
                    g.drawString("Press J to Start",400,250);
                }
                image = ImageIO.read(new File ("SideBar.gif")); //Creates the sidebar to the left of the game with the player's stats and monster analysis
                g.drawImage(image,0,0,200,505,this);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
                g.drawString("STAGE " + level, 50, 70);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 18));
                g.drawString("Health:", 5, 145);
                g.setColor(Color.BLACK);
                g.fillRect(72,128,105, 19);
                g.clearRect(75, 130,100, 14);
                g.setColor(Color.RED);
                g.fillRect(75, 130,p.getHealth(), 15);
                g.setColor(Color.WHITE);
                g.drawString("Special:", 5, 205);
                g.setColor(Color.BLACK);
                g.fillRect(72, 189,105, 19);
                g.clearRect(75, 191,100, 14);
                g.setColor(Color.YELLOW);
                g.fillRect(75, 191,p.getSpecial(), 15);
                g.setColor(Color.WHITE);
                g.setFont(new Font("TimesRoman", Font.BOLD, 16));
                g.drawString("Name: " + m.name, 5, 285);
                switch(m.type){
                    case 0: image = ImageIO.read(new File ("Crawler 3.gif"));break;
                    case 1: image = ImageIO.read(new File ("Creeper 3.gif"));break;
                    case 2: image = ImageIO.read(new File ("Mage.gif"));break;
                    case 3: image = ImageIO.read(new File ("Jockey 3.gif"));break;
                    case 4: image = ImageIO.read(new File ("Warper 3.gif"));break;
                        
                }
                g.drawImage(image, 120, 245, 70, 70, this);
                g.drawString("ANALYSIS",5,325);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 13));
                switch(m.type){
                    case 0: g.drawString("Large and aggressive organism. ",5,355);
                            g.drawString("Seemingly native. Humanoid  ",5,375);
                            g.drawString("in shape,but much larger and  ",5,395);
                            g.drawString("more powerful. Slow, but a ", 5, 415);
                            g.drawString("hit could be crippling.",5,435);
                            break;
                    case 1: g.drawString("Creeper = BOOM!",5, 355);
                            break;
                    case 2: g.drawString("Mysterious entity, seems fully ",5,355);
                            g.drawString("organic with inorganic elements. ",5,375);
                            g.drawString("Tends to stay at range from ",5,395);
                            g.drawString("enemies. Can shoot what seems",5,415);
                            g.drawString("to be energy projectiles.", 5, 435);
                            break;
                    case 3: g.drawString("Small and agile organism, ",5,355);
                            g.drawString("similar in appearance to a large",5,375);
                            g.drawString("Hedgehog. Aggressive but will ",5,395);
                            g.drawString("run away from threats. ",5,415);
                            g.drawString("Fast yet frail.",5,435);
                            break;
                    case 4: g.drawString("Semi-organic entity. Insectoid",5,355);
                            g.drawString("in appearance but with metallic",5,375);
                            g.drawString("and plastic elements. Signs of ",5,395);
                            g.drawString("experimentation. Can teleport. ",5,415);
                            break;
                }
                break;
            case MENU: //if the game is in the menu state
                image = ImageIO.read(new File ("Menu.jfif"));
                g.setColor(Color.LIGHT_GRAY);
                g.drawImage(image, 0,0, 730,530,this);
                g.fillRect(250,170,200,75); //creates two buttons, Play and Instructions
                g.fillRect(250,300,200,75);
                g.setColor(Color.BLACK);
                g.setFont(new Font("TimesRoman", Font.PLAIN|Font.BOLD, 28));
                g.drawString("PLAY", 315,220);
                g.setFont(new Font("TimesRoman", Font.PLAIN|Font.BOLD, 20));
                g.drawString("INSTRUCTIONS", 275,347);
                g.setFont(new Font("TimesRoman", Font.PLAIN|Font.BOLD, 48));
                g.setColor(Color.WHITE);
                g.drawString("COLOSSEUM", 215,100);
                
                break;
            case DEATH: //if the game is in the DEATH state
                g.setFont(new Font("TimesRoman", Font.PLAIN|Font.BOLD, 72));
                g.drawString("YOU DIED",250,180); //prints you died and what level the player reached
                
                g.setFont(new Font("TimesRoman", Font.PLAIN|Font.BOLD, 32));
                g.drawString("You got to level: " + level ,300,240);
                g.fillRect(320,300,200,75);
                g.setColor(Color.WHITE);
                g.setFont(new Font("TimesRoman", Font.PLAIN|Font.BOLD, 32));
                g.drawString("MENU",375,345);
                break;
            case INSTRUCTIONS: //if the game is in the instructions state, prints the controls
                g.drawImage(image, 0,0, 700,500,this);
                g.setColor(Color.LIGHT_GRAY);
                g.fillRect(0, 0, 200, 75);
                g.setColor(Color.WHITE);
                g.setFont(new Font("TimesRoman", Font.PLAIN|Font.BOLD, 48));
                g.drawString("CONTROLS",230,70);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 30));
                g.drawString("W",200,150);
                g.drawString("MOVE UP",380,150);
                g.drawString("A", 200,200);
                g.drawString("MOVE LEFT", 380,200);
                g.drawString("S", 200,250);
                g.drawString("MOVE DOWN", 380,250);
                g.drawString("D", 200,300);
                g.drawString("MOVE RIGHT", 380,300);
                g.drawString("J", 200,350);
                g.drawString("SHOOT", 380,350);
                g.drawString("K", 200,400);
                g.drawString("DASH", 380,400);
                g.drawString("L", 200,450);
                g.drawString("SPECIAL", 380,450);
                g.drawString("BACK", 50,50);
                
                
                break;
            default:
                break;
        }

        g.dispose(); //gets rid of the graphics so that it can be reassigned to the bufferstrategy again when the method is recalled
        bs.show(); //shows the bufferstrategy, running the graphics
  
        
    }
       
    @Override //abstract methods that are automatically added due to the class implementing keylistener
    public void keyTyped(KeyEvent e) {
    }
            
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W){ //if W is pressed
            p.setDir(1); //sets the dir variable in the Player p class to 1(NORTH)

            if ( m.withinRange(p,p.getDir())==false){ //if the withinRange method with player and direction returns false, it will change the velocity of the player
                //essentially checks whether the monster is in front of the player, and if it isn't, allows the player to move forward
                p.setVelY(-3);
            }else{
                p.setVelY(0);
            }

        } else if (e.getKeyCode() == KeyEvent.VK_S){
            p.setDir(2); //sets direction to south

            if ( m.withinRange(p,p.getDir())==false){
                p.setVelY(3);
            }else{
                p.setVelY(0);
            }

        } else if (e.getKeyCode() == KeyEvent.VK_A){
            p.setDir(3);//sets direction to west

            if (m.withinRange(p,p.getDir())==false){ 
                p.setVelX(-3);
            }else{
            p.setVelX(0);
            } 

        } else if (e.getKeyCode() == KeyEvent.VK_D){
            p.setDir(4); //sets direction to east

            if ( m.withinRange(p,p.getDir())==false){
                p.setVelX(3);
            }else{
                p.setVelX(0);
            }   

        } else if (e.getKeyCode() == KeyEvent.VK_J){ //Shooting button

            try {
                thispress = System.currentTimeMillis(); //uses thispress and last press give a cooldown between shots

                if(thispress - lastpress > 400){ //if the time between the current press and the last press is greater than 400 milliseconds
                    //Creates a new object of the class Bullet and spawns it at the players position
                    Bullet b = new Bullet(p.getX(), p.getY(), p.getDir(), this);
                    //Adds the bullet to the controller, telling the controller to shoot it 
                    c.addBullet(b);
                    lastpress = thispress;
                }

            } catch (Exception g) {

            }


        } else if (e.getKeyCode() == KeyEvent.VK_K){ //Dashing button

            try {
                thispress = System.currentTimeMillis();
                
                if(thispress - lastpress > 350){ //350 millisecond cooldown 
                    
                    switch(p.getDir()){
                        //uses the player's direction to determine how it will dash
                        case 1:p.setVelY(-14); //if the player is facing north, they will gain a velocity of -14 in the y direction for 100 milliseconds
                               Thread.sleep(100);

                               if(p.move == p.move.MOVING){
                                    //if the enum move of the player class is in the MOVING state, the player will continue moving 
                                    //basically allows the player to keep moving after dashing if they were already moving
                                    p.setVelY(-3);
                               }else{
                                   //if the player was not moving prior to dashing they will stop moving after dashing
                                    p.setVelY(0);
                               }
                               break;
                               
                        case 2:p.setVelY(14);
                               Thread.sleep(100);

                               if(p.move == p.move.MOVING){
                                    p.setVelY(3);
                               }else{
                                    p.setVelY(0);
                               }
                               break;
                               
                        case 3:p.setVelX(-14);
                               Thread.sleep(100);

                               if(p.move == p.move.MOVING){
                                    p.setVelX(-3);
                               }else{
                                    p.setVelX(0);
                               }
                               break;
                               
                        case 4:p.setVelX(14);
                               Thread.sleep(100);

                               if(p.move == p.move.MOVING){
                                    p.setVelX(3);
                               }else{
                                    p.setVelX(0);
                               }
                               break;
                    }
                    lastpress = thispress;
                }
            } catch (Exception g) {

            } 
            
        } else if (e.getKeyCode() == KeyEvent.VK_L){ //Special attack button(airstrike)
            //if the special variable is equal to or greater than 100(if the special bar is full)
            if(p.getSpecial()>=100){
                //spawns the special attack(calls the special method of the object) and sets the special value of the player to 0
                s.spawn(m);
                p.setSpecial(0);
            }
        }
                
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //if the player releases any of the movement keys: W,A,S,or D, sets the enum move of the player class to notmoving and sets their velocity to 0
        //stops the player from moving if they release the keys
        if (e.getKeyCode() == KeyEvent.VK_W){
            p.setVelY(0);
            p.move = p.move.NOTMOVING;
            
        } else if (e.getKeyCode() == KeyEvent.VK_S){
            p.setVelY(0);
            p.move = p.move.NOTMOVING;
            
        } else if (e.getKeyCode() == KeyEvent.VK_A){
            p.setVelX(0);
            p.move = p.move.NOTMOVING;
            
        } else if (e.getKeyCode() == KeyEvent.VK_D){
            p.setVelX(0);
            p.move = p.move.NOTMOVING;
        }     
    }
    
    @Override//abstract methods that are automatically added due to the class implementing mouselistener
    public void mousePressed(MouseEvent event) {
        if(state == state.MENU){//when the game is in the MENU state
            
            if(event.getX()> 250 && event.getX()<450 && event.getY()>170 && event.getY()<245){
                //if the player clicks on the first button of the screen, sets the state to GAME and calls the run method
                state = state.GAME;
                run();
            }else if(event.getX()> 250 && event.getX()<450 && event.getY()>300 && event.getY()<375){
                //if the player clicks on the second button of the screen, sets the state to INSTRUCTIONS
                state = state.INSTRUCTIONS;
            }
            
        }else if (state == state.INSTRUCTIONS){//when the game is in the INSTRUCTIONS state
            
            if(event.getX()> 0 && event.getX()<200 && event.getY()>0 && event.getY()<75){
                //if the player clicks on the BACK button, sets the state to MENU
                state = state.MENU;
            }   
            
        }else if(state == state.DEATH){//when the game is the DEATH state
            
            if(event.getX()>320 && event.getX()<520 && event.getY()>300 && event.getY()<375){
                //if the player clicks on the MENU button, calls on the reset method 
                reset();
            }
        }
            
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        
    }

    @Override
    public void mouseExited(MouseEvent e) {
        
    }        
            
     
    public static void main(String[] args) throws InterruptedException, IOException {
        CPT game = new CPT(); //creates a new instance of the CPT class to add to the jframe and run non static methods
        //sets the preffered size of the canvas and class at 1000, and 1000, anything past that those dimensions will not run or render
        game.setPreferredSize(new Dimension(1000,1000 ));
        f.add(game); //add the instance to the jframe
        f.pack(); //sizes frame so the content equlas the preferred sizes
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //allows the user to close the game by exiting the jframe
        f.setVisible(true); //sets the jframe visible
        f.setResizable(false); //disallows the user to resize the jframe
        f.setSize(730, 530); //sets the size of the frame to a width of 730 pixels and a height of 530
        game.setSize(730,530); //sets the actual size of the canvas (where the graphics and shown) to 730 by 530
        game.init();
    }   
        Container contentPane = f.getContentPane(); //creates a new Container using the contenPane of the frame that allows these elements of these games to be contained by the Jframe
  

    
       
} 





   
        

