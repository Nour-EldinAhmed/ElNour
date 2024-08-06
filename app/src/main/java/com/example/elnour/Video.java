package com.example.elnour;
public class Video {
    private String videoName;
    private String videoUrl;
    private String price;
    private String subject;
    private String gradeYear;

    public Video(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public Video() {
        // Default constructor required for calls to DataSnapshot.getValue(Video.class)
    }

    public Video(String videoName, String videoUrl, String price, String subject, String gradeYear) {
        this.videoName = videoName;
        this.videoUrl = videoUrl;
        this.price = price;
        this.subject = subject;
        this.gradeYear = gradeYear;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getGradeYear() {
        return gradeYear;
    }

    public void setGradeYear(String gradeYear) {
        this.gradeYear = gradeYear;
    }
// Getters and setters
}
