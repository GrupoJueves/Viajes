package org.masterandroid.wander;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ivan on 07/11/2017.
 */

public class BaseDeDatos extends SQLiteOpenHelper {

    public BaseDeDatos(Context context){

        super(context, "viajes", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("PRAGMA foreign_keys=1;");

        db.execSQL("CREATE TABLE user("+
                "user_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "email TEXT NOT NULL UNIQUE," +
                "password TEXT NOT NULL," +
                "name TEXT," +
                "surname TEXT)");

        db.execSQL("CREATE TABLE user_data("+
                "user_data_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "user INTEGER NOT NULL," +
                "photo TEXT ," + //Foto del usuario (en teoria de su cara)
                "localidad TEXT," +
                "telefono INTEGER," +
                " edad INTEGER, " +
                "pais TEXT, " +
                "username TEXT, " + //nombre de usuario, puede ser distinto del nombre real
                "web TEXT, " + //pagina personal, si existe
                "referencia INTEGER, " + //Para poder poner imagenes de referencia como en el itinerario
                "FOREIGN KEY(user) REFERENCES user(user_id) ON DELETE CASCADE )");



        db.execSQL("CREATE TABLE route ( " +
                "route_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "user INTEGER NOT NULL, " +
                "title TEXT NOT NULL, " +
                "checked NUMERIC, " +
                "date NUMERIC, " +
                "ref INTEGER,  " +
                "FOREIGN KEY(user) REFERENCES user(user_id) ON DELETE CASCADE)");




        db.execSQL("CREATE TABLE poi ( " +
                "poi_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT NOT NULL, " +
                "identificador TEXT NOT NULL UNIQUE, " +
                "lon REAL NOT NULL, " +
                "lat REAL NOT NULL, " +
                "img TEXT, " +
                "categoria INTEGER)");



        db.execSQL("CREATE TABLE poi_pic ( " +
                "poi_pic_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "poi INTEGER NOT NULL, " +
                "user INTEGER NOT NULL, " +
                "path TEXT NOT NULL, " +
                "FOREIGN KEY(poi) REFERENCES poi (poi_id) ON DELETE CASCADE, " +
                "FOREIGN KEY(user) REFERENCES user(id) ON DELETE CASCADE)");



        db.execSQL("CREATE TABLE route_pois ( " +
                "route_pois_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "route INTEGER NOT NULL, " +
                "poi INTEGER NOT NULL, " +
                "position INTEGER, " +
                "day INTEGER, " +
                "visto INTEGER, " +
                "FOREIGN KEY(route) REFERENCES route(route_id) ON DELETE CASCADE, " +
                "FOREIGN KEY(poi) REFERENCES poi(poi_id) ON DELETE CASCADE)");






        db.execSQL("CREATE TABLE poi_comment ( " +
                "poi_comment_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "poi INTEGER NOT NULL, " +
                "user INTEGER NOT NULL, " +
                "comment TEXT NOT NULL, " +
                "rating REAL, " +
                "date NUMERIC, " +
                "FOREIGN KEY(user) REFERENCES user(user_id) ON DELETE CASCADE, " +
                "FOREIGN KEY(poi) REFERENCES poi(poi_id) ON DELETE CASCADE)");







    }

    @Override
    public void onUpgrade(SQLiteDatabase db,int OldVersion, int newVersion){
        //codigo para futuras actualizaciones
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        //if (!db.isReadOnly()) {
        // Enable foreign key constraints
        db.execSQL("PRAGMA foreign_keys=1;");

    }
}
