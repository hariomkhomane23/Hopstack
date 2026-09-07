package com.example.hopstack;

public class StudentProfile {
    private String name;
    private String branch;

    public StudentProfile(String name, String branch) {
        this.name = name;
        this.branch = branch;
    }

    public String getName() {
        return name;
    }

    public String getBranch() {
        return branch;
    }
}
