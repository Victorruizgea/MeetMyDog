package com.ucm.meetmydog;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
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

public class FiltroMapaActivity extends AppCompatActivity {
    Slider distancia, tiempo;
    RangeSlider peso;
    CheckBox nobusquedaperros;
    Button comenzarPaseo;
    TextView display;
    ImageView image;
    int distanciaNum, tiempoNum, pesoMin, pesoMax;

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
        image = findViewById(R.id.imageDisplay);

        distanciaNum = 0;
        tiempoNum = 0;
        pesoMin = 0;
        pesoMax = 0;
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
                //image.setImageURI();
                if (value < 30){
                    image.setImageResource(@DrawableRes)
                }
                if (value > 30 && value < 90)
            }
        });

        peso.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {

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
    }
    private void cargarDatos (){
        SharedPreferences.Editor edit = mPref.edit();
        String Parameters = "";
        if(nobusquedaperros.isChecked()){
            Parameters += "0,0,0,0";
        }
        else{
            Parameters += distanciaNum +"," + tiempoNum + "," + pesoMin + "," + pesoMax;
        }
        edit.putString("parameters", Parameters);
        edit.apply();
    }
}