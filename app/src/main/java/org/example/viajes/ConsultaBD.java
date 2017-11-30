package org.example.viajes;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

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

    //Itinerarios

    //Cursor con la lista de itinerarios de un cliente, donde id es el identiticador unico del usuario
    public static Cursor listadoItinerarios(int id){
        SQLiteDatabase bdw = BaseDeDatos.getReadableDatabase();
        return bdw.rawQuery("SELECT route_id AS _id, * FROM route " +
                "WHERE user = "+id, null);
    }

    //Modidica el parametro checked de un itinerario
    public static boolean changeCheck(int id, boolean valor){
        boolean correcto = true;
        int checked = (valor)?1:0;
        try {
            //SQLiteDatabase bdw = BaseDeDatos.getWritableDatabase();
            bdw.execSQL("UPDATE route SET checked = "+checked+" WHERE route_id = "+id);
        }
        catch (Exception e){
            correcto = false;
        }
        return correcto;
    }

    //Crea un nuevo itinerario con el valor de checked a false(0)(No tiene en cuenta el valor de date, ya que en este primer release no se utiliza)
    public static boolean newRoute (int user_id, String title){
        boolean correcto = true;
        try {
           bdw.execSQL("INSERT INTO route (user, title, checked) VALUES ("+user_id+" , '"+title+"' , 0)");
            //bdw.rawQuery("INSERT INTO route  VALUES (null, "+user_id+" , '"+title+"' , 0, null)",null);
        }
        catch (Exception e){
            correcto = false;
            Log.e("error:", e.getMessage());
        }
        return correcto;
    }

    //Elimina un itinerario
    public static boolean deleteRoute (int id){
        boolean correcto = true;
        try {
            bdw.execSQL("DELETE FROM route WHERE route_id = "+id);

        }
        catch (Exception e){
            correcto = false;
            Log.e("error:", e.getMessage());
        }
        return correcto;
    }


    //POI
//No se utilizan con la api de places
    //Cursor con la lista de los puntos de interes

    public static Cursor listadoPOI(){
        SQLiteDatabase bdw = BaseDeDatos.getReadableDatabase();
        return bdw.rawQuery("SELECT poi_id AS _id, * FROM poi ORDER BY title", null);
    }
/*
    //Crear un nuevo punto de interes (se le pasa el elemento POI a añadir a la BD)
    public static boolean newPOI (POI poi){
        boolean correcto = true;
        try {
            bdw.execSQL("INSERT INTO poi (title, description, lon, lat, img) VALUES ('"+poi.getTitle()+"' , '"+poi.getDescription()+"' , "+poi.getLon()+" , "+poi.getLat()+" , '"+poi.getImg()+"')");

        }
        catch (Exception e){
            correcto = false;
            Log.e("error:", e.getMessage());
        }
        return correcto;
    }*/

    //Informacion de un POI en concreto (devuelve un elemto POI con la informacion solicitada)
    public static POI infoPoi (int id){
        POI poi = null;

        Cursor cursor = bdw.rawQuery("SELECT * FROM poi WHERE poi_id = " + id, null);
        if (cursor.moveToNext()){
            poi = new POI();
            poi.setTitle(""+cursor.getString(cursor.getColumnIndex("title")));
            poi.setIdentificador(""+cursor.getString(cursor.getColumnIndex("identificador")));
            poi.setImg(""+cursor.getString(cursor.getColumnIndex("img")));
            poi.setLat(0+cursor.getFloat(cursor.getColumnIndex("lat")));
            poi.setLon(0+cursor.getFloat(cursor.getColumnIndex("lon")));

        }
        cursor.close();


        return poi;
    }

    //Añadir un POI de la api a la BD
    public static boolean newPOI (POI poi){
        boolean correcto = true;
        try {
            bdw.execSQL("INSERT INTO poi (title, identificador, lon, lat, img) VALUES ('"+poi.getTitle()+"' , '"+poi.getIdentificador()+"' , "+poi.getLon()+" , "+poi.getLat()+" , '"+poi.getImg()+"')");

        }
        catch (Exception e){
            correcto = false;
            Log.e("error:", e.getMessage());
        }
        return correcto;
    }

    //obtener id de un identificador
    public static int getIdPOI(String identificador){
        int id = -1;

        Cursor cursor = bdw.rawQuery("SELECT * FROM poi WHERE identificador = '" + identificador+"'", null);
        if (cursor.moveToNext()){

            id = cursor.getInt(cursor.getColumnIndex("poi_id"));

        }


        return id;
    }


    //Pois de un itinerario

    //Cursor con la lista de los puntos de interes de un itinerario (route_id)
    public static Cursor listadoPOIItinerario(int id){
        SQLiteDatabase bdw = BaseDeDatos.getReadableDatabase();
        return bdw.rawQuery("SELECT route_pois_id AS _id, * FROM route_pois, route, poi " +
                "WHERE route = route_id AND poi_id = poi AND route_id = "+id+" ORDER BY position", null);
    }

    //Añadir un nuevo Poi al itinerario (El dia siempre sera 1 de momento)
    public static boolean addPoi(int route_id, int poi_id, int position, boolean checked){
        boolean correcto = true;
        int visto = (checked)?1:0;
        try {
            bdw.execSQL("INSERT INTO route_pois (route, poi, position, day, visto) VALUES ("+route_id+" , "+poi_id+" , "+position+" , 1 , "+visto+")");

        }
        catch (Exception e){
            correcto = false;
            Log.e("error:", e.getMessage());
        }
        return correcto;
    }

    //marcar un Poi como visto (id es el identificador del poi del itinerario que queremos modificar)
    public static boolean changeCheckPoi(int id, boolean valor){
        boolean correcto = true;
        int checked = (valor)?1:0;
        try {
            //SQLiteDatabase bdw = BaseDeDatos.getWritableDatabase();
            bdw.execSQL("UPDATE route_pois SET visto = "+checked+" WHERE route_pois_id = "+id);
        }
        catch (Exception e){
            correcto = false;
        }
        return correcto;
    }

    //Elimina un itinerario
    public static boolean deletePoiRoute (int id){
        boolean correcto = true;
        try {
            bdw.execSQL("DELETE FROM route_pois WHERE route_pois_id = "+id);

        }
        catch (Exception e){
            correcto = false;
            Log.e("error:", e.getMessage());
        }
        return correcto;
    }


    ///Funciones comentarios

    //Añadir comentario en un poi (El valor de rating debe ser entre 1 y 5, se debe de comprobar antes de pasarlos)
    public static boolean addComment(int user_id, int poi_id, String comment, int val, long date){
        boolean correcto = true;

        try {
            bdw.execSQL("INSERT INTO poi_comment (poi, user, comment, rating, date) VALUES ("+poi_id+" , "+user_id+" , '"+comment+"' , "+val+" , "+date+")");

        }
        catch (Exception e){
            correcto = false;
            Log.e("error:", e.getMessage());
        }
        return correcto;
    }

    //Añadir comentario en un poi, en este caso se le pasa un objeto Comentario(El valor de rating debe ser entre 1 y 5, se debe de comprobar antes de pasarlos)
    public static boolean addComment(Comentario commentario){
        boolean correcto = true;

        try {
            bdw.execSQL("INSERT INTO poi_comment (poi, user, comment, rating, date) VALUES ("+commentario.getPoi()+" , "+commentario.getUser()+" , '"+commentario.getUser()+"' , "+commentario.getRating()+" , "+commentario.getDate()+")");

        }
        catch (Exception e){
            correcto = false;
            Log.e("error:", e.getMessage());
        }
        return correcto;
    }

    //Obtener cursor con todos los comentarios de un poi
    public static Cursor listadoComentarios(int poi_id){
        SQLiteDatabase bdw = BaseDeDatos.getReadableDatabase();
        return bdw.rawQuery("SELECT poi_comment_id AS _id, * FROM poi_comment " +
                "WHERE poi = "+poi_id, null);
    }

    //Obtener el comentario de un usuario para un poi
    public static Comentario comentUser (int user_id, int poi_id){
       Comentario comentario = null;

        Cursor cursor = bdw.rawQuery("SELECT * FROM poi_comment WHERE poi = " + poi_id+" AND user = "+user_id, null);
        if (cursor.moveToNext()){
            comentario = new Comentario();
            comentario.setId(0+cursor.getInt(cursor.getColumnIndex("poi_comment_id")));
            comentario.setPoi(0+cursor.getInt(cursor.getColumnIndex("poi")));
            comentario.setUser(0+cursor.getInt(cursor.getColumnIndex("user")));
            comentario.setRating(0+cursor.getInt(cursor.getColumnIndex("rating")));
            comentario.setComment(""+cursor.getString(cursor.getColumnIndex("comment")));
            comentario.setDate(0+cursor.getLong(cursor.getColumnIndex("date")));

        }
        cursor.close();


        return comentario;
    }

    //eliminar un comentario (Solo debe ser el del usuario)

    public static boolean deleteComentario (int id){
        boolean correcto = true;
        try {
            bdw.execSQL("DELETE FROM poi_comment WHERE poi_comment_id = "+id);

        }
        catch (Exception e){
            correcto = false;
            Log.e("error:", e.getMessage());
        }
        return correcto;
    }


}
