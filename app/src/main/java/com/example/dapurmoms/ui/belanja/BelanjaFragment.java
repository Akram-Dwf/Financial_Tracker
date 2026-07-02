package com.example.dapurmoms.ui.belanja;

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
import com.example.dapurmoms.data.database.entity.BelanjaBahan;
import com.example.dapurmoms.util.CurrencyFormatter;
import com.example.dapurmoms.util.MonthYearPickerDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import com.google.android.material.datepicker.MaterialDatePicker;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class BelanjaFragment extends Fragment {

    private void showEditDialog(com.example.dapurmoms.data.database.entity.BelanjaBahan belanja) {
        viewModel.setBelanjaToEdit(belanja);
        TambahBelanjaDialogFragment dialog = new TambahBelanjaDialogFragment();
        dialog.show(getChildFragmentManager(), "EditBelanjaDialog");
    }

    private BelanjaViewModel viewModel;
    private BelanjaAdapter adapter;
    private RecyclerView rvBelanja;
    private View layoutEmpty;
    private TextView tvTotalBelanjaBulan;
    private Chip chipBulanBelanja;
    private Chip chipTanggalBelanja;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_belanja, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvBelanja = view.findViewById(R.id.rv_belanja);
        layoutEmpty = view.findViewById(R.id.layout_empty);
        tvTotalBelanjaBulan = view.findViewById(R.id.tv_total_belanja_bulan);
        chipBulanBelanja = view.findViewById(R.id.chip_bulan_belanja);
        chipTanggalBelanja = view.findViewById(R.id.chip_tanggal_belanja);
        ExtendedFloatingActionButton fabTambah = view.findViewById(R.id.fab_tambah_belanja);

        viewModel = new ViewModelProvider(requireActivity()).get(BelanjaViewModel.class);

        adapter = new BelanjaAdapter(this::showDeleteConfirmation, this::showEditDialog);
        rvBelanja.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvBelanja.setAdapter(adapter);

        // Filter bulan
        chipBulanBelanja.setOnClickListener(v -> showMonthPicker());
        viewModel.getSelectedMonth().observe(getViewLifecycleOwner(), cal -> {
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", new Locale("id", "ID"));
            chipBulanBelanja.setText(sdf.format(cal.getTime()));
        });
        
        // Filter tanggal
        chipTanggalBelanja.setOnClickListener(v -> showDatePicker());
        chipTanggalBelanja.setOnCloseIconClickListener(v -> viewModel.clearDate());
        
        viewModel.getSelectedDate().observe(getViewLifecycleOwner(), cal -> {
            if (cal != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", new Locale("id", "ID"));
                chipTanggalBelanja.setText(sdf.format(cal.getTime()));
                chipTanggalBelanja.setCloseIconVisible(true);
                chipBulanBelanja.setAlpha(0.5f);
            } else {
                chipTanggalBelanja.setText("Pilih Tanggal");
                chipTanggalBelanja.setCloseIconVisible(false);
                chipBulanBelanja.setAlpha(1.0f);
            }
        });

        viewModel.getBelanjaList().observe(getViewLifecycleOwner(), belanjaList -> {
            adapter.setData(belanjaList);
            if (belanjaList == null || belanjaList.isEmpty()) {
                rvBelanja.setVisibility(View.GONE);
                layoutEmpty.setVisibility(View.VISIBLE);
            } else {
                rvBelanja.setVisibility(View.VISIBLE);
                layoutEmpty.setVisibility(View.GONE);
            }
        });

        viewModel.getTotalBelanja().observe(getViewLifecycleOwner(), total -> {
            long value = total != null ? total : 0L;
            tvTotalBelanjaBulan.setText(CurrencyFormatter.formatRupiah(value));
        });

        fabTambah.setOnClickListener(v -> {
            TambahBelanjaDialogFragment dialog = new TambahBelanjaDialogFragment();
            dialog.show(getChildFragmentManager(), "TambahBelanjaDialog");
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
        dialog.show(getParentFragmentManager(), "MONTH_YEAR_PICKER_BELANJA");
    }

    private void showDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Pilih Tanggal Belanja")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();
                
        datePicker.addOnPositiveButtonClickListener(selection -> {
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            cal.setTimeInMillis(selection);
            viewModel.setDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        });
        
        datePicker.show(getParentFragmentManager(), "DATE_PICKER_BELANJA");
    }

    private void showDeleteConfirmation(BelanjaBahan belanja) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Hapus Belanja")
                .setMessage("Apakah Anda yakin ingin menghapus data belanja " + belanja.getNamaBahan() + "?")
                .setPositiveButton("Hapus", (dialog, which) -> viewModel.deleteBelanja(belanja))
                .setNegativeButton("Batal", null)
                .show();
    }
}
