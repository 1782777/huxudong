package com.example.teachapplication;

public class Team {
    private String name;
    private int imageId;
    private int score;
    public Team(String name, int imageId,int score) {
        this.name = name;
        this.imageId = imageId;
        this.score = score;
    }
    public String getName() {
        return name;
    }
    public int getImageId() {
        return imageId;
    }
    public int getScore(){
        return score;
    }
}
