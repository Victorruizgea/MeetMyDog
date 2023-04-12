package com.ucm.meetmydog;

import java.io.Serializable;

public class Perro implements Serializable {
    private String nombre;
    private String imagenUri;
    private String descripcion;
    private int edad;
    private int peso;
    private String raza;

    public Perro(String nombre,String imagenUri,String descripcion,int edad,int peso,String raza){
        this.nombre=nombre;
        this.imagenUri=imagenUri;
        this.descripcion=descripcion;
        this.edad=edad;
        this.peso=peso;
        this.raza=raza;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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
