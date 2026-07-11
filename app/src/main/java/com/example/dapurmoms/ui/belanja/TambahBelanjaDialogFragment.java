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
import com.google.android.material.chip.ChipGroup;
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

    private boolean isEditMode = false;
    private int editId = 0;
    private BelanjaViewModel viewModel;
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
        btnSimpan = view.findViewById(R.id.btn_simpan);
        chipGroupMetode = view.findViewById(R.id.chip_group_metode_belanja);

        viewModel = new ViewModelProvider(requireActivity()).get(BelanjaViewModel.class);

        etTanggal.setFocusable(false);
        etTanggal.setOnClickListener(v -> showDatePicker());

        btnSimpan.setOnClickListener(v -> saveBelanja());

        checkEditMode();
    }

    private void checkEditMode() {
        BelanjaBahan belanjaToEdit = viewModel.getBelanjaToEdit().getValue();
        if (belanjaToEdit != null) {
            isEditMode = true;
            editId = belanjaToEdit.getId();
            selectedDateMillis = belanjaToEdit.getTanggal();

            etTanggal.setText(dateFormat.format(new Date(selectedDateMillis)));
            etNamaBahan.setText(belanjaToEdit.getNamaBahan());
            etToko.setText(belanjaToEdit.getToko());
            etVolume.setText(belanjaToEdit.getVolume());
            // Format double without trailing zero if not needed
            String jumlahStr = belanjaToEdit.getJumlahUnit() == (long) belanjaToEdit.getJumlahUnit() 
                ? String.format(Locale.US, "%d", (long) belanjaToEdit.getJumlahUnit())
                : String.format(Locale.US, "%s", belanjaToEdit.getJumlahUnit());
            etJumlah.setText(jumlahStr);
            etHargaBeli.setText(String.valueOf(belanjaToEdit.getHargaBeli()));
            etCatatan.setText(belanjaToEdit.getCatatan());

            String metode = belanjaToEdit.getMetodePembayaran();
            if ("Transfer".equals(metode)) {
                chipGroupMetode.check(R.id.chip_transfer_belanja);
            } else if ("Utang".equals(metode)) {
                chipGroupMetode.check(R.id.chip_utang_belanja);
            } else {
                chipGroupMetode.check(R.id.chip_cash_belanja);
            }

            btnSimpan.setText("Ubah Belanja");
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
        if (checkedId == R.id.chip_transfer_belanja) return "Transfer";
        if (checkedId == R.id.chip_utang_belanja) return "Utang";
        return "Cash";
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
            toko = "-";
        }

        String volume = etVolume.getText().toString().trim();
        if (volume.isEmpty()) {
            volume = "-";
        }

        String jumlahStr = etJumlah.getText().toString().trim();
        double jumlahUnit = 1.0;
        if (!jumlahStr.isEmpty()) {
            try {
                jumlahUnit = Double.parseDouble(jumlahStr);
            } catch (NumberFormatException e) {
                etJumlah.setError("Angka tidak valid");
                return;
            }
        }

        String hargaStr = etHargaBeli.getText().toString().trim();
        if (hargaStr.isEmpty()) {
            etHargaBeli.setError("Harga beli/Total harus diisi");
            etHargaBeli.requestFocus();
            return;
        }

        long hargaBeli;
        try {
            hargaBeli = Long.parseLong(hargaStr);
        } catch (NumberFormatException e) {
            etHargaBeli.setError("Angka tidak valid");
            return;
        }

        String catatan = etCatatan.getText().toString().trim();
        long totalHarga = (long) (jumlahUnit * hargaBeli);

        BelanjaBahan belanja = new BelanjaBahan();
        if (isEditMode) {
            belanja.setId(editId);
        }
        belanja.setTanggal(selectedDateMillis);
        belanja.setNamaBahan(namaBahan);
        belanja.setToko(toko);
        belanja.setVolume(volume);
        belanja.setJumlahUnit(jumlahUnit);
        belanja.setHargaBeli(hargaBeli);
        belanja.setTotalHarga(totalHarga);
        belanja.setCatatan(catatan);
        belanja.setMetodePembayaran(getSelectedMetode());

        if (isEditMode) {
            viewModel.updateBelanja(belanja);
            if (getView() != null) {
                Snackbar.make(getView(), "Belanja berhasil diubah", Snackbar.LENGTH_SHORT).show();
            }
            viewModel.clearBelanjaToEdit();
        } else {
            viewModel.insertBelanja(belanja);
            if (getView() != null) {
                Snackbar.make(getView(), "Belanja berhasil disimpan", Snackbar.LENGTH_SHORT).show();
            }
        }

        dismiss();
    }

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        if (view != null) {
            View parent = (View) view.getParent();
            com.google.android.material.bottomsheet.BottomSheetBehavior<View> behavior =
                    com.google.android.material.bottomsheet.BottomSheetBehavior.from(parent);
            behavior.setState(com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED);
            behavior.setSkipCollapsed(true);

            android.view.ViewGroup.LayoutParams layoutParams = parent.getLayoutParams();
            layoutParams.height = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
            parent.setLayoutParams(layoutParams);
        }

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
    }

    @Override
    public void onDismiss(@NonNull android.content.DialogInterface dialog) {
        super.onDismiss(dialog);
        if (viewModel != null) {
            viewModel.clearBelanjaToEdit();
        }
    }
}
