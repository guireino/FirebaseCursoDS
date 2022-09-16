package com.example.firebasecursods;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.firebasecursods.database.DatabaseGravarAlterarRemoverActivity;
import com.example.firebasecursods.database.DatabaseLerDadosActivity;
import com.example.firebasecursods.database_list_empresa.DatabaseListEmpresaActivity;
import com.example.firebasecursods.storage.StorageDownloadActivity;
import com.example.firebasecursods.storage.StorageUploadActivity;
import com.example.firebasecursods.util.Permissao;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView cardView_Storage_Download, cardView_Storage_Upload, cardView_Database_Ler, cardView_Database_Gravar, cardView_Empresa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cardView_Storage_Download = (CardView) findViewById(R.id.cardView_Storage_Download);
        cardView_Storage_Upload = (CardView) findViewById(R.id.cardView_Storage_Upload);
        cardView_Database_Ler = (CardView) findViewById(R.id.cardView_Database_Ler);
        cardView_Database_Gravar = (CardView) findViewById(R.id.cardView_Database_Gravar);
        cardView_Empresa = (CardView) findViewById(R.id.cardView_Empresas);

        cardView_Storage_Download.setOnClickListener(this);
        cardView_Storage_Upload.setOnClickListener(this);
        cardView_Database_Ler.setOnClickListener(this);
        cardView_Database_Gravar.setOnClickListener(this);
        cardView_Empresa.setOnClickListener(this);

        permission();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.cardView_Storage_Download:
                //Toast.makeText(this, "cardView_Storage_Download", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getBaseContext(), StorageDownloadActivity.class);
                startActivity(intent);
            break;

            case R.id.cardView_Storage_Upload:
                //Toast.makeText(this, "cardView_Storage_Upload", Toast.LENGTH_LONG).show();
                startActivity(new Intent(getBaseContext(), StorageUploadActivity.class));
            break;

            case R.id.cardView_Database_Ler:
                //Toast.makeText(this, "cardView_Database_Ler", Toast.LENGTH_LONG).show();
                startActivity(new Intent(getBaseContext(), DatabaseLerDadosActivity.class));
            break;

            case R.id.cardView_Database_Gravar:
                //Toast.makeText(this, "cardView_Database_Gravar", Toast.LENGTH_LONG).show();
                startActivity(new Intent(getBaseContext(), DatabaseGravarAlterarRemoverActivity.class));
            break;

            case R.id.cardView_Empresas:
                //Toast.makeText(this, "cardView_Empresas", Toast.LENGTH_LONG).show();
                startActivity(new Intent(getBaseContext(), DatabaseListEmpresaActivity.class));
            break;
        }
    }

    private void permission(){

        String permissoes [] = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
        };

        Permissao.permissao(this, 0, permissoes);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for(int result: grantResults){

            if(result == PackageManager.PERMISSION_DENIED){
                Toast.makeText(this, "Aceite as permissões para o aplicativo funcionar corretamente", Toast.LENGTH_LONG).show();
                finish();

                break;
            }
        }
    }
}