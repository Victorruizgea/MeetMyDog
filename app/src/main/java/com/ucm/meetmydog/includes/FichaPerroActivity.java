package com.ucm.meetmydog.includes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ucm.meetmydog.modelos.Perro;
import com.ucm.meetmydog.R;

public class FichaPerroActivity extends AppCompatActivity {
    private TextView nombre;
    private TextView raza;
    private TextView edad;
    private TextView descripcion;
    private TextView peso;
    private ImageView imagen;

    private StorageReference mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ficha_perro);
        Intent intent= getIntent();
        Perro perro= (Perro) intent.getSerializableExtra("datos");
        nombre=findViewById(R.id.nombreFichaPerro);
        nombre.setText(perro.getNombre());
        raza=findViewById(R.id.razaFicha);
        raza.setText(perro.getRaza());
        edad=findViewById(R.id.edadFicha);
        edad.setText(perro.getEdad()+" años");
        descripcion=findViewById(R.id.descripcionFicha);
        descripcion.setText(perro.getDescripcion());
        peso=findViewById(R.id.pesoFicha);
        peso.setText(perro.getPeso()+" kg");
        imagen=findViewById(R.id.imagenPerfilPerro);

        descargarImagen("imagenesPerro/" + perro.getImagenUri());

    }

    private void descargarImagen(String imagenUri) {

        mStorage = FirebaseStorage.getInstance().getReference().child( imagenUri);
        mStorage.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imagen.setImageBitmap(bitmap);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("PerfilUsuario", "Error al descargar la imagen", e);
            }
        });
    }
}