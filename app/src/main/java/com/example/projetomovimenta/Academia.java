package com.example.projetomovimenta;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Academia implements Parcelable {
    private String nome;
    private double latitude;
    private double longitude;
    private ArrayList<Float> avaliacoes; // ArrayList para armazenar as avaliações

    public Academia(String nome, double latitude, double longitude, ArrayList<Float> avaliacoes) {
        this.nome = nome;
        this.latitude = latitude;
        this.longitude = longitude;
        this.avaliacoes = avaliacoes;
}

    // Getters para os atributos
    public String getNome() {
        return nome;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    // Método para salvar as avaliações no armazenamento local (arquivo de texto)
    public void salvarAvaliacoes(Context context, ArrayList<Float> avaliacoes) {
        String filename = "avaliacoes_" + nome + ".txt";
        try {
            FileOutputStream fileOutputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            for (Float avaliacao : avaliacoes) {
                fileOutputStream.write((avaliacao + "\n").getBytes());
            }
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para carregar as avaliações do armazenamento local (arquivo de texto)
    public ArrayList<Float> carregarAvaliacoes(Context context) {
        ArrayList<Float> avaliacoes = new ArrayList<>();
        String filename = "avaliacoes_" + nome + ".txt";
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.openFileInput(filename)));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                float avaliacao = Float.parseFloat(line);
                avaliacoes.add(avaliacao);
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return avaliacoes;
    }

    // Implementação dos métodos da interface Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nome);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }

    protected Academia(Parcel in) {
        nome = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public static final Parcelable.Creator<Academia> CREATOR = new Parcelable.Creator<Academia>() {
        @Override
        public Academia createFromParcel(Parcel in) {
            return new Academia(in);
        }

        @Override
        public Academia[] newArray(int size) {
            return new Academia[size];
        }
    };
}
