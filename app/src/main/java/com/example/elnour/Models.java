package com.example.elnour;

public class Models {
    private String id;
    private String title;
    private String videourl;
    private int price;

    public Models() {
    }

    public Models(String id, String title, String videourl, int price) {
        this.id = id;
        this.title = title;
        this.videourl = videourl;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideourl() {
        return videourl;
    }

    public void setVideourl(String videourl) {
        this.videourl = videourl;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
