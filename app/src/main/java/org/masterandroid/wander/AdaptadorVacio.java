package org.masterandroid.wander;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;

/**
 * Created by Ivan on 18/12/2017.
 */

public class AdaptadorVacio extends RecyclerView.Adapter<AdaptadorVacio.ViewHolder> {

    private LayoutInflater inflador;//Crea Layouts a partir del XML
    private long id;
    private int longitud;
    private GoogleApiClient mGoogleApiClient;
    private String place_Id;


    private OnItemClickListener escucha;
    private View.OnLongClickListener onLongClickListener; // ecuchador long
    private Context contexto;


    //lo utilizaremos desde la actividad
    interface OnItemClickListener {
        public void onClick(ViewHolder holder);//falta añadir que devuelva la imagen
    }



    // Constructor
    public AdaptadorVacio(Context contexto, int longitud, OnItemClickListener escucha,GoogleApiClient mGoogleApiClient,String placeId) {

        this.longitud=longitud;
        this.escucha=escucha;
        this.mGoogleApiClient=mGoogleApiClient;
        this.contexto=contexto;
        place_Id=placeId;
    }




    //ViewHolder con los elementos a modificar
    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        //TextView titulo, checked, etc;;
        ImageView foto;

        public ViewHolder(View vista) {
            super(vista);
            //referencio los elemtos a modificar
            foto = vista.findViewById(R.id.image_item);

            vista.setOnClickListener(this);
            // vista.setOnLongClickListener(this);
        }
        @Override
        public void onClick(View view) {
            escucha.onClick(this);//falta añadir que devuelva la imagen
        }


    }

    //Creamos el viewHolder con las vistas de los elementos sin personalizar
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //inflamos la vista
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false); //Cambiar prueba por el nombre del layout del elemento del reciclerview

       // vista.setOnLongClickListener(onLongClickListener);//aplicamos escuchador long a cada vista
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //siguiente elemento del cursor
       // c.moveToPosition(position);
        //Log.e("position",""+position);


        //Modifico los elementos de la vista
        // EJEMPLO  holder.Titulo.setText(c.getString(c.getColumnIndex("title")));
        //Log.e("width",""+holder.foto.getWidth());
       // Log.e("Height",""+holder.foto.getHeight());

        ponerFoto(place_Id, position, holder.foto);


    }



    @Override
    public int getItemCount() {

        return longitud;
    }



    //lo usariamos desde la actividad
    public void setOnItemLongClickListener(View.OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;

    }

   //debemos crear una funcion que nos devuelva la imagen

   /* public Bitmap obtenerBitmap(){
        Bitmap bitmap;

        return bitmap;
    }*/

    //ponemos la imagen

    public void ponerFoto(String placeId, int position, final ImageView imagePOI){

        // Create a new AsyncTask that displays the bitmap and attribution once loaded.
        new PhotoTask(imagePOI.getWidth(), imagePOI.getHeight(),position) {
            @Override
            protected void onPreExecute() {
                // Display a temporary image to show while bitmap is loading.
                imagePOI.setImageResource(R.drawable.transparente);
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

        private int mPosition;

        public PhotoTask(int width, int height,int position) {
            mHeight = 800;
            mWidth = 800;
            mPosition = position;
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
                    PlacePhotoMetadata photo = photoMetadataBuffer.get(mPosition);
                    CharSequence attribution = photo.getAttributions();
                    // Load a scaled bitmap for this photo.
                    Bitmap image = photo.getPhoto(mGoogleApiClient).await()
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