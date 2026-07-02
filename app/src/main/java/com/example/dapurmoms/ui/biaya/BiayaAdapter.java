package com.example.dapurmoms.ui.biaya;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dapurmoms.R;
import com.example.dapurmoms.data.database.entity.BiayaLain;
import com.example.dapurmoms.util.CurrencyFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BiayaAdapter extends RecyclerView.Adapter<BiayaAdapter.BiayaViewHolder> {

    private List<BiayaLain> biayaList = new ArrayList<>();
    private final OnDeleteClickListener deleteClickListener;
    private final OnEditClickListener editClickListener;
    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("dd MMM yyyy", new Locale("id", "ID"));

    public interface OnDeleteClickListener {
        void onDeleteClick(BiayaLain biaya);
    }

    public interface OnEditClickListener {
        void onEditClick(BiayaLain biaya);
    }

    public BiayaAdapter(OnDeleteClickListener deleteListener, OnEditClickListener editListener) {
        this.deleteClickListener = deleteListener;
        this.editClickListener = editListener;
    }

    public void setData(List<BiayaLain> newList) {
        List<BiayaLain> oldList = this.biayaList;
        this.biayaList = newList != null ? newList : new ArrayList<>();

        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return oldList.size();
            }

            @Override
            public int getNewListSize() {
                return biayaList.size();
            }

            @Override
            public boolean areItemsTheSame(int oldPos, int newPos) {
                return oldList.get(oldPos).getId() == biayaList.get(newPos).getId();
            }

            @Override
            public boolean areContentsTheSame(int oldPos, int newPos) {
                BiayaLain oldItem = oldList.get(oldPos);
                BiayaLain newItem = biayaList.get(newPos);
                return oldItem.getId() == newItem.getId()
                        && oldItem.getTanggal() == newItem.getTanggal()
                        && oldItem.getJumlah() == newItem.getJumlah()
                        && oldItem.getKeterangan().equals(newItem.getKeterangan());
            }
        });
        result.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public BiayaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_biaya, parent, false);
        return new BiayaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BiayaViewHolder holder, int position) {
        BiayaLain biaya = biayaList.get(position);
        holder.bind(biaya);
    }

    @Override
    public int getItemCount() {
        return biayaList.size();
    }

    class BiayaViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvTanggal;
        private final TextView tvKeterangan;
        private final TextView tvKategori;
        private final TextView tvJumlah;
        private final ImageButton btnDelete;
        private final ImageButton btnEdit;

        BiayaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTanggal = itemView.findViewById(R.id.tv_tanggal);
            tvKeterangan = itemView.findViewById(R.id.tv_keterangan);
            tvKategori = itemView.findViewById(R.id.tv_kategori);
            tvJumlah = itemView.findViewById(R.id.tv_jumlah);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            btnEdit = itemView.findViewById(R.id.btn_edit);
        }

        void bind(BiayaLain biaya) {
            tvTanggal.setText(dateFormat.format(new Date(biaya.getTanggal())));
            tvKeterangan.setText(biaya.getKeterangan());
            tvKategori.setText(biaya.getKategori());
            tvJumlah.setText(CurrencyFormatter.formatRupiah(biaya.getJumlah()));

            btnDelete.setOnClickListener(v -> {
                if (deleteClickListener != null) {
                    deleteClickListener.onDeleteClick(biaya);
                }
            });

            btnEdit.setOnClickListener(v -> {
                if (editClickListener != null) {
                    editClickListener.onEditClick(biaya);
                }
            });
        }
    }
}
