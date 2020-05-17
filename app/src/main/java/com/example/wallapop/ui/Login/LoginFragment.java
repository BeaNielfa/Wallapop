package com.example.wallapop.ui.Login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.FragmentTransaction;
import com.example.wallapop.ControladorBD.UtilSql;
import com.example.wallapop.MainActivity;
import com.example.wallapop.Models.Sesion;
import com.example.wallapop.Models.Usuario;
import com.example.wallapop.R;
import com.example.wallapop.Rest.APIUtils;
import com.example.wallapop.Rest.SesionRest;
import com.example.wallapop.Rest.UsuarioRest;
import com.example.wallapop.Utilidades.Utilidades;
import com.example.wallapop.ui.Productos.MapaFragment;
import com.google.android.material.snackbar.Snackbar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    //VARIABLES
    EditText usuario, contraseña;
    private UsuarioRest usuarioRest;
    private SesionRest sesionRest;
    View view;
    TextView registrar,entrar;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_login, container, false);

        usuario = (EditText) view.findViewById(R.id.etRegistrarUsuario);
        contraseña = (EditText) view.findViewById(R.id.etContraseña);
        registrar = (TextView) view.findViewById(R.id.tvRegistrarse);
        entrar = (TextView) view.findViewById(R.id.btnEntrar);



        registrar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //LLAMAMOS A REGISTRAR
                RegistrarFragment mapa = new RegistrarFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment, mapa);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        entrar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String user = usuario.getText().toString();
                String password = contraseña.getText().toString();

                if (user.isEmpty() || password.isEmpty()){
                    Toast.makeText(getContext(), "Debes rellenar todos los campos", Toast.LENGTH_SHORT).show();
                }else {
                    //Cuándo pinchamos en el botón de entrar, ciframos la contraseña
                    String cifrada = Utilidades.convertPassMd5(contraseña.getText().toString());
                    comprobarUsuario(usuario.getText().toString(), cifrada);//y comprobamos el usuario
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

            //Para que se conecte con el servicio
            usuarioRest = APIUtils.getService();
            sesionRest = APIUtils.getServiceSesiones();


    }

    /**
     * METODO EN EL QUE COMPROBAMOS SI EL USUARIO INTRODUCIDO EXISTE
     * @param correo
     * @param password
     */
    private void comprobarUsuario(String correo, String password){


        Call<Usuario> call = usuarioRest.buscarPorUsuarioPass(correo,password);

        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful()){

                    if (response.code() == 200){
                        Usuario usuario = response.body();
                        //INSERTAMOS EL USUARIO EN LA BBDD LOCAL
                        UtilSql.insertarUsuarioLocal(usuario,getContext());

                        String[] fechas = Utilidades.gestionFechas();

                        Sesion sesion = new Sesion (usuario.getId(), Utilidades.generarToken(usuario.getUsuario()),fechas[0],fechas[1]);

                        Call<Sesion> call2 = sesionRest.nuevaSesion(sesion);//CREAMOS UNA NUEVA SESION
                        call2.enqueue(new Callback<Sesion>() {
                            @Override
                            public void onResponse(Call<Sesion> call, Response<Sesion> response) {
                                if (response.isSuccessful()){
                                    if (response.code() == 200){
                                        //INSERTAMOS LA SESION EN LA BBDD LOCAL
                                        UtilSql.insertarSesionLocal(response.body(),getContext());
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<Sesion> call, Throwable t) {

                            }
                        });
                        //UNA VEZ HA INICIADO SESIÓN ABRIMOS EL WALLAPOP
                        Intent i = new Intent(getActivity(),MainActivity.class);
                        startActivity(i);
                        getActivity().finish();
                    }else{
                        Snackbar.make(getView(), "NO REGISTRADO", Snackbar.LENGTH_LONG).show();

                    }
                }else {
                    Toast.makeText(getContext(), "No se obtuvo respuesta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(getContext(), "Servicio no activo", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
