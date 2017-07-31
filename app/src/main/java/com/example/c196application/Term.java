package com.example.c196application;

import java.util.Date;

/**
 * Created by user on 7/9/2017.
 */

public class Term {
    protected int termID;
    protected String termTitle;
    protected Date startDate;
    protected Date endDate;
    Term(String title, Date start, Date end) {
        termTitle=title;
        startDate=start;
        endDate=end;
    }

    protected int getTermID(){
        return termID;
    }
    protected Date getStartDate(){
        return startDate;

    }
    protected  Date getEndDate(){
        return endDate;
    }
    protected String getTermTitle(){
        return termTitle;
    }
}
