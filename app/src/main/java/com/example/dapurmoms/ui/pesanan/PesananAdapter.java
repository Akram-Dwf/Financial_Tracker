package com.example.dapurmoms.ui.pesanan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dapurmoms.R;
import com.example.dapurmoms.data.database.entity.Pesanan;
import com.example.dapurmoms.util.CurrencyFormatter;
import com.google.android.material.chip.Chip;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PesananAdapter extends RecyclerView.Adapter<PesananAdapter.PesananViewHolder> {

    private List<Pesanan> pesananList = new ArrayList<>();
    private final OnDeleteClickListener deleteClickListener;
    private final OnEditClickListener editClickListener;
    private final OnPrintClickListener printClickListener;
    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("dd MMM yyyy", new Locale("id", "ID"));

    public interface OnDeleteClickListener {
        void onDeleteClick(Pesanan pesanan);
    }

    public interface OnEditClickListener {
        void onEditClick(Pesanan pesanan);
    }

    public interface OnPrintClickListener {
        void onPrintClick(Pesanan pesanan);
    }

    public PesananAdapter(OnDeleteClickListener deleteListener, OnEditClickListener editListener, OnPrintClickListener printListener) {
        this.deleteClickListener = deleteListener;
        this.editClickListener = editListener;
        this.printClickListener = printListener;
    }

    public void setData(List<Pesanan> newList) {
        List<Pesanan> oldList = this.pesananList;
        this.pesananList = newList != null ? newList : new ArrayList<>();

        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return oldList.size();
            }

            @Override
            public int getNewListSize() {
                return pesananList.size();
            }

            @Override
            public boolean areItemsTheSame(int oldPos, int newPos) {
                return oldList.get(oldPos).getId() == pesananList.get(newPos).getId();
            }

            @Override
            public boolean areContentsTheSame(int oldPos, int newPos) {
                Pesanan oldItem = oldList.get(oldPos);
                Pesanan newItem = pesananList.get(newPos);
                boolean isCatatanSame = (oldItem.getCatatan() == null && newItem.getCatatan() == null) ||
                        (oldItem.getCatatan() != null && oldItem.getCatatan().equals(newItem.getCatatan()));
                        
                return oldItem.getId() == newItem.getId()
                        && oldItem.getTanggal() == newItem.getTanggal()
                        && oldItem.getTotal() == newItem.getTotal()
                        && oldItem.getJumlah() == newItem.getJumlah()
                        && oldItem.getHargaSatuan() == newItem.getHargaSatuan()
                        && oldItem.getNamaPemesan().equals(newItem.getNamaPemesan())
                        && oldItem.getNamaMenu().equals(newItem.getNamaMenu())
                        && isCatatanSame
                        && oldItem.getMetodePembayaran().equals(newItem.getMetodePembayaran());
            }
        });
        result.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public PesananViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pesanan, parent, false);
        return new PesananViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PesananViewHolder holder, int position) {
        Pesanan pesanan = pesananList.get(position);
        holder.bind(pesanan);
    }

    @Override
    public int getItemCount() {
        return pesananList.size();
    }

    class PesananViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvTanggal;
        private final TextView tvNama;
        private final TextView tvMenu;
        private final TextView tvQty;
        private final TextView tvTotal;
        private final TextView tvCatatan;
        private final ImageButton btnDelete;
        private final ImageButton btnEdit;
        private final ImageButton btnPrint;
        private final Chip chipMetode;

        PesananViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTanggal = itemView.findViewById(R.id.tv_tanggal);
            tvNama = itemView.findViewById(R.id.tv_nama);
            tvMenu = itemView.findViewById(R.id.tv_menu);
            tvQty = itemView.findViewById(R.id.tv_qty);
            tvTotal = itemView.findViewById(R.id.tv_total);
            tvCatatan = itemView.findViewById(R.id.tv_catatan);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnPrint = itemView.findViewById(R.id.btn_print);
            chipMetode = itemView.findViewById(R.id.chip_metode_pesanan);
        }

        void bind(Pesanan pesanan) {
            tvTanggal.setText(dateFormat.format(new Date(pesanan.getTanggal())));
            if (pesanan.getNamaPemesan().equals("-")) {
                tvNama.setVisibility(View.GONE);
            } else {
                tvNama.setVisibility(View.VISIBLE);
                tvNama.setText(pesanan.getNamaPemesan());
            }
            tvMenu.setText(pesanan.getNamaMenu());
            tvQty.setText(pesanan.getJumlah() + " x " + CurrencyFormatter.formatRupiah(pesanan.getHargaSatuan()));
            tvTotal.setText(CurrencyFormatter.formatRupiah(pesanan.getTotal()));

            if (pesanan.getCatatan() != null && !pesanan.getCatatan().trim().isEmpty()) {
                tvCatatan.setVisibility(View.VISIBLE);
                tvCatatan.setText("Catatan: " + pesanan.getCatatan());
            } else {
                tvCatatan.setVisibility(View.GONE);
            }

            String metode = pesanan.getMetodePembayaran();
            if ("Transfer".equals(metode)) {
                chipMetode.setText("🏦 Transfer");
                chipMetode.setChipBackgroundColor(android.content.res.ColorStateList.valueOf(0xFF2196F3));
            } else if ("Piutang".equals(metode)) {
                chipMetode.setText("📋 Piutang");
                chipMetode.setChipBackgroundColor(android.content.res.ColorStateList.valueOf(0xFFFF9800));
            } else {
                chipMetode.setText("💵 Cash");
                chipMetode.setChipBackgroundColor(android.content.res.ColorStateList.valueOf(0xFF4CAF50));
            }

            btnDelete.setOnClickListener(v -> {
                if (deleteClickListener != null) {
                    deleteClickListener.onDeleteClick(pesanan);
                }
            });

            btnEdit.setOnClickListener(v -> {
                if (editClickListener != null) {
                    editClickListener.onEditClick(pesanan);
                }
            });

            btnPrint.setOnClickListener(v -> {
                if (printClickListener != null) {
                    printClickListener.onPrintClick(pesanan);
                }
            });
        }
    }
}
