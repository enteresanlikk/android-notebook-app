package com.enteresanlikk.notdefteri;

import java.sql.Time;
import java.util.Date;

public class Note {
    public String title;
    public String content;
    public Integer reminder;
    public String reminder_date;
    public String reminder_time;
    public Integer status;
    public String date;

    public Note(String title, String content, Integer reminder, String reminder_date, String reminder_time, Integer status, String date) {
        this.title = title;
        this.content = content;
        this.reminder = reminder;
        this.reminder_date = reminder_date;
        this.reminder_time = reminder_time;
        this.status = status;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getReminder() {
        return reminder;
    }

    public void setReminder(Integer reminder) {
        this.reminder = reminder;
    }

    public String getReminder_date() {
        return reminder_date;
    }

    public void setReminder_date(String reminder_date) {
        this.reminder_date = reminder_date;
    }

    public String getReminder_time() {
        return reminder_time;
    }

    public void setReminder_time(String reminder_time) {
        this.reminder_time = reminder_time;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
