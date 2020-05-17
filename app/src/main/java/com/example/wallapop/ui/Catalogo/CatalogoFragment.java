package com.example.wallapop.ui.Catalogo;

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
import com.example.wallapop.Models.Producto;
import com.example.wallapop.Models.Usuario;
import com.example.wallapop.R;
import com.example.wallapop.Rest.APIUtils;
import com.example.wallapop.Rest.ProductosRest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class CatalogoFragment extends Fragment {

    //VARIABLES
    ArrayList<Producto> productos = new ArrayList<>();
    private RecyclerView recyclerViewCatalogo;
    private AdaptadorCatalogo adapterCatalogo;
    private SwipeRefreshLayout swipeRefreshLayout;
    ProductosRest productosRest;
    Button btnFiltro;
    EditText etFiltrarNombre;
    Spinner spinner;
    ArrayList<Integer> items = new ArrayList<Integer>();
    Context context;
    float precio = 50f;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_catalogo, container, false);

        context = root.getContext();

        recyclerViewCatalogo = (RecyclerView) root.findViewById(R.id.recyclerCatalogo);
        spinner=(Spinner) root.findViewById(R.id.spinnerPrecio);
        etFiltrarNombre = (EditText) root.findViewById(R.id.filtroNombre);
        btnFiltro = (Button) root.findViewById(R.id.btnFiltro);

        recyclerViewCatalogo.setLayoutManager(new LinearLayoutManager(context));

        //para que se comunique con el servicio
        productosRest = APIUtils.getServiceProductos();

        //Relleno el spinner del filtro de precio
        for (int i =50; i<550; i = i+50){
            items.add(i);
        }
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(context,android.R.layout.simple_spinner_item, items);
        spinner.setAdapter(adapter);

        //Consulto los datos
        consultarProductos();

        recyclerViewCatalogo.setAdapter(new AdaptadorCatalogo(productos, getFragmentManager(), getResources(),context));
        return root;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        swipeRecarga(getView());

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                precio = Float.parseFloat(adapterView.getItemAtPosition(i).toString());//guardamos el precio elegido por el usuario
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnFiltro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productos.clear();

                if (!etFiltrarNombre.getText().toString().isEmpty()) {//Si hemos rellenado ambos filtros

                    Usuario usuario = MainActivity.getUsuario();
                    Call<List<Producto>> call = productosRest.filtroCompleto( usuario.getUsuario(),etFiltrarNombre.getText().toString(), precio);
                    call.enqueue(new Callback<List<Producto>>() {
                        @Override
                        public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {
                            if (response.isSuccessful()) {
                                productos = (ArrayList<Producto>) response.body();
                                recyclerViewCatalogo.setAdapter(new AdaptadorCatalogo(productos, getFragmentManager(), getResources(), getActivity()));
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Producto>> call, Throwable t) {
                            Toast.makeText(getContext(), "No se han podido recuperar los productos ", Toast.LENGTH_LONG).show();
                        }
                    });
                }else{//si no, solo filtra por precio
                   // Toast.makeText(context, precio+"", Toast.LENGTH_SHORT).show();
                    Usuario usuario = MainActivity.getUsuario();
                    Call<List<Producto>> call = productosRest.filtroPrecio(  usuario.getUsuario(), precio);
                    call.enqueue(new Callback<List<Producto>>() {
                        @Override
                        public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {
                            if (response.isSuccessful()) {
                                productos = (ArrayList<Producto>) response.body();
                                recyclerViewCatalogo.setAdapter(new AdaptadorCatalogo(productos, getFragmentManager(), getResources(), getActivity()));
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Producto>> call, Throwable t) {
                            Toast.makeText(getContext(), "No se han podido recuperar los productos ", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });



    }


    /**
     * METODO QUE REFRESCA LA LISTA Y APLICA LOS CAMBIOS
     */
    private void swipeRecarga(View vista) {

        swipeRefreshLayout = (SwipeRefreshLayout) vista.findViewById(R.id.swipeRefreshCatalogo);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Se le ponen los colores que queramos
                swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
                swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.colorBlanco);
                consultarProductos();
                adapterCatalogo = new AdaptadorCatalogo(productos, getFragmentManager(), getResources(),getActivity());
                recyclerViewCatalogo.setAdapter(adapterCatalogo);
                adapterCatalogo.notifyDataSetChanged();
                recyclerViewCatalogo.setHasFixedSize(true);
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
        Call<List<Producto>> call = productosRest.buscarCatalogo(usuario.getUsuario());
        call.enqueue(new Callback<List<Producto>>() {
            @Override
            public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {
                if (response.isSuccessful()) {
                    productos = (ArrayList<Producto>) response.body();
                    recyclerViewCatalogo.setAdapter(new AdaptadorCatalogo(productos,  getFragmentManager(),getResources(),getActivity()));
                }
            }

            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) {
                Toast.makeText(getContext(), "No se han podido recuperar los productos ", Toast.LENGTH_LONG).show();
            }
        });
    }

}