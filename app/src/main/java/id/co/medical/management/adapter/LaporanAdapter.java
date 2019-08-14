package id.co.medical.management.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.co.medical.management.R;
import id.co.medical.management.component.RecordsComponent;

public class LaporanAdapter extends RecyclerView.Adapter<LaporanAdapter.ViewHolder> {

    private Context context;
    private List<RecordsComponent> records;

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id._txt_jml_kalori) TextView txtJmlKalori;
        @BindView(R.id._txt_makanan) TextView txtMakanan;
        @BindView(R.id._txt_waktu) TextView txtWaktu;

        public ViewHolder(View v) {

            super(v);
            ButterKnife.bind(this, v);
        }
    }

    public LaporanAdapter(Context context, List<RecordsComponent> records) {

        this.context = context;
        this.records = records;
    }

    @Override
    public LaporanAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_recycler_laporan, viewGroup, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(LaporanAdapter.ViewHolder viewHolder, int i) {
        final RecordsComponent recordsComponent = records.get(i);
        viewHolder.txtJmlKalori.setText(recordsComponent.getJmlKalori());
        viewHolder.txtMakanan.setText(recordsComponent.getNamaMakanan());
        viewHolder.txtWaktu.setText(recordsComponent.getWaktu());
    }

    @Override
    public int getItemCount() {
        return records.size();
    }
}
