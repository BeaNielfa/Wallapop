package com.example.wallapop.ui.Favoritos;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.wallapop.MainActivity;
import com.example.wallapop.Models.Favoritos;
import com.example.wallapop.Models.Producto;
import com.example.wallapop.Models.Usuario;
import com.example.wallapop.R;
import com.example.wallapop.Rest.APIUtils;
import com.example.wallapop.Rest.FavoritosRest;
import com.example.wallapop.Rest.ProductosRest;
import com.example.wallapop.ui.Catalogo.AdaptadorCatalogo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class FavoritosFragment extends Fragment {


    //VARIABLES
    ArrayList<Favoritos> favoritos = new ArrayList<>();
    private RecyclerView recyclerViewFavoritos;
    private AdaptadorFavoritos adaptadorFavoritos;
    private SwipeRefreshLayout swipeRefreshLayout;
    FavoritosRest favoritosRest;
    Context context;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_favoritos, container, false);

        context = root.getContext();

        recyclerViewFavoritos = (RecyclerView) root.findViewById(R.id.recyclerFavoritos);

        recyclerViewFavoritos.setLayoutManager(new LinearLayoutManager(context));

        //para que se comunique con el servicio
        favoritosRest = APIUtils.getServiceFavoritos();


        //Consulto los datos
        consultarProductos();

        recyclerViewFavoritos.setAdapter(new AdaptadorFavoritos(favoritos, getFragmentManager(), getResources(),context));
        return root;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        swipeRecarga(getView());


    }


    /**
     * METODO QUE REFRESCA LA LISTA Y APLICA LOS CAMBIOS
     */
    private void swipeRecarga(View vista) {

        swipeRefreshLayout = (SwipeRefreshLayout) vista.findViewById(R.id.swipeRefreshFavoritos);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Se le ponen los colores que queramos
                swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
                swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.colorBlanco);
                consultarProductos();
                adaptadorFavoritos = new AdaptadorFavoritos(favoritos, getFragmentManager(), getResources(),getActivity());
                recyclerViewFavoritos.setAdapter(adaptadorFavoritos);
                adaptadorFavoritos.notifyDataSetChanged();
                recyclerViewFavoritos.setHasFixedSize(true);
                swipeRefreshLayout.setRefreshing(false);

            }
        });
    }

    /**
     * Método para consumir REST y que nos devuelva todos los productos que
     * hay listados
     */
    private void consultarProductos() {
        favoritos.clear();
        Usuario usuario = MainActivity.getUsuario();
        Call<List<Favoritos>> call = favoritosRest.consultar(usuario.getUsuario());
        call.enqueue(new Callback<List<Favoritos>>() {
            @Override
            public void onResponse(Call<List<Favoritos>> call, Response<List<Favoritos>> response) {
                if (response.isSuccessful()) {
                    favoritos = (ArrayList<Favoritos>) response.body();
                    recyclerViewFavoritos.setAdapter(new AdaptadorFavoritos(favoritos,  getFragmentManager(),getResources(),getActivity()));
                }
            }

            @Override
            public void onFailure(Call<List<Favoritos>> call, Throwable t) {
                Toast.makeText(getContext(), "No se han podido recuperar los productos ", Toast.LENGTH_LONG).show();
            }
        });
    }

}