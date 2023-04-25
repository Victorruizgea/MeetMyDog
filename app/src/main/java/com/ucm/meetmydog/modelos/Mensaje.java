package com.ucm.meetmydog.modelos;

public class Mensaje {
    String texto;
    String idUsuarioOrigen;
    String nombreOrigen;
    String fecha;

    public Mensaje(String texto, String idusuarioOrigen,String nombreOrigen,String fecha){
        this.texto=texto;
        this.idUsuarioOrigen=idusuarioOrigen;
        this.nombreOrigen=nombreOrigen;
        this.fecha=fecha;
    }
    public Mensaje(){
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getIdUsuarioOrigen() {
        return idUsuarioOrigen;
    }

    public void setIdUsuarioOrigen(String idUsuarioOrigen) {
        this.idUsuarioOrigen = idUsuarioOrigen;
    }

    public String getNombreOrigen() {
        return nombreOrigen;
    }

    public void setNombreOrigen(String nombreOrigen) {
        this.nombreOrigen = nombreOrigen;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
