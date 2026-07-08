package com.example.dapurmoms.ui.pesanan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.dapurmoms.R;
import com.example.dapurmoms.data.database.entity.Pesanan;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TambahPesananDialogFragment extends BottomSheetDialogFragment {

    private EditText etTanggal, etNamaPemesan, etNamaMenu, etJumlah, etHargaSatuan, etCatatan;
    
    private long selectedDateMillis = 0;
    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("dd MMM yyyy", new Locale("id", "ID"));
            
    private boolean isEditMode = false;
    private int editId = 0;
    private PesananViewModel viewModel;
    private MaterialButton btnSimpan;
    private ChipGroup chipGroupMetode;

    @Override
    public int getTheme() {
        return com.google.android.material.R.style.ThemeOverlay_Material3_BottomSheetDialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_tambah_pesanan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etTanggal = view.findViewById(R.id.et_tanggal);
        etNamaPemesan = view.findViewById(R.id.et_nama_pemesan);
        etNamaMenu = view.findViewById(R.id.et_nama_menu);
        etJumlah = view.findViewById(R.id.et_jumlah);
        etHargaSatuan = view.findViewById(R.id.et_harga_satuan);
        etCatatan = view.findViewById(R.id.et_catatan);
        btnSimpan = view.findViewById(R.id.btn_simpan);
        chipGroupMetode = view.findViewById(R.id.chip_group_metode);

        viewModel = new ViewModelProvider(requireActivity()).get(PesananViewModel.class);

        etTanggal.setFocusable(false);
        etTanggal.setOnClickListener(v -> showDatePicker());

        btnSimpan.setOnClickListener(v -> savePesanan());
        
        checkEditMode();
    }
    
    private void checkEditMode() {
        Pesanan pesananToEdit = viewModel.getPesananToEdit().getValue();
        if (pesananToEdit != null) {
            isEditMode = true;
            editId = pesananToEdit.getId();
            selectedDateMillis = pesananToEdit.getTanggal();
            
            etTanggal.setText(dateFormat.format(new Date(selectedDateMillis)));
            etNamaPemesan.setText(pesananToEdit.getNamaPemesan());
            etNamaMenu.setText(pesananToEdit.getNamaMenu());
            etJumlah.setText(String.valueOf(pesananToEdit.getJumlah()));
            etHargaSatuan.setText(String.valueOf(pesananToEdit.getHargaSatuan()));
            etCatatan.setText(pesananToEdit.getCatatan());
            
            String metode = pesananToEdit.getMetodePembayaran();
            if ("Transfer".equals(metode)) {
                chipGroupMetode.check(R.id.chip_transfer);
            } else if ("Piutang".equals(metode)) {
                chipGroupMetode.check(R.id.chip_piutang);
            } else {
                chipGroupMetode.check(R.id.chip_cash);
            }
            
            btnSimpan.setText("Ubah Pesanan");
        }
    }

    private void showDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Pilih Tanggal")
                .setSelection(selectedDateMillis > 0 ? selectedDateMillis : MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            selectedDateMillis = selection;
            etTanggal.setText(dateFormat.format(new Date(selection)));
        });

        datePicker.show(getChildFragmentManager(), "DATE_PICKER");
    }

    private String getSelectedMetode() {
        int checkedId = chipGroupMetode.getCheckedChipId();
        if (checkedId == R.id.chip_transfer) return "Transfer";
        if (checkedId == R.id.chip_piutang) return "Piutang";
        return "Cash";
    }

    private void savePesanan() {
        if (selectedDateMillis == 0) {
            etTanggal.setError("Tanggal harus diisi");
            return;
        }

        String namaPemesan = etNamaPemesan.getText().toString().trim();
        if (namaPemesan.isEmpty()) {
            namaPemesan = "-";
        }

        String namaMenu = etNamaMenu.getText().toString().trim();
        if (namaMenu.isEmpty()) {
            etNamaMenu.setError("Nama menu harus diisi");
            etNamaMenu.requestFocus();
            return;
        }

        String jumlahStr = etJumlah.getText().toString().trim();
        int jumlah = 1;
        if (!jumlahStr.isEmpty()) {
            try {
                jumlah = Integer.parseInt(jumlahStr);
            } catch (NumberFormatException e) {
                etJumlah.setError("Angka tidak valid");
                return;
            }
        }

        String hargaStr = etHargaSatuan.getText().toString().trim();
        if (hargaStr.isEmpty()) {
            etHargaSatuan.setError("Harga satuan/Total harus diisi");
            etHargaSatuan.requestFocus();
            return;
        }

        long hargaSatuan;
        try {
            hargaSatuan = Long.parseLong(hargaStr);
        } catch (NumberFormatException e) {
            etHargaSatuan.setError("Angka tidak valid");
            return;
        }

        String catatan = etCatatan.getText().toString().trim();
        long total = (long) jumlah * hargaSatuan;

        Pesanan pesanan = new Pesanan();
        if (isEditMode) {
            pesanan.setId(editId);
        }
        pesanan.setTanggal(selectedDateMillis);
        pesanan.setNamaPemesan(namaPemesan);
        pesanan.setNamaMenu(namaMenu);
        pesanan.setJumlah(jumlah);
        pesanan.setHargaSatuan(hargaSatuan);
        pesanan.setTotal(total);
        pesanan.setCatatan(catatan);
        pesanan.setMetodePembayaran(getSelectedMetode());

        if (isEditMode) {
            viewModel.updatePesanan(pesanan);
            if (getView() != null) {
                Snackbar.make(getView(), "Pesanan berhasil diubah", Snackbar.LENGTH_SHORT).show();
            }
            viewModel.clearPesananToEdit();
        } else {
            viewModel.insertPesanan(pesanan);
            if (getView() != null) {
                Snackbar.make(getView(), "Pesanan berhasil disimpan", Snackbar.LENGTH_SHORT).show();
            }
        }

        dismiss();
    }
}
