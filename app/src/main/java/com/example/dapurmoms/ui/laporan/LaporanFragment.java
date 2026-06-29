package com.example.dapurmoms.ui.laporan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.dapurmoms.R;
import com.example.dapurmoms.util.CurrencyFormatter;

import java.util.Locale;

public class LaporanFragment extends Fragment {

    private LaporanViewModel viewModel;

    private TextView tvPendapatanPenjualan, tvTotalUangMasuk;
    private TextView tvBiayaBahan, tvTotalBiayaBahan;
    private TextView tvBiayaOperasional, tvTotalBiayaOperasional;
    private TextView tvHppBahan, tvHppOperasional, tvTotalHpp;
    private TextView tvTotalMasuk, tvTotalHppFinal, tvKeuntunganBersih;
    private TextView tvMargin, tvStatus;
    private CardView cardStatus;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_laporan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvPendapatanPenjualan = view.findViewById(R.id.tv_pendapatan_penjualan);
        tvTotalUangMasuk = view.findViewById(R.id.tv_total_uang_masuk);
        tvBiayaBahan = view.findViewById(R.id.tv_biaya_bahan);
        tvTotalBiayaBahan = view.findViewById(R.id.tv_total_biaya_bahan);
        tvBiayaOperasional = view.findViewById(R.id.tv_biaya_operasional);
        tvTotalBiayaOperasional = view.findViewById(R.id.tv_total_biaya_operasional);
        tvHppBahan = view.findViewById(R.id.tv_hpp_bahan);
        tvHppOperasional = view.findViewById(R.id.tv_hpp_operasional);
        tvTotalHpp = view.findViewById(R.id.tv_total_hpp);
        tvTotalMasuk = view.findViewById(R.id.tv_total_masuk);
        tvTotalHppFinal = view.findViewById(R.id.tv_total_hpp_final);
        tvKeuntunganBersih = view.findViewById(R.id.tv_keuntungan_bersih);
        tvMargin = view.findViewById(R.id.tv_margin);
        cardStatus = view.findViewById(R.id.card_status);
        tvStatus = view.findViewById(R.id.tv_status);

        viewModel = new ViewModelProvider(this).get(LaporanViewModel.class);

        observeData();
    }

    private void observeData() {
        viewModel.getTotalUangMasuk().observe(getViewLifecycleOwner(), value -> {
            long total = value != null ? value : 0L;
            tvPendapatanPenjualan.setText(CurrencyFormatter.formatRupiah(total));
            tvTotalUangMasuk.setText(CurrencyFormatter.formatRupiah(total));
            tvTotalMasuk.setText(CurrencyFormatter.formatRupiah(total));
        });

        viewModel.getTotalBelanja().observe(getViewLifecycleOwner(), value -> {
            long total = value != null ? value : 0L;
            tvBiayaBahan.setText(CurrencyFormatter.formatRupiah(total));
            tvTotalBiayaBahan.setText(CurrencyFormatter.formatRupiah(total));
            tvHppBahan.setText(CurrencyFormatter.formatRupiah(total));
        });

        viewModel.getTotalBiaya().observe(getViewLifecycleOwner(), value -> {
            long total = value != null ? value : 0L;
            tvBiayaOperasional.setText(CurrencyFormatter.formatRupiah(total));
            tvTotalBiayaOperasional.setText(CurrencyFormatter.formatRupiah(total));
            tvHppOperasional.setText(CurrencyFormatter.formatRupiah(total));
        });

        viewModel.getTotalHpp().observe(getViewLifecycleOwner(), value -> {
            long total = value != null ? value : 0L;
            tvTotalHpp.setText(CurrencyFormatter.formatRupiah(total));
            tvTotalHppFinal.setText(CurrencyFormatter.formatRupiah(total));
        });

        viewModel.getKeuntungan().observe(getViewLifecycleOwner(), value -> {
            long keuntungan = value != null ? value : 0L;
            tvKeuntunganBersih.setText(CurrencyFormatter.formatRupiah(keuntungan));
            updateStatusCard(keuntungan);
        });

        viewModel.getMargin().observe(getViewLifecycleOwner(), value -> {
            double marginValue = value != null ? value : 0.0;
            tvMargin.setText(String.format(new Locale("id", "ID"), "%.1f%%", marginValue));
        });
    }

    private void updateStatusCard(long keuntungan) {
        if (keuntungan > 0) {
            cardStatus.setCardBackgroundColor(
                    ContextCompat.getColor(requireContext(), android.R.color.holo_green_light));
            tvStatus.setText("🎉 Selamat! Usaha Anda menghasilkan keuntungan. Terus pertahankan!");
            tvStatus.setTextColor(
                    ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark));
        } else if (keuntungan < 0) {
            cardStatus.setCardBackgroundColor(
                    ContextCompat.getColor(requireContext(), android.R.color.holo_red_light));
            tvStatus.setText("⚠️ Perhatian! Usaha Anda mengalami kerugian. Evaluasi pengeluaran Anda.");
            tvStatus.setTextColor(
                    ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark));
        } else {
            cardStatus.setCardBackgroundColor(
                    ContextCompat.getColor(requireContext(), android.R.color.darker_gray));
            tvStatus.setText("Belum ada data transaksi. Mulai catat pesanan dan pengeluaran Anda.");
            tvStatus.setTextColor(
                    ContextCompat.getColor(requireContext(), android.R.color.white));
        }
    }
}
