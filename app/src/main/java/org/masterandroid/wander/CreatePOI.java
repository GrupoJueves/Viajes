package org.masterandroid.wander;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

/**
 * Created by rodriii on 16/11/17.
 */

public class CreatePOI extends AppCompatActivity {

    private EditText titulo, descripcion;
    private FloatingActionButton floatingCrear;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_poi);

        titulo = findViewById(R.id.titulo);
        descripcion = findViewById(R.id.descripcion);
        floatingCrear = findViewById(R.id.fabCrearPOI);

        floatingCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                POI POI = new POI();
                POI.setTitle(titulo.getText().toString());
                POI.setDescription(descripcion.getText().toString());
                ConsultaBD.newPOI(POI);
            }
        });
    }
}
