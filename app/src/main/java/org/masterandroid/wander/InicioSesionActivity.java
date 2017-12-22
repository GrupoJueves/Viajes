package org.masterandroid.wander;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class InicioSesionActivity extends AppCompatActivity {
    private EditText email;
    private EditText contraseña;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_sesion);

        email = (EditText) findViewById(R.id.email);
        contraseña = (EditText) findViewById(R.id.contraseña);


        Transition slide = TransitionInflater.from(this).inflateTransition(R.transition.transition_slide);
        getWindow().setExitTransition(slide);
    }



    public void acceder (View view){
        if (checkEmpty(email)) return;
        if (checkEmpty(contraseña)) return;

        int answer = ConsultaBD.identificar(email.getText().toString(), contraseña.getText().toString());
        if (answer > 0){
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = pref.edit();
            CheckBox recordarme= (CheckBox) findViewById(R.id.recordarme);
            //Recordar usuario mediante preferencias compartidas
            if (recordarme.isChecked()){
                editor.putBoolean("rememberMe", true);
            }
            editor.putInt("id", answer);
            editor.commit();
            Intent intent = new Intent(this, ListaItinerariosActivity.class);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            mHandler.postDelayed(new Runnable() {
                public void run() {
                    finish();
                }
            }, 5000);

        }
        else if (answer == -1){
            Snackbar.make(view, R.string.login_incorrecto, Snackbar.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, R.string.login_error, Toast.LENGTH_LONG).show();
        }
    }

    public void borrarCampos (View view){
        EditText contraseña = (EditText) findViewById(R.id.contraseña);
        email.setText("");
        contraseña.setText("");
        email.requestFocus();
    }

    public void registrarUsuario (View view){
        Intent intent = new Intent(this, Registro.class);
        intent.putExtra("email", email.getText().toString());
        ActivityOptionsCompat options = ActivityOptionsCompat. makeSceneTransitionAnimation(InicioSesionActivity.this, new Pair<View, String>(findViewById(R.id.logo), getString(R.string.transition_name_logo)));
       // startActivityForResult(intent,42);
        ActivityCompat.startActivityForResult(InicioSesionActivity.this, intent,42, options .toBundle());
    }

    /*
    * Para evitar volver al login al darle atrás, sale de la aplicación
    * */
    /*@Override protected void onRestart() {
        super.onRestart();
        this.finish();
    }*/
    /*
* Comprueba si un campo está vacio, en caso de estarlo le asigna un mensaje de error
* */
    public boolean checkEmpty(EditText input){
        if(TextUtils.isEmpty(input.getText().toString())){
            input.setError(getString(R.string.campo_vacio_error));
            input.requestFocus();
            return true;
        }
        return false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 42 && resultCode == RESULT_OK){

            String email_s = data.getExtras().getString("email");
            Toast.makeText(this, R.string.usuario_creado_toast, Toast.LENGTH_LONG).show();
            email.setText(email_s);
        }

    }

}
