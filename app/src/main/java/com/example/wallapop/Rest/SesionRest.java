package com.example.wallapop.Rest;


import com.example.wallapop.Models.Sesion;
import retrofit2.Call;
import retrofit2.http.*;

public interface SesionRest {

    /**
     * METODO QUE INSERTA UNA NUEVA SESION
     * @param sesion
     * @return
     */
    @POST("sesiones/nueva")
    Call<Sesion> nuevaSesion(@Body Sesion sesion);

    /**
     * METODO QUE ELIMINA UNA SESION
     * @param id
     * @return
     */
    @DELETE("sesiones/cerrar/{id}")
    Call<Sesion> eliminarSesion(@Path("id") Integer id);


}
