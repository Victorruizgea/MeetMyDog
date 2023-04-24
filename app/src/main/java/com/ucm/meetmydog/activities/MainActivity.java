package com.ucm.meetmydog.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ucm.meetmydog.R;
import com.ucm.meetmydog.activities.autentificacion.RegistroActivity;
import com.ucm.meetmydog.activities.autentificacion.InicioSesionActivity;

public class MainActivity extends AppCompatActivity {
    Button registroBoton;
    Button iniciarSesionBoton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registroBoton=findViewById(R.id.registrarse);
        iniciarSesionBoton=findViewById(R.id.iniciarSesion);


      registroBoton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent i= new Intent (MainActivity.this, RegistroActivity.class);
                startActivity(i);
          }
      });

        iniciarSesionBoton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent (MainActivity.this, InicioSesionActivity.class);
                startActivity(i);
            }
        });
    }
}