package com.example.firebasecursods.database_list_funcionario;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.firebasecursods.R;
import com.example.firebasecursods.database.Gerente;
import com.example.firebasecursods.util.DialogAlerta;
import com.example.firebasecursods.util.DialogProgress;
import com.example.firebasecursods.util.Util;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
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
                btnAlterar();
            break;

            case R.id.btn_database_funcionario_remover:
                //Toast.makeText(getBaseContext(), "btn_database_funcionario_remover", Toast.LENGTH_LONG).show();
                //removerFuncionarioImg();
                btnRemover();
            break;
        }
    }

    private void btnAlterar() {

        String nome = edTxt_Nome.getText().toString();
        String idadeString = edTxt_Idade.getText().toString();

        if(Util.ifFields(getBaseContext(), nome, idadeString)){

            int idade = Integer.parseInt(idadeString);

            if(!nome.equals(funcionario_Selected.getNome()) || idade != funcionario_Selected.getIdade() || imagem_Alterada){

                if(imagem_Alterada){
                    removerImagem(nome, idade);
                }else{
                    alterarBD(nome, idade, funcionario_Selected.getUrlImagem());
                }

            }else{

                DialogAlerta alerta = new DialogAlerta("Erro", "Nenhuma informacao foi alterada " +
                        "para poder salvar no Banco de Dados");

                alerta.show(getSupportFragmentManager(), "2");
            }
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
                //Toast.makeText(getBaseContext(), "item_shara", Toast.LENGTH_LONG).show();
                share();
            return true;

            case R.id.item_create_pdf:

                //Toast.makeText(getBaseContext(), "item_create_pdf", Toast.LENGTH_LONG).show();

                try {
                    generatePdf();
                } catch (DocumentException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

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

    // ======================================= TRATAMENTO DE ALTERACAO DE DADOS =======================================

    private void salvarDadoStorage(final String nome, final int idade){

        DialogProgress progress = new DialogProgress();
        progress.show(getSupportFragmentManager(), "1");

        StorageReference reference = storage.getReference().child("BD").child("BD").child("Empresas").child(funcionario_Selected.getId_empresa());

        final StorageReference nome_Imagem = reference.child("CursoFirebase" + System.currentTimeMillis() + ".jpg");

        // mudando resolucao da imagem
        Glide.with(getBaseContext()).asBitmap().load(uri_imagem).apply(new RequestOptions().override(1024, 768))
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        Toast.makeText(getBaseContext(), "Sucesso ao transformar imagem", Toast.LENGTH_SHORT).show();

                        progress.dismiss();

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

                                    progress.dismiss();

                                    Uri uri = task.getResult();
                                    String url_imagem = uri.toString();

                                    alterarBD(nome,  idade, url_imagem);
                                }else{
                                    progress.dismiss();
                                    Toast.makeText(getBaseContext(), "Erro ao realizar Upload - Database", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        return false;
                    }
                }).submit();
    }

    private void alterarBD(String nome, int idade, String url_imagem) {

        //System.out.println("funcionario_Selected.getId_empresa(): " + funcionario_Selected.getId_empresa());

        DialogProgress progress = new DialogProgress();
        progress.show(getSupportFragmentManager(), "1");

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

                    progress.dismiss();
                    Toast.makeText(getBaseContext(), "Sucesso ao Alterar Dados", Toast.LENGTH_LONG).show();
                    finish();
                }else{
                    progress.dismiss();
                    Toast.makeText(getBaseContext(), "Erro ao Alterar Dados", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // ======================================= TRATAMENTO DE REMOVER DE DADOS =======================================

    private void btnRemover(){

        if(Util.statusInternet(getBaseContext())){ // verificando se tem net
            removerFuncionarioImg();
        }else{
            Toast.makeText(getBaseContext(), "Sem conexão com a Internet", Toast.LENGTH_LONG).show();
        }
    }

    private void removerImagem(final String nome, final int idade) {

        DialogProgress progress = new DialogProgress();
        progress.show(getSupportFragmentManager(), "1");

        String url = funcionario_Selected.getUrlImagem();

        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(url);

        reference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {

            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    progress.dismiss();
                    salvarDadoStorage(nome, idade);
                }else{
                    progress.dismiss();
                    Toast.makeText(getBaseContext(), "Erro ao Remover Imagem", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void removerFuncionarioImg() {

        String url = funcionario_Selected.getUrlImagem();

        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(url);

        reference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {

            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    removerFuncionario();
                }else{
                    Toast.makeText(getBaseContext(), "Erro ao Remover Imagem", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void removerFuncionario() {

        DatabaseReference reference = database.getReference().child("BD").child("BD").child("Empresas")
                .child(funcionario_Selected.getId_empresa())
                .child("Funcionarios");

        reference.child(funcionario_Selected.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getBaseContext(), "Sucesso ao Remover Funcionario", Toast.LENGTH_LONG).show();
                    finish();
                }else{
                    Toast.makeText(getBaseContext(), "Erro ao Remover Funcionario", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void share() {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/jpeg");

        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();  // bitmap e usado para nao dar estoro de memoria
        Bitmap bitmap = drawable.getBitmap();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "CursoFirebase", null);

        Uri uri = Uri.parse(path);

        intent.putExtra(Intent.EXTRA_STREAM, uri);

        startActivity(Intent.createChooser(intent, "Compartilhar imagem Funcionario"));

        //Toast.makeText(getBaseContext(), "Não possui imagem ainda para Compartilhar", Toast.LENGTH_LONG).show();
    }

    private void generatePdf() throws DocumentException, FileNotFoundException {

        File diretorio = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        String nomeFile = diretorio.getPath() + "/" + "FirebaseCurso" + System.currentTimeMillis() + ".pdf";

        File pdf = new File(nomeFile);

        OutputStream outputStream = new FileOutputStream(pdf);

        Document document = new Document();

        PdfWriter writer = PdfWriter.getInstance(document, outputStream);
        writer.setBoxSize("firebase", new Rectangle(36, 54, 559, 788));

        document.open();

        Font font = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);

        Paragraph paragraph = new Paragraph("Dados Funcionario - " + funcionario_Selected.getNome(), font);
        paragraph.setAlignment(Element.ALIGN_CENTER);

        ListItem item = new ListItem();

        item.add(paragraph);

        document.add(item);

        PdfPTable table = new PdfPTable(2);

        table.setWidthPercentage(100);
        table.setSpacingBefore(25f);

        try {

            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();  // bitmap e usado para nao dar estouro de memoria
            Bitmap bitmap = drawable.getBitmap();
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();

            //System.out.println("bitmap: " + bitmap);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

            Image image = Image.getInstance(bytes.toByteArray()); // inserindo imagem no arquivo pdf
            image.scaleAbsolute(100f, 100f);
            image.setAlignment(Element.ALIGN_CENTER);

            //image.setRotationDegrees(10f);

            table.addCell(image);

        } catch (IOException e) {
            e.printStackTrace();
        }

        Font fontDados = new Font(Font.FontFamily.HELVETICA, 30, Font.BOLD);

        String dados = "Nome: " + funcionario_Selected.getNome() + "\n\nIdade: " + funcionario_Selected.getIdade();

        PdfPCell cell = new PdfPCell(new Paragraph(dados, fontDados));

        cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);

        cell.setBorder(PdfPCell.NO_BORDER);

        table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
        table.addCell(cell);

        document.add(table);
        document.close();

        viewPdf(pdf);
    }

    private void viewPdf(File pdf){

        PackageManager packageManager = getPackageManager();

        Intent itent = new Intent(Intent.ACTION_VIEW);
        itent.setType("application/pfd");

        List<ResolveInfo> list = packageManager.queryIntentActivities(itent, PackageManager.MATCH_DEFAULT_ONLY);

        if(list.size() > 0){

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            Uri uri = FileProvider.getUriForFile(getBaseContext(), "com.example.firebasecursods", pdf);

            intent.setDataAndType(uri,"application/pdf");

            startActivity(intent);
        }else{
            DialogAlerta dialogAlerta = new DialogAlerta("Erro ao Abrir PDF", "Não foi detectado nenhum leitor PDF no seu Dispositivo.");
            dialogAlerta.show(getSupportFragmentManager(), "3");
        }
    }

}