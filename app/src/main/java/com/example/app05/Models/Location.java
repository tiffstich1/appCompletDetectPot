package com.example.app05.Models;

public class Location {

    private String id;
    private double latitude;
    private double longitude;
    private String idUser;
    private String date;
    private String poteau;
    private String section;
    private int rang;
    private String composant;


    public double getLatitude() {
        return latitude;
    }

    public String getId() { return id; }

    public double getLongitude() {
        return longitude;
    }

    public String getIdUser() {
        return idUser;
    }

    public String getDate() {
        return date;
    }

    public String getPoteau() { return poteau; }

    public String getSection() { return section; }

    public int getRang() { return rang; }

    public String getComposant() { return composant; }



    public void setDate(String date) {
        this.date = date;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setPoteau(String poteau) {
        this.poteau = poteau;
    }

    public void setSection(String section) { this.section = section; }

    public void setRang(Integer rang) {
        this.rang = rang;
    }

    public void setComposant(String composant) { this.composant = composant; }

}
