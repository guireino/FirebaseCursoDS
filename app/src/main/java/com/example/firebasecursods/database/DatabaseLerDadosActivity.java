package com.example.firebasecursods.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.firebasecursods.R;
import com.example.firebasecursods.util.DialogAlerta;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DatabaseLerDadosActivity extends AppCompatActivity {

    private TextView txtView_Nome, txtView_Idade, txtView_Fumante;

    private TextView txtView_Nome_2, txtView_Idade_2, txtView_Fumante_2;

    private FirebaseDatabase database;

    private DatabaseReference reference_database;
    private ValueEventListener valueEventListener;
    private ChildEventListener childEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database_ler_dados_activity);

        txtView_Nome = (TextView) findViewById(R.id.textView_Database_LerDados_Nome);
        txtView_Idade = (TextView) findViewById(R.id.textView_Database_LerDados_Idade);
        txtView_Fumante = (TextView) findViewById(R.id.textView_Database_LerDados_Fumante);

        txtView_Nome_2 = (TextView) findViewById(R.id.textView_Database_LerDados_Nome_2);
        txtView_Idade_2 = (TextView) findViewById(R.id.textView_Database_LerDados_Idade_2);
        txtView_Fumante_2 = (TextView) findViewById(R.id.textView_Database_LerDados_Fumante_2);

        database = FirebaseDatabase.getInstance();

        //ouvinte_1();
        //ouvinte_2();
        //ouvinte_3();
        //ouvinte_4();
        //ouvinte_5();
    }

    // ====================> Testando varias formas de buscar informacao no banco de dados no firebase <====================

    private void ouvinte_1(){

        // buscando no banco de dados do firebase a pastas
        DatabaseReference reference = database.getReference().child("BD").child("Gerentes").child("0");

        reference.addListenerForSingleValueEvent(new ValueEventListener() { // acesando dados do banco de dados

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String nome = snapshot.child("nome").getValue(String.class);
                int idade = snapshot.child("idade").getValue(int.class);

                List<String> list = new ArrayList<String>();

                for (DataSnapshot data: snapshot.child("contatos").getChildren()){

                    String value = data.getValue(String.class);
                    //String _value = data.child("numero").getValue(String.class);

                    list.add(value);
                }

                String values = list.get(0) + " -- " + list.get(1) + " -- " + list.get(2);

                if(snapshot.child("fumante").exists()){ // verificando se tem valor fumante
                    boolean fumante = snapshot.child("fumante").getValue(boolean.class);

                    DialogAlerta dialog = new DialogAlerta("Valor",
                            nome + "\n" + idade + "\n" + fumante + "\n" + values);

                    dialog.show(getSupportFragmentManager(), "1");
                }else{
                    DialogAlerta dialog = new DialogAlerta("Valor", nome + "\n" + idade);
                    dialog.show(getSupportFragmentManager(), "1");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ouvinte_2(){

        // buscando no banco de dados do firebase a pastas
        DatabaseReference reference = database.getReference().child("BD").child("Gerentes");

        reference.addListenerForSingleValueEvent(new ValueEventListener() { // acesando dados do banco de dados

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                List<String> nomes = new ArrayList<String>();
                List<Integer> idades = new ArrayList<Integer>();
                List<Boolean> fumantes = new ArrayList<Boolean>();

                for(DataSnapshot data: snapshot.getChildren()){

                    String nome = data.child("nome").getValue(String.class);
                    int idade = data.child("idade").getValue(int.class);
                    Boolean fumante = data.child("fumante").getValue(boolean.class);

                    nomes.add(nome);
                    idades.add(idade);
                    fumantes.add(fumante);
                }

                txtView_Nome.setText(nomes.get(0));
                txtView_Idade.setText(idades.get(0) + "");
                // essa tipo de informacao boolean nao pode ser inserida direto tem que outra variavel string vacia
                txtView_Fumante.setText(fumantes.get(0) + "");

                txtView_Nome_2.setText(nomes.get(1));
                txtView_Idade_2.setText(idades.get(1) + "");
                txtView_Fumante_2.setText(fumantes.get(1) + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ouvinte_3(){

        // buscando no banco de dados do firebase a pastas
        DatabaseReference reference = database.getReference().child("BD").child("Gerentes");

        reference.addListenerForSingleValueEvent(new ValueEventListener() { // acesando dados do banco de dados apenas um vez

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                List<Gerente> gerentes = new ArrayList<Gerente>();

                for(DataSnapshot data: snapshot.getChildren()){

                    Gerente gerente = data.getValue(Gerente.class);
                    gerentes.add(gerente);
                }

                txtView_Nome.setText(gerentes.get(0).getNome());
                txtView_Idade.setText(gerentes.get(0).getIdade() + "");
                // essa tipo de informacao boolean nao pode ser inserida direto tem que outra variavel string vacia
                txtView_Fumante.setText(gerentes.get(0).isFumante() + "");

                txtView_Nome_2.setText(gerentes.get(1).getNome());
                txtView_Idade_2.setText(gerentes.get(1).getIdade() + "");
                txtView_Fumante_2.setText(gerentes.get(1).isFumante() + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ouvinte_4(){

        // buscando no banco de dados do firebase a pastas
        DatabaseReference reference = database.getReference().child("BD").child("Gerentes");

        reference.addValueEventListener(new ValueEventListener() { // acesando dados do banco de dados e atualizando dados banco em tempo real

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                List<Gerente> gerentes = new ArrayList<Gerente>();

                for(DataSnapshot data: snapshot.getChildren()){

                    Gerente gerente = data.getValue(Gerente.class);
                    gerentes.add(gerente);
                }

                txtView_Nome.setText(gerentes.get(0).getNome());
                txtView_Idade.setText(gerentes.get(0).getIdade() + "");
                // essa tipo de informacao boolean nao pode ser inserida direto tem que outra variavel string vacia
                txtView_Fumante.setText(gerentes.get(0).isFumante() + "");

                txtView_Nome_2.setText(gerentes.get(1).getNome());
                txtView_Idade_2.setText(gerentes.get(1).getIdade() + "");
                txtView_Fumante_2.setText(gerentes.get(1).isFumante() + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ouvinte_5(){

        // buscando no banco de dados do firebase a pastas
        reference_database = database.getReference().child("BD").child("Gerentes");

        valueEventListener = new ValueEventListener() { // acesando dados do banco de dados e atualizando dados banco em tempo real

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                List<Gerente> gerentes = new ArrayList<Gerente>();

                for(DataSnapshot data: snapshot.getChildren()){

                    Gerente gerente = data.getValue(Gerente.class);

                    // log mostra esta maneira buscar dados no banco de dados nao e boa ideia,
                    // porque ele quando voce modifica um valor no banco de dados ele carrega todos valores banco
                    // e isso pode sobre carregar banco do firebase
                    Log.i("testeOuvinte_5", gerente.getNome() + "");

                    gerentes.add(gerente);
                }

                txtView_Nome.setText(gerentes.get(0).getNome());
                txtView_Idade.setText(gerentes.get(0).getIdade() + "");
                // essa tipo de informacao boolean nao pode ser inserida direto tem que outra variavel string vacia
                txtView_Fumante.setText(gerentes.get(0).isFumante() + "");

                txtView_Nome_2.setText(gerentes.get(1).getNome());
                txtView_Idade_2.setText(gerentes.get(1).getIdade() + "");
                txtView_Fumante_2.setText(gerentes.get(1).isFumante() + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        reference_database.addValueEventListener(valueEventListener);
    }

    private void ouvinte_6(){

        // buscando no banco de dados do firebase a pastas
        reference_database = database.getReference().child("BD").child("Gerentes");

        childEventListener = new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Gerente gerente = snapshot.getValue(Gerente.class);

                String key = snapshot.getKey(); // key significa nome da pasta do banco de dados do firebase

                Log.i("testOuvinte_6_added", gerente.getNome() + " pasta: " + key);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                String key = snapshot.getKey(); // key significa nome da pasta do banco de dados do firebase
                Log.i("testOuvinte_6_changed"," pasta: " + key);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String key = snapshot.getKey(); // key significa nome da pasta do banco de dados do firebase
                Log.i("testOuvinte_6_removed"," pasta: " + key);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        reference_database.addChildEventListener(childEventListener);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //ouvinte_5();
        ouvinte_6();
    }

    @Override
    protected void onStop() {
        super.onStop();

        /*
        if(valueEventListener != null){
            reference_database.removeEventListener(valueEventListener);
        }
         */

        if(childEventListener != null){
            reference_database.removeEventListener(childEventListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        /*
        if(valueEventListener != null){
            reference_database.removeEventListener(valueEventListener);
        }
         */

        if(childEventListener != null){
            reference_database.removeEventListener(childEventListener);
        }
    }
}