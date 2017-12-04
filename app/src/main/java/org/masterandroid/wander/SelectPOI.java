package org.masterandroid.wander;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Ivan on 16/11/2017.
 */

public class SelectPOI extends AppCompatActivity implements AdaptadorPoi.OnItemClickListener{

    private long id;
    public AdaptadorPoi adaptador;
    public RecyclerView recyclerViewClientes;
    private RecyclerView.LayoutManager lManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_poi);
        Bundle extras = getIntent().getExtras();
        id = extras.getLong("id", -1);

        //inicializo la base de datos, si no existe la crea
        ConsultaBD.inicializaBD(this);

        recyclerViewClientes = (RecyclerView) findViewById(R.id.selecciona);
        recyclerViewClientes.setHasFixedSize(true);

        // Usar un administrador para LinearLayout
        lManager = new LinearLayoutManager(this);
        recyclerViewClientes.setLayoutManager(lManager);

        //Inicializar los elementos
        Cursor c = ConsultaBD.listadoPOI();
        //creamos el adaptador
        adaptador = new AdaptadorPoi(this,c ,this);
        //Esto seria para el caso de que no existireran rutas para este usuario
        // emptyview seria lo que se mostraria en una lista vacia
        if(adaptador.getItemCount()==0){
            //emptyview.setVisibility(View.VISIBLE);
            recyclerViewClientes.setVisibility(View.GONE);

        }else{
            int a = adaptador.getItemCount();
            Toast.makeText(SelectPOI.this, ""+a, Toast.LENGTH_SHORT).show();
            //emptyview.setVisibility(View.GONE);
            recyclerViewClientes.setVisibility(View.VISIBLE);
        }
        //rellenamos el reciclerview
        recyclerViewClientes.setAdapter(adaptador);


    }

    //accion de pulsar sobre un elemento de la lista
    @Override
    public void onClick(AdaptadorPoi.ViewHolder holder, long id2) {

        if(ConsultaBD.addPoi((int)id,(int)id2,false)){
            setResult(RESULT_OK);
            finish();
        }else{
            setResult(5);
            finish();
        }


    }

    public void cancelar(View v){
        setResult(RESULT_CANCELED);
        finish();

    }


}