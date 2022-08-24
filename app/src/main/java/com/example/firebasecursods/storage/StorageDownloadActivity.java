package com.example.firebasecursods.storage;

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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.firebasecursods.R;
import com.example.firebasecursods.util.DialogAlerta;
import com.example.firebasecursods.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import com.itextpdf.text.Document;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class StorageDownloadActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageView;
    private ProgressBar progressBar;
    private Button btn_Download, btn_Remover;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.storage_download_activity);

        imageView = (ImageView) findViewById(R.id.imageView_Storage);
        progressBar = (ProgressBar) findViewById(R.id.progressbar_storage_download);

        btn_Download = (Button) findViewById(R.id.btn_StorageDownload);
        btn_Remover = (Button) findViewById(R.id.btn_StorageRemover);

        btn_Download.setOnClickListener(this);
        btn_Remover.setOnClickListener(this);

        progressBar.setVisibility(View.GONE);

        storage = FirebaseStorage.getInstance();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btn_StorageDownload:
                //Toast.makeText(getBaseContext(), "Download", Toast.LENGTH_SHORT).show();
                //download_imagem_1();
                //download_imagem_2();
                btn_Download();
            break;

            case R.id.btn_StorageRemover:
                //Toast.makeText(getBaseContext(), "Remover", Toast.LENGTH_SHORT).show();
                //removar_imagem_1();
               // removar_imagem_2();
                btn_Remover();
            break;
        }
    }

    // ===================== TRATAMENTO DE ERROS ==================

    private void btn_Download(){

        if(Util.statusInternet(getBaseContext())){
            //download_imagem_1();
            download_imagem_2();
        }else{
            DialogAlerta alerta = new DialogAlerta("Erro de Conexão", "Verifique se sua conexão Wifi ou 3G esta funcionando");
            alerta.show(getSupportFragmentManager(), "1");
        }
    }

    private void btn_Remover(){

        if(Util.statusInternet(getBaseContext())){
            //removar_imagem_1();
            removar_imagem_2();
        }else{
            DialogAlerta alerta = new DialogAlerta("Erro de Conexão", "Verifique se sua conexão Wifi ou 3G esta funcionando");
            alerta.show(getSupportFragmentManager(), "1");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_storage_download, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.item_shara:

                //Toast.makeText(getBaseContext(), "Compartilhar", Toast.LENGTH_SHORT).show();

                item_Shara();

            return true;

            case R.id.item_create_pdf:

                //Toast.makeText(getBaseContext(), "Criar PDF", Toast.LENGTH_SHORT).show();

                item_GerarPDF();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // ===================== TRATAMENTO DE ERROS ==================

    private void item_Shara(){

        if(imageView.getDrawable() != null){
            share();
        }else{
            DialogAlerta alerta = new DialogAlerta("Erro de Compartilhamento",
                    "Não existe nenhuma imagem para compartilhar");
            alerta.show(getSupportFragmentManager(), "1");
        }
    }

    private void item_GerarPDF(){

        if(imageView.getDrawable() != null){

                try {
                    generatePdf();
                } catch (DocumentException e){
                    e.printStackTrace();
                } catch (FileNotFoundException e){
                    e.printStackTrace();
                }
        }else{
            DialogAlerta alerta = new DialogAlerta("Erro ao Gerar Pdf",
                    "Não existe nenhuma imagem para gerar o Pdf");
            alerta.show(getSupportFragmentManager(), "1");
        }
    }

    public void download_imagem_1(){

        progressBar.setVisibility(View.VISIBLE);

        String url = "https://firebasestorage.googleapis.com/v0/b/fir-cursods-fd59d.appspot.com/o/imagem%2Fimagem_pdf.png?alt=media&token=c457d24e-9ce9-4414-98d9-4b7453a39495";

//        Picasso.with(getBaseContext()).load(url).into(imageView, new com.squareup.picasso.Callback() {

           // O picasso pode dar erro memoria quando imagem e muito grande
//
//            @Override
//            public void onSuccess() {
//                progressBar.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onError() {
//                progressBar.setVisibility(View.GONE);
//            }
//        });

        Glide.with(getBaseContext()).asBitmap().load(url).listener(new RequestListener<Bitmap>() {

            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                progressBar.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                progressBar.setVisibility(View.GONE);
                return false;
            }
        }).into(imageView);
    }

    private void download_imagem_2(){

        progressBar.setVisibility(View.VISIBLE);

        //FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference reference = storage.getReference().child("imagem").child("imagem_pdf.png"); // buscando imagem no firebase

        reference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {

                if(task.isSuccessful()){
                    String url = task.getResult().toString();

                    Picasso.with(getBaseContext()).load(url).into(imageView, new Callback() {
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
            }
        });
    }

    private void removar_imagem_1(){

        progressBar.setVisibility(View.VISIBLE);

        String url = "https://firebasestorage.googleapis.com/v0/b/fir-cursods-fd59d.appspot.com/o/imagem%2Fimagem_pdf.png?alt=media&token=c457d24e-9ce9-4414-98d9-4b7453a39495";

        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(url);

        reference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {

            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    imageView.setImageDrawable(null);
                    Toast.makeText(getBaseContext(), "Sucesso ao Remover Imagem", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getBaseContext(), "Erro ao Remover Imagem", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void removar_imagem_2(){

        String nome = "imagem_pdf.png";

        StorageReference reference = storage.getReference().child("imagem").child(nome);

        reference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {

            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    imageView.setImageDrawable(null);
                    Toast.makeText(getBaseContext(), "Sucesso ao Remover Imagem", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getBaseContext(), "Erro ao Remover Imagem", Toast.LENGTH_LONG).show();
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

            startActivity(Intent.createChooser(intent, "Compartilhar imagem Curso"));

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

        Paragraph paragraph = new Paragraph("Curso Firebase Modulo II", font);
        paragraph.setAlignment(Element.ALIGN_CENTER);

        Paragraph paragraph_2 = new Paragraph("Guilherme A.R", font);
        paragraph_2.setAlignment(Element.ALIGN_LEFT);

        ListItem item = new ListItem();

        item.add(paragraph);
        item.add(paragraph_2);

        document.add(item);

        try {

            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();  // bitmap e usado para nao dar estouro de memoria
            Bitmap bitmap = drawable.getBitmap();
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();

            System.out.println("bitmap: " + bitmap);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

            Image image = Image.getInstance(bytes.toByteArray()); // inserindo imagem no arquivo pdf
            image.scaleAbsolute(100f, 100f);
            image.setAlignment(Element.ALIGN_CENTER);

            Toast.makeText(this, "image: " + image, Toast.LENGTH_LONG).show();
            System.out.println("image: " + image);

            image.setRotationDegrees(10f);
            document.add(image);
        } catch (IOException e) {
            e.printStackTrace();
        }

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