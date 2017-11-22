package org.example.viajes;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ListaItinerariosActivity extends AppCompatActivity implements AdaptadorItinerarios.OnItemClickListener, NavigationView.OnNavigationItemSelectedListener{
    private RecyclerView recyclerViewClientes;
    public AdaptadorItinerarios adaptador;
    private RecyclerView.LayoutManager lManager;
    private SharedPreferences pref;

    private String nombreItinerario = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_itinerarios);

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        int id = pref.getInt("id",0);
        if (id == 0){this.finish();}

        //inicializo la base de datos, si no existe la crea
        ConsultaBD.inicializaBD(this);

        recyclerViewClientes = (RecyclerView) findViewById(R.id.reciclador);
        recyclerViewClientes.setHasFixedSize(true);

        // Usar un administrador para LinearLayout
        lManager = new LinearLayoutManager(this);
        recyclerViewClientes.setLayoutManager(lManager);

        //Inicializar los elementos
        listaitinerarios();

        FloatingActionButton fab=(FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Nombre de itinerario");
                // Set up the input
                final EditText input = new EditText(view.getContext());
                //input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                builder.setView(input);
                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        nombreItinerario = input.getText().toString();
                        if (!nombreItinerario.isEmpty()){
                            int id = pref.getInt("id",0);
                            ConsultaBD.newRoute(id, nombreItinerario);
                            listaitinerarios();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // Navigation Drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(
                R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(
                R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void listaitinerarios(){
        int id = pref.getInt("id",0);
        //Obtenemos el cursor con todas las rutas del usuario
        Cursor c = ConsultaBD.listadoItinerarios(id);
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

        //Implementamos la funcionalidad del longClick
        adaptador.setOnItemLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(final View v) {
                final int posicion = recyclerViewClientes.getChildAdapterPosition(v);
                final long id = adaptador.getId(posicion);
                AlertDialog.Builder menu = new AlertDialog.Builder(ListaItinerariosActivity.this);
                CharSequence[] opciones = { "Abrir", "Eliminar", "Visitado"};
                menu.setItems(opciones, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int opcion) {
                        switch (opcion) {
                            case 0: //Abrir
                                Intent intent = new Intent(ListaItinerariosActivity.this, ListaPuntosInteresActivity.class);
                                intent.putExtra("id", id);
                                startActivity(intent);
                                break;
                            case 1: //Borrar

                                new AlertDialog.Builder(ListaItinerariosActivity.this)
                                        .setTitle("Borrar Itinerario")
                                        .setMessage("¿Seguro que quiere borrar este itinerario?")
                                        .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                ConsultaBD.deleteRoute((int)id);
                                                listaitinerarios();

                                            }
                                        })
                                        .setNegativeButton("Cancelar", null)
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
            startActivity(intent);
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_itinerarios) {
            if (!this.getClass().equals(ListaItinerariosActivity.class)) {
                Intent intent = new Intent(this, ListaItinerariosActivity.class);
                startActivity(intent);
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
            Intent intent = new Intent(this, InicioSesionActivity.class);
            startActivity(intent);
            finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(
                R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(
                R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void showPointOnMap(Double LatPoint,Double LngPoint){
        Intent i = new Intent(ListaItinerariosActivity.this, MapActivity.class);
        i.putExtra("LatPoint", LatPoint);
        i.putExtra("LngPoint", LngPoint);
        startActivity(i);
    }

    public void lanzarPreferencias(View view){
        Intent i = new Intent(this, PreferenciasActivity.class);
        startActivity(i);
    }
}
