package com.example.projetomovimenta;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

public class DicasFragment extends Fragment {
    public DicasFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dicasfragment, container, false);

        // Encontre o botão do blog no layout
        Button openBlogButton = rootView.findViewById(R.id.idBtnBlog);

        // Defina um listener de clique para o botão do blog
        openBlogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Verifique se o botão do blog foi clicado
                if (view.getId() == R.id.idBtnBlog) {
                    // Crie um Intent para abrir o link do blog
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://blog.nutrify.com.br/category/saude-e-bem-estar"));
                    startActivity(browserIntent);
                }
            }
        });

        // Encontre o botão de assistir ao vídeo no layout
        Button watchVideoButton = rootView.findViewById(R.id.btnWatchVideo1);

        // Defina um listener de clique para o botão de assistir ao vídeo
        watchVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Verifique se o botão de assistir ao vídeo foi clicado
                if (view.getId() == R.id.btnWatchVideo1) {
                    // Crie um Intent para abrir o link do vídeo
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=dx79ZCvfGe4"));
                    startActivity(browserIntent);
                }
            }
        });

        return rootView;
    }
}
