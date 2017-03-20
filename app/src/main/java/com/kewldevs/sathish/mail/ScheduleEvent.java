package com.kewldevs.sathish.mail;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.widget.Toast;

import java.io.Serializable;
import java.util.Date;

public class ScheduleEvent  implements Serializable {
    int eventId;
    String _sub,_body;
    long _trigg;
    String[] _toArr;
    int _reqCode,expire; //expire: 1,expired ; 0,not expired
    String _TAG ="SCHEDULE";
    String _to="";
    String _attach;
    DataBase db;
    Context _Context;
    Integer _from;
    public ScheduleEvent() {
    }

    public ScheduleEvent(Context context,Integer _from, String _sub, String _body, long _trigg, String[] _toArr, int _reqCode,int _expire,String _attach) {
        this._from = _from;
        this._sub = _sub;
        this._body = _body;
        this._trigg = _trigg;
        this._toArr = _toArr;
        this._reqCode = _reqCode;
        this.expire = _expire;
        this._attach = _attach;
        _Context = context;
        db = new DataBase(_Context);

    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public Integer get_from() {
        return _from;
    }

    public void set_from(Integer _from) {
        this._from = _from;
    }

    public String get_sub() {
        return _sub;
    }

    public void set_sub(String _sub) {
        this._sub = _sub;
    }

    public String get_body() {
        return _body;
    }

    public void set_body(String _body) {
        this._body = _body;
    }

    public long get_trigg() {
        return _trigg;
    }

    public void set_trigg(long _trigg) {
        this._trigg = _trigg;
    }

    public String[] get_toArr() {
        return _toArr;
    }

    public void set_toArr(String[] _toArr) {
        this._toArr = _toArr;
    }

    public int get_reqCode() {
        return _reqCode;
    }

    public void set_reqCode(int _reqCode) {
        this._reqCode = _reqCode;
    }

    public int getExpire() {
        return expire;
    }

    public void setExpire(int expire) {
        this.expire = expire;
    }

    public String get_attach() {
        return _attach;
    }

    public void set_attach(String _attach) {
        this._attach = _attach;
    }

    public String[] get_ArrFromString(String to){
      String[] toArr;
        toArr = to.split(";");
        return toArr;
    }



    public String getDate(){
        return DateFormat.format("dd/MM/yyy",_trigg).toString();
    }

    public String get_to(){
        for (String a_toArr : _toArr) {
            _to+=a_toArr;
            _to+=";";
        }
        return _to;
    }



    public String getTime(){
        Date date = new Date(_trigg);
        return  (String) DateFormat.format("HH:mm",date);
    }

    public boolean cancelSchedule(Context context,int reqCode){
        PendingIntent pi = PendingIntent.getBroadcast(context,reqCode,new Intent(context,AlarmReceiver.class),PendingIntent.FLAG_UPDATE_CURRENT);
        pi.cancel();
        return true;
    }

    public void createSchedule(ScheduleEvent event){
        db.addNewSchedule(event);

        int key = db.getId(event.get_reqCode(),event.get_trigg());

        Intent myIntent = new Intent(_Context, AlarmReceiver.class);
        myIntent.putExtra("EventID",key);
        PendingIntent pi = PendingIntent.getBroadcast(_Context, event.get_reqCode(), myIntent, 0);
        AlarmManager alarmManager = (AlarmManager)_Context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, event.get_trigg(), pi);
        Toast.makeText(_Context,"Scheduled",Toast.LENGTH_SHORT).show();
    }

    public void deleteSchedule(Context context,int KeyId){
        this._Context = context;
        DataBase db = new DataBase(_Context);
        if(cancelSchedule(_Context,db.getReqCode(KeyId))){
            db.deleteSchedule(KeyId);
        }
    }

}
