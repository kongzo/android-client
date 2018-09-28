package com.example.jiheepyo.ggung;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Comment implements Comparable<Comment>, Serializable{
    private Date commentWrittenTime;
    private final static String DATE_FORAMT_STRING = "MM-dd hh-mm";
    private String id;
    private String nickname;
    private String contents;

    public Comment(Date date, String id, String nickname, String contents){
        commentWrittenTime = date;
        this.id = id;
        this.nickname = nickname;
        this.contents = contents;
    }

    public String getWrittenTime(){
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORAMT_STRING);
        return sdf.format(commentWrittenTime);
    }

    public String getContents() {
        return contents;
    }

    public String getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }


    @Override
    public int compareTo(@NonNull Comment o) {
        if(commentWrittenTime.getTime() < o.commentWrittenTime.getTime())
            return 0;
        return 1;
    }
}
