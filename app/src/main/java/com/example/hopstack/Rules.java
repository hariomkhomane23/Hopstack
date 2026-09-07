package com.example.hopstack;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class Rules extends AppCompatActivity {

    RecyclerView rulesRecyclerView;
    RulesAdapter rulesAdapter;
    ArrayList<String> rulesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules);

        rulesRecyclerView = findViewById(R.id.rulesRecyclerView);
        rulesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        rulesList = new ArrayList<>();
        rulesList.add("Curfew: Return by 10 PM weekdays, 11 PM weekends.");
        rulesList.add("Visitors: Allowed 10 AM - 6 PM; no overnight stays.");
        rulesList.add("Cleanliness: Keep rooms and common areas tidy.");
        rulesList.add("Noise: Quiet hours after 9 PM.");
        rulesList.add("Respect Property: Report any damages.");
        rulesList.add("No Substances: No alcohol, drugs, or smoking indoors.");
        rulesList.add("Security: Carry hostel ID; don’t share keys.");
        rulesList.add("Emergency: Know emergency exits; participate in drills.");
        rulesList.add("No Cooking in Rooms: Use common kitchen.");
        rulesList.add("Respect Others: No harassment or bullying.");

        rulesAdapter = new RulesAdapter(rulesList);
        rulesRecyclerView.setAdapter(rulesAdapter);
    }
}
