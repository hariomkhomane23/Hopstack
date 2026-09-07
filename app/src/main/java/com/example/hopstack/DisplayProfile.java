package com.example.hopstack;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DisplayProfile extends AppCompatActivity {

    private TextView txtName, txtEmail, txtPhone, txtRoomNo, txtBranch, txtDob, txtBloodGroup;
    private ImageView profileImage;
    Button BtnEditProfile;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_profile);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userEmailKey = user.getEmail().replace(".", "_");
        databaseReference = FirebaseDatabase.getInstance().getReference("Profiles").child(userEmailKey);

        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);
        txtPhone = findViewById(R.id.txtPhone);
        txtRoomNo = findViewById(R.id.txtRoomNo);
        txtBranch = findViewById(R.id.txtBranch);
        txtDob = findViewById(R.id.txtDob);
        txtBloodGroup = findViewById(R.id.txtBloodGroup);
        profileImage = findViewById(R.id.profileImage);
        BtnEditProfile=findViewById(R.id.btnEditProfile);
        BtnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent edit=new Intent(DisplayProfile.this, Profile.class);
                startActivity(edit);
                finish();
            }
        });

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserProfile profile = snapshot.getValue(UserProfile.class);
                    txtName.setText(profile.getName().replace("Name: ", ""));
                    txtEmail.setText(profile.getEmail().replace("Email: ", ""));
                    txtPhone.setText(profile.getPhone().replace("Phone: ", ""));
                    txtRoomNo.setText(profile.getRoomNo().replace("Room No: ", ""));
                    txtBranch.setText(profile.getBranch().replace("Branch: ", ""));
                    txtDob.setText(profile.getDob().replace("DOB: ", ""));
                    txtBloodGroup.setText(profile.getBloodGroup().replace("Blood Group: ", ""));

                    if (!profile.getImage().isEmpty()) {
                        profileImage.setImageBitmap(decodeImage(profile.getImage()));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private Bitmap decodeImage(String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}
