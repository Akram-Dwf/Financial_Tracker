package com.example.dapurmoms.ui.trash;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dapurmoms.R;
import com.example.dapurmoms.data.database.entity.BelanjaBahan;
import com.example.dapurmoms.data.database.entity.BiayaLain;
import com.example.dapurmoms.data.database.entity.Pesanan;
import com.example.dapurmoms.util.CurrencyFormatter;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TrashItemAdapter extends RecyclerView.Adapter<TrashItemAdapter.TrashViewHolder> {

    public static final int TYPE_PESANAN = 0;
    public static final int TYPE_BELANJA = 1;
    public static final int TYPE_BIAYA = 2;

    private final int type;
    private List<Object> items = new ArrayList<>();
    private final OnRestoreClickListener restoreClickListener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", new Locale("id", "ID"));

    public interface OnRestoreClickListener {
        void onRestoreClick(Object item);
    }

    public TrashItemAdapter(int type, OnRestoreClickListener restoreClickListener) {
        this.type = type;
        this.restoreClickListener = restoreClickListener;
    }

    public void setData(List<?> newList) {
        List<Object> oldList = this.items;
        this.items = newList != null ? new ArrayList<>(newList) : new ArrayList<>();

        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return oldList.size();
            }

            @Override
            public int getNewListSize() {
                return items.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                Object oldItem = oldList.get(oldItemPosition);
                Object newItem = items.get(newItemPosition);
                if (oldItem.getClass() != newItem.getClass()) return false;

                if (oldItem instanceof Pesanan) {
                    return ((Pesanan) oldItem).getId() == ((Pesanan) newItem).getId();
                } else if (oldItem instanceof BelanjaBahan) {
                    return ((BelanjaBahan) oldItem).getId() == ((BelanjaBahan) newItem).getId();
                } else if (oldItem instanceof BiayaLain) {
                    return ((BiayaLain) oldItem).getId() == ((BiayaLain) newItem).getId();
                }
                return false;
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                Object oldItem = oldList.get(oldItemPosition);
                Object newItem = items.get(newItemPosition);
                return oldItem.equals(newItem);
            }
        });
        result.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public TrashViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trash, parent, false);
        return new TrashViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrashViewHolder holder, int position) {
        Object item = items.get(position);
        long deletedAt = 0;
        long tanggal = 0;

        if (item instanceof Pesanan) {
            Pesanan p = (Pesanan) item;
            deletedAt = p.getDeletedAt();
            tanggal = p.getTanggal();
            holder.tvTitle.setText(p.getNamaPemesan().equals("-") ? "Pelanggan" : p.getNamaPemesan());
            holder.tvSubtitle.setText(p.getNamaMenu() + " (" + p.getJumlah() + " porsi)");
            holder.tvAmount.setText(CurrencyFormatter.formatRupiah(p.getTotal()));
            holder.tvAmount.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.color_income));
        } else if (item instanceof BelanjaBahan) {
            BelanjaBahan b = (BelanjaBahan) item;
            deletedAt = b.getDeletedAt();
            tanggal = b.getTanggal();
            holder.tvTitle.setText(b.getNamaBahan());
            String desc = b.getToko() + " (" + b.getJumlahUnit() + " " + b.getVolume() + ")";
            holder.tvSubtitle.setText(desc);
            holder.tvAmount.setText(CurrencyFormatter.formatRupiah(b.getTotalHarga()));
            holder.tvAmount.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.color_expense));
        } else if (item instanceof BiayaLain) {
            BiayaLain c = (BiayaLain) item;
            deletedAt = c.getDeletedAt();
            tanggal = c.getTanggal();
            holder.tvTitle.setText(c.getKeterangan());
            holder.tvSubtitle.setText(c.getKategori());
            holder.tvAmount.setText(CurrencyFormatter.formatRupiah(c.getJumlah()));
            holder.tvAmount.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.color_expense));
        }

        // Tanggal Transaksi
        holder.tvTanggal.setText(dateFormat.format(new Date(tanggal)));

        // Warning Text: Terhapus X hari lalu (Sisa 30-X hari)
        long diffMs = System.currentTimeMillis() - deletedAt;
        long diffDays = diffMs / (24L * 60 * 60 * 1000);
        if (diffDays < 0) diffDays = 0;
        long remainingDays = 30 - diffDays;
        if (remainingDays < 0) remainingDays = 0;

        String warningText = "Terhapus " + diffDays + " hari lalu (Sisa " + remainingDays + " hari)";
        holder.tvWarning.setText(warningText);

        holder.btnRestore.setOnClickListener(v -> {
            if (restoreClickListener != null) {
                restoreClickListener.onRestoreClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class TrashViewHolder extends RecyclerView.ViewHolder {
        TextView tvTanggal;
        TextView tvTitle;
        TextView tvSubtitle;
        TextView tvWarning;
        TextView tvAmount;
        MaterialButton btnRestore;

        public TrashViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTanggal = itemView.findViewById(R.id.tv_tanggal);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvSubtitle = itemView.findViewById(R.id.tv_subtitle);
            tvWarning = itemView.findViewById(R.id.tv_warning);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            btnRestore = itemView.findViewById(R.id.btn_restore);
        }
    }
}
