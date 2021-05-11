package com.example.taller3;

public class UserPojo {
    private String nombre;
    private String apellido;
    private String email;
    private String numeroIdentificacion;
    private String uid;
    private double latitud;
    private double longitud;
    private int disponible;

    @Override
    public String toString() {
        return "Nombre: " + nombre + "," +
               "Apellido: " + apellido + "," +
               "Email: " + email + "," +
               "Numero identificacion: " + numeroIdentificacion + "," +
               "UID: " + uid + "," +
               "Latitud: " + latitud + "," +
               "Longitud: " + longitud + "," +
               "Disponible: " + disponible + ",";
    }

    public UserPojo() {
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setNumeroIdentificacion(String numeroIdentificacion) {
        this.numeroIdentificacion = numeroIdentificacion;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public void setDisponible(int disponible) {
        this.disponible = disponible;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getEmail() {
        return email;
    }

    public String getNumeroIdentificacion() {
        return numeroIdentificacion;
    }

    public String getUid() {
        return uid;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public int getDisponible() {
        return disponible;
    }
}
