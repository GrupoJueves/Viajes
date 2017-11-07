package org.example.viajes;

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
        db.execSQL("PRAGMA foreign_keys=ON;");

        db.execSQL("CREATE TABLE user("+
                "user_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "email TEXT NOT NULL UNIQUE," +
                "password TEXT NOT NULL," +
                "name TEXT," +
                "surname TEXT)");

        db.execSQL("CREATE TABLE route ( " +
                "route_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "title TEXT NOT NULL, " +
                "checked NUMERIC, " +
                "date NUMERIC, " +
                "FOREIGN KEY(user_id) REFERENCES user(user_id) ON DELETE CASCADE)");


        db.execSQL("CREATE TABLE poi ( " +
                "poi_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT NOT NULL, " +
                "description TEXT, " +
                "lon REAL NOT NULL, " +
                "lat REAL NOT NULL, " +
                "img TEXT)");



        db.execSQL("CREATE TABLE poi_pic ( " +
                "poi_pic_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "poi_id INTEGER NOT NULL, " +
                "user_id INTEGER NOT NULL, " +
                "path TEXT NOT NULL, " +
                "FOREIGN KEY(poi_id) " +
                "REFERENCES poi (poi_id) ON DELETE CASCADE, " +
                "FOREIGN KEY(user_id) REFERENCES user(id) ON DELETE CASCADE)");



        db.execSQL("CREATE TABLE route_pois ( " +
                "route_pois_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "route_id INTEGER NOT NULL, " +
                "poi_id INTEGER NOT NULL, " +
                "position INTEGER, " +
                "day INTEGER, " +
                "checked INTEGER, " +
                "FOREIGN KEY(route_id) REFERENCES route(route_id) ON DELETE CASCADE, " +
                "FOREIGN KEY(poi_id) REFERENCES poi(poi_id) ON DELETE CASCADE)");




        db.execSQL("CREATE TABLE poi_comment ( " +
                "poi_comment_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "poi_id INTEGER NOT NULL, " +
                "user_id INTEGER NOT NULL, " +
                "comment TEXT NOT NULL, " +
                "rating REAL, date NUMERIC, " +
                "FOREIGN KEY(user_id) REFERENCES user(user_id) ON DELETE CASCADE, " +
                "FOREIGN KEY(poi_id) REFERENCES poi(poi_id) ON DELETE CASCADE)");





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
        db.execSQL("PRAGMA foreign_keys=ON;");

    }
}
