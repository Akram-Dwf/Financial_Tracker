package com.example.dapurmoms.ui.biaya;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.dapurmoms.data.database.entity.BiayaLain;
import com.example.dapurmoms.data.repository.DapurMomsRepository;

import java.util.Calendar;
import java.util.List;

public class BiayaViewModel extends AndroidViewModel {

    private final DapurMomsRepository repository;
    private final MutableLiveData<Calendar> selectedMonth = new MutableLiveData<>();
    private final LiveData<List<BiayaLain>> biayaList;
    private final LiveData<Long> totalBiaya;

    public BiayaViewModel(@NonNull Application application) {
        super(application);
        repository = DapurMomsRepository.getInstance(application);

        Calendar now = Calendar.getInstance();
        selectedMonth.setValue(now);

        biayaList = Transformations.switchMap(selectedMonth, cal -> {
            long[] range = getMonthRange(cal);
            return repository.getBiayaBulan(range[0], range[1]);
        });

        totalBiaya = Transformations.switchMap(selectedMonth, cal -> {
            long[] range = getMonthRange(cal);
            return repository.getTotalBiayaBulan(range[0], range[1]);
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

    public LiveData<List<BiayaLain>> getBiayaList() {
        return biayaList;
    }

    public LiveData<Long> getTotalBiaya() {
        return totalBiaya;
    }

    public void insertBiaya(BiayaLain biaya) {
        repository.insertBiaya(biaya);
    }

    public void deleteBiaya(BiayaLain biaya) {
        repository.deleteBiaya(biaya);
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
