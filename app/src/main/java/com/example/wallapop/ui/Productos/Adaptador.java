package com.example.wallapop.ui.Productos;

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
import android.widget.Switch;
import android.widget.TextView;

import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wallapop.Models.Producto;
import com.example.wallapop.R;
import com.example.wallapop.Rest.APIUtils;
import com.example.wallapop.Rest.ProductosRest;
import com.example.wallapop.Utilidades.Utilidades;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;

public class Adaptador extends RecyclerView.Adapter<Adaptador.ViewHolder> {

    //VARIBLES
    private ArrayList<Producto> listProductos = new ArrayList<>();
    private FragmentManager fm;
    Resources res;
    private Context ctx;
    ProductosRest productosRest;

    public Adaptador(ArrayList<Producto> listProductos, FragmentManager fm, Resources res, Context ctx) {
        this.listProductos = listProductos;
        this.fm = fm;
        this.res = res;
        this.ctx = ctx;

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemListaProductos = layoutInflater.inflate(R.layout.item_producto, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemListaProductos);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(Adaptador.ViewHolder holder, int position) {

        final Producto productoLista = listProductos.get(position);

        //PASAMOS LA IMAGEN A BITMAP Y LA ASIGNAMOS AL ITEM DEL RECYCLERVIEW
        Bitmap imagenJuego = Utilidades.base64ToBitmap(productoLista.getImagen());
        float proporcion = 600 / (float) imagenJuego.getWidth();
        Bitmap imagenFinal = Bitmap.createScaledBitmap(imagenJuego, 600, (int) (imagenJuego.getHeight() * proporcion), false);

        //ASIGNAMOS LOS DATOS AL ITEM
        holder.imgProductos.setImageBitmap(imagenFinal);
        holder.tvProductosNombre.setText(productoLista.getNombre());
        holder.tvProductosFecha.setText(productoLista.getFecha());
        String precio = Utilidades.gestionarPrecioDosDecimales(String.valueOf(productoLista.getPrecio()));
        holder.tvProductosPrecio.setText(precio);

        //para que se comunique con el servicio
        productosRest = APIUtils.getServiceProductos();


        holder.itemProductos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llamarDetalle(productoLista);//Mostramos el detalle del producto en el que se ha hecho click
            }
        });

        //Cuándo hacemos click en la imgen de borrar
        holder.imgBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoEliminar(listProductos.get(position));//Mostramos el dialogo
            }
        });

        //Cuando hacemos click en la imagen editar
        holder.imgEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llamarActualizarProducto(productoLista);

            }
        });


        holder.switchProductos.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                boolean isChecked = ((Switch)view).isChecked();
                if(isChecked){//Si lo chequeamos
                    cambiarEstadoProducto(productoLista,1);//Cambiamos el estado a no disponible
                    ((Switch) view).setText("NO disponible");

                }else{//Si no
                    cambiarEstadoProducto(productoLista,0);//Cambiamos el estado a dispobible
                    ((Switch) view).setText("Disponible");
                }

            }
        });


        //comprobamos el estado del producto para que al mostrar la lista se vea reflejado
        if (productoLista.getEstado() == 1) {
            holder.switchProductos.setChecked(true);
            holder.switchProductos.setText("NO disponible");
        }else{
            holder.switchProductos.setChecked(false);
            holder.switchProductos.setText("Disponible");
        }




    }
    //

    /**
     * METODO QUE CAMBIA EL ESTADO DEL PRODUCTO
     * ESTADO 0 DISPONIBLE
     * ESTADO 1 NO DISPONIBLE
     * ESTADO 2 COMPRADO
     * @param producto
     * @param estado
     */
    private void cambiarEstadoProducto(Producto producto, int estado){
        producto.setEstado(estado);
        //ACTUALIZAMOS EL PRODUCTO
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
        DetalleProducto detalle = new DetalleProducto(productoLista);
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.nav_host_fragment, detalle);
        transaction.addToBackStack(null);
        transaction.commit();
    }



    /**
     * METODO QUE MUESTRA EL FRAGMENT DE ACTUALIZAR PRODUCTO
     * @param producto
     */
    private void llamarActualizarProducto(Producto producto){

        //Lo llamamos con el modo de visualizacion "actualizar" porque en esté caso es lo que queremos hacer
        AnadirNuevoProducto añadirJuego = new AnadirNuevoProducto(producto,"actualizar");
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.nav_host_fragment, añadirJuego);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    /**
     *  METODO QUE MUESTRA UN DIALOGO PARA LA CONFIRMACION DE SI QUEREMOS ELIMINAR UN PRODUCTO
     */
    private void mostrarDialogoEliminar(final Producto mItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle("Eliminar Producto");
        builder.setMessage("¿Desea eliminar definitivamente el producto "+mItem.getNombre()+"?");

        builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {
                borrarProducto(mItem.getId());//llamamos al metodo para borrar

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
     * METODO PARA BORRAR UN PRODUCTO DE LA BASE DE DATOS
     * @param id
     */
    private void borrarProducto(long id){


        Call<Producto> call  = productosRest.eliminarProducto(id);
        call.enqueue(new Callback<Producto>() {

            @Override
            public void onResponse(Call<Producto> call, Response<Producto> response) {
                if(response.isSuccessful()){
                    Toast.makeText(ctx, "Producto eliminado correctamente", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<Producto> call, Throwable t) {
                Toast.makeText(ctx, "fallo", Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    public int getItemCount() {
        return listProductos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgProductos;
        public TextView tvProductosNombre;
        public TextView tvProductosPrecio;
        public TextView tvProductosFecha;
        public  ImageView imgEditar;
        public ImageView imgBorrar;
        public CardView itemProductos;
        public Switch switchProductos;


        public ViewHolder(View itemView) {
            super(itemView);
            this.imgProductos = (ImageView) itemView.findViewById(R.id.imageViewFotoProducto);
            this.tvProductosNombre = (TextView) itemView.findViewById(R.id.textViewNombre);
            this.tvProductosPrecio = (TextView) itemView.findViewById(R.id.textViewPrecio);
            this.tvProductosFecha = (TextView) itemView.findViewById(R.id.textViewFechaPublicacion);
            this.itemProductos = (CardView) itemView.findViewById(R.id.itemProductos);
            this.imgEditar = (ImageView) itemView.findViewById(R.id.imageViewEditar);
            this.imgBorrar = (ImageView) itemView.findViewById(R.id.imageViewBorrar);
            this.switchProductos= (Switch) itemView.findViewById(R.id.switchProductoDisponible);
        }
    }

}

