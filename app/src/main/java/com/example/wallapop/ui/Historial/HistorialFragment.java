package com.example.wallapop.ui.Historial;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.wallapop.MainActivity;
import com.example.wallapop.Models.Historial;
import com.example.wallapop.Models.Usuario;
import com.example.wallapop.R;
import com.example.wallapop.Rest.APIUtils;
import com.example.wallapop.Rest.HistorialRest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class HistorialFragment extends Fragment {


    ArrayList<Historial> historial = new ArrayList<>();
    private RecyclerView recyclerViewHistorial;
    private AdaptadorHistorial adapterHistorial;
    private SwipeRefreshLayout swipeRefreshLayout;
    HistorialRest historialRest;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_historial, container, false);

        Context context = root.getContext();

        recyclerViewHistorial = (RecyclerView) root.findViewById(R.id.recyclerHistorial);

        recyclerViewHistorial.setLayoutManager(new LinearLayoutManager(context));

        //para que se comunique con el servicio
        historialRest = APIUtils.getServiceHistorial();

        consultarDatos();

        recyclerViewHistorial.setAdapter(new AdaptadorHistorial(historial, getFragmentManager(), getResources(),context));
        return root;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        swipeRecarga(getView());




    }
    /**
     * Swipe de recarga, se llama a su vista correspondiente y se le setea el listener
     */
    private void swipeRecarga(View vista) {

        swipeRefreshLayout = (SwipeRefreshLayout) vista.findViewById(R.id.swipeRefreshHistorial);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Se le ponen los colores que queramos
                swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
                swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.colorBlanco);
                //Le pasamos el fragment manager para gestionar las transacciones necesarias
                consultarDatos();
                adapterHistorial = new AdaptadorHistorial(historial, getFragmentManager(), getResources(),getActivity());
                recyclerViewHistorial.setAdapter(adapterHistorial);
                adapterHistorial.notifyDataSetChanged();
                recyclerViewHistorial.setHasFixedSize(true);
                swipeRefreshLayout.setRefreshing(false);

            }
        });
    }



        /**
         * Método para consumir REST y que nos devuelva todos los productos que
         * hay listados
         */
        private void consultarDatos() {
            historial.clear();
            Usuario usuario = MainActivity.getUsuario();
            Call<List<Historial>> call = historialRest.consultar(usuario.getUsuario());
            call.enqueue(new Callback<List<Historial>>() {
                @Override
                public void onResponse(Call<List<Historial>> call, Response<List<Historial>> response) {
                    if (response.isSuccessful()) {
                        historial = (ArrayList<Historial>) response.body();
                        recyclerViewHistorial.setAdapter(new AdaptadorHistorial(historial,  getFragmentManager(),getResources(),getActivity()));
                    }
                }

                @Override
                public void onFailure(Call<List<Historial>> call, Throwable t) {
                    Toast.makeText(getContext(), "No se han podido recuperar los productos ", Toast.LENGTH_LONG).show();
                }
            });
        }


}