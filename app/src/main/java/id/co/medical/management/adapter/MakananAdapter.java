package id.co.medical.management.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.co.medical.management.R;
import id.co.medical.management.component.RecordsComponent;
import id.co.medical.management.listener.RecyclerOnClickListener;

public class MakananAdapter extends RecyclerView.Adapter<MakananAdapter.ViewHolder> implements Filterable {

    private Context context;
    private List<RecordsComponent> records;
    private List<RecordsComponent> recordsFull;
    private RecyclerOnClickListener.ClickListener listener;

    @Override
    public Filter getFilter() {
        return pencarian;
    }

    private Filter pencarian = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<RecordsComponent> filteredList = new ArrayList<>();
            if(constraint == null || constraint.length() == 0){
                filteredList.addAll(recordsFull);
            }else{
                String filterPatern = constraint.toString().toLowerCase().trim();
                for(RecordsComponent recordsComponent : recordsFull){
                    if(recordsComponent.getNamaMakanan().toLowerCase().contains(filterPatern)){
                        filteredList.add(recordsComponent);
                    }
                    if(recordsComponent.getJmlKalori().toLowerCase().contains(filterPatern)){
                        filteredList.add(recordsComponent);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            records.clear();
            records.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id._txt_makanan) TextView txtMakanan;
        @BindView(R.id._txt_jml_kalori) TextView txtJmlKalori;
        @BindView(R.id._btnPilihMakanan) Button btnPilihMakanan;
        private WeakReference<RecyclerOnClickListener.ClickListener> listenerRef;

        public ViewHolder(View v, RecyclerOnClickListener.ClickListener listener) {

            super(v);
            ButterKnife.bind(this, v);
            listenerRef = new WeakReference<>(listener);

            btnPilihMakanan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listenerRef.get().onClick(v, getAdapterPosition());
                }
            });
        }
    }

    public MakananAdapter(Context context, List<RecordsComponent> records, RecyclerOnClickListener.ClickListener listener) {

        this.context = context;
        this.records = records;
        this.listener = listener;
        recordsFull = new ArrayList<>(records);
    }

    @Override
    public MakananAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_recycler_makanan, viewGroup, false);
        ViewHolder holder = new ViewHolder(v, listener);
        return holder;
    }

    @Override
    public void onBindViewHolder(MakananAdapter.ViewHolder viewHolder, int i) {
        final RecordsComponent recordsComponent = records.get(i);
        viewHolder.txtMakanan.setText(recordsComponent.getNamaMakanan());
        viewHolder.txtJmlKalori.setText(recordsComponent.getJmlKalori());
    }

    @Override
    public int getItemCount() {
        return records.size();
    }
}
