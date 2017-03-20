package com.kewldevs.sathish.mail;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EditEvent extends AppCompatActivity {
    String TAG = "EDIT EVENT";
    EditText etTo;
    EditText etSub;
    EditText etComp;
    TextView tvDate;
    TextView tvTime;
    TextView tvSize;
    ScheduleEvent scheduleEvent,event;
    private int mYear,mMonth,mDay,mHr,mMin;
    String Date,Time;
    Menu myMenu;
    DataBase db;
    Integer _from;
    String _to;
    String _sub;
    String _comp;
    String[] _toArr;
    int[] cal;
    int _reqcode;
    AlarmManager alarmManager;
    Toolbar toolbar;
    ActionBar actionBar;
    int REQ_CODE = 2;
    String rawAttach;
    ListView listAttach;
    List<String> attachList;
    ArrayAdapter attachAdapter;
    InterstitialAd mInterstitialAd;
    InterAd interAd;
    long totalSize;
    long maxSize = 25000000;
    boolean attachStatus;
    Util util;
    SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);
        toolbar = (Toolbar) findViewById(R.id.include);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        preferences =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        util = new Util(this);
        Intent i = getIntent();
        scheduleEvent = (ScheduleEvent) i.getSerializableExtra("Schedule");
        db = new DataBase(this);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        etTo = (EditText) findViewById(R.id.to);
        etSub = (EditText) findViewById(R.id.sub);
        attachStatus = true;
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
        cal = new int[5];
        rawAttach = "";
        listAttach = (ListView) findViewById(R.id.listAttach1);
        attachList = new ArrayList<String>();
        set();
        interAd = new InterAd(EditEvent.this,mInterstitialAd);
        new Thread(new Runnable() {
            @Override
            public void run() {
                interAd.initAd();
            }
        }).start();

    }


    private void set() {
        etTo.setText(scheduleEvent.get_to());
        etSub.setText(scheduleEvent.get_sub());
        etComp.setText(scheduleEvent.get_body());
        tvDate.setText(scheduleEvent.getDate());
        tvTime.setText(scheduleEvent.getTime());
        rawAttach=""+scheduleEvent.get_attach();
        attachList = Util.getAttachList(rawAttach);

        updateList();

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
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditEvent.this);
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
        else {tvSize.setVisibility(View.INVISIBLE); attachStatus = true;}
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
                {  s = "Some Files are not present!!"; attachStatus = false;}
            }else {s="Attachment size exceeds the limit!"; attachStatus = false; }
                tvSize.setText(s);
        }else tvSize.setVisibility(View.INVISIBLE);

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
                        tvTime.setText(Util.convertToTime(Time));
                    }
                }, mHr, mMin, false);

        tpd.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        myMenu = menu;
        getMenuInflater().inflate(R.menu.menu_edit_list,menu);
        if((System.currentTimeMillis()>scheduleEvent.get_trigg())|| (scheduleEvent.getExpire()==1)){
            myMenu.findItem(R.id.action_cancel).setEnabled(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        int key = scheduleEvent.getEventId() ;

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
        if(id == R.id.action_cancel){
            if(scheduleEvent.cancelSchedule(this,scheduleEvent.get_reqCode())){
                Toast.makeText(EditEvent.this,"Event Dismissed",Toast.LENGTH_SHORT).show();
                db.setKeyExpire(key);
                finish();
            }else {
                Toast.makeText(EditEvent.this,"Problem occurred!",Toast.LENGTH_SHORT).show();
            }

        }
        if(id == R.id.action_reschedule){

            if(scheduleEvent.cancelSchedule(this,scheduleEvent.get_reqCode())){

                reschedule();
            }


        }


        return super.onOptionsItemSelected(item);
    }

    private void requestReadPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(EditEvent.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(EditEvent.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 999);
        } else {
            Toast.makeText(EditEvent.this,"Grant permission for Mail@ to access your files from app settings.",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 999:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    chooseFile();
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }

    private boolean checkReadPermission() {
        int result = ContextCompat.checkSelfPermission(EditEvent.this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
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
            if(resultCode == RESULT_OK && data!=null){
                try {
                    String path = FileUtils.getPath(this, data.getData());
                    attachList.add(path);
                    updateList();
                }catch (Exception e){
                    Toast.makeText(this,"Use any other file explorer!",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void reschedule() {


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
        final long _trigg = calendar.getTimeInMillis();
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

        if(_to_&&_comp_&&_sub_&&attachStatus) {
            if (_trigg > System.currentTimeMillis()) {
                db.setKeyExpire(scheduleEvent.getEventId());
                db.deleteSchedule(scheduleEvent.getEventId());
                _toArr = _to.split(";");
                if (Util.isValidEmail(_toArr)) {

                    _reqcode = (int) System.currentTimeMillis();
                    _from = scheduleEvent.get_from();
                    rawAttach = Util.getAttachString(attachList);
                    event = new ScheduleEvent(this, _from, _sub, _comp, _trigg, _toArr, _reqcode, 0, rawAttach);
                    event.createSchedule(event);
                    interAd.showInterstitial();

                } else {
                    Toast.makeText(this, "Not a valid email id", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "To make sure set the date and time again!!!", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this,"Could not attach!",Toast.LENGTH_SHORT).show();
        }

    }





}
