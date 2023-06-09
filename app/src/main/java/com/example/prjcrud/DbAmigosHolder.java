package com.example.prjcrud;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class DbAmigosHolder extends RecyclerView.ViewHolder {
    public TextView nmAmigo;
    public TextView    vlCelular;
    public ImageButton btnEditar;
    public ImageButton btnExcluir;
    public ImageButton btnRedo;

    public DbAmigosHolder(View itemView) {
        super(itemView);
        nmAmigo = (TextView) itemView.findViewById(R.id.nmAmigo);
        vlCelular = (TextView) itemView.findViewById(R.id.vlCelular);
        btnEditar = (ImageButton) itemView.findViewById(R.id.btnEditar);
        btnExcluir = (ImageButton) itemView.findViewById(R.id.btnExcluir);
        btnRedo = (ImageButton) itemView.findViewById(R.id.btnRedo);
    }
}

