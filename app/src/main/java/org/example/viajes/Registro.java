package org.example.viajes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Registro extends AppCompatActivity {
    private EditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        ConsultaBD.inicializaBD(this);

        email = (EditText) findViewById(R.id.email);

        Bundle extras = getIntent().getExtras();
        String email_s = extras.getString("email");
        email.setText(email_s);
    }

    /*
    * Método para crear un usuario.
    * Comprueba si el usuario existe, después intenta crearlo y le devuelve a la pantalla de inicio para que ingrese con sus credenciales
    * */
    public void registrarUsuario (View view){
        EditText contraseña = (EditText) findViewById(R.id.contraseña);
        EditText nombre = (EditText) findViewById(R.id.nombre);
        EditText apellido = (EditText) findViewById(R.id.apellido);

        if (checkEmpty(email)) return;
        if (checkEmpty(contraseña)) return;

        String email_s = email.getText().toString();
        String contraseña_s = contraseña.getText().toString();
        String nombre_s = nombre.getText().toString();
        String apellido_s = apellido.getText().toString();

        ConsultaBD.inicializaBD(view.getContext());
        if (ConsultaBD.emailUnico(email_s)){
           if (ConsultaBD.newUser(email_s,contraseña_s,nombre_s,apellido_s)){
               Intent intent = new Intent(this, InicioSesionActivity.class);
               intent.putExtra("email", email.getText().toString());
               startActivity(intent);
           }
           else{
               Toast.makeText(this, "Ha habido un error, intentelo de nuevo más tarde", Toast.LENGTH_LONG).show();
           }
        }
        else{
            email.setError("Existe una cuenta con este correo");
            email.requestFocus();
        }
    }

    /*
    * Comprueba si un campo está vacio, en caso de estarlo le asigna un mensaje de error
    * */
    public static boolean checkEmpty(EditText input){
        if(TextUtils.isEmpty(input.getText().toString())){
            input.setError("Este campo no puede estar vacio");
            input.requestFocus();
            return true;
        }
        return false;
    }
}
