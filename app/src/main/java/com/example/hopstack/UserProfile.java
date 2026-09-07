package com.example.hopstack;

public class UserProfile {
    private String name;
    private String email;
    private String phone;
    private String roomNo;
    private String branch;
    private String dob;
    private String bloodGroup;
    private String image;  // Store image URL

    // Required empty constructor for Firebase
    public UserProfile() {
    }

    public UserProfile(String name, String email, String phone, String roomNo, String branch, String dob, String bloodGroup, String image) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.roomNo = roomNo;
        this.branch = branch;
        this.dob = dob;
        this.bloodGroup = bloodGroup;
        this.image = image;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public String getBranch() {
        return branch;
    }

    public String getDob() {
        return dob;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public String getImage() {
        return image;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
