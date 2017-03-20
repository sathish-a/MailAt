package com.kewldevs.sathish.mail;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;

/**
 * Created by sathish on 5/8/16.
 */
public class BootReceiver extends BroadcastReceiver {
    Util util;
    DataBase db;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            // Set the alarm here.
            util = new Util(context);
            db = new DataBase(context);
            List<Recreate> recreateList = db.getRecreate();
            for (Recreate recreate : recreateList){
                util.recreateSchedule(recreate);
            }

        }
    }
}
