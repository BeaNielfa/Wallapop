package com.example.wallapop.Utilidades;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utilidades {

    /**
     * Para generar el token del usuario
     * @param nombreUsuario
     * @return
     */
    public static String generarToken(String nombreUsuario){
        String cadena = nombreUsuario;
        String token = convertPassMd5(cadena);
        return token;
    }

    /**
     * Devuelve las fechas de inicio y fin del token
     * @return
     */

    public static String[] gestionFechas(){
        String[] fechas = new String[2];
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fechaComoCadena = sdf.format(new Date());
        fechas[0] = fechaComoCadena;
        fechas[1] = fechaLimite();
        return fechas;
    }

    /**
     * Generamos la fecha de caducidad sumándole 7 días a la de inicio
     * @return
     */
    private static String fechaLimite(){
        String fechaLimite;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_YEAR, 7);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        fechaLimite= sdf.format(calendar.getTime());
        return fechaLimite;
    }


    /**
     * METODO QUE COMPRUEBA SI HAY CONEXION CON EL SERVICIO
     * @param context
     * @return
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
    }

    public static Date parseFecha(String fecha) {
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        Date fechaDate = null;
        try {
            fechaDate = formato.parse(fecha);
        } catch (ParseException ex) {
            System.out.println(ex);
        }
        return fechaDate;
    }

    /**
     * METODO QUE CIFRA
     * @param pass
     * @return
     */
    public static String convertPassMd5(String pass) {
        String password = null;
        MessageDigest mdEnc;
        try {
            mdEnc = MessageDigest.getInstance("MD5");
            mdEnc.update(pass.getBytes(), 0, pass.length());
            pass = new BigInteger(1, mdEnc.digest()).toString(16);

            while (pass.length() < 32) {
                pass = "0" + pass;
            }

            password = pass;
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        return password;
    }


    /**
     * PASAMOS LA IMAGEN A BASE64 Y LUEGO A BITMAP PARA PODER MOSTRARLO EN EL IMAGEVIEW
     * @param b64
     * @return
     */
    public static Bitmap base64ToBitmap(String b64) {
        byte[] imageAsBytes = Base64.decode(b64.getBytes(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }

    /**
     * Nos quedamos solo con dos decimales del precio
     *
     * @param precio
     * @return
     */
    public static String gestionarPrecioDosDecimales(String precio) {
        String precioFinal;
        int posicion = -1;
        boolean encontrado = false;

        if (precio.length() > 5) {
            for (int i = 0; i < precio.length() && !encontrado; i++) {
                if (precio.charAt(i) == '.') {
                    posicion = i;
                    encontrado = true;
                }
            }
            precioFinal = precio.substring(0, (posicion + 3));

        } else {
            precioFinal = precio;
        }
        return precioFinal;
    }



}
