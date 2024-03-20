package com.example.NeostudyCalculator.models;

import java.util.Date;

public class Day {
    private Date date;
    private boolean workday;
    private String dayType;
    private String reason;

    public Day(Date date, boolean workday, String dayType, String reason) {
        this.date = date;
        this.workday = workday;
        this.dayType = dayType;
        this.reason = reason;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isWorkday() {
        return workday;
    }

    public void setWorkday(boolean workday) {
        this.workday = workday;
    }

    public String getDayType() {
        return dayType;
    }

    public void setDayType(String dayType) {
        this.dayType = dayType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

}
