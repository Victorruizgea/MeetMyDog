package com.ucm.meetmydog.activities.home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ucm.meetmydog.R;
import com.ucm.meetmydog.activities.mapa.SeleccionPerrosActivity;
import com.ucm.meetmydog.activities.perfil.PerfilUsuarioActivity;
import com.ucm.meetmydog.modelos.Mensaje;

import java.util.Calendar;
import java.util.Map;
import java.util.UUID;

import nl.joery.animatedbottombar.AnimatedBottomBar;

public class BuzonActivity extends AppCompatActivity {
    private LinearLayout contenedorMensajes;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private AnimatedBottomBar bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buzon);
        contenedorMensajes=findViewById(R.id.contenedorMensajes);
        mDatabase = FirebaseDatabase.getInstance("https://meetmydog-6a9f5-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        mAuth = FirebaseAuth.getInstance();
        String id = mAuth.getCurrentUser().getUid();
        bottomBar = findViewById(R.id.bottom_bar);
        bottomBar.setOnTabSelectListener(new AnimatedBottomBar.OnTabSelectListener() {
            @Override
            public void onTabSelected(int i, @Nullable AnimatedBottomBar.Tab tab, int i1, @NonNull AnimatedBottomBar.Tab tab1) {
                switch (i1) {
                    case 0:
                        Intent intent1 = new Intent(BuzonActivity.this, InicialActivity.class);
                        startActivity(intent1);
                        break;
                    case 1:
                        Intent intent2 = new Intent(BuzonActivity.this, SeleccionPerrosActivity.class);
                        startActivity(intent2);
                        break;
                    case 2:
                        Intent intent3 = new Intent(BuzonActivity.this, PerfilUsuarioActivity.class);
                        startActivity(intent3);
                        break;
                }
            }


            @Override
            public void onTabReselected(int i, @NonNull AnimatedBottomBar.Tab tab) {
            }
        });
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
                    Button eliminar= cardView.findViewById(R.id.buttonEliminarMensaje);
                    fechaText.setText(mensaje.getFecha());
                    mensajeText.setText(mensaje.getTexto());
                    usuarioText.setText(mensaje.getNombreOrigen());
                    eliminar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            mDatabase.child("user").child(id).child("mensajes").child(mensajeSnapshot.getKey()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(BuzonActivity.this,"Mensaje eliminado correctamente",Toast.LENGTH_LONG);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(BuzonActivity.this,"No se ha podido eliminar el mensaje",Toast.LENGTH_LONG);
                                }
                            });
                            recreate();
                        }
                    });
                    responder.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(BuzonActivity.this);
                            LayoutInflater inflater = getLayoutInflater();
                            View dialogo = inflater.inflate(R.layout.dialogo_enviar_mensaje, null);
                            builder.setView(dialogo);
                            EditText text = dialogo.findViewById(R.id.textDialog);
                            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });

                            builder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (text.getText().toString().isEmpty()) {
                                        Toast.makeText(builder.getContext(), "No se puede enviar un mensaje vacio", Toast.LENGTH_LONG).show();
                                    } else {
                                        Calendar calendar = Calendar.getInstance();
                                        int year = calendar.get(Calendar.YEAR);
                                        int month = calendar.get(Calendar.MONTH);
                                        int day = calendar.get(Calendar.DAY_OF_MONTH);
                                        String fecha = day + "/" + (month + 1) + "/" + year;
                                        UUID uuid = UUID.randomUUID();
                                        String idMensaje = uuid.toString();
                                        mDatabase.child("user").child(id).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                Map<String, Object> usuarioMap = (Map<String, Object>) snapshot.getValue();
                                                Mensaje mensaje = new Mensaje(text.getText().toString(), id, (String) usuarioMap.get("nombre"), fecha);
                                                mDatabase.child("user").child(id).child("mensajes").child(idMensaje).setValue(mensaje).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(BuzonActivity.this, "Mensaje enviado", Toast.LENGTH_LONG).show();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(BuzonActivity.this, "No se ha podido enviar el mensaje", Toast.LENGTH_LONG).show();
                                                    }
                                                });

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });


                                    }
                                    dialogInterface.dismiss();


                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }

                        });

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