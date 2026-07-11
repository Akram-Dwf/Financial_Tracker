package com.example.dapurmoms.ui.trash;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.dapurmoms.data.database.entity.BelanjaBahan;
import com.example.dapurmoms.data.database.entity.BiayaLain;
import com.example.dapurmoms.data.database.entity.Pesanan;
import com.example.dapurmoms.data.repository.DapurMomsRepository;
import java.util.List;

public class TrashViewModel extends AndroidViewModel {
    private final DapurMomsRepository repository;

    public TrashViewModel(@NonNull Application application) {
        super(application);
        repository = DapurMomsRepository.getInstance(application);
    }

    public LiveData<List<Pesanan>> getDeletedPesanan() {
        return repository.getDeletedPesanan();
    }

    public LiveData<List<BelanjaBahan>> getDeletedBelanja() {
        return repository.getDeletedBelanja();
    }

    public LiveData<List<BiayaLain>> getDeletedBiaya() {
        return repository.getDeletedBiaya();
    }

    public void restorePesanan(int id) {
        repository.restorePesanan(id);
    }

    public void restoreBelanja(int id) {
        repository.restoreBelanja(id);
    }

    public void restoreBiaya(int id) {
        repository.restoreBiaya(id);
    }
}
