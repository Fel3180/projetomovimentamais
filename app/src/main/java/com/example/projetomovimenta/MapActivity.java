package com.example.projetomovimenta;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.IOException;
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

        if (hasLocationPermission()) {
            // Você já tem permissão, pode acessar a localização aqui.
        } else {
            // Solicite permissão se ela ainda não foi concedida.
            requestLocationPermission();
        }

        // Inicialize a lista de academias a partir do arquivo CSV em res/raw
        academias = readAcademiasFromCSV();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            } else {
                // Permissão negada. Lide com o caso de permissão negada, se necessário.
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        // Verifique se você tem permissão para acessar a localização do usuário
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//
//        googleMap.setMyLocationEnabled(true);
//
//        FusedLocationProviderClient locationProviderClient = LocationServices.getFusedLocationProviderClient(this);

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
            } catch (NumberFormatException e) {
                // Lidar com valores de latitude/longitude inválidos
                Log.e("NumberFormatException", "Valores de latitude/longitude inválidos para academia: " + academia.getNome());
            }
        }

        // Aproxime o mapa para exibir todos os marcadores
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        double latitude = Double.parseDouble(academias.get(0).getLocalizacao().getLatitude());
        double longitude = Double.parseDouble(academias.get(0).getLocalizacao().getLongitude());

        LatLng location = new LatLng(latitude, longitude);
        builder.include(location);

        LatLng defaultLocation = getLocationFromCityName("Belo Horizonte");

        FusedLocationProviderClient locationProviderClient = LocationServices.getFusedLocationProviderClient(this);


//        if (defaultLocation != null) {
//            // Move the camera to the default location and add a marker
//            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 11)); // You can adjust the zoom level
//            googleMap.addMarker(new MarkerOptions().position(defaultLocation).title("Belo Horizonte"));
//        }

//        LatLngBounds bounds = generateLatLngBounds();
//        if (builder != null) {
//            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
//        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationProviderClient.getLastLocation().addOnSuccessListener(this, locationUser -> {
            if (locationUser != null) {
                LatLng userLocation = new LatLng(locationUser.getLatitude(), locationUser.getLongitude());
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 11));
                googleMap.addMarker(new MarkerOptions().position(userLocation).title("Minha Localização"));
            }
        });
    }

    private ArrayList<Academia> readAcademiasFromCSV() {
        academias = new ArrayList<>();

        try {
            // Abra o arquivo CSV a partir de res/raw
            InputStream inputStream = getResources().openRawResource(R.raw.academias);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line = reader.readLine();

            while (line != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String nome = parts[0];
                    Academia academia = new Academia(nome, new Localizacao(parts[1], parts[2]));
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
        boolean atleastOnePoint = false;

        for (Academia academia : academias) {
            try {
                double latitude = Double.parseDouble(academia.getLocalizacao().getLatitude());
                double longitude = Double.parseDouble(academia.getLocalizacao().getLongitude());

                LatLng location = new LatLng(latitude, longitude);
                builder.include(location);
                atleastOnePoint = true;
            } catch (NumberFormatException e) {
                // Lidar com valores de latitude/longitude inválidos
                Log.e("NumberFormatException", "Valores de latitude/longitude inválidos para academia: " + academia.getNome());
            }
        }

        if (atleastOnePoint) {
            return builder.build();
        } else {
            // Lide com a situação em que não há pontos válidos
            Log.e("No Included Points", "Não foi possível criar limites de mapa devido à falta de pontos válidos.");
            return null;
        }
    }

    private LatLng getLocationFromCityName(String cityName) {
        Geocoder geocoder = new Geocoder(this);
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocationName(cityName, 1);
            assert addresses != null;
            if (!addresses.isEmpty()) {
                double latitude = addresses.get(0).getLatitude();
                double longitude = addresses.get(0).getLongitude();
                return new LatLng(latitude, longitude);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
