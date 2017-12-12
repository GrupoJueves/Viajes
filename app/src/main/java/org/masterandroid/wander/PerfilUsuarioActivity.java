package org.masterandroid.wander;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by rodriii on 6/12/17.
 */

public class PerfilUsuarioActivity extends AppCompatActivity {

    private TextView correo, nombre, apellidos, telefono, edad, lugar;
    private SharedPreferences pref;
    private Usuario usuario;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);

        correo = findViewById(R.id.correoUsuario);
        nombre = findViewById(R.id.nombreUsuario);
        apellidos = findViewById(R.id.apellidoUsuario);
        telefono = findViewById(R.id.telefonoUsuario);
        edad = findViewById(R.id.edadUsuario);
        lugar = findViewById(R.id.localizacionUsuario);

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        int id = pref.getInt("id", -1);

        if (id == -1) {
            this.finish();
        }
        usuario = ConsultaBD.infoUser(id);
        if(usuario != null){
            correo.setText(usuario.getCorreo());
            nombre.setText(usuario.getNombre());
            apellidos.setText(usuario.getApellidos());
            telefono.setText(String.valueOf(usuario.getTelefono()));
            edad.setText(String.valueOf(usuario.getEdad()));
            lugar.setText(usuario.getLugar());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.perfil_usuario, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.accion_editar:
                lanzarEditar(null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void lanzarEditar(View view){
        Intent i = new Intent(this, EditarPerfilUsuarioActivity.class);
        startActivity(i);
    }
}
