package com.spinarplus.googlebooks;

/**
 * Created by Desarrollo on 8/21/2017.
 */

public class Book {
    private String mTitle;
    private String mAuthor;
    private Double mRating;
    private String mCover;
    private String mURL;

    public Book(String title, String author, Double rating, String cover, String url) {
        mTitle = title;
        mAuthor = author;
        mRating = rating;
        mCover = cover;
        mURL = url;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public Double getRating() {
        return mRating;
    }

    public String getCover() {
        return mCover;
    }

    public String getURL() {
        return mURL;
    }
}
