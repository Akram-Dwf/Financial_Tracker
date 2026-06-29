package com.example.dapurmoms.ui.belanja;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.dapurmoms.R;
import com.example.dapurmoms.data.database.entity.BelanjaBahan;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TambahBelanjaDialogFragment extends BottomSheetDialogFragment {

    private EditText etTanggal, etNamaBahan, etToko, etVolume, etJumlah, etHargaBeli, etCatatan;
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
        return inflater.inflate(R.layout.dialog_tambah_belanja, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etTanggal = view.findViewById(R.id.et_tanggal);
        etNamaBahan = view.findViewById(R.id.et_nama_bahan);
        etToko = view.findViewById(R.id.et_toko);
        etVolume = view.findViewById(R.id.et_volume);
        etJumlah = view.findViewById(R.id.et_jumlah);
        etHargaBeli = view.findViewById(R.id.et_harga_beli);
        etCatatan = view.findViewById(R.id.et_catatan);
        MaterialButton btnSimpan = view.findViewById(R.id.btn_simpan);

        etTanggal.setFocusable(false);
        etTanggal.setOnClickListener(v -> showDatePicker());

        btnSimpan.setOnClickListener(v -> saveBelanja());
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

    private void saveBelanja() {
        if (selectedDateMillis == 0) {
            etTanggal.setError("Tanggal harus diisi");
            return;
        }

        String namaBahan = etNamaBahan.getText().toString().trim();
        if (namaBahan.isEmpty()) {
            etNamaBahan.setError("Nama bahan harus diisi");
            etNamaBahan.requestFocus();
            return;
        }

        String toko = etToko.getText().toString().trim();
        if (toko.isEmpty()) {
            etToko.setError("Nama toko harus diisi");
            etToko.requestFocus();
            return;
        }

        String volume = etVolume.getText().toString().trim();
        if (volume.isEmpty()) {
            etVolume.setError("Volume/satuan harus diisi");
            etVolume.requestFocus();
            return;
        }

        String jumlahStr = etJumlah.getText().toString().trim();
        if (jumlahStr.isEmpty()) {
            etJumlah.setError("Jumlah unit harus diisi");
            etJumlah.requestFocus();
            return;
        }

        String hargaStr = etHargaBeli.getText().toString().trim();
        if (hargaStr.isEmpty()) {
            etHargaBeli.setError("Harga beli harus diisi");
            etHargaBeli.requestFocus();
            return;
        }

        double jumlahUnit;
        long hargaBeli;
        try {
            jumlahUnit = Double.parseDouble(jumlahStr);
            hargaBeli = Long.parseLong(hargaStr);
        } catch (NumberFormatException e) {
            etJumlah.setError("Angka tidak valid");
            return;
        }

        String catatan = etCatatan.getText().toString().trim();
        long totalHarga = (long) (jumlahUnit * hargaBeli);

        BelanjaBahan belanja = new BelanjaBahan();
        belanja.setTanggal(selectedDateMillis);
        belanja.setNamaBahan(namaBahan);
        belanja.setToko(toko);
        belanja.setVolume(volume);
        belanja.setJumlahUnit(jumlahUnit);
        belanja.setHargaBeli(hargaBeli);
        belanja.setTotalHarga(totalHarga);
        belanja.setCatatan(catatan);

        BelanjaViewModel viewModel = new ViewModelProvider(requireActivity()).get(BelanjaViewModel.class);
        viewModel.insertBelanja(belanja);

        if (getView() != null) {
            Snackbar.make(getView(), "Belanja berhasil disimpan", Snackbar.LENGTH_SHORT).show();
        }

        dismiss();
    }
}
