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
    private final MutableLiveData<Calendar> selectedDate = new MutableLiveData<>();
    private final MutableLiveData<long[]> currentRange = new MutableLiveData<>();
    
    private final LiveData<List<Pesanan>> pesananList;
    private final LiveData<Long> totalUangMasuk;
    
    private final MutableLiveData<Pesanan> pesananToEdit = new MutableLiveData<>();

    public PesananViewModel(@NonNull Application application) {
        super(application);
        repository = DapurMomsRepository.getInstance(application);

        // Default to current month
        Calendar now = Calendar.getInstance();
        setMonth(now.get(Calendar.YEAR), now.get(Calendar.MONTH));

        pesananList = Transformations.switchMap(currentRange, range -> {
            return repository.getPesananBulan(range[0], range[1]);
        });

        totalUangMasuk = Transformations.switchMap(currentRange, range -> {
            return repository.getTotalUangMasukBulan(range[0], range[1]);
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

    public void restorePesanan(int id) {
        repository.restorePesanan(id);
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

    public LiveData<List<com.example.dapurmoms.data.database.entity.Menu>> getAllMenu() {
        return repository.getAllMenu();
    }

    private final MutableLiveData<Pesanan> selectedPesanan = new MutableLiveData<>();

    public LiveData<Pesanan> getSelectedPesanan() {
        return selectedPesanan;
    }

    public void setSelectedPesanan(Pesanan pesanan) {
        selectedPesanan.setValue(pesanan);
    }

    public void clearSelectedPesanan() {
        selectedPesanan.setValue(null);
    }
}
