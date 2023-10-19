package com.example.projetomovimenta;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LocalizacaoFragment extends Fragment {
    public LocalizacaoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.localizacaofragment, container, false);

        Button btnLocation = view.findViewById(R.id.btn_location);
        btnLocation.setOnClickListener(v -> openLocationOnMap());

        return view;
    }

    private void openLocationOnMap() {
        // Primeiro, obtenha o recurso do arquivo CSV das academias
        try {
            InputStream is = getResources().openRawResource(R.raw.academias);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                // Processa cada linha do arquivo CSV com informações das academias
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String nomeAcademia = parts[0];
                    String regional = parts[1];
                    String endereco = parts[2];

                    // Exibe os dados da academia no log
                    Log.d("Dados Academia", "Nome: " + nomeAcademia + " Regional: " + regional + " Endereço: " + endereco);
                }
            }
            is.close(); // Fecha o InputStream após a leitura
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Agora continue com o código para abrir o Google Maps ou o navegador
        Uri uri = Uri.parse("geo:-19.912998,-43.940933,z=12");
        Intent locationIntent = new Intent(Intent.ACTION_VIEW, uri);
        locationIntent.setPackage("com.google.android.apps.maps");

        if (locationIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            // O Google Maps está instalado; abra-o
            startActivity(locationIntent);
        } else {
            // O Google Maps não está instalado; forneça uma alternativa
            Toast.makeText(requireContext(), "O Google Maps não está instalado. Abrindo no navegador.", Toast.LENGTH_SHORT).show();

            // Abra a localização no navegador
            locationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps?q=-19.912998,-43.940933"));
            startActivity(locationIntent);
        }
    }
}
