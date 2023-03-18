package com.ucm.meetmydog;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistroActivity extends AppCompatActivity {
    EditText usuario;
    EditText email;
    EditText password;
    Button registro;

    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        mFirestore=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();

        usuario=findViewById(R.id.nombreUsuario);
        email=findViewById(R.id.emailUsuario);
        password=findViewById(R.id.passwordUsuario);
        registro=findViewById(R.id.botonRegistro);

        registro.setOnClickListener(v -> {
            String nombre = usuario.getText().toString();
            String correo = email.getText().toString();
            String passw=password.getText().toString();
            if(nombre.isEmpty()||correo.isEmpty()||passw.isEmpty()){
                Toast.makeText(RegistroActivity.this,"Complete todos los campos",Toast.LENGTH_LONG).show();
            }else{
                registroUsuario(nombre,correo,passw);
            }
        });

    }
    private void registroUsuario(String nombre, String correo, String passw) {
        mAuth.createUserWithEmailAndPassword(correo,passw).addOnCompleteListener(task -> {
            String id = mAuth.getCurrentUser().getUid();
            Map<String,Object> map= new HashMap<>();
            map.put("id",id);
            map.put("name",nombre);
            map.put("email",correo);
            map.put("password",passw);
            mFirestore.collection("user").document(id).set(map).addOnSuccessListener(unused -> {
                finish();
                startActivity(new Intent(RegistroActivity.this,MainActivity.class));
                Toast.makeText(RegistroActivity.this,"Usuario registrado correctamente",Toast.LENGTH_LONG).show();
            }).addOnFailureListener(e -> Toast.makeText(RegistroActivity.this,"Error al guardar",Toast.LENGTH_LONG).show());
        }).addOnFailureListener(e -> Toast.makeText(RegistroActivity.this,"Error al registrar",Toast.LENGTH_LONG).show());
    }
}