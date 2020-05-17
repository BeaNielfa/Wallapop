package com.example.wallapop.ui.Productos;


import android.graphics.Bitmap;
import android.os.Bundle;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.FragmentTransaction;
import com.example.wallapop.MainActivity;
import com.example.wallapop.Models.Favoritos;
import com.example.wallapop.Models.Historial;
import com.example.wallapop.Models.Producto;
import com.example.wallapop.Models.Usuario;
import com.example.wallapop.R;
import com.example.wallapop.Rest.APIUtils;
import com.example.wallapop.Rest.FavoritosRest;
import com.example.wallapop.Rest.UsuarioRest;
import com.example.wallapop.Utilidades.Utilidades;
import com.google.android.material.snackbar.Snackbar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetalleProducto extends Fragment {

    //variables
    private Producto producto;
    private TextView etNombre, etDescripcion, etPrecio, etFecha, etVendedor;
    private ImageView imgProducto;
    private View v;
    Button ubicacion, favoritos;
    String visualizacion;
    FavoritosRest favoritosRest;
    UsuarioRest usuarioRest;

    public DetalleProducto() {
        // Required empty public constructor

    }



    public DetalleProducto(Producto producto)
    {
        visualizacion="detalle";
        this.producto = producto;
    }


    public DetalleProducto(Producto producto, String visualizacion)
    {
        this.visualizacion = visualizacion;
        this.producto = producto;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_detalle_producto, container, false);

        //enlazamos los valores con la interfaz
        etNombre = (TextView)  v.findViewById(R.id.tvDetalleNombre);
        etDescripcion = (TextView)  v.findViewById(R.id.etInsertarDescripcion);
        etPrecio = (TextView)  v.findViewById(R.id.tvDetallePrecio);
        etFecha = (TextView)  v.findViewById(R.id.tvDetalleFecha);
        imgProducto = (ImageView)  v.findViewById(R.id.imgDetalleProducto);
        etVendedor = (TextView) v.findViewById(R.id.tvDetalleVendedor);
        ubicacion = (Button)v.findViewById(R.id.posicionbtn);
        favoritos = (Button) v.findViewById(R.id.btnDetalleFavoritos);

        //asignamos los datos en la interfaz del producto que hemos recibido
        etNombre.setText(producto.getNombre());
        etFecha.setText("Fecha de publicación: "+producto.getFecha());
        etDescripcion.setText(producto.getDescripcion());
        etPrecio.setText("Precio: "+String.valueOf(producto.getPrecio()));
        etVendedor.setText(producto.getVendedor());

        if(visualizacion.equals("detalle")){
            favoritos.setVisibility(View.GONE);
        }

        if(visualizacion.equals("detalleCatalogo")){
            favoritos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    comprobarSiExiste(etVendedor.getText().toString());
                }
            });

        }

        gestionarImg();

        ubicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MapaFragment m = new MapaFragment(producto.getLatitud(), producto.getLongitud(),"detalle");
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_host_fragment, m);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });



        return v;
    }

    /**
     * METODO QUE COMPRUEBA SI EL VENDEDOR YA FORMA PARTE DE SUS FAVORITOS
     * @param vendedor
     */
    private void comprobarSiExiste(String vendedor){

        Usuario u = MainActivity.getUsuario();
        favoritosRest = APIUtils.getServiceFavoritos();


        Usuario usuarioLogueado = MainActivity.getUsuario();
        //Comprobamos si existe el vendedor
        Call<Favoritos> call = favoritosRest.buscarPorVendedorUsuario(vendedor,usuarioLogueado.getUsuario());
        call.enqueue(new Callback<Favoritos>() {
            @Override
            public void onResponse(Call<Favoritos> call, Response<Favoritos> response) {


                if (response.isSuccessful()) {
                    if (response.code() == 200) {
                        Toast.makeText(getContext(), "El vendedor ya ha sido añadido en sus favoritos", Toast.LENGTH_SHORT).show();

                    }else{
                        insertarFavorito(vendedor);//SI NO FORMA PARTE, LO AGREGA
                    }
                }
            }

            @Override
            public void onFailure(Call<Favoritos> call, Throwable t) {
                Toast.makeText(getContext(), "El servicio no está disponible", Toast.LENGTH_SHORT).show();
            }
        });



    }

    /**
     * METODO QUE AGREGA UN VENDEDOR A LOS FAVORITOS DEL USUARIO
     * @param vendedor
     */
    private void insertarFavorito (String vendedor){

        favoritosRest = APIUtils.getServiceFavoritos();
        usuarioRest = APIUtils.getService();

        //Comprobamos si existe el vendedor
        Call<Usuario> call = usuarioRest.buscarPorNombreUsuario(vendedor);
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {

                Usuario user = response.body();
                Usuario usuarioLogueado = MainActivity.getUsuario();
                if (response.isSuccessful()) {
                    if (response.code() == 200) {

                        Favoritos fav = new Favoritos();
                        fav.setVendedor(user.getUsuario());
                        fav.setEmail(user.getEmail());
                        fav.setUsuario(usuarioLogueado.getUsuario());

                        Call<Favoritos> callInsert  = favoritosRest.create(fav);
                        callInsert.enqueue(new Callback<Favoritos>() {
                            @Override
                            public void onResponse(Call<Favoritos> call, Response<Favoritos> response) {
                                if(response.isSuccessful()){
                                    Toast.makeText(getContext(), "Contacto añadido correctamente", Toast.LENGTH_SHORT).show();


                                }
                            }

                            @Override
                            public void onFailure(Call<Favoritos> call, Throwable t) {
                                Toast.makeText(getContext(), "No se obtuvo respuesta", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }else{
                    Toast.makeText(getContext(), "El vendedor no existe", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(getContext(), "El servicio no está disponible", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * Método para la imagen que sacamos codificada de la BBDD
     */
    private void gestionarImg(){
        Bitmap imagenJuego = Utilidades.base64ToBitmap(producto.getImagen());
        float proporcion = 600 / (float) imagenJuego.getWidth();
        Bitmap imagenFinal = Bitmap.createScaledBitmap(imagenJuego, 600, (int) (imagenJuego.getHeight() * proporcion), false);
        this.imgProducto.setImageBitmap(imagenFinal);
    }




}
