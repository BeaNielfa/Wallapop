package com.example.wallapop.Models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Favoritos {

    @SerializedName("id")
    @Expose
    long id;

    @SerializedName("vendedor")
    @Expose
    String vendedor;

    @SerializedName("usuario")
    @Expose
    String usuario;

    @SerializedName("email")
    @Expose
    String email;
    public Favoritos(){

    }

    public Favoritos( String vendedor, String usuario, String email) {
        this.vendedor = vendedor;
        this.usuario = usuario;
        this.email = email;

    }

    public String getVendedor() {
        return vendedor;
    }

    public void setVendedor(String vendedor) {
        this.vendedor = vendedor;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
