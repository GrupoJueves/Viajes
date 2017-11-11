package org.example.viajes;

/**
 * Created by 2k45y9w789ys on 11/11/2017.
 */

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Utilizada para mostrar el mapa de google y el punto que se envia a traves de la actividad
 * desde varias actividades y se manda informacion para saber donde se debe centrar la vista, tambien
 * se puede modificar el tipo de mapa en las preferencias(sin implementar), tiene un escuchador de long click en la pantalla.
 * Created by 2k45y9w789ys on 01/12/2016.
 */

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    ////ATRIBUTOS////

    private GoogleMap map;

    //posicion inicial
    private double latPoint;
    private double lngPoint;

    //Extras
    private int seeOnMap = 0;

    private double latAux;
    private double lngAux;



    ////CONSTRUCTOR////
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        SupportMapFragment mapFragment =(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        try {
            Bundle extras = getIntent().getExtras();
            latPoint = extras.getDouble("LatPoint", -1);
            lngPoint = extras.getDouble("LngPoint", -1);
            seeOnMap = 1;
        }catch (NullPointerException e){
            seeOnMap = 0;
        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setCompassEnabled(true);
        map.setOnMapLongClickListener(this);

        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        /*
        //Decidir el tipo de mapa, editable en preferencias
        if (pref.getString("tipo_mapa", "0").equals("0")) {
            mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }else{
            mapa.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        }
        */

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(true);
        }

        if(seeOnMap != 0){
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latPoint, lngPoint), 15));
        }

        colocarMarcadores();
    }


    ////METODOS////

    /**Escuchador de longClik*/
    @Override
    public void onMapLongClick(LatLng latLng) {
        Toast.makeText(this, "Coordenadas: "+latLng, Toast.LENGTH_SHORT).show();
    }

    /**Coloca sobre el mapa los marcadores de los puntos*/
    public void colocarMarcadores(){

        if(seeOnMap != 0){
            map.addMarker(new MarkerOptions().position(new LatLng(latPoint, lngPoint))
                    .title("Punto"));
        }
    }
}