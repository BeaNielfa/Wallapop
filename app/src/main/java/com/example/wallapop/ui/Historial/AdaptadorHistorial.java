package com.example.wallapop.ui.Historial;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.wallapop.Models.Historial;
import com.example.wallapop.R;
import com.example.wallapop.Utilidades.Utilidades;

import java.util.ArrayList;
import java.util.Collection;

public class AdaptadorHistorial extends RecyclerView.Adapter<AdaptadorHistorial.ViewHolder> {

    //VARIABLES
    private ArrayList<Historial> listHistorial;
    private FragmentManager fm;
    Resources res;
    private Context ctx;

    public AdaptadorHistorial(ArrayList<Historial> listHistorial, FragmentManager fm, Resources res, Context ctx) {
        this.listHistorial = listHistorial;
        this.fm = fm;
        this.res = res;
        this.ctx = ctx;

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemListaHistorial = layoutInflater.inflate(R.layout.item_historial, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemListaHistorial);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final Historial historialLista = listHistorial.get(position);

        //PASAMOS LA IMAGEN A BITMAP Y LA ASIGNAMOS AL ITEM DEL RECYCLERVIEW
        Bitmap imagenJuego = Utilidades.base64ToBitmap(historialLista.getImagen());
        float proporcion = 600 / (float) imagenJuego.getWidth();
        Bitmap imagenFinal = Bitmap.createScaledBitmap(imagenJuego, 600, (int) (imagenJuego.getHeight() * proporcion), false);
        //RoundedBitmapDrawable redondeado = RoundedBitmapDrawableFactory.create(res, imagenFinal);
        //redondeado.setCornerRadius(imagenFinal.getHeight());

        //ASIGNAMOS LOS DATOS AL ITEM
        holder.imgHistorialImagen.setImageBitmap(imagenFinal);
        holder.tvHistorialNombre.setText(historialLista.getNombre());
        holder.tvHistorialFecha.setText("Fecha de compra: "+historialLista.getFecha());
        holder.tvHistorialDescripcion.setText(historialLista.getDescripcion());
        String precio = Utilidades.gestionarPrecioDosDecimales(String.valueOf(historialLista.getPrecio()));
        holder.tvHistorialPrecio.setText(precio+"€");




    }

    @Override
    public int getItemCount() {
        return listHistorial.size();
    }




    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvHistorialNombre;
        public TextView tvHistorialFecha;
        public TextView tvHistorialPrecio;
        public TextView tvHistorialDescripcion;
        public ImageView imgHistorialImagen;
        public CardView itemHistorial;

        public ViewHolder(View itemView) {
            super(itemView);
            this.tvHistorialNombre =(TextView) itemView.findViewById(R.id.tvHistorialNombre);
            this.tvHistorialFecha = (TextView) itemView.findViewById(R.id.tvHistorialFecha);
            this.tvHistorialPrecio = (TextView) itemView.findViewById(R.id.tvHistorialPrecio);
            this.tvHistorialDescripcion = (TextView) itemView.findViewById(R.id.tvHistorialDescripcion);
            this.imgHistorialImagen = (ImageView) itemView.findViewById(R.id.imgHistorialImagen);
            this.itemHistorial = (CardView) itemView.findViewById(R.id.itemHistorial);

        }
    }

}

