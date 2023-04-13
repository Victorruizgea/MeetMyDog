package com.ucm.meetmydog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class imagenUsuarioActivity extends AppCompatActivity {
    private static final int GALLERY_REQUEST_CODE = 100;
    private Button cargarImagen;
    private Button guardarImagen;
    private ImageView imagenView;

    private Boolean imagen_cargada=false;
    private String uri=null;

    private StorageReference mStorage;
    private DatabaseReference mDatabase;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagen_usuario);
        mStorage = FirebaseStorage.getInstance().getReference();
        cargarImagen=findViewById(R.id.cargar);
        guardarImagen=findViewById(R.id.guardar);
        imagenView=findViewById(R.id.imageUsuario);
        mAuth=FirebaseAuth.getInstance();

        cargarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cargar();
            }
        });
        guardarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               guardar();
            }
        });

    }

    private void guardar() {
        String id = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance("https://meetmydog-6a9f5-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        HashMap<String, Object> nuevoCampo = new HashMap<>();
        nuevoCampo.put("imagen", uri);
        mDatabase.child("user").child(id).updateChildren(nuevoCampo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent intent = new Intent(imagenUsuarioActivity.this, CrearPerfilPerroActivity.class);
                startActivity(intent);
            }
        });

    }

    private void cargar() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST_CODE);


    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri imagenUri = data.getData();
            uri=imagenUri.getLastPathSegment();
            StorageReference filepath = mStorage.child("imagenesUsuario").child(uri);
            filepath.putFile(imagenUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(imagenUsuarioActivity.this,"Imagen guardada correctamente",Toast.LENGTH_LONG).show();
                }
            });

            imagenView.setImageURI(imagenUri);
            imagen_cargada=true;

        }
    }
}