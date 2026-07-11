package com.example.dapurmoms.ui.pesanan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.dapurmoms.R;
import com.example.dapurmoms.data.database.entity.Menu;
import com.example.dapurmoms.data.database.entity.Pesanan;
import com.example.dapurmoms.data.database.entity.PesananItem;
import com.example.dapurmoms.util.CurrencyFormatter;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TambahPesananDialogFragment extends BottomSheetDialogFragment {

    private EditText etTanggal, etNamaPemesan, etCatatan;
    private LinearLayout layoutItemsContainer;
    private TextView tvTotalPembayaran;
    private MaterialButton btnTambahItem;
    private MaterialButton btnSimpan;
    private ChipGroup chipGroupMetode;

    private long selectedDateMillis = 0;
    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("dd MMM yyyy", new Locale("id", "ID"));

    private boolean isEditMode = false;
    private int editId = 0;
    private PesananViewModel viewModel;
    
    private final List<PesananItem> addedItems = new ArrayList<>();
    private final List<Menu> availableMenus = new ArrayList<>();

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
        layoutItemsContainer = view.findViewById(R.id.layout_items_container);
        tvTotalPembayaran = view.findViewById(R.id.tv_total_pembayaran);
        btnTambahItem = view.findViewById(R.id.btn_tambah_item);
        etCatatan = view.findViewById(R.id.et_catatan);
        btnSimpan = view.findViewById(R.id.btn_simpan);
        chipGroupMetode = view.findViewById(R.id.chip_group_metode);

        viewModel = new ViewModelProvider(requireActivity()).get(PesananViewModel.class);

        etTanggal.setFocusable(false);
        etTanggal.setOnClickListener(v -> showDatePicker());

        btnTambahItem.setOnClickListener(v -> showAddItemDialog());
        btnSimpan.setOnClickListener(v -> savePesanan());

        // Observe available menus for autocomplete suggestions
        viewModel.getAllMenu().observe(getViewLifecycleOwner(), menus -> {
            if (menus != null) {
                availableMenus.clear();
                availableMenus.addAll(menus);
            }
        });

        checkEditMode();
    }

    private void renderItems() {
        layoutItemsContainer.removeAllViews();
        long totalPayment = 0;

        for (int i = 0; i < addedItems.size(); i++) {
            final int index = i;
            PesananItem item = addedItems.get(i);
            totalPayment += item.getTotal();

            View itemView = LayoutInflater.from(requireContext()).inflate(R.layout.item_added_pesanan, layoutItemsContainer, false);
            TextView tvItemName = itemView.findViewById(R.id.tv_item_name);
            TextView tvItemDetails = itemView.findViewById(R.id.tv_item_details);
            TextView tvItemTotal = itemView.findViewById(R.id.tv_item_total);
            View btnEditItem = itemView.findViewById(R.id.btn_edit_item);
            View btnDelete = itemView.findViewById(R.id.btn_delete_item);

            tvItemName.setText(item.getNamaMenu());
            tvItemDetails.setText(item.getJumlah() + " x " + CurrencyFormatter.formatRupiah(item.getHargaSatuan()));
            tvItemTotal.setText(CurrencyFormatter.formatRupiah(item.getTotal()));

            btnEditItem.setOnClickListener(v -> showAddItemDialog(index));

            btnDelete.setOnClickListener(v -> {
                addedItems.remove(index);
                renderItems();
            });

            layoutItemsContainer.addView(itemView);
        }

        tvTotalPembayaran.setText(CurrencyFormatter.formatRupiah(totalPayment));
    }

    private void showAddItemDialog() {
        showAddItemDialog(-1);
    }

    private void showAddItemDialog(final int editIndex) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_menu_item, null);
        TextView tvDialogTitle = dialogView.findViewById(R.id.tv_dialog_title); // Wait, tv_dialog_title exists in dialog_add_menu_item.xml? Let's check or handle it safely.
        AutoCompleteTextView actvMenuName = dialogView.findViewById(R.id.actv_menu_name);
        EditText etQty = dialogView.findViewById(R.id.et_item_qty);
        EditText etPrice = dialogView.findViewById(R.id.et_item_price);

        // Pre-fill if editing
        if (editIndex >= 0) {
            if (tvDialogTitle != null) {
                tvDialogTitle.setText("Ubah Menu di Pesanan");
            }
            PesananItem itemToEdit = addedItems.get(editIndex);
            actvMenuName.setText(itemToEdit.getNamaMenu());
            etQty.setText(String.valueOf(itemToEdit.getJumlah()));
            etPrice.setText(String.valueOf(itemToEdit.getHargaSatuan()));
        }

        // Prepare autocomplete suggestions
        List<String> suggestions = new ArrayList<>();
        for (Menu m : availableMenus) {
            suggestions.add(m.getNamaMenu());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, suggestions);
        actvMenuName.setAdapter(adapter);

        // Autocomplete selection listener to autofill price
        actvMenuName.setOnItemClickListener((parent, view, position, id) -> {
            String selectedName = (String) parent.getItemAtPosition(position);
            for (Menu m : availableMenus) {
                if (m.getNamaMenu().equals(selectedName)) {
                    etPrice.setText(String.valueOf(m.getHargaJual()));
                    break;
                }
            }
        });

        String positiveButtonText = editIndex >= 0 ? "Ubah" : "Tambah";
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setPositiveButton(positiveButtonText, (dialog, which) -> {
                    String name = actvMenuName.getText().toString().trim();
                    String qtyStr = etQty.getText().toString().trim();
                    String priceStr = etPrice.getText().toString().trim();

                    if (name.isEmpty()) {
                        Snackbar.make(requireView(), "Nama menu tidak boleh kosong", Snackbar.LENGTH_SHORT).show();
                        return;
                    }

                    int qty = 1;
                    if (!qtyStr.isEmpty()) {
                        try {
                            qty = Integer.parseInt(qtyStr);
                        } catch (NumberFormatException e) {
                            qty = 1;
                        }
                    }

                    long price = 0;
                    if (!priceStr.isEmpty()) {
                        try {
                            price = Long.parseLong(priceStr);
                        } catch (NumberFormatException e) {
                            price = 0;
                        }
                    }

                    PesananItem newItem = new PesananItem(name, qty, price);
                    if (editIndex >= 0) {
                        addedItems.set(editIndex, newItem);
                    } else {
                        addedItems.add(newItem);
                    }
                    renderItems();
                })
                .setNegativeButton("Batal", null);

        builder.show();
    }

    private void checkEditMode() {
        Pesanan pesananToEdit = viewModel.getPesananToEdit().getValue();
        if (pesananToEdit != null) {
            isEditMode = true;
            editId = pesananToEdit.getId();
            selectedDateMillis = pesananToEdit.getTanggal();

            etTanggal.setText(dateFormat.format(new Date(selectedDateMillis)));
            etNamaPemesan.setText(pesananToEdit.getNamaPemesan());
            etCatatan.setText(pesananToEdit.getCatatan());

            addedItems.clear();
            if (pesananToEdit.getNamaMenu() != null) {
                addedItems.addAll(pesananToEdit.getNamaMenu());
            }
            renderItems();

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

        if (addedItems.isEmpty()) {
            Snackbar.make(requireView(), "Tambahkan minimal satu menu pesanan", Snackbar.LENGTH_SHORT).show();
            return;
        }

        long totalPayment = 0;
        int totalQty = 0;
        for (PesananItem item : addedItems) {
            totalPayment += item.getTotal();
            totalQty += item.getJumlah();
        }

        long avgPrice = totalQty > 0 ? (totalPayment / totalQty) : 0;
        String catatan = etCatatan.getText().toString().trim();

        Pesanan pesanan = new Pesanan();
        if (isEditMode) {
            pesanan.setId(editId);
        }
        pesanan.setTanggal(selectedDateMillis);
        pesanan.setNamaPemesan(namaPemesan);
        pesanan.setNamaMenu(addedItems);
        pesanan.setJumlah(totalQty);
        pesanan.setHargaSatuan(avgPrice);
        pesanan.setTotal(totalPayment);
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

            ViewGroup.LayoutParams layoutParams = parent.getLayoutParams();
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
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
            viewModel.clearPesananToEdit();
        }
    }
}
