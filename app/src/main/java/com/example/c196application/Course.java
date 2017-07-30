package com.example.c196application;

import android.database.Cursor;

import java.util.Date;

/**
 * Created by user on 7/9/2017.
 */

public class Course {
    String title;
    int mentorID;
    String notes;
    String status;
    Date endDate;
    Date startDate;
    int courseID;
    int termID;
    int alert;

    Course(String titleStr,  String statusStr, Date start,Date end, int term,int alertStatus){
        title=titleStr;
        status = statusStr;
        startDate = start;
        endDate=end;
        termID = term;
        alert=alertStatus;


    }
    Course(String titleStr,  String statusStr, Date start,Date end, int term, int mentor, int alertStatus){
        title=titleStr;
        status = statusStr;
        startDate = start;
        endDate=end;
        termID = term;
        mentorID=mentor;
        alert=alertStatus;
    }
    private void setMentorID(int mentor){
        mentorID=mentor;
    }
    protected void setCourseID(int id){ this.courseID=id;}

    public String getCourseTitle() {
        return title;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public int getTermID() {
        return termID;
    }

    public String getStatus() {
        return status;
    }

    public int getMentor() {
        return mentorID;
    }
    public int getCourseID(){
        return courseID;
    }


    public int getAlert() {
        return alert;
    }
}
