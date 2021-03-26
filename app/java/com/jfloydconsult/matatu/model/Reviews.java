package com.jfloydconsult.matatu.model;

public class Reviews {
    public String comment;
    public String date;
    public String rating;
    public String user;

    public Reviews() {
    }
    public Reviews(String comment, String date, String rating, String user) {

        this.comment = comment;
        this.date = date;
        this.rating = rating;
        this.user = user;
    }
    public String getComment() {
        return comment;
    }

    public String getDate() {
        return date;
    }
    public String getRating() {
        return rating;
    }

    public String getUser() {
        return user;
    }

}
