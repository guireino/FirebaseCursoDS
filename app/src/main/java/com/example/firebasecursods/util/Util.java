package com.example.firebasecursods.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class Util {

    public static boolean statusInternet(Context context){

        ConnectivityManager conexao = (ConnectivityManager)context.getSystemService(context.CONNECTIVITY_SERVICE);

        NetworkInfo info = conexao.getActiveNetworkInfo();

        if(info != null && info.isConnected()){
            return true;
        }else{
            return false;
        }
    }

    public static boolean ifFields(Context context, String text_1, String text_2){

        if(!text_1.isEmpty() && !text_2.isEmpty()){

            if (statusInternet(context)){
                return true;
            }else{
                Toast.makeText(context, "Sem conexao com a Internet", Toast.LENGTH_LONG).show();
                return false;
            }
        }else{
            Toast.makeText(context, "Preencha os campos", Toast.LENGTH_LONG).show();
            return false;
        }
    }

}