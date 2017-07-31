package com.example.c196application;

import java.util.Date;

/**
 * Created by Omar Mohamad on 7/9/2017.
 */

/* Creating the assessment class - used for inserting
an assessment into the SQLite Database

 */
public class Assessment {

    protected String assessmentTitle;
    protected String assessmentType;
    protected Date assessmentDate;
    protected int assessmentCourseID;
    protected int assessmentID;
    protected int alert;

    Assessment(String title, String type, Date date, int course,int alertStatus){
        assessmentTitle = title;
        assessmentType=type;
        assessmentDate=date;
        assessmentCourseID=course;
        alert=alertStatus;
    }

    public void setAssessmentID(int id){
        this.assessmentID=id;
    }
    public Date getAssessmentDate() {
        return assessmentDate;
    }
    public String getAssessmentType(){
        return assessmentType;
    }
    public int getAssessmentCourseID()
    {
        return assessmentCourseID;
    }
    public String getAssessmentTitle(){
        return assessmentTitle;
    }

    public int getAlert() {
        return alert;
    }

    public int getAssessmentID() {
        return assessmentID;
    }
}
