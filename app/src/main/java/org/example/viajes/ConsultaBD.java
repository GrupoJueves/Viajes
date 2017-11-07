package org.example.viajes;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Ivan on 07/11/2017.
 */

public class ConsultaBD {

    private static BaseDeDatos BaseDeDatos;
    private static SQLiteDatabase bdw;


    public static void inicializaBD(Context contexto){

        BaseDeDatos = new BaseDeDatos(contexto);
        bdw = BaseDeDatos.getWritableDatabase();
    }

    //Cursor con la lista de itinerarios de un cliente, donde id es el identiticador unico del cliente
    public static Cursor listadoItinerarios(int id){
        SQLiteDatabase bdw = BaseDeDatos.getReadableDatabase();
        return bdw.rawQuery("SELECT route_id AS _id, * FROM route " +
                "WHERE user_id = "+id, null);
    }

    //Cursor con la lista de los puntos de interes de un itinerario (route_id)
    public static Cursor listadoPOIItinerario(int id){
        SQLiteDatabase bdw = BaseDeDatos.getReadableDatabase();
        return bdw.rawQuery("SELECT route_pois_id_id AS _id, * FROM route_pois, route " +
                "WHERE route = route_id AND route_id = "+id, null);
    }

    //Cursor con la lista de los puntos de interes

    public static Cursor listadoPOI(){
        SQLiteDatabase bdw = BaseDeDatos.getReadableDatabase();
        return bdw.rawQuery("SELECT poi_id AS _id, * FROM poi ORDER BY title", null);
    }

    



}
