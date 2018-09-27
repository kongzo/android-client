package com.example.jiheepyo.ggung;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Post implements Serializable {
    private int idx;
    private double latitude;
    private double longitude;
    private int likes;
    private int layout;
    private String nickname;
    private String contents;
    private Date writtenTime;
    private ArrayList<Comment> comments;
    private final static String DATE_FORAMT_STRING = "MM-dd hh-mm";

    Post(int idx, double latitude, double longitude, int likes, String nickname, String contents, Date date, int layout){
        this.idx = idx;
        this.latitude = latitude;
        this.longitude = longitude;
        this.likes = likes;
        this.nickname = nickname;
        this.contents = contents;
        this.layout = layout;
        writtenTime = date;
        comments = new ArrayList<>();
    }

    public String getWrittenTime() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORAMT_STRING);
        return sdf.format(writtenTime);
    }

    public void addComment(Comment comment){
        comments.add(comment);
    }

    public int getIdx() {
        return idx;
    }

    public String getNickname() {
        return nickname;
    }

    public String getContents() {
        return contents;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getLikes() {
        return likes;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public int getLayout() {
        return layout;
    }
}
