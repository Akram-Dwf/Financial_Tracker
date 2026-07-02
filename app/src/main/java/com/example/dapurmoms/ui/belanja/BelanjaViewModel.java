package com.example.dapurmoms.ui.belanja;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.dapurmoms.data.database.entity.BelanjaBahan;
import com.example.dapurmoms.data.repository.DapurMomsRepository;

import java.util.Calendar;
import java.util.List;

public class BelanjaViewModel extends AndroidViewModel {

    private final DapurMomsRepository repository;
    private final MutableLiveData<Calendar> selectedMonth = new MutableLiveData<>();
    private final MutableLiveData<Calendar> selectedDate = new MutableLiveData<>();
    private final MutableLiveData<long[]> currentRange = new MutableLiveData<>();

    private final LiveData<List<BelanjaBahan>> belanjaList;
    private final LiveData<Long> totalBelanja;
    private final MutableLiveData<BelanjaBahan> belanjaToEdit = new MutableLiveData<>();

    public BelanjaViewModel(@NonNull Application application) {
        super(application);
        repository = DapurMomsRepository.getInstance(application);

        Calendar now = Calendar.getInstance();
        setMonth(now.get(Calendar.YEAR), now.get(Calendar.MONTH));

        belanjaList = Transformations.switchMap(currentRange, range -> {
            return repository.getBelanjaBulan(range[0], range[1]);
        });

        totalBelanja = Transformations.switchMap(currentRange, range -> {
            return repository.getTotalBelanjaBulan(range[0], range[1]);
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

    public LiveData<List<BelanjaBahan>> getBelanjaList() {
        return belanjaList;
    }

    public LiveData<Long> getTotalBelanja() {
        return totalBelanja;
    }

    public void insertBelanja(BelanjaBahan belanja) {
        repository.insertBelanja(belanja);
    }

    public void updateBelanja(BelanjaBahan belanja) {
        repository.updateBelanja(belanja);
    }

    public void deleteBelanja(BelanjaBahan belanja) {
        repository.deleteBelanja(belanja);
    }

    public LiveData<BelanjaBahan> getBelanjaToEdit() {
        return belanjaToEdit;
    }

    public void setBelanjaToEdit(BelanjaBahan belanja) {
        belanjaToEdit.setValue(belanja);
    }

    public void clearBelanjaToEdit() {
        belanjaToEdit.setValue(null);
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
