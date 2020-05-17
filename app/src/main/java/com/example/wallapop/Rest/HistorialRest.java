package com.example.wallapop.Rest;

import com.example.wallapop.Models.Historial;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.Collection;
import java.util.List;

public interface HistorialRest {

    /**
     * METODO QUE INSERTA UNA COMPRA AL HISTORIAL DE UN USUARIO
     * @param historial
     * @return
     */
    @POST("historial/")
    Call<Historial> create(@Body Historial historial);

    /**
     * METODO QUE DEVUELVE UNA LISTA DEL HISTORIAL DE COMPRAS QUE HA REALIZADO UN USUARIO
     * @param usuario
     * @return
     */
    @GET("historial/{usuario}")
    Call<List<Historial>> consultar(@Path("usuario") String usuario);
}

