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
    private final MutableLiveData<Calendar> selectedDate = new MutableLiveData<>();
    private final MutableLiveData<long[]> currentRange = new MutableLiveData<>();

    private final LiveData<List<BiayaLain>> biayaList;
    private final LiveData<Long> totalBiaya;
    private final MutableLiveData<BiayaLain> biayaToEdit = new MutableLiveData<>();

    public BiayaViewModel(@NonNull Application application) {
        super(application);
        repository = DapurMomsRepository.getInstance(application);

        Calendar now = Calendar.getInstance();
        setMonth(now.get(Calendar.YEAR), now.get(Calendar.MONTH));

        biayaList = Transformations.switchMap(currentRange, range -> {
            return repository.getBiayaBulan(range[0], range[1]);
        });

        totalBiaya = Transformations.switchMap(currentRange, range -> {
            return repository.getTotalBiayaBulan(range[0], range[1]);
        });
    }

    public LiveData<Calendar> getSelectedMonth() {
        return selectedMonth;
    }

    public LiveData<Calendar> getSelectedDate() {
        return selectedDate;
    }

    public void setMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        selectedMonth.setValue(cal);
        selectedDate.setValue(null);
        currentRange.setValue(getMonthRange(cal));
    }

    public void setDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        selectedDate.setValue(cal);
        currentRange.setValue(getDayRange(cal));
    }

    public void clearDate() {
        Calendar monthCal = selectedMonth.getValue();
        if (monthCal != null) {
            setMonth(monthCal.get(Calendar.YEAR), monthCal.get(Calendar.MONTH));
        }
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

    public void updateBiaya(BiayaLain biaya) {
        repository.updateBiaya(biaya);
    }

    public void deleteBiaya(BiayaLain biaya) {
        repository.deleteBiaya(biaya);
    }

    public void restoreBiaya(int id) {
        repository.restoreBiaya(id);
    }

    public LiveData<BiayaLain> getBiayaToEdit() {
        return biayaToEdit;
    }

    public void setBiayaToEdit(BiayaLain biaya) {
        biayaToEdit.setValue(biaya);
    }

    public void clearBiayaToEdit() {
        biayaToEdit.setValue(null);
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

    private long[] getDayRange(Calendar cal) {
        Calendar start = (Calendar) cal.clone();
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);

        Calendar end = (Calendar) start.clone();
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);
        end.set(Calendar.MILLISECOND, 999);

        return new long[]{start.getTimeInMillis(), end.getTimeInMillis()};
    }
}
