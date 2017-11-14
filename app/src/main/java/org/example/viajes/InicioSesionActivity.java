package org.example.viajes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class InicioSesionActivity extends AppCompatActivity {
    private EditText email;
    private EditText contraseña;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_sesion);

        ConsultaBD.inicializaBD(this);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean rememberMe = pref.getBoolean("rememberMe",false);
        if (rememberMe){
            Intent intent = new Intent(this, ListaItinerariosActivity.class);
            startActivity(intent);
        }

        email = (EditText) findViewById(R.id.email);
        contraseña = (EditText) findViewById(R.id.contraseña);

        if (getIntent().hasExtra("email")){
            Bundle extras = getIntent().getExtras();
            String email_s = extras.getString("email");
            Toast.makeText(this, "Usuario creado. Inicie sesión con sus credenciales", Toast.LENGTH_LONG).show();
            email.setText(email_s);
        }
    }

    public void mostrarContraseña(View v) {
        EditText contraseña = (EditText) findViewById(R.id.contraseña);
        CheckBox mostrar = (CheckBox) findViewById(R.id.mostrar_contraseña);
        if (mostrar.isChecked()) {
            contraseña.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_NORMAL);
        } else {
            contraseña.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
    }

    public void acceder (View view){
        if (Registro.checkEmpty(email)) return;
        if (Registro.checkEmpty(contraseña)) return;

        int answer = ConsultaBD.identificar(email.getText().toString(),contraseña.getText().toString());
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
            startActivity(intent);
        }
        else if (answer == -1){
            Snackbar.make(view, "Correo o contraseña incorrectos", Snackbar.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "Ha habido un error, intentelo de nuevo más tarde", Toast.LENGTH_LONG).show();
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
        startActivity(intent);
    }

    /*
    * Para evitar volver al login al darle atrás, sale de la aplicación
    * */
    @Override protected void onRestart() {
        super.onRestart();
        this.finish();
    }
}
