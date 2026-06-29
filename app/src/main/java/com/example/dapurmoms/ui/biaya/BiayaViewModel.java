package com.example.dapurmoms.ui.biaya;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.dapurmoms.data.database.entity.BiayaLain;
import com.example.dapurmoms.data.repository.DapurMomsRepository;

import java.util.List;

public class BiayaViewModel extends AndroidViewModel {

    private final DapurMomsRepository repository;
    private final LiveData<List<BiayaLain>> allBiaya;
    private final LiveData<Long> totalBiaya;

    public BiayaViewModel(@NonNull Application application) {
        super(application);
        repository = new DapurMomsRepository(application);
        allBiaya = repository.getAllBiaya();
        totalBiaya = repository.getTotalBiaya();
    }

    public LiveData<List<BiayaLain>> getAllBiaya() {
        return allBiaya;
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
}
