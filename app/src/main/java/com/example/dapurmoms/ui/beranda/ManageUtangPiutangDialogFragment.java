package com.example.dapurmoms.ui.beranda;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dapurmoms.R;
import com.example.dapurmoms.data.database.entity.BelanjaBahan;
import com.example.dapurmoms.data.database.entity.BiayaLain;
import com.example.dapurmoms.data.database.entity.Pesanan;
import com.example.dapurmoms.util.CurrencyFormatter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ManageUtangPiutangDialogFragment extends DialogFragment {

    private static final String ARG_IS_PIUTANG = "is_piutang";

    private boolean isPiutang;
    private BerandaViewModel viewModel;
    private TextView tvTitle;
    private TextView tvTotal;
    private View layoutEmptyState;
    private RecyclerView recyclerView;
    private ManageAdapter adapter;

    public static ManageUtangPiutangDialogFragment newInstance(boolean isPiutang) {
        ManageUtangPiutangDialogFragment fragment = new ManageUtangPiutangDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_PIUTANG, isPiutang);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isPiutang = getArguments().getBoolean(ARG_IS_PIUTANG);
        }
        viewModel = new ViewModelProvider(requireActivity()).get(BerandaViewModel.class);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_manage_utang_piutang, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvTitle = view.findViewById(R.id.tv_dialog_title);
        tvTotal = view.findViewById(R.id.tv_dialog_total);
        layoutEmptyState = view.findViewById(R.id.layout_empty_state);
        recyclerView = view.findViewById(R.id.rv_utang_piutang);
        ImageButton btnClose = view.findViewById(R.id.btn_close_dialog);

        btnClose.setOnClickListener(v -> dismiss());

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ManageAdapter();
        recyclerView.setAdapter(adapter);

        if (isPiutang) {
            tvTitle.setText("Kelola Piutang Usaha");
            observePiutang();
        } else {
            tvTitle.setText("Kelola Utang Usaha");
            observeUtang();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawableResource(android.R.color.transparent);
            }
        }
    }

    private void observePiutang() {
        viewModel.getPiutangPesananList().observe(getViewLifecycleOwner(), pesananList -> {
            List<ManageItem> items = new ArrayList<>();
            long grandTotal = 0;
            if (pesananList != null) {
                for (Pesanan p : pesananList) {
                    String customer = p.getNamaPemesan().equals("-") ? "Pelanggan Umum" : p.getNamaPemesan();
                    String menuDetail = p.getMenuSummary() + " (" + p.getJumlah() + " porsi)";
                    items.add(new ManageItem(p, customer, menuDetail, p.getTotal(), p.getTanggal()));
                    grandTotal += p.getTotal();
                }
            }
            tvTotal.setText("Total Piutang: " + CurrencyFormatter.formatRupiah(grandTotal));
            updateList(items);
        });
    }

    private void observeUtang() {
        // We have to observe both BelanjaBahan list and BiayaLain list and combine them
        viewModel.getUtangBelanjaList().observe(getViewLifecycleOwner(), belanjaList -> {
            combineUtangList(belanjaList, viewModel.getUtangBiayaList().getValue());
        });

        viewModel.getUtangBiayaList().observe(getViewLifecycleOwner(), biayaList -> {
            combineUtangList(viewModel.getUtangBelanjaList().getValue(), biayaList);
        });
    }

    private void combineUtangList(List<BelanjaBahan> belanjaList, List<BiayaLain> biayaList) {
        List<ManageItem> items = new ArrayList<>();
        long grandTotal = 0;

        if (belanjaList != null) {
            for (BelanjaBahan b : belanjaList) {
                String qtyStr = (b.getJumlahUnit() == (long) b.getJumlahUnit()) ?
                        String.format("%d", (long) b.getJumlahUnit()) :
                        String.valueOf(b.getJumlahUnit());
                String details = b.getNamaBahan() + " (" + qtyStr + " " + b.getVolume() + ")";
                items.add(new ManageItem(b, b.getToko(), details, b.getTotalHarga(), b.getTanggal()));
                grandTotal += b.getTotalHarga();
            }
        }

        if (biayaList != null) {
            for (BiayaLain b : biayaList) {
                String details = b.getKategori() + (b.getCatatan() != null && !b.getCatatan().isEmpty() ? " - " + b.getCatatan() : "");
                items.add(new ManageItem(b, "Operasional", details, b.getJumlah(), b.getTanggal()));
                grandTotal += b.getJumlah();
            }
        }

        // Sort combined list by date descending
        items.sort((o1, o2) -> Long.compare(o2.date, o1.date));

        tvTotal.setText("Total Utang: " + CurrencyFormatter.formatRupiah(grandTotal));
        updateList(items);
    }

    private void updateList(List<ManageItem> items) {
        if (items.isEmpty()) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.setItems(items);
        }
    }

    private void showLunasiDialog(ManageItem item) {
        String[] options = {"💵 Cash", "🏦 Transfer"};
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Pilih Metode Pelunasan")
                .setItems(options, (dialog, which) -> {
                    String metodeBaru = (which == 1) ? "Transfer" : "Cash";
                    lunasiItem(item, metodeBaru);
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void lunasiItem(ManageItem item, String metodeBaru) {
        if (item.originalObject instanceof Pesanan) {
            viewModel.lunasiPesanan((Pesanan) item.originalObject, metodeBaru);
            Toast.makeText(requireContext(), "Piutang telah dilunasi (" + metodeBaru + ")", Toast.LENGTH_SHORT).show();
        } else if (item.originalObject instanceof BelanjaBahan) {
            viewModel.lunasiBelanja((BelanjaBahan) item.originalObject, metodeBaru);
            Toast.makeText(requireContext(), "Utang belanja telah dilunasi (" + metodeBaru + ")", Toast.LENGTH_SHORT).show();
        } else if (item.originalObject instanceof BiayaLain) {
            viewModel.lunasiBiaya((BiayaLain) item.originalObject, metodeBaru);
            Toast.makeText(requireContext(), "Utang biaya telah dilunasi (" + metodeBaru + ")", Toast.LENGTH_SHORT).show();
        }
    }

    // ==========================================
    // INNER CLASSES (Adapter & Item)
    // ==========================================

    private static class ManageItem {
        Object originalObject;
        String title;
        String subtitle;
        long amount;
        long date;

        ManageItem(Object originalObject, String title, String subtitle, long amount, long date) {
            this.originalObject = originalObject;
            this.title = title;
            this.subtitle = subtitle;
            this.amount = amount;
            this.date = date;
        }
    }

    private class ManageAdapter extends RecyclerView.Adapter<ManageViewHolder> {

        private final List<ManageItem> items = new ArrayList<>();

        public void setItems(List<ManageItem> newItems) {
            items.clear();
            items.addAll(newItems);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ManageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_utang_piutang_manage, parent, false);
            return new ManageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ManageViewHolder holder, int position) {
            ManageItem item = items.get(position);
            holder.bind(item);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    private class ManageViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvTitleItem;
        private final TextView tvAmount;
        private final TextView tvSubtitle;
        private final TextView tvDate;
        private final View btnLunasi;

        ManageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitleItem = itemView.findViewById(R.id.tv_title);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvSubtitle = itemView.findViewById(R.id.tv_subtitle);
            tvDate = itemView.findViewById(R.id.tv_date);
            btnLunasi = itemView.findViewById(R.id.btn_lunasi);
        }

        void bind(ManageItem item) {
            tvTitleItem.setText(item.title);
            tvAmount.setText(CurrencyFormatter.formatRupiah(item.amount));
            tvSubtitle.setText(item.subtitle);

            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", new Locale("id", "ID"));
            tvDate.setText(sdf.format(new Date(item.date)));

            btnLunasi.setOnClickListener(v -> showLunasiDialog(item));
        }
    }
}
