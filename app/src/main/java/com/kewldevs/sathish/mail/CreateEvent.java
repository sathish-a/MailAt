package com.kewldevs.sathish.mail;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CreateEvent extends AppCompatActivity {


    EditText etTo;
    EditText etSub;
    EditText etComp;
    int _from;
    String _to;
    String _sub;
    String _comp;
    String[] _toArr;
    TextView tvDate;
    TextView tvTime;
    TextView tvSize;
    ImageButton btDate,btTime;
    private int mYear,mMonth,mDay,mHr,mMin;
    String Date,Time;
    int[] cal;
    ScheduleEvent event;
    int _reqcode;
    DataBase db;
    Menu myMenu;
    Toolbar toolbar;
    ActionBar actionBar;
    String rawAttach;
    int REQ_CODE = 2;
    ListView listAttach;
    List<String> attachList;
    ArrayAdapter attachAdapter;
    InterstitialAd interstitialAd;
    InterAd interAd;
    long totalSize;
    boolean attachStatus;
    long maxSize = 25000000;
    SharedPreferences preferences;
    Util util;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        toolbar = (Toolbar) findViewById(R.id.include);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        preferences =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        util = new Util(this);
        etTo = (EditText) findViewById(R.id.to);
        etSub = (EditText) findViewById(R.id.sub);
        if (etSub != null) {
            etSub.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        }
        etComp = (EditText) findViewById(R.id.compose);

        tvDate = (TextView) findViewById(R.id.tvDate);
        tvTime = (TextView) findViewById(R.id.tvTime);
        tvSize = (TextView) findViewById(R.id.tvSize);
        if (tvSize != null) {
            tvSize.setVisibility(View.INVISIBLE);
        }
        _from = 1;
        cal = new int[5];
        rawAttach = "";
        listAttach = (ListView)findViewById(R.id.listAttach);
        attachList =  new ArrayList<String>();
        interAd = new InterAd(CreateEvent.this,interstitialAd);
        attachStatus = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                interAd.initAd();
            }
        }).start();    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_email_list, menu);
        myMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_attach){
            if(Build.VERSION.SDK_INT >= 23)
            {
                if(checkReadPermission()){
                     chooseFile();
                }else {
                    requestReadPermission();
                }
            }else {
                chooseFile();
            }

        }
        if(id == R.id.action_set){
            getFields();

        }



        return super.onOptionsItemSelected(item);

    }

    private void requestReadPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(CreateEvent.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(CreateEvent.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 999);
        } else {
            Toast.makeText(CreateEvent.this,"Grant permission for Mail@ to access your files from app settings.",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 999:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                    chooseFile();
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }

    private boolean checkReadPermission() {
        int result = ContextCompat.checkSelfPermission(CreateEvent.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void chooseFile() {
        try{
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(intent,REQ_CODE);
        }
        catch(ActivityNotFoundException exp){
            Toast.makeText(getBaseContext(), "No File (Manager / Explorer)etc Found In Your Device",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQ_CODE){
            if (resultCode != Activity.RESULT_CANCELED) {
                if (resultCode == RESULT_OK && data != null) {
                   // String path = Util.getPathFromURI(this, data.getData());
                    try {
                        Uri raw = data.getData();
                        String path = FileUtils.getPath(this, raw);
                        attachList.add(path);
                        updateList();
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(this,"Use any other file explorer!",Toast.LENGTH_SHORT).show();
                    }

                }
            }
        }
    }

    private void updateList() {
        if(!attachList.isEmpty()){
            updateTvSize();
            attachAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,attachList);
            listAttach.setAdapter(attachAdapter);
            listAttach.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    final String item = attachList.get(position);
                    AlertDialog.Builder builder = new AlertDialog.Builder(CreateEvent.this);
                    builder.setTitle("Remove attachment");
                    builder.setMessage(item+" ?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            attachList.remove(item);
                            updateTvSize();
                            attachAdapter.remove(item);
                            attachAdapter.notifyDataSetChanged();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
            });
        }
        else tvSize.setVisibility(View.INVISIBLE);
    }

    void updateTvSize(){
        String s = "Attachments size:";
        attachStatus = true;
        if(!attachList.isEmpty()){
            tvSize.setVisibility(View.VISIBLE);
            totalSize = FileUtils.getFileListSize(attachList);
            if(totalSize<maxSize) {
                if (attachList.size() == FileUtils.isFileListPresent(attachList)) {
                    s += FileUtils.getReadableFileSize((int) totalSize);
                    attachStatus = true;
                } else
                    s = "Some Files are not present!!";
            }else {s="Attachment size exceeds the limit!"; attachStatus = false; }
            tvSize.setText(s);
        }else tvSize.setVisibility(View.INVISIBLE);
    }


    private void getFields() {

        _to = etTo.getText().toString().trim();
        _sub = etSub.getText().toString().trim();
        _comp = etComp.getText().toString().trim();


        cal[0]=mDay;
        cal[1]=mMonth;
        cal[2]=mYear;
        cal[3]=mHr;
        cal[4]=mMin;


        Calendar calendar = Calendar.getInstance();
        calendar.set(cal[2], cal[1], cal[0], cal[3], cal[4],0);
        long _trigg = calendar.getTimeInMillis();
        boolean _to_ , _sub_ , _comp_ ;
        if(_to.contentEquals("")){
         etTo.setError("This field cannot be blank");
            _to_ = false;
        } else _to_ = true;
        if(_comp.contentEquals("")){
            etComp.setError("This field cannot be blank");
            _comp_ = false;
        }else _comp_ = true;
        if(_sub.contentEquals("")){
            etSub.setError("This field cannot be blank");
            _sub_ = false;
        }else _sub_ = true;

        if(_to_&&_comp_&&_sub_) {
            if ((_trigg > System.currentTimeMillis())) {
                _toArr = _to.split(";");
                if (Util.isValidEmail(_toArr)) {
                    if(attachStatus) {

                            _reqcode = (int) System.currentTimeMillis();
                            rawAttach = Util.getAttachString(attachList);
                            event = new ScheduleEvent(this, _from, _sub, _comp, _trigg, _toArr, _reqcode, 0, rawAttach);
                            event.createSchedule(event);
                            interAd.showInterstitial();

                    }else {
                        Toast.makeText(this,"Could not attach!",Toast.LENGTH_SHORT).show();
                    }

                } else {
                    etTo.setError("Not a valid email");
                }

            } else {
                Toast.makeText(this, "Check the date and time", Toast.LENGTH_SHORT).show();
            }
        }
    }





    public void onDatePick(View v){
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                          mYear = year;
                          mMonth = monthOfYear;
                          mDay = dayOfMonth;
                        Date =""+ mDay + "/" + (mMonth+1) + "/" + mYear;
                        tvDate.setText(Date);

                    }
                }, mYear, mMonth, mDay);
        dpd.show();
    }

    public void onTimePick(View v){
        TimePickerDialog tpd = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        mHr = hourOfDay;
                        mMin = minute;
                        Time = "" + mHr + ":" + mMin;
                        tvTime.setText(""+Util.convertToTime(Time));
                    }
                }, mHr, mMin, false);

        tpd.show();
    }


}
