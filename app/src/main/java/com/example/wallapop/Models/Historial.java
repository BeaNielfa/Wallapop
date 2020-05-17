package com.example.wallapop.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Historial {

    @SerializedName("id")
    @Expose
    private long id;

    @SerializedName("producto")
    @Expose
    private long producto;

    @SerializedName("usuario")
    @Expose
    private String usuario;



    @SerializedName("fecha")
    @Expose
    private String fecha;

    @SerializedName("descripcion")
    @Expose
    private String descripcion;

    @SerializedName("precio")
    @Expose
    private float precio;

    @SerializedName("imagen")
    @Expose
    private String imagen;

    @SerializedName("nombre")
    @Expose
    private String nombre;

    public Historial(){

    }

    public Historial(long id, long producto, String usuario, String fecha, String descripcion, float precio, String imagen, String nombre) {
        this.id = id;
        this.producto = producto;
        this.usuario = usuario;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.precio = precio;
        this.imagen = imagen;
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getProducto() {
        return producto;
    }

    public void setProducto(long producto) {
        this.producto = producto;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
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

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}

