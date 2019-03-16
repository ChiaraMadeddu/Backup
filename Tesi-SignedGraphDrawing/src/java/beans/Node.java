/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

/**
 *
 * @author Chiara
 */
public class Node {
    private int id;
    private float x;
    private float y;
    private float originalX;
    private float originalY;
    private int cluster;
    
    
    public Node(int id){
        this.id = id;
    }
    
    public int getId(){
        return id;
    }
    
    public void setId(int id){
        this.id = id;
    }
    
    
    public float getX(){
        return x;
    }
    
    public void setX(float x){
        this.x = x;
    }
    
    
    public float getY(){
        return y;
    }
    
    public void setY(float y){
        this.y = y;
    }
    
    
    public float getOriginalX(){
        return originalX;
    }
    
    public void setOriginalX(float originalX){
        this.originalX = originalX;
    }
    
    
    public float getOriginalY(){
        return originalY;
    }
    
    public void setOriginalY(float originalY){
        this.originalY = originalY;
    }
    
    
    public int getCluster(){
        return cluster;
    }
    
    public void setCluster(int cluster){
        this.cluster = cluster;
    }
    
    public String toString(){
        return "{\"id\": \"" + id +"\", \"x\":" + x + ", \"y\":" + y + ", \"cluster\": \"" + cluster + "\"}";
    }
    
    public Node clone(){
        Node nuovo = new Node(id);
        nuovo.setX(x);
        nuovo.setY(y);
        nuovo.setOriginalX(originalX);
        nuovo.setOriginalY(originalY);
        nuovo.setCluster(cluster);
        return nuovo;
    }
}
