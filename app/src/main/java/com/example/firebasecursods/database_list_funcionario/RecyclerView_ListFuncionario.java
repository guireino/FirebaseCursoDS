package com.example.firebasecursods.database_list_funcionario;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebasecursods.R;
import com.example.firebasecursods.database_list_empresa.RecyclerView_listEmpresa;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerView_ListFuncionario extends RecyclerView.Adapter<RecyclerView_ListFuncionario.ViewHolder>{

    private Context context;
    private List<Funcionario> funcionariosList;
    private ClickFuncionario clickFuncionario;

    public RecyclerView_ListFuncionario(Context context, List<Funcionario> funcionarios, ClickFuncionario clickFuncionario){

        this.context = context;
        this.funcionariosList = funcionarios;
        this.clickFuncionario = clickFuncionario;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.database_list_funcionario_recyclerview, parent, false);

        RecyclerView_ListFuncionario.ViewHolder holder = new RecyclerView_ListFuncionario.ViewHolder(view);
        view.setTag(holder);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        Funcionario funcionario = funcionariosList.get(position);

        holder.txtView_Nome.setText(funcionario.getNome());
        holder.txtView_Idade.setText(funcionario.getIdade() + "");

        holder.progressBar.setVisibility(View.VISIBLE);

        Picasso.with(context).load(funcionario.getUrlImagem()).into(holder.imageView, new com.squareup.picasso.Callback(){
            @Override
            public void onSuccess() {
                holder.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError() {
                holder.progressBar.setVisibility(View.GONE);
            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickFuncionario.click_Funcionario(funcionario);
                System.out.println("click_Funcionario(funcionario): " + funcionario);
            }
        });
    }

    @Override
    public int getItemCount() {
        return funcionariosList.size(); // pegando tamanho lista
    }

    public interface ClickFuncionario{
        void click_Funcionario(Funcionario funcionario);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{ // esse metado vai criar vacuracao com variaveis recyclerview com lista

        CardView cardView;
        TextView txtView_Nome, txtView_Idade;
        ImageView imageView;
        ProgressBar progressBar;

        public ViewHolder(View itemView){
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.cardView_ListFuncionario_Item);
            txtView_Nome = (TextView) itemView.findViewById(R.id.txtView_ListFuncionario_Nome);
            txtView_Idade = (TextView) itemView.findViewById(R.id.txtView_ListFuncionario_Idade);
            imageView = (ImageView) itemView.findViewById(R.id.imageView_ListFuncionario_Item);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar_ListFuncionario_Item);
        }
    }
}