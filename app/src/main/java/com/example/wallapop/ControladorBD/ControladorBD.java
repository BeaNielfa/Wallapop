package com.example.wallapop.ControladorBD;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;


public class ControladorBD extends SQLiteOpenHelper {


    //TABLAS USUARIO Y SESION

    private final static String CREATE_TABLA_USUARIOS = "CREATE TABLE Usuarios (idUsuario INTEGER PRIMARY KEY AUTOINCREMENT, usuario VARCHAR, "+
            " email VARCHAR, contraseña VARCHAR)";


    private final static String CREATE_TABLA_SESION = "CREATE TABLE Sesiones (idSesion INTEGER PRIMARY KEY AUTOINCREMENT,idUsuario INTEGER, token VARCHAR, "+
            " fechaInicio VARCHAR, fechaFin VARCHAR)";

    public ControladorBD(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Cuando se llame al onCreate se realiza la sentencia
        db.execSQL(CREATE_TABLA_USUARIOS);
        db.execSQL(CREATE_TABLA_SESION);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Se borra y se vuelve a crear para "actualizar" la versión de la tabla
        db.execSQL("DROP TABLE IF EXISTS Sesiones");
        db.execSQL("DROP TABLE IF EXISTS Usuarios");

        //Se crea la nueva versión de la tabla
        onCreate(db);
    }

}
