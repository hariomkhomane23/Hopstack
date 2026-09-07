package com.example.hopstack;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {
    private List<RaggingReport> reportList;

    public ReportAdapter(List<RaggingReport> reportList) {
        this.reportList = reportList;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reports, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        RaggingReport report = reportList.get(position);
        holder.reportText.setText(report.getReport());
        holder.reportDate.setText("Submitted on: " + report.getDate());
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView reportText, reportDate;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            reportText = itemView.findViewById(R.id.reportText);
            reportDate = itemView.findViewById(R.id.reportDate);
        }
    }
}
