package com.bachelor.robin.api_twitter.tweet;

public class Tweet {
    private String date = "";
    private String text = "";
    private String name = "";
    private String screen_name = "";
    private String avatar = "";

    public Tweet() {

    }

    public Tweet(String date, String text, String name, String screen_name, String avatar) {
        this.date = date;
        this.text = text;
        this.name = name;
        this.screen_name = screen_name;
        this.avatar = avatar;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getScreen_name() {
        return screen_name;
    }

    public void setScreen_name(String screen_name) {
        this.screen_name = screen_name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
