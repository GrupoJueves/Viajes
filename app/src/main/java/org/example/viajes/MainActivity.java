package org.example.viajes;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements  AdaptadorItinerarios.OnItemClickListener{

    public AdaptadorItinerarios adaptador;
    public RecyclerView recyclerViewClientes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //referencio el reciclerview
        recyclerViewClientes = (RecyclerView) findViewById(R.id.prueba);
        recyclerViewClientes.setLayoutManager(new LinearLayoutManager(this));

        //creo el recicler view
        listaitinerarios();

    }

    public void listaitinerarios(){
        //inicializo la base de datos, si no existe la crea
        ConsultaBD.inicializaBD(this);

        //Obtenemos el cursor con todas las rutas del usuario 0
            Cursor c =ConsultaBD.listadoItinerarios(0);//el 0 se debe cambiar por el user_id
        //creamos el adaptador
        adaptador = new AdaptadorItinerarios(this,c ,this);
        //Esto seria para el caso de que no existireran rutas para este usuario
        // emptyview seria lo que se mostraria en una lista vacia
            if(adaptador.getItemCount()==0){
                //emptyview.setVisibility(View.VISIBLE);
                recyclerViewClientes.setVisibility(View.GONE);
            }else{
                //emptyview.setVisibility(View.GONE);
                recyclerViewClientes.setVisibility(View.VISIBLE);
            }
            //rellenamos el reciclerview
            recyclerViewClientes.setAdapter(adaptador);


    }

    //accion de pulsar sobre un elemento de la lista
    @Override
    public void onClick(AdaptadorItinerarios.ViewHolder holder, long id) {
        Toast.makeText(MainActivity.this, " elemento " + id, Toast.LENGTH_SHORT).show();
    }

    //en el caso de crear o borrar un itinerario sera aconsejable volver a llamar la funcion listaitinerario() para que actualice la vista

}
