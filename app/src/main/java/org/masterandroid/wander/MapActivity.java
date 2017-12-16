package org.masterandroid.wander;

/**
 * Created by 2k45y9w789ys on 11/11/2017.
 */

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;


import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;

/**
 * Utilizada para mostrar el mapa de google y el punto que se envia a traves de la actividad
 * desde varias actividades y se manda informacion para saber donde se debe centrar la vista, tambien
 * se puede modificar el tipo de mapa en las preferencias(sin implementar), tiene un escuchador de long click en la pantalla.
 * Created by 2k45y9w789ys on 01/12/2016.
 */

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnPoiClickListener, PlaceSelectionListener {

    ////ATRIBUTOS////

    private GoogleMap map;

    //posicion inicial
    private double latPoint;
    private double lngPoint;

    //Extras
    private int seeOnMap = 0;

    private double latAux;
    private double lngAux;

    private Cursor c;



    ////CONSTRUCTOR////
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        SupportMapFragment mapFragment =(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ConsultaBD.inicializaBD(this);
        c = ConsultaBD.listadoPOI();

        try {
            Bundle extras = getIntent().getExtras();
            latPoint = extras.getDouble("LatPoint", -1);
            lngPoint = extras.getDouble("LngPoint", -1);
            seeOnMap = 1;
        }catch (NullPointerException e){
            seeOnMap = 0;
        }

        // Retrieve the PlaceAutocompleteFragment.
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Register a listener to receive callbacks when a place has been selected or an error has
        // occurred.
        autocompleteFragment.setOnPlaceSelectedListener(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setCompassEnabled(true);
        map.setOnMapLongClickListener(this);
        map.setOnPoiClickListener(this);

        SharedPreferences pref = PreferenceManager.
                getDefaultSharedPreferences(this);
        switch (Integer.parseInt(pref.getString(getString(R.string.mapa),"0"))){
            case 1:
                map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                //Toast.makeText(this,"Map type: Satellite", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                //Toast.makeText(this,"Map type: Hybrid", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                //Toast.makeText(this,"Map type: Terrain", Toast.LENGTH_SHORT).show();
                break;
            default:
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                //Toast.makeText(this,"Map type: Normal", Toast.LENGTH_SHORT).show();
        }

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
        Toast.makeText(this, getString(R.string.coordenadas)+":"+latLng, Toast.LENGTH_SHORT).show();
    }

    /**Coloca sobre el mapa los marcadores de los puntos*/
    public void colocarMarcadores(){

        if(seeOnMap != 0){

            for (int n=0; n<c.getCount(); n++) {
                c.moveToPosition(n);
                Double lat = c.getDouble(c.getColumnIndex("lat"));
                Double lng = c.getDouble(c.getColumnIndex("lon"));
                String name = c.getString(c.getColumnIndex("title"));

                map.addMarker(new MarkerOptions().position(new LatLng(lat, lng))
                        .title(name));
            }
        }
    }





    @Override
    public void onPoiClick(PointOfInterest poi) {
        Toast.makeText(getApplicationContext(), getString(R.string.seleccionado) + ":" +
                        poi.name + "\n" + getString(R.string.id_lugar) + ":" + poi.placeId +
                        "\n" + R.string.latitud + poi.latLng.latitude +
                        R.string.longitud + poi.latLng.longitude,
                Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onPlaceSelected(Place place) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15));


    }

    @Override
    public void onError(Status status) {
        // TODO: Handle the error.
        Toast.makeText(getApplicationContext(), "error",
                Toast.LENGTH_SHORT).show();


    }
}