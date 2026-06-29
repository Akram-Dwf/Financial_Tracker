package com.example.dapurmoms.ui.pesanan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.dapurmoms.R;
import com.example.dapurmoms.data.database.entity.Pesanan;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
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
        MaterialButton btnSimpan = view.findViewById(R.id.btn_simpan);

        etTanggal.setFocusable(false);
        etTanggal.setOnClickListener(v -> showDatePicker());

        btnSimpan.setOnClickListener(v -> savePesanan());
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

    private void savePesanan() {
        if (selectedDateMillis == 0) {
            etTanggal.setError("Tanggal harus diisi");
            return;
        }

        String namaPemesan = etNamaPemesan.getText().toString().trim();
        if (namaPemesan.isEmpty()) {
            etNamaPemesan.setError("Nama pemesan harus diisi");
            etNamaPemesan.requestFocus();
            return;
        }

        String namaMenu = etNamaMenu.getText().toString().trim();
        if (namaMenu.isEmpty()) {
            etNamaMenu.setError("Nama menu harus diisi");
            etNamaMenu.requestFocus();
            return;
        }

        String jumlahStr = etJumlah.getText().toString().trim();
        if (jumlahStr.isEmpty()) {
            etJumlah.setError("Jumlah harus diisi");
            etJumlah.requestFocus();
            return;
        }

        String hargaStr = etHargaSatuan.getText().toString().trim();
        if (hargaStr.isEmpty()) {
            etHargaSatuan.setError("Harga satuan harus diisi");
            etHargaSatuan.requestFocus();
            return;
        }

        int jumlah;
        long hargaSatuan;
        try {
            jumlah = Integer.parseInt(jumlahStr);
            hargaSatuan = Long.parseLong(hargaStr);
        } catch (NumberFormatException e) {
            etJumlah.setError("Angka tidak valid");
            return;
        }

        String catatan = etCatatan.getText().toString().trim();
        long total = (long) jumlah * hargaSatuan;

        Pesanan pesanan = new Pesanan();
        pesanan.setTanggal(selectedDateMillis);
        pesanan.setNamaPemesan(namaPemesan);
        pesanan.setNamaMenu(namaMenu);
        pesanan.setJumlah(jumlah);
        pesanan.setHargaSatuan(hargaSatuan);
        pesanan.setTotal(total);
        pesanan.setCatatan(catatan);

        PesananViewModel viewModel = new ViewModelProvider(requireActivity()).get(PesananViewModel.class);
        viewModel.insertPesanan(pesanan);

        if (getView() != null) {
            Snackbar.make(getView(), "Pesanan berhasil disimpan", Snackbar.LENGTH_SHORT).show();
        }

        dismiss();
    }
}
