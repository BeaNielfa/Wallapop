package com.example.wallapop;

import android.content.Intent;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.wallapop.ControladorBD.UtilSql;
import com.example.wallapop.Models.Sesion;
import com.example.wallapop.Rest.APIUtils;
import com.example.wallapop.Rest.SesionRest;
import com.example.wallapop.Utilidades.Utilidades;
import com.example.wallapop.ui.Login.LoginActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.File;
import java.util.Date;

public class Splah extends AppCompatActivity {

    private Sesion sesion;
    private SesionRest sesionRest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splah);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getSupportActionBar().hide();

        if (Utilidades.isOnline(getApplicationContext())) {//Si hay conexion al servicio
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    gestionarInicioApp();
                    finish();
                }
            }, 3000);//tiempo que debe estar ejecutandose

        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Debes tener una conexión a internet " +
                            "para empezar ", Toast.LENGTH_LONG).show();
                    finish();
                }
            }, 3000);//tiempo que debe estar ejecutandose
        }
    }

    /**
     * METODO QUE GESTIONA EL INICIO DE SESIÓN
     */
    private void gestionarInicioApp() {
        File bd = new File("/data/data/com.example.wallapop/databases/BDConfig");
        if (!bd.exists()) {//Si no existe la bbdd local, quiere decir que no tiene sesión y tiene que loguearse
            irLogin();
        } else {//si no
            sesion = UtilSql.consultarSesion(getApplicationContext());//consulta la sesion
            if (sesion == null) {
                irLogin();//se loguea
            } else {
                Date fechaActual = new Date();
                Date fechafin = Utilidades.parseFecha(sesion.getFechafin());
                int dias = (int) ((fechafin.getTime() - fechaActual.getTime()) / 86400000);

                if (dias < 0) {//si dias es menor a 0 la sesion acaba
                    UtilSql.eliminarSesionLocal(sesion.getId(), getApplicationContext());
                    eliminarSesion(sesion.getId());
                    irLogin();//y se va al login
                } else {//si no
                    irMain();//va directamente al wallapop
                }
            }
        }
    }

    /**
     * Metodo que abre la ventana del login
     */
    private void irLogin() {
        Intent intent = new Intent(com.example.wallapop.Splah.this, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * Metodo que abre la ventana del main
     */
    private void irMain() {
        Intent intent = new Intent(com.example.wallapop.Splah.this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * METODO QUE ELIMINA UNA SESION
     * @param id
     */
    private void eliminarSesion(int id) {
        sesionRest = APIUtils.getServiceSesiones();

        Call<Sesion> call = sesionRest.eliminarSesion(id);
        call.enqueue(new Callback<Sesion>() {
            @Override
            public void onResponse(Call<Sesion> call, Response<Sesion> response) {
                if (response.isSuccessful()) {
                    if (response.code() == 200) {
                        Toast.makeText(getApplicationContext(), " Sesión expirada",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Sesion> call, Throwable t) {

            }
        });
    }

}