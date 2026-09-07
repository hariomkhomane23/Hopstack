package com.example.hopstack;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HostelVibes extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MemoryAdapter memoryAdapter;
    private List<Memory> memoryList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hostel_vibes);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        memoryList = new ArrayList<>();
        memoryAdapter = new MemoryAdapter(memoryList);
        recyclerView.setAdapter(memoryAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("memories");

        fetchAllMemories();
    }

    private void fetchAllMemories() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                memoryList.clear();

                if (!snapshot.exists()) {
                    Toast.makeText(HostelVibes.this, "No Memories Uploaded", Toast.LENGTH_SHORT).show();
                    memoryAdapter.notifyDataSetChanged();
                    return;
                }

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot memorySnapshot : userSnapshot.getChildren()) {
                        Memory memory = memorySnapshot.getValue(Memory.class);
                        if (memory != null) {
                            memoryList.add(memory);
                        }
                    }
                }

                memoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HostelVibes.this, "Failed to load memories", Toast.LENGTH_SHORT).show();
                Log.e("FirebaseError", error.getMessage());
            }
        });
    }
}
