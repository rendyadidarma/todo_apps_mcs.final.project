package com.example.mcsproject;

import com.google.firebase.firestore.FieldValue;

import java.util.Date;

public class Tasks {

//    data.put("title", titleValue);
//                data.put("date", dateValue);
//                data.put("time", timeValue);
//                data.put("type", typeValue);
//                data.put("createdAt", FieldValue.serverTimestamp());
//                data.put("flag", false);

    private Date date;
    private String time;
    private String type;
    private String title;
    private Date createdAt;
    private boolean flag;

    public Tasks() {

    }

    public Tasks(Date date, String time, String type, String title, Date createdAt, boolean flag) {
        this.date = date;
        this.time = time;
        this.type = type;
        this.title = title;
        this.createdAt = createdAt;
        this.flag = flag;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
