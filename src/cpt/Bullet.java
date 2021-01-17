/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cpt;


import java.awt.Color;
import java.awt.Graphics;


/**
 *
 * @author tangs6732
 */
public class Bullet {
    private int x; //position along x axis
    private int y; //position along y axis
    private int dir; //direction the bullet is traveling
   
    public Bullet(int x, int y, int dir, CPT game){
        //when Bullet object is created, sets x,y, and direction to the parameters given
        this.x = x;
        this.y = y;
        this.dir = dir;
        switch (dir){ //changes position slightly to make it fall inline with the sprite
                case 1: 
                    this.y = y;
                    this.x = x +35;
                    break;

                case 2: 
                    this.y = y+35;
                    this.x = x+30;
                    break;

                case 3: 
                    this.y = y+30;
                    this.x = x;
                    break;

                case 4: 
                    this.y = y+35;
                    this.x = x+30;
                    break;

            }
    }
    public void Shoot(){
        //essentially the tick method of the class, changes either the x or the y variable by 10 each tick, depending on the direction
        switch (dir){
            case 1: 
                y -=10;
                break;
                   
            case 2: 
                y += 10;
                break;
                 
            case 3: 
                x -=10;
                break;
                  
            case 4: 
                  x += 10;
                  break;
        
        }
    }
    public void render(Graphics g){
       //renders a small black bullet at x and y coordinate
        g.setColor(Color.BLACK);
        g.fillRect(x,y,5,5);
        //if the bullet leaves bounds of the game, it will clear the rectangle of the bullet
        if (x < 200 || x > 750 || y < 0 || y > 750){
             g.clearRect(x,y,5,5);
        }
    }
    
    //getters and setters for ease of access by other classes
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
    
    
}

