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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class BiayaFragment extends Fragment {

    private BiayaViewModel viewModel;
    private BiayaAdapter adapter;
    private RecyclerView rvBiaya;
    private View layoutEmpty;
    private TextView tvTotalBiayaBulan;

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
        ExtendedFloatingActionButton fabTambah = view.findViewById(R.id.fab_tambah_biaya);

        viewModel = new ViewModelProvider(requireActivity()).get(BiayaViewModel.class);

        adapter = new BiayaAdapter(this::showDeleteConfirmation);
        rvBiaya.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvBiaya.setAdapter(adapter);

        viewModel.getAllBiaya().observe(getViewLifecycleOwner(), biayaList -> {
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

    private void showDeleteConfirmation(BiayaLain biaya) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Hapus Biaya")
                .setMessage("Apakah Anda yakin ingin menghapus biaya \"" + biaya.getKeterangan() + "\"?")
                .setPositiveButton("Hapus", (dialog, which) -> viewModel.deleteBiaya(biaya))
                .setNegativeButton("Batal", null)
                .show();
    }
}
