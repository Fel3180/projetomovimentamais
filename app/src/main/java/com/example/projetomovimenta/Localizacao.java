package com.example.projetomovimenta;

import android.os.Parcel;
import android.os.Parcelable;

public class Localizacao implements Parcelable {
    private String latitude;
    private String longitude;

    public Localizacao(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    // Implementação da interface Parcelable
    protected Localizacao(Parcel in) {
        latitude = in.readString();
        longitude = in.readString();
    }

    public static final Creator<Localizacao> CREATOR = new Creator<Localizacao>() {
        @Override
        public Localizacao createFromParcel(Parcel in) {
            return new Localizacao(in);
        }

        @Override
        public Localizacao[] newArray(int size) {
            return new Localizacao[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(latitude);
        dest.writeString(longitude);
    }
}
