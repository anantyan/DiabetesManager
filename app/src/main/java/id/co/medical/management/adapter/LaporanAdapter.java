package id.co.medical.management.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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

    public LaporanAdapter(Context context, List<RecordsComponent> records) {
        this.context = context;
        this.records = records;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id._txt_jml_kalori) TextView txtJmlKalori;
        @BindView(R.id._txt_makanan) TextView txtMakanan;
        @BindView(R.id._txt_waktu) TextView txtWaktu;

        public ViewHolder(View v) {

            super(v);
            ButterKnife.bind(this, v);
        }
    }

    @Override
    public LaporanAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_recycler_laporan, viewGroup, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final LaporanAdapter.ViewHolder viewHolder, int i) {
        RecordsComponent recordsComponent = records.get(i);
        if(recordsComponent.getUkuran_makanan().equals("")){
            viewHolder.txtJmlKalori.setText(recordsComponent.getJumlah_kalori());
        }else{
            String a = recordsComponent.getJumlah_kalori();
            String b = recordsComponent.getUkuran_makanan();
            viewHolder.txtJmlKalori.setText(String.format("%s (%s)", a, b));
        }
        viewHolder.txtMakanan.setText(recordsComponent.getNama_makanan());
        viewHolder.txtWaktu.setText(recordsComponent.getWaktu());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String id = records.get(viewHolder.getAdapterPosition()).getId();
                //Toast.makeText(context, id, Toast.LENGTH_SHORT).show();
            }
        });
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //String id = records.get(viewHolder.getAdapterPosition()).getId();
                //Toast.makeText(context, id, Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return records.size();
    }
}
