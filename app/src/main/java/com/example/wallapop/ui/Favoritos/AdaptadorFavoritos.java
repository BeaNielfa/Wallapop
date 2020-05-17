package com.example.wallapop.ui.Favoritos;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.wallapop.Models.Favoritos;
import com.example.wallapop.R;
import com.example.wallapop.Rest.APIUtils;
import com.example.wallapop.Rest.FavoritosRest;
import com.example.wallapop.Rest.HistorialRest;


import java.util.ArrayList;
import java.util.Calendar;

public class AdaptadorFavoritos   extends RecyclerView.Adapter<AdaptadorFavoritos.ViewHolder> {

    //VARIABLES
    private ArrayList<Favoritos> listFavoritos;
    private FragmentManager fm;
    Resources res;
    private Context ctx;
    public Calendar calendar;
    FavoritosRest favoritosRest;
    HistorialRest historialRest;

    public AdaptadorFavoritos(ArrayList<Favoritos> listFavoritos, FragmentManager fm, Resources res, Context ctx) {
        this.listFavoritos = listFavoritos;
        this.fm = fm;
        this.res = res;
        this.ctx = ctx;

    }


    @Override
    public AdaptadorFavoritos.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemListaJuegos = layoutInflater.inflate(R.layout.item_favoritos, parent, false);
        AdaptadorFavoritos.ViewHolder viewHolder = new AdaptadorFavoritos.ViewHolder(itemListaJuegos);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AdaptadorFavoritos.ViewHolder holder, int position) {

        final Favoritos productoLista = listFavoritos.get(position);


        //ASIGNAMOS LOS DATOS AL ITEM
        holder.tvFavoritosUsuario.setText(productoLista.getVendedor());
        holder.tvFavoritosEmail.setText(productoLista.getEmail());

        //para que se comunique con el servicio
        favoritosRest = APIUtils.getServiceFavoritos();

    }




    @Override
    public int getItemCount() {
        return listFavoritos.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgFavoritos;
        public TextView tvFavoritosUsuario;
        public TextView tvFavoritosEmail;

        public ViewHolder(View itemView) {
            super(itemView);
            this.imgFavoritos = (ImageView) itemView.findViewById(R.id.imgFavoritoImagen);
            this.tvFavoritosUsuario = (TextView) itemView.findViewById(R.id.tvFavoritoUsuario);
            this.tvFavoritosEmail = (TextView) itemView.findViewById(R.id.tvFavoritoEmail);

        }
    }

}

