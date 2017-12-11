package org.masterandroid.wander;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by rodriii on 6/12/17.
 */

public class EditarPerfilUsuarioActivity extends AppCompatActivity {

    private EditText nombre, apellidos, telefono, edad, lugar;
    private ImageView imageView;
    private Uri uriFoto;
    private SharedPreferences pref;
    private Usuario usuario;
    private int id;
    final static int RESULTADO_GALERIA = 2;
    final static int RESULTADO_FOTO = 3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil_usuario);

        nombre = findViewById(R.id.nombreEdit);
        apellidos = findViewById(R.id.apellidoEdit);
        telefono = findViewById(R.id.telefonoEdit);
        edad = findViewById(R.id.edadEdit);
        lugar = findViewById(R.id.localizacionEdit);
        imageView = findViewById(R.id.foto);

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        id = pref.getInt("id", 0);
        if (id == 0) {
            this.finish();
        }
        usuario = ConsultaBD.infoUser(id);
        if(usuario != null){
            nombre.setText(usuario.getNombre());
            apellidos.setText(usuario.getApellidos());
            telefono.setText(usuario.getTelefono());
            edad.setText(String.valueOf(usuario.getEdad()));
            lugar.setText(usuario.getLugar());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editar_perfil, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.accion_guardar:
                guardar(null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void guardar(View view){
        ConsultaBD.updateUser(usuario, id);
        finish();
    }

    public void tomarFoto(View view) {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        uriFoto = Uri.fromFile(new File(
                Environment.getExternalStorageDirectory() + File.separator
                        + "img_" + (System.currentTimeMillis() / 1000) + ".jpg"));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriFoto);
        startActivityForResult(intent, RESULTADO_FOTO);
    }

    public void eliminarFoto(View view) {
        ponerFoto(imageView, null);
    }

    public void galeria(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULTADO_GALERIA);
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
            Toast.makeText(contexto, "Fichero/recurso no encontrado",
                    Toast.LENGTH_LONG).show(); e.printStackTrace();
            return null; }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULTADO_GALERIA && resultCode == Activity.RESULT_OK) {
            ponerFoto(imageView, data.getDataString());
        } else if (requestCode == RESULTADO_FOTO && resultCode == Activity.RESULT_OK && lugar!=null && uriFoto!=null) {
            ponerFoto(imageView, uriFoto.toString());
        }
    }
}
