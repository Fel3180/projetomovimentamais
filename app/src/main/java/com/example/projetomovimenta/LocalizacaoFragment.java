package com.example.projetomovimenta;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class LocalizacaoFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap googleMap;
    private ArrayList<Academia> academias;
    private AutoCompleteTextView academiaAutoComplete;
    private ArrayAdapter<String> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.localizacaofragment, container, false);
        academiaAutoComplete = view.findViewById(R.id.autocompletepesquisa);
        academias = readAcademiasFromCSV();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        setupAcademiaAutoComplete();
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        LatLng beloHorizonte = new LatLng(-19.9167, -43.9345);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(beloHorizonte, 12));

        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        showAllGymsOnMap();

        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null; // Retornar null para usar o padrão
            }

            @Override
            public View getInfoContents(Marker marker) {
                View infoWindow = getLayoutInflater().inflate(R.layout.info_window_layout, null);

                Academia academia = (Academia) marker.getTag();
                if (academia != null) {
                    TextView title = infoWindow.findViewById(R.id.title);
                    TextView location = infoWindow.findViewById(R.id.location);
                    RatingBar ratingBar = infoWindow.findViewById(R.id.ratingBar);

                    title.setText(academia.getNome());
                    location.setText("Latitude: " + academia.getLatitude() + ", Longitude: " + academia.getLongitude());
                    ratingBar.setRating(academia.getAvaliacao());
                }

                return infoWindow;
            }
        });
    }

    private ArrayList<Academia> readAcademiasFromCSV() {
        ArrayList<Academia> academias = new ArrayList<>();
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.academias);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String nome = parts[0];
                    String latitude = parts[1];
                    String longitude = parts[2];
                    Academia academia = new Academia(nome, Double.valueOf(latitude), Double.valueOf(longitude), Float.valueOf("0.0"));
                    academias.add(academia);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return academias;
    }

    private void setupAcademiaAutoComplete() {
        ArrayList<String> academiaNames = new ArrayList<>();
        for (Academia academia : academias) {
            academiaNames.add(academia.getNome());
        }

        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, academiaNames);
        academiaAutoComplete.setAdapter(adapter);

        academiaAutoComplete.setOnClickListener(view -> academiaAutoComplete.showDropDown());
        academiaAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
            String selectedAcademia = parent.getItemAtPosition(position).toString();
            academiaAutoComplete.setText(selectedAcademia);
            Toast.makeText(requireContext(), "Academia selecionada: " + selectedAcademia, Toast.LENGTH_SHORT).show();
            filterMap(selectedAcademia);
        });
    }

    private void showAllGymsOnMap() {
        if (googleMap != null && !academias.isEmpty()) {
            for (Academia academia : academias) {
                try {
                    double latitude = academia.getLatitude();
                    double longitude = academia.getLongitude();
                    LatLng location = new LatLng(latitude, longitude);

                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(location)
                            .title(academia.getNome())
                            .snippet(location.toString());

                    Marker marker = googleMap.addMarker(markerOptions);
                    marker.setTag(academia); // Associa a academia ao marcador
                } catch (NumberFormatException e) {
                    Log.e("NumberFormatException", "Valores de latitude/longitude inválidos para academia: " + academia.getNome());
                }
            }
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Academia academia : academias) {
                try {
                    double latitude = academia.getLatitude();
                    double longitude = academia.getLongitude();
                    LatLng location = new LatLng(latitude, longitude);
                    builder.include(location);
                } catch (NumberFormatException e) {
                    Log.e("NumberFormatException", "Valores de latitude/longitude inválidos para academia: " + academia.getNome());
                }
            }

            LatLngBounds bounds = builder.build();
            int padding = 100;
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            googleMap.moveCamera(cu);
        }
    }

    private void filterMap(String selectedAcademia) {
        if (googleMap != null && !academias.isEmpty()) {
            googleMap.clear();

            for (Academia academia : academias) {
                if (academia.getNome().equals(selectedAcademia)) {
                    try {
                        double latitude = academia.getLatitude();
                        double longitude = academia.getLongitude();
                        LatLng location = new LatLng(latitude, longitude);

                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(location)
                                .title(academia.getNome())
                                .snippet(location.toString());

                        googleMap.addMarker(markerOptions);
                        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(location, 18);
                        googleMap.moveCamera(cu);
                        break;
                    } catch (NumberFormatException e) {
                        Log.e("NumberFormatException", "Valores de latitude/longitude inválidos para academia: " + academia.getNome());
                    }
                }
            }
        }
    }
}
