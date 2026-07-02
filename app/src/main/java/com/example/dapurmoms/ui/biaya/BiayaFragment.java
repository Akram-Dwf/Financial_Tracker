package com.example.dapurmoms.ui.biaya;

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
import com.example.dapurmoms.data.database.entity.BiayaLain;
import com.example.dapurmoms.util.CurrencyFormatter;
import com.example.dapurmoms.util.MonthYearPickerDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import com.google.android.material.datepicker.MaterialDatePicker;

public class BiayaFragment extends Fragment {

    private void showEditDialog(com.example.dapurmoms.data.database.entity.BiayaLain biaya) {
        viewModel.setBiayaToEdit(biaya);
        TambahBiayaDialogFragment dialog = new TambahBiayaDialogFragment();
        dialog.show(getChildFragmentManager(), "EditBiayaDialog");
    }

    private BiayaViewModel viewModel;
    private BiayaAdapter adapter;
    private RecyclerView rvBiaya;
    private View layoutEmpty;
    private TextView tvTotalBiayaBulan;
    private Chip chipBulanBiaya;
    private Chip chipTanggalBiaya;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_biaya, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvBiaya = view.findViewById(R.id.rv_biaya);
        layoutEmpty = view.findViewById(R.id.layout_empty);
        tvTotalBiayaBulan = view.findViewById(R.id.tv_total_biaya_bulan);
        chipBulanBiaya = view.findViewById(R.id.chip_bulan_biaya);
        chipTanggalBiaya = view.findViewById(R.id.chip_tanggal_biaya);
        ExtendedFloatingActionButton fabTambah = view.findViewById(R.id.fab_tambah_biaya);

        viewModel = new ViewModelProvider(requireActivity()).get(BiayaViewModel.class);

        adapter = new BiayaAdapter(this::showDeleteConfirmation, this::showEditDialog);
        rvBiaya.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvBiaya.setAdapter(adapter);

        // Filter bulan
        chipBulanBiaya.setOnClickListener(v -> showMonthPicker());
        viewModel.getSelectedMonth().observe(getViewLifecycleOwner(), cal -> {
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", new Locale("id", "ID"));
            chipBulanBiaya.setText(sdf.format(cal.getTime()));
        });
        
        // Filter tanggal
        chipTanggalBiaya.setOnClickListener(v -> showDatePicker());
        chipTanggalBiaya.setOnCloseIconClickListener(v -> viewModel.clearDate());
        
        viewModel.getSelectedDate().observe(getViewLifecycleOwner(), cal -> {
            if (cal != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", new Locale("id", "ID"));
                chipTanggalBiaya.setText(sdf.format(cal.getTime()));
                chipTanggalBiaya.setCloseIconVisible(true);
                chipBulanBiaya.setAlpha(0.5f);
            } else {
                chipTanggalBiaya.setText("Pilih Tanggal");
                chipTanggalBiaya.setCloseIconVisible(false);
                chipBulanBiaya.setAlpha(1.0f);
            }
        });

        viewModel.getBiayaList().observe(getViewLifecycleOwner(), biayaList -> {
            adapter.setData(biayaList);
            if (biayaList == null || biayaList.isEmpty()) {
                rvBiaya.setVisibility(View.GONE);
                layoutEmpty.setVisibility(View.VISIBLE);
            } else {
                rvBiaya.setVisibility(View.VISIBLE);
                layoutEmpty.setVisibility(View.GONE);
            }
        });

        viewModel.getTotalBiaya().observe(getViewLifecycleOwner(), total -> {
            long value = total != null ? total : 0L;
            tvTotalBiayaBulan.setText(CurrencyFormatter.formatRupiah(value));
        });

        fabTambah.setOnClickListener(v -> {
            TambahBiayaDialogFragment dialog = new TambahBiayaDialogFragment();
            dialog.show(getChildFragmentManager(), "TambahBiayaDialog");
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
        dialog.show(getParentFragmentManager(), "MONTH_YEAR_PICKER_BIAYA");
    }

    private void showDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Pilih Tanggal Biaya")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();
                
        datePicker.addOnPositiveButtonClickListener(selection -> {
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            cal.setTimeInMillis(selection);
            viewModel.setDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        });
        
        datePicker.show(getParentFragmentManager(), "DATE_PICKER_BIAYA");
    }

    private void showDeleteConfirmation(BiayaLain biaya) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Hapus Biaya")
                .setMessage("Apakah Anda yakin ingin menghapus data biaya " + biaya.getKeterangan() + "?")
                .setPositiveButton("Hapus", (dialog, which) -> viewModel.deleteBiaya(biaya))
                .setNegativeButton("Batal", null)
                .show();
    }
}
