package com.example.wallapop.ControladorBD;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.wallapop.Models.Sesion;
import com.example.wallapop.Models.Usuario;

public class UtilSql {
    /**
     * NOMBRE QUE SE LE DA A LA BASE DE DATOS LOCAL (SQLITE)
     */
    private static final String NOMBRE_BD = "BDConfig";
    private static final int VERSION_BD = 1;

    /**
     * METODO QUE CONSULTA SESION
     * @param context
     * @return
     */
    public static Sesion consultarSesion(Context context) {
        Sesion aux = null;
        ControladorBD controlador = new ControladorBD(context, NOMBRE_BD, null, VERSION_BD);
        SQLiteDatabase bd = controlador.getReadableDatabase();
        String[] campos = new String[]{"idSesion", "idUsuario", "token", "fechaInicio", "fechaFin"};
        Cursor c = bd.query("SESIONES", campos, null, null, null, null, null);
        if (c.moveToFirst()) {
            aux = new Sesion(c.getLong(1), c.getString(2), c.getString(3), c.getString(4));
            aux.setId(c.getInt(0));
        }
        bd.close();
        controlador.close();
        return aux;
    }

    /**
     * METODO QUE CONSULTA UN USUARIO
     * @param context
     * @return
     */
    public static Usuario consultarUsuarioLocal(Context context) {
        Usuario aux = null;
        ControladorBD controlador = new ControladorBD(context, NOMBRE_BD, null, VERSION_BD);
        SQLiteDatabase bd = controlador.getReadableDatabase();
        String[] campos = new String[]{"idUsuario", "usuario", "email", "contraseña"};
        Cursor c = bd.query("USUARIOS", campos, null, null, null, null, null);
        if (c.moveToFirst()) {
            aux = new Usuario(c.getString(1), c.getString(2),
                    c.getString(3));
            aux.setId(c.getLong(0));
        }
        bd.close();
        controlador.close();
        return aux;
    }

    /**
     * METODO QUE INSERTA UN USUARIO EN LA BBDD LOCAL
     * @param u
     * @param context
     */
    public static void insertarUsuarioLocal(Usuario u, Context context) {
        ControladorBD controlador = new ControladorBD(context, NOMBRE_BD, null, VERSION_BD);
        SQLiteDatabase bd = controlador.getWritableDatabase();
        ContentValues contenido = new ContentValues();
        contenido.put("idUsuario", u.getId());
        contenido.put("usuario", u.getUsuario());
        contenido.put("email", u.getEmail());
        contenido.put("contraseña", u.getContraseña());
        bd.insert("USUARIOS", null, contenido);
        bd.close();
        controlador.close();
    }

    /**
     * METODO QUE INSERTA UNA SESION EN LA BBDD LOCAL
     * @param s
     * @param context
     */
    public static void insertarSesionLocal(Sesion s, Context context) {
        ControladorBD controlador = new ControladorBD(context, NOMBRE_BD, null, VERSION_BD);
        SQLiteDatabase bd = controlador.getWritableDatabase();
        ContentValues contenido = new ContentValues();
        contenido.put("idSesion", s.getId());
        contenido.put("idUsuario", s.getIdusuario());
        contenido.put("token", s.getToken());
        contenido.put("fechaInicio", s.getFechainicio());
        contenido.put("fechaFin", s.getFechafin());
        bd.insert("SESIONES", null, contenido);
        bd.close();
        controlador.close();
    }

    /**
     * METODO QUE ELIMINA UNA SESION DE LA BBDD LOCAL
     * @param idSesion
     * @param context
     */
    public static void eliminarSesionLocal(int idSesion, Context context) {
        ControladorBD controlador = new ControladorBD(context, NOMBRE_BD, null, VERSION_BD);
        SQLiteDatabase bd = controlador.getWritableDatabase();
        bd.delete("SESIONES", "idSesion=" + idSesion, null);
        bd.close();
        controlador.close();
    }

}