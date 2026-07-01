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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class BelanjaFragment extends Fragment {

    private BelanjaViewModel viewModel;
    private BelanjaAdapter adapter;
    private RecyclerView rvBelanja;
    private View layoutEmpty;
    private TextView tvTotalBelanjaBulan;
    private Chip chipBulanBelanja;

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
        ExtendedFloatingActionButton fabTambah = view.findViewById(R.id.fab_tambah_belanja);

        viewModel = new ViewModelProvider(requireActivity()).get(BelanjaViewModel.class);

        adapter = new BelanjaAdapter(this::showDeleteConfirmation);
        rvBelanja.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvBelanja.setAdapter(adapter);

        // Filter bulan
        chipBulanBelanja.setOnClickListener(v -> showMonthPicker());
        viewModel.getSelectedMonth().observe(getViewLifecycleOwner(), cal -> {
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", new Locale("id", "ID"));
            chipBulanBelanja.setText(sdf.format(cal.getTime()));
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

    private void showDeleteConfirmation(BelanjaBahan belanja) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Hapus Belanja")
                .setMessage("Apakah Anda yakin ingin menghapus data belanja " + belanja.getNamaBahan() + "?")
                .setPositiveButton("Hapus", (dialog, which) -> viewModel.deleteBelanja(belanja))
                .setNegativeButton("Batal", null)
                .show();
    }
}
