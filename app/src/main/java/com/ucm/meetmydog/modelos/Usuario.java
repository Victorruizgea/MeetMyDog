package com.ucm.meetmydog.modelos;


import com.ucm.meetmydog.modelos.Perro;

import java.util.List;

public class Usuario {

    private String nombre;

    private String email;
    private String password;

    private String imagenUri;

    private List<Perro> listaPerros;
    private List<String> amigos;

    public Usuario(String nombre,String email,String password){
        this.nombre=nombre;
        this.email=email;
        this.password=password;
    }
    public Usuario(String nombre,String email,String password,String imagenUri){
        this.nombre=nombre;
        this.email=email;
        this.password=password;
        this.imagenUri=imagenUri;
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
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getImagenUri(){
        return imagenUri;
    }
    public void setImagenUri(String imagenUri){
        this.imagenUri=imagenUri;
    }



}
