package com.example.dapurmoms.ui.belanja;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.dapurmoms.data.database.entity.BelanjaBahan;
import com.example.dapurmoms.data.repository.DapurMomsRepository;

import java.util.List;

public class BelanjaViewModel extends AndroidViewModel {

    private final DapurMomsRepository repository;
    private final LiveData<List<BelanjaBahan>> allBelanja;
    private final LiveData<Long> totalBelanja;

    public BelanjaViewModel(@NonNull Application application) {
        super(application);
        repository = new DapurMomsRepository(application);
        allBelanja = repository.getAllBelanja();
        totalBelanja = repository.getTotalBelanja();
    }

    public LiveData<List<BelanjaBahan>> getAllBelanja() {
        return allBelanja;
    }

    public LiveData<Long> getTotalBelanja() {
        return totalBelanja;
    }

    public void insertBelanja(BelanjaBahan belanja) {
        repository.insertBelanja(belanja);
    }

    public void deleteBelanja(BelanjaBahan belanja) {
        repository.deleteBelanja(belanja);
    }
}
