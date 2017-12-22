package org.masterandroid.wander;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;

/**
 * Created by rodriii on 6/12/17.
 */

public class PerfilUsuarioActivity extends AppCompatActivity {

    private TextView correo, nombre, apellidos, telefono, username, localidad, pais, direccion;
    private com.makeramen.roundedimageview.RoundedImageView imageView;
    private SharedPreferences pref;
    private Usuario usuario;
    private int id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contenedor_perfil_usuario);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        correo = findViewById(R.id.e_mail);
        nombre = findViewById(R.id.nombre);
        apellidos = findViewById(R.id.apellido);
        telefono = findViewById(R.id.telefono);
        username = findViewById(R.id.username);
        localidad = findViewById(R.id.poblacion);
        pais = findViewById(R.id.pais);
        direccion = findViewById(R.id.direcion);
        imageView = findViewById(R.id.foto);

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        id = pref.getInt("id", -1);

        if (id == -1) {
            this.finish();
        }

        rellenarInfo();
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

    @SuppressLint("RestrictedApi")
    public void lanzarEditar(View view){
        Intent i = new Intent(this, EditarPerfilUsuarioActivity.class);
        ActivityOptionsCompat options = ActivityOptionsCompat. makeSceneTransitionAnimation(PerfilUsuarioActivity.this, new Pair<View, String>(findViewById(R.id.foto), getString(R.string.transition_name_user)));
        ActivityCompat.startActivityForResult(PerfilUsuarioActivity.this,i,1, options.toBundle());
    }

    public void rellenarInfo(){
        usuario = ConsultaBD.infoUser(id);

        if(usuario != null){


            if (usuario.getNombre().equals("") || usuario.getNombre().equals("null")){
                nombre.setVisibility(View.INVISIBLE);
            }else{
                nombre.setVisibility(View.VISIBLE);
                nombre.setText(usuario.getNombre());
            }

            if (usuario.getApellidos().equals("")|| usuario.getApellidos().equals("null")){
                apellidos.setVisibility(View.INVISIBLE);
            }else{
                apellidos.setVisibility(View.VISIBLE);
                apellidos.setText(usuario.getApellidos());
            }

            if (usuario.getUsername().equals("")|| usuario.getUsername().equals("null")){
                username.setVisibility(View.INVISIBLE);
            }else{
                username.setVisibility(View.VISIBLE);
                username.setText(usuario.getUsername());
            }

            if (usuario.getWeb().equals("")|| usuario.getWeb().equals("null")){
                direccion.setVisibility(View.GONE);
            }else{
                direccion.setVisibility(View.VISIBLE);
                direccion.setText(usuario.getWeb());
            }

            if (usuario.getLugar().equals("")|| usuario.getLugar().equals("null")){
                localidad.setVisibility(View.GONE);
            }else{
                localidad.setVisibility(View.VISIBLE);
                localidad.setText(usuario.getLugar());
            }

            if (usuario.getPais().equals("")|| usuario.getPais().equals("null")){
                pais.setVisibility(View.GONE);
            }else{
                pais.setVisibility(View.VISIBLE);
                pais.setText(usuario.getPais());
            }

            if (usuario.getTelefono()==0 ){
                telefono.setVisibility(View.GONE);
            }else{
                telefono.setVisibility(View.VISIBLE);
                telefono.setText(String.valueOf(usuario.getTelefono()));
            }

           correo.setText(usuario.getCorreo()); //el correo siempre existe


            //poner la foto
            if(!usuario.getPhoto().equals("") && usuario.getPhoto()!= null){

                ponerFoto(imageView,usuario.getPhoto());
            }


        }

    }

    protected void ponerFoto(ImageView imageView, String uri) {
        if (uri != null) {
            imageView.setImageBitmap(reduceBitmap(this, uri, 1024, 1024));
        } else {
            imageView.setImageBitmap(null);
        }
    }

    public static Bitmap reduceBitmap(Context contexto, String uri, int maxAncho, int maxAlto) {
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(contexto.getContentResolver()
                    .openInputStream(Uri.parse(uri)), null, options);
            options.inSampleSize = (int) Math.max(
                    Math.ceil(options.outWidth / maxAncho),
                    Math.ceil(options.outHeight / maxAlto));
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeStream(contexto.getContentResolver() .openInputStream(Uri.parse(uri)), null, options);
        } catch (FileNotFoundException e) {
            Toast.makeText(contexto, R.string.archivo_no_encontrado,
                    Toast.LENGTH_LONG).show(); e.printStackTrace();
            return null; }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1 && resultCode==RESULT_OK)
       rellenarInfo();
    }

}
