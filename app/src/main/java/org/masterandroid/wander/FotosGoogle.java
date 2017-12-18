package org.masterandroid.wander;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;

public class FotosGoogle extends AppCompatActivity implements AdaptadorVacio.OnItemClickListener,GoogleApiClient.OnConnectionFailedListener{

    RecyclerView recyclerview;
    RecyclerView.LayoutManager manager;
    AdaptadorVacio adaptador;
    private GoogleApiClient mGoogleApiClient;

    int fotos;
    String idPlace;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contenedor_fotos_google);

        Bundle extras = getIntent().getExtras();
        fotos = extras.getInt("fotos",0);
        idPlace = extras.getString("idPlace","");

        if(idPlace.equals("")|| fotos==0){
            finish();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Creo cliente api places
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, this)
                .build();


        rellena();
    }

    public void rellena(){

        recyclerview = (RecyclerView) findViewById(R.id.reciclador);
        recyclerview.setHasFixedSize(true);

        // Usar un administrador para LinearLayout
        manager = new GridLayoutManager(this,2,LinearLayoutManager.VERTICAL, false);
        recyclerview.setLayoutManager(manager);

        //creamos el adaptador
        adaptador = new AdaptadorVacio(this, fotos, this, mGoogleApiClient,idPlace);
        //Esto seria para el caso de que no existireran rutas para este usuario
        // emptyview seria lo que se mostraria en una lista vacia
       /* if (adaptador.getItemCount() == 0) {
            vacio.setVisibility(View.VISIBLE);
            recyclerViewClientes.setVisibility(View.GONE);
        } else {
            vacio.setVisibility(View.GONE);
            recyclerViewClientes.setVisibility(View.VISIBLE);
        }*/




        //rellenamos el reciclerview
        recyclerview.setAdapter(adaptador);
    }

    @Override
    public void onClick(AdaptadorVacio.ViewHolder holder) {

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        int dWidht=0,dHeigt=0;


        Bitmap bitmap = ((BitmapDrawable)holder.foto.getDrawable()).getBitmap();

        Log.e("medidas","pantalla width: "+width+" height: "+height+" bitmap width: "+bitmap.getWidth()+" height: "+bitmap.getHeight());
        if(bitmap.getWidth()>bitmap.getHeight()){

            dHeigt=(int) width*bitmap.getHeight()/bitmap.getWidth();
            dWidht=width;
        }else{
            dHeigt=(int) width*bitmap.getHeight()/bitmap.getWidth();
            dWidht=width;
        }
        if(dHeigt>height-150){
            dHeigt=height-150;
            dWidht=(height-150)*bitmap.getWidth()/bitmap.getHeight();
        }
        Log.e("Final","width: "+dWidht+" height: "+dHeigt);



// custom dialog

        final Dialog dialog = new Dialog(this,R.style.Theme_Dialog_custom);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.ampliada);

        // set the custom dialog components - text, image and button

        ImageView image = (ImageView) dialog.findViewById(R.id.imagen_ampliada);
        image.setImageBitmap(bitmap);


        dialog.show();
        dialog.getWindow().setLayout(dWidht,dHeigt);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
