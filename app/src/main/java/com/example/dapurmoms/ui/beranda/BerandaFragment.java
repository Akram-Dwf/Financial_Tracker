package com.example.dapurmoms.ui.beranda;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.dapurmoms.R;
import com.example.dapurmoms.util.CurrencyFormatter;

public class BerandaFragment extends Fragment {

    private BerandaViewModel viewModel;
    private TextView tvTotalPesanan;
    private TextView tvTotalBelanja;
    private TextView tvTotalBiaya;
    private TextView tvKeuntungan;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_beranda, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvTotalPesanan = view.findViewById(R.id.tv_total_pesanan);
        tvTotalBelanja = view.findViewById(R.id.tv_total_belanja);
        tvTotalBiaya = view.findViewById(R.id.tv_total_biaya);
        tvKeuntungan = view.findViewById(R.id.tv_keuntungan);

        viewModel = new ViewModelProvider(this).get(BerandaViewModel.class);

        viewModel.getTotalPesanan().observe(getViewLifecycleOwner(), value -> {
            long total = value != null ? value : 0L;
            tvTotalPesanan.setText(CurrencyFormatter.formatRupiah(total));
        });

        viewModel.getTotalBelanja().observe(getViewLifecycleOwner(), value -> {
            long total = value != null ? value : 0L;
            tvTotalBelanja.setText(CurrencyFormatter.formatRupiah(total));
        });

        viewModel.getTotalBiaya().observe(getViewLifecycleOwner(), value -> {
            long total = value != null ? value : 0L;
            tvTotalBiaya.setText(CurrencyFormatter.formatRupiah(total));
        });

        viewModel.getKeuntungan().observe(getViewLifecycleOwner(), value -> {
            long keuntungan = value != null ? value : 0L;
            tvKeuntungan.setText(CurrencyFormatter.formatRupiah(keuntungan));

            if (keuntungan >= 0) {
                tvKeuntungan.setTextColor(ContextCompat.getColor(requireContext(),
                        com.google.android.material.R.color.design_default_color_primary));
            } else {
                tvKeuntungan.setTextColor(ContextCompat.getColor(requireContext(),
                        com.google.android.material.R.color.design_default_color_error));
            }
        });
    }
}
