package com.kewldevs.sathish.mail;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created by sathish on 4/27/16.
 */
public class AlarmReceiver extends BroadcastReceiver {

    Context mcontext;
    ScheduleEvent event;
    DataBase db;
    Util util;
    int key;
    List<String> cred;
    @Override
    public void onReceive(final Context context, Intent intent) {

        mcontext = context;
        db = new DataBase(mcontext);
        util = new Util(mcontext);
        key = intent.getIntExtra("EventID",-1);


        if(key != -1) {
            if(util.isConnectingToInternet()) {
                event = db.getSchedule(key);
                cred = db.getUserCred(event.get_from());
                sendMail();
            }else{
                util.Notify("No network connection","Email was not sent, Reschedule it");
            }
        }else {
            Toast.makeText(mcontext,"Some error occured!",Toast.LENGTH_SHORT).show();
        }

    }

    private void sendMail() {

        Mail m = new Mail(cred.get(0),cred.get(1));
        m.setTo(event.get_toArr());
        m.setFrom(cred.get(0));
        m.setSubject(event.get_sub());
        m.setBody(event.get_body());

        try {

            if(!event.get_attach().contentEquals("")){
                m.addAttachment(event.get_attach());
            }
            if(m.send(mcontext)) {
                db.setKeyExpire(key);
                util.Notify(event.get_sub(),"Email was sent successfully");
            } else {
                util.Notify(event.get_sub(),"Failed to email,Check your credentials!");
            }
        }catch (FileNotFoundException f){
            util.Notify(event.get_sub(),"Failed to email,Attachment not found!");
        }
        catch(Exception e) {
            util.Notify(event.get_sub(),"There was a problem sending the email. Try again");
        }
    }
}
