package com.kewldevs.sathish.mail;

import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class version extends AppCompatActivity {

    int count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version);
        count=0;
        TextView textView = (TextView)findViewById(R.id.tvVersion);
        try {
            textView.setText("Version "+getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (textView != null) {
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    count++;
                    if(count==5){
                        Toast.makeText(version.this,"Love you Gollusu!!! <3 ",Toast.LENGTH_SHORT).show();
                        count=0;
                    }
                }
            });
        }
    }
}
