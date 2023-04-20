package com.ucm.meetmydog;

public class Paseo {
    String lat, lon, peso;
    String distancia, tiempo;
    // String hora;

    public Paseo(double lat, double lon, String distancia, String tiempo, double peso){
        this.lat = lat + "";
        this.lon = lon +  "";
        this.distancia = distancia;
        this.tiempo = tiempo;
        this.peso = peso + "";
        //Tiempo tiempo1 = new Tiempo();
        //this.hora = tiempo1.getTiempo();
    }

    public static String BadString(double lat, double lon, String distancia, String tiempo, double peso){
        return (lat + " " + lon + " " + distancia + " " + tiempo + " " + peso);
    }
}
