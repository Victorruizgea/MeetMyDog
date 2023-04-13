package com.ucm.meetmydog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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

public class PerfilActivity extends AppCompatActivity {
    private TextView nombreUsuarioTextView;
    private ImageView imagenUsuarioView;
    private LinearLayout contenedorPerros;

    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        nombreUsuarioTextView = findViewById(R.id.nombreUsuarioPerfil);
        imagenUsuarioView = findViewById(R.id.imageUsuarioPerfil);
        contenedorPerros=findViewById(R.id.contenedorPerros);
        cargarDatosUsuario();

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
                String imagenUri = (String) usuarioMap.get("imagen");
                descargarImagen("imagenesUsuario/"+imagenUri);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        mDatabase.child("user").child(id).child("perros").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Perro> perros=new ArrayList<>();
                for (DataSnapshot perroSnapshot : snapshot.getChildren()) {
                    // Obtén el usuario actual como un objeto de la clase Usuario
                    Perro perro = perroSnapshot.getValue(Perro.class);
                    CardView cardView = (CardView) LayoutInflater.from(PerfilActivity.this).inflate(R.layout.cardviewperro, contenedorPerros, false);
                    ImageView imagenCard = cardView.findViewById(R.id.imagePerroCard);
                    TextView nombreCard=   cardView.findViewById(R.id.nombrePerroCard);
                    nombreCard.setText(perro.getNombre());

                    // Configura el cardView aquí
                    contenedorPerros.addView(cardView);

                    // Agrega el usuario a la lista de usuarios
                    listaUsuarios.add(usuario);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        perros= (List<Perro>) usuarioMap.get("perros");
        for (Perro perro : perros) {

        }

    }

    private Bitmap descargarImagen(String imagenUri) {

        mStorage = FirebaseStorage.getInstance().getReference().child( imagenUri);
        mStorage.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imagenUsuarioView.setImageBitmap(bitmap);
                
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("EditarPerfil", "Error al descargar la imagen", e);
            }
        });
    }
}