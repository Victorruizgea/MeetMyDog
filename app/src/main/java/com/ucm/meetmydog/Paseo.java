package com.ucm.meetmydog;

public class Paseo {
    Double lat, lon, peso;
    String personalidad, distancia, tiempo, hora;

    public Paseo(double lat, double lon, String distancia, String tiempo, double peso, String personalidad){
        this.lat = lat;
        this.lon = lon;
        this.distancia = distancia;
        this.tiempo = tiempo;
        this.peso = peso;
        this.personalidad = personalidad;
        Tiempo tiempo1 = new Tiempo();
        this.hora = tiempo1.getTiempo();
    }
}
