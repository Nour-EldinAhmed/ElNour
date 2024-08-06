package com.example.elnour;

public class FileModel {
    String title,videourl,sub;
    int price;
    String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public FileModel() {
    }

    public FileModel(String title, String videourl,int price,String id,String sub) {
        this.title = title;
        this.videourl = videourl;
        this.price=price;
        this.id=id;
        this.sub=sub;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
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
}
