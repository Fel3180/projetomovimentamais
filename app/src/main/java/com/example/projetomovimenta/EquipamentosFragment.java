package com.example.projetomovimenta;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class EquipamentosFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.equipamentosfragment, container, false);

        // Array de URLs de vídeos do YouTube
        final String[] videoUrls = {
                "https://www.youtube.com/watch?v=gJmMyaANhbY",
                "https://www.youtube.com/watch?v=3L6F0G2XTH0",
                "https://www.youtube.com/watch?v=gUwVnhMv0jM&list=PLkonCRbZ6HCcbsFS7I4ja7h3LoRlj-94C&index=2",
                "https://www.youtube.com/watch?v=xQeVCpYfC0k&list=PLkonCRbZ6HCcbsFS7I4ja7h3LoRlj-94C&index=4",
                "https://www.youtube.com/watch?v=Er7_ABcvl6Q&list=PLkonCRbZ6HCcbsFS7I4ja7h3LoRlj-94C&index=8",
                "https://www.youtube.com/watch?v=0eaElNme_ss&list=PLkonCRbZ6HCcbsFS7I4ja7h3LoRlj-94C&index=12",
                "https://www.youtube.com/watch?v=Baah3_M2bEc",
                "https://www.youtube.com/watch?v=sAOaqoiDLuA"
        };

        // Array de IDs de botões correspondentes
        int[] buttonIds = {
                R.id.btnWatchVideo1,
                R.id.btnWatchVideo2,
                R.id.btnWatchVideo3,
                R.id.btnWatchVideo4,
                R.id.btnWatchVideo5,
                R.id.btnWatchVideo6,
                R.id.btnWatchVideo7,
                R.id.btnWatchVideo8
        };

        for (int i = 0; i < videoUrls.length; i++) {
            final int index = i;
            Button btnWatchVideo = view.findViewById(buttonIds[i]);
            btnWatchVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Crie um Intent para abrir a URL do vídeo do YouTube
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrls[index]));

                    // Inicie o aplicativo do YouTube ou o navegador da web, se o YouTube não estiver instalado
                    intent.setPackage("com.google.android.youtube");
                    startActivity(intent);
                }
            });
        }

        return view;
    }
}