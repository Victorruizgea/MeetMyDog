package com.ucm.meetmydog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class CrearPerfilActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST_CODE = 100;
    EditText nombrePerro;
    EditText descripcionPerro;
    ImageView imagenPerfil;

    StorageReference mStorage;
    DatabaseReference mDatabase;
    String uri;

    String nombreUsuario;
    Button guardar;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crearperfil);
        mStorage = FirebaseStorage.getInstance().getReference();
        nombreUsuario=getIntent().getStringExtra("nombre");
        nombrePerro=findViewById(R.id.nombrePerro);
        descripcionPerro=findViewById(R.id.descripcionPerro);
        mAuth=FirebaseAuth.getInstance();
        imagenPerfil=findViewById(R.id.imagenPerfil);
        imagenPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onImageViewClicked(view);
            }
        });


        guardar=findViewById(R.id.guardarPerfil);
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardarPerfil();
            }
        });

    }

    private void guardarPerfil() {
        String nombre= String.valueOf(nombrePerro.getText());
        String descripcion= String.valueOf(descripcionPerro.getText());
        String id = mAuth.getCurrentUser().getUid();
        PerfilUsuario perfil=new PerfilUsuario(nombre,descripcion,uri);
        mDatabase = FirebaseDatabase.getInstance("https://meetmydog-6a9f5-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        mDatabase.child("user").child(id).child("perfil").setValue(perfil).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent intent = new Intent(CrearPerfilActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public void onImageViewClicked(View view) {
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
            StorageReference filepath = mStorage.child("imagenesPerfil").child(uri);
            filepath.putFile(imagenUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(CrearPerfilActivity.this,"Imagen guardada correctamente",Toast.LENGTH_LONG).show();
                }
            });

            imagenPerfil.setImageURI(imagenUri);

        }
    }


}