package com.ucm.meetmydog;

import androidx.annotation.NonNull;
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

public class SeleccionPerrosActivity extends AppCompatActivity {

    private LinearLayout contenedorPerros;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    SharedPreferences mPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccion_perros);
        mDatabase = FirebaseDatabase.getInstance("https://meetmydog-6a9f5-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        mAuth = FirebaseAuth.getInstance();
        String id = mAuth.getCurrentUser().getUid();
        contenedorPerros=findViewById(R.id.contenedorPerrosSeleccionar);
        mPreference = getApplicationContext().getSharedPreferences("typeUser", MODE_PRIVATE);

        mDatabase.child("user").child(id).child("perros").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot perroSnapshot : snapshot.getChildren()) {
                    // Obtén el usuario actual como un objeto de la clase Usuario
                    Perro perro = perroSnapshot.getValue(Perro.class);
                    CardView cardView = (CardView) LayoutInflater.from(SeleccionPerrosActivity.this).inflate(R.layout.cardviewperro2, contenedorPerros, false);
                    ImageView imagenCard = cardView.findViewById(R.id.imagePerroSeleccionCard);
                    TextView nombreCard = cardView.findViewById(R.id.nombrePerroSeleccionCard);
                    Button seleccionar= cardView.findViewById(R.id.seleccionar);

                    nombreCard.setText(perro.getNombre());
                    String imagenUri = perro.getImagenUri();
                    if (imagenUri == null) {
                        Drawable drawable = getResources().getDrawable(R.drawable.defaultperro);
                        imagenCard.setImageDrawable(drawable);
                    } else {
                        descargarImagen("imagenesPerro/" + imagenUri, imagenCard);
                    }
                    seleccionar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i= new Intent(SeleccionPerrosActivity.this,FiltroMapaActivity.class);
                            SharedPreferences.Editor edit = mPreference.edit();
                            String Parameters = perro.getNombre();
                            edit.putString("perros", Parameters);
                            edit.apply();
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
                Log.w("PerfilUsuario", "Error al descargar la imagen", e);
            }
        });
    }
}

