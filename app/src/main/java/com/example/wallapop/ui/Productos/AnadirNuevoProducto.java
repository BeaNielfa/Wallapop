package com.example.wallapop.ui.Productos;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.*;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.FragmentTransaction;
import com.example.wallapop.MainActivity;
import com.example.wallapop.Models.Producto;
import com.example.wallapop.Models.Usuario;
import com.example.wallapop.R;
import com.example.wallapop.Rest.APIUtils;
import com.example.wallapop.Rest.ProductosRest;
import com.example.wallapop.Utilidades.Utilidades;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class AnadirNuevoProducto extends Fragment {

    //Variables
    Producto producto;
    ProductosRest productosRest;
    View v;
    EditText etNombre, etPrecio, etFecha, etDescripcion;
    TextView tvVendedor;
    Context ctx;
    FloatingActionButton fabElegirFoto;
    ImageView fotoElegida;
    Button btnAceptar, btnCancelar, btnFecha, btnMapa;
    private String b64="";
    private Calendar calendar;
    private DatePickerDialog datePickerDialog;
    private String modoVisualización;
    Double latitud, longitud;
    //Constantes
    private static final int GALERIA = 1;
    private static final int CAMARA = 2;

    public AnadirNuevoProducto() {

        modoVisualización ="insertar";
    }
    public AnadirNuevoProducto(Producto producto, String modoVisualización) {
        this.producto = producto;
        this.modoVisualización = modoVisualización;

    }

    public AnadirNuevoProducto( Double latitud, Double  longitud) {
        this.latitud = latitud;
        this.longitud = longitud;
        modoVisualización ="insertar";



    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ctx = getActivity();
        v = inflater.inflate(R.layout.nuevo_producto, container, false);

        //ENLAZAMS LOS VALORES
        etNombre = (EditText) v.findViewById(R.id.etInsertarNombre);
        etDescripcion = (EditText) v.findViewById(R.id.etInsertarDescripcion);
        etPrecio = (EditText) v.findViewById(R.id.etInsertarPrecio);
        fabElegirFoto = (FloatingActionButton) v.findViewById(R.id.fbInsertarImagen);
        this.fabElegirFoto.setOnClickListener(listeners);
        fotoElegida = (ImageView) v.findViewById(R.id.imgInsertarProducto);
        btnAceptar = (Button) v.findViewById(R.id.btnInsertarAceptar);
        this.btnAceptar.setOnClickListener(listeners);
        btnCancelar = (Button) v.findViewById(R.id.btnInsertarCancelar);
        this.btnCancelar.setOnClickListener(listeners);
        etFecha =(EditText) v.findViewById(R.id.etInsertarFecha);
        btnFecha = (Button) v.findViewById(R.id.btnInsertarFecha);
        this.btnFecha.setOnClickListener(listeners);
        btnMapa = (Button)v.findViewById(R.id.btnInsertarMapa);
        this.btnMapa.setOnClickListener(listeners);
        tvVendedor = (TextView) v.findViewById(R.id.tvVendedor);
        etFecha.setEnabled(false);

        //para que se comunique con el servicio
        productosRest = APIUtils.getServiceProductos();

        //GESTIONAMOS EL TIPO DE VISUALIZACION
        tipoVisualizacion(modoVisualización);

        return  v;
    }


    /**
     * Para manejar los listeners
     */
    private View.OnClickListener listeners = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btnInsertarFecha:
                    escogerFecha();

                    break;
                case R.id.btnInsertarAceptar:
                    //El botón hará una cosa u otra en función del modo de visualización
                    switch (modoVisualización) {
                        case "insertar"://Si es insertar
                            String nombre = etNombre.getText().toString();
                            String categoria = etDescripcion.getText().toString();
                            String precio = etPrecio.getText().toString();
                            String imagen = b64;
                            String fecha = etFecha.getText().toString();


                            insertarProducto(nombre, categoria,precio,fecha,imagen);//insertará el producto

                            break;
                        case "actualizar"://y si es actualizar, actualizará el producto.
                            actualizarProducto();
                            break;

                        default:
                            break;

                    }
                    break;
                case R.id.fbInsertarImagen:
                    mostrarDialogoFoto();
                    break;
                case R.id.btnInsertarMapa:
                    llamarMapa();
                    break;
                case R.id.btnInsertarCancelar:
                    volverALRecycler();
                default:
                    break;
            }
        }
    };

    /**
     * Metodo que segun sea el tipo de visualizacion (insertar o actualizar)
     * Mostrará información del producto o no.
     * @param tipo
     */
    private void tipoVisualizacion(String tipo){
        if(tipo.equals("actualizar")){
            mostrarInformacionProducto();
            btnAceptar.setText("Actualizar");
        }else{
            tvVendedor.setVisibility(View.GONE);
        }
    }

    /**
     * Metodo que muestra la información del producto a actualizar.
     */
    private void mostrarInformacionProducto(){
        etNombre.setText(producto.getNombre());
        etFecha.setText(producto.getFecha());
        etDescripcion.setText(producto.getDescripcion());
        etPrecio.setText(String.valueOf(producto.getPrecio()));
        tvVendedor.setText(producto.getVendedor());


        gestionarImg();
    }

    /**
     * Método para la imagen que sacamos codificada de la BBDD
     */
    private void gestionarImg(){
        Bitmap imagenJuego = Utilidades.base64ToBitmap(producto.getImagen());
        float proporcion = 600 / (float) imagenJuego.getWidth();
        Bitmap imagenFinal = Bitmap.createScaledBitmap(imagenJuego, 600, (int) (imagenJuego.getHeight() * proporcion), false);
        this.fotoElegida.setImageBitmap(imagenFinal);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALERIA) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), contentURI);
                    b64 = convertirenBytes(bitmap);

                    this.fotoElegida.setImageBitmap(bitmap);

                } catch (IOException e) {
                    Snackbar.make(getView(), "Fallo en la galería", Snackbar.LENGTH_LONG).show();

                }
            }
        } else if (requestCode == CAMARA) {
            Bitmap thumbnail = null;
            try {
                thumbnail = (Bitmap) data.getExtras().get("data");
                b64 = convertirenBytes(thumbnail);
                this.fotoElegida.setImageBitmap(thumbnail);

            } catch (Exception e) {
                Snackbar.make(getView(), "Fallo en la cámara", Snackbar.LENGTH_LONG).show();
            }
        }
    }


    /**
     * Para convertir bitmap en Array de Bites
     */

    public String convertirenBytes(Bitmap bitmap) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream);
        byte[] byteArray = stream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);

    }

    /**
     * Metodo que muestra un dialogo para elegir
     * foto de galeria
     * desde la camara
     */
    private void mostrarDialogoFoto() {
        AlertDialog.Builder fotoDialogo = new AlertDialog.Builder(getContext());
        fotoDialogo.setTitle("Elige un método de entrada");
        String[] fotoDialogoItems = {
                "Seleccionar fotografía de galería",
                "Capturar fotografía desde la cámara"};
        fotoDialogo.setItems(fotoDialogoItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                elegirFotoGaleria();
                                break;
                            case 1:
                                tomarFotoCamara();
                                break;
                        }
                    }
                });
        fotoDialogo.show();
    }



    /**
     * Llamamos a al galería
     */
    public void elegirFotoGaleria() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALERIA);
    }

    /**
     * Llamamos a la cámara
     */
    private void tomarFotoCamara() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMARA);
    }


    /**
     * METODO QUE MUESTRA UN DIALOGO PARA ELEGIR LA FECHA Y LA ASIGNAMOS AL EDITTEXT DE FECHA
     */
    private void escogerFecha() {
        calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);


        datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int mYear, int mMonth, int mDay) {
                etFecha.setText(mDay + "/" + (mMonth + 1) + "/" + mYear);
            }
        }, day, month, year);
        datePickerDialog.show();
    }

    //METODO QUE VUELVE A LA LISTA DE MIS PRODUCTOS
    private void volverALRecycler(){
        ProductosFragment recycler = new ProductosFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment, recycler);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    /**
     * Metodo que muestra el fragment de mapa para elegir la posición.
     */
    private void llamarMapa (){

        if (modoVisualización == "actualizar") {
            MapaFragment mapa = new MapaFragment( producto.getLatitud(), producto.getLongitud(),modoVisualización);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.nav_host_fragment, mapa);
            transaction.addToBackStack(null);
            transaction.commit();
        }else{
            MapaFragment mapa = new MapaFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.nav_host_fragment, mapa);
            transaction.addToBackStack(null);
            transaction.commit();
        }

    }


    /********************************************************/
    /*                GESTIÓN DE BBDD                       */
    /********************************************************/


    /**
     * Metodo que actualiza un producto
     */
    private void actualizarProducto() {
        //RECOGEMOS LOS DATOS A ACTUALIZAR
        String nombre = etNombre.getText().toString();
        String categoria = etDescripcion.getText().toString();
        String precio = etPrecio.getText().toString();
        String imagen = b64;
        String fecha = etFecha.getText().toString();


        //SI B64 ES CADENA VACIA SIGNIFICA QUE NO A ESCOGIDO UNA NUEVA IMAGEN Y DEJAMOS LA QUE YA TENIA ESE PRODUCTO.
        if(b64.equals("")){
            imagen = producto.getImagen();

        }
        //Actualizamos el producto antes de pasarlo al servicio
        producto.setNombre(nombre);
        producto.setDescripcion(categoria);
        producto.setPrecio(Float.parseFloat(precio));
        producto.setFecha(fecha);
        producto.setImagen(imagen);

        Call<Producto> callupdate = productosRest.modificarProducto(producto.getId(), producto);
        callupdate.enqueue(new Callback<Producto>() {
            @Override
            public void onResponse(Call<Producto> call, Response<Producto> response) {

                if (response.code() == 200) {

                    Toast.makeText(getContext(), "Actualizando Producto",
                            Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<Producto> call, Throwable t) {
                Log.e("ERROR: ", t.getMessage());
            }
        });

        //UNA VEZ ACTUALIZADO EL PRODUCTO VOLVEMOS A LA LISTA DE MIS PRODUCTOS
        volverALRecycler();
    }


    /**
     * Metodo que inserta un producto en la base de datos del servicio
     * Estado = 0 DISPONIBLE
     * Estado = 1 NO DISPONIBLE
     * Estado = 2 COMPRADO
     * @param nombre
     * @param descripcion
     * @param precio
     * @param fecha
     * @param imagen
     */
    private void insertarProducto(String nombre, String descripcion, String precio, String fecha, String imagen){

        Usuario usuario = MainActivity.getUsuario();
        if(!nombre.isEmpty() && !descripcion.isEmpty()  && !precio.isEmpty() && !b64.equals("") && !fecha.isEmpty()){
            Producto product = new Producto() ;

            product.setNombre(nombre);
            product.setDescripcion(descripcion);
            product.setPrecio(Float.parseFloat(precio));
            product.setFecha(fecha);
            product.setImagen(imagen);
            product.setLatitud(latitud);
            product.setLongitud(longitud);
            product.setVendedor(usuario.getUsuario());
            product.setEstado(0);




            Call<Producto> call  = productosRest.insertar(product);
            call.enqueue(new Callback<Producto>() {
                @Override
                public void onResponse(Call<Producto> call, Response<Producto> response) {
                    if(response.isSuccessful()){
                        Toast.makeText(getContext(), "Producto añadido correctamente", Toast.LENGTH_SHORT).show();

                    }
                }

                @Override
                public void onFailure(Call<Producto> call, Throwable t) {
                    Toast.makeText(getContext(), "fallo", Toast.LENGTH_SHORT).show();
                }
            });


            //Cuándo ya hemos terminado de insertar, volvemos a la lista de productos.
            volverALRecycler();

        }else{
            Snackbar.make(getView(), "Introduzca todos los datos.", Snackbar.LENGTH_LONG).show();

        }
    }


}
