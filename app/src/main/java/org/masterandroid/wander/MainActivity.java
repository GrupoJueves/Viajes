package org.masterandroid.wander;

import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements AdaptadorItinerarios.OnItemClickListener {

    public AdaptadorItinerarios adaptador;
    public RecyclerView recyclerViewClientes;
    public EditText email, pass, nombre, surname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setContentView(R.layout.pruebaregistro);

        //inicializo la base de datos, si no existe la crea
        ConsultaBD.inicializaBD(this);

        //referencio el reciclerview
        recyclerViewClientes = (RecyclerView) findViewById(R.id.prueba);
        recyclerViewClientes.setLayoutManager(new LinearLayoutManager(this));

        //creo el recicler view
        listaitinerarios();

        //fab
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crear();
            }
        });

        /* //refererencio los campos de pruebaregistro

        email = findViewById(R.id.correo);
        pass = findViewById(R.id.contraseña);
        nombre = findViewById(R.id.usuario);
        surname = findViewById(R.id.surname);
*/

    }

    ///////MENU///////
    // Infla el menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail_poi, menu);
        return true; //true -> el menu ya esta visible
    }

    //Recibe los OnClicks de los items del menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.map_menu) {
            showPointOnMap(40.418153, -3.684369);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showPointOnMap(Double LatPoint, Double LngPoint) {
        Intent i = new Intent(MainActivity.this, MapActivity.class);
        i.putExtra("LatPoint", LatPoint);
        i.putExtra("LngPoint", LngPoint);
        startActivity(i);
    }

    public void listaitinerarios() {

        //Obtenemos el cursor con todas las rutas del usuario 0
        Cursor c = ConsultaBD.listadoItinerarios(1);//el 1 se debe cambiar por el user_id
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
        //rellenamos el reciclerview
        recyclerViewClientes.setAdapter(adaptador);

    }
    //en el caso de crear o borrar un itinerario sera aconsejable volver a llamar la funcion listaitinerario()
    // para que actualice la vista, tal como se ve en las funciones siguientes

    //accion de pulsar sobre un elemento de la lista
    @Override
    public void onClick(AdaptadorItinerarios.ViewHolder holder, long id) {

        if (ConsultaBD.changeCheck((int) id, true)) {
            Toast.makeText(MainActivity.this, R.string.cambio_realizado, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, getString(R.string.error_cambio) + id, Toast.LENGTH_SHORT).show();
        }
        listaitinerarios();
    }



    //crear un nuevo itinerario llamado prueba
    public void crear() {
        ConsultaBD.newRoute(1, getString(R.string.prueba));
        listaitinerarios();
    }
/*
    public void registrar(View v){
        //inicializo la base de datos, si no existe la crea
        ConsultaBD.inicializaBD(this);
        if(ConsultaBD.newUser(email.getText().toString(),pass.getText().toString(),nombre.getText().toString(),surname.getText().toString())){
            Toast.makeText(MainActivity.this, "Registrado correctamente", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(MainActivity.this, "Error en el pruebaregistro", Toast.LENGTH_SHORT).show();
        }


    }
    public void unico(View v){
        //inicializo la base de datos, si no existe la crea
        ConsultaBD.inicializaBD(this);

        String correo = email.getText().toString();
        if(ConsultaBD.emailUnico(correo)){
            Toast.makeText(MainActivity.this, "El correo no existe", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(MainActivity.this, "el correo ya existe", Toast.LENGTH_SHORT).show();
        }
    }

    public void identificar(View v){
        //inicializo la base de datos, si no existe la crea
        ConsultaBD.inicializaBD(this);
        int user_id = ConsultaBD.identificar(email.getText().toString(), pass.getText().toString());
        if (user_id == -1){
            Toast.makeText(MainActivity.this, "usuario no registrado", Toast.LENGTH_SHORT).show();
        }else{if(user_id == -2){
            Toast.makeText(MainActivity.this, "error en la base de datos", Toast.LENGTH_SHORT).show();
        }
            Toast.makeText(MainActivity.this, "usuario "+user_id, Toast.LENGTH_SHORT).show();
        }

    }
    */
}
