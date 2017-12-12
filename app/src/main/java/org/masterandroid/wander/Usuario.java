package org.masterandroid.wander;

/**
 * Created by rodriii on 6/12/17.
 */

public class Usuario {
    String correo, nombre, apellidos, lugar,pais,  username, photo, web;
    int  telefono,edad, ref;

    public Usuario(){
        edad = 0;
        telefono = 0;
        lugar = "";
    }

    public Usuario(String correo, String nombre, String apellidos, int telefono, int edad, String lugar) {
        this.correo = correo;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.edad = edad;
        this.lugar = lugar;
    }

    public Usuario(String correo, String nombre, String apellidos, String lugar, String pais, String username, String photo, String web, int telefono, int edad, int ref) {
        this.correo = correo;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.lugar = lugar;
        this.pais = pais;
        this.username = username;
        this.photo = photo;
        this.web = web;
        this.telefono = telefono;
        this.edad = edad;
        this.ref = ref;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public int getTelefono() {
        return telefono;
    }

    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public int getRef() {
        return ref;
    }

    public void setRef(int ref) {
        this.ref = ref;
    }
}
