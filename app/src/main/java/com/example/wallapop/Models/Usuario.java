package com.example.wallapop.Models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Usuario  implements Serializable {

    @SerializedName("id")
    @Expose
    private long id;

    @SerializedName("usuario")
    @Expose
    private String usuario;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("contraseña")
    @Expose
    private String contraseña;


    public Usuario(){

    }

    public Usuario(String usuario, String email, String contraseña, long id) {
        this.usuario = usuario;
        this.email = email;
        this.contraseña = contraseña;
        this.id = id;
    }

    public Usuario(String usuario, String email, String contraseña) {
        this.usuario = usuario;
        this.email = email;
        this.contraseña = contraseña;
        this.id = id;
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

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
