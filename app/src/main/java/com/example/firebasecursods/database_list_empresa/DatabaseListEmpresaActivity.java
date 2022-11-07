package com.example.firebasecursods.database_list_empresa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.firebasecursods.R;
import com.example.firebasecursods.database.Gerente;
import com.example.firebasecursods.database_list_funcionario.DatabaseListFuncionarioActivity;
import com.example.firebasecursods.util.Util;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DatabaseListEmpresaActivity extends AppCompatActivity implements RecyclerView_listEmpresa.ClickEmpresa, Runnable{

    private RecyclerView recyclerView;
    private FirebaseDatabase database;

    private RecyclerView_listEmpresa recyclerView_listEmpresa;

    private List<Empresa> empresas = new ArrayList<Empresa>();

    private List<String> keys = new ArrayList<String>();

    private ChildEventListener childEventListener;

    private DatabaseReference reference;

    private Handler handler;
    private Thread thread;

    private boolean firebaseOffline = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database_list_empresa_activity);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_Empresa_List);

        database = FirebaseDatabase.getInstance();

        //conexaoFirebaseBD();

        handler = new Handler();

        thread = new Thread(this);
        thread.start();

        ativarFirebaseOffline();
        startingRecyclerView();
    }

    private void ativarFirebaseOffline(){

        try {

            if (!firebaseOffline){ // verificando se firebase esta false
                FirebaseDatabase.getInstance().setPersistenceEnabled(true);

                firebaseOffline = true;

            }else{ // firebase ja estiver funcionando offline

            }

        }catch (Exception e){ // erro

        }
    }

    @Override
    public void run() {

        try {

            Thread.sleep(10000);  // espera um 10 seconds

            handler.post(new Runnable() {
                @Override
                public void run() {
                    conexaoFirebaseBD();
                }
            });

        }catch (InterruptedException e){

        }

    }

    private void conexaoFirebaseBD(){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(".info/connected");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                boolean conexao = snapshot.getValue(Boolean.class);

                if (conexao){
                    Toast.makeText(getBaseContext(), "Temos conexao com o BD ", Toast.LENGTH_LONG).show();
                }else{

                    if(Util.statusInternet(getBaseContext())){
                        Toast.makeText(getBaseContext(), "Bloqueio ao nosso BD ", Toast.LENGTH_LONG).show();
                    }else{
                        //Toast.makeText(getBaseContext(), "sem conexao com a internet ", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void startingRecyclerView() {

        //Empresa empresa0 = new Empresa("0", "Coca-Cola");
        //Empresa empresa1 = new Empresa("1", "Pepsi");

        //empresas.add(empresa0);
        //empresas.add(empresa1);

        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // iniciando list das empresas
        recyclerView_listEmpresa = new RecyclerView_listEmpresa(getBaseContext(), empresas, this);
        recyclerView.setAdapter(recyclerView_listEmpresa);
    }

    @Override
    public void clickEmpresa(Empresa empresa) {

        Intent intent = new Intent(getBaseContext(), DatabaseListFuncionarioActivity.class);
        intent.putExtra("empresa", empresa);

        System.out.println("clickEmpresa: " + "Nome: " + empresa.getNome() + "\n\nPasta: " + empresa.getId());

        startActivity(intent);
        //Toast.makeText(getBaseContext(), "Nome: " + empresa.getNome() + "\n\nPasta: " + empresa.getId(), Toast.LENGTH_LONG).show();
    }

    private void ouvinte(){

        // buscando no banco de dados do firebase a pastas
        reference = database.getReference().child("BD").child("BD").child("Empresas");

        if(childEventListener == null){

            childEventListener = new ChildEventListener() {

                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    String key = snapshot.getKey();

                    keys.add(key);

                    Empresa empresa = snapshot.getValue(Empresa.class);

                    empresa.setId(key); // pegando id da pasta

                    empresas.add(empresa);

                    recyclerView_listEmpresa.notifyDataSetChanged();

                    // keys 0 = 0
                    //empresas 0 = coca cola
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    String key = snapshot.getKey();

                    int index = keys.indexOf(key);  // index e para localizacao id da pasta

                    Empresa empresa = snapshot.getValue(Empresa.class);
                    empresa.setId(key);

                    empresas.set(index, empresa);

                    recyclerView_listEmpresa.notifyDataSetChanged(); // atualizando banco de dados firebase
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    String key = snapshot.getKey();

                    int index = keys.indexOf(key);

                    empresas.remove(index);

                    keys.remove(index);

                    recyclerView_listEmpresa.notifyItemRemoved(index); // atualizando banco de dados firebase
                    recyclerView_listEmpresa.notifyItemChanged(index, empresas.size());
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };

            reference.addChildEventListener(childEventListener);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        ouvinte();
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//
//        if(childEventListener != null){
//            reference.removeEventListener(childEventListener);
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(childEventListener != null){
            reference.removeEventListener(childEventListener);
        }
    }
}