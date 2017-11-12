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
        db.execSQL("PRAGMA foreign_keys=1;");

        db.execSQL("CREATE TABLE user("+
                "user_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "email TEXT NOT NULL UNIQUE," +
                "password TEXT NOT NULL," +
                "name TEXT," +
                "surname TEXT)");

        db.execSQL("INSERT INTO user VALUES (null, 'grupojueves5@gmail.com', 'android1', 'Jueves', '5 de la tarde')");

        db.execSQL("CREATE TABLE route ( " +
                "route_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "user INTEGER NOT NULL, " +
                "title TEXT NOT NULL, " +
                "checked NUMERIC, " +
                "date NUMERIC, " +
                "FOREIGN KEY(user) REFERENCES user(user_id) ON DELETE CASCADE)");

        db.execSQL("INSERT INTO route VALUES (null, 1, 'primera ruta', 0, null)");
        db.execSQL("INSERT INTO route VALUES (null, 1, 'segunda ruta', 0, null)");
        db.execSQL("INSERT INTO route VALUES (null, 1, 'tercera ruta', 1, null)");
        db.execSQL("INSERT INTO route VALUES (null, 2, 'ruta segundo usuario', 0, null)");


        db.execSQL("CREATE TABLE poi ( " +
                "poi_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT NOT NULL, " +
                "description TEXT, " +
                "lon REAL NOT NULL, " +
                "lat REAL NOT NULL, " +
                "img TEXT)");

        db.execSQL("INSERT INTO poi VALUES (null, 'Santiago Bernabeu', 'El Estadio Santiago Bernabéu es un recinto deportivo propiedad del Real Madrid Club de Fútbol, situado en pleno Paseo de la Castellana, en el distrito de Chamartín de Madrid, España', -3.689452, 40.453113, null)");
        db.execSQL("INSERT INTO poi VALUES (null, 'Puerta de Alcala', 'La Puerta de Alcalá es una de las cinco antiguas puertas reales que daban acceso a la ciudad de Madrid. Se encuentra situada en el centro de la rotonda de la Plaza de la Independencia', -3.688658, 40.419918, null)");
        db.execSQL("INSERT INTO poi VALUES (null, 'Palacio Real', 'Palacio del s. XVIII con balaustrada que ofrece visitas guiadas por salones repletos de arte y antigüedades.', -3.713666, 40.418150, null)");
        db.execSQL("INSERT INTO poi VALUES (null, 'Teatro Real', 'Detalles dorados, terciopelo rojo y obras de arte en un majestuoso teatro centenario de danza clásica y ópera.', -3.710555, 40.418127, null)");
        db.execSQL("INSERT INTO poi VALUES (null, 'Parroquia de san gines', 'La iglesia de San Ginés es un templo católico bajo la advocación de San Ginés de Arlés, notario mártir, y sede de la parroquia de San Ginés de la Villa de Madrid', -3.707132, 40.416873, null)");
        db.execSQL("INSERT INTO poi VALUES (null, 'Chocolateria San Gines', 'Casa fundada en 1894 y abierta las 24 horas conocida por su chocolate con churros y su concurrida terraza', -3.706907, 40.416675, null)");


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

        db.execSQL("INSERT INTO route_pois VALUES (null,1,2,1,1,1)");
        db.execSQL("INSERT INTO route_pois VALUES (null,1,1,2,1,0)");
        db.execSQL("INSERT INTO route_pois VALUES (null,1,5,3,1,0)");
        db.execSQL("INSERT INTO route_pois VALUES (null,1,4,4,1,0)");
        db.execSQL("INSERT INTO route_pois VALUES (null,2,3,1,1,1)");




        db.execSQL("CREATE TABLE poi_comment ( " +
                "poi_comment_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "poi INTEGER NOT NULL, " +
                "user INTEGER NOT NULL, " +
                "comment TEXT NOT NULL, " +
                "rating REAL, date NUMERIC, " +
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
