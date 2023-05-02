package com.ucm.meetmydog.activities.perfil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ucm.meetmydog.R;

import java.util.HashMap;
import java.util.Map;

public class EditarPerfilUsuario extends AppCompatActivity {
    private static final int GALLERY_REQUEST_CODE = 100;
    private EditText nombreUsuario;
    private ImageView imageView;
    private String uri;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private Button guardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil_usuario);
        mAuth=FirebaseAuth.getInstance();
        String id = mAuth.getCurrentUser().getUid();
        nombreUsuario=findViewById(R.id.nombreUsuarioEdit);
        imageView=findViewById(R.id.imagenUsuarioEdit);
        guardar=findViewById(R.id.guardarUsuarioEdit);
        mDatabase = FirebaseDatabase.getInstance("https://meetmydog-6a9f5-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase.child("user").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Object> usuarioMap = (Map<String, Object>) snapshot.getValue();
                nombreUsuario.setText((CharSequence) usuarioMap.get("nombre"));

                String imagenUri= (String) usuarioMap.get("imagen");
                descargarImagen(imagenUri);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onImageViewClicked(view);
            }
        });
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardarEditarPerfil();
            }
        });



    }
    private void guardarEditarPerfil() {
        String nombre= String.valueOf(nombreUsuario.getText());

        String id = mAuth.getCurrentUser().getUid();
        //Usuario usuario=new Usuario(nombre,correo,password,uri);
        DatabaseReference userRef= mDatabase.child("user").child(id);
        Map<String, Object> updates = new HashMap<>();
        updates.put("nombre", nombre);
        updates.put("imagen", uri);
        userRef.updateChildren(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent intent = new Intent(EditarPerfilUsuario.this, PerfilUsuarioActivity.class);
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
            StorageReference filepath = mStorage.child("imagenesUsuario").child(uri);
            filepath.putFile(imagenUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(EditarPerfilUsuario.this,"Imagen guardada correctamente",Toast.LENGTH_LONG).show();
                }
            });

            imageView.setImageURI(imagenUri);

        }
    }
    private void descargarImagen(String imagenUri) {
        mStorage.child("imagenesUsuario/"+imagenUri).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(bitmap);
                uri=imagenUri;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("EditarPerfil", "Error al descargar la imagen", e);
            }
        });
    }
}