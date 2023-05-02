package com.ucm.meetmydog.modelos;


import com.ucm.meetmydog.modelos.Perro;

import java.io.Serializable;
import java.util.List;

public class Usuario implements Serializable {

    private String nombre;

    private String email;

    private String imagenUri;

    

    public Usuario(String nombre,String email){
        this.nombre=nombre;
        this.email=email;
    }
    public Usuario(){

    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getImagenUri(){
        return imagenUri;
    }
    public void setImagenUri(String imagenUri){
        this.imagenUri=imagenUri;
    }



}
