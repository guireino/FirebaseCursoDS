package com.example.firebasecursods.storage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.firebasecursods.R;
import com.example.firebasecursods.util.DialogAlerta;
import com.example.firebasecursods.util.DialogProgress;
import com.example.firebasecursods.util.Permissao;
import com.example.firebasecursods.util.Util;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class StorageUploadActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageView;
    private Button btn_Enviar;
    private Uri uri_imagem;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.storage_upload_activity);

        imageView = (ImageView) findViewById(R.id.imageView_StorageUpload);
        btn_Enviar = (Button) findViewById(R.id.btn_StorageUpload_Enviar);

        btn_Enviar.setOnClickListener(this);

        storage = FirebaseStorage.getInstance();

        permission();
    }

    private void permission(){

        String permissoes [] = new String[]{
             Manifest.permission.CAMERA,
        };

        Permissao.permissao(this, 0, permissoes);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_StorageUpload_Enviar:
                //Toast.makeText(getBaseContext(), "btn_StorageUpload_Enviar", Toast.LENGTH_SHORT).show();
                btn_Upload();
            break;
        }
    }

    private void btn_Upload(){

        if(Util.statusInternet(getBaseContext())){

            if(imageView.getDrawable() != null){
                //upload_Imagem_1();
                upload_Imagem_2();
            }else{
                Toast.makeText(getBaseContext(), "Não existe imagem ainda para realizar o upload", Toast.LENGTH_LONG).show();
            }

        }else{
            Toast.makeText(getBaseContext(), "Erro de Conexão - verifique se o seu Wifi ou 3G esta funcionando", Toast.LENGTH_LONG).show();
        }
    }

    // ======================================= SALVAR DE DADOS =======================================

    private void upload_Imagem_1() {

        DialogProgress dialogProgress = new DialogProgress();
        dialogProgress.show(getSupportFragmentManager(), "");

        StorageReference reference = storage.getReference().child("upload").child("imagens"); // criando pasta firebase
        StorageReference nome_Imagem = reference.child("CursoFirebaseUpload" + System.currentTimeMillis() + ".jpg");

        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();  // bitmap e usado para nao dar estoro de memoria
        Bitmap bitmap = drawable.getBitmap();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        UploadTask uploadTask = nome_Imagem.putBytes(bytes.toByteArray());

        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if(task.isSuccessful()){
                    dialogProgress.dismiss();
                    Toast.makeText(getBaseContext(), "Sucesso ao realizar upload", Toast.LENGTH_LONG).show();
                }else{
                    dialogProgress.dismiss();
                    Toast.makeText(getBaseContext(), "Erro ao realizar upload", Toast.LENGTH_LONG).show();
                }
            }
        })
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

            }
        });
    }

    private void upload_Imagem_2(){

        DialogProgress dialogProgress = new DialogProgress();
        dialogProgress.show(getSupportFragmentManager(), "");

        StorageReference reference = storage.getReference().child("upload").child("imagens");
        StorageReference nome_Imagem = reference.child("CursoFirebaseUpload" + System.currentTimeMillis() + ".jpg");

        // mudando resolucao da imagem
        Glide.with(getBaseContext()).asBitmap().load(uri_imagem).apply(new RequestOptions().override(1024, 768))
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        Toast.makeText(getBaseContext(), "Sucesso ao transformar imagem", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {

                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        resource.compress(Bitmap.CompressFormat.JPEG, 70, bytes);

                        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes.toByteArray());

                        try {
                            bytes.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        UploadTask uploadTask = nome_Imagem.putStream(inputStream);

                        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>(){

                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                return nome_Imagem.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {

                                if(task.isSuccessful()){

                                    dialogProgress.dismiss();

                                    Uri uri = task.getResult();

                                    String url_imagem = uri.toString();

                                    DialogAlerta alerta = new DialogAlerta("URL IMAGE", url_imagem);
                                    alerta.show(getSupportFragmentManager(), "3");

                                    Toast.makeText(getBaseContext(), "Sucesso ao realizar Upload", Toast.LENGTH_SHORT).show();
                                }else{
                                    dialogProgress.dismiss();
                                    Toast.makeText(getBaseContext(), "Erro ao realizar Upload", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        return false;
                    }
                }).submit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_storage_upload, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.item_gallery:
                obterImagem_gallery();
                //Toast.makeText(getBaseContext(), "item_gallery", Toast.LENGTH_SHORT).show();
            break;

            case R.id.item_camera:
                //Toast.makeText(getBaseContext(), "item_camera", Toast.LENGTH_SHORT).show();
                item_camera();
            break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void item_camera() {

        if(ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            DialogAlerta dialogAlerta = new DialogAlerta("Permissao Necessaria",
                    "Acesse as configurações do aplicativo para poder utilizar a camera no nosso aplicativo");
            dialogAlerta.show(getSupportFragmentManager(), "1");
        }else{
            obterImagem_camera();
        }
    }

    private void obterImagem_gallery(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "Escolha uma Imagem"), 0);
    }

    private void obterImagem_camera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File diretorio = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        String nomeImagem = diretorio.getPath() + "/" + "CursoImagem" + System.currentTimeMillis() + ".jpg";

        File file = new File(nomeImagem);

        String autorizacao = "com.example.firebasecursods";

        uri_imagem = FileProvider.getUriForFile(getBaseContext(), autorizacao, file);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri_imagem);

        startActivityForResult(intent, 1);
    }

    // ======================================= REPOSTAS DE COMUNICACAO =======================================

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){

            if(requestCode == 0){ // resposta da galeria

                if(data != null){   // conteudo da escolha da imagem da galeria

//                   Bundle extras = data.getExtras();
//                   Bitmap bitmap = (Bitmap) extras.get("data");

                    uri_imagem = data.getData();

//                  imageView.setImageBitmap(bitmap);

                    Glide.with(getBaseContext()).asBitmap().load(uri_imagem).listener(new RequestListener<Bitmap>() {

                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            Toast.makeText(getBaseContext(), "Erro ao carregar imagem", Toast.LENGTH_SHORT).show();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }

                    }).into(imageView);

                }else{
                    Toast.makeText(getBaseContext(), "Falha ao selecionar imagem", Toast.LENGTH_SHORT).show();
                }
            }

            else if(requestCode == 1){ // resposta da camera

                if (uri_imagem != null){ // verificar resposta da camera

                    // metado glide e para tratavendo imagem para dimenuir tamanho e tambem para nao tem estroro de memoria
                    Glide.with(getBaseContext()).asBitmap().load(uri_imagem).listener(new RequestListener<Bitmap>() {

                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            Toast.makeText(getBaseContext(), "Erro ao carregar imagem", Toast.LENGTH_SHORT).show();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }

                    }).into(imageView);

                }else{
                    Toast.makeText(getBaseContext(), "Falha ao Tirar Foto", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for(int result: grantResults){

            if(result == PackageManager.PERMISSION_DENIED){
                Toast.makeText(this, "Aceite as permissões para o aplicativo acessar sua camera", Toast.LENGTH_LONG).show();
                finish();

                break;
            }
        }
    }
}