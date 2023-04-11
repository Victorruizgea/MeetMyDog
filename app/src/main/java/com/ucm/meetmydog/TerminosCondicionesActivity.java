package com.ucm.meetmydog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TerminosCondicionesActivity extends AppCompatActivity {
    CheckBox Condicion1, Condicion2;
    Button AcceptTerm, CancelTerm;

    DatabaseReference mDatabase;
    FirebaseAuth auth;
    String nombreUsuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminos_condiciones);
        Condicion1 = findViewById(R.id.Condicion1);
        Condicion2 = findViewById(R.id.Condicion2);
        AcceptTerm = findViewById(R.id.AceptarTerm);
        CancelTerm = findViewById(R.id.CancelarTerm);
        auth = FirebaseAuth.getInstance();
        nombreUsuario=getIntent().getStringExtra("usuario");
        AcceptTerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickAccept();
            }
        });

        CancelTerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickCancel();
            }
        });
    }
    private void clickAccept(){
        if(Condicion1.isChecked() && Condicion2.isChecked()){
            mDatabase = FirebaseDatabase.getInstance("https://meetmydog-6a9f5-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

            String Uid = auth.getCurrentUser().getUid();
            mDatabase.child("user").child(Uid).child("Term").setValue("1").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Intent intent = new Intent(TerminosCondicionesActivity.this, CrearPerfilActivity.class);
                    intent.putExtra("nombre",nombreUsuario);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            });
        }
    }
    private void clickCancel(){
        Intent intent = new Intent(TerminosCondicionesActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}