package com.example.wallapop.Rest;

import com.example.wallapop.Models.Favoritos;
import com.example.wallapop.Models.Historial;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.List;

public interface FavoritosRest {


    /**
     * METODO QUE INSERTA UN FAVORITO
     * @param favoritos
     * @return
     */
    @POST("favoritos/")
    Call<Favoritos> create(@Body Favoritos favoritos);

    /**
     * METODO QUE DEVUELVE UNA LISTA DE FAVORITOS DE UN USUARIO
     * @param usuario
     * @return
     */
    @GET("favoritos/{usuario}")
    Call<List<Favoritos>> consultar(@Path("usuario") String usuario);


    /**
     * METODO QUE BUSCA POR VENDEDOR Y USUSARIO
     * @param vendedor
     * @param usuario
     * @return
     */
    @GET("favoritos/{vendedor}/{usuario}")
    Call<Favoritos> buscarPorVendedorUsuario(@Path("vendedor") String vendedor, @Path("usuario") String usuario);
}
