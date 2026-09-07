package com.example.hopstack;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MemoryAdapter extends RecyclerView.Adapter<MemoryAdapter.MemoryViewHolder> {
    private List<Memory> memoryList;

    public MemoryAdapter(List<Memory> memoryList) {
        this.memoryList = memoryList;
    }

    @NonNull
    @Override
    public MemoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.memory_item, parent, false);
        return new MemoryViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull MemoryViewHolder holder, int position) {
        Memory memory = memoryList.get(position);
        if (memory.getType().equals("Note")) {
            // Show the text content and hide the image
            holder.memoryTitle.setText(memory.getTitle());
            holder.memoryTitle.setVisibility(View.VISIBLE);
            holder.memoryContent.setText(memory.getContent());
            holder.memoryContent.setVisibility(View.VISIBLE);
            holder.memoryImage.setVisibility(View.GONE);
        } else { // 🔹 This handles BOTH text + image case
            holder.memoryTitle.setText(memory.getTitle());
            holder.memoryTitle.setVisibility(View.VISIBLE);
            holder.memoryContent.setText(memory.getContent());
            holder.memoryContent.setVisibility(View.VISIBLE);

            if (memory.getImage() != null && !memory.getImage().isEmpty()) {
                holder.memoryImage.setImageBitmap(decodeBase64ToBitmap(memory.getImage()));
                holder.memoryImage.setVisibility(View.VISIBLE);
            } else {
                holder.memoryImage.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public int getItemCount() {
        return memoryList.size();
    }

    public static class MemoryViewHolder extends RecyclerView.ViewHolder {
        TextView memoryTitle, memoryContent;
        ImageView memoryImage;

        public MemoryViewHolder(@NonNull View itemView) {
            super(itemView);
            memoryTitle = itemView.findViewById(R.id.memoryTitle);
            memoryContent = itemView.findViewById(R.id.memoryContent);
            memoryImage = itemView.findViewById(R.id.memoryImage);
        }
    }

    private Bitmap decodeBase64ToBitmap(String encodedImage) {
        try {
            byte[] decodedBytes = Base64.decode(encodedImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (Exception e) {
            return null; // Return null if decoding fails
        }
    }
}
