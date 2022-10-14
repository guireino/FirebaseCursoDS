package com.example.firebasecursods.database_list_empresa;

import android.os.Parcel;
import android.os.Parcelable;

public class Empresa implements Parcelable {

    private String id, nome;

    public Empresa(){

    }

    public Empresa(String id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    // gerando para implements Parcelable

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.nome);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readString();
        this.nome = source.readString();
    }

    protected Empresa(Parcel in) {
        this.id = in.readString();
        this.nome = in.readString();
    }

    public static final Creator<Empresa> CREATOR = new Creator<Empresa>() {
        @Override
        public Empresa createFromParcel(Parcel source) {
            return new Empresa(source);
        }

        @Override
        public Empresa[] newArray(int size) {
            return new Empresa[size];
        }
    };
}