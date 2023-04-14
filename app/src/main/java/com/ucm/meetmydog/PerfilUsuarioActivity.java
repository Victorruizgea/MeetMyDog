package com.ucm.meetmydog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PerfilUsuarioActivity extends AppCompatActivity {
    private TextView nombreUsuarioTextView;
    private TextView emailUsuarioTextView;
    private ImageView imagenUsuarioView;
    private LinearLayout contenedorPerros;
    private Button editarPerfilUsuario;
    private Button anadirPerro;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        nombreUsuarioTextView = findViewById(R.id.nombreUsuarioPerfil);
        imagenUsuarioView = findViewById(R.id.imageUsuarioPerfil);
        emailUsuarioTextView=findViewById(R.id.emailUsuarioPerfil);
        contenedorPerros=findViewById(R.id.contenedorPerros);
        editarPerfilUsuario=findViewById(R.id.editarPerfilUsuario);
        anadirPerro=findViewById(R.id.anadirPerroBoton);
        cargarDatosUsuario();
        editarPerfilUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(PerfilUsuarioActivity.this, EditarPerfilUsuario.class);
                startActivity(i);
            }
        });
        anadirPerro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(PerfilUsuarioActivity.this, CrearPerfilPerroActivity.class);
                startActivity(i);
            }
        });


    }

    private void cargarDatosUsuario() {
        mDatabase = FirebaseDatabase.getInstance("https://meetmydog-6a9f5-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        mAuth = FirebaseAuth.getInstance();
        String id = mAuth.getCurrentUser().getUid();
        mDatabase.child("user").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Object> usuarioMap = (Map<String, Object>) snapshot.getValue();
                nombreUsuarioTextView.setText((CharSequence) usuarioMap.get("nombre"));
                emailUsuarioTextView.setText((CharSequence) usuarioMap.get("email"));
                String imagenUri = (String) usuarioMap.get("imagen");
                if(imagenUri==null){
                    Drawable drawable = getResources().getDrawable(R.drawable.defaultperfil);
                    imagenUsuarioView.setImageDrawable(drawable);

                    //imagenUsuarioView.setImageResource(R.drawable.defaultperfil);

                }else {
                    descargarImagen("imagenesUsuario/" + imagenUri,imagenUsuarioView);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        mDatabase.child("user").child(id).child("perros").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot perroSnapshot : snapshot.getChildren()) {
                    // Obtén el usuario actual como un objeto de la clase Usuario
                    Perro perro = perroSnapshot.getValue(Perro.class);
                    CardView cardView = (CardView) LayoutInflater.from(PerfilUsuarioActivity.this).inflate(R.layout.cardviewperro, contenedorPerros, false);
                    ImageView imagenCard = cardView.findViewById(R.id.imagePerroCard);
                    TextView nombreCard=   cardView.findViewById(R.id.nombrePerroCard);
                    Button verPerfil= cardView.findViewById(R.id.verPerfil);
                    Button eliminar= cardView.findViewById(R.id.eliminar);
                    nombreCard.setText(perro.getNombre());
                    String imagenUri=perro.getImagenUri();
                    if(imagenUri==null){
                        Drawable drawable = getResources().getDrawable(R.drawable.defaultperro);
                        imagenCard.setImageDrawable(drawable);
                    }else{
                        descargarImagen("imagenesPerro/" + imagenUri,imagenCard);
                    }
                    eliminar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mDatabase.child("user").child(id).child("perros").child(perro.getNombre()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    recreate();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                        }
                    });

                    // Configura el cardView aquí
                    contenedorPerros.addView(cardView);

                    int totalWidth = contenedorPerros.getChildCount() * cardView.getLayoutParams().width;

                    contenedorPerros.getLayoutParams().width = totalWidth;


                }

             }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

    private void descargarImagen(String imagenUri,ImageView view) {

        mStorage = FirebaseStorage.getInstance().getReference().child( imagenUri);
        mStorage.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                view.setImageBitmap(bitmap);
                
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("PerfilUsuario", "Error al descargar la imagen", e);
            }
        });
    }
}