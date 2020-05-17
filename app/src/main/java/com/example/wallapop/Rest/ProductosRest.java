package com.example.wallapop.Rest;

import com.example.wallapop.Models.Producto;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface ProductosRest {


    /**
     * METODO QUE DEVUELVE LA LISTA DEL CATALOGO SIN MOSTRAR LOS PRODUCTOS QUE A SUBIDO EL PROPIO USUARIO
     * @param nombre
     * @return
     */
    @GET("productos/catalogo/{vendedor}")
    Call<List<Producto>> buscarCatalogo(@Path("vendedor") String nombre);

    /**
     * METODO QUE DEVUELVE UNA LISTA DE PRODUCTOS EN EL CATALOGO SIN MOSTRAR LOS PRODUCTOS QUE SUBE EL PROPIO USUARIO
     * Y QUE EMPIEZAN POR LO QUE INTRODUZCA EN EL FILTRO DE NOMBRE Y EL PRECIO ES IGUAL O MENOR A LO INTRODUCIDO
     * @param nombre
     * @param precio
     * @param vendedor
     * @return
     */
    @GET("productos/catalogo/filtro/{nombre}/{precio}/{vendedor}")
    Call<List<Producto>> filtroCompleto(@Path("vendedor") String vendedor, @Path("nombre") String nombre, @Path("precio") float precio);


    /**
     * METODO QUE DEVUELVE UNA LISTA DE PRODUCTOS EN EL CATALOGO SIN MOSTRAR LOS PRODUCTOS QUE SUBE EL PROPIO USUARIO
     * Y EL PRECIO ES IGUAL O MENOR A LO INTRODUCIDO
     * @param precio
     * @param vendedor
     * @return
     */
    @GET("productos/catalogo/filtro/{vendedor}/{precio}")
    Call<List<Producto>> filtroPrecio( @Path("vendedor") String vendedor, @Path("precio") float precio);

    /**
     * METODO QUE DEVUELVE UNA LISTA DE PRODUCTOS EN "MIS PRODUCTOS"
     * SE MUESTRAN LOS PRODUCTOS QUE HA SUBIDO EL USUARIO LOGUEADO QUE NO HAN SIDO COMPRADOS
     * @param nombre
     * @return
     */
    @GET("productos/ver/{vendedor}")
    Call<List<Producto>> buscar(@Path("vendedor") String nombre);

    /**
     * METODO QUE INSERTA UN NUEVO PRODUCTO
     * @param producto
     * @return
     */
    @POST("productos/")
    Call<Producto> insertar(@Body Producto producto);

    /**
     * METODO QUE ELIMINA UN PRODUCTO
     * @param id
     * @return
     */
    @DELETE("productos/{id}")
    Call<Producto> eliminarProducto(@Path("id") Long id);

    /**
     * METODO QUE ACTUALIZA UN PRODUCTO
     * @param id
     * @param producto
     * @return
     */
    @PUT("productos/{id}")
    Call<Producto> modificarProducto(@Path("id") Long id,@Body Producto producto);

    /**
     * METODO QUE ACTUALIZA EL ESTADO DE UN PRODUCTO
     * @param id
     * @param producto
     * @return
     */
    @PUT("productos/estado/{id}")
    Call<Producto> modificarEstadoProducto(@Path("id") Long id,@Body Producto producto);
}
