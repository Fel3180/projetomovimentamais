package com.example.projetomovimenta;

import android.os.Parcel;
import android.os.Parcelable;

public class Academia implements Parcelable {
    private String nome;
    private Localizacao localizacao;

    public Academia(String nome, Localizacao localizacao) {
        this.nome = nome;
        this.localizacao = localizacao;
    }

    public String getNome() {
        return nome;
    }

    public Localizacao getLocalizacao() {
        return localizacao;
    }

    // Implementação da interface Parcelable
    protected Academia(Parcel in) {
        nome = in.readString();
        localizacao = in.readParcelable(Localizacao.class.getClassLoader());
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
        dest.writeParcelable(localizacao, flags);
    }
}
