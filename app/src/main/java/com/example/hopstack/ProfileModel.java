package com.example.hopstack;

public class ProfileModel {
    private String name, email, phone, roomNo, branch, dob, bloodGroup, image;

    public ProfileModel() {
        // Default constructor required for Firebase
    }

    public ProfileModel(String name, String email, String phone, String roomNo, String branch, String dob, String bloodGroup, String image) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.roomNo = roomNo;
        this.branch = branch;
        this.dob = dob;
        this.bloodGroup = bloodGroup;
        this.image = image;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getRoomNo() { return roomNo; }
    public String getBranch() { return branch; }
    public String getDob() { return dob; }
    public String getBloodGroup() { return bloodGroup; }
    public String getImage() { return image; }
}
