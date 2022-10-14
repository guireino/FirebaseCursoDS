package com.example.firebasecursods.database_list_funcionario;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.firebasecursods.R;
import com.example.firebasecursods.database_list_empresa.Empresa;
import com.example.firebasecursods.util.DialogAlerta;
import com.example.firebasecursods.util.DialogProgress;
import com.example.firebasecursods.util.PdfCreator;
import com.example.firebasecursods.util.Util;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseListFuncionarioActivity extends AppCompatActivity implements View.OnClickListener,
        RecyclerView_ListFuncionario.ClickFuncionario {

    private LinearLayout linearLayout;
    private ImageView imageView_clearfields, imageView_galeria;

    private EditText Edtxt_nome, Edtxt_idade;

    private Button btn_save;

    private RecyclerView recyclerView;
    private RecyclerView_ListFuncionario rView_list;

    private List<Funcionario> funcionarios = new ArrayList<Funcionario>();
    private List<String> keys = new ArrayList<String>();

    private FirebaseDatabase database;
    private FirebaseStorage storage;

    private ChildEventListener childEventListener;
    private DatabaseReference reference;

    private Uri uri_imagem = null;

    private Empresa empresa;

    private DialogProgress progress;
    private boolean imagem_Selected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database_list_funcionario_activity);

        linearLayout = (LinearLayout) findViewById(R.id.linearlayout_Funcionario);
        imageView_clearfields = (ImageView) findViewById(R.id.imageView_Funcionario_limparCampos);

        Edtxt_nome = (EditText) findViewById(R.id.eTxt_funcionario_Nome);
        Edtxt_idade = (EditText) findViewById(R.id.eTxt_funcionario_idade);

        btn_save = (Button) findViewById(R.id.btn_Funcionario_Salvar);
        imageView_galeria = (ImageView) findViewById(R.id.imageView_Funcionario_Imagem);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_Funcionario_list);

        imageView_clearfields.setOnClickListener(this);
        imageView_galeria.setOnClickListener(this);
        btn_save.setOnClickListener(this);

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        // essa variavel pega nome da pasta onde ele estao armazenada dados
        empresa = getIntent().getParcelableExtra("empresa");

        startRecyclerView();
    }

    private void startRecyclerView() {

        //Funcionario funcionario0 = new Funcionario("1", "Guilherme",
         //       31, "https://firebasestorage.googleapis.com/v0/b/fir-cursods-fd59d.appspot.com/o/upload%2Fimagens%2FCursoFirebaseUpload1661527218115.jpg?alt=media&token=f4182aae-9d23-4fd8-8368-7211e952c3d9");
        //Funcionario funcionario1 = new Funcionario("2", "Arce Franco",
         //       28, "https://firebasestorage.googleapis.com/v0/b/fir-cursods-fd59d.appspot.com/o/upload%2Fimagens%2FCursoFirebaseUpload1661527218115.jpg?alt=media&token=f4182aae-9d23-4fd8-8368-7211e952c3d9");

        //funcionarios.add(funcionario0);
        //funcionarios.add(funcionario1);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));  // iniciando lista com os funcionarios
        rView_list = new RecyclerView_ListFuncionario(getBaseContext(), funcionarios, this);

        recyclerView.setAdapter(rView_list);

        //System.out.println("id getId: " + empresa.getId());
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.imageView_Funcionario_limparCampos:
                //Toast.makeText(getBaseContext(), "imageView_Funcionario_limparCampos", Toast.LENGTH_LONG).show();
                clearFields();
            break;

            case R.id.imageView_Funcionario_Imagem:
                //Toast.makeText(getBaseContext(), "imageView_Funcionario_Imagem", Toast.LENGTH_LONG).show();
                obterImagem_gallery();
            break;

            case R.id.btn_Funcionario_Salvar:
                //Toast.makeText(getBaseContext(), "btn_Funcionario_Salvar", Toast.LENGTH_LONG).show();
                //salvarDadosStorage();
                btnSave();
            break;
        }
    }

    private void btnSave(){
        String nome = Edtxt_nome.getText().toString();
        String idadeString = Edtxt_idade.getText().toString();

        if(Util.ifFields(getBaseContext(), nome, idadeString)){

            int idade = Integer.parseInt(idadeString);

            if (imagem_Selected){
                salvarDadosStorage(nome, idade);
            }else{
                DialogAlerta alerta = new DialogAlerta("Imagem - Erro",
                        "E obrigatorio escolher uma imagem para salvar os dados do funcionario");
                alerta.show(getSupportFragmentManager(), "1");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_database_list_funcionario, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.item_show_layout:
                linearLayout.setVisibility(View.VISIBLE);
            return true;

            case R.id.item_hide_layout:
                linearLayout.setVisibility(View.GONE);
            return true;

            case R.id.item_create_funcionario:

                itemCriarPdf();
                //Toast.makeText(getBaseContext(), "case: item_create_funcionario", Toast.LENGTH_LONG).show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void itemCriarPdf() {

        if(funcionarios.size() > 0){

            new GerarPDF().execute();

        }else{
            DialogAlerta alerta = new DialogAlerta("Erro ao gerar PDF", "Não existem funcionarios para gerar o relatorio PDF");
            alerta.show(getSupportFragmentManager(), "1");
        }
    }

    @Override
    public void click_Funcionario(Funcionario funcionario) {
        //Toast.makeText(getBaseContext(), "Nome: " + funcionario.getNome() + "\n\nIdade: " + funcionario.getIdade(), Toast.LENGTH_LONG).show();

        funcionario.setId(empresa.getId());

        System.out.println("funcionario: " + funcionario);
        System.out.println("funcionario.setId(empresa.getId()): " + empresa.getId());
        System.out.println("funcionario.getId_empresa(): " + funcionario.getId_empresa());  // null

        // iniciando a tela DatabaseFuncionarioActivity
        Intent intent = new Intent(getBaseContext(), DatabaseFuncionarioActivity.class);
        intent.putExtra("Funcionarios", funcionario);

        startActivity(intent);
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
                            imagem_Selected = true;
                            return false;
                        }

                    }).into(imageView_galeria);

                }else{
                    Toast.makeText(getBaseContext(), "Falha ao selecionar imagem", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    private void clearFields(){

        Edtxt_nome.setText("");
        Edtxt_idade.setText("");
        uri_imagem = null;
        imagem_Selected = false;

        imageView_galeria.setImageResource(R.drawable.ic_galeria_24);
    }

    // ======================================= SALVAR DE DADOS =======================================

    private void salvarDadosStorage(String nome, int idade){

        progress = new DialogProgress();
        progress.show(getSupportFragmentManager(), "2");

        StorageReference reference = storage.getReference().child("BD").child("BD").child("Empresas").child(empresa.getNome());

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

                                    salvarDadosDatabase(nome,  idade, url_imagem);
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

    private void salvarDadosDatabase(String nome, int idade, String urlImagem){

        //String nome = Edtxt_nome.getText().toString();
        //String idade_String = Edtxt_idade.getText().toString();

        //int idade = Integer.parseInt(idade_String);

        progress = new DialogProgress();
        progress.show(getSupportFragmentManager(), "2");

        Funcionario funcionario = new Funcionario(nome, idade, urlImagem);

        System.out.println("empresa.getId() " + empresa.getId());

        DatabaseReference databaseReference = database.getReference().child("BD").child("BD").child("Empresas").child(empresa.getId()).child("Funcionarios");

        databaseReference.push().setValue(funcionario).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    Toast.makeText(getBaseContext(), "Sucesso ao realizar Upload - Database", Toast.LENGTH_SHORT).show();
                    progress.dismiss();
                }else{
                    Toast.makeText(getBaseContext(), "Erro ao realizar Upload - Database", Toast.LENGTH_SHORT).show();
                    progress.dismiss();
                }
            }
        });
    }

    private void generatePdf() throws DocumentException, FileNotFoundException {

        File diretorio = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        String nomeFile = diretorio.getPath() + "/" + "Relatorio Funcionarios" + System.currentTimeMillis() + ".pdf";

        File pdf = new File(nomeFile);

        OutputStream outputStream = new FileOutputStream(pdf);

        Document document = new Document();

        PdfCreator event = new PdfCreator(); // criando nomerasao da pagina
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);

        writer.setBoxSize("box_a", new Rectangle(36, 54, 559, 788));
        writer.setPageEvent(event);

        document.open();

        Font font = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
        Font fontDados = new Font(Font.FontFamily.HELVETICA, 20, Font.NORMAL);

        Paragraph paragraph = new Paragraph("Relatorio de Funcionarios", font);

        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(30f);
        table.setSpacingAfter(30f);

        for(Funcionario funcionario: funcionarios){

            String dados = "Nome: " + funcionario.getNome() + "\n\nIdade: " + funcionario.getIdade();

            PdfPCell cell = new PdfPCell(new Paragraph(dados, fontDados));

            cell.setPadding(10);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setBorder(PdfPCell.NO_BORDER);

            table.addCell(cell);
        }

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

    private void ouvinte(){

        // buscando no banco de dados do firebase a pastas
        reference = database.getReference().child("BD").child("BD").child("Empresas").child(empresa.getId()).child("Funcionarios");
        //reference = database.getReference().child("Funcionarios");

        if (childEventListener == null){

            childEventListener = new ChildEventListener() {

                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    String key = snapshot.getKey();

                    keys.add(key);

                    Funcionario funcionario = snapshot.getValue(Funcionario.class);

                    funcionario.setId(key); // pegando id da pasta

                    funcionarios.add(funcionario);

                    System.out.println("snapshot.getValue(Funcionario.class " + snapshot.getValue(Funcionario.class));
                    System.out.println("funcionarios.add " + funcionarios.add(funcionario));

                    rView_list.notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    String key = snapshot.getKey();

                    int index = keys.indexOf(key);  // index e para localizacao id da pasta

                    Funcionario funcionario = snapshot.getValue(Funcionario.class);
                    funcionario.setId(key);

                    funcionarios.set(index, funcionario);

                    rView_list.notifyDataSetChanged(); // atualizando banco de dados firebase
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    String key = snapshot.getKey();

                    int index = keys.indexOf(key);

                    funcionarios.remove(index);

                    keys.remove(index);

                    rView_list.notifyItemRemoved(index); // atualizando banco de dados firebase
                    rView_list.notifyItemChanged(index, funcionarios.size());
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

    private class GerarPDF extends AsyncTask<Void, Void, Void>{

        private DialogProgress dialogProgress;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            dialogProgress = new DialogProgress();
            dialogProgress.show(getSupportFragmentManager(), "2");
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                generatePdf();
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            dialogProgress.dismiss();
        }
    }
}