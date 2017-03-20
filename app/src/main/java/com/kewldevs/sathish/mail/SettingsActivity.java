package com.kewldevs.sathish.mail;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Random;

public class SettingsActivity extends AppCompatActivity  {

    Button edit;
    String email,paswd;
    Integer verificationNo;
    String TAG = "Settings";
    TextView tvEmail;
    Switch notiSwitch,alertSwitch;
    Util util;
    ProgressDialog pd;
    SharedPreferences sharedPreferences;
    DataBase dataBase;
    String usrId;
    ListView settingsList;
    String [] settings_menu;
    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.include);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        dataBase = new DataBase(this);
        usrId = dataBase.getUserID(1);
        notiSwitch = (Switch) findViewById(R.id.switch1);
        alertSwitch = (Switch) findViewById(R.id.switch2);
        settingsList = (ListView) findViewById(R.id.settings_list);
        util = new Util(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        edit = (Button)findViewById(R.id.edit_login);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoginWindow();
            }
        });
        tvEmail = (TextView)findViewById(R.id.tvEmail);
        if (tvEmail != null) {
            tvEmail.setText(usrId);
        }
        notiSwitch.setChecked(sharedPreferences.getBoolean("SOUND",true));
        notiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Store("SOUND",isChecked);
                if(isChecked)
                Toast.makeText(SettingsActivity.this,"Notification sound is enabled",Toast.LENGTH_SHORT).show();
                else  Toast.makeText(SettingsActivity.this,"Notification sound is disabled",Toast.LENGTH_SHORT).show();

            }
        });
        alertSwitch.setChecked(sharedPreferences.getBoolean("MESSAGE",true));
        alertSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Store("MESSAGE",isChecked);
            }
        });

        settings_menu = new String[]{"Version"};
        adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,settings_menu);
        settingsList.setAdapter(adapter);

        updateList();


    }

    private void updateList() {
        settingsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position)
                {
                    case 0:
                        startActivity(new Intent(SettingsActivity.this,version.class));
                        break;
                }
            }
        });
    }


    private void showLoginWindow() {
        LayoutInflater li = LayoutInflater.from(this);
        View loginView = li.inflate(R.layout.login_view, null);
        final AlertDialog.Builder loginDialog = new AlertDialog.Builder(SettingsActivity.this);
        loginDialog.setView(loginView);
        loginDialog.setTitle("Mail@");
        loginDialog.setCancelable(false);
        final EditText Etemail = (EditText) loginView.findViewById(R.id.email);
        final EditText Etpasswd = (EditText) loginView.findViewById(R.id.passwd);
        ToggleButton toggleButton = (ToggleButton) loginView.findViewById(R.id.viewPass);
        TextView textView = (TextView) loginView.findViewById(R.id.tvOne);
        String info = "Goto gmail settings \"<b>turn on</b>\" the access";
        textView.setText(Html.fromHtml(info));
        Button goTo = (Button) loginView.findViewById(R.id.goTo);
        goTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingsActivity.this,"Sign in to your gmail account",Toast.LENGTH_SHORT).show();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/settings/security/lesssecureapps"));
                startActivity(browserIntent);
            }
        });

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked){
                    Etpasswd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                else Etpasswd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
        });
        loginDialog.setPositiveButton("Send Verification code", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                email = Etemail.getText().toString().trim().toLowerCase();
                paswd = Etpasswd.getText().toString().trim();
                if (!email.contentEquals("") && !paswd.contentEquals(""))
                {      email = Util.addORemoveGmail(email);
                    if (Util.isValidEmail(email))
                    {
                        if (util.isConnectingToInternet())
                        {
                            Random rand = new Random();
                            verificationNo = rand.nextInt(999999);
                            dialog.dismiss();
                            pd = ProgressDialog.show(SettingsActivity.this,"Loading","Please wait");
                            pd.setCancelable(false);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    if (sendMail(verificationNo)) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                gotoAuthenticate();
                                            }
                                        });

                                    } else {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                showLoginWindow();
                                            }
                                        });

                                    }
                                }
                            }).start();
                            dialog.dismiss();



                        } else {
                            DisplayToast("No Network connection!");
                            dialog.dismiss();
                            showLoginWindow();
                        }
                    }else {
                        DisplayToast("Enter valid email id");
                        dialog.dismiss();
                        showLoginWindow();
                    }
                } else {
                    DisplayToast("Enter the fields!");
                    dialog.dismiss();
                    showLoginWindow();
                }
            }
        });

        loginDialog.setNegativeButton("Quit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(pd!=null){
                    pd.dismiss();
                }
            }
        });

        AlertDialog login = loginDialog.create();
        login.show();


    }



    private void gotoAuthenticate() {
        if(pd!=null){
            pd.dismiss();
        }
        LayoutInflater li = LayoutInflater.from(this);
        View authView = li.inflate(R.layout.authenticate, null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setView(authView);
        builder.setTitle("Verification");
        String msg = "Enter the verification code which is sent to "+"<b>"+email+"</b>";
        TextView tv = (TextView) authView.findViewById(R.id.tvAuth);
        tv.setText(Html.fromHtml(msg));
        final EditText Etverify = (EditText) authView.findViewById(R.id.etVerify);
        builder.setCancelable(false);
        builder.setPositiveButton("Verify", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String Svno = Etverify.getText().toString();
                if(!Svno.contentEquals("")){
                    Integer vno = Integer.parseInt(Svno);
                    if(vno.equals(verificationNo)){
                        Toast.makeText(SettingsActivity.this,"Verfifcation success!!",Toast.LENGTH_SHORT).show();
                        store(email,paswd);
                        dialog.dismiss();
                        pd.dismiss();


                    }else {
                        Toast.makeText(SettingsActivity.this,"Verification failed!!",Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        gotoAuthenticate();
                    }
                }else {
                    Toast.makeText(SettingsActivity.this,"Enter verification code",Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    gotoAuthenticate();
                }
            }
        });
        builder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showLoginWindow();
            }
        });

        AlertDialog ad = builder.create();
        ad.show();
    }

    private boolean sendMail(Integer verificationNo) {
        boolean status = true;
        Mail m = new Mail(email,paswd);
        String[] to = new String[1];
        to[0]=email;
        m.setTo(to);
        m.setFrom(email);
        m.setSubject("Self verification");
        m.setBody("Your verification code is "+verificationNo);
        try {

            if (m.send(this)) {
                DisplayToast("Verification mail was sent");
            } else {
                status = false;
                DisplayToast("Verification mail was not sent");
            }
        }
        catch(Exception e) {
            //Toast.makeText(MailApp.this, "There was a problem sending the email.", Toast.LENGTH_LONG).show();
            status = false;

        }

        return status;
    }
    void store(String email,String paswd){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("iPresent", true);
        editor.apply();
        editor.commit();
        DataBase db = new DataBase(this);
        db.updateUser(email,paswd,1);
        tvEmail.setText(email);
    }

    void DisplayToast(final String message){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    void Store(String KEY,boolean value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY,value);
        editor.apply();
        editor.commit();
    }

}
