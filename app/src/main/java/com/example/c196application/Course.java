package com.example.c196application;

import android.database.Cursor;

import java.util.Date;

/**
 * Created by user on 7/9/2017.
 */

public class Course {
    protected String title;
    protected int mentorID;
    protected String notes;
    protected String status;
    protected Date endDate;
    protected Date startDate;
    protected int courseID;
    protected int termID;
    protected int alert;

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

    protected String getCourseTitle() {
        return title;
    }

    protected Date getStartDate() {
        return startDate;
    }

    protected Date getEndDate() {
        return endDate;
    }

    protected int getTermID() {
        return termID;
    }

    protected String getStatus() {
        return status;
    }

    protected int getMentor() {
        return mentorID;
    }
    protected int getCourseID(){
        return courseID;
    }


    protected int getAlert() {
        return alert;
    }
}
