package com.example.hopstack;

public class Memory {
    private String title;
    private String content;
    private String image;
    private String type;

    // Default constructor (Required for Firebase)
    public Memory() {}

    public Memory(String title, String content, String image, String type) {
        this.title = title;
        this.content = content;
        this.image = image;
        this.type = type;
    }

    public String getTitle() {
        return title != null ? title : "";
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content != null ? content : "";
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image != null ? image : "";
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type != null ? type : "Note";
    }

    public void setType(String type) {
        this.type = type;
    }
}
