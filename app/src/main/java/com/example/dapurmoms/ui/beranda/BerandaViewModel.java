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

    private final LiveData<Long> totalPesananCash;
    private final LiveData<Long> totalPesananTransfer;
    private final LiveData<Long> totalBelanjaCash;
    private final LiveData<Long> totalBelanjaTransfer;
    private final LiveData<Long> totalBiayaCash;
    private final LiveData<Long> totalBiayaTransfer;
    private final MediatorLiveData<Long> saldoKasAktual;

    private final LiveData<Long> totalPiutangAktif;
    private final LiveData<Long> totalUtangBelanjaAktif;
    private final LiveData<Long> totalUtangBiayaAktif;
    private final MediatorLiveData<Long> totalUtangAktif;

    public BerandaViewModel(@NonNull Application application) {
        super(application);
        repository = DapurMomsRepository.getInstance(application);

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

        // Cash flow breakdowns for selected month
        totalPesananCash = Transformations.switchMap(selectedMonth, cal -> {
            long[] range = getMonthRange(cal);
            return repository.getTotalPesananByMetodeBulan(range[0], range[1], "Cash");
        });
        totalPesananTransfer = Transformations.switchMap(selectedMonth, cal -> {
            long[] range = getMonthRange(cal);
            return repository.getTotalPesananByMetodeBulan(range[0], range[1], "Transfer");
        });
        totalBelanjaCash = Transformations.switchMap(selectedMonth, cal -> {
            long[] range = getMonthRange(cal);
            return repository.getTotalBelanjaByMetodeBulan(range[0], range[1], "Cash");
        });
        totalBelanjaTransfer = Transformations.switchMap(selectedMonth, cal -> {
            long[] range = getMonthRange(cal);
            return repository.getTotalBelanjaByMetodeBulan(range[0], range[1], "Transfer");
        });
        totalBiayaCash = Transformations.switchMap(selectedMonth, cal -> {
            long[] range = getMonthRange(cal);
            return repository.getTotalBiayaByMetodeBulan(range[0], range[1], "Cash");
        });
        totalBiayaTransfer = Transformations.switchMap(selectedMonth, cal -> {
            long[] range = getMonthRange(cal);
            return repository.getTotalBiayaByMetodeBulan(range[0], range[1], "Transfer");
        });

        keuntungan = new MediatorLiveData<>();
        keuntungan.setValue(0L);
        keuntungan.addSource(totalPesanan, value -> calculateKeuntungan());
        keuntungan.addSource(totalBelanja, value -> calculateKeuntungan());
        keuntungan.addSource(totalBiaya, value -> calculateKeuntungan());

        saldoKasAktual = new MediatorLiveData<>();
        saldoKasAktual.setValue(0L);
        saldoKasAktual.addSource(totalPesananCash, value -> calculateSaldoKasAktual());
        saldoKasAktual.addSource(totalPesananTransfer, value -> calculateSaldoKasAktual());
        saldoKasAktual.addSource(totalBelanjaCash, value -> calculateSaldoKasAktual());
        saldoKasAktual.addSource(totalBelanjaTransfer, value -> calculateSaldoKasAktual());
        saldoKasAktual.addSource(totalBiayaCash, value -> calculateSaldoKasAktual());
        saldoKasAktual.addSource(totalBiayaTransfer, value -> calculateSaldoKasAktual());

        // Active Debt & Receivables (All time cumulative)
        totalPiutangAktif = repository.getTotalPiutangAktif();
        totalUtangBelanjaAktif = repository.getTotalUtangBelanjaAktif();
        totalUtangBiayaAktif = repository.getTotalUtangBiayaAktif();

        totalUtangAktif = new MediatorLiveData<>();
        totalUtangAktif.setValue(0L);
        totalUtangAktif.addSource(totalUtangBelanjaAktif, value -> calculateTotalUtangAktif());
        totalUtangAktif.addSource(totalUtangBiayaAktif, value -> calculateTotalUtangAktif());
    }

    private void calculateKeuntungan() {
        long pesanan = totalPesanan.getValue() != null ? totalPesanan.getValue() : 0L;
        long belanja = totalBelanja.getValue() != null ? totalBelanja.getValue() : 0L;
        long biaya = totalBiaya.getValue() != null ? totalBiaya.getValue() : 0L;
        keuntungan.setValue(pesanan - belanja - biaya);
    }

    private void calculateSaldoKasAktual() {
        long inCash = totalPesananCash.getValue() != null ? totalPesananCash.getValue() : 0L;
        long inTransfer = totalPesananTransfer.getValue() != null ? totalPesananTransfer.getValue() : 0L;
        long outBelanjaCash = totalBelanjaCash.getValue() != null ? totalBelanjaCash.getValue() : 0L;
        long outBelanjaTransfer = totalBelanjaTransfer.getValue() != null ? totalBelanjaTransfer.getValue() : 0L;
        long outBiayaCash = totalBiayaCash.getValue() != null ? totalBiayaCash.getValue() : 0L;
        long outBiayaTransfer = totalBiayaTransfer.getValue() != null ? totalBiayaTransfer.getValue() : 0L;
        saldoKasAktual.setValue((inCash + inTransfer) - (outBelanjaCash + outBelanjaTransfer + outBiayaCash + outBiayaTransfer));
    }

    private void calculateTotalUtangAktif() {
        long utangBelanja = totalUtangBelanjaAktif.getValue() != null ? totalUtangBelanjaAktif.getValue() : 0L;
        long utangBiaya = totalUtangBiayaAktif.getValue() != null ? totalUtangBiayaAktif.getValue() : 0L;
        totalUtangAktif.setValue(utangBelanja + utangBiaya);
    }

    public LiveData<Long> getSaldoKasAktual() {
        return saldoKasAktual;
    }

    public LiveData<Long> getTotalPiutangAktif() {
        return totalPiutangAktif;
    }

    public LiveData<Long> getTotalUtangAktif() {
        return totalUtangAktif;
    }

    public LiveData<java.util.List<com.example.dapurmoms.data.database.entity.Pesanan>> getPiutangPesananList() {
        return repository.getPiutangPesananAktif();
    }

    public LiveData<java.util.List<com.example.dapurmoms.data.database.entity.BelanjaBahan>> getUtangBelanjaList() {
        return repository.getUtangBelanjaAktif();
    }

    public LiveData<java.util.List<com.example.dapurmoms.data.database.entity.BiayaLain>> getUtangBiayaList() {
        return repository.getUtangBiayaAktif();
    }

    public void lunasiPesanan(com.example.dapurmoms.data.database.entity.Pesanan pesanan, String metodePembayaran) {
        pesanan.setMetodePembayaran(metodePembayaran);
        repository.updatePesanan(pesanan);
    }

    public void lunasiBelanja(com.example.dapurmoms.data.database.entity.BelanjaBahan belanja, String metodePembayaran) {
        belanja.setMetodePembayaran(metodePembayaran);
        repository.updateBelanja(belanja);
    }

    public void lunasiBiaya(com.example.dapurmoms.data.database.entity.BiayaLain biaya, String metodePembayaran) {
        biaya.setMetodePembayaran(metodePembayaran);
        repository.updateBiaya(biaya);
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
