package com.ucm.meetmydog.activities.mapa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.slider.RangeSlider;
import com.google.android.material.slider.Slider;
import com.ucm.meetmydog.R;
import com.ucm.meetmydog.activities.home.InicialActivity;
import com.ucm.meetmydog.activities.perfil.PerfilUsuarioActivity;

import java.util.ArrayList;
import java.util.List;

import nl.joery.animatedbottombar.AnimatedBottomBar;

public class FiltroMapaActivity extends AppCompatActivity {
    Slider distancia, tiempo;
    RangeSlider peso;
    CheckBox nobusquedaperros;
    Button comenzarPaseo;
    TextView display;
    ImageView image;
    int distanciaNum;
    int tiempoNum;
    Float pesoMin;
    Float pesoMax;

    AnimatedBottomBar bottomBar;
    SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtro_mapa);
        mPref = getApplicationContext().getSharedPreferences("typeUser", MODE_PRIVATE);

        distancia = findViewById(R.id.sliderDistancia);
        tiempo = findViewById(R.id.sliderTiempo);
        peso = findViewById(R.id.sliderTamañoPerro);
        nobusquedaperros = findViewById(R.id.checkboxNoBusquedaPerros);
        comenzarPaseo = findViewById(R.id.bottonPasear);
        display = findViewById(R.id.textViewDisplayer);
        //image = findViewById(R.id.imageDisplay);
        bottomBar = findViewById(R.id.bottom_bar);

        distanciaNum = 100;
        tiempoNum = 10;
        pesoMin = Float.valueOf(10);
        pesoMax = Float.valueOf(45);
        distancia.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                distanciaNum = (int) value;
                display.setText("Vas a recorrer "+ distanciaNum + " metros");
                //image.setImageURI();
            }
        });

        tiempo.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                tiempoNum = (int) value;
                display.setText("Vas a pasear durante "+ tiempoNum + " minutos");
            }
        });

            peso.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {
                List<Float> values = slider.getValues();
                pesoMin = values.get(0);
                pesoMax = values.get(1);
            }
        });

        comenzarPaseo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarDatos();
                Intent intent = new Intent(FiltroMapaActivity.this, MapaActivity.class);
                startActivity(intent);
                finish();
            }
        });
        bottomBar.setOnTabSelectListener(new AnimatedBottomBar.OnTabSelectListener() {
            @Override
            public void onTabSelected(int i, @Nullable AnimatedBottomBar.Tab tab, int i1, @NonNull AnimatedBottomBar.Tab tab1) {
                switch (i1) {
                    case 0:
                        Intent intent1 = new Intent(FiltroMapaActivity.this, InicialActivity.class);
                        startActivity(intent1);
                        break;
                    case 1:
                        Intent intent2 = new Intent(FiltroMapaActivity.this, SeleccionPerrosActivity.class);
                        startActivity(intent2);
                        break;
                    case 2:
                        Intent intent3 = new Intent(FiltroMapaActivity.this, PerfilUsuarioActivity.class);
                        startActivity(intent3);
                        break;
                }
            }

            @Override
            public void onTabReselected(int i, @NonNull AnimatedBottomBar.Tab tab) {}
        });
    }
    private void cargarDatos (){
        SharedPreferences.Editor edit = mPref.edit();
        String Parameters = "";
        if(nobusquedaperros.isChecked()){
            Parameters += "100,10,10,45";
        }
        else{
            Parameters += distanciaNum +"," + tiempoNum + "," + pesoMin + "," + pesoMax;
        }
        edit.putString("parameters", Parameters);
        edit.apply();
    }
}