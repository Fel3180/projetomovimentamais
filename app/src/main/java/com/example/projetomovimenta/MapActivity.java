package com.example.projetomovimenta;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap googleMap;
    private ArrayList<Academia> academias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Inicialize a lista de academias a partir do arquivo CSV em res/raw
        readAcademiasFromCSV();

        // Inicialize o mapa (você deve configurar o layout XML correspondente)
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        // Adicione marcadores das academias ao mapa
        for (Academia academia : academias) {
            LatLng location = getLocationFromAddress(academia.getEndereco());

            if (location != null) {
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(location)
                        .title(academia.getNome())
                        .snippet(academia.getRegional());

                googleMap.addMarker(markerOptions);
            }
        }

        // Aproxime o mapa para exibir todos os marcadores
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(generateLatLngBounds(), 50));
    }

    // Implemente um método para converter um endereço em coordenadas de latitude e longitude
    private LatLng getLocationFromAddress(String strAddress) {
        Geocoder coder = new Geocoder(this);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null || address.isEmpty()) {
                return null;
            }
            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return p1;
    }

    private ArrayList<Academia> readAcademiasFromCSV() {
        academias = new ArrayList<>();

        try {
            // Abra o arquivo CSV a partir de res/raw
            InputStream inputStream = getResources().openRawResource(R.raw.academias);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line = reader.readLine();

            while (line != null) {
                String[] parts = line.split(";");
                if (parts.length == 4) {
                    String nome = parts[0];
                    String regional = parts[1];
                    String endereco = parts[2];
                    Academia academia = new Academia(nome, regional, endereco);
                    academias.add(academia);
                }
                line = reader.readLine();
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return academias;
    }

    private LatLngBounds generateLatLngBounds() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Academia academia : academias) {
            LatLng location = getLocationFromAddress(academia.getEndereco());
            if (location != null) {
                builder.include(location);
            }
        }

        // Verifique se há pelo menos um ponto válido antes de criar os limites
        if (builder.build().getCenter() != null) {
            return builder.build();
        } else {
            // Lide com a situação em que não há pontos válidos
            Log.e("No Included Points", "Não foi possível criar limites de mapa devido à falta de pontos válidos.");
            return null;
        }

    }
}
