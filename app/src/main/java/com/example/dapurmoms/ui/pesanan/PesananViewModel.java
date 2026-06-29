package com.example.dapurmoms.ui.pesanan;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.dapurmoms.data.database.entity.Pesanan;
import com.example.dapurmoms.data.repository.DapurMomsRepository;

import java.util.List;

public class PesananViewModel extends AndroidViewModel {

    private final DapurMomsRepository repository;
    private final LiveData<List<Pesanan>> allPesanan;
    private final LiveData<Long> totalUangMasuk;

    public PesananViewModel(@NonNull Application application) {
        super(application);
        repository = new DapurMomsRepository(application);
        allPesanan = repository.getAllPesanan();
        totalUangMasuk = repository.getTotalUangMasuk();
    }

    public LiveData<List<Pesanan>> getAllPesanan() {
        return allPesanan;
    }

    public LiveData<Long> getTotalUangMasuk() {
        return totalUangMasuk;
    }

    public void insertPesanan(Pesanan pesanan) {
        repository.insertPesanan(pesanan);
    }

    public void deletePesanan(Pesanan pesanan) {
        repository.deletePesanan(pesanan);
    }
}
