package org.masterandroid.wander;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;

import static com.google.firebase.crash.FirebaseCrash.log;

/**
 * Created by rodriii on 16/11/17.
 */


public class DetailPOI extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private TextView titulo, detalle, longitud, latitud, telefono, web, tipo,precio;
    private FloatingActionButton nuevoComentario;
    private RatingBar valoracion;
    private ImageView imagePOI;
    private POI POI;
    private GoogleApiClient mGoogleApiClient;
    private long id_poi;
    private String nombre, direccion, localidad, nombreBusqueda;
    private String[] secnom,secdir;
    private int numero;

    //Anuncios
    private AdView adView;
    private InterstitialAd interstitialAd;
    private String ID_BLOQUE_ANUNCIOS_INTERSTICIAL;
    private String ID_INICIALIZADOR_ADS;

    private RecyclerView recyclerView;
    public AdaptadorComentarios adaptador;
    private RecyclerView.LayoutManager lManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_poi);

        recyclerView = findViewById(R.id.recyclerComentarios);
        recyclerView.setHasFixedSize(true);
        lManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(lManager);
        adaptador = new AdaptadorComentarios(this);
        recyclerView.setAdapter(adaptador);

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
        telefono = findViewById(R.id.telefono);
        web = findViewById(R.id.web);
        nuevoComentario = findViewById(R.id.nuevoComentario);

        tipo = findViewById(R.id.categoria);
        precio = findViewById(R.id.precio);
        valoracion = findViewById(R.id.valoracion);

        //Anuncios:
        ID_BLOQUE_ANUNCIOS_INTERSTICIAL = getString(R.string.ads_intersticial_id_test);
        ID_INICIALIZADOR_ADS = getString(R.string.ads_initialize_test);

        MobileAds.initialize(this, ID_INICIALIZADOR_ADS);

        adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(ID_BLOQUE_ANUNCIOS_INTERSTICIAL);
        interstitialAd.loadAd(new AdRequest.Builder().build());
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                interstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });

        nuevoComentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowDialog();
            }
        });
    }

    ///////MENU///////
    // Infla el menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail_poi, menu);
        return true; //true -> el menu ya esta visible
    }

    //Recibe los OnClicks de los items del menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId(); //Esto almacena el Id del elemento del menu que pulsamos

        //Busca cual es el id del menu que se ha pulsado para lanzar la actividad correspondiente
        if (id == R.id.map_menu) {
            if (interstitialAd.isLoaded()) {
                interstitialAd.show();
            }

            double lat = POI.getLat();
            double lng = POI.getLon();
            showPointOnMap(lat, lng);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void ShowDialog()
    {
        final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
        popDialog.setView(R.layout.alert_dialog);
        // Button OK
        popDialog.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editText = popDialog.create().findViewById(R.id.alertComentario);
                        RatingBar ratingBar = popDialog.create().findViewById(R.id.alertRatingBar);
                        //ConsultaBD
                        dialog.dismiss();
                    }
                }).setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        popDialog.create();
        popDialog.show();
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
                            direccion = myPlace.getAddress().toString();
                            nombre = myPlace.getName().toString();
                            telefono.setText(myPlace.getPhoneNumber());
                            //Pagina web, primero comprobamos que existe
                            Uri uri = myPlace.getWebsiteUri();
                            if (uri!= null){
                            web.setText(""+uri.toString());
                            }

                            tipo.setText(""+myPlace.getPlaceTypes().toString());
                           precio.setText(""+myPlace.getPriceLevel());
                            valoracion.setRating(myPlace.getRating());
                            busca();
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

    //Funcion que busca en wikipedia
    public void busca(){
        //separo el nombre en partes
        secnom = nombre.split(" ");
        //genero la cadena que se pasara al buscador
        nombreBusqueda= secnom[0];

       for (int i = 1; i < secnom.length; i++)
       {
           nombreBusqueda = nombreBusqueda+"+"+secnom[i];
       }

        //separo la direccion por las comas
        secdir = direccion.split(",");
        //compruebo si es una direccion española, donde el segundo argumento seria un numero
        localidad = secdir[1].replace(" ","");

        try {
            if(!localidad.equals("s/n")) {
                numero = Integer.parseInt(localidad);
                localidad = secdir[2];
            }else{
                localidad = secdir[2];
            }
        }
        catch (Exception e){

            localidad = secdir[1];
            Log.e("error:", e.getMessage());
        }

        //separo la localidad obtenida en los espacios en blanco
        secdir = localidad.split(" ");

        //Para eliminar los codigos postales compruebo si el resultado es un numero entero
        //en caso contrario lo añado a la busqueda
        for (int i = 0; i < secdir.length ; i++){
            try {
                if(!secdir[i].replace(" ","").equals("")) {
                    numero = Integer.parseInt(secdir[i].replace(" ", "")); //elimino los espacios en blanco
                }

            }
            catch (Exception e){

                nombreBusqueda = nombreBusqueda +"+"+secdir[i];
                Log.e("error:", e.getMessage());
            }
        }

        //Toast.makeText(this,"la busqueda es: "+ nombreBusqueda, Toast.LENGTH_LONG).show();
        //Toast.makeText(this,"la localidad es: "+ localidad, Toast.LENGTH_LONG).show();

        new Link().execute();

    }

    // Link AsyncTask
    private class Link extends AsyncTask<Void, Void, Void> {
       boolean encontrado;
        String url2, informacion;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                // Connect to the google
                URL url = new URL("https://www.google.es/search?q="+nombreBusqueda);
                Document document = Jsoup.parse(url,4000);
                Elements links = document.select("cite._Rm");


                boolean wiki = false;
                for (int i =0; i<5 && !wiki; i++) {
                    Element link = links.get(i);
                    url2 = link.text();

                    if (url2.contains("wikipedia.org")) {
                        wiki = true;

                    }
                }

                if (wiki){

                    // Connect to the wikipedia
                    URL url3 = new URL(url2);
                    Document document2 = Jsoup.parse(url3, 10000);
                    Elements info = document2.select("#mw-content-text > div > p:first-of-type");
                    // Elements links2 = info.select("p");


                    //Element link2 = info.first();
                    informacion = info.text();

                    encontrado = true;
                }else{encontrado=false;}



            } catch (SocketTimeoutException e) {
                //this.errorMessage.put("Grouphug request timed out. Waiting before retrying");
                encontrado = false;
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                encontrado = false;
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            TextView title = findViewById(R.id.informacion);
            TextView wikipedia = findViewById(R.id.wiki);
            if (encontrado){
                title.setText(informacion);
                wikipedia.setText(url2);
            }else{
                title.setText("No encontrado");
                wikipedia.setText("No disponoble");
            }

        }
    }

    private void showPointOnMap(Double LatPoint, Double LngPoint) {
        Intent i = new Intent(DetailPOI.this, MapActivity.class);
        i.putExtra("LatPoint", LatPoint);
        i.putExtra("LngPoint", LngPoint);
        startActivity(i);
    }
}
