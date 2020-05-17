package com.example.wallapop.Rest;



public class APIUtils {

    // IP del servidor
    private static final String server = "192.168.43.107";
    // Puerto del microservicio
    private static final String port = "8080";
    //Servicio, si usamos otro punto de partida, pero lo hemos definido en el ProuctoRest
    private static final String servicio = "usuarios";
    // IP del servicio
    public static final String API_URL = "http://"+server+":"+port+"/";

    private APIUtils() {
    }

    // Constructor del servicio con los elementos de la interfaz
    public static UsuarioRest getService() {
        return RetrofitClient.getClient(API_URL).create(UsuarioRest.class);
    }

    public static ProductosRest getServiceProductos(){
        return RetrofitClient.getClient(API_URL).create(ProductosRest.class);
    }

    public static HistorialRest getServiceHistorial(){
        return RetrofitClient.getClient(API_URL).create(HistorialRest.class);
    }

    public static SesionRest getServiceSesiones()
    {
        return RetrofitClient.getClient(API_URL).create(SesionRest.class);
    }

    public static FavoritosRest getServiceFavoritos()
    {
        return RetrofitClient.getClient(API_URL).create(FavoritosRest.class);
    }

}