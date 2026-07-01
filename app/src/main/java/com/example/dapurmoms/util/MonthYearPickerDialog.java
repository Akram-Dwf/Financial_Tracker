package com.example.dapurmoms.util;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.dapurmoms.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Calendar;

public class MonthYearPickerDialog extends DialogFragment {

    private static final String ARG_YEAR = "arg_year";
    private static final String ARG_MONTH = "arg_month";

    private OnDateSetListener listener;

    public interface OnDateSetListener {
        void onDateSet(int year, int month);
    }

    public static MonthYearPickerDialog newInstance(int year, int month) {
        MonthYearPickerDialog fragment = new MonthYearPickerDialog();
        Bundle args = new Bundle();
        args.putInt(ARG_YEAR, year);
        args.putInt(ARG_MONTH, month);
        fragment.setArguments(args);
        return fragment;
    }

    public void setListener(OnDateSetListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_month_year_picker, null);
        builder.setView(view);

        NumberPicker monthPicker = view.findViewById(R.id.picker_month);
        NumberPicker yearPicker = view.findViewById(R.id.picker_year);
        Button btnCancel = view.findViewById(R.id.btn_cancel);
        Button btnOk = view.findViewById(R.id.btn_ok);

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);

        if (getArguments() != null) {
            currentYear = getArguments().getInt(ARG_YEAR, currentYear);
            currentMonth = getArguments().getInt(ARG_MONTH, currentMonth);
        }

        // Setup Month Picker
        monthPicker.setMinValue(0);
        monthPicker.setMaxValue(11);
        String[] months = new String[]{"Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
        monthPicker.setDisplayedValues(months);
        monthPicker.setValue(currentMonth);

        // Setup Year Picker
        yearPicker.setMinValue(2020);
        yearPicker.setMaxValue(2050);
        yearPicker.setValue(currentYear);

        Dialog dialog = builder.create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnOk.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDateSet(yearPicker.getValue(), monthPicker.getValue());
            }
            dialog.dismiss();
        });

        return dialog;
    }
}
