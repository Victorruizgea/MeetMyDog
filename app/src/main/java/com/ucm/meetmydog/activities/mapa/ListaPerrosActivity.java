package com.ucm.meetmydog.activities.mapa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ucm.meetmydog.R;
import com.ucm.meetmydog.activities.perfil.PerfilUsuarioActivity;
import com.ucm.meetmydog.includes.FichaPerroActivity;
import com.ucm.meetmydog.modelos.Mensaje;
import com.ucm.meetmydog.modelos.Perro;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ListaPerrosActivity extends AppCompatActivity {
    SharedPreferences mPreference;
    DatabaseReference mDatabase;
    FirebaseAuth auth;
    String[] parAux, perroAux;
    private LinearLayout contenedorPerros;
    private StorageReference mStorage;
    double usrlat, usrlon;
    private FirebaseAuth mAuth;
    List<String> perros;
    String nombreDueno;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_perros);
        mPreference = getApplicationContext().getSharedPreferences("typeUser", MODE_PRIVATE);
        String parameters = mPreference.getString("parameters", "");
        String nombrePerros = mPreference.getString("perros", "");
        mDatabase = FirebaseDatabase.getInstance("https://meetmydog-6a9f5-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        contenedorPerros=findViewById(R.id.contenedorPerros2);
        mAuth=FirebaseAuth.getInstance();
        String id=mAuth.getUid();
        if (!parameters.isEmpty() && !nombrePerros.isEmpty()) {
            parAux = parameters.split(",");
            perroAux = nombrePerros.split(",");
            perros=new ArrayList<>();
            getPerros(parAux);

            Double pesoMin = Double.valueOf(parAux[2]);
            Double pesoMax = Double.valueOf(parAux[3]);
            mDatabase.child("user").child("paseo").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()) {
                        for (DataSnapshot ds : task.getResult().getChildren()) {
                            String key = ds.getKey();
                            String result = ds.getValue(String.class);
                            String[] resultAux = result.split(" ");
                            if (pesoMin <= Double.valueOf(resultAux[4]) && Double.valueOf(resultAux[4]) <= pesoMax) {
                                if (distanciaPaseo(usrlat, usrlon, Double.valueOf(resultAux[0]), Double.valueOf(resultAux[1]), Integer.valueOf(resultAux[2]), Integer.valueOf(parAux[0])))
                                    perros.add(key);
                            }
                        }
                        for (int i = 0; i < perros.size(); i++) {
                            String[] partes = perros.get(i).split(",");
                            String user = partes[0];
                            String nombrePerro = partes[1];

                            mDatabase.child("user").child(user).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Map<String, Object> usuarioMap = (Map<String, Object>) snapshot.getValue();
                                    nombreDueno = (String) usuarioMap.get("nombre");
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            mDatabase.child("user").child(user).child("perros").child(nombrePerro).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Map<String, Object> datosPerro = (Map<String, Object>) snapshot.getValue();
                                    String nombre = (String) datosPerro.get("nombre");
                                    String raza = (String) datosPerro.get("raza");
                                    String descripcion = (String) datosPerro.get("descripcion");
                                    //int peso= (int) datosPerro.get("peso");
                                    int peso = 5;
                                    int edad = 4;
                                    //int edad= (int) datosPerro.get("edad");
                                    String imagen = (String) datosPerro.get("imagenUri");
                                    Perro perro = new Perro(nombre, imagen, descripcion, edad, peso, raza);
                                    CardView cardView = (CardView) LayoutInflater.from(ListaPerrosActivity.this).inflate(R.layout.cardviewperro3, contenedorPerros, false);
                                    TextView nombrePCard = cardView.findViewById(R.id.nombrePerroSeleccionCard3);
                                    TextView nombreUsuarioCard = cardView.findViewById(R.id.nombreDueñoCard2);
                                    ImageView imagenCard = cardView.findViewById(R.id.imagePerroSeleccionCard3);
                                    Button verPerfil = cardView.findViewById(R.id.ver_ficha2);
                                    Button enviarMensaje = cardView.findViewById(R.id.mensaje);
                                    Switch seguirAmigos = cardView.findViewById(R.id.switch1);
                                    nombrePCard.setText(nombre);
                                    nombreUsuarioCard.setText(nombreDueno);
                                    if (imagen == null) {
                                        Drawable drawable = getResources().getDrawable(R.drawable.defaultperro);
                                        imagenCard.setImageDrawable(drawable);
                                    } else {
                                        descargarImagen("imagenesPerro/" + imagen, imagenCard);
                                    }

                                    seguirAmigos.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                            DatabaseReference amigosRef = mDatabase.child("user").child(id).child("amigos");
                                            if (b) {
                                                amigosRef.push().setValue(user);

                                            } else {
                                                amigosRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        if (dataSnapshot.exists()) {
                                                            for (DataSnapshot amigoSnapshot : dataSnapshot.getChildren()) {
                                                                String amigo = amigoSnapshot.getValue(String.class);
                                                                if (amigo.equals(user)) {
                                                                    amigoSnapshot.getRef().removeValue();
                                                                    break;
                                                                }
                                                            }
                                                        } else {
                                                            // El campo amigos no existe para este usuario
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                        // Manejo de errores
                                                    }
                                                });

                                            }
                                        }
                                    });
                                    verPerfil.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent i = new Intent(ListaPerrosActivity.this, FichaPerroActivity.class);
                                            i.putExtra("datos", perro);
                                            startActivity(i);
                                        }
                                    });
                                    enviarMensaje.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(ListaPerrosActivity.this);
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
                                                                mDatabase.child("user").child(user).child("mensajes").child(idMensaje).setValue(mensaje).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        Toast.makeText(ListaPerrosActivity.this, "Mensaje enviado", Toast.LENGTH_LONG).show();
                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Toast.makeText(ListaPerrosActivity.this, "No se ha podido enviar el mensaje", Toast.LENGTH_LONG).show();
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
                                    contenedorPerros.addView(cardView);
                                    int totalWidth = contenedorPerros.getChildCount() * cardView.getLayoutParams().width;

                                    contenedorPerros.getLayoutParams().width = totalWidth;
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                    }
                }
            });
        }
    }
    private void descargarImagen(String imagenUri, ImageView view) {

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
                Log.w("ListaPerrosActivity", "Error al descargar la imagen", e);
            }
        });
    }

    private void getPerros(String[] parametros) {
        auth = FirebaseAuth.getInstance();
        String idUser = auth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance("https://meetmydog-6a9f5-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //En el caso de no tenerlos los pedimos con la funncion requestPermissions
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            }

        } else {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            double usrlat = location.getLatitude();
            double usrlon = location.getLongitude();
        }
    }
    private boolean distanciaPaseo(Double lat1, Double long1, Double lat2, Double long2, int distanciaExt, int distanciaUsr) {
        int R = 6371;
        lat1 = Math.toRadians(lat1);
        long1 = Math.toRadians(long1);
        lat2 = Math.toRadians(lat2);
        long2 = Math.toRadians(long2);
        double delta_lat = lat2 - lat1;
        double delta_long = long2 - long1;
        double a = Math.sin(delta_lat / 2) * 2 + Math.cos(lat1) * Math.cos(lat2) * Math.sin(delta_long / 2) * 2;
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distancia = R * c;
        return distancia < (distanciaUsr + distanciaExt);
    }
}