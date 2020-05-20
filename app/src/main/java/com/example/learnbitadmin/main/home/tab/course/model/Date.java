package com.example.learnbitadmin.main.home.tab.course.model;

public class Date {

    private String startDate;
    private String endDate;

    public Date(String startDate, String endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Date() {
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
