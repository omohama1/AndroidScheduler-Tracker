package com.example.c196application;

import java.util.Date;

/**
 * Created by user on 7/9/2017.
 */

public class Term {
    int termID;
    String termTitle;
    Date startDate;
    Date endDate;
    Term(String title, Date start, Date end) {
        termTitle=title;
        startDate=start;
        endDate=end;
    }

    public int getTermID(){
        return termID;
    }
    public Date getStartDate(){
        return startDate;

    }
    public Date getEndDate(){
        return endDate;
    }
    public String getTermTitle(){
        return termTitle;
    }
}
