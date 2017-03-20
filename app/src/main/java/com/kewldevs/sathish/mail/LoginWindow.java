package com.kewldevs.sathish.mail;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Random;



public class LoginWindow extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences sharedPreferences;
    String TAG = "LoginWindow";
    Toolbar toolbar;
    String email, paswd;
    Integer verificationNo;
    Util util;
    ProgressDialog pd;
    Button btVerify,btSettings;
    EditText etEmail,etPass;
    TextView tvMsg;
    CheckBox checkBox;
    boolean alreadyPresent;
    ToggleButton toggleButton;
    ImageButton infoBut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        alreadyPresent = sharedPreferences.getBoolean("iPresent", false);
        util = new Util(this);
        if (alreadyPresent) {
            gotoMailList();

        } else {
            setContentView(R.layout.activity_login_window);

            toolbar = (Toolbar) findViewById(R.id.include);
            setSupportActionBar(toolbar);
            btVerify = (Button) findViewById(R.id.btVerify);
            btSettings = (Button) findViewById(R.id.btGmail);
            etEmail = (EditText) findViewById(R.id.etEmail);
            etPass = (EditText) findViewById(R.id.etPass);
            tvMsg = (TextView) findViewById(R.id.tvMess);
            checkBox = (CheckBox) findViewById(R.id.checkOn);
            infoBut =(ImageButton) findViewById(R.id.infoBut);
            toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
            String info = "Goto gmail settings \"<b>turn on</b>\" the access";
            tvMsg.setText(Html.fromHtml(info));
            btVerify.setOnClickListener(this);
            btSettings.setOnClickListener(this);
            btVerify.setEnabled(false);
            etEmail.setEnabled(false);
            etPass.setEnabled(false);
            toggleButton.setEnabled(false);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        btVerify.setEnabled(true);
                        etEmail.setEnabled(true);
                        etPass.setEnabled(true);
                        toggleButton.setEnabled(true);
                    }
                    else {
                        btVerify.setEnabled(false);
                        etEmail.setEnabled(false);
                        etPass.setEnabled(false);
                        toggleButton.setEnabled(false);
                    }
                }
            });

            toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(!isChecked){
                        etPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    }
                    else etPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            });

            infoBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showInfo();
                }
            });

        }

    }

    private void showInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginWindow.this);
        builder.setTitle("Why should I turn on?");
        builder.setMessage(R.string.Info_msg);
        builder.setCancelable(true);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }

        });
        AlertDialog ad = builder.create();
        ad.show();
    }

    private void gotoSettings()
    {
        Toast.makeText(LoginWindow.this, "Sign in to your gmail account", Toast.LENGTH_SHORT).show();
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/settings/security/lesssecureapps"));
        startActivity(browserIntent);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btGmail){ gotoSettings();}
        if(v.getId() == R.id.btVerify) {
            email = etEmail.getText().toString().trim().toLowerCase();
            paswd = etPass.getText().toString();
            boolean _email, _pass;
            if (email.contentEquals("")) {
                etEmail.setError("This field cannot be blank");
                _email = false;
            } else _email = true;

            if (paswd.contentEquals("")) {
                etPass.setError("This field cannot be blank");
                _pass = false;
            } else _pass = true;

            if (_email && _pass) {
                email = Util.addORemoveGmail(email);
                if (Util.isValidEmail(email)) {
                    sendVerificationCode();
                }else etEmail.setError("Not a valid email id");
            }
        }
    }

    private void sendVerificationCode() {
        if (util.isConnectingToInternet()) {
            Random rand = new Random();
            verificationNo = rand.nextInt(999999);
            pd = ProgressDialog.show(LoginWindow.this, "Loading", "Please wait");
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

                    }else {
                        if (pd != null) {
                            pd.dismiss();
                        }
                    }

                }
            }).start();


        } else {
           Toast.makeText(this,"No network connection",Toast.LENGTH_SHORT).show();

        }

    }


    private void gotoAuthenticate() {
        if (pd != null) {
            pd.dismiss();
        }
        LayoutInflater li = LayoutInflater.from(this);
        View authView = li.inflate(R.layout.authenticate, null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(LoginWindow.this);
        builder.setView(authView);
        builder.setTitle("Verification");
        String msg = "Enter the verification code which is sent to " + "<b>" + email + "</b>";
        TextView tv = (TextView) authView.findViewById(R.id.tvAuth);
        tv.setText(Html.fromHtml(msg));
        final EditText Etverify = (EditText) authView.findViewById(R.id.etVerify);

        builder.setCancelable(false);
        builder.setPositiveButton("Verify", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String Svno = Etverify.getText().toString();
                if (!Svno.contentEquals("")) {
                    Integer vno = Integer.parseInt(Svno);
                    if (vno.equals(verificationNo)) {
                        dialog.dismiss();
                        Toast.makeText(LoginWindow.this, "Verfifcation success!!", Toast.LENGTH_SHORT).show();
                        store(email, paswd);
                        gotoMailList();
                    } else {
                        Toast.makeText(LoginWindow.this, "Verification failed!!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        gotoAuthenticate();
                    }
                } else {
                    Toast.makeText(LoginWindow.this, "Enter verification code", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    gotoAuthenticate();
                }
            }
        });
        builder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog ad = builder.create();
        ad.show();
    }

    void gotoMailList() {
        Intent i = new Intent(this, MailList.class);
        startActivity(i);
        finish();
    }

    void store(String email, String paswd) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("iPresent", true);
        editor.apply();
        editor.commit();
        DataBase db = new DataBase(this);
        db.addUser(email,paswd);
        db.close();
    }

    private boolean sendMail(Integer verificationNo) {
        boolean status = true;
        Mail m = new Mail(email, paswd);
        String[] to = new String[1];
        to[0] = email;
        m.setTo(to);
        m.setFrom(email);
        m.setSubject("Self verification");
        m.setBody("Your verification code is " + verificationNo);
        try {

            if (m.send(this)) {
                DisplayToast("Verification mail was sent");
            } else {
                status = false;
                DisplayToast("Verification mail was not sent");
            }
        } catch (Exception e) {
            //Toast.makeText(MailApp.this, "There was a problem sending the email.", Toast.LENGTH_LONG).show();
            status = false;

        }

        return status;
    }

    void DisplayToast(final String message) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginWindow.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }
}
