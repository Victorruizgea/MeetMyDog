package com.ucm.meetmydog.activities.autentificacion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ucm.meetmydog.R;
import com.ucm.meetmydog.activities.home.InicialActivity;

public class InicioSesionActivity extends AppCompatActivity {
    EditText email;
    EditText password;
    Button inicioSesion;
    DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_sesion);
        mAuth= FirebaseAuth.getInstance();

        email=findViewById(R.id.mailIncioSesion);
        password=findViewById(R.id.psswdIncioSesion);
        inicioSesion=findViewById(R.id.buttonInicioSesion);

        inicioSesion.setOnClickListener(v -> {
            String correo = email.getText().toString();
            String passw=password.getText().toString();
            if(correo.isEmpty()||passw.isEmpty()||passw.length()<6){
                Toast.makeText(InicioSesionActivity.this,"Complete todos los campos",Toast.LENGTH_LONG).show();
            }else{
                inicioSesionUsuario(correo,passw);
            }
        });
    }
    private void inicioSesionUsuario(String email, String passwd) {
        mAuth.signInWithEmailAndPassword(email, passwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    String id = mAuth.getCurrentUser().getUid();
                    mDatabase = FirebaseDatabase.getInstance("https://meetmydog-6a9f5-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
                    mDatabase.child("user").child(id).child("Term").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            String Acceptance = task.getResult().getValue(String.class);
                            //Comprobamos si los términos han sido aceptados, si han sido (1), el usuario va directamente a La Actividad INicial del Usuario,
                            // si no, se le lleva de uevo a Terminos y condiciones para que los acepte
                            if (Acceptance.equals("1")) {
                                Intent intent = new Intent(InicioSesionActivity.this, InicialActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(InicioSesionActivity.this, TerminosCondicionesActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        }
                    });
                }
            }
        });
    }

}


