package com.example.wallapop.ui.Productos;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.wallapop.MainActivity;
import com.example.wallapop.Models.Producto;
import com.example.wallapop.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapaFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    GoogleMap map;
    private Marker marcadorActual = null;
    private SupportMapFragment supportMapFragment;
    private FusedLocationProviderClient mPosicion;
    private Location miUltimaLocalizacion;
    Button btnUbicacion;
    private LatLng posActual;
    View view;
    Circle circle;
    Double latitud, longitud, la, lo;
    String visualizacion;
    Producto producto;

    public MapaFragment() {
      // Required empty public constructor
        visualizacion = "insertar";
    }

    public MapaFragment(Double la, Double lo, String visualizacion) {
        this.la = la;
        this.lo = lo;
        this.visualizacion = visualizacion;

    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_mapa, container, false);

        btnUbicacion = (Button) view.findViewById(R.id.btnMapaDireccion);




        // Inflate the layout for this fragment
        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //pintamos el mapa
        mPosicion = LocationServices.getFusedLocationProviderClient(getActivity());
        FragmentManager fm = getChildFragmentManager();
        supportMapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (supportMapFragment == null) {
            supportMapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map, supportMapFragment).commit();
        }

        supportMapFragment.getMapAsync(this);


            btnUbicacion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    latitud = marcadorActual.getPosition().latitude;
                    longitud = marcadorActual.getPosition().longitude;


                    volverFragmentAñadir(latitud, longitud);
                }
            });



    }

    public void volverFragmentAñadir(Double latitud, Double longitud){
        Toast.makeText(getContext(), latitud+""+longitud, Toast.LENGTH_SHORT).show();
       AnadirNuevoProducto recycler = new AnadirNuevoProducto( latitud, longitud);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment, recycler);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        configurarIUMapa();
        obtenerPosicion();

        //SI LA VISUALIZACION ES INSERTAR O ACTUALIZAR, DEBE DE DEJAR MODIFICAR LA UBICACIÓN DEL PRODUCTO
        if(visualizacion == "insertar" || visualizacion=="actualizar") {
            map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {

                    marcadorActual.setPosition(latLng);
                    circle.remove();
                    circle = map.addCircle(new CircleOptions()
                            .center(latLng)
                            .radius(50)
                            .strokeColor(Color.RED)
                            .fillColor(ContextCompat.getColor(getContext(), R.color.colorVerde)));
                    map.moveCamera(CameraUpdateFactory.newLatLng(marcadorActual.getPosition()));


                }
            });
        }


    }

    // Obtenermos y leemos directamente el GPS
    // Esto se puede hacer trabajemos con mapas o no
    // Por ejemplo pata mostrar la localización en etiquetas
    private void obtenerPosicion() {
        try {

                // Lo lanzamos como tarea concurrente
                Task<Location> local = mPosicion.getLastLocation();
                local.addOnCompleteListener((getActivity()), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Actualizamos la última posición conocida
                            miUltimaLocalizacion = task.getResult();
                            posActual = new LatLng(miUltimaLocalizacion.getLatitude(),
                                    miUltimaLocalizacion.getLongitude());
                            // Añadimos un marcador especial para poder operar con esto
                            marcadorPosicionActual();


                        } else {
                            Log.d("GPS", "No se encuetra la última posición.");
                            Log.e("GPS", "Exception: %s", task.getException());
                        }
                    }
                });

        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }
    private void configurarIUMapa() {
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setOnMarkerClickListener(this);
        UiSettings uiSettings = map.getUiSettings();
        uiSettings.setScrollGesturesEnabled(true);
        uiSettings.setTiltGesturesEnabled(true);
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setMapToolbarEnabled(true);
        uiSettings.setCompassEnabled(false);
    }

    // Para dibujar el marcador actual
    public void marcadorPosicionActual() {

         circle = map.addCircle(new CircleOptions()
                .center(this.posActual)
                .radius(50)
                .strokeColor(Color.RED)
                .fillColor(ContextCompat.getColor(getContext(), R.color.colorVerde)));



        // Borramos el arcador actual si está puesto
        if(marcadorActual!=null){
            marcadorActual.remove();
        }
        // añadimos el marcador actual
        marcadorActual= map.addMarker(new MarkerOptions()
                // Posición
                .position(posActual)
                // Título
                .title("Mi Localización")
                // Subtitulo
                .snippet("Localización actual")
                // Color o tipo d icono
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        );

        //SI LA VISUALIZACION ES EN MODO DETALLE, NO NECESITAMOS GUARDAR LA DIRECCION,
        // ASI QUE PONEMOS INVISIBLE EL BOTON
        if(visualizacion =="detalle"){
            btnUbicacion.setVisibility(View.GONE);
        }

        if(la != null && lo != null ) {

            marcadorActual.setPosition(new LatLng(la, lo));
            circle.remove();
            circle = map.addCircle(new CircleOptions()
                    .center(marcadorActual.getPosition())
                    .radius(50)
                    .strokeColor(Color.RED)
                    .fillColor(ContextCompat.getColor(getContext(), R.color.colorVerde)));

        }
        map.moveCamera(CameraUpdateFactory.newLatLng(marcadorActual.getPosition()));
    }



    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }



}
