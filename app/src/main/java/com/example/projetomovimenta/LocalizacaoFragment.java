package com.example.projetomovimenta;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.localizacaofragment, container, false);
        academiaAutoComplete = view.findViewById(R.id.autoCompleteAcademia);
        academias = readAcademiasFromCSV();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
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
        showNearbyGyms();
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
                    Academia academia = new Academia(nome, new Localizacao(latitude, longitude));
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

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, academiaNames);
        academiaAutoComplete.setAdapter(adapter);

        academiaAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
            String selectedAcademia = parent.getItemAtPosition(position).toString();
            academiaAutoComplete.setText(selectedAcademia); // Define o texto selecionado no AutoCompleteTextView
            Toast.makeText(requireContext(), "Academia selecionada: " + selectedAcademia, Toast.LENGTH_SHORT).show();
        });
    }

    private void showNearbyGyms() {
        if (googleMap != null && !academias.isEmpty()) {
            googleMap.clear();
            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            for (Academia academia : academias) {
                try {
                    double latitude = Double.parseDouble(academia.getLocalizacao().getLatitude());
                    double longitude = Double.parseDouble(academia.getLocalizacao().getLongitude());
                    LatLng location = new LatLng(latitude, longitude);

                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(location)
                            .title(academia.getNome())
                            .snippet(location.toString());

                    googleMap.addMarker(markerOptions);
                    builder.include(location); // Inclui a localização do marcador nos limites do mapa
                } catch (NumberFormatException e) {
                    Log.e("NumberFormatException", "Valores de latitude/longitude inválidos para academia: " + academia.getNome());
                }
            }

            LatLngBounds bounds = builder.build();
            int padding = 100; // Espaçamento em pixels ao redor dos marcadores
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

            // Movimenta a câmera para mostrar todos os marcadores
            googleMap.moveCamera(cu);
        }
    }
}
