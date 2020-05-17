package com.example.wallapop.ui.Login;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.FragmentTransaction;
import com.example.wallapop.Models.Usuario;
import com.example.wallapop.R;
import com.example.wallapop.Rest.APIUtils;
import com.example.wallapop.Rest.UsuarioRest;
import com.example.wallapop.Utilidades.Utilidades;
import com.google.android.material.snackbar.Snackbar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegistrarFragment extends Fragment {

    //VARIABLES
    EditText etUsuario, etEmail, etContraseña;
    Button btnAceptar;
    UsuarioRest usuarioRest;
    View view;
    String  user, email, contraseña;

    public RegistrarFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_registrar, container, false);

        etUsuario = (EditText) view.findViewById(R.id.etRegistrarUsuario);
        etEmail =(EditText)view.findViewById(R.id.etRegistrarEmail);
        etContraseña =(EditText)view.findViewById(R.id.etRegistrarContraseña);
        btnAceptar =(Button) view.findViewById(R.id.btnRegistrarAceptar);

        //para que se comunique
        usuarioRest = APIUtils.getService();

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Usuario usuario = new Usuario() ;

                //recogemos los datos introducidos por el usuario
                user = etUsuario.getText().toString();
                email = etEmail.getText().toString();
                contraseña = etContraseña.getText().toString();

                //se lo asigamos al usuario
                usuario.setUsuario(user);
                usuario.setEmail(email);
                String cifrada = Utilidades.convertPassMd5(contraseña);
                usuario.setContraseña(cifrada);//le pasamos la contraseña cifrada


                guardarCambios(usuario);



            }
        });

        return view;
    }


    private void guardarCambios(Usuario usuario){

        //Comprobamos si existe un usuario con el mismo nombre de usuario que quiere registrar
        Call<Usuario> call = usuarioRest.buscarPorNombreUsuario(usuario.getUsuario());
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {

                if (response.isSuccessful()) {//Si encuentra un usuario con el mismo nombre, EL USUARIO YA EXISTE
                    if (response.code() == 200) {
                        Snackbar.make(getView(), "El usuario ya existe", Snackbar.LENGTH_LONG).show();
                    }else{//si no, procede a registrar el usuario
                        Call<Usuario> callInsert  = usuarioRest.create(usuario);
                        callInsert.enqueue(new Callback<Usuario>() {
                            @Override
                            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                                if(response.isSuccessful()){
                                    Snackbar.make(getView(), "Usuario registrado correctamente", Snackbar.LENGTH_LONG).show();
                                    volverLogin();//Una vez registrado, volvemos al login para que se pueda loguear
                                }
                            }

                            @Override
                            public void onFailure(Call<Usuario> call, Throwable t) {
                                Toast.makeText(getContext(), "No se obtuvo respuesta", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(getContext(), "El servicio no está disponible", Toast.LENGTH_SHORT).show();
            }
        });


    }

    /**
     * METODO QUE VUELVE A LA VENTANA DEL LOGIN
     */
    public void volverLogin(){
        LoginFragment mapa = new LoginFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment, mapa);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
