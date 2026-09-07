package com.example.hopstack;

public class Attendance_new {
    private String name;

    public Attendance_new() {
        // Default constructor required for Firebase
    }

    public Attendance_new(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
