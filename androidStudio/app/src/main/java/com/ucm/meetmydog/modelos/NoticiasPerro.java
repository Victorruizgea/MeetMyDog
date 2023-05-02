package com.ucm.meetmydog.modelos;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class NoticiasPerro {
    private String titulo;
    private String link;

    private String autor;

    public static List<NoticiasPerro> fromJsonResponse(String s) {
        List<NoticiasPerro> noticias = new ArrayList<NoticiasPerro>();
        try {
            JSONObject objeto = new JSONObject(s);
            JSONArray articulos= objeto.getJSONArray("articles");//me quedo con la parte de items que es donde estan los datos de los libros
            for(int i =0;i<articulos.length();i++){
                JSONObject noticia=articulos.getJSONObject(i);
                NoticiasPerro notPerro= new NoticiasPerro();
                notPerro.titulo=noticia.getString("title");
                notPerro.autor=noticia.getString("author");
                notPerro.link=noticia.getString("link");

                noticias.add(notPerro);

            }

        } catch (JSONException e) {
            throw new RuntimeException(e);
        } finally {
            return noticias;
        }
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }
}
