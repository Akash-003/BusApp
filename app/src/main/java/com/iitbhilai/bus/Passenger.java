package com.iitbhilai.bus;

public class Passenger {
    private String name;
    private String id;

    public Passenger(){

    }

    public Passenger(String id, String name){
        this.id = id;
        this.name =name;
    }

    public String getName(){
        return name;
    }

    public String getId(){
        return id;
    }
}
