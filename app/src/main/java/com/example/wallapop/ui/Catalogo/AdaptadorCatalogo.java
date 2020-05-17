package com.example.wallapop.ui.Catalogo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import com.example.wallapop.MainActivity;
import com.example.wallapop.Models.Historial;
import com.example.wallapop.Models.Producto;
import com.example.wallapop.Models.Usuario;
import com.example.wallapop.R;
import com.example.wallapop.Rest.APIUtils;
import com.example.wallapop.Rest.HistorialRest;
import com.example.wallapop.Rest.ProductosRest;
import com.example.wallapop.Utilidades.Utilidades;
import com.example.wallapop.ui.Productos.DetalleProducto;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import java.util.ArrayList;
import java.util.Calendar;

public class AdaptadorCatalogo extends RecyclerView.Adapter<AdaptadorCatalogo.ViewHolder> {

    //VARIABLES
    private ArrayList<Producto> listProductos;
    private FragmentManager fm;
    Resources res;
    private Context ctx;
    public Calendar calendar;
    ProductosRest productosRest;
    HistorialRest historialRest;

    public AdaptadorCatalogo(ArrayList<Producto> listJuegos, FragmentManager fm, Resources res, Context ctx) {
        this.listProductos = listJuegos;
        this.fm = fm;
        this.res = res;
        this.ctx = ctx;

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemListaJuegos = layoutInflater.inflate(R.layout.item_catalogo, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemListaJuegos);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final Producto productoLista = listProductos.get(position);

        //PASAMOS LA IMAGEN A BITMAP Y LA ASIGNAMOS AL ITEM DEL RECYCLERVIEW
        Bitmap imagenJuego = Utilidades.base64ToBitmap(productoLista.getImagen());
        float proporcion = 600 / (float) imagenJuego.getWidth();
        Bitmap imagenFinal = Bitmap.createScaledBitmap(imagenJuego, 600, (int) (imagenJuego.getHeight() * proporcion), false);

        //ASIGNAMOS LOS DATOS AL ITEM
        holder.imgProductoCatalogo.setImageBitmap(imagenFinal);
        holder.tvCatalogoNombre.setText(productoLista.getNombre());
        holder.tvCatalogoFecha.setText(productoLista.getFecha());
        String precio = Utilidades.gestionarPrecioDosDecimales(String.valueOf(productoLista.getPrecio()));
        holder.tvCatalogoPrecio.setText(precio);

        //para que se comunique con el servicio
        productosRest = APIUtils.getServiceProductos();
        historialRest = APIUtils.getServiceHistorial();

        holder.itemCatalogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llamarDetalle(productoLista);
            }
        });

        holder.imgCatalogoComprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoComprar(productoLista);
            }
        });



    }

    /**
     * METODO QUE MUESTRA UN DIALOGO PARA LA CONFIRMACION DE SI QUEREMOS ELIMINAR UN PRODUCTO
     * @param mItem
     */
    private void mostrarDialogoComprar(final Producto mItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle("Comprar Producto");
        builder.setMessage("¿Desea comprar el producto "+mItem.getNombre()+ " Por "+mItem.getPrecio()+"€"+"?");
        builder.setPositiveButton("Comprar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {
                insertarCompra(mItem);

                dialogInterface.dismiss();
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    /**
     * METODO QUE CAMBIA EL ESTADO DEL PRODUCTO
     * ESTADO 0 DISPONIBLE
     * ESTADO 1 NO DISPONIBLE
     * ESTADO 2 COMPRADO
     * @param producto
     */
    private void cambiarEstadoProducto(Producto producto){
        int estado = 2;
        producto.setEstado(estado);
        //ACTUALIZAMOS EL ESTADO DEL PRODUCTO
        Call<Producto> callupdate = productosRest.modificarEstadoProducto(producto.getId(), producto);
        callupdate.enqueue(new Callback<Producto>() {
            @Override
            public void onResponse(Call<Producto> call, Response<Producto> response) {

                if (response.code() == 200) {

                    Toast.makeText(ctx, "Actualizando estado del Producto",
                            Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<Producto> call, Throwable t) {
                Log.e("ERROR: ", t.getMessage());
            }
        });

    }

    /**
     * METODO QUE MUESTRA EL FRAGMENT DE DETALLE
     * @param productoLista
     */
    private void llamarDetalle(Producto productoLista){
        DetalleProducto detalle = new DetalleProducto(productoLista, "detalleCatalogo");
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.nav_host_fragment, detalle);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    /**
     * METODO QUE INSERTA UNA COMPRA EN LA TABLA HISTORIAL
     * @param producto
     */
    private void insertarCompra(Producto producto){

       //COGEMOS LA FECHA ACTUAL DE LA COMPRA
        calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH)+1;
        int year = calendar.get(Calendar.YEAR);

        String fecha = day+"/"+month+"/"+ year;

        Historial historial = new Historial();
        historial.setFecha(fecha);
        historial.setProducto(producto.getId());
        Usuario usuario = MainActivity.getUsuario();
        historial.setUsuario(usuario.getUsuario());
        historial.setNombre(producto.getNombre());
        historial.setDescripcion(producto.getDescripcion());
        historial.setPrecio(producto.getPrecio());
        historial.setImagen(producto.getImagen());

        Call<Historial> call  = historialRest.create(historial);
        call.enqueue(new Callback<Historial>() {
            @Override
            public void onResponse(Call<Historial> call, Response<Historial> response) {
                if(response.isSuccessful()){
                    Toast.makeText(ctx, "Compra realizada con exito", Toast.LENGTH_SHORT).show();
                    cambiarEstadoProducto(producto);//Cambiamos el estado del producto a comprado

                }
            }

            @Override
            public void onFailure(Call<Historial> call, Throwable t) {
                Toast.makeText(ctx, "fallo", Toast.LENGTH_SHORT).show();
            }
        });


    }


    @Override
    public int getItemCount() {
        return listProductos.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgProductoCatalogo;
        public TextView tvCatalogoNombre;
        public TextView tvCatalogoPrecio;
        public TextView tvCatalogoFecha;
        public ImageView imgCatalogoComprar;
        public CardView itemCatalogo;


        public ViewHolder(View itemView) {
            super(itemView);
            this.imgProductoCatalogo = (ImageView) itemView.findViewById(R.id.imgCatalogoImagen);
            this.tvCatalogoNombre = (TextView) itemView.findViewById(R.id.tvCatalogoNombre);
            this.tvCatalogoPrecio = (TextView) itemView.findViewById(R.id.tvCatalogoPrecio);
            this.tvCatalogoFecha = (TextView) itemView.findViewById(R.id.tvCatalogoFecha);
            this.itemCatalogo = (CardView) itemView.findViewById(R.id.itemCatalogo);
            this.imgCatalogoComprar =(ImageView) itemView.findViewById(R.id.img_comprar);

        }
    }

}

