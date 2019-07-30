package com.diabetes.manager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.diabetes.manager.R;
import com.diabetes.manager.component.RecordsComponent;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private Context context;
    private List<RecordsComponent> records;

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id._txt_nomor) TextView txtNomor;
        @BindView(R.id._txt_kalori) TextView txtKalori;
        @BindView(R.id._txt_makanan) TextView txtMakanan;
        @BindView(R.id._txt_jam) TextView txtJam;

        public ViewHolder(View v) {

            super(v);
            ButterKnife.bind(this, v);
        }
    }

    public RecyclerAdapter(Context context, List<RecordsComponent> records) {

        this.context = context;
        this.records = records;
    }

    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_recycler, viewGroup, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter.ViewHolder viewHolder, int i) {
        final RecordsComponent recordsComponent = records.get(i);
        viewHolder.txtNomor.setText(recordsComponent.getNomor());
        viewHolder.txtKalori.setText(recordsComponent.getKalori());
        viewHolder.txtMakanan.setText(recordsComponent.getNamaMakanan());
        viewHolder.txtJam.setText(recordsComponent.getJam());
    }

    @Override
    public int getItemCount() {
        return records.size();
    }
}
