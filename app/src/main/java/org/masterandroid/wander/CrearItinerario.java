package org.masterandroid.wander;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class CrearItinerario extends AppCompatActivity implements  DatePickerDialog.OnDateSetListener{


    private TextView fecha;
    private ImageView calendar, imageRef, imageref2;
    private EditText itinerario;
    private int ref=1;

    long id_user;
    long date = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_itinerario);

        //recojo el valor del identificador del usuario
        Bundle extras = getIntent().getExtras();
        id_user = extras.getLong("id", -1);

        fecha = findViewById(R.id.fecha);
        calendar = findViewById( R.id.calendar);
        imageRef = findViewById(R.id.referencia2);
        imageref2 = findViewById(R.id.referencia);
        itinerario = findViewById(R.id.name_itinerario);


    }

    public void crear(View v)
    {

        String nombreItinerario = ""+itinerario.getText();

        //inicializo la base de datos, si no existe la crea
        ConsultaBD.inicializaBD(this);

        ConsultaBD.newRoute((int)id_user, nombreItinerario, date, ref);

        setResult(RESULT_OK);
        finish();
    }

    public void calendar(View v){
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setEscuchador(this);
        newFragment.show(getSupportFragmentManager(), "Elegir fecha");
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        Calendar calendario = Calendar.getInstance();
        calendario.set(year, month, day);
        long valor = calendario.getTimeInMillis();
        mostrarfecha(valor);

    }
    public void mostrarfecha(long num){

        date = num;
        fecha.setText(DateFormat.getDateInstance().format(new Date(num)));
        fecha.setVisibility(View.VISIBLE);;
        calendar.setVisibility(View.GONE);


    }

    public void pickImagen(View v){

        Intent intent = new Intent(this, PickImage.class);
        startActivityForResult(intent,888);
    }

    public void mostrarImagen(){
        switch (ref){
            case 1:
                imageRef.setImageResource(R.drawable.ref1);
                imageref2.setVisibility(View.GONE);
                imageRef.setVisibility(View.VISIBLE);
                break;
            case 2:
                imageRef.setImageResource(R.drawable.ref2);
                imageref2.setVisibility(View.GONE);
                imageRef.setVisibility(View.VISIBLE);
                break;
            case 3:
                imageRef.setImageResource(R.drawable.ref3);
                imageref2.setVisibility(View.GONE);
                imageRef.setVisibility(View.VISIBLE);
                break;
            case 4:
                imageRef.setImageResource(R.drawable.ref4);
                imageref2.setVisibility(View.GONE);
                imageRef.setVisibility(View.VISIBLE);
                break;
            case 5:
                imageRef.setImageResource(R.drawable.ref5);
                imageref2.setVisibility(View.GONE);
                imageRef.setVisibility(View.VISIBLE);
                break;
            case 6:
                imageRef.setImageResource(R.drawable.ref6);
                imageref2.setVisibility(View.GONE);
                imageRef.setVisibility(View.VISIBLE);
                break;
            default:
                imageRef.setImageResource(R.drawable.ref1);
                imageref2.setVisibility(View.GONE);
                imageRef.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == 888 && resultCode == RESULT_OK){
            ref = data.getExtras().getInt("ref",1);
            mostrarImagen();
        }
    }

}
