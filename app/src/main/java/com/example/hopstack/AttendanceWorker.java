package com.example.hopstack;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AttendanceWorker extends Worker {

    private static final String TAG = "AttendanceWorker";
    private final DatabaseReference databaseReference;

    public AttendanceWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        databaseReference = FirebaseDatabase.getInstance("https://hopstack-59120-default-rtdb.firebaseio.com/")
                .getReference().child("Attendance");
    }

    @NonNull
    @Override
    public Result doWork() {
        checkAndMarkAbsentForMissingStudents();
        return Result.success();
    }

    private void checkAndMarkAbsentForMissingStudents() {
        String todayDate = getCurrentDate();
        int currentHour = getCurrentHour();

        // Only proceed if it's 23:00 or later
        if (currentHour < 23) {
            Log.d(TAG, "Skipping absent marking as current time is before 23:00");
            return;
        }

        Log.d(TAG, "Checking attendance for date: " + todayDate);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String emailKey = userSnapshot.getKey();

                    // If no attendance record exists for today, mark the student as absent
                    if (emailKey != null && !userSnapshot.hasChild(todayDate)) {
                        databaseReference.child(emailKey).child(todayDate).setValue("Absent")
                                .addOnSuccessListener(aVoid -> Log.d(TAG, "Marked absent at 23:00 for: " + emailKey))
                                .addOnFailureListener(e -> Log.e(TAG, "Failed to mark absent for: " + emailKey, e));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error reading attendance database", error.toException());
            }
        });
    }

    private String getCurrentDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    private int getCurrentHour() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.HOUR_OF_DAY);
    }
}
