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


    //Registro e Identificacion

    //Comprobar que el e-mail no existe
    public static boolean emailUnico(String email){
        boolean unico = false;
        SQLiteDatabase bdw = BaseDeDatos.getReadableDatabase();
        Cursor c = bdw.rawQuery("SELECT * FROM user WHERE email = '"+email+"'", null);
        if (c.getCount()==0){
            unico = true;
        }
        c.close();
        return unico;
    }

    //Registrar un nuevo usuario
    public static boolean newUser(String email, String password, String name, String surname){
        boolean correcto = true;
        try{
            bdw.execSQL("INSERT INTO user (email,password,name,surname) VALUES ('"+email+"' , '"+password+"' , '"+name+"' , '"+surname+"')");
        }
        catch (Exception e){
            correcto = false;
        }

        return correcto;
    }

    //Identificarse (devuelve -1 si el usuario no esta registrado, -2 en caso de error en lectura de la base de datos, si va bien devuelve el identificador del usuario)
    public static int identificar(String email, String password){
        int id = -1;
        try {
            Cursor c = bdw.rawQuery("SELECT * FROM user WHERE email = '"+email+"' AND password = '"+password+"'",null);
            if(c.moveToNext()){
                id=c.getInt(0);
            }
            c.close();
        }catch (Exception e){
            id = -2;
        }

        return id;
    }





    //Cursor con la lista de itinerarios de un cliente, donde id es el identiticador unico del usuario
    public static Cursor listadoItinerarios(int id){
        SQLiteDatabase bdw = BaseDeDatos.getReadableDatabase();
        return bdw.rawQuery("SELECT route_id AS _id, * FROM route " +
                "WHERE user = "+id, null);
    }

    //Cursor con la lista de los puntos de interes de un itinerario (route_id)
    public static Cursor listadoPOIItinerario(int id){
        SQLiteDatabase bdw = BaseDeDatos.getReadableDatabase();
        return bdw.rawQuery("SELECT route_pois_id AS _id, * FROM route_pois, route, poi " +
                "WHERE route = route_id AND poi_id = poi AND route_id = "+id, null);
    }

    //Cursor con la lista de los puntos de interes

    public static Cursor listadoPOI(){
        SQLiteDatabase bdw = BaseDeDatos.getReadableDatabase();
        return bdw.rawQuery("SELECT poi_id AS _id, * FROM poi ORDER BY title", null);
    }





}
