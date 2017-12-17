package org.masterandroid.wander;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MapaRuta extends FragmentActivity implements OnMapReadyCallback {

    ///ATRIBUTOS////

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

    private Ruta ruta;

    private static final int overview = 0;

    ////CONSTRUCTOR////
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_ruta);



        //obtenemos id de la ruta
        Bundle extras = getIntent().getExtras();
        id = extras.getLong("id", -1);

        //Inicializa la BD
        ConsultaBD.inicializaBD(this);

        //compruebo si la ruta esta vacia
        c = ConsultaBD.listadoPOIItinerario((int)id);

        //si la ruta esta vaciano hago nada
        //en caso contrario centro el mapa en el ultimo poi
        if(c.getCount()==0){


        }else{
            c.moveToFirst();
            latPoint = (0+c.getFloat(c.getColumnIndex("lat")));
            lngPoint = (0+c.getFloat(c.getColumnIndex("lon")));
        }



        //inicializo el mapa
        SupportMapFragment mapFragment =(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }



    @Override
    public void onMapReady(GoogleMap googleMap) {

        setupGoogleMapScreenSettings(googleMap);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latPoint, lngPoint), 15));
        colocarMarcadores(googleMap);
        buscoRuta(googleMap);


    }

    public void buscoRuta(GoogleMap googleMap){
        //obtengo los identificadores de la ruta

        ruta = ConsultaBD.getRoute((int)id);

        DirectionsResult results = getDirectionsDetails(ruta.getOrigen(),ruta.getDestino(),TravelMode.WALKING,ruta.getWaypoints());
        if (results != null) {
            addPolyline(results, googleMap);
            positionCamera(results.routes[overview], googleMap);
           // addMarkersToMap(results, googleMap);
            Log.e("funciona","");
        }else{
            Log.e("sin resultado","");
        }
    }

    private void setupGoogleMapScreenSettings(GoogleMap mMap) {
        mMap.setBuildingsEnabled(true);
        mMap.setIndoorEnabled(true);
        UiSettings mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(true);
        mUiSettings.setRotateGesturesEnabled(true);
        SharedPreferences pref = PreferenceManager.
                getDefaultSharedPreferences(this);
        switch (Integer.parseInt(pref.getString(getString(R.string.mapa),"0"))){
            case 1:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                //Toast.makeText(this,"Map type: Satellite", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                //Toast.makeText(this,"Map type: Hybrid", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                //Toast.makeText(this,"Map type: Terrain", Toast.LENGTH_SHORT).show();
                break;
            default:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                //Toast.makeText(this,"Map type: Normal", Toast.LENGTH_SHORT).show();
        }
    }

    private void addMarkersToMap(DirectionsResult results, GoogleMap mMap) {
        mMap.addMarker(new MarkerOptions().position(new LatLng(results.routes[overview].legs[overview].startLocation.lat,results.routes[overview].legs[overview].startLocation.lng)).title(results.routes[overview].legs[overview].startAddress));
        mMap.addMarker(new MarkerOptions().position(new LatLng(results.routes[overview].legs[overview].endLocation.lat,results.routes[overview].legs[overview].endLocation.lng)).title(results.routes[overview].legs[overview].startAddress).snippet(getEndLocationTitle(results)));
    }

    private void positionCamera(DirectionsRoute route, GoogleMap mMap) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(route.legs[overview].startLocation.lat, route.legs[overview].startLocation.lng), 15));
    }

    private void addPolyline(DirectionsResult results, GoogleMap mMap) {
        List<LatLng> decodedPath = PolyUtil.decode(results.routes[overview].overviewPolyline.getEncodedPath());
        mMap.addPolyline(new PolylineOptions().addAll(decodedPath));
    }

    private String getEndLocationTitle(DirectionsResult results){
        return  "Tiempo :"+ results.routes[overview].legs[overview].duration.humanReadable + " Distancia :" + results.routes[overview].legs[overview].distance.humanReadable;
    }

    private GeoApiContext getGeoContext() {
        GeoApiContext geoApiContext = new GeoApiContext();
        Log.e("obteniendo geocontext","");
        return geoApiContext
                .setQueryRateLimit(3)
                .setApiKey("AIzaSyBHQI1r6_NA8xCKglF9VSUTBrVk4CFNWoI")
                .setConnectTimeout(10, TimeUnit.SECONDS)
                .setReadTimeout(10, TimeUnit.SECONDS)
                .setWriteTimeout(10, TimeUnit.SECONDS);
    }
    private DirectionsResult getDirectionsDetails(String origin, String destination, TravelMode mode, String waypoints) {

        DateTime now = new DateTime();
        try {
            Log.e("enviando peticion","");
            return DirectionsApi.newRequest(getGeoContext())
                    .mode(mode)
                    .origin(origin)
                    .waypoints(waypoints)
                    .destination(destination)
                    .departureTime(now)
                    .await();

        } catch (ApiException e) {
            e.printStackTrace();
            Log.e("error",""+e);
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.e("error",""+e);
            return null;
        } catch (IOException e) {
            Log.e("error",""+e);
            e.printStackTrace();
            return null;
        }
    }

    /**Coloca sobre el mapa los marcadores de los puntos*/
    public void colocarMarcadores(GoogleMap googleMap){

        c = ConsultaBD.listadoPOIItinerario((int)id);



        for (int n=0; n<c.getCount(); n++) {
            c.moveToPosition(n);
            Double lat = c.getDouble(c.getColumnIndex("lat"));
            Double lng = c.getDouble(c.getColumnIndex("lon"));
            String name = c.getString(c.getColumnIndex("title"));

            googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng))
                    .title(name));
        }

    }
}
