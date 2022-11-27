package com.example.upload.model;

public class DataUpload {
    private String key;
    private String path;
    private String image;
    private String title;
    private String description;

    public DataUpload() {
    }

    public DataUpload(String path, String image, String title, String description) {
        this.path = path;
        this.image = image;
        this.title = title;
        this.description = description;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String getPath() {
        return path;
    }

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
