package com.abdulla.qrcode_scanner;

public class Information {
    private String name;
    private String Attendence;
    private int id;
    public Information() {
    }
    public Information(String name, String attendence, int id) {
        this.name = name;
        Attendence = attendence;
        this.id = id;
    }

    public String getName() {return name;}

    public void setName(String name) {
        this.name = name;
    }

    public String getAttendence() {return Attendence;}


    public void setAttendence(String attendence) {
        Attendence = attendence;
    }

    public int getId() {return id;}

    public void setId(int id) {this.id = id;}
}
