package org.example.viajes;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;

/**
 * Created by rodriii on 16/11/17.
 */


public class DetailPOI extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private TextView titulo, detalle, longitud, latitud;
    private ImageView imagePOI;
    private POI POI;
    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_poi);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, this)
                .build();


        titulo = findViewById(R.id.titulo);
        detalle = findViewById(R.id.detalle);
        longitud = findViewById(R.id.longitud);
        latitud = findViewById(R.id.latitud);
        imagePOI = findViewById(R.id.imagePOI);

        rellenarPOI();
    }

    public void rellenarPOI(){
        POI = ConsultaBD.infoPoi(2);
        Places.GeoDataApi.getPlaceById(mGoogleApiClient, POI.getIdentificador())
                .setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (places.getStatus().isSuccess() && places.getCount() > 0) {
                            final Place myPlace = places.get(0);
                            //obtengo la toda la info disponible del api places en la variable myPlace
                        } else {
                            Log.e("", "Place not found");
                        }
                        places.release();
                    }
                });


        titulo.setText(POI.getTitle());
        detalle.setText(POI.getDescription());
        longitud.setText(String.valueOf(POI.getLon()));
        latitud.setText(String.valueOf(POI.getLat()));
        //imagePOI
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
