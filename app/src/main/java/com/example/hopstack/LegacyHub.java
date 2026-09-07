package com.example.hopstack;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class LegacyHub extends AppCompatActivity {

    private EditText noteTitle, noteContent;
    private ImageView selectedImageView;
    private RadioGroup memoryTypeGroup;
    private Button uploadImageButton, submitNoteButton;
    private TextView uptext;
    private static final int PICK_IMAGE_REQUEST = 1;
    private DatabaseReference databaseReference;
    private String selectedMemoryType = "Note"; // Default selection

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_legacy_hub);

        noteTitle = findViewById(R.id.noteTitle);
        noteContent = findViewById(R.id.noteContent);
        selectedImageView = findViewById(R.id.selectedImageView);
        memoryTypeGroup = findViewById(R.id.memoryTypeGroup);
        uploadImageButton = findViewById(R.id.uploadImageButton);
        submitNoteButton = findViewById(R.id.submitNoteButton);
        uptext = findViewById(R.id.uptext);

        databaseReference = FirebaseDatabase.getInstance().getReference("memories");

        memoryTypeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioNote) {
                selectedMemoryType = "Note";
                noteTitle.setVisibility(View.VISIBLE);
                noteContent.setVisibility(View.VISIBLE);
                uploadImageButton.setVisibility(View.GONE);
                selectedImageView.setVisibility(View.GONE);
                uptext.setVisibility(View.GONE);
            } else {
                selectedMemoryType = "Memory";
                noteTitle.setVisibility(View.GONE);
                noteContent.setVisibility(View.GONE);
                uploadImageButton.setVisibility(View.VISIBLE);
                uptext.setVisibility(View.VISIBLE);
            }
        });

        uploadImageButton.setOnClickListener(v -> openImagePicker());

        submitNoteButton.setOnClickListener(v -> saveMemoryToFirebase());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageView.setVisibility(View.VISIBLE);
            selectedImageView.setImageURI(data.getData());
        }
    }

    private void saveMemoryToFirebase() {
        String title = noteTitle.getText().toString().trim();
        String content = noteContent.getText().toString().trim();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // 🔹 Get Logged-in User UID


        Map<String, Object> memoryData = new HashMap<>();
        memoryData.put("title", title);
        memoryData.put("type", selectedMemoryType);

        if (selectedMemoryType.equals("Note")) {
            if (content.isEmpty()) {
                Toast.makeText(this, "Please write something", Toast.LENGTH_SHORT).show();
                return;
            }

            if (title.isEmpty()) {
                Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show();
                return;
            }
            memoryData.put("content", content);
            memoryData.put("image", "image");  // 🔹 No image for Notes
        } else {  // 🔹 Memory (Image) Upload Case
            if (selectedImageView.getDrawable() != null) {
                Bitmap bitmap = ((BitmapDrawable) selectedImageView.getDrawable()).getBitmap();
                String base64Image = encodeToBase64(bitmap);
                memoryData.put("image", base64Image);

                // 🔹 Ensure content is always stored for images
                if (content.isEmpty()) {
                    content = "No description provided"; // ✅ Avoid empty content
                }
                memoryData.put("content", content);
            } else {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        String key = databaseReference.child(userId).push().getKey(); // 🔹 Store Under User UID
        if (key != null) {
            databaseReference.child(userId).child(key).setValue(memoryData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(LegacyHub.this, "Memory saved!", Toast.LENGTH_SHORT).show();
                        noteTitle.setText("");
                        noteContent.setText("");
                        selectedImageView.setVisibility(View.GONE);
                        startActivity(new Intent(LegacyHub.this, MainActivity.class));
                    })
                    .addOnFailureListener(e -> Toast.makeText(LegacyHub.this, "Failed to save", Toast.LENGTH_SHORT).show());
        }
    }


    private String encodeToBase64(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] byteArray = baos.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}
