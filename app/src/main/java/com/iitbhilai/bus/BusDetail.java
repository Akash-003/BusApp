package com.iitbhilai.bus;

public class BusDetail {
    private String busName;
    private String time;
    private int seats;

    public BusDetail(String busName, String time, int seats){
        this.time = time;
        this.seats = seats;
        this.busName = busName;
    }

    public String getTime(){
        return time;
    }

    public int getSeats(){
        return seats;
    }

    public String getBusName() {
        return busName;
    }
}
