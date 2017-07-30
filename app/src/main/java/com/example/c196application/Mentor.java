package com.example.c196application;

/**
 * Created by user on 7/9/2017.
 */

public class Mentor {
    String mentorName;
    String phoneNumber;
    String emailAdddress;
    int mentorID;

    Mentor(String name, String phone, String email)
    {
        mentorName=name;
        phoneNumber=phone;
        emailAdddress=email;

    }
    public String getMentorName(){
        return mentorName;
    }
    public String getPhoneNumber(){
        return phoneNumber;
    }
    public String getEmailAdddress(){
        return emailAdddress;
    }
    public int getMentorID()
    {
        return mentorID;
    }
}
