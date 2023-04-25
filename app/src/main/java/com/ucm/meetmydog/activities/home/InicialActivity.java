package com.ucm.meetmydog.activities.home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ucm.meetmydog.activities.perfil.PerfilUsuarioActivity;
import com.ucm.meetmydog.R;
import com.ucm.meetmydog.activities.mapa.SeleccionPerrosActivity;

import nl.joery.animatedbottombar.AnimatedBottomBar;

public class InicialActivity extends AppCompatActivity {
    Button editarPerfil;
    Button paseo;
    Button buzon;

    private AnimatedBottomBar bottomBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicial);
        editarPerfil=findViewById(R.id.botoneditar);
        paseo=findViewById(R.id.paseo);
        buzon=findViewById(R.id.buttonbuzon);
        bottomBar = findViewById(R.id.bottom_bar);
        editarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(InicialActivity.this, PerfilUsuarioActivity.class);
                startActivity(intent);
            }
        });
        paseo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(InicialActivity.this, SeleccionPerrosActivity.class);
                startActivity(intent);
            }
        });
        buzon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(InicialActivity.this, BuzonActivity.class);
                startActivity(intent);
            }
        });
        bottomBar.setOnTabSelectListener(new AnimatedBottomBar.OnTabSelectListener() {
            @Override
            public void onTabSelected(int i, @Nullable AnimatedBottomBar.Tab tab, int i1, @NonNull AnimatedBottomBar.Tab tab1) {
                switch (i1) {
                    case 0:
                        Intent intent1 = new Intent(InicialActivity.this, InicialActivity.class);
                        startActivity(intent1);
                        break;
                    case 1:
                        Intent intent2 = new Intent(InicialActivity.this, SeleccionPerrosActivity.class);
                        startActivity(intent2);
                        break;
                    case 2:
                        Intent intent3 = new Intent(InicialActivity.this, PerfilUsuarioActivity.class);
                        startActivity(intent3);
                        break;
                }
            }


            @Override
            public void onTabReselected(int i, @NonNull AnimatedBottomBar.Tab tab) {}
        });
    }
}