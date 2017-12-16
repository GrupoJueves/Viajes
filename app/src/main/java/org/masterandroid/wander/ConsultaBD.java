package org.masterandroid.wander;

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
            int id = identificar(email,password);
            bdw.execSQL("INSERT INTO user_data (user,photo) VALUES ("+id+", '')");
        }
        catch (Exception e){
            correcto = false;
        }

        return correcto;
    }

    //Informacion de un usuario
    public static Usuario infoUser (int id){
        Usuario user = null;

        Cursor cursor = bdw.rawQuery("SELECT * FROM user, user_data WHERE  user_id = " + id +" AND user = "+id, null);

        if (cursor.moveToNext()){
            user = new Usuario();
            user.setCorreo(""+cursor.getString(cursor.getColumnIndex("email")));
            user.setNombre(""+cursor.getString(cursor.getColumnIndex("name")));
            user.setApellidos(""+cursor.getString(cursor.getColumnIndex("surname")));
            user.setPais(""+cursor.getString(cursor.getColumnIndex("pais")));
            user.setUsername(""+cursor.getString(cursor.getColumnIndex("username")));
            user.setLugar(""+cursor.getString(cursor.getColumnIndex("localidad")));
            user.setPhoto(""+cursor.getString(cursor.getColumnIndex("photo")));
            user.setWeb(""+cursor.getString(cursor.getColumnIndex("web")));
            user.setEdad(0+cursor.getInt(cursor.getColumnIndex("edad")));
            user.setTelefono(0+cursor.getInt(cursor.getColumnIndex("telefono")));
            user.setRef(0+cursor.getInt(cursor.getColumnIndex("referencia")));

        }else{
            Log.e("cursor vacio","");
        }
        cursor.close();

        return user;
    }

    //Actualizar un usuario
    public static boolean updateUser (Usuario usuario, int id){
        boolean correcto = true;
        try {
            //SQLiteDatabase bdw = BaseDeDatos.getWritableDatabase();
            bdw.execSQL("UPDATE user SET " +
                    "name = '"+usuario.getNombre()+"', " +
                    "surname = '"+usuario.getApellidos()+"' " +
                    "WHERE user_id = "+id);

            bdw.execSQL("UPDATE user_data SET " +
                    "photo = '"+usuario.getPhoto()+"', " +
                    "localidad = '"+usuario.getLugar()+"', " +
                    "telefono = "+usuario.getTelefono()+", " +
                    "edad = "+usuario.getEdad()+", " +
                    "pais = '"+usuario.getPais()+"', " +
                    "username = '"+usuario.getUsername()+"', " +
                    "web = '"+usuario.getWeb()+"', " +
                    "referencia = "+usuario.getRef()+"  " +
                    "WHERE user = "+id);
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

    //Obtener la info de un itinerario
    public static Itinerario infoRoute (int id){
        Itinerario itinerario = null;

        Cursor cursor = bdw.rawQuery("SELECT * FROM route WHERE route_id = " + id, null);
        if (cursor.moveToNext()){
            itinerario = new Itinerario();
            itinerario.setTitulo(""+cursor.getString(cursor.getColumnIndex("title")));
           itinerario.setFecha(0+cursor.getLong(cursor.getColumnIndex("date")));
           itinerario.setRef(0+cursor.getInt(cursor.getColumnIndex("ref")));
           itinerario.setVisto(0+cursor.getInt(cursor.getColumnIndex("checked")));

        }
        cursor.close();


        return itinerario;
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

    //Crea un nuevo itinerario con el valor de checked a false(0)
    public static boolean newRoute (int user_id, String title, long date, int ref){
        boolean correcto = true;
        try {
            bdw.execSQL("INSERT INTO route (user, title, checked, date, ref) VALUES ("+user_id+" , '"+title+"' , 0, "+date+", "+ref+")");
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

    //Obtener el user de un itinerario
    public static int getUser(int route_id){
        int id = -1;
        Cursor cursor = bdw.rawQuery("SELECT * FROM route WHERE route_id = " + route_id, null);
        if (cursor.moveToNext()){

            id = cursor.getInt(cursor.getColumnIndex("user"));

        }
        return id;
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
            poi.setCategoria(0+cursor.getInt(cursor.getColumnIndex("categoria")));
            poi.setLocalidad(""+cursor.getString(cursor.getColumnIndex("localidad")));

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

    //Añadir la categoria de un poi
    public static boolean putCat(String identificador, int cat){
        boolean correcto = true;
        try {
            //SQLiteDatabase bdw = BaseDeDatos.getWritableDatabase();
            bdw.execSQL("UPDATE poi SET categoria = "+cat+" WHERE identificador = '"+identificador+"'");
        }
        catch (Exception e){
            correcto = false;
        }
        return correcto;
    }

    //Añadir la categoria de un poi
    public static boolean putLocalidad(String identificador, String localidad){
        boolean correcto = true;
        try {
            //SQLiteDatabase bdw = BaseDeDatos.getWritableDatabase();
            bdw.execSQL("UPDATE poi SET localidad = '"+localidad+"' WHERE identificador = '"+identificador+"'");
        }
        catch (Exception e){
            correcto = false;
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
    public static boolean addPoi(int route_id, int poi_id, boolean checked){
        boolean correcto = true;
        int position = getMaxPosition(route_id)+1;
        //Log.e("position: ",""+position+" ruta: "+route_id);
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

    //Elimina un poi de un itinerario
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

    //Obtener poi_id de poi_route_id
    public static int getPoiId(int id){
        int poiId = -1;
        Cursor cursor = bdw.rawQuery("SELECT * FROM route_pois WHERE route_pois_id = " + id, null);
        if (cursor.moveToNext()){

            poiId = cursor.getInt(cursor.getColumnIndex("poi"));

        }
        return poiId;
    }

    //Obtener route_id de poi_route_id
    public static int getRouteId(int id){
        int poiId = -1;
        Cursor cursor = bdw.rawQuery("SELECT * FROM route_pois WHERE route_pois_id = " + id, null);
        if (cursor.moveToNext()){

            poiId = cursor.getInt(cursor.getColumnIndex("route"));

        }
        return poiId;
    }

    //obtengo el usuario de una poiroute
    public static int getUserId(int id){
        int userId = -1;
        Cursor cursor = bdw.rawQuery("SELECT * FROM route_pois, route WHERE route = route_id AND route_pois_id = " + id, null);
        if (cursor.moveToNext()){

            userId = cursor.getInt(cursor.getColumnIndex("user"));

        }
        return userId;
    }

    //obtener la posicion maxima de un itinerario
    public static int getMaxPosition (int id){
        int max = -1;
        Cursor cursor = bdw.rawQuery("SELECT MAX(position) AS max_position FROM route_pois WHERE route = " + id, null);
        if (cursor.moveToNext()){

            max = cursor.getInt(cursor.getColumnIndex("max_position"));
           //Log.e("position: ",""+max+" ruta: "+id);

        }
        return max;
    }

    //Modificar la posicion de un elemento
    public static boolean swapPosition(int id, int position, int route_id){
        boolean correcto = true;

        Cursor c = bdw.rawQuery("SELECT * FROM route_pois WHERE route = " + route_id +" AND position >= "+position, null);

        //Nos aseguramos de que existe al menos un registro
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                try {
                    int pos = 1+c.getInt(c.getColumnIndex("position"));
                    int poi_id = 0 + c.getInt(c.getColumnIndex("route_pois_id"));


                    //SQLiteDatabase bdw = BaseDeDatos.getWritableDatabase();
                    bdw.execSQL("UPDATE route_pois SET position = "+pos+" WHERE route_pois_id = "+poi_id);

                }
                catch (Exception e){

                    correcto = false;
                }

            } while(c.moveToNext());
            bdw.execSQL("UPDATE route_pois SET position = "+position+" WHERE route_pois_id = "+id);
        }



        return correcto;
    }

    //obtiene los elementos de una ruta
    public static Ruta getRoute(int route_id){
        Ruta ruta = null;
        String waypoints="";
        boolean primer = true;

        Cursor c = bdw.rawQuery("SELECT route_pois_id AS _id, * FROM route_pois, route, poi " +
                "WHERE route = route_id AND poi_id = poi AND route_id = "+route_id+" ORDER BY position", null);
        if (c.moveToNext()) {
            ruta = new Ruta();
            ruta.setOrigen("place_id:"+c.getString(c.getColumnIndex("identificador")));
           // Log.e("origen"," "+ruta.getOrigen());
            for (int i = 1; i<c.getCount()-1;i++){
                c.moveToPosition(i);
                if(primer){
                    waypoints = "place_id:"+c.getString(c.getColumnIndex("identificador"));
                    primer=false;
                }else{
                    waypoints = waypoints+"|place_id:"+c.getString(c.getColumnIndex("identificador"));
                }
            }
            //Log.e("waypoints"," "+waypoints);
            ruta.setWaypoints(waypoints);

            c.moveToLast();
            ruta.setDestino("place_id:"+c.getString(c.getColumnIndex("identificador")));
            //Log.e("destino"," "+ruta.getDestino());
        }

        c.close();

        return ruta;
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
        return bdw.rawQuery("SELECT poi_comment_id AS _id, * FROM poi_comment, user " +
                "WHERE user_id = user AND poi = "+poi_id, null);
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
