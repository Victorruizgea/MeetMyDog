package com.ucm.meetmydog;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class InicialActivity extends AppCompatActivity {
    Button editarPerfil;
    Button paseo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicial);
        editarPerfil=findViewById(R.id.botoneditar);
        paseo=findViewById(R.id.paseo);
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
    }
}