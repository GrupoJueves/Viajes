package org.masterandroid.wander;

/**
 * Created by Ivan on 13/12/2017.
 */

public class Ruta {
    String origen, destino, waypoints;

    public Ruta() {
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(String waypoints) {
        this.waypoints = waypoints;
    }
}
