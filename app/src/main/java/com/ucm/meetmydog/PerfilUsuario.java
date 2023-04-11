package com.ucm.meetmydog;

import java.io.Serializable;

public class PerfilUsuario implements Serializable {
    private String nombrePerro;
    private String imagenUri;
    private String descripcion;
    private int edad;
    private int peso;
    private String raza;

    public PerfilUsuario(String nombrePerro,String descripcion,String imagenUri,int edad,int peso,String raza){
        this.nombrePerro=nombrePerro;
        this.descripcion=descripcion;
        this.imagenUri=imagenUri;
        this.edad=edad;
        this.peso=peso;
        this.raza=raza;
    }

    public String getNombrePerro() {
        return nombrePerro;
    }

    public void setNombrePerro(String nombrePerro) {
        this.nombrePerro = nombrePerro;
    }

    public String getImagenUri() {
        return imagenUri;
    }

    public void setImagenUri(String imagenUri) {
        this.imagenUri = imagenUri;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public int getPeso() {
        return peso;
    }

    public void setPeso(int peso) {
        this.peso = peso;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }
}
