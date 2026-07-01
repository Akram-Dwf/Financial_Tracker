package com.example.dapurmoms.ui.beranda;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.dapurmoms.data.repository.DapurMomsRepository;

import java.util.Calendar;

public class BerandaViewModel extends AndroidViewModel {

    private final DapurMomsRepository repository;

    private final MutableLiveData<Calendar> selectedMonth = new MutableLiveData<>();

    private final LiveData<Long> totalPesanan;
    private final LiveData<Long> totalBelanja;
    private final LiveData<Long> totalBiaya;
    private final MediatorLiveData<Long> keuntungan;

    public BerandaViewModel(@NonNull Application application) {
        super(application);
        repository = new DapurMomsRepository(application);

        // Default to current month
        Calendar now = Calendar.getInstance();
        selectedMonth.setValue(now);

        // Switch data source whenever selectedMonth changes
        totalPesanan = Transformations.switchMap(selectedMonth, cal -> {
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

        keuntungan = new MediatorLiveData<>();
        keuntungan.setValue(0L);

        keuntungan.addSource(totalPesanan, value -> calculateKeuntungan());
        keuntungan.addSource(totalBelanja, value -> calculateKeuntungan());
        keuntungan.addSource(totalBiaya, value -> calculateKeuntungan());
    }

    private void calculateKeuntungan() {
        long pesanan = totalPesanan.getValue() != null ? totalPesanan.getValue() : 0L;
        long belanja = totalBelanja.getValue() != null ? totalBelanja.getValue() : 0L;
        long biaya = totalBiaya.getValue() != null ? totalBiaya.getValue() : 0L;
        keuntungan.setValue(pesanan - belanja - biaya);
    }

    /**
     * Set the month to filter data for.
     * @param year the year (e.g. 2026)
     * @param month the month (0-indexed, January=0)
     */
    public void setMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        selectedMonth.setValue(cal);
    }

    public LiveData<Calendar> getSelectedMonth() {
        return selectedMonth;
    }

    public LiveData<Long> getTotalPesanan() {
        return totalPesanan;
    }

    public LiveData<Long> getTotalBelanja() {
        return totalBelanja;
    }

    public LiveData<Long> getTotalBiaya() {
        return totalBiaya;
    }

    public LiveData<Long> getKeuntungan() {
        return keuntungan;
    }

    /**
     * Calculate start and end timestamps for a given month.
     * @param cal Calendar set to the desired month
     * @return long[2] where [0]=start millis, [1]=end millis
     */
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
