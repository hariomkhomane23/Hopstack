package com.example.hopstack;

public class RaggingReport {
    private String id;
    private String report;
    private String date;

    public RaggingReport() {
        // Default constructor required for Firebase
    }

    public RaggingReport(String id, String report, String date) {
        this.id = id;
        this.report = report;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public String getReport() {
        return report;
    }

    public String getDate() {
        return date;
    }
}
