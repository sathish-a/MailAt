package com.kewldevs.sathish.mail;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

public class Help extends AppCompatActivity implements View.OnClickListener {

    String msg;
    Button rate, byIn, byExt;
    EditText feed;
    final static String EMAIL = "sathish97rockz@gmail.com";
    final static String subject = "Feedback";
    DataBase dataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        Toolbar toolbar = (Toolbar) findViewById(R.id.include);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        dataBase = new DataBase(this);

        rate = (Button) findViewById(R.id.rate);
        byIn = (Button) findViewById(R.id.byIn);
        byExt = (Button) findViewById(R.id.byExt);
        feed = (EditText) findViewById(R.id.feed);

        rate.setOnClickListener(this);
        byExt.setOnClickListener(this);
        byIn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rate:
                rateUs();
                break;
            case R.id.byExt:
                msg=feed.getText().toString().trim();
                if(!msg.contentEquals(""))
                {
                    sendEmailByExternal(msg);
                }
                break;
            case R.id.byIn:
                msg=feed.getText().toString().trim();
                if(!msg.contentEquals(""))
                {


                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            List<String> cred = dataBase.getUserCred(1);
                            sendEmailByInternal(msg,cred);

                        }
                    }).start();
                }
                break;
        }
    }

    private void rateUs() {
        Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName())));
        }
    }

    private void sendEmailByInternal(String msg,List<String> cred) {
        Mail m = new Mail(cred.get(0),cred.get(1));
        String[] to = new String[1];
        to[0]=EMAIL;
        m.setTo(to);
        m.setFrom(cred.get(0));
        m.setSubject(subject);
        m.setBody(msg);
        try {

            if (m.send(this)) {
                    Util.DisplayToast(this,"Mail sent, Thanks for your feedback");

            } else {
                Util.DisplayToast(this,"Mail not sent");

            }
        }
        catch(Exception e) {
            //Toast.makeText(MailApp.this, "There was a problem sending the email.", Toast.LENGTH_LONG).show();

        }
    }

    private void sendEmailByExternal(String msg) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + EMAIL));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, msg);
        startActivityForResult(Intent.createChooser(emailIntent, "Send mail using"),999);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==999 && resultCode == RESULT_OK){
            Toast.makeText(this,"Thanks for your feedback",Toast.LENGTH_SHORT).show();
        }
    }
}
