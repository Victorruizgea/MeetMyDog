package com.ucm.meetmydog.activities.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ucm.meetmydog.R;
import com.ucm.meetmydog.activities.perfil.PerfilUsuarioActivity;
import com.ucm.meetmydog.modelos.Mensaje;

public class BuzonActivity extends AppCompatActivity {
    private LinearLayout contenedorMensajes;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buzon);
        contenedorMensajes=findViewById(R.id.contenedorMensajes);
        mDatabase = FirebaseDatabase.getInstance("https://meetmydog-6a9f5-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        mAuth = FirebaseAuth.getInstance();
        String id = mAuth.getCurrentUser().getUid();
        mDatabase.child("user").child(id).child("mensajes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot mensajeSnapshot:snapshot.getChildren()){
                    Mensaje mensaje=mensajeSnapshot.getValue(Mensaje.class);
                    CardView cardView = (CardView) LayoutInflater.from(BuzonActivity.this).inflate(R.layout.cardview_mensaje, contenedorMensajes, false);
                    TextView fechaText= cardView.findViewById(R.id.fechaMensaje);
                    TextView mensajeText= cardView.findViewById(R.id.mensajeCard);
                    TextView usuarioText= cardView.findViewById(R.id.nombreOrigenCard);
                    Button responder= cardView.findViewById(R.id.buttonResponder);
                    fechaText.setText(mensaje.getFecha());
                    mensajeText.setText(mensaje.getTexto());
                    usuarioText.setText(mensaje.getNombreOrigen());
                    contenedorMensajes.addView(cardView);
                    int totalWidth = contenedorMensajes.getChildCount() * cardView.getLayoutParams().width;

                    contenedorMensajes.getLayoutParams().width = totalWidth;


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}