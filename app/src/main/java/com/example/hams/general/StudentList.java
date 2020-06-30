package com.example.hams.general;


public class StudentList {
    private int mImageRes;
    private String string;

    public StudentList(int mImageRes, String string) {
        this.mImageRes = mImageRes;
        this.string = string;
    }

    public int getmImageRes() {
        return mImageRes;
    }

    public void setmImageRes(int mImageRes) {
        this.mImageRes = mImageRes;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public void setText(String text) {
        string = text;
    }
}
