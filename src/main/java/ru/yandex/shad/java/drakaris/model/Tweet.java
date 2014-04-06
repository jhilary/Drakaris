package ru.yandex.shad.java.drakaris.model;

import org.joda.time.DateTime;

public class Tweet {
    private long ID;
    private long userID;
    private String text;
    private DateTime data;

    public Tweet(long ID, long userID, String text, DateTime data) {
        this.ID = ID;
        this.userID = userID;
        this.text = text;
        this.data = data;
    }

    public long getUserID() {
        return userID;
    }

    public String getText() {
        return text;
    }

    public DateTime getData() {
        return data;
    }

    @Override
    public String toString(){
        return "(" + this.ID + ", " + this.userID + ", " + this.text + ", " + this.data + ")";
    }
}
