package com.ucm.meetmydog.includes;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.ucm.meetmydog.modelos.NoticiasPerro;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

public class NoticiasLoader extends AsyncTaskLoader<List<NoticiasPerro>> {
    final String BASE_URL= "https://api.newscatcherapi.com/v2/search?q=Perros&lang=es&page_size=5";
    List<NoticiasPerro> result;

    public NoticiasLoader(@NonNull Context context) {
        super(context);
    }



    @Nullable
    @Override
    public List<NoticiasPerro> loadInBackground() {
        Log.d("Loader","exito");
        result= NoticiasPerro.fromJsonResponse(getNoticeInfoJson());
        return result;
    }
    public String getNoticeInfoJson() {
        HttpURLConnection conn = null;
        InputStream is = null;
        Uri builtURI = Uri.parse(BASE_URL).buildUpon()
                .build();
        try {
            URL requestURL = new URL(builtURI.toString());
            conn = (HttpURLConnection) requestURL.openConnection();

            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("x-api-key", "2sKHLMlWZYM117biCouzp545ey8BFNjovYwG4t8kmB8");
            conn.setDoInput(true);
            conn.connect();
            int response = conn.getResponseCode();
            is = conn.getInputStream();
            String contentAsString = convertIsToString(is);
            return contentAsString;

        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    public String convertIsToString(InputStream is) throws IOException {
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line + "\n");
        }
        if (builder.length() == 0) {
            return null;
        }
        return builder.toString();
    }
    @Override
    public void deliverResult(@Nullable List<NoticiasPerro> data) {
        result=data;
        super.deliverResult(data);
    }

    @Override
    protected void onStartLoading() {
        if (result != null) {
            // Si ya hay un resultado almacenado, entregarlo inmediatamente
            deliverResult(result);
        } else {
            // Si no, forzar la carga del Loader
            forceLoad();
        }
    }

    public List<NoticiasPerro> getResult() {
        return result;
    }

    public void setResult(List<NoticiasPerro> result) {
        this.result = result;
    }



}
