package com.ucm.meetmydog;

import java.io.Serializable;

public class PerfilUsuario implements Serializable {
    private String nombrePerro;
    private String imagenUri;
    private String descripcion;

    public PerfilUsuario(String nombrePerro,String descripcion,String imagenUri){
        this.nombrePerro=nombrePerro;
        this.descripcion=descripcion;
        this.imagenUri=imagenUri;
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
}
