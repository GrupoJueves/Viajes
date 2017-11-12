package org.example.viajes;

/**
 * Created by Ivan on 11/11/2017.
 */

public class POI {
    String title, description, img;
    Float lon, lat;

    public POI() {
        img = null;
        description = null;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        title = title.replace("\'","\'\'");  //para evitar problemas con la base de datos
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        description = description.replace("\'","\'\'");
        this.description = description;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        img = img.replace("\'","\'\'");
        this.img = img;
    }

    public float getLon() {
        return lon;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }
}
