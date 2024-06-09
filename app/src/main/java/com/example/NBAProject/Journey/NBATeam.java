package com.example.NBAProject.Journey;


import com.google.android.gms.maps.model.LatLng;

public class NBATeam {
     private String TeamName;
    private String Location;
    private String Arena;
    private String Codename;
    private LatLng cord;
    private int resourceid;

     //Empty constructor
    NBATeam(){

    }

    //Getter and setter methods

    public String getTeamName() {
        return TeamName;
    }

    public void setTeamName(String teamName) {
        this.TeamName = teamName;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        this.Location = location;
    }

    public String getArena() {
        return Arena;
    }

    public void setArena(String arena) {
        this.Arena = arena;
    }

    public String getCodename() {
        return Codename;
    }

    public void setCodename(String codename) {
        this.Codename = codename;
    }

    public LatLng getCord() {
        return cord;
    }

    public void setCord(LatLng cord) {
        this.cord = cord;
    }

    public int getResourceid() {
        return resourceid;
    }

    public void setResourceid(int resourceid) {
        this.resourceid = resourceid;
    }
}