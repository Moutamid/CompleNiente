package com.moutamid.calenderapp.models;

import java.util.Date;

public class CalendarDate {

    private Date date;
    private boolean isSelected;
    private boolean isToday;
    private MonthType monthType;

    public CalendarDate() {
    }

    public CalendarDate(Date date, boolean isSelected, boolean isToday, MonthType monthType) {
        this.date = date;
        this.isSelected = isSelected;
        this.isToday = isToday;
        this.monthType = monthType;
    }

    public Date getDate() {
        return date;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public boolean isToday() {
        return isToday;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void setToday(boolean today) {
        isToday = today;
    }

    public MonthType getMonthType() {
        return monthType;
    }

    public void setMonthType(MonthType monthType) {
        this.monthType = monthType;
    }
}
