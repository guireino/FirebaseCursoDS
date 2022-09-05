package com.example.firebasecursods.database_list_empresa;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.firebasecursods.R;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseListEmpresaActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database_list_empresa_activity);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_Empresa_List);

        database = FirebaseDatabase.getInstance();
    }
}