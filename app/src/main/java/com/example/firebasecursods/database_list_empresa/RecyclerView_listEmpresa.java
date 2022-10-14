package com.example.firebasecursods.database_list_empresa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebasecursods.R;

import java.util.List;

public class RecyclerView_listEmpresa extends RecyclerView.Adapter<RecyclerView_listEmpresa.ViewHolder> {

    private Context context;
    private List<Empresa> empresas;
    private ClickEmpresa clickEmpresa;

    public RecyclerView_listEmpresa(Context context, List<Empresa> empresas, ClickEmpresa clickEmpresa){
        this.context = context;
        this.empresas = empresas;
        this.clickEmpresa = clickEmpresa;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_empresa_recycleview, parent, false);

        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Empresa empresa = empresas.get(position);

        System.out.println("empresas.get(position) " + empresas.get(position));

        holder.textViewNome.setText(empresa.getNome());

        System.out.println("empresa.getNome() " + empresa.getNome());

        holder.cardView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                clickEmpresa.clickEmpresa(empresa);
            }
        });
    }

    @Override
    public int getItemCount() {  // metado vai retonar tamanho list
        return empresas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CardView cardView;
        TextView textViewNome;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.cardView_listItem);
            textViewNome = (TextView) itemView.findViewById(R.id.textView_list_item);
        }
    }

    public interface ClickEmpresa{
        void clickEmpresa(Empresa empresa);
    }
}
