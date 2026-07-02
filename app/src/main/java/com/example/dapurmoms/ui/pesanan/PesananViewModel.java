package com.example.dapurmoms.ui.pesanan;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.dapurmoms.data.database.entity.Pesanan;
import com.example.dapurmoms.data.repository.DapurMomsRepository;

import java.util.Calendar;
import java.util.List;

public class PesananViewModel extends AndroidViewModel {

    private final DapurMomsRepository repository;

    private final MutableLiveData<Calendar> selectedMonth = new MutableLiveData<>();
    private final LiveData<List<Pesanan>> pesananList;
    private final LiveData<Long> totalUangMasuk;
    
    private final MutableLiveData<Pesanan> pesananToEdit = new MutableLiveData<>();

    public PesananViewModel(@NonNull Application application) {
        super(application);
        repository = DapurMomsRepository.getInstance(application);

        // Default to current month
        Calendar now = Calendar.getInstance();
        selectedMonth.setValue(now);

        pesananList = Transformations.switchMap(selectedMonth, cal -> {
            long[] range = getMonthRange(cal);
            return repository.getPesananBulan(range[0], range[1]);
        });

        totalUangMasuk = Transformations.switchMap(selectedMonth, cal -> {
            long[] range = getMonthRange(cal);
            return repository.getTotalUangMasukBulan(range[0], range[1]);
        });
    }

    public LiveData<Calendar> getSelectedMonth() {
        return selectedMonth;
    }

    public void setMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        selectedMonth.setValue(cal);
    }

    public LiveData<List<Pesanan>> getPesananList() {
        return pesananList;
    }

    public LiveData<Long> getTotalUangMasuk() {
        return totalUangMasuk;
    }

    public void insertPesanan(Pesanan pesanan) {
        repository.insertPesanan(pesanan);
    }

    public void updatePesanan(Pesanan pesanan) {
        repository.updatePesanan(pesanan);
    }

    public void deletePesanan(Pesanan pesanan) {
        repository.deletePesanan(pesanan);
    }

    public LiveData<Pesanan> getPesananToEdit() {
        return pesananToEdit;
    }

    public void setPesananToEdit(Pesanan pesanan) {
        pesananToEdit.setValue(pesanan);
    }

    public void clearPesananToEdit() {
        pesananToEdit.setValue(null);
    }

    private long[] getMonthRange(Calendar cal) {
        Calendar start = (Calendar) cal.clone();
        start.set(Calendar.DAY_OF_MONTH, 1);
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);

        Calendar end = (Calendar) start.clone();
        end.add(Calendar.MONTH, 1);
        end.add(Calendar.MILLISECOND, -1);

        return new long[]{start.getTimeInMillis(), end.getTimeInMillis()};
    }
}
