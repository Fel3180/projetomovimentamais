package com.example.projetomovimenta;

import android.os.Parcel;
import android.os.Parcelable;

public class Academia implements Parcelable {
    private String nome;
    private double latitude;
    private double longitude;
    private float avaliacao;

    public Academia(String nome, double latitude, double longitude, float avaliacao) {
        this.nome = nome;
        this.latitude = latitude;
        this.longitude = longitude;
        this.avaliacao = avaliacao;
    }

    protected Academia(Parcel in) {
        nome = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        avaliacao = in.readFloat();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nome);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeFloat(avaliacao);
    }

    @Override
    public int describeContents() {
        return 0;
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

    public String getNome() {
        return nome;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public float getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(float avaliacao) {
        this.avaliacao = avaliacao;
    }
}
