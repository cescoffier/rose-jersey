package org.ow2.chameleon.rose.rest;


public class MyRessource implements RessourceService{
    
    private String name;
    
    public String hello(){
        return name;
    }
}