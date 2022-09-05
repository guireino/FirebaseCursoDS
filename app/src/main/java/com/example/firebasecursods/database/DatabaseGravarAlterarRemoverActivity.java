package com.example.firebasecursods.database;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.firebasecursods.R;
import com.example.firebasecursods.util.DialogAlerta;
import com.example.firebasecursods.util.DialogProgress;
import com.example.firebasecursods.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class DatabaseGravarAlterarRemoverActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edtxt_nomePasta, edtxt_nome, edtxt_idade;

    private Button btn_salvar, btn_altera, btn_remover;

    private FirebaseDatabase database;

    private DialogProgress progress;

    private boolean firebaseOffline = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database_gravar_alterar_remover_activity);

        edtxt_nomePasta = (EditText) findViewById(R.id.edtxt_nomePasta);
        edtxt_nome = (EditText) findViewById(R.id.edtxt_nome);
        edtxt_idade = (EditText) findViewById(R.id.edtxt_idade);

        btn_salvar = (Button) findViewById(R.id.btn_Database_salvar);
        btn_altera = (Button) findViewById(R.id.btn_Database_altera);
        btn_remover = (Button) findViewById(R.id.btn_Database_remover);

        btn_salvar.setOnClickListener(this);
        btn_altera.setOnClickListener(this);
        btn_remover.setOnClickListener(this);

        database = FirebaseDatabase.getInstance();

        // metado tem que chamado antes instancia firebase por que pode dar erro e app fechar inesperado
        //ativarFirebaseOffline();
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
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btn_Database_salvar:
                btn_salvar();
                //Toast.makeText(getBaseContext(), "btn_Database_salvar", Toast.LENGTH_LONG).show();
            break;

            case R.id.btn_Database_altera:
                //Toast.makeText(getBaseContext(), "btn_Database_altera", Toast.LENGTH_LONG).show();
                //alterarBD();
                btn_alterar();
            break;

            case R.id.btn_Database_remover:
                //Toast.makeText(getBaseContext(), "btn_Database_remover", Toast.LENGTH_LONG).show();
                btn_remover();
                //removerData();
            break;
        }

    }

    private void btn_salvar() {

        String nome = edtxt_nome.getText().toString();
        String idadeString = edtxt_idade.getText().toString();

        if(Util.ifFields(getBaseContext(), nome, idadeString)){
            int idade = Integer.parseInt(idadeString);
            saveData(nome, idade);
        }
    }

    private void btn_alterar() {

        String nome = edtxt_nome.getText().toString();
        String idadeString = edtxt_idade.getText().toString();

        if(Util.ifFields(getBaseContext(), nome, idadeString)){
            int idade = Integer.parseInt(idadeString);
            alterarBD(nome, idade);
        }
    }

    private void btn_remover() {

        String nomePasta = edtxt_nomePasta.getText().toString();

        if(!nomePasta.isEmpty()){
            removerData(nomePasta);
        }else{
            Toast.makeText(getBaseContext(), "Preencha o campo com o Pasta que você quer remover", Toast.LENGTH_LONG).show();
        }
    }


    private void saveData(String nome, int idade) {

        progress = new DialogProgress();
        progress.show(getSupportFragmentManager(), "1");

        DatabaseReference reference = database.getReference().child("BD").child("Gerentes");

        //Map<String, Object> velue = new HashMap<>(); // inserindo dados no bando do firebase

//       velue.put("nome", "Guilhemre");
//       velue.put("idade", 28);
//       velue.put("fumante", false);

        Gerente gerente = new Gerente(nome, idade, false);

        // metado push vai gerar id para banco de dados
        reference.push().setValue(gerente).addOnCompleteListener(new OnCompleteListener<Void>() {

            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    Toast.makeText(getBaseContext(), "Sucesso ao Gravar Dados", Toast.LENGTH_LONG).show();
                    progress.dismiss(); // finalizando progresso bar
                }else{
                    Toast.makeText(getBaseContext(), "Erro ao Gravar Dados", Toast.LENGTH_LONG).show();
                    progress.dismiss(); // finalizando progresso bar
                }
            }
        });
    }

    private void alterarBD(String nome, int idade) {

        progress = new DialogProgress();
        progress.show(getSupportFragmentManager(), "1");

        String nomePasta = edtxt_nomePasta.getText().toString();

        //String nome = edtxt_nome.getText().toString();
        //String idadeString = edtxt_idade.getText().toString();

        //int idade = Integer.parseInt(idadeString);

        if (!nomePasta.isEmpty()){

            DatabaseReference reference = database.getReference().child("BD").child("Gerentes");

            Gerente gerente = new Gerente(nome, idade, false);

            Map<String, Object> updateBD = new HashMap<>();

            updateBD.put(nomePasta, gerente);

            reference.updateChildren(updateBD).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        progress.dismiss(); // finalizando progresso bar
                        Toast.makeText(getBaseContext(), "Sucesso ao Alterar Dados", Toast.LENGTH_LONG).show();
                    }else{
                        progress.dismiss(); // finalizando progresso bar
                        Toast.makeText(getBaseContext(), "Erro ao Alterar Dados", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }else{
            progress.dismiss(); // finalizando progresso bar
            DialogAlerta alerta = new DialogAlerta("Erro", "Preencha o campo com o Pasta que você quer alterar");
            alerta.show(getSupportFragmentManager(), "1");
            //Toast.makeText(getBaseContext(), "Preencha o campo com o Pasta que você quer alterar", Toast.LENGTH_LONG).show();
        }
    }

    private void removerData(String pasta) {

        progress = new DialogProgress();
        progress.show(getSupportFragmentManager(), "1");

        //String nomePasta = edtxt_nomePasta.getText().toString();

        DatabaseReference reference = database.getReference().child("BD").child("Gerentes");

        reference.child(pasta).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    progress.dismiss(); // finalizando progresso bar
                    Toast.makeText(getBaseContext(), "Sucesso ao Remover Dados", Toast.LENGTH_LONG).show();
                }else{
                    progress.dismiss(); // finalizando progresso bar
                    Toast.makeText(getBaseContext(), "Erro ao Remover Dados", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}