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
    private final LiveData<Long> totalPesananCash;
    private final LiveData<Long> totalPesananTransfer;
    private final LiveData<Long> totalPiutang;

    private final LiveData<Long> totalBelanja;
    private final LiveData<Long> totalBelanjaCash;
    private final LiveData<Long> totalBelanjaTransfer;
    private final LiveData<Long> totalUtangBelanja;

    private final LiveData<Long> totalBiaya;
    private final LiveData<Long> totalBiayaCash;
    private final LiveData<Long> totalBiayaTransfer;
    private final LiveData<Long> totalUtangBiaya;

    private final LiveData<java.util.List<com.example.dapurmoms.data.database.entity.Pesanan>> listPesanan;
    private final LiveData<java.util.List<com.example.dapurmoms.data.database.entity.BelanjaBahan>> listBelanja;
    private final LiveData<java.util.List<com.example.dapurmoms.data.database.entity.BiayaLain>> listBiaya;
    private final MediatorLiveData<Long> totalHpp;
    private final MediatorLiveData<Long> keuntungan;
    private final MediatorLiveData<Double> margin;

    private final MediatorLiveData<Long> totalUangMasukRealized;
    private final MediatorLiveData<Long> totalBelanjaRealized;
    private final MediatorLiveData<Long> totalBiayaRealized;
    private final MediatorLiveData<Long> totalUtang;

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
        totalPesananCash = Transformations.switchMap(selectedMonth, cal -> {
            long[] range = getMonthRange(cal);
            return repository.getTotalPesananByMetodeBulan(range[0], range[1], "Cash");
        });
        totalPesananTransfer = Transformations.switchMap(selectedMonth, cal -> {
            long[] range = getMonthRange(cal);
            return repository.getTotalPesananByMetodeBulan(range[0], range[1], "Transfer");
        });
        totalPiutang = Transformations.switchMap(selectedMonth, cal -> {
            long[] range = getMonthRange(cal);
            return repository.getTotalPiutangBulan(range[0], range[1]);
        });

        totalBelanja = Transformations.switchMap(selectedMonth, cal -> {
            long[] range = getMonthRange(cal);
            return repository.getTotalBelanjaBulan(range[0], range[1]);
        });
        totalBelanjaCash = Transformations.switchMap(selectedMonth, cal -> {
            long[] range = getMonthRange(cal);
            return repository.getTotalBelanjaByMetodeBulan(range[0], range[1], "Cash");
        });
        totalBelanjaTransfer = Transformations.switchMap(selectedMonth, cal -> {
            long[] range = getMonthRange(cal);
            return repository.getTotalBelanjaByMetodeBulan(range[0], range[1], "Transfer");
        });
        totalUtangBelanja = Transformations.switchMap(selectedMonth, cal -> {
            long[] range = getMonthRange(cal);
            return repository.getTotalUtangBelanjaBulan(range[0], range[1]);
        });

        totalBiaya = Transformations.switchMap(selectedMonth, cal -> {
            long[] range = getMonthRange(cal);
            return repository.getTotalBiayaBulan(range[0], range[1]);
        });
        totalBiayaCash = Transformations.switchMap(selectedMonth, cal -> {
            long[] range = getMonthRange(cal);
            return repository.getTotalBiayaByMetodeBulan(range[0], range[1], "Cash");
        });
        totalBiayaTransfer = Transformations.switchMap(selectedMonth, cal -> {
            long[] range = getMonthRange(cal);
            return repository.getTotalBiayaByMetodeBulan(range[0], range[1], "Transfer");
        });
        totalUtangBiaya = Transformations.switchMap(selectedMonth, cal -> {
            long[] range = getMonthRange(cal);
            return repository.getTotalUtangBiayaBulan(range[0], range[1]);
        });
        
        listPesanan = Transformations.switchMap(selectedMonth, cal -> {
            long[] range = getMonthRange(cal);
            return repository.getPesananBulan(range[0], range[1]);
        });
        
        listBelanja = Transformations.switchMap(selectedMonth, cal -> {
            long[] range = getMonthRange(cal);
            return repository.getBelanjaBulan(range[0], range[1]);
        });
        
        listBiaya = Transformations.switchMap(selectedMonth, cal -> {
            long[] range = getMonthRange(cal);
            return repository.getBiayaBulan(range[0], range[1]);
        });

        totalHpp = new MediatorLiveData<>();
        totalHpp.setValue(0L);
        totalHpp.addSource(totalBelanjaCash, value -> calculateHpp());
        totalHpp.addSource(totalBelanjaTransfer, value -> calculateHpp());
        totalHpp.addSource(totalBiayaCash, value -> calculateHpp());
        totalHpp.addSource(totalBiayaTransfer, value -> calculateHpp());

        keuntungan = new MediatorLiveData<>();
        keuntungan.setValue(0L);
        keuntungan.addSource(totalPesananCash, value -> calculateKeuntungan());
        keuntungan.addSource(totalPesananTransfer, value -> calculateKeuntungan());
        keuntungan.addSource(totalHpp, value -> calculateKeuntungan());

        margin = new MediatorLiveData<>();
        margin.setValue(0.0);
        margin.addSource(totalPesananCash, value -> calculateMargin());
        margin.addSource(totalPesananTransfer, value -> calculateMargin());
        margin.addSource(keuntungan, value -> calculateMargin());

        totalUangMasukRealized = new MediatorLiveData<>();
        totalUangMasukRealized.setValue(0L);
        totalUangMasukRealized.addSource(totalPesananCash, value -> calculateUangMasukRealized());
        totalUangMasukRealized.addSource(totalPesananTransfer, value -> calculateUangMasukRealized());

        totalBelanjaRealized = new MediatorLiveData<>();
        totalBelanjaRealized.setValue(0L);
        totalBelanjaRealized.addSource(totalBelanjaCash, value -> calculateBelanjaRealized());
        totalBelanjaRealized.addSource(totalBelanjaTransfer, value -> calculateBelanjaRealized());

        totalBiayaRealized = new MediatorLiveData<>();
        totalBiayaRealized.setValue(0L);
        totalBiayaRealized.addSource(totalBiayaCash, value -> calculateBiayaRealized());
        totalBiayaRealized.addSource(totalBiayaTransfer, value -> calculateBiayaRealized());

        totalUtang = new MediatorLiveData<>();
        totalUtang.setValue(0L);
        totalUtang.addSource(totalUtangBelanja, value -> calculateTotalUtang());
        totalUtang.addSource(totalUtangBiaya, value -> calculateTotalUtang());
    }

    private void calculateHpp() {
        long belanjaCash = totalBelanjaCash.getValue() != null ? totalBelanjaCash.getValue() : 0L;
        long belanjaTf = totalBelanjaTransfer.getValue() != null ? totalBelanjaTransfer.getValue() : 0L;
        long biayaCash = totalBiayaCash.getValue() != null ? totalBiayaCash.getValue() : 0L;
        long biayaTf = totalBiayaTransfer.getValue() != null ? totalBiayaTransfer.getValue() : 0L;
        totalHpp.setValue(belanjaCash + belanjaTf + biayaCash + biayaTf);
    }

    private void calculateKeuntungan() {
        long masukCash = totalPesananCash.getValue() != null ? totalPesananCash.getValue() : 0L;
        long masukTf = totalPesananTransfer.getValue() != null ? totalPesananTransfer.getValue() : 0L;
        long hpp = totalHpp.getValue() != null ? totalHpp.getValue() : 0L;
        keuntungan.setValue((masukCash + masukTf) - hpp);
    }

    private void calculateMargin() {
        long masukCash = totalPesananCash.getValue() != null ? totalPesananCash.getValue() : 0L;
        long masukTf = totalPesananTransfer.getValue() != null ? totalPesananTransfer.getValue() : 0L;
        long masuk = masukCash + masukTf;
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
    
    public LiveData<Long> getTotalPesananCash() {
        return totalPesananCash;
    }
    
    public LiveData<Long> getTotalPesananTransfer() {
        return totalPesananTransfer;
    }
    
    public LiveData<Long> getTotalPiutang() {
        return totalPiutang;
    }

    public LiveData<Long> getTotalBelanja() {
        return totalBelanja;
    }
    
    public LiveData<Long> getTotalBelanjaCash() {
        return totalBelanjaCash;
    }
    
    public LiveData<Long> getTotalBelanjaTransfer() {
        return totalBelanjaTransfer;
    }
    
    public LiveData<Long> getTotalUtangBelanja() {
        return totalUtangBelanja;
    }

    public LiveData<Long> getTotalBiaya() {
        return totalBiaya;
    }
    
    public LiveData<Long> getTotalBiayaCash() {
        return totalBiayaCash;
    }
    
    public LiveData<Long> getTotalBiayaTransfer() {
        return totalBiayaTransfer;
    }
    
    public LiveData<Long> getTotalUtangBiaya() {
        return totalUtangBiaya;
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
    
    public LiveData<java.util.List<com.example.dapurmoms.data.database.entity.Pesanan>> getListPesanan() {
        return listPesanan;
    }
    
    public LiveData<java.util.List<com.example.dapurmoms.data.database.entity.BelanjaBahan>> getListBelanja() {
        return listBelanja;
    }
    
    public LiveData<java.util.List<com.example.dapurmoms.data.database.entity.BiayaLain>> getListBiaya() {
        return listBiaya;
    }

    private void calculateUangMasukRealized() {
        long cash = totalPesananCash.getValue() != null ? totalPesananCash.getValue() : 0L;
        long tf = totalPesananTransfer.getValue() != null ? totalPesananTransfer.getValue() : 0L;
        totalUangMasukRealized.setValue(cash + tf);
    }

    private void calculateBelanjaRealized() {
        long cash = totalBelanjaCash.getValue() != null ? totalBelanjaCash.getValue() : 0L;
        long tf = totalBelanjaTransfer.getValue() != null ? totalBelanjaTransfer.getValue() : 0L;
        totalBelanjaRealized.setValue(cash + tf);
    }

    private void calculateBiayaRealized() {
        long cash = totalBiayaCash.getValue() != null ? totalBiayaCash.getValue() : 0L;
        long tf = totalBiayaTransfer.getValue() != null ? totalBiayaTransfer.getValue() : 0L;
        totalBiayaRealized.setValue(cash + tf);
    }

    private void calculateTotalUtang() {
        long utangBelanja = totalUtangBelanja.getValue() != null ? totalUtangBelanja.getValue() : 0L;
        long utangBiaya = totalUtangBiaya.getValue() != null ? totalUtangBiaya.getValue() : 0L;
        totalUtang.setValue(utangBelanja + utangBiaya);
    }

    public LiveData<Long> getTotalUangMasukRealized() {
        return totalUangMasukRealized;
    }

    public LiveData<Long> getTotalBelanjaRealized() {
        return totalBelanjaRealized;
    }

    public LiveData<Long> getTotalBiayaRealized() {
        return totalBiayaRealized;
    }

    public LiveData<Long> getTotalUtang() {
        return totalUtang;
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
