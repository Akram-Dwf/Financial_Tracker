package com.example.dapurmoms.ui.pesanan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dapurmoms.R;
import com.example.dapurmoms.data.database.entity.Pesanan;
import com.example.dapurmoms.util.CurrencyFormatter;
import com.example.dapurmoms.util.MonthYearPickerDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class PesananFragment extends Fragment {

    private PesananViewModel viewModel;
    private PesananAdapter adapter;
    private RecyclerView rvPesanan;
    private View layoutEmpty;
    private TextView tvTotalPesananBulan;
    private Chip chipBulanPesanan;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pesanan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvPesanan = view.findViewById(R.id.rv_pesanan);
        layoutEmpty = view.findViewById(R.id.layout_empty);
        tvTotalPesananBulan = view.findViewById(R.id.tv_total_pesanan_bulan);
        chipBulanPesanan = view.findViewById(R.id.chip_bulan_pesanan);
        ExtendedFloatingActionButton fabTambah = view.findViewById(R.id.fab_tambah_pesanan);

        viewModel = new ViewModelProvider(requireActivity()).get(PesananViewModel.class);

        adapter = new PesananAdapter(this::showDeleteConfirmation, this::showEditDialog);
        rvPesanan.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvPesanan.setAdapter(adapter);

        // Filter bulan
        chipBulanPesanan.setOnClickListener(v -> showMonthPicker());
        viewModel.getSelectedMonth().observe(getViewLifecycleOwner(), cal -> {
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", new Locale("id", "ID"));
            chipBulanPesanan.setText(sdf.format(cal.getTime()));
        });

        viewModel.getPesananList().observe(getViewLifecycleOwner(), pesananList -> {
            adapter.setData(pesananList);
            if (pesananList == null || pesananList.isEmpty()) {
                rvPesanan.setVisibility(View.GONE);
                layoutEmpty.setVisibility(View.VISIBLE);
            } else {
                rvPesanan.setVisibility(View.VISIBLE);
                layoutEmpty.setVisibility(View.GONE);
            }
        });

        viewModel.getTotalUangMasuk().observe(getViewLifecycleOwner(), total -> {
            long value = total != null ? total : 0L;
            tvTotalPesananBulan.setText(CurrencyFormatter.formatRupiah(value));
        });

        fabTambah.setOnClickListener(v -> {
            TambahPesananDialogFragment dialog = new TambahPesananDialogFragment();
            dialog.show(getChildFragmentManager(), "TambahPesananDialog");
        });
    }

    private void showMonthPicker() {
        Calendar current = viewModel.getSelectedMonth().getValue();
        if (current == null) current = Calendar.getInstance();

        MonthYearPickerDialog dialog = MonthYearPickerDialog.newInstance(
                current.get(Calendar.YEAR),
                current.get(Calendar.MONTH)
        );
        dialog.setListener((year, month) -> {
            viewModel.setMonth(year, month);
        });
        dialog.show(getParentFragmentManager(), "MONTH_YEAR_PICKER_PESANAN");
    }

    private void showDeleteConfirmation(Pesanan pesanan) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Hapus Pesanan")
                .setMessage("Apakah Anda yakin ingin menghapus pesanan dari " + pesanan.getNamaPemesan() + "?")
                .setPositiveButton("Hapus", (dialog, which) -> viewModel.deletePesanan(pesanan))
                .setNegativeButton("Batal", null)
                .show();
    }

    private void showEditDialog(Pesanan pesanan) {
        viewModel.setPesananToEdit(pesanan);
        TambahPesananDialogFragment dialog = new TambahPesananDialogFragment();
        dialog.show(getChildFragmentManager(), "EditPesananDialog");
    }
}
