package com.example.dapurmoms.worker;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.example.dapurmoms.data.repository.DapurMomsRepository;

public class TrashCleanupWorker extends Worker {
    public TrashCleanupWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        DapurMomsRepository repository = DapurMomsRepository.getInstance((android.app.Application) getApplicationContext());
        // 30 days limit
        long limitTimestamp = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000);
        repository.permanentlyDeleteOlderThan(limitTimestamp);
        return Result.success();
    }
}
