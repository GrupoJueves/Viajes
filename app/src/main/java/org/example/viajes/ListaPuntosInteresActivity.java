package org.example.viajes;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ListaPuntosInteresActivity extends AppCompatActivity implements AdaptadorPuntosInteres.OnItemClickListener {

    private RecyclerView recyclerViewClientes;
    public AdaptadorPuntosInteres adaptador;
    private RecyclerView.LayoutManager lManager;
    private SharedPreferences pref;

    private String nombrePuntoInteres = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_puntos_interes);

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        int id = pref.getInt("id", 0);
        if (id == 0) {
            this.finish();
        }

        //inicializo la base de datos, si no existe la crea
        ConsultaBD.inicializaBD(this);

        recyclerViewClientes = (RecyclerView) findViewById(R.id.recicladorPunto);
        recyclerViewClientes.setHasFixedSize(true);

        // Usar un administrador para LinearLayout
        lManager = new LinearLayoutManager(this);
        recyclerViewClientes.setLayoutManager(lManager);

        //Inicializar los elementos
        //listaPuntosInteres();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Nombre de punto de interés");
                // Set up the input
                final EditText input = new EditText(view.getContext());
                //input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                builder.setView(input);
                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        nombrePuntoInteres = input.getText().toString();
                        if (!nombrePuntoInteres.isEmpty()) {
                            int id = pref.getInt("id", 0);
                            //ConsultaBD.newPOI("","","",0.0,0.0);
                            //listaPuntosInteres();
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
    }

    /*public void listaPuntosInteres(){
        int id = pref.getInt("id", 0);
        //Obtenemos el cursor con todos los puntos del usuario
        Cursor c = ConsultaBD.listadoPOI();
        //creamos el adaptador
        adaptador = new AdaptadorPuntosInteres(this, c, this);
        //Esto seria para el caso de que no existireran rutas para este usuario
        // emptyview seria lo que se mostraria en una lista vacia
        if (adaptador.getItemCount() == 0) {
            //emptyview.setVisibility(View.VISIBLE);
            recyclerViewClientes.setVisibility(View.GONE);
        } else {
            //emptyview.setVisibility(View.GONE);
            recyclerViewClientes.setVisibility(View.VISIBLE);
        }
        //rellenamos el reciclerview
        recyclerViewClientes.setAdapter(adaptador);
    }*/

    //accion de pulsar sobre un elemento de la lista
    @Override
    public void onClick(AdaptadorPuntosInteres.ViewHolder holder, long id) {
        if (ConsultaBD.changeCheck((int) id, true)) {
            Toast.makeText(ListaPuntosInteresActivity.this, "Cambio realizado", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, ListaPuntosInteresActivity.class);
            intent.putExtra("", "");
            startActivity(intent);
        } else {
            Toast.makeText(ListaPuntosInteresActivity.this, "Error al intentar cambiar" + id, Toast.LENGTH_SHORT).show();
        }
        //listaPuntosInteres();
    }
}
