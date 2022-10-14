package com.example.firebasecursods.database_list_funcionario;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.firebasecursods.R;
import com.example.firebasecursods.database.Gerente;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class DatabaseFuncionarioActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageView;
    private ProgressBar progressBar;

    private EditText edTxt_Nome, edTxt_Idade;

    private Button btn_Alterar, btn_Remover;

    private Funcionario funcionario_Selected;

    private Uri uri_imagem = null;
    private boolean imagem_Alterada = false;

    private FirebaseDatabase database;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database_funcionario_activity);

        imageView = (ImageView) findViewById(R.id.imageView_database_funcionario);
        progressBar = (ProgressBar) findViewById(R.id.progressbar_database_funcionario);

        edTxt_Nome = (EditText) findViewById(R.id.edTxt_database_funcionario_Nome);
        edTxt_Idade = (EditText) findViewById(R.id.edTxt_database_funcionario_Idade);

        btn_Alterar = (Button) findViewById(R.id.btn_database_funcionario_alterar);
        btn_Remover = (Button) findViewById(R.id.btn_database_funcionario_remover);

        imageView.setOnClickListener(this);
        btn_Alterar.setOnClickListener(this);
        btn_Remover.setOnClickListener(this);

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        // essa variavel pega nome da pasta onde ele estao armazenada dados
        funcionario_Selected = getIntent().getParcelableExtra("Funcionarios");

        LoadingDadosFuncionario();
    }

    private void LoadingDadosFuncionario() {

        progressBar.setVisibility(View.VISIBLE);

        edTxt_Nome.setText(funcionario_Selected.getNome());
        edTxt_Idade.setText(funcionario_Selected.getIdade() + "");

        System.out.println("funcionario_Selected.getNome() " + funcionario_Selected.getNome());
        System.out.println("funcionario_Selected.getIdade() " + funcionario_Selected.getIdade());

        System.out.println("funcionario_Selected.getId_empresa(): " + funcionario_Selected.getId_empresa());

        // carregando imagem do funcionario
        Picasso.with(getBaseContext()).load(funcionario_Selected.getUrlImagem()).into(imageView,
                new com.squareup.picasso.Callback(){

            @Override
            public void onSuccess() {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError() {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.imageView_database_funcionario:
                //Toast.makeText(getBaseContext(), "imageView_database_funcionario", Toast.LENGTH_LONG).show();
                obterImagem_gallery();
            break;

            case R.id.btn_database_funcionario_alterar:
                //Toast.makeText(getBaseContext(), "btn_database_funcionario_alterar", Toast.LENGTH_LONG).show();

                String nome = edTxt_Nome.getText().toString();
                String idadeString = edTxt_Idade.getText().toString();

                int idade = Integer.parseInt(idadeString);

                if(imagem_Alterada){

                }else{
                    alterarBD(nome, idade, funcionario_Selected.getUrlImagem());
                }

            break;

            case R.id.btn_database_funcionario_remover:
                Toast.makeText(getBaseContext(), "btn_database_funcionario_remover", Toast.LENGTH_LONG).show();
            break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {

        getMenuInflater().inflate(R.menu.menu_storage_download, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.item_shara:
                Toast.makeText(getBaseContext(), "item_shara", Toast.LENGTH_LONG).show();
            return true;

            case R.id.item_create_pdf:
                Toast.makeText(getBaseContext(), "item_create_pdf", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void obterImagem_gallery(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "Escolha uma Imagem"), 0);
    }

    // ======================================= REPOSTAS DE COMUNICACAO =======================================

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){

            if(requestCode == 0){ // resposta da galeria

                if(data != null){   // conteudo da escolha da imagem da galeria

                    uri_imagem = data.getData();

                    Glide.with(getBaseContext()).asBitmap().load(uri_imagem).listener(new RequestListener<Bitmap>() {

                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            Toast.makeText(getBaseContext(), "Erro ao carregar imagem", Toast.LENGTH_SHORT).show();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            imagem_Alterada = true;
                            return false;
                        }

                    }).into(imageView);

                }else{
                    Toast.makeText(getBaseContext(), "Falha ao selecionar imagem", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void alterarBD(String nome, int idade, String url_imagem) {

        System.out.println("funcionario_Selected.getId_empresa(): " + funcionario_Selected.getId_empresa());

        DatabaseReference reference = database.getReference().child("BD").child("BD").child("Empresas")
                .child(funcionario_Selected.getId_empresa())
                .child("Funcionarios");

        Funcionario funcionario = new Funcionario(nome, idade, url_imagem);

        Map<String, Object> updateBD = new HashMap<>();

        updateBD.put(funcionario_Selected.getId(), funcionario);

        reference.updateChildren(updateBD).addOnCompleteListener(new OnCompleteListener<Void>() {

            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    Toast.makeText(getBaseContext(), "Sucesso ao Alterar Dados", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getBaseContext(), "Erro ao Alterar Dados", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}