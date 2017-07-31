package com.example.c196application;

/**
 * Created by user on 7/9/2017.
 */

public class Mentor {
   protected String mentorName;
    protected String phoneNumber;
    protected String emailAdddress;
    protected int mentorID;

    Mentor(String name, String phone, String email)
    {
        mentorName=name;
        phoneNumber=phone;
        emailAdddress=email;

    }
    protected String getMentorName(){
        return mentorName;
    }
    protected String getPhoneNumber(){
        return phoneNumber;
    }
    protected String getEmailAdddress(){
        return emailAdddress;
    }
    protected int getMentorID()
    {
        return mentorID;
    }
}
