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
    private TextView tvTotalUangMasuk;
    private TextView tvTotalBiayaBahan;
    private TextView tvTotalBiayaOperasional;
    private TextView tvHppBahan, tvHppOperasional, tvTotalHpp;
    private TextView tvTotalMasuk, tvTotalHppFinal, tvKeuntunganBersih;
    private TextView tvPendapatanCash, tvPendapatanTransfer, tvPendapatanPiutang;
    private TextView tvBiayaBahanCash, tvBiayaBahanTransfer, tvBiayaBahanUtang;
    private TextView tvBiayaOperasionalCash, tvBiayaOperasionalTransfer, tvBiayaOperasionalUtang;
    private TextView tvMargin, tvStatus;
    private CardView cardStatus;
    private View layoutStatusBg;
    private android.widget.ImageView ivStatusIcon;
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

        tvTotalUangMasuk = view.findViewById(R.id.tv_total_uang_masuk);
        tvTotalBiayaBahan = view.findViewById(R.id.tv_total_biaya_bahan);
        tvTotalBiayaOperasional = view.findViewById(R.id.tv_total_biaya_operasional);
        tvHppBahan = view.findViewById(R.id.tv_hpp_bahan);
        tvHppOperasional = view.findViewById(R.id.tv_hpp_operasional);
        tvTotalHpp = view.findViewById(R.id.tv_total_hpp);
        tvTotalMasuk = view.findViewById(R.id.tv_total_masuk);
        tvTotalHppFinal = view.findViewById(R.id.tv_total_hpp_final);
        tvKeuntunganBersih = view.findViewById(R.id.tv_keuntungan_bersih);
        
        tvPendapatanCash = view.findViewById(R.id.tv_pendapatan_cash);
        tvPendapatanTransfer = view.findViewById(R.id.tv_pendapatan_transfer);
        tvPendapatanPiutang = view.findViewById(R.id.tv_pendapatan_piutang);
        
        tvBiayaBahanCash = view.findViewById(R.id.tv_biaya_bahan_cash);
        tvBiayaBahanTransfer = view.findViewById(R.id.tv_biaya_bahan_transfer);
        tvBiayaBahanUtang = view.findViewById(R.id.tv_biaya_bahan_utang);
        
        tvBiayaOperasionalCash = view.findViewById(R.id.tv_biaya_operasional_cash);
        tvBiayaOperasionalTransfer = view.findViewById(R.id.tv_biaya_operasional_transfer);
        tvBiayaOperasionalUtang = view.findViewById(R.id.tv_biaya_operasional_utang);

        tvMargin = view.findViewById(R.id.tv_margin);
        cardStatus = view.findViewById(R.id.card_status);
        tvStatus = view.findViewById(R.id.tv_status);
        layoutStatusBg = view.findViewById(R.id.layout_status_bg);
        ivStatusIcon = view.findViewById(R.id.iv_status_icon);
        pieChart = view.findViewById(R.id.pie_chart_laporan);

        viewModel = new ViewModelProvider(requireActivity()).get(LaporanViewModel.class);

        setupPieChart();

        // Month chip click
        chipBulanLaporan.setOnClickListener(v -> showMonthPicker());
        
        // Month navigation arrows
        view.findViewById(R.id.btn_prev_month).setOnClickListener(v -> {
            Calendar current = viewModel.getSelectedMonth().getValue();
            if (current != null) {
                current.add(Calendar.MONTH, -1);
                viewModel.setMonth(current.get(Calendar.YEAR), current.get(Calendar.MONTH));
            }
        });

        view.findViewById(R.id.btn_next_month).setOnClickListener(v -> {
            Calendar current = viewModel.getSelectedMonth().getValue();
            if (current != null) {
                current.add(Calendar.MONTH, 1);
                viewModel.setMonth(current.get(Calendar.YEAR), current.get(Calendar.MONTH));
            }
        });
        
        // Print button click
        android.widget.ImageButton btnCetak = view.findViewById(R.id.btn_cetak_laporan);
        btnCetak.setOnClickListener(v -> printReport());

        com.google.android.material.button.MaterialButton btnCetakBesar = view.findViewById(R.id.btn_cetak_laporan_besar);
        btnCetakBesar.setOnClickListener(v -> printReport());

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
            tvTotalUangMasuk.setText(CurrencyFormatter.formatRupiah(total));
            tvTotalMasuk.setText(CurrencyFormatter.formatRupiah(total));
        });
        
        viewModel.getTotalPesananCash().observe(getViewLifecycleOwner(), value -> {
            tvPendapatanCash.setText(CurrencyFormatter.formatRupiah(value != null ? value : 0L));
        });
        viewModel.getTotalPesananTransfer().observe(getViewLifecycleOwner(), value -> {
            tvPendapatanTransfer.setText(CurrencyFormatter.formatRupiah(value != null ? value : 0L));
        });
        viewModel.getTotalPiutang().observe(getViewLifecycleOwner(), value -> {
            tvPendapatanPiutang.setText(CurrencyFormatter.formatRupiah(value != null ? value : 0L));
        });

        viewModel.getTotalBelanja().observe(getViewLifecycleOwner(), value -> {
            long total = value != null ? value : 0L;
            tvTotalBiayaBahan.setText(CurrencyFormatter.formatRupiah(total));
            tvHppBahan.setText(CurrencyFormatter.formatRupiah(total));
        });
        
        viewModel.getTotalBelanjaCash().observe(getViewLifecycleOwner(), value -> {
            tvBiayaBahanCash.setText(CurrencyFormatter.formatRupiah(value != null ? value : 0L));
        });
        viewModel.getTotalBelanjaTransfer().observe(getViewLifecycleOwner(), value -> {
            tvBiayaBahanTransfer.setText(CurrencyFormatter.formatRupiah(value != null ? value : 0L));
        });
        viewModel.getTotalUtangBelanja().observe(getViewLifecycleOwner(), value -> {
            tvBiayaBahanUtang.setText(CurrencyFormatter.formatRupiah(value != null ? value : 0L));
        });

        viewModel.getTotalBiaya().observe(getViewLifecycleOwner(), value -> {
            long total = value != null ? value : 0L;
            tvTotalBiayaOperasional.setText(CurrencyFormatter.formatRupiah(total));
            tvHppOperasional.setText(CurrencyFormatter.formatRupiah(total));
        });
        
        viewModel.getTotalBiayaCash().observe(getViewLifecycleOwner(), value -> {
            tvBiayaOperasionalCash.setText(CurrencyFormatter.formatRupiah(value != null ? value : 0L));
        });
        viewModel.getTotalBiayaTransfer().observe(getViewLifecycleOwner(), value -> {
            tvBiayaOperasionalTransfer.setText(CurrencyFormatter.formatRupiah(value != null ? value : 0L));
        });
        viewModel.getTotalUtangBiaya().observe(getViewLifecycleOwner(), value -> {
            tvBiayaOperasionalUtang.setText(CurrencyFormatter.formatRupiah(value != null ? value : 0L));
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
        
        // Observe lists so they are loaded from DB
        viewModel.getListPesanan().observe(getViewLifecycleOwner(), list -> {});
        viewModel.getListBelanja().observe(getViewLifecycleOwner(), list -> {});
        viewModel.getListBiaya().observe(getViewLifecycleOwner(), list -> {});
    }

    private void updateStatusCard(long keuntungan) {
        if (keuntungan > 0) {
            layoutStatusBg.setBackgroundResource(R.drawable.bg_gradient_status_untung);
            ivStatusIcon.setImageResource(R.drawable.ic_trending_up);
            ivStatusIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.color_profit));
            tvStatus.setText("Bulan ini UNTUNG\n" + CurrencyFormatter.formatRupiah(keuntungan));
            tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_profit));
        } else if (keuntungan < 0) {
            layoutStatusBg.setBackgroundResource(R.drawable.bg_gradient_status_rugi);
            ivStatusIcon.setImageResource(R.drawable.ic_trending_down);
            ivStatusIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.color_expense));
            tvStatus.setText("Bulan ini RUGI\n" + CurrencyFormatter.formatRupiah(Math.abs(keuntungan)));
            tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_expense));
        } else {
            layoutStatusBg.setBackground(null);
            layoutStatusBg.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.md_theme_surface_variant));
            ivStatusIcon.setImageResource(R.drawable.ic_trending_up); // just a placeholder
            ivStatusIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.md_theme_on_surface_variant));
            tvStatus.setText("Belum ada data transaksi bulan ini.");
            tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.md_theme_on_surface_variant));
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
        
        pieChart.setDrawEntryLabels(false); // Hide labels on slice, use legend instead
        
        com.github.mikephil.charting.components.Legend l = pieChart.getLegend();
        l.setEnabled(true);
        l.setTextColor(ContextCompat.getColor(requireContext(), R.color.md_theme_on_surface));
        l.setTextSize(14f);
        l.setFormSize(14f);
        l.setWordWrapEnabled(true);
        l.setHorizontalAlignment(com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment.CENTER);
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
        
        // Draw values outside the slices for better readability
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setValueLinePart1OffsetPercentage(80.f);
        dataSet.setValueLinePart1Length(0.4f);
        dataSet.setValueLinePart2Length(0.2f);
        dataSet.setValueLineColor(ContextCompat.getColor(requireContext(), R.color.md_theme_on_surface));

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(14f);
        data.setValueTextColor(ContextCompat.getColor(requireContext(), R.color.md_theme_on_surface));

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
                            
                            java.util.List<com.example.dapurmoms.data.database.entity.Pesanan> pesananList = viewModel.getListPesanan().getValue();
                            java.util.List<com.example.dapurmoms.data.database.entity.BelanjaBahan> belanjaList = viewModel.getListBelanja().getValue();
                            java.util.List<com.example.dapurmoms.data.database.entity.BiayaLain> biayaList = viewModel.getListBiaya().getValue();
                            
                            boolean success = com.example.dapurmoms.util.PdfGeneratorUtil.generateLaporanPdf(
                                    requireContext(), pfd, monthStr, pendapatan, biayaBahan, biayaOps, hpp, untung, margin,
                                    pesananList, belanjaList, biayaList);
                            if (success) {
                                com.google.android.material.snackbar.Snackbar snackbar = com.google.android.material.snackbar.Snackbar.make(requireView(), "Laporan berhasil disimpan!", com.google.android.material.snackbar.Snackbar.LENGTH_LONG);
                                snackbar.setAnchorView(requireActivity().findViewById(com.example.dapurmoms.R.id.bottom_navigation));
                                snackbar.setAction("BUKA", v -> {
                                    android.content.Intent viewIntent = new android.content.Intent(android.content.Intent.ACTION_VIEW);
                                    viewIntent.setDataAndType(uri, "application/pdf");
                                    viewIntent.addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    try {
                                        startActivity(android.content.Intent.createChooser(viewIntent, "Buka PDF dengan..."));
                                    } catch (Exception e) {
                                        android.widget.Toast.makeText(requireContext(), "Tidak ada aplikasi untuk membuka PDF", android.widget.Toast.LENGTH_SHORT).show();
                                    }
                                });
                                snackbar.show();
                            } else {
                                com.google.android.material.snackbar.Snackbar snackbar = com.google.android.material.snackbar.Snackbar.make(requireView(), "Gagal menyimpan laporan", com.google.android.material.snackbar.Snackbar.LENGTH_SHORT);
                                snackbar.setAnchorView(requireActivity().findViewById(com.example.dapurmoms.R.id.bottom_navigation));
                                snackbar.show();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        com.google.android.material.snackbar.Snackbar snackbar = com.google.android.material.snackbar.Snackbar.make(requireView(), "Error: " + e.getMessage(), com.google.android.material.snackbar.Snackbar.LENGTH_SHORT);
                        snackbar.setAnchorView(requireActivity().findViewById(com.example.dapurmoms.R.id.bottom_navigation));
                        snackbar.show();
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
