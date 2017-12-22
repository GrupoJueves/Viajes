package org.masterandroid.wander;

import android.*;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
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

import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by rodriii on 6/12/17.
 */

public class EditarPerfilUsuarioActivity extends AppCompatActivity {


    private TextView  nombre, apellidos, telefono, username, localidad, pais, direccion;
    private SharedPreferences pref;
    private Usuario usuario;
    private com.makeramen.roundedimageview.RoundedImageView imageView;
    private Uri uriFoto;
    Uri mCropImageUri;
    private String uri2;
    private int id;
    final static int RESULTADO_GALERIA = 2;
    final static int RESULTADO_FOTO = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contenedor_editar_perfil);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        nombre = findViewById(R.id.nombre);
        apellidos = findViewById(R.id.apellidos);
        telefono = findViewById(R.id.telefono);
        username = findViewById(R.id.username);
        localidad = findViewById(R.id.poblacion);
        pais = findViewById(R.id.pais);
        direccion = findViewById(R.id.direccion);
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
        getInfo();
        ConsultaBD.updateUser(usuario, id);
        setResult(RESULT_OK);
        EditarPerfilUsuarioActivity.this.overridePendingTransition(0,0);

       onBackPressed();

    }

    public void tomarFoto(View view) {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        uriFoto = Uri.fromFile(new File(
                Environment.getExternalStorageDirectory() + File.separator
                        + "img_" + (System.currentTimeMillis() / 1000) + ".jpg"));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriFoto);
        startActivityForResult(intent, RESULTADO_FOTO);
    }

    public void eliminarFoto(View view)
    {
        uri2 = "";
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
            Toast.makeText(contexto, R.string.archivo_no_encontrado,
                    Toast.LENGTH_LONG).show(); e.printStackTrace();
            return null; }
    }


    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       /* if (requestCode == RESULTADO_GALERIA && resultCode == Activity.RESULT_OK) {
            uri2 =  data.getDataString();
            ponerFoto(imageView, uri2);
        } else if (requestCode == RESULTADO_FOTO && resultCode == Activity.RESULT_OK &&  uriFoto!=null) {
            uri2 = uriFoto.toString();
            ponerFoto(imageView, uri2);
        }*/

        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);


            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                mCropImageUri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
            } else {
                // no permissions required or already grunted, can start crop image activity
                startCropImageActivity(imageUri);
            }
        }
        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                uri2 = result.getUri().toString();
                ((ImageView) findViewById(R.id.foto)).setImageURI(result.getUri());

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

            }
        }

    }

    public void rellenarInfo(){
        usuario = ConsultaBD.infoUser(id);
        if(usuario != null){
            if (!usuario.getNombre().equals("") && !usuario.getNombre().equals("null")){
                nombre.setText(usuario.getNombre());
            }

            if (!usuario.getApellidos().equals("") && !usuario.getApellidos().equals("null")){
                apellidos.setText(usuario.getApellidos());
            }

            if (!usuario.getUsername().equals("") && !usuario.getUsername().equals("null")){
                username.setText(usuario.getUsername());
            }

            if (!usuario.getWeb().equals("") && !usuario.getWeb().equals("null")){
                direccion.setText(usuario.getWeb());
            }

            if (!usuario.getLugar().equals("") && !usuario.getLugar().equals("null")){
                localidad.setText(usuario.getLugar());
            }

            if (!usuario.getPais().equals("") && !usuario.getPais().equals("null")){
                pais.setText(usuario.getPais());
            }

            if (usuario.getTelefono()!=0){
                telefono.setText(String.valueOf(usuario.getTelefono()));
            }

            //poner la foto
            if(!usuario.getPhoto().equals("") && usuario.getPhoto()!= null){

                ponerFoto(imageView,usuario.getPhoto());
            }

        }
    }
    private void getInfo(){
        if (usuario==null){
            usuario = new Usuario();
        }

        usuario.setNombre(""+nombre.getText());
        usuario.setApellidos(""+apellidos.getText());
        usuario.setLugar(""+localidad.getText());
        usuario.setPais(""+pais.getText());
        usuario.setUsername(""+username.getText());
        usuario.setWeb(""+direccion.getText());
        int tel = 0;
        try {
           tel = Integer.parseInt(telefono.getText().toString());
        }catch (Exception e){
            Log.e("error","parse "+telefono.getText().toString());
        }
        usuario.setTelefono(0+tel);

        if(uri2!=null){
            usuario.setPhoto(uri2);
        }


    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CropImage.startPickImageActivity(this);
            } else {
                Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // required permissions granted, start crop image activity
                startCropImageActivity(mCropImageUri);
            } else {
                Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setAspectRatio(1,1)
                .start(this);
    }

    @SuppressLint("NewApi")
    public void onSelectImageClick(View view) {
        if (CropImage.isExplicitCameraPermissionRequired(this)) {
            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE);
        } else {
            CropImage.startPickImageActivity(this);
        }
    }

}
