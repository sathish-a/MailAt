package com.kewldevs.sathish.mail;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by sathish on 5/6/16.
 */
public class Util {
    Context mContext;
    SharedPreferences sharedPreferences;

    public Util(Context mContext) {
        this.mContext = mContext;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public Util() {

    }


    public void Notify(String title, String msg) {
        Bitmap icon = BitmapFactory.decodeResource(mContext.getResources(),
                R.mipmap.ic_launcher);
        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(mContext).setSmallIcon(R.drawable.ic_stat_at)
                .setContentTitle(title).setContentText(msg).setAutoCancel(true).setLargeIcon(icon);
        Uri notifcationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if (sharedPreferences.getBoolean("SOUND", true)) {
            mBuilder.setSound(notifcationSound);
        }
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent;
        if(sharedPreferences.getBoolean("iPresent", false)){
            resultIntent = new Intent(mContext, MailList.class);
            // The stack builder object will contain an artificial back stack for the
            // started Activity.
            // This ensures that navigating backward from the Activity leads out of
            // your application to the Home screen.
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
            // Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addParentStack(MailList.class);
            // Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);
        }


        NotificationManager mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        int mId = 1;
        mNotificationManager.notify(mId, mBuilder.build());

    }

    public boolean isConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }

    public void recreateSchedule(Recreate recreate) {
        Intent myIntent = new Intent(mContext, AlarmReceiver.class);
        myIntent.putExtra("EventID", recreate.getKey());
        PendingIntent pi = PendingIntent.getBroadcast(mContext, recreate.getReqCode(), myIntent, 0);
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, recreate.getMillis(), pi);

    }


    public static String getAttachString(List<String> list) {
        String raw = "";
        for (String a : list) {
            raw += a + ";";
        }
        return raw;
    }

    public static List<String> getAttachList(String attachString) {
        String[] strings = attachString.split(";");
        List<String> attachList = new ArrayList<String>();
        Collections.addAll(attachList, strings);
        if(attachList.toString().contentEquals("[]")) attachList.clear();
        return attachList;
    }


    public static boolean isValidEmail(String target) {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static boolean isValidEmail(String[] strings){
        for(String target : strings){
            if(!isValidEmail(target))
            {
                return false;
            }
        }
        return true;
    }

    public static String addORemoveGmail(String target){
        String result;
        if(target.endsWith("@gmail.com")){
            result = target;
        }else result = target+"@gmail.com";

        return result;
    }
  static  void DisplayToast(Context context,final String message){
        final Activity activity = (Activity) context;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
            }
        });
    }

   public static String convertToDate(long dateInMilliseconds) {
        return DateFormat.format("dd/MM/yyyy hh:mm:ss", dateInMilliseconds).toString();
    }


    public static String convertToTime(String time)
    {
        Date dateObj = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            dateObj = sdf.parse(time);

           // System.out.println(dateObj);
           // System.out.println(new SimpleDateFormat("K:mm").format(dateObj));
        } catch (final ParseException e) {
            e.printStackTrace();
        }
        return (String) DateFormat.format("HH:mm",dateObj);
    }

    public void displayAlert(String Title,int Message)
    {
        final boolean[] status = {true};
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.alert_disp_view,null);
        AlertDialog.Builder builder =  new AlertDialog.Builder(mContext);
        builder.setMessage(Message);
        builder.setTitle(Title);
        builder.setView(view);
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.alert_disp);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                status[0] = isChecked;
            }
        });

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(status[0]) {
                    store("MESSAGE", status[0]);
                }
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void store(String message, Boolean status) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(message, !status);
        editor.apply();
        editor.commit();
    }


}