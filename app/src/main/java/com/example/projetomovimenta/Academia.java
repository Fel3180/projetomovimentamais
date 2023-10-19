package com.example.projetomovimenta;

import android.os.Parcel;
import android.os.Parcelable;

public class Academia implements Parcelable {
    private String nome;
    private String regional;
    private String endereco;

    public Academia(String nome, String regional, String endereco) {
        this.nome = nome;
        this.regional = regional;
        this.endereco = endereco;
    }

    public String getNome() {
        return nome;
    }

    public String getRegional() {
        return regional;
    }

    public String getEndereco() {
        return endereco;
    }

    // Implementação da interface Parcelable
    protected Academia(Parcel in) {
        nome = in.readString();
        regional = in.readString();
        endereco = in.readString();
    }

    public static final Creator<Academia> CREATOR = new Creator<Academia>() {
        @Override
        public Academia createFromParcel(Parcel in) {
            return new Academia(in);
        }

        @Override
        public Academia[] newArray(int size) {
            return new Academia[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nome);
        dest.writeString(regional);
        dest.writeString(endereco);
    }
}
