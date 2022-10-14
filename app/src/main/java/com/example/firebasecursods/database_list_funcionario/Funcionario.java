package com.example.firebasecursods.database_list_funcionario;

import android.os.Parcel;
import android.os.Parcelable;

public class Funcionario implements Parcelable {

    private String  id, nome, urlImagem, id_empresa;
    private int idade;

    public Funcionario(){

    }

    public Funcionario(String id, String nome, int idade, String urlImagem) {
        this.id = id;
        this.nome = nome;
        this.idade = idade;
        this.urlImagem = urlImagem;
    }

    public Funcionario(String nome, int idade, String urlImagem) {
        this.nome = nome;
        this.idade = idade;
        this.urlImagem = urlImagem;
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

    public String getUrlImagem() {
        return urlImagem;
    }

    public void setUrlImagem(String urlImagem) {
        this.urlImagem = urlImagem;
    }

    public String getId_empresa() {
        return id_empresa;
    }

    public void setId_empresa(String id_empresa) {
        this.id_empresa = id_empresa;
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        this.idade = idade;
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
        dest.writeString(this.urlImagem);
        dest.writeString(this.id_empresa);
        dest.writeInt(this.idade);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readString();
        this.nome = source.readString();
        this.urlImagem = source.readString();
        this.id_empresa = source.readString();
        this.idade = source.readInt();
    }

    protected Funcionario(Parcel in) {
        this.id = in.readString();
        this.nome = in.readString();
        this.urlImagem = in.readString();
        this.id_empresa = in.readString();
        this.idade = in.readInt();
    }

    public static final Creator<Funcionario> CREATOR = new Creator<Funcionario>() {
        @Override
        public Funcionario createFromParcel(Parcel source) {
            return new Funcionario(source);
        }

        @Override
        public Funcionario[] newArray(int size) {
            return new Funcionario[size];
        }
    };
}