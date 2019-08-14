package id.co.medical.management.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
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
import id.co.medical.management.api.KaloriApi;
import id.co.medical.management.component.RecordsComponent;
import id.co.medical.management.component.ResponseComponent;
import id.co.medical.management.component.SharedPreferencesComponent;
import id.co.medical.management.listener.RecyclerOnClickListener;
import id.co.medical.management.utils.RetrofitUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MakananAdapter extends RecyclerView.Adapter<MakananAdapter.ViewHolder> implements Filterable {

    private Context context;
    private List<RecordsComponent> records;
    private List<RecordsComponent> recordsFull;

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
        @BindView(R.id._btnPilihMakanan) Button btnPilihMakanan; //make button click

        public ViewHolder(View v) {

            super(v);
            ButterKnife.bind(this, v);
        }
    }

    public MakananAdapter(Context context, List<RecordsComponent> records) {

        this.context = context;
        this.records = records;
        recordsFull = new ArrayList<>(records);
    }

    @Override
    public MakananAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_recycler_makanan, viewGroup, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(MakananAdapter.ViewHolder viewHolder, int i) {
        final RecordsComponent recordsComponent = records.get(i);
        viewHolder.txtMakanan.setText(recordsComponent.getNamaMakanan());
        viewHolder.txtJmlKalori.setText(recordsComponent.getJmlKalori());

        //how to make button onClick
        viewHolder.btnPilihMakanan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog progressDialog = new ProgressDialog(viewHolder.itemView.getContext());
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Tunggu...");
                progressDialog.show();

                String id = new SharedPreferencesComponent(viewHolder.itemView.getContext()).getDataId();
                String namaMakanan = records.get(viewHolder.getAdapterPosition()).getNamaMakanan();

                Retrofit retrofit = RetrofitUtil.getClient();
                KaloriApi kaloriApi = retrofit.create(KaloriApi.class);
                Call<ResponseComponent> call = kaloriApi.updateKalori(id, namaMakanan);
                call.enqueue(new Callback<ResponseComponent>() {
                    @Override
                    public void onResponse(Call<ResponseComponent> call, Response<ResponseComponent> response) {
                        String error = response.body() != null ? response.body().getError() : null;
                        String status = response.body() != null ? response.body().getStatus() : null;
                        progressDialog.dismiss();
                        if(response.isSuccessful()){
                            if(error != null){
                                switch(error){
                                    case "0":
                                        Toast.makeText(viewHolder.itemView.getContext(), status, Toast.LENGTH_SHORT).show();
                                        break;
                                    case "1":
                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(viewHolder.itemView.getContext());
                                        alertDialogBuilder.setTitle("Peringatan!");
                                        alertDialogBuilder
                                                .setMessage(status)
                                                .setCancelable(false)
                                                .setPositiveButton("Iya",new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                    }
                                                });
                                        AlertDialog alertDialog = alertDialogBuilder.create();
                                        alertDialog.show();
                                        break;
                                }
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<ResponseComponent> call, Throwable t) {
                        progressDialog.dismiss();
                        Snackbar.make(viewHolder.itemView, "Kesalahan pada jaringan!", Snackbar.LENGTH_LONG)
                                .setAction("Oke", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                    }
                                })
                                .setDuration(3000)
                                .show();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return records.size();
    }
}
