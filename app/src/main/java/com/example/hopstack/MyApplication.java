package com.example.hopstack;

import android.app.Application;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import java.util.concurrent.TimeUnit;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        WorkRequest attendanceWorkRequest =
                new PeriodicWorkRequest.Builder(AttendanceWorker.class, 1, TimeUnit.DAYS)
                        .setInitialDelay(1, TimeUnit.MINUTES)
                        .build();

        WorkManager.getInstance(this).enqueue(attendanceWorkRequest);
    }
}
