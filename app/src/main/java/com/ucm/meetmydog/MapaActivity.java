package com.ucm.meetmydog;

import androidx.annotation.NonNull;
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

import java.util.ArrayList;

public class MapaActivity extends AppCompatActivity implements OnMapReadyCallback {
    GoogleMap mMap;
    SharedPreferences mPreference;
    String[] parAux;
    private Marker marcador;
    double usrlat=0.0;
    double usrlon=0.0;
    String mensaje;
    DatabaseReference mDatabase;
    FirebaseAuth auth;

    double peso = -1;
    String personalidad = "";
    Paseo paseo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);
        mPreference = getApplicationContext().getSharedPreferences("typeUser", MODE_PRIVATE);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.FragmentoMapaExtra);
        mapFragment.getMapAsync(this);
    }

    //Muestra las localizaciones con su fecha asignada en nuestro mapa (tantas como se le hayan pasado por el bundle)
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        String parameters = mPreference.getString("parameters", "");
        LatLng madrid;
        if(!parameters.isEmpty()){
            parAux = parameters.split(",");
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
        mDatabase = FirebaseDatabase.getInstance("https://tfg01-aa25e-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        if (peso == -1 && personalidad.isEmpty()) {
            mDatabase.child("Users").child("padre").child(idUser).child("hijos").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()) {
                        for (DataSnapshot ds : task.getResult().getChildren()) {
                            if (ds.getKey().equals("peso"))
                                peso = ds.getValue(Integer.class);
                            if (ds.getKey().equals("personalidad"))
                                personalidad = ds.getValue(String.class);
                        }
                        paseo = new Paseo(lat,lon,parAux[0], parAux[1], peso, personalidad);
                        mDatabase.child("Users").child("hijo").child(idUser).child("location").child ("0").child("com").setValue(paseo);
                    }
                }
            });
        }
        else{
            //Actualizar tiempo
            paseo = new Paseo(lat,lon,parAux[0], parAux[1], peso, personalidad);
            mDatabase.child("Users").child("hijo").child(idUser).child("location").child ("0").child("com").setValue(paseo);
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
            return;
        } else {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            ActualizarUbicacion(location);
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 1200, 0, locationListener);
        }
    }
    public void Mensaje(){
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG);
    }

    private ArrayList<String> getPerros(String[] parametros){
        auth = FirebaseAuth.getInstance();
        String idUser = auth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance("https://tfg01-aa25e-default-rtdb.europe-west1.firebasedatabase.app").getReference();

        ArrayList<String> perros = new ArrayList<>();
        Integer pesoMin = Integer.valueOf(parametros[2]);
        Integer pesoMax = Integer.valueOf(parametros[3]);
        String Mipersonalidad = parametros[4];

        final int[] peso = new int[1];
        final Double[] lat = new Double[1];
        final Double[] lon = new Double[1];
        final String[] personalidad = new String[1];

        mDatabase.child("Users").child("padre").child(idUser).child("hijos").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    for (DataSnapshot ds : task.getResult().getChildren()) {
                        String key = ds.getKey();
                        mDatabase.child("Users").child("padre").child(idUser).child("hijos").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (DataSnapshot ds : task.getResult().getChildren()) {
                                        if (ds.getKey().equals("peso"))
                                            peso[0] = ds.getValue(Integer.class);
                                        if (ds.getKey().equals("personalidad"))
                                            personalidad[0] = ds.getValue(String.class);
                                        if (ds.getKey().equals("lat"))
                                            lat[0] = ds.getValue(Double.class);
                                        if (ds.getKey().equals("lon"))
                                            lon[0] = ds.getValue(Double.class);
                                    }
                                    if (pesoMin <= peso[0] && peso[0] <= pesoMax && personalidad[0].equals(Mipersonalidad)) {
                                        perros.add(lat[0] + "");
                                        perros.add(lon[0] + "");
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
}