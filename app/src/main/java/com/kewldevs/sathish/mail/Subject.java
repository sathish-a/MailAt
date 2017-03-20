package com.kewldevs.sathish.mail;

import android.text.format.DateFormat;

/**
 * Created by sathish on 4/30/16.
 */
public class Subject {
    int id;
    String subject;
    int expiry;
    String to;
    long trig;
    String Date;

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public long getTrig() {
        return trig;
    }

    public void setTrig(long trig) {
        this.trig = trig;
        setDate(convertDate(this.trig));
    }

    public Subject() {
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getExpiry() {
        return expiry;
    }

    public void setExpiry(int expiry) {
        this.expiry = expiry;
    }

    String convertDate(long dateInMilliseconds) {
        return DateFormat.format("dd/MM/yyyy HH:mm", dateInMilliseconds).toString();
    }
}
