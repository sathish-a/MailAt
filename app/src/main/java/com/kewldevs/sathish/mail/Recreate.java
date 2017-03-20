package com.kewldevs.sathish.mail;

/**
 * Created by sathish on 5/8/16.
 */
public class Recreate {
    Integer key;
    Integer reqCode;
    long millis;

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public Integer getReqCode() {
        return reqCode;
    }

    public void setReqCode(Integer reqCode) {
        this.reqCode = reqCode;
    }

    public long getMillis() {
        return millis;
    }

    public void setMillis(long millis) {
        this.millis = millis;
    }

    public Recreate(Integer key, Integer reqCode, long millis) {
        this.key = key;
        this.reqCode = reqCode;
        this.millis = millis;
    }
}
