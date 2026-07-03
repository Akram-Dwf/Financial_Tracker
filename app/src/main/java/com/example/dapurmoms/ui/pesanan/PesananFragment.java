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
import java.util.TimeZone;

import com.google.android.material.datepicker.MaterialDatePicker;

public class PesananFragment extends Fragment {

    private PesananViewModel viewModel;
    private PesananAdapter adapter;
    private RecyclerView rvPesanan;
    private View layoutEmpty;
    private TextView tvTotalPesananBulan;
    private Chip chipBulanPesanan;
    private Chip chipTanggalPesanan;

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
        chipTanggalPesanan = view.findViewById(R.id.chip_tanggal_pesanan);
        ExtendedFloatingActionButton fabTambah = view.findViewById(R.id.fab_tambah_pesanan);

        viewModel = new ViewModelProvider(requireActivity()).get(PesananViewModel.class);

        adapter = new PesananAdapter(this::showDeleteConfirmation, this::showEditDialog, this::printReceipt);
        rvPesanan.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvPesanan.setAdapter(adapter);

        // Filter bulan
        chipBulanPesanan.setOnClickListener(v -> showMonthPicker());
        viewModel.getSelectedMonth().observe(getViewLifecycleOwner(), cal -> {
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", new Locale("id", "ID"));
            chipBulanPesanan.setText(sdf.format(cal.getTime()));
        });
        
        // Filter tanggal
        chipTanggalPesanan.setOnClickListener(v -> showDatePicker());
        chipTanggalPesanan.setOnCloseIconClickListener(v -> viewModel.clearDate());
        
        viewModel.getSelectedDate().observe(getViewLifecycleOwner(), cal -> {
            if (cal != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", new Locale("id", "ID"));
                chipTanggalPesanan.setText(sdf.format(cal.getTime()));
                chipTanggalPesanan.setCloseIconVisible(true);
                chipBulanPesanan.setAlpha(0.5f);
            } else {
                chipTanggalPesanan.setText("Pilih Tanggal");
                chipTanggalPesanan.setCloseIconVisible(false);
                chipBulanPesanan.setAlpha(1.0f);
            }
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

    private void showDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Pilih Tanggal Pesanan")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();
                
        datePicker.addOnPositiveButtonClickListener(selection -> {
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            cal.setTimeInMillis(selection);
            viewModel.setDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        });
        
        datePicker.show(getParentFragmentManager(), "DATE_PICKER_PESANAN");
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
    
    private Pesanan pesananToPrint;

    private final androidx.activity.result.ActivityResultLauncher<android.content.Intent> createReceiptPdfLauncher = registerForActivityResult(
            new androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                    android.net.Uri uri = result.getData().getData();
                    try {
                        android.os.ParcelFileDescriptor pfd = requireContext().getContentResolver().openFileDescriptor(uri, "w");
                        if (pfd != null) {
                            boolean success = com.example.dapurmoms.util.PdfGeneratorUtil.generateReceiptPdf(requireContext(), pfd, pesananToPrint);
                            if (success) {
                                com.google.android.material.snackbar.Snackbar snackbar = com.google.android.material.snackbar.Snackbar.make(requireView(), "Struk berhasil disimpan!", com.google.android.material.snackbar.Snackbar.LENGTH_LONG);
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
                                com.google.android.material.snackbar.Snackbar snackbar = com.google.android.material.snackbar.Snackbar.make(requireView(), "Gagal menyimpan struk", com.google.android.material.snackbar.Snackbar.LENGTH_SHORT);
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

    private void printReceipt(Pesanan pesanan) {
        pesananToPrint = pesanan;
        android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(android.content.Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        
        String namaPemesan = pesanan.getNamaPemesan().equals("-") ? "Pelanggan" : pesanan.getNamaPemesan();
        String fileName = "Struk_DapurMoms_" + namaPemesan.replaceAll("\\s+", "_") + ".pdf";
        intent.putExtra(android.content.Intent.EXTRA_TITLE, fileName);
        
        createReceiptPdfLauncher.launch(intent);
    }
}
