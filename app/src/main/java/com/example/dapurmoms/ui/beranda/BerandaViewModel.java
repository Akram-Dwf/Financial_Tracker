package com.example.dapurmoms.ui.beranda;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.example.dapurmoms.data.repository.DapurMomsRepository;

public class BerandaViewModel extends AndroidViewModel {

    private final LiveData<Long> totalPesanan;
    private final LiveData<Long> totalBelanja;
    private final LiveData<Long> totalBiaya;
    private final MediatorLiveData<Long> keuntungan;

    public BerandaViewModel(@NonNull Application application) {
        super(application);
        DapurMomsRepository repository = new DapurMomsRepository(application);

        totalPesanan = repository.getTotalUangMasuk();
        totalBelanja = repository.getTotalBelanja();
        totalBiaya = repository.getTotalBiaya();

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
}
