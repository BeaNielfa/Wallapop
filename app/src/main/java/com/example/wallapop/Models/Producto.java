package com.example.wallapop.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Producto {

    @SerializedName("id")
    @Expose
    private long id;

    @SerializedName("nombre")
    @Expose
    private String Nombre;

    @SerializedName("descripcion")
    @Expose
    private String descripcion;

    @SerializedName("precio")
    @Expose
    private float precio;

    @SerializedName("imagen")
    @Expose
    private String imagen;

    @SerializedName("fecha")
    @Expose
    private String fecha;


    @SerializedName("vendedor")
    @Expose
    private String vendedor;

    @SerializedName("estado")
    @Expose
    private int estado;

    @SerializedName("latitud")
    @Expose
    private double latitud;

    @SerializedName("longitud")
    @Expose
    private double longitud;
    public Producto() {

    }

    public Producto(long id, String nombre, String descripcion, float precio, String imagen, String fecha, String vendedor, int estado, double latitud, double longitud) {
        this.id = id;
        Nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.imagen = imagen;
        this.fecha = fecha;
        this.vendedor = vendedor;
        this.estado = estado;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getVendedor() {
        return vendedor;
    }

    public void setVendedor(String vendedor) {
        this.vendedor = vendedor;
    }

    public long getId() {
        return id;
    }

    public void setId(long idProducto) {
        this.id = idProducto;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String img) {
        this.imagen = img;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

}

