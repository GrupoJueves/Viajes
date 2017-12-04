package org.masterandroid.wander;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
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
 * Created by Ivan on 30/11/2017.
 */

public class SelectPOIGrafic extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnPoiClickListener, PlaceSelectionListener {

    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;


    ////ATRIBUTOS////

    private GoogleMap map;

    //posicion inicial
    private double latPoint = 0;
    private double lngPoint = 0;

    //Extras
    private int seeOnMap = 0;

    private double latAux;
    private double lngAux;

    private Cursor c;

    private long id;



    ////CONSTRUCTOR////
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //obtenemos id de la ruta
        Bundle extras = getIntent().getExtras();
        id = extras.getLong("id", -1);

        //Referencia el fragment del mapa
        SupportMapFragment mapFragment =(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Inicializa la BD
        ConsultaBD.inicializaBD(this);
        //compruebo si la ruta esta vacia
        c = ConsultaBD.listadoPOIItinerario((int)id);

        //si la ruta esta vacia muestro un intent para lugar inicial
        //en caso contrario centro el mapa en el ultimo poi
        if(c.getCount()==0){
            //lanzo un intent de autocomplet
            try {
                Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                .build(this);
                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
            } catch (GooglePlayServicesRepairableException e) {
                // TODO: Handle the error.
            } catch (GooglePlayServicesNotAvailableException e) {
                // TODO: Handle the error.
            }

        }else{
            c.moveToLast();
            latPoint = (0+c.getFloat(c.getColumnIndex("lat")));
            lngPoint = (0+c.getFloat(c.getColumnIndex("lon")));
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
                map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
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

        /*
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(true);
        }*/

        //Centro el mapa en el ultimo poi
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latPoint, lngPoint), 15));


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

        c = ConsultaBD.listadoPOIItinerario((int)id);



            for (int n=0; n<c.getCount(); n++) {
                c.moveToPosition(n);
                Double lat = c.getDouble(c.getColumnIndex("lat"));
                Double lng = c.getDouble(c.getColumnIndex("lon"));
                String name = c.getString(c.getColumnIndex("title"));

                map.addMarker(new MarkerOptions().position(new LatLng(lat, lng))
                        .title(name));
            }

    }





    @Override
    public void onPoiClick(final PointOfInterest poi) {
        String dialogo = getString(R.string.dialog1)+" "+poi.name+" "+getString(R.string.dialog2);
        String confirmar = getString(R.string.confirmar);
        String cancelar = getString(R.string.cancelar);

        new AlertDialog.Builder(SelectPOIGrafic.this)
                .setTitle(R.string.titulo_dialog)
                .setMessage(dialogo)
                .setPositiveButton(confirmar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        anadirPOI(poi);

                    }
                })
                .setNegativeButton(cancelar, null)
                .show();


    }

    public void anadirPOI(PointOfInterest poi){
        POI punto = new POI(poi);

        ConsultaBD.newPOI(punto);
        int poi_id = ConsultaBD.getIdPOI(poi.placeId);
        if (poi_id != -1){
            if(ConsultaBD.addPoi((int)id,poi_id,false)){
                map.addMarker(new MarkerOptions().position(poi.latLng)
                        .title(poi.name));
            }
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15));
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i("", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }


    public void añadirPOI (){}
}