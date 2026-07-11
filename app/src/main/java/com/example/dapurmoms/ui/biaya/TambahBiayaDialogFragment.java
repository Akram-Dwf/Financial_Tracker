package com.example.dapurmoms.ui.biaya;

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
import com.example.dapurmoms.data.database.entity.BiayaLain;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TambahBiayaDialogFragment extends BottomSheetDialogFragment {

    private EditText etTanggal, etKeterangan, etJumlah, etCatatan;
    private AutoCompleteTextView etKategori;
    private long selectedDateMillis = 0;
    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("dd MMM yyyy", new Locale("id", "ID"));

    private boolean isEditMode = false;
    private int editId = 0;
    private BiayaViewModel viewModel;
    private MaterialButton btnSimpan;
    private ChipGroup chipGroupMetode;

    private static final String[] KATEGORI_OPTIONS = {
            "Bahan Bakar",
            "Kemasan",
            "Pengiriman",
            "Listrik & Air",
            "Perlengkapan",
            "Lain-lain"
    };

    @Override
    public int getTheme() {
        return com.google.android.material.R.style.ThemeOverlay_Material3_BottomSheetDialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_tambah_biaya, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etTanggal = view.findViewById(R.id.et_tanggal);
        etKeterangan = view.findViewById(R.id.et_keterangan);
        etKategori = view.findViewById(R.id.et_kategori);
        etJumlah = view.findViewById(R.id.et_jumlah);
        etCatatan = view.findViewById(R.id.et_catatan);
        btnSimpan = view.findViewById(R.id.btn_simpan);
        chipGroupMetode = view.findViewById(R.id.chip_group_metode_biaya);

        viewModel = new ViewModelProvider(requireActivity()).get(BiayaViewModel.class);

        ArrayAdapter<String> kategoriAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                KATEGORI_OPTIONS
        );
        etKategori.setAdapter(kategoriAdapter);

        etTanggal.setFocusable(false);
        etTanggal.setOnClickListener(v -> showDatePicker());

        btnSimpan.setOnClickListener(v -> saveBiaya());
        
        checkEditMode();
    }
    
    private void checkEditMode() {
        BiayaLain biayaToEdit = viewModel.getBiayaToEdit().getValue();
        if (biayaToEdit != null) {
            isEditMode = true;
            editId = biayaToEdit.getId();
            selectedDateMillis = biayaToEdit.getTanggal();
            
            etTanggal.setText(dateFormat.format(new Date(selectedDateMillis)));
            etKeterangan.setText(biayaToEdit.getKeterangan());
            etKategori.setText(biayaToEdit.getKategori(), false);
            etJumlah.setText(String.valueOf(biayaToEdit.getJumlah()));
            etCatatan.setText(biayaToEdit.getCatatan());

            String metode = biayaToEdit.getMetodePembayaran();
            if ("Transfer".equals(metode)) {
                chipGroupMetode.check(R.id.chip_transfer_biaya);
            } else if ("Utang".equals(metode)) {
                chipGroupMetode.check(R.id.chip_utang_biaya);
            } else {
                chipGroupMetode.check(R.id.chip_cash_biaya);
            }

            btnSimpan.setText("Ubah Biaya");
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
        if (checkedId == R.id.chip_transfer_biaya) return "Transfer";
        if (checkedId == R.id.chip_utang_biaya) return "Utang";
        return "Cash";
    }

    private void saveBiaya() {
        if (selectedDateMillis == 0) {
            etTanggal.setError("Tanggal harus diisi");
            return;
        }

        String keterangan = etKeterangan.getText().toString().trim();
        if (keterangan.isEmpty()) {
            etKeterangan.setError("Keterangan harus diisi");
            etKeterangan.requestFocus();
            return;
        }

        String kategori = etKategori.getText().toString().trim();
        if (kategori.isEmpty()) {
            etKategori.setError("Kategori harus dipilih");
            etKategori.requestFocus();
            return;
        }

        String jumlahStr = etJumlah.getText().toString().trim();
        if (jumlahStr.isEmpty()) {
            etJumlah.setError("Jumlah harus diisi");
            etJumlah.requestFocus();
            return;
        }

        long jumlah;
        try {
            jumlah = Long.parseLong(jumlahStr);
        } catch (NumberFormatException e) {
            etJumlah.setError("Angka tidak valid");
            return;
        }

        String catatan = etCatatan.getText().toString().trim();

        BiayaLain biaya = new BiayaLain();
        if (isEditMode) {
            biaya.setId(editId);
        }
        biaya.setTanggal(selectedDateMillis);
        biaya.setKeterangan(keterangan);
        biaya.setKategori(kategori);
        biaya.setJumlah(jumlah);
        biaya.setCatatan(catatan);
        biaya.setMetodePembayaran(getSelectedMetode());

        if (isEditMode) {
            viewModel.updateBiaya(biaya);
            if (getView() != null) {
                Snackbar.make(getView(), "Biaya berhasil diubah", Snackbar.LENGTH_SHORT).show();
            }
            viewModel.clearBiayaToEdit();
        } else {
            viewModel.insertBiaya(biaya);
            if (getView() != null) {
                Snackbar.make(getView(), "Biaya berhasil disimpan", Snackbar.LENGTH_SHORT).show();
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
            viewModel.clearBiayaToEdit();
        }
    }
}
