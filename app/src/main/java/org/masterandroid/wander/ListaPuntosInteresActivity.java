package org.masterandroid.wander;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;

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
                Intent i = new Intent(ListaPuntosInteresActivity.this, SelectPOIGrafic.class);
                i.putExtra("id", id_ruta);
                startActivityForResult(i, RESULTADO_AÑADIR);
            }
        });

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Transition lista_enter = TransitionInflater.from(this).inflateTransition(R.transition.transition_lista_enter);
        getWindow().setEnterTransition(lista_enter);
        getWindow().setExitTransition(null);
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
        //Implementamos la funcionalidad del longClick
        adaptador.setOnItemLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(final View v) {
                final int posicion = recyclerViewitinerario.getChildAdapterPosition(v);
                final long id = adaptador.getId(posicion);
                AlertDialog.Builder menu = new AlertDialog.Builder(ListaPuntosInteresActivity.this);
                CharSequence[] opciones = { getString(R.string.abrir), getString(R.string.eliminar), getString(R.string.visitado)};
                menu.setItems(opciones, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int opcion) {
                        switch (opcion) {
                            case 0: //Abrir
                                Intent intent = new Intent(ListaPuntosInteresActivity.this, DetailPOI.class);
                                //intent.putExtra("id", id);
                                startActivity(intent);
                                break;
                            case 1: //Borrar

                                new AlertDialog.Builder(ListaPuntosInteresActivity.this)
                                        .setTitle(R.string.borrar_poi)
                                        .setMessage(R.string.borrar_poi_pregunta)
                                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                ConsultaBD.deletePoiRoute((int)id);
                                                listaPuntosInteres();

                                            }
                                        })
                                        .setNegativeButton("Cancelar", null)
                                        .show();

                                break;
                            case 2: //Marcar como visto
                                ConsultaBD.changeCheckPoi((int) id, true);
                                listaPuntosInteres();
                                break;


                        }
                    }
                });
                menu.create().show();
                return true;
            }
        });

        //rellenamos el reciclerview
        recyclerViewitinerario.setAdapter(adaptador);
    }

    //accion de pulsar sobre un elemento de la lista
    @Override
    public void onClick(AdaptadorPuntosInteres.ViewHolder holder, long id) {
        Intent intent = new Intent(this, DetailPOI.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    //Accion a realizar al volver del otra actividad
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == RESULTADO_AÑADIR) {
            listaPuntosInteres();
        }
    }
}
