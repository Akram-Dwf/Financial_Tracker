package com.example.dapurmoms.ui.pesanan;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.dapurmoms.R;
import com.example.dapurmoms.data.database.entity.Pesanan;
import com.example.dapurmoms.data.database.entity.PesananItem;
import com.example.dapurmoms.util.CurrencyFormatter;
import com.example.dapurmoms.util.PdfGeneratorUtil;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetailPesananDialogFragment extends BottomSheetDialogFragment {

    private TextView tvTanggal, tvPelanggan, tvMetode, tvTotal, tvCatatanLabel, tvCatatan;
    private LinearLayout layoutItemsContainer;
    private MaterialButton btnPrint, btnEdit, btnDelete;

    private PesananViewModel viewModel;
    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID"));

    @Override
    public int getTheme() {
        return com.google.android.material.R.style.ThemeOverlay_Material3_BottomSheetDialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_detail_pesanan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvTanggal = view.findViewById(R.id.tv_detail_tanggal);
        tvPelanggan = view.findViewById(R.id.tv_detail_pelanggan);
        tvMetode = view.findViewById(R.id.tv_detail_metode);
        tvTotal = view.findViewById(R.id.tv_detail_total);
        tvCatatanLabel = view.findViewById(R.id.tv_detail_catatan_label);
        tvCatatan = view.findViewById(R.id.tv_detail_catatan);
        layoutItemsContainer = view.findViewById(R.id.layout_detail_items_container);

        btnPrint = view.findViewById(R.id.btn_detail_print);
        btnEdit = view.findViewById(R.id.btn_detail_edit);
        btnDelete = view.findViewById(R.id.btn_detail_delete);

        viewModel = new ViewModelProvider(requireActivity()).get(PesananViewModel.class);

        viewModel.getSelectedPesanan().observe(getViewLifecycleOwner(), this::bindPesanan);
    }

    private void bindPesanan(Pesanan pesanan) {
        if (pesanan == null) {
            dismiss();
            return;
        }

        tvTanggal.setText(dateFormat.format(new Date(pesanan.getTanggal())));
        tvPelanggan.setText(pesanan.getNamaPemesan().equals("-") ? "Pelanggan" : pesanan.getNamaPemesan());
        tvMetode.setText(pesanan.getMetodePembayaran());
        tvTotal.setText(CurrencyFormatter.formatRupiah(pesanan.getTotal()));

        if (pesanan.getCatatan() == null || pesanan.getCatatan().trim().isEmpty()) {
            tvCatatanLabel.setVisibility(View.GONE);
            tvCatatan.setVisibility(View.GONE);
        } else {
            tvCatatanLabel.setVisibility(View.VISIBLE);
            tvCatatan.setVisibility(View.VISIBLE);
            tvCatatan.setText(pesanan.getCatatan());
        }

        // Render ordered menu items
        layoutItemsContainer.removeAllViews();
        if (pesanan.getNamaMenu() != null) {
            for (PesananItem item : pesanan.getNamaMenu()) {
                View itemView = LayoutInflater.from(requireContext()).inflate(R.layout.item_added_pesanan, layoutItemsContainer, false);
                TextView tvItemName = itemView.findViewById(R.id.tv_item_name);
                TextView tvItemDetails = itemView.findViewById(R.id.tv_item_details);
                TextView tvItemTotal = itemView.findViewById(R.id.tv_item_total);
                View btnDelete = itemView.findViewById(R.id.btn_delete_item);
                View btnEditItem = itemView.findViewById(R.id.btn_edit_item);

                tvItemName.setText(item.getNamaMenu());
                tvItemDetails.setText(item.getJumlah() + " x " + CurrencyFormatter.formatRupiah(item.getHargaSatuan()));
                tvItemTotal.setText(CurrencyFormatter.formatRupiah(item.getTotal()));
                btnDelete.setVisibility(View.GONE); // Hide delete button in details view
                if (btnEditItem != null) {
                    btnEditItem.setVisibility(View.GONE); // Hide edit button in details view
                }

                layoutItemsContainer.addView(itemView);
            }
        }

        // Cetak Button
        btnPrint.setOnClickListener(v -> doPrint(pesanan));

        // Edit Button
        btnEdit.setOnClickListener(v -> {
            viewModel.setPesananToEdit(pesanan);
            TambahPesananDialogFragment dialog = new TambahPesananDialogFragment();
            dialog.show(getParentFragmentManager(), "EditPesananDialog");
            dismiss();
        });

        // Delete Button
        btnDelete.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Hapus Pesanan")
                    .setMessage("Apakah Anda yakin ingin menghapus pesanan dari " + (pesanan.getNamaPemesan().equals("-") ? "Pelanggan" : pesanan.getNamaPemesan()) + "?")
                    .setPositiveButton("Hapus", (dialog, which) -> {
                        viewModel.deletePesanan(pesanan);
                        // Show undo Snackbar on the main activity view
                        Snackbar snackbar = Snackbar.make(
                                requireActivity().findViewById(android.R.id.content),
                                "Pesanan dipindahkan ke Tempat Sampah",
                                Snackbar.LENGTH_LONG
                        );
                        snackbar.setAnchorView(requireActivity().findViewById(R.id.bottom_navigation));
                        snackbar.setAction("UNDO", view -> {
                            viewModel.restorePesanan(pesanan.getId());
                        });
                        snackbar.show();
                        dismiss();
                    })
                    .setNegativeButton("Batal", null)
                    .show();
        });
    }

    private void doPrint(Pesanan pesanan) {
        android.print.PrintManager printManager = (android.print.PrintManager) requireContext().getSystemService(Context.PRINT_SERVICE);
        String jobName = "Struk DapurMoms - " + (pesanan.getNamaPemesan().equals("-") ? "Pelanggan" : pesanan.getNamaPemesan());

        printManager.print(jobName, new android.print.PrintDocumentAdapter() {
            @Override
            public void onWrite(android.print.PageRange[] pages, android.os.ParcelFileDescriptor destination, android.os.CancellationSignal cancellationSignal, WriteResultCallback callback) {
                boolean success = PdfGeneratorUtil.generateReceiptPdf(requireContext(), destination, pesanan);
                if (success) {
                    callback.onWriteFinished(new android.print.PageRange[]{android.print.PageRange.ALL_PAGES});
                } else {
                    callback.onWriteFailed("Gagal membuat struk PDF");
                }
            }

            @Override
            public void onLayout(android.print.PrintAttributes oldAttributes, android.print.PrintAttributes newAttributes, android.os.CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
                if (cancellationSignal.isCanceled()) {
                    callback.onLayoutCancelled();
                    return;
                }

                android.print.PrintDocumentInfo pdi = new android.print.PrintDocumentInfo.Builder("struk.pdf")
                        .setContentType(android.print.PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                        .build();
                callback.onLayoutFinished(pdi, true);
            }
        }, null);
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
    }
}
