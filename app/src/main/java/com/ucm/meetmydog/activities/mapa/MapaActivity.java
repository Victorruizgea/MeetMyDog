package com.ucm.meetmydog.activities.mapa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ucm.meetmydog.activities.home.InicialActivity;
import com.ucm.meetmydog.activities.perfil.PerfilUsuarioActivity;
import com.ucm.meetmydog.modelos.Paseo;
import com.ucm.meetmydog.R;

import java.util.ArrayList;

import nl.joery.animatedbottombar.AnimatedBottomBar;

public class MapaActivity extends AppCompatActivity implements OnMapReadyCallback {
    GoogleMap mMap;
    SharedPreferences mPreference;
    String[] parAux, perroAux;
    private Marker marcador;
    double usrlat=0.0;
    double usrlon=0.0;
    String mensaje;
    DatabaseReference mDatabase;
    FirebaseAuth auth;

    AnimatedBottomBar bottomBar;
    double peso = -1;
    Paseo paseo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);
        mPreference = getApplicationContext().getSharedPreferences("typeUser", MODE_PRIVATE);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.FragmentoMapaExtra);
        mapFragment.getMapAsync(this);
        bottomBar = findViewById(R.id.bottom_bar);
        bottomBar.setOnTabSelectListener(new AnimatedBottomBar.OnTabSelectListener() {
            @Override
            public void onTabSelected(int i, @Nullable AnimatedBottomBar.Tab tab, int i1, @NonNull AnimatedBottomBar.Tab tab1) {
                switch (i1) {
                    case 0:
                        Intent intent1 = new Intent(MapaActivity.this, InicialActivity.class);
                        startActivity(intent1);
                        break;
                    case 1:
                        Intent intent2 = new Intent(MapaActivity.this, SeleccionPerrosActivity.class);
                        startActivity(intent2);
                        break;
                    case 2:
                        Intent intent3 = new Intent(MapaActivity.this, PerfilUsuarioActivity.class);
                        startActivity(intent3);
                        break;
                }
            }

            @Override
            public void onTabReselected(int i, @NonNull AnimatedBottomBar.Tab tab) {}
        });
    }

    //Muestra las localizaciones con su fecha asignada en nuestro mapa (tantas como se le hayan pasado por el bundle)
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        String parameters = mPreference.getString("parameters", "");
        String nombrePerros = mPreference.getString("perros", "");
        LatLng madrid;
        if(!parameters.isEmpty() && !nombrePerros.isEmpty()){
            parAux = parameters.split(",");
            perroAux = nombrePerros.split(",");
            miUbicacion();
        }
    }
    private void locationSart(){
        LocationManager mlocManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!gpsEnabled){
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
    }

    private void AgregarMArcador(double lat, double lon){
        LatLng coordenadas=new LatLng(lat, lon);
        CameraUpdate MiUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 16);
        if(marcador!=null)marcador.remove();
        marcador = mMap.addMarker(new MarkerOptions().position(coordenadas).title("Esta es tu ubicación"));
        mMap.animateCamera(MiUbicacion);
    }

    private void ActualizarUbicacion(Location location){
        if(location!=null){
            usrlat=location.getLatitude();
            usrlon=location.getLongitude();
            ActualizarPaseo(usrlat,usrlon);
            AgregarMArcador(usrlat,usrlon);
            ubicacionPerros();
        }
    }

    private void ubicacionPerros() {
        ArrayList<String> posAux = getPerros(parAux);
        int Counter = posAux.size()/2;

        for (int x = 0; x < Counter; x++){
            Double lat = Double.valueOf(posAux.get(x*2));
            Double lon = Double.valueOf(posAux.get(1 + x*2));
            LatLng childLocation = new LatLng(lat, lon);

            mMap.addMarker(new MarkerOptions().position(childLocation)).setTitle("Perrito " + x );
        }
    }

    private void ActualizarPaseo(double lat, double lon) {
        auth = FirebaseAuth.getInstance();
        String idUser = auth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance("https://meetmydog-6a9f5-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        if (peso == -1) {
            for(int x = 0; x < perroAux.length; x++){
                int finalX = x;
                mDatabase.child("user").child(idUser).child("perros").child(perroAux[x]).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DataSnapshot ds : task.getResult().getChildren()) {
                                if (ds.getKey().equals("peso"))
                                    peso = ds.getValue(Integer.class);
                            }
                            String result = Paseo.BadString(lat,lon,parAux[0], parAux[1], peso);
                            mDatabase.child("user").child("paseo").child(idUser + "," +perroAux[finalX]).setValue(result);
                        }
                    }
                });
            }
        }
        else{
            //Actualizar tiempo
            for(int x = 0; x < perroAux.length; x++) {
                String result = Paseo.BadString(lat, lon, parAux[0], parAux[1], peso);
                mDatabase.child("user").child("paseo").child(idUser + "," + perroAux[x]).setValue(result);
            }
        }
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            ActualizarUbicacion(location);
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras){}
        @Override
        public void onProviderEnabled( String s){
            mensaje = "GPS ACTIVADO";
            Mensaje();
        }
        @Override
        public void onProviderDisabled( String s){
            mensaje = "GPS DESACTIVADO";
            locationSart();
            Mensaje();
        }
    };
    private static int PETICION_PERMISOS_LOCALIZACION = 101;

    private void miUbicacion() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //En el caso de no tenerlos los pedimos con la funncion requestPermissions
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PETICION_PERMISOS_LOCALIZACION);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                return;
        } else {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            ActualizarUbicacion(location);
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 60000, 20, locationListener);
        }
    }
    public void Mensaje(){
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG);
    }

    private ArrayList<String> getPerros(String[] parametros){
        auth = FirebaseAuth.getInstance();
        String idUser = auth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance("https://meetmydog-6a9f5-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        ArrayList<String> perros = new ArrayList<>();
        Double pesoMin = Double.valueOf(parametros[2]);
        Double pesoMax = Double.valueOf(parametros[3]);


        mDatabase.child("user").child("paseo").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    for (DataSnapshot ds : task.getResult().getChildren()) {
                        String key = ds.getKey();
                        mDatabase.child("user").child("paseo").child(key).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (task.isSuccessful()) {
                                    String result = task.getResult().getValue(String.class);
                                    String [] resultAux = result.split(" ");
                                    if (pesoMin <= Double.valueOf(resultAux[4]) && Double.valueOf(resultAux[4]) <= pesoMax && distanciaPaseo(usrlat, usrlon, Double.valueOf(resultAux[0]), Double.valueOf(resultAux[1]), Integer.valueOf(resultAux[2])
                                            , Integer.valueOf(parAux[0]))) {
                                        perros.add(resultAux[0]);
                                        perros.add(resultAux[1]);
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
        return perros;
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