package com.example.projetomovimenta;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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
    private TextView academiaAverageRating;
    private View fragmentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.localizacaofragment, container, false);
        fragmentView = view;

        // Inicializar o AutoCompleteTextView
        academiaAutoComplete = view.findViewById(R.id.autocompletepesquisa);
        academiaAverageRating = view.findViewById(R.id.academia_average_rating);

        academias = readAcademiasFromCSV();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        setupAcademiaAutoComplete();

        return view;
    }

    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        LatLng beloHorizonte = new LatLng(-19.9167, -43.9345);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(beloHorizonte, 12));
        showAllAcademiasOnMap();
        for (Academia academia : academias) {
            LatLng location = new LatLng(academia.getLatitude(), academia.getLongitude());

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(location)
                    .title(academia.getNome())
                    .snippet("Latitude: " + academia.getLatitude() + ", Longitude: " + academia.getLongitude());

            Marker marker = googleMap.addMarker(markerOptions);
            marker.setTag(academia);
        }

        googleMap.setOnMarkerClickListener(marker -> {
            if (marker.getTag() instanceof Academia) {
                showInfoWindowWithRatingBar(marker);
                return true;
            }
            return false;
        });
    }


    private float calcularMedia(ArrayList<Float> avaliacoes) {
        if (avaliacoes.isEmpty()) {
            return 0.0f;
        }

        float total = 0;
        for (Float avaliacao : avaliacoes) {
            total += avaliacao;
        }
        return total / avaliacoes.size();
    }
    private void iniciarNavegacao(LatLng localizacaoAcademia) {
        // Cria a URI para abrir o Google Maps para a localização da academia
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + localizacaoAcademia.latitude + "," + localizacaoAcademia.longitude);

        // Cria um novo intent para abrir o Google Maps
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

        // Define o pacote do Google Maps para o Intent
        mapIntent.setPackage("com.google.android.apps.maps");

        // Verifica se o dispositivo tem o Google Maps instalado e pode lidar com o Intent
        if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Inicia a atividade usando o Intent
            startActivity(mapIntent);
        } else {
            // Se o Google Maps não estiver instalado, mostra uma mensagem de erro ou oferece uma alternativa
            Toast.makeText(requireContext(), "Google Maps não está instalado!", Toast.LENGTH_SHORT).show();
        }
    }
    private void showAllAcademiasOnMap() {
        if (googleMap != null && !academias.isEmpty()) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            for (Academia academia : academias) {
                try {
                    double latitude = academia.getLatitude();
                    double longitude = academia.getLongitude();
                    LatLng location = new LatLng(latitude, longitude);

                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(location)
                            .title(academia.getNome())
                            .snippet("Latitude: " + academia.getLatitude() + ", Longitude: " + academia.getLongitude());

                    Marker marker = googleMap.addMarker(markerOptions);
                    marker.setTag(academia);

                    builder.include(location); // Inclui a localização do marcador na área a ser exibida
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            LatLngBounds bounds = builder.build();
            int padding = 200; // Espaçamento em pixels ao redor dos marcadores
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

            googleMap.moveCamera(cu);
        }
    }


    private void showInfoWindowWithRatingBar(Marker marker) {
        if (marker.getTag() instanceof Academia) {
            Academia academia = (Academia) marker.getTag();

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            LayoutInflater inflater = requireActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.academia_details_view, null);
            builder.setView(view);

            TextView academiaName = view.findViewById(R.id.academia_name);
            TextView academiaAddress = view.findViewById(R.id.academia_address);
            RatingBar academiaRating = view.findViewById(R.id.academia_rating);
            TextView academiaAverageRating = view.findViewById(R.id.academia_average_rating);
            Button tracarRotaButton = view.findViewById(R.id.btn_tracar_rota); // Botão para traçar a rota

            float media = calcularMedia(academia.carregarAvaliacoes(requireContext()));
            academiaAverageRating.setText("Média de Avaliações: " + media);

            academiaName.setText(academia.getNome());
            academiaAddress.setText("Latitude: " + academia.getLatitude() + ", Longitude: " + academia.getLongitude());

            academiaRating.setRating(0);
            academiaRating.setIsIndicator(false);

            academiaRating.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
                ArrayList<Float> avaliacoes = academia.carregarAvaliacoes(requireContext());
                avaliacoes.add(rating);
                academia.salvarAvaliacoes(requireContext(), avaliacoes);

                float novaMedia = calcularMedia(avaliacoes);
                academiaAverageRating.setText("Média de Avaliações: " + novaMedia);

                academiaRating.setIsIndicator(true);
            });

            tracarRotaButton.setOnClickListener(v -> iniciarNavegacao(marker.getPosition())); // Inicia a navegação ao clicar no botão

            AlertDialog dialog = builder.create();
            dialog.show();
        }
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
                    double latitude = Double.parseDouble(parts[1]);
                    double longitude = Double.parseDouble(parts[2]);
                    Academia academia = new Academia(nome, latitude, longitude, new ArrayList<>());
                    academias.add(academia);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return academias;
    }

    private void setupAcademiaAutoComplete() {
        if (academiaAutoComplete != null) {
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

                        Marker marker1 = googleMap.addMarker(markerOptions);
                        marker1.setTag(academia);

                        googleMap.setOnMarkerClickListener(marker -> {
                            if (marker.getTag() instanceof Academia) {
                                showInfoWindowWithRatingBar(marker);
                                return true;
                            }
                            return false;
                        });
                        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(location, 18);
                        googleMap.moveCamera(cu);
                        break;
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}