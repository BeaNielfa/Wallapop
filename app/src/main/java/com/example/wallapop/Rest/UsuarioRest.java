package com.example.wallapop.Rest;

import com.example.wallapop.Models.Usuario;
import retrofit2.http.GET;

import retrofit2.*;
import retrofit2.http.*;


public interface UsuarioRest {

    /**
     * METODO QUE INSERTA UN NUEVO USUARIO
     * @param usuario
     * @return
     */
    @POST("usuarios/")
    Call<Usuario> create(@Body Usuario usuario);

    /**
     * METODO QUE BUSCA UN USUARIO POR EL NOMBRE
     * @param nombre
     * @return
     */
    @GET("usuarios/registro/{usuario}")
    Call<Usuario> buscarPorNombreUsuario(@Path("usuario") String nombre);


    /**
     * METODO QUE BUSCA UN USUARIO POR EL NOMBRE DE USUARIO Y CONTRASEÑA
     * @param correo
     * @param password
     * @return
     */
    @GET("usuarios/{usuario}/{contrasena}")
    Call<Usuario> buscarPorUsuarioPass(@Path("usuario") String correo, @Path("contrasena") String password);

}


