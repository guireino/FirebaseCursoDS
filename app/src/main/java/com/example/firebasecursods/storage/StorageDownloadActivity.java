package com.example.firebasecursods.storage;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.firebasecursods.R;

public class StorageDownloadActivity extends AppCompatActivity {

    private ImageView imageView;
    private ProgressBar progressBar;
    private Button btn_Download, btn_Remover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.storage_download_activity);
    }
}