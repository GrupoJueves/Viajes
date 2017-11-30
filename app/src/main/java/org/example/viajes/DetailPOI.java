package org.example.viajes;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;

/**
 * Created by rodriii on 16/11/17.
 */


public class DetailPOI extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private TextView titulo, detalle, longitud, latitud;
    private ImageView imagePOI;
    private POI POI;
    private GoogleApiClient mGoogleApiClient;
    private long id_poi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_poi);

        //recojo el valor del identificador de la ruta
        Bundle extras = getIntent().getExtras();
        id_poi = extras.getLong("id", -1);

        //inicializo la base de datos, si no existe la crea
        ConsultaBD.inicializaBD(this);

        //Creo cliente api places
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, this)
                .build();


        titulo = findViewById(R.id.tituloPOI);
        detalle = findViewById(R.id.detalle);
        longitud = findViewById(R.id.longitud);
        latitud = findViewById(R.id.latitud);
        imagePOI = findViewById(R.id.imagePOI);

        rellenarPOI();
    }

    public void rellenarPOI(){

        int id = ConsultaBD.getPoiId((int)id_poi);
        POI = ConsultaBD.infoPoi((int) id);

        Places.GeoDataApi.getPlaceById(mGoogleApiClient, POI.getIdentificador())
                .setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (places.getStatus().isSuccess() && places.getCount() > 0) {
                            final Place myPlace = places.get(0);
                            //obtengo la toda la info disponible del api places en la variable myPlace
                            ponerFoto(myPlace.getId());
                            detalle.setText(myPlace.getAddress());
                        } else {
                            Log.e("", "Place not found");
                        }
                        places.release();
                    }
                });


        titulo.setText(POI.getTitle());
        //detalle.setText(POI.getDescription());
        longitud.setText(String.valueOf(POI.getLon()));
        latitud.setText(String.valueOf(POI.getLat()));
        //imagePOI
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void ponerFoto(String placeId){
        // Create a new AsyncTask that displays the bitmap and attribution once loaded.
        new PhotoTask(imagePOI.getWidth(), imagePOI.getHeight()) {
            @Override
            protected void onPreExecute() {
                // Display a temporary image to show while bitmap is loading.
                imagePOI.setImageResource(R.drawable.ic_photo_black_48dp);
            }

            @Override
            protected void onPostExecute(AttributedPhoto attributedPhoto) {
                if (attributedPhoto != null) {
                    // Photo has been loaded, display it.
                    imagePOI.setImageBitmap(attributedPhoto.bitmap);

                   /* // Display the attribution as HTML content if set.
                    if (attributedPhoto.attribution == null) {
                        mText.setVisibility(View.GONE);
                    } else {
                        mText.setVisibility(View.VISIBLE);
                        mText.setText(Html.fromHtml(attributedPhoto.attribution.toString()));
                    }*/

                }
            }
        }.execute(placeId);


    }



    abstract class PhotoTask extends AsyncTask<String, Void, PhotoTask.AttributedPhoto> {

        private int mHeight;

        private int mWidth;

        public PhotoTask(int width, int height) {
            mHeight = height;
            mWidth = width;
        }

        /**
         * Loads the first photo for a place id from the Geo Data API.
         * The place id must be the first (and only) parameter.
         */
        @Override
        protected AttributedPhoto doInBackground(String... params) {
            if (params.length != 1) {
                return null;
            }
            final String placeId = params[0];
            AttributedPhoto attributedPhoto = null;

            PlacePhotoMetadataResult result = Places.GeoDataApi
                    .getPlacePhotos(mGoogleApiClient, placeId).await();

            if (result.getStatus().isSuccess()) {
                PlacePhotoMetadataBuffer photoMetadataBuffer = result.getPhotoMetadata();
                if (photoMetadataBuffer.getCount() > 0 && !isCancelled()) {
                    // Get the first bitmap and its attributions.
                    PlacePhotoMetadata photo = photoMetadataBuffer.get(0);
                    CharSequence attribution = photo.getAttributions();
                    // Load a scaled bitmap for this photo.
                    Bitmap image = photo.getScaledPhoto(mGoogleApiClient, mWidth, mHeight).await()
                            .getBitmap();

                    attributedPhoto = new AttributedPhoto(attribution, image);
                }
                // Release the PlacePhotoMetadataBuffer.
                photoMetadataBuffer.release();
            }
            return attributedPhoto;
        }

        /**
         * Holder for an image and its attribution.
         */
        class AttributedPhoto {

            public final CharSequence attribution;

            public final Bitmap bitmap;

            public AttributedPhoto(CharSequence attribution, Bitmap bitmap) {
                this.attribution = attribution;
                this.bitmap = bitmap;
            }
        }
    }

}
