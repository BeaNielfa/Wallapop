package com.example.wallapop.ui.Productos;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.wallapop.MainActivity;
import com.example.wallapop.Models.Producto;
import com.example.wallapop.Models.Usuario;
import com.example.wallapop.R;
import com.example.wallapop.Rest.APIUtils;
import com.example.wallapop.Rest.ProductosRest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class ProductosFragment extends Fragment {

    //Variables
    ArrayList<Producto> productos = new ArrayList<>();
    private RecyclerView recyclerViewProductos;
    private Adaptador adapterProductos;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton fabProductos;
    private ProductosRest productosRest;
    Context context;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_productos, container, false);


         context = root.getContext();

        recyclerViewProductos= (RecyclerView) root.findViewById(R.id.recyclerProductos);

        recyclerViewProductos.setLayoutManager(new LinearLayoutManager(context));


        //para que se comunique con el servicio
        productosRest = APIUtils.getServiceProductos();

        consultarProductos();

       recyclerViewProductos.setAdapter(new Adaptador(productos, getFragmentManager(), getResources(),context));
       return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        swipeRecarga(getView());



        fabProductos = (FloatingActionButton) getView().findViewById(R.id.fabProductos);
        fabProductos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //LLAMAMOS A AÑADIR PRODUCTO CON EL CONSTRUCTOR VACIO PORQUE QUEREMOS INSERTAR
                AnadirNuevoProducto añadirProducto = new AnadirNuevoProducto();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_host_fragment, añadirProducto);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });




    }

    /**
     * Swipe de recarga, actualiza la lista de productos
     */
    private void swipeRecarga(View vista) {

        swipeRefreshLayout = (SwipeRefreshLayout) vista.findViewById(R.id.swipeRefreshLayoutProductos);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
                swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.colorBlanco);
                consultarProductos();
                adapterProductos = new Adaptador(productos, getFragmentManager(), getResources(),getActivity());
                recyclerViewProductos.setAdapter(adapterProductos);
                adapterProductos.notifyDataSetChanged();
                recyclerViewProductos.setHasFixedSize(true);
                swipeRefreshLayout.setRefreshing(false);

            }
        });
    }

    /**
     * Método para consumir REST y que nos devuelva todos los productos que
     * hay listados
     */
    private void consultarProductos() {
        productos.clear();
        Usuario usuario = MainActivity.getUsuario();
        Call<List<Producto>> call = productosRest.buscar(usuario.getUsuario());
        call.enqueue(new Callback<List<Producto>>() {
            @Override
            public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {
                if (response.isSuccessful()) {
                    productos = (ArrayList<Producto>) response.body();
                    recyclerViewProductos.setAdapter(new Adaptador(productos,  getFragmentManager(),getResources(),context));
                }
            }

            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) {
                Toast.makeText(getContext(), "No se han podido recuperar los productos ", Toast.LENGTH_LONG).show();
            }
        });
    }

}