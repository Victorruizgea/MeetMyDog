package com.ucm.meetmydog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegistroActivity extends AppCompatActivity {
    EditText usuario;
    EditText email;
    EditText password;
    Button registro;

    DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        mAuth=FirebaseAuth.getInstance();

        usuario=findViewById(R.id.nombreUsuario);
        email=findViewById(R.id.emailUsuario);
        password=findViewById(R.id.passwordUsuario);
        registro=findViewById(R.id.botonRegistro);

        registro.setOnClickListener(v -> {
            String nombre = usuario.getText().toString();
            String correo = email.getText().toString();
            String passw=password.getText().toString();
            if(nombre.isEmpty()||correo.isEmpty()||passw.isEmpty()||passw.length()<6){
                Toast.makeText(RegistroActivity.this,"Complete todos los campos",Toast.LENGTH_LONG).show();
            }else{
                registroUsuario(nombre,correo,passw);
            }
        });

    }
    private void registroUsuario(String nombre, String correo, String passw) {
        mAuth.createUserWithEmailAndPassword(correo,passw).addOnCompleteListener(task -> {
            String id = mAuth.getCurrentUser().getUid();
            Usuario user = new Usuario(nombre, correo, passw);
            mDatabase = FirebaseDatabase.getInstance("https://meetmydog-6a9f5-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
            mDatabase.child("user").child(id).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    mDatabase.child("user").child(id).child("Term").setValue("0").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Intent intent = new Intent(RegistroActivity.this, TerminosCondicionesActivity.class);
                            startActivity(intent);
                        }
                    });
                }
            });
        });
    }
}