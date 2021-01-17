package cpt;

import java.awt.Graphics;
import java.util.LinkedList;

/**
 *
 * @author tangs6732
 */
public class Controller {
    private LinkedList<Bullet> blist = new LinkedList<Bullet>(); //initializes a new List of Bullet objects, tracks the number of bullets and allows them to be removed when neccessary
    Bullet b; //declares a new bullet
    CPT game; //new instance of CPT class
    public int shots = 0; //int variable used to track how many shots the player made in their runthrough, used so the player can start the game by pressing J
    
    public Controller(CPT game){ //when created, sets the game instance to the parameter given
        this.game = game;
    }
    
    public void tick(Monster m){
        //uses for loop to traverse the list
        for(int i = 0; blist.size()>i;i++){
            //for each bullet, sets the bullet b to the one of the list
            b = blist.get(i);
            //ticks/shoots the bullet selected
            b.Shoot();
            
            //if the monster and the bullet collide, the bullet gets removed and the monster's health is lowered by 7
            if (m.getX() < b.getX() && m.getX()+45 > b.getX() && m.getY() < b.getY() && m.getY()+40 > b.getY()){
                removeBullet(b);
                m.setHealth(m.getHealth()-7);        

            } else if (b.getX()<= -5 || b.getX() > 750 || b.getY() <= -5 || b.getY() > 750){
                //if the bullet leaves the bounds of the game, it is removed from the list
                removeBullet(b);
            }
        
            
        }
    }
    
    public void render(Graphics g){
        //uses for loop to traverse list
        for(int i = 0; blist.size()>i;i++){
            //sets the bullet b to the corresponding on from the list
            b = blist.get(i);
            //renderst the bullet selected
            b.render(g);
        }
    }
    public void addBullet(Bullet block){
        //adds bullets to the list as long as the shots variable is greater than 0(so the player can start the game without shooting a bullet) increments the shots variabel
        if(shots>0){
            blist.add(block);
        }
        shots++;
    }
    public void removeBullet(Bullet block){
       //removes the bullet from the list
        blist.remove(block);
        
    }
}


