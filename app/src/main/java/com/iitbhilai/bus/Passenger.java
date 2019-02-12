package com.iitbhilai.bus;

public class Passenger {
    private String name;
    private String id;
    private String departureTime, seatNo, txnDate, busId;

    public Passenger(){

    }

    public Passenger(String id, String name, String departureTime, String seatNo, String txnDate, String busId){
        this.id = id;
        this.name =name;
        this.departureTime = departureTime;
        this.seatNo = seatNo;
        this.txnDate = txnDate;
        this.busId = busId;
    }
    public Passenger(String id, String name){
        this.id = id;
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public String getId(){
        return id;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public String getSeatNo() {
        return seatNo;
    }

    public String getTxnDate() {
        return txnDate;
    }

    public String getBusId() {
        return busId;
    }
}
