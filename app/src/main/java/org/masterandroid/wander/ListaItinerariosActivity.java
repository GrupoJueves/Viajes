package org.masterandroid.wander;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.mxn.soul.flowingdrawer_core.ElasticDrawer;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;

public class ListaItinerariosActivity extends AppCompatActivity implements AdaptadorItinerarios.OnItemClickListener {
    private RecyclerView recyclerViewClientes;
    public AdaptadorItinerarios adaptador;
    private RecyclerView.LayoutManager lManager;
    private SharedPreferences pref;

    private String nombreItinerario = "";
    private FlowingDrawer mDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_itinerarios);

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        int id = pref.getInt("id", 0);
        if (id == 0) {
            this.finish();
        }

        //inicializo la base de datos, si no existe la crea
        ConsultaBD.inicializaBD(this);

        recyclerViewClientes = (RecyclerView) findViewById(R.id.reciclador);
        recyclerViewClientes.setHasFixedSize(true);

        // Usar un administrador para LinearLayout
        lManager = new LinearLayoutManager(this);
        recyclerViewClientes.setLayoutManager(lManager);

        //Inicializar los elementos
        listaitinerarios();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String confirmar = getString(R.string.confirmar);
                String cancelar = getString(R.string.cancelar);
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle(getString(R.string.nombre_itinerario));
                // Set up the input
                final EditText input = new EditText(view.getContext());
                //input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                builder.setView(input);
                // Set up the buttons
                builder.setPositiveButton(confirmar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        nombreItinerario = input.getText().toString();
                        if (!nombreItinerario.isEmpty()) {
                            int id = pref.getInt("id", 0);
                            ConsultaBD.newRoute(id, nombreItinerario);
                            listaitinerarios();
                        }
                    }
                });
                builder.setNegativeButton(cancelar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });

        // Toolbar
        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final Intent intent1 = new Intent(this, ListaItinerariosActivity.class);
        final Intent intent2 = new Intent(this, InicioSesionActivity.class);
        NavigationView navigationView = (NavigationView) findViewById(R.id.vNavigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressWarnings("StatementWithEmptyBody")
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_itinerarios) {
                    if (!this.getClass().equals(ListaItinerariosActivity.class)) {
                        startActivity(intent1);
                    }
                } else if (id == R.id.nav_preferencias) {
                    lanzarPreferencias(null);
                    return true;
                } else if (id == R.id.nav_mapa) {
                    showPointOnMap(40.418153, -3.684369);
                    return true;
                } else if (id == R.id.nav_salir) {
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putInt("id", 0);
                    editor.putBoolean("rememberMe", false);
                    editor.commit();
                    startActivity(intent2);
                    finish();
                }

                mDrawer.closeMenu();
                return true;
            }
        });
        mDrawer = (FlowingDrawer) findViewById(R.id.drawerlayout);
        mDrawer.setTouchMode(ElasticDrawer.TOUCH_MODE_BEZEL);
        toolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.toggleMenu();
            }
        });

        Transition lista_enter = TransitionInflater.from(this).inflateTransition(R.transition.transition_explode);
        getWindow().setEnterTransition(lista_enter);
    }

    public void listaitinerarios() {
        int id = pref.getInt("id", 0);
        //Obtenemos el cursor con todas las rutas del usuario
        Cursor c = ConsultaBD.listadoItinerarios(id);
        //creamos el adaptador
        adaptador = new AdaptadorItinerarios(this, c, this);
        //Esto seria para el caso de que no existireran rutas para este usuario
        // emptyview seria lo que se mostraria en una lista vacia
        if (adaptador.getItemCount() == 0) {
            //emptyview.setVisibility(View.VISIBLE);
            recyclerViewClientes.setVisibility(View.GONE);
        } else {
            //emptyview.setVisibility(View.GONE);
            recyclerViewClientes.setVisibility(View.VISIBLE);
        }

        //Implementamos la funcionalidad del longClick
        adaptador.setOnItemLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(final View v) {
                final int posicion = recyclerViewClientes.getChildAdapterPosition(v);
                final long id = adaptador.getId(posicion);
                AlertDialog.Builder menu = new AlertDialog.Builder(ListaItinerariosActivity.this);
                CharSequence[] opciones = {getString(R.string.abrir),getString(R.string.eliminar), getString(R.string.visitado)};
                menu.setItems(opciones, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int opcion) {
                        switch (opcion) {
                            case 0: //Abrir
                                Intent intent = new Intent(ListaItinerariosActivity.this, ListaPuntosInteresActivity.class);
                                intent.putExtra("id", id);
                                startActivity(intent);
                                break;
                            case 1: //Borrar
                                String confirmar = getString(R.string.confirmar);
                                String cancelar = getString(R.string.cancelar);
                                String mensaje = getString(R.string.borrar_itinerario_pregunta);
                                new AlertDialog.Builder(ListaItinerariosActivity.this)
                                        .setTitle(getString(R.string.borrar_itinerario))
                                        .setMessage(mensaje)
                                        .setPositiveButton(confirmar, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                ConsultaBD.deleteRoute((int) id);
                                                listaitinerarios();

                                            }
                                        })
                                        .setNegativeButton(cancelar, null)
                                        .show();

                                break;
                            case 2: //Marcar como visto
                                ConsultaBD.changeCheck((int) id, true);
                                listaitinerarios();
                                break;

                        }
                    }
                });
                menu.create().show();
                return true;
            }
        });


        //rellenamos el reciclerview
        recyclerViewClientes.setAdapter(adaptador);
    }

    //accion de pulsar sobre un elemento de la lista
    @Override
    public void onClick(AdaptadorItinerarios.ViewHolder holder, long id) {
        Intent intent = new Intent(this, ListaPuntosInteresActivity.class);
        intent.putExtra("id", id);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }


    @Override
    public void onBackPressed() {
        if (mDrawer.isMenuVisible()) {
            mDrawer.closeMenu();
        } else {
            super.onBackPressed();
        }
    }

    private void showPointOnMap(Double LatPoint, Double LngPoint) {
        Intent i = new Intent(ListaItinerariosActivity.this, MapActivity.class);
        i.putExtra("LatPoint", LatPoint);
        i.putExtra("LngPoint", LngPoint);
        startActivity(i);
    }

    public void lanzarPreferencias(View view) {
        Intent i = new Intent(this, PreferenciasActivity.class);
        startActivity(i);
    }
}
