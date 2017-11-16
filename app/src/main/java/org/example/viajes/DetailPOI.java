package org.example.viajes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by rodriii on 16/11/17.
 */

public class DetailPOI extends AppCompatActivity {

    private TextView titulo, detalle, longitud, latitud;
    private ImageView imagePOI;
    private POI POI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_poi);

        titulo = findViewById(R.id.titulo);
        detalle = findViewById(R.id.detalle);
        longitud = findViewById(R.id.longitud);
        latitud = findViewById(R.id.latitud);
        imagePOI = findViewById(R.id.imagePOI);

        rellenarPOI();
    }

    public void rellenarPOI(){
        POI = ConsultaBD.infoPoi(4);
        titulo.setText(POI.getTitle());
        detalle.setText(POI.getDescription());
        longitud.setText(String.valueOf(POI.getLon()));
        latitud.setText(String.valueOf(POI.getLat()));
        //imagePOI
    }
}
