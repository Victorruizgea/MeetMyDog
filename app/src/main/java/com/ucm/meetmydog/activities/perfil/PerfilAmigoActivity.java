package com.ucm.meetmydog.activities.perfil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.ucm.meetmydog.R;
import com.ucm.meetmydog.activities.mapa.FiltroMapaActivity;
import com.ucm.meetmydog.activities.mapa.SeleccionPerrosActivity;
import com.ucm.meetmydog.includes.FichaPerroActivity;
import com.ucm.meetmydog.modelos.Perro;
import com.ucm.meetmydog.activities.home.InicialActivity;

import nl.joery.animatedbottombar.AnimatedBottomBar;

public class PerfilAmigoActivity extends AppCompatActivity {
    private TextView nombreUsuarioTextView;
    private TextView emailUsuarioTextView;
    private ImageView imagenUsuarioView;
    private LinearLayout contenedorPerros;

    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private AnimatedBottomBar bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_amigo);
        nombreUsuarioTextView = findViewById(R.id.nombreUsuarioPerfilAmigo);
        imagenUsuarioView = findViewById(R.id.imageUsuarioPerfilAmigo);
        emailUsuarioTextView=findViewById(R.id.emailUsuarioPerfilAmigo);
        contenedorPerros=findViewById(R.id.contenedorPerrosAmigo);
        mDatabase = FirebaseDatabase.getInstance("https://meetmydog-6a9f5-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        mAuth = FirebaseAuth.getInstance();
        bottomBar = findViewById(R.id.bottom_bar);
        bottomBar.setOnTabSelectListener(new AnimatedBottomBar.OnTabSelectListener() {
            @Override
            public void onTabSelected(int i, @Nullable AnimatedBottomBar.Tab tab, int i1, @NonNull AnimatedBottomBar.Tab tab1) {
                switch (i1) {
                    case 0:
                        Intent intent1 = new Intent(PerfilAmigoActivity.this, InicialActivity.class);
                        startActivity(intent1);
                        break;
                    case 1:
                        Intent intent2 = new Intent(PerfilAmigoActivity.this, SeleccionPerrosActivity.class);
                        startActivity(intent2);
                        break;
                    case 2:
                        Intent intent3 = new Intent(PerfilAmigoActivity.this, PerfilUsuarioActivity.class);
                        startActivity(intent3);
                        break;
                }
            }


            @Override
            public void onTabReselected(int i, @NonNull AnimatedBottomBar.Tab tab) {
            }
        });

        String id=getIntent().getStringExtra("id");
        String nombreAmigo= getIntent().getStringExtra("nombre");
        String correoAmigo= getIntent().getStringExtra("email");
        String imagenUriAmigo= getIntent().getStringExtra("imagen");
        if(imagenUriAmigo==null){
            Drawable drawable = getResources().getDrawable(R.drawable.defaultperro);
            imagenUsuarioView.setImageDrawable(drawable);
        }else{
            descargarImagen("imagenesUsuario/" + imagenUriAmigo,imagenUsuarioView);
        }

        nombreUsuarioTextView.setText(nombreAmigo);
        emailUsuarioTextView.setText(correoAmigo);

        mDatabase.child("user").child(id).child("perros").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot perroSnapshot : snapshot.getChildren()) {

                    Perro perro = perroSnapshot.getValue(Perro.class);
                    CardView cardView = (CardView) LayoutInflater.from(PerfilAmigoActivity.this).inflate(R.layout.cardviewperro4, contenedorPerros, false);
                    ImageView imagenCard = cardView.findViewById(R.id.imagePerroAmigoCard);
                    TextView nombreCard = cardView.findViewById(R.id.nombrePerroAmigoCard);
                    Button verPerfil = cardView.findViewById(R.id.verPerfilAmigo);

                    nombreCard.setText(perro.getNombre());
                    String imagenUri = perro.getImagenUri();
                    if (imagenUri == null) {
                        Drawable drawable = getResources().getDrawable(R.drawable.defaultperro);
                        imagenCard.setImageDrawable(drawable);
                    } else {
                        descargarImagen("imagenesPerro/" + imagenUri, imagenCard);
                    }

                    verPerfil.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(PerfilAmigoActivity.this, FichaPerroActivity.class);
                            i.putExtra("datos", perro);
                            startActivity(i);

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
                Log.w("PerfilAmigo", "Error al descargar la imagen", e);
            }
        });
    }
}

