package com.example.dapurmoms.ui.belanja;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dapurmoms.R;
import com.example.dapurmoms.data.database.entity.BelanjaBahan;
import com.example.dapurmoms.util.CurrencyFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BelanjaAdapter extends RecyclerView.Adapter<BelanjaAdapter.BelanjaViewHolder> {

    private List<BelanjaBahan> belanjaList = new ArrayList<>();
    private final OnDeleteClickListener deleteClickListener;
    private final OnEditClickListener editClickListener;
    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("dd MMM yyyy", new Locale("id", "ID"));

    public interface OnDeleteClickListener {
        void onDeleteClick(BelanjaBahan belanja);
    }

    public interface OnEditClickListener {
        void onEditClick(BelanjaBahan belanja);
    }

    public BelanjaAdapter(OnDeleteClickListener deleteListener, OnEditClickListener editListener) {
        this.deleteClickListener = deleteListener;
        this.editClickListener = editListener;
    }

    public void setData(List<BelanjaBahan> newList) {
        List<BelanjaBahan> oldList = this.belanjaList;
        this.belanjaList = newList != null ? newList : new ArrayList<>();

        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return oldList.size();
            }

            @Override
            public int getNewListSize() {
                return belanjaList.size();
            }

            @Override
            public boolean areItemsTheSame(int oldPos, int newPos) {
                return oldList.get(oldPos).getId() == belanjaList.get(newPos).getId();
            }

            @Override
            public boolean areContentsTheSame(int oldPos, int newPos) {
                BelanjaBahan oldItem = oldList.get(oldPos);
                BelanjaBahan newItem = belanjaList.get(newPos);
                return oldItem.getId() == newItem.getId()
                        && oldItem.getTanggal() == newItem.getTanggal()
                        && oldItem.getTotalHarga() == newItem.getTotalHarga()
                        && oldItem.getNamaBahan().equals(newItem.getNamaBahan());
            }
        });
        result.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public BelanjaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_belanja, parent, false);
        return new BelanjaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BelanjaViewHolder holder, int position) {
        BelanjaBahan belanja = belanjaList.get(position);
        holder.bind(belanja);
    }

    @Override
    public int getItemCount() {
        return belanjaList.size();
    }

    class BelanjaViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvTanggal;
        private final TextView tvNama;
        private final TextView tvToko;
        private final TextView tvQty;
        private final TextView tvTotal;
        private final ImageButton btnDelete;
        private final ImageButton btnEdit;

        BelanjaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTanggal = itemView.findViewById(R.id.tv_tanggal);
            tvNama = itemView.findViewById(R.id.tv_nama);
            tvToko = itemView.findViewById(R.id.tv_toko);
            tvQty = itemView.findViewById(R.id.tv_qty);
            tvTotal = itemView.findViewById(R.id.tv_total);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            btnEdit = itemView.findViewById(R.id.btn_edit);
        }

        void bind(BelanjaBahan belanja) {
            tvTanggal.setText(dateFormat.format(new Date(belanja.getTanggal())));
            tvNama.setText(belanja.getNamaBahan());
            if (belanja.getToko().equals("-")) {
                tvToko.setVisibility(View.GONE);
            } else {
                tvToko.setVisibility(View.VISIBLE);
                tvToko.setText(belanja.getToko());
            }

            String volumeStr = belanja.getVolume().equals("-") ? "" : belanja.getVolume().trim();
            String jumlahStr = (belanja.getJumlahUnit() == (long) belanja.getJumlahUnit()) ?
                    String.valueOf((long) belanja.getJumlahUnit()) : String.valueOf(belanja.getJumlahUnit());
            
            String qtyDisplay;
            if (volumeStr.isEmpty()) {
                qtyDisplay = jumlahStr;
            } else {
                if (volumeStr.matches("^\\d.*")) {
                    if (jumlahStr.equals("1")) {
                        qtyDisplay = volumeStr;
                    } else {
                        qtyDisplay = jumlahStr + " x " + volumeStr;
                    }
                } else {
                    qtyDisplay = jumlahStr + " " + volumeStr;
                }
            }
            
            tvQty.setText(qtyDisplay + " x " + CurrencyFormatter.formatRupiah(belanja.getHargaBeli()));
            tvTotal.setText(CurrencyFormatter.formatRupiah(belanja.getTotalHarga()));

            btnDelete.setOnClickListener(v -> {
                if (deleteClickListener != null) {
                    deleteClickListener.onDeleteClick(belanja);
                }
            });

            btnEdit.setOnClickListener(v -> {
                if (editClickListener != null) {
                    editClickListener.onEditClick(belanja);
                }
            });
        }
    }
}
