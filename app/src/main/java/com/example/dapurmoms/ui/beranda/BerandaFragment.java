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
import com.example.dapurmoms.util.MonthYearPickerDialog;
import com.google.android.material.chip.Chip;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class BerandaFragment extends Fragment {

    private BerandaViewModel viewModel;
    private TextView tvTotalPesanan;
    private TextView tvTotalBelanja;
    private TextView tvTotalBiaya;
    private TextView tvKeuntungan;
    private TextView tvInsightMessage;
    private Chip chipBulan;
    
    private TextView tvSaldoKasAktual;
    private TextView tvTotalPiutangBeranda;
    private TextView tvTotalUtangBeranda;
    private View cardPiutangBeranda;
    private View cardUtangBeranda;

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
        tvInsightMessage = view.findViewById(R.id.tv_insight_message);
        chipBulan = view.findViewById(R.id.chip_bulan);
        
        tvSaldoKasAktual = view.findViewById(R.id.tv_saldo_kas_aktual);
        tvTotalPiutangBeranda = view.findViewById(R.id.tv_total_piutang_beranda);
        tvTotalUtangBeranda = view.findViewById(R.id.tv_total_utang_beranda);
        cardPiutangBeranda = view.findViewById(R.id.card_piutang_beranda);
        cardUtangBeranda = view.findViewById(R.id.card_utang_beranda);

        viewModel = new ViewModelProvider(requireActivity()).get(BerandaViewModel.class);

        // Month chip click -> show date picker
        chipBulan.setOnClickListener(v -> showMonthPicker());

        // Observe selected month to update chip text
        viewModel.getSelectedMonth().observe(getViewLifecycleOwner(), cal -> {
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", new Locale("id", "ID"));
            chipBulan.setText(sdf.format(cal.getTime()));
        });

        // Quick Actions setup
        View btnPesanan = view.findViewById(R.id.btn_action_pesanan);
        View btnBelanja = view.findViewById(R.id.btn_action_belanja);
        View btnBiaya = view.findViewById(R.id.btn_action_biaya);

        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = 
                requireActivity().findViewById(R.id.bottom_navigation);

        if (bottomNav != null) {
            btnPesanan.setOnClickListener(v -> bottomNav.setSelectedItemId(R.id.pesananFragment));
            btnBelanja.setOnClickListener(v -> bottomNav.setSelectedItemId(R.id.belanjaFragment));
            btnBiaya.setOnClickListener(v -> bottomNav.setSelectedItemId(R.id.biayaFragment));
        }

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
                        R.color.color_profit));
            } else {
                tvKeuntungan.setTextColor(ContextCompat.getColor(requireContext(),
                        R.color.color_expense));
            }
            updateInsightMessage();
        });

        viewModel.getSaldoKasAktual().observe(getViewLifecycleOwner(), value -> {
            long total = value != null ? value : 0L;
            tvSaldoKasAktual.setText(CurrencyFormatter.formatRupiah(total));
        });

        viewModel.getTotalPiutangAktif().observe(getViewLifecycleOwner(), value -> {
            long total = value != null ? value : 0L;
            tvTotalPiutangBeranda.setText(CurrencyFormatter.formatRupiah(total));
        });

        viewModel.getTotalUtangAktif().observe(getViewLifecycleOwner(), value -> {
            long total = value != null ? value : 0L;
            tvTotalUtangBeranda.setText(CurrencyFormatter.formatRupiah(total));
        });

        cardPiutangBeranda.setOnClickListener(v -> {
            ManageUtangPiutangDialogFragment dialog = ManageUtangPiutangDialogFragment.newInstance(true);
            dialog.show(getChildFragmentManager(), "ManagePiutang");
        });

        cardUtangBeranda.setOnClickListener(v -> {
            ManageUtangPiutangDialogFragment dialog = ManageUtangPiutangDialogFragment.newInstance(false);
            dialog.show(getChildFragmentManager(), "ManageUtang");
        });
    }

    private void updateInsightMessage() {
        long masuk = viewModel.getTotalPesanan().getValue() != null ? viewModel.getTotalPesanan().getValue() : 0L;
        long belanja = viewModel.getTotalBelanja().getValue() != null ? viewModel.getTotalBelanja().getValue() : 0L;
        long biaya = viewModel.getTotalBiaya().getValue() != null ? viewModel.getTotalBiaya().getValue() : 0L;
        long totalKeluar = belanja + biaya;
        long untung = masuk - totalKeluar;

        if (masuk == 0 && totalKeluar == 0) {
            tvInsightMessage.setText("Belum ada data transaksi bulan ini. Mulai catat pesanan Anda!");
        } else if (untung > 0) {
            if (totalKeluar > 0) {
                double persentase = ((double) totalKeluar / masuk) * 100;
                if (persentase < 50) {
                    tvInsightMessage.setText(String.format(new Locale("id", "ID"), "Bagus! Pengeluaran Anda terjaga di %.0f%% dari pemasukan.", persentase));
                } else {
                    tvInsightMessage.setText(String.format(new Locale("id", "ID"), "Keuntungan bulan ini positif, namun pengeluaran mencapai %.0f%%.", persentase));
                }
            } else {
                tvInsightMessage.setText("Luar biasa! 100% Pemasukan bulan ini menjadi keuntungan bersih.");
            }
        } else if (untung < 0) {
            if (masuk > 0) {
                tvInsightMessage.setText("Waspada! Pengeluaran bulan ini sudah melampaui pemasukan.");
            } else {
                tvInsightMessage.setText("Bulan ini ada pengeluaran namun belum ada pemasukan yang tercatat.");
            }
        } else {
            tvInsightMessage.setText("Pemasukan dan Pengeluaran Anda bulan ini seimbang (Break Even).");
        }
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
        dialog.show(getParentFragmentManager(), "MONTH_YEAR_PICKER");
    }
}
