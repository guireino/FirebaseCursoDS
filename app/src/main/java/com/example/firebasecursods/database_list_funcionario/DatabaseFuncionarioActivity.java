package com.example.firebasecursods.database_list_funcionario;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.firebasecursods.R;

public class DatabaseFuncionarioActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageView;
    private ProgressBar progressBar;

    private EditText edTxt_Nome, edTxt_Idade;

    private Button btn_Alterar, btn_Remover;

    private Funcionario funcionario_Selected;

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

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.imageView_database_funcionario:
                Toast.makeText(getBaseContext(), "imageView_database_funcionario", Toast.LENGTH_LONG).show();
            break;

            case R.id.btn_database_funcionario_alterar:
                Toast.makeText(getBaseContext(), "btn_database_funcionario_alterar", Toast.LENGTH_LONG).show();
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
}