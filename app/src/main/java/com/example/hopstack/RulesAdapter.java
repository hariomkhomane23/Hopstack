package com.example.hopstack;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RulesAdapter extends RecyclerView.Adapter<RulesAdapter.ViewHolder> {

    private final List<String> rulesList;

    public RulesAdapter(List<String> rulesList) {
        this.rulesList = rulesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rule_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.ruleText.setText(rulesList.get(position));
    }

    @Override
    public int getItemCount() {
        return rulesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView ruleText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ruleText = itemView.findViewById(R.id.ruleText);
        }
    }
}
