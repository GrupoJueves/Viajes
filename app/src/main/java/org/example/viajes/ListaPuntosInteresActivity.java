package org.example.viajes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

public class ListaPuntosInteresActivity extends AppCompatActivity implements AdaptadorPuntosInteres.OnItemClickListener {

    private RecyclerView recyclerViewitinerario;
    public AdaptadorPuntosInteres adaptador;
    private RecyclerView.LayoutManager lManager;

    //Valor para la llamada a añadir poi
    final static int RESULTADO_AÑADIR = 1;

    //Variable donde almacenare el identificador de la ruta
    private long id_ruta;

    private SharedPreferences pref;

    private String nombrePuntoInteres = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_puntos_interes);

        //recojo el valor del identificador de la ruta
        Bundle extras = getIntent().getExtras();
        id_ruta = extras.getLong("id", -1);

        //inicializo la base de datos, si no existe la crea
        ConsultaBD.inicializaBD(this);

        recyclerViewitinerario = (RecyclerView) findViewById(R.id.recicladorPunto);
        recyclerViewitinerario.setHasFixedSize(true);

        // Usar un administrador para LinearLayout
        lManager = new LinearLayoutManager(this);
        recyclerViewitinerario.setLayoutManager(lManager);

        //Inicializar los elementos
        listaPuntosInteres();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ListaPuntosInteresActivity.this, SelectPOI.class);
                i.putExtra("id", id_ruta);
                startActivityForResult(i, RESULTADO_AÑADIR);
            }
        });

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    public void listaPuntosInteres(){

        //Obtenemos el cursor con todos los puntos del usuario
        Cursor c = ConsultaBD.listadoPOIItinerario((int)id_ruta);
        //creamos el adaptador
        adaptador = new AdaptadorPuntosInteres(this, c, this);
        //Esto seria para el caso de que no existireran rutas para este usuario
        // emptyview seria lo que se mostraria en una lista vacia
        if (adaptador.getItemCount() == 0) {
            //emptyview.setVisibility(View.VISIBLE);
            recyclerViewitinerario.setVisibility(View.GONE);
        } else {

            //emptyview.setVisibility(View.GONE);
            recyclerViewitinerario.setVisibility(View.VISIBLE);
        }
        //rellenamos el reciclerview
        recyclerViewitinerario.setAdapter(adaptador);
    }

    //accion de pulsar sobre un elemento de la lista
    @Override
    public void onClick(AdaptadorPuntosInteres.ViewHolder holder, long id) {
        Intent intent = new Intent(this, DetailPOI.class);
        //intent.putExtra("id", id);
        startActivity(intent);
    }


    //Accion a realizar al volver del otra actividad
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == RESULTADO_AÑADIR) {
            if (resultCode == RESULT_OK) {
                listaPuntosInteres();
            } if(resultCode == 5){
                Toast.makeText(ListaPuntosInteresActivity.this, "Error al añadir punto", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(ListaPuntosInteresActivity.this, "Cancelado por el usuario", Toast.LENGTH_SHORT).show();
            }
        }
        }



}
