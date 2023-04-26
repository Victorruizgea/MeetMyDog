package com.ucm.meetmydog.activities.home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ucm.meetmydog.activities.NoticiasLoader;
import com.ucm.meetmydog.activities.mapa.ListaPerrosActivity;
import com.ucm.meetmydog.activities.perfil.PerfilAmigoActivity;
import com.ucm.meetmydog.activities.perfil.PerfilUsuarioActivity;
import com.ucm.meetmydog.R;
import com.ucm.meetmydog.activities.mapa.SeleccionPerrosActivity;
import com.ucm.meetmydog.modelos.Mensaje;
import com.ucm.meetmydog.modelos.NoticiasPerro;
import com.ucm.meetmydog.modelos.Perro;
import com.ucm.meetmydog.modelos.Usuario;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import nl.joery.animatedbottombar.AnimatedBottomBar;

public class InicialActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<NoticiasPerro>> {

    Button buzon;

    private AnimatedBottomBar bottomBar;
    private List<NoticiasPerro> listaNoticias;
    private LinearLayout contenedorAmigos;
    private LinearLayout contenedorNoticias;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private StorageReference mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicial);
        mDatabase = FirebaseDatabase.getInstance("https://meetmydog-6a9f5-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        mAuth = FirebaseAuth.getInstance();
        String id = mAuth.getCurrentUser().getUid();

        LoaderManager loaderManager = LoaderManager.getInstance(this);
        loaderManager.initLoader(0, null, this);
        buzon = findViewById(R.id.buttonbuzon);
        bottomBar = findViewById(R.id.bottom_bar);
        contenedorAmigos = findViewById(R.id.contenedorAmigos);
        contenedorNoticias = findViewById(R.id.contenedorNoticias);
        buzon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InicialActivity.this, BuzonActivity.class);
                startActivity(intent);
            }
        });
        bottomBar.setOnTabSelectListener(new AnimatedBottomBar.OnTabSelectListener() {
            @Override
            public void onTabSelected(int i, @Nullable AnimatedBottomBar.Tab tab, int i1, @NonNull AnimatedBottomBar.Tab tab1) {
                switch (i1) {
                    case 0:
                        Intent intent1 = new Intent(InicialActivity.this, InicialActivity.class);
                        startActivity(intent1);
                        break;
                    case 1:
                        Intent intent2 = new Intent(InicialActivity.this, SeleccionPerrosActivity.class);
                        startActivity(intent2);
                        break;
                    case 2:
                        Intent intent3 = new Intent(InicialActivity.this, PerfilUsuarioActivity.class);
                        startActivity(intent3);
                        break;
                }
            }


            @Override
            public void onTabReselected(int i, @NonNull AnimatedBottomBar.Tab tab) {
            }
        });
        mDatabase.child("user").child(id).child("amigos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot amigosnapshot : snapshot.getChildren()) {
                    String idAmigo = amigosnapshot.getValue(String.class);
                    CardView cardView = (CardView) LayoutInflater.from(InicialActivity.this).inflate(R.layout.cardview_amigo, contenedorAmigos, false);

                    mDatabase.child("user").child(idAmigo).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Map<String, Object> usuarioMap = (Map<String, Object>) snapshot.getValue();
                            ImageView imagenCard = cardView.findViewById(R.id.imageAmigo);
                            TextView nombreAmigoCard = cardView.findViewById(R.id.nombreAmigo);
                            ImageView perfil = cardView.findViewById(R.id.imageViewVerPerfil);
                            ImageView enviarMensaje = cardView.findViewById(R.id.imageViewEnviar);
                            ImageView eliminarAmigo = cardView.findViewById(R.id.imageViewDejarSeguir);
                            nombreAmigoCard.setText((CharSequence) usuarioMap.get("nombre"));
                            String imagenUri = (String) usuarioMap.get("imagen");
                            if (imagenUri == null) {
                                Drawable drawable = getResources().getDrawable(R.drawable.defaultperro);
                                imagenCard.setImageDrawable(drawable);
                            } else {
                                descargarImagen("imagenesUsuario/" + imagenUri, imagenCard);
                            }
                            List<Perro> listaPerros=new ArrayList<>();

                            perfil.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent i = new Intent(InicialActivity.this, PerfilAmigoActivity.class);
                                    i.putExtra("nombre", (String) usuarioMap.get("nombre"));
                                    i.putExtra("imagen",(String) usuarioMap.get("imagen"));
                                    i.putExtra("email",(String) usuarioMap.get("email"));
                                    i.putExtra("id", idAmigo);
                                    startActivity(i);

                                }
                            });
                            enviarMensaje.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(InicialActivity.this);
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
                                                        mDatabase.child("user").child(idAmigo).child("mensajes").child(idMensaje).setValue(mensaje).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                Toast.makeText(InicialActivity.this, "Mensaje enviado", Toast.LENGTH_LONG).show();
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(InicialActivity.this, "No se ha podido enviar el mensaje", Toast.LENGTH_LONG).show();
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
                            eliminarAmigo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    DatabaseReference amigosRef = mDatabase.child("user").child(id).child("amigos");
                                    amigosRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                for (DataSnapshot amigoSnapshot : dataSnapshot.getChildren()) {
                                                    String amigo = amigoSnapshot.getValue(String.class);
                                                    if (amigo.equals(idAmigo)) {
                                                        amigoSnapshot.getRef().removeValue();
                                                        recreate();
                                                        break;
                                                    }
                                                }
                                            } else {
                                                // El campo amigos no existe para este usuario
                                            }
                                        }


                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    contenedorAmigos.addView(cardView);

                    int totalWidth = contenedorAmigos.getChildCount() * cardView.getLayoutParams().width;

                    contenedorAmigos.getLayoutParams().width = totalWidth;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        }



    @NonNull
    @Override
    public Loader<List<NoticiasPerro>> onCreateLoader(int id, @Nullable Bundle args) {
        Log.d("inicialActivity","exito");
        return new NoticiasLoader(this);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<NoticiasPerro>> loader, List<NoticiasPerro> data) {
        listaNoticias = data;

    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<NoticiasPerro>> loader) {

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
                Log.w("InicialActivity", "Error al descargar la imagen", e);
            }
        });
    }
}