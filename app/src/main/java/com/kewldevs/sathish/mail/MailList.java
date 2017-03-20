package com.kewldevs.sathish.mail;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MailList extends AppCompatActivity {

    List<Subject> subjectsList;
    DataBase db;
    String TAG = "List";
    ListView lvSchedule;
    List<String> sub;
    ListAdapter listAdapter;
    Context context;
    GetSub getSub;
    Toolbar toolbar;
    TextView tvDisp;
    FloatingActionButton fab;
    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_list);
        toolbar = (Toolbar) findViewById(R.id.include);
        setSupportActionBar(toolbar);
        tvDisp = (TextView)findViewById(R.id.tvDisp);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        if (tvDisp != null) {
            tvDisp.setText("No Schedules");
        }
        lvSchedule=(ListView)findViewById(R.id.listViewSch);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        db = new DataBase(this);
        sub = new ArrayList<String>();
        listAdapter = new ListAdapter(this,R.layout.list_view);
        getSub = new GetSub(this);
        updateList();
        new Thread(new Runnable() {
            @Override
            public void run() {
                setAd();
            }
        }).start();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddEvent();
            }
        });
        if(sp.getBoolean("MESSAGE",true)){
            Util util = new Util(this);
            util.displayAlert("IMPORTANT",R.string.alert_msg);
        }
    }

    private void setAd() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AdView mAdView = (AdView) findViewById(R.id.adView);

                AdRequest adRequest = new AdRequest.Builder().build();
                if (mAdView != null) {
                    mAdView.loadAd(adRequest);

                }

            }
        });

    }

    private void updateList() {
        subjectsList = getSub.doInBackground();

        if(!subjectsList.isEmpty()){
            if(tvDisp!=null)tvDisp.setVisibility(View.INVISIBLE);

            Collections.reverse(subjectsList);
            listAdapter.addToList(subjectsList);
            lvSchedule.setAdapter(listAdapter);
            registerForContextMenu(lvSchedule);
            lvSchedule.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    final int kid = subjectsList.get(position).getId();
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {

                           ScheduleEvent se = db.getSchedule(kid);
                            se.setEventId(kid);
//                        se.displayLogs(TAG);

                            Intent i = new Intent(getApplicationContext(),EditEvent.class);
                            i.putExtra("Schedule",se);
                            startActivity(i);
                        }
                    });
                    thread.start();

                }
            });

        }
        else {
            tvDisp.setVisibility(View.VISIBLE);

        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_mail_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_create) {
            AddEvent();
        }
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }

        if (id == R.id.action_support) {
            startActivity(new Intent(this, Help.class));
        }

        if (id == R.id.action_clear_all) {
            final int count = db.getCount();
            if (count > 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MailList.this);
                builder.setTitle("Are you sure want to delete all?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ProgressDialog progressDialog = new ProgressDialog(MailList.this);
                        showProgress(true, progressDialog);


                        List<Recreate> recreateList = db.getRecreate();
                        for (Recreate recreate : recreateList) {
                            ScheduleEvent event = new ScheduleEvent();
                            event.cancelSchedule(MailList.this, recreate.getKey());
                        }

                        listAdapter.removeItem(null);
                        listAdapter.notifyDataSetChanged();

                        if (count == db.clearScheduleTable()) {
                            showProgress(false, progressDialog);
                        }

                        updateList();
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
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(MailList.this);
                builder.setTitle("Ooops nothing to delete!!!");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }

        }
        return super.onOptionsItemSelected(item);
    }

    private void AddEvent() {
        Intent i = new Intent(this,CreateEvent.class);
        i.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
        startActivity(i);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateList();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if(v.getId() == R.id.listViewSch){
            MenuInflater iflater = getMenuInflater();
            iflater.inflate(R.menu.context_main,menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final int pos = info.position;
        final int KeyI = subjectsList.get(pos).getId();
        final ScheduleEvent se = new ScheduleEvent();
        int req_code = db.getReqCode(KeyI);

        switch (item.getItemId())
        {
            case R.id.menu_delete:


                AlertDialog.Builder builder = new AlertDialog.Builder(MailList.this);
                builder.setTitle("Delete");
                builder.setMessage(""+subjectsList.get(pos).getSubject()+" ?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        se.deleteSchedule(MailList.this,KeyI);
                        Toast.makeText(MailList.this,"Event Deleted",Toast.LENGTH_SHORT).show();
                        listAdapter.removeItem((Subject) listAdapter.getItem(pos));
                        listAdapter.notifyDataSetChanged();
                        if (db.getCount() == 0){
                            tvDisp.setVisibility(View.VISIBLE);
                        }
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
                break;
            case R.id.menu_cancel:

                if(subjectsList.get(pos).getExpiry() == 0){
                    se.cancelSchedule(MailList.this,req_code);
                    db.setKeyExpire(KeyI);
                    updateList();
                    Toast.makeText(MailList.this,"Event Canceled",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MailList.this,"Event is no longer active",Toast.LENGTH_SHORT).show();

                }

                break;

        }


        return super.onContextItemSelected(item);
    }

    void showProgress(boolean status,ProgressDialog progressDialog){
        if(status) {
            progressDialog.setTitle("Deleting");
            progressDialog.setMessage("Please wait");
            progressDialog.setCancelable(false);
             progressDialog.show();
        }else progressDialog.dismiss();

    }




}

