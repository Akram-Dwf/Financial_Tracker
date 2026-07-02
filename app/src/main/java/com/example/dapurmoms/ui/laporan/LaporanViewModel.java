package com.example.dapurmoms.ui.laporan;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.dapurmoms.data.repository.DapurMomsRepository;

import java.util.Calendar;

public class LaporanViewModel extends AndroidViewModel {

    private final DapurMomsRepository repository;

    private final MutableLiveData<Calendar> selectedMonth = new MutableLiveData<>();

    private final LiveData<Long> totalUangMasuk;
    private final LiveData<Long> totalBelanja;
    private final LiveData<Long> totalBiaya;
    private final MediatorLiveData<Long> totalHpp;
    private final MediatorLiveData<Long> keuntungan;
    private final MediatorLiveData<Double> margin;

    public LaporanViewModel(@NonNull Application application) {
        super(application);
        repository = DapurMomsRepository.getInstance(application);

        // Default to current month
        Calendar now = Calendar.getInstance();
        selectedMonth.setValue(now);

        // Switch data source whenever selectedMonth changes
        totalUangMasuk = Transformations.switchMap(selectedMonth, cal -> {
            long[] range = getMonthRange(cal);
            return repository.getTotalUangMasukBulan(range[0], range[1]);
        });

        totalBelanja = Transformations.switchMap(selectedMonth, cal -> {
            long[] range = getMonthRange(cal);
            return repository.getTotalBelanjaBulan(range[0], range[1]);
        });

        totalBiaya = Transformations.switchMap(selectedMonth, cal -> {
            long[] range = getMonthRange(cal);
            return repository.getTotalBiayaBulan(range[0], range[1]);
        });

        totalHpp = new MediatorLiveData<>();
        totalHpp.setValue(0L);
        totalHpp.addSource(totalBelanja, value -> calculateHpp());
        totalHpp.addSource(totalBiaya, value -> calculateHpp());

        keuntungan = new MediatorLiveData<>();
        keuntungan.setValue(0L);
        keuntungan.addSource(totalUangMasuk, value -> calculateKeuntungan());
        keuntungan.addSource(totalHpp, value -> calculateKeuntungan());

        margin = new MediatorLiveData<>();
        margin.setValue(0.0);
        margin.addSource(totalUangMasuk, value -> calculateMargin());
        margin.addSource(keuntungan, value -> calculateMargin());
    }

    private void calculateHpp() {
        long belanja = totalBelanja.getValue() != null ? totalBelanja.getValue() : 0L;
        long biaya = totalBiaya.getValue() != null ? totalBiaya.getValue() : 0L;
        totalHpp.setValue(belanja + biaya);
    }

    private void calculateKeuntungan() {
        long masuk = totalUangMasuk.getValue() != null ? totalUangMasuk.getValue() : 0L;
        long hpp = totalHpp.getValue() != null ? totalHpp.getValue() : 0L;
        keuntungan.setValue(masuk - hpp);
    }

    private void calculateMargin() {
        long masuk = totalUangMasuk.getValue() != null ? totalUangMasuk.getValue() : 0L;
        long laba = keuntungan.getValue() != null ? keuntungan.getValue() : 0L;
        if (masuk > 0) {
            margin.setValue((double) laba / masuk * 100.0);
        } else {
            margin.setValue(0.0);
        }
    }

    public void setMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        selectedMonth.setValue(cal);
    }

    public LiveData<Calendar> getSelectedMonth() {
        return selectedMonth;
    }

    public LiveData<Long> getTotalUangMasuk() {
        return totalUangMasuk;
    }

    public LiveData<Long> getTotalBelanja() {
        return totalBelanja;
    }

    public LiveData<Long> getTotalBiaya() {
        return totalBiaya;
    }

    public LiveData<Long> getTotalHpp() {
        return totalHpp;
    }

    public LiveData<Long> getKeuntungan() {
        return keuntungan;
    }

    public LiveData<Double> getMargin() {
        return margin;
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
