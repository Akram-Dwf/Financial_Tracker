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
import com.example.dapurmoms.util.MonthYearPickerDialog;
import com.google.android.material.chip.Chip;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import android.graphics.Color;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import java.util.ArrayList;
import java.util.List;
public class LaporanFragment extends Fragment {

    private LaporanViewModel viewModel;

    private Chip chipBulanLaporan;
    private TextView tvPendapatanPenjualan, tvTotalUangMasuk;
    private TextView tvBiayaBahan, tvTotalBiayaBahan;
    private TextView tvBiayaOperasional, tvTotalBiayaOperasional;
    private TextView tvHppBahan, tvHppOperasional, tvTotalHpp;
    private TextView tvTotalMasuk, tvTotalHppFinal, tvKeuntunganBersih;
    private TextView tvMargin, tvStatus;
    private CardView cardStatus;
    private PieChart pieChart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_laporan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chipBulanLaporan = view.findViewById(R.id.chip_bulan_laporan);

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
        pieChart = view.findViewById(R.id.pie_chart_laporan);

        viewModel = new ViewModelProvider(requireActivity()).get(LaporanViewModel.class);

        setupPieChart();

        // Month chip click
        chipBulanLaporan.setOnClickListener(v -> showMonthPicker());
        
        // Print button click
        android.widget.ImageButton btnCetak = view.findViewById(R.id.btn_cetak_laporan);
        btnCetak.setOnClickListener(v -> printReport());

        // Observe selected month
        viewModel.getSelectedMonth().observe(getViewLifecycleOwner(), cal -> {
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", new Locale("id", "ID"));
            chipBulanLaporan.setText(sdf.format(cal.getTime()));
        });

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
            updateChartData();
        });

        viewModel.getMargin().observe(getViewLifecycleOwner(), value -> {
            double marginValue = value != null ? value : 0.0;
            tvMargin.setText(String.format(new Locale("id", "ID"), "%.1f%%", marginValue));
        });
    }

    private void updateStatusCard(long keuntungan) {
        if (keuntungan > 0) {
            cardStatus.setCardBackgroundColor(
                    ContextCompat.getColor(requireContext(), R.color.card_beranda_untung));
            tvStatus.setText("🎉 Selamat! Usaha Anda menghasilkan keuntungan. Terus pertahankan!");
            tvStatus.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.color_profit));
        } else if (keuntungan < 0) {
            cardStatus.setCardBackgroundColor(
                    ContextCompat.getColor(requireContext(), R.color.card_beranda_biaya));
            tvStatus.setText("⚠️ Perhatian! Usaha Anda mengalami kerugian. Evaluasi pengeluaran Anda.");
            tvStatus.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.color_expense));
        } else {
            cardStatus.setCardBackgroundColor(
                    ContextCompat.getColor(requireContext(), R.color.md_theme_surface_variant));
            tvStatus.setText("Belum ada data transaksi bulan ini. Mulai catat pesanan dan pengeluaran Anda.");
            tvStatus.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.md_theme_on_surface_variant));
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
        dialog.show(getParentFragmentManager(), "MONTH_YEAR_PICKER_LAPORAN");
    }

    private void setupPieChart() {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setCenterText("Alokasi\nDana");
        pieChart.setCenterTextSize(14f);
        pieChart.setCenterTextColor(ContextCompat.getColor(requireContext(), R.color.md_theme_on_surface));
        pieChart.getLegend().setEnabled(true);
        pieChart.getLegend().setTextColor(ContextCompat.getColor(requireContext(), R.color.md_theme_on_surface));
    }

    private void updateChartData() {
        long belanja = viewModel.getTotalBelanja().getValue() != null ? viewModel.getTotalBelanja().getValue() : 0L;
        long biaya = viewModel.getTotalBiaya().getValue() != null ? viewModel.getTotalBiaya().getValue() : 0L;
        long untung = viewModel.getKeuntungan().getValue() != null ? viewModel.getKeuntungan().getValue() : 0L;

        if (belanja == 0 && biaya == 0 && untung <= 0) {
            pieChart.clear();
            return;
        }

        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        if (belanja > 0) {
            entries.add(new PieEntry(belanja, "Bahan"));
            colors.add(ContextCompat.getColor(requireContext(), R.color.color_warning));
        }
        if (biaya > 0) {
            entries.add(new PieEntry(biaya, "Operasional"));
            colors.add(ContextCompat.getColor(requireContext(), R.color.color_expense));
        }
        if (untung > 0) {
            entries.add(new PieEntry(untung, "Keuntungan"));
            colors.add(ContextCompat.getColor(requireContext(), R.color.color_profit));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.WHITE);

        pieChart.setData(data);
        pieChart.invalidate();
        pieChart.animateY(1000);
    }
    
    private final androidx.activity.result.ActivityResultLauncher<android.content.Intent> createLaporanPdfLauncher = registerForActivityResult(
            new androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                    android.net.Uri uri = result.getData().getData();
                    try {
                        android.os.ParcelFileDescriptor pfd = requireContext().getContentResolver().openFileDescriptor(uri, "w");
                        if (pfd != null) {
                            Calendar cal = viewModel.getSelectedMonth().getValue();
                            if (cal == null) cal = Calendar.getInstance();
                            SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", new Locale("id", "ID"));
                            String monthStr = sdf.format(cal.getTime());
                            
                            long pendapatan = viewModel.getTotalUangMasuk().getValue() != null ? viewModel.getTotalUangMasuk().getValue() : 0L;
                            long biayaBahan = viewModel.getTotalBelanja().getValue() != null ? viewModel.getTotalBelanja().getValue() : 0L;
                            long biayaOps = viewModel.getTotalBiaya().getValue() != null ? viewModel.getTotalBiaya().getValue() : 0L;
                            long hpp = viewModel.getTotalHpp().getValue() != null ? viewModel.getTotalHpp().getValue() : 0L;
                            long untung = viewModel.getKeuntungan().getValue() != null ? viewModel.getKeuntungan().getValue() : 0L;
                            double margin = viewModel.getMargin().getValue() != null ? viewModel.getMargin().getValue() : 0.0;
                            
                            boolean success = com.example.dapurmoms.util.PdfGeneratorUtil.generateLaporanPdf(requireContext(), pfd, monthStr, pendapatan, biayaBahan, biayaOps, hpp, untung, margin);
                            if (success) {
                                com.google.android.material.snackbar.Snackbar.make(requireView(), "Laporan berhasil disimpan!", com.google.android.material.snackbar.Snackbar.LENGTH_LONG).show();
                            } else {
                                com.google.android.material.snackbar.Snackbar.make(requireView(), "Gagal menyimpan laporan", com.google.android.material.snackbar.Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        com.google.android.material.snackbar.Snackbar.make(requireView(), "Error: " + e.getMessage(), com.google.android.material.snackbar.Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private void printReport() {
        Calendar cal = viewModel.getSelectedMonth().getValue();
        if (cal == null) cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM_yyyy", new Locale("id", "ID"));
        String monthStr = sdf.format(cal.getTime());
        
        android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(android.content.Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        
        String fileName = "Laporan_DapurMoms_" + monthStr + ".pdf";
        intent.putExtra(android.content.Intent.EXTRA_TITLE, fileName);
        
        createLaporanPdfLauncher.launch(intent);
    }
}
