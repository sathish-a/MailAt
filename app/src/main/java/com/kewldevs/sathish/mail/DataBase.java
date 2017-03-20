package com.kewldevs.sathish.mail;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sathish on 4/30/16.
 */
public class DataBase extends SQLiteOpenHelper {
    SharedPreferences sp;
    private static final int DATABASE_VERSION =1;


    private static final String DATABASE_NAME = "EventScheduler";

    private static final String TABLE_NAME = "ScheduleTable";
    /**/private static final String USR_TABLE_NAME = "UsersTable";

    //User Table
    /**/private static final String USR_KEY_ID = "_id";
   /**/ private static final String USR_KEY_EMAIL = "_email";
   /**/ private static final String USR_KEY_PASS = "_pass";


    //Schedule Table
    private static final String KEY_ID = "_id";
    private static final String KEY_RCODE = "_reqcode";
    private static final String KEY_FROM = "_from";
    private static final String KEY_TO = "_to";
    private static final String KEY_SUB = "_sub";
    private static final String KEY_BODY = "_body";
    private static final String KEY_TRIG = "_trigg";
    private static final String KEY_EXPIRE = "_expire";
    private static final String KEY_ATTACH = "_attach";

    final String TAG = "DataBase";


    public DataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /**/  String query1 = "CREATE TABLE "+USR_TABLE_NAME+"("+USR_KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+USR_KEY_EMAIL +" TEXT NOT NULL,"+USR_KEY_PASS+" TEXT NOT NULL"+")";
        /**/  db.execSQL(query1);

        /*KEY_FROM ==== TEXT*/
        String query = "CREATE TABLE "+TABLE_NAME+"("+KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+KEY_RCODE+" INTEGER NOT NULL,"
                        +KEY_FROM +" INTEGER NOT NULL,"+KEY_TO+" TEXT NOT NULL,"+KEY_SUB+" TEXT NOT NULL,"
                        +KEY_BODY+" TEXT NOT NULL,"+KEY_TRIG+" INTEGER NOT NULL,"+KEY_EXPIRE+" INTEGER NOT NULL,"
                        +KEY_ATTACH+" TEXT NOT NULL"+")";
        db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /**/String query1 = "DROP TABLE IF EXISTS "+USR_TABLE_NAME;
        String query = "DROP TABLE IF EXISTS "+TABLE_NAME;
        /**/db.execSQL(query1);
        db.execSQL(query);
        onCreate(db);

    }

    public void addUser(String email,String password)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(USR_KEY_EMAIL,email);
        cv.put(USR_KEY_PASS,password);
        db.insert(USR_TABLE_NAME,null,cv);
        db.close();
    }

    public String getUserID(int id)
    {
        SQLiteDatabase database = this.getReadableDatabase();
        String name = "";
        String query = "SELECT "+USR_KEY_EMAIL+" FROM "+USR_TABLE_NAME+" WHERE "+USR_KEY_ID+"="+""+id;
        Cursor cursor = database.rawQuery(query,null);
        if(cursor.moveToFirst()){
            name = cursor.getString(0);
        }
        return name;
    }

    public void updateUser(String email,String password,Integer id){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(USR_KEY_EMAIL,email);
        cv.put(USR_KEY_PASS,password);
        database.update(USR_TABLE_NAME,cv,USR_KEY_ID+"=?",new String[]{""+id});
        database.close();
    }

    public List<String> getUserCred(int id){
        List<String> cred = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM "+USR_TABLE_NAME+" WHERE "+USR_KEY_ID+"="+""+id;
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            cred.add(cursor.getString(1));
            cred.add(cursor.getString(2));

        }
        cursor.close();
        db.close();
        return cred;
    }
    //Adding new Schedule
   public void addNewSchedule(ScheduleEvent se){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_RCODE,se.get_reqCode());
        cv.put(KEY_FROM,""+se.get_from());
        cv.put(KEY_TO,se.get_to());
        cv.put(KEY_SUB,se.get_sub());
        cv.put(KEY_BODY,se.get_body());
        cv.put(KEY_TRIG,se.get_trigg());
        cv.put(KEY_EXPIRE,se.getExpire());
        cv.put(KEY_ATTACH,se.get_attach());
        long i = db.insert(TABLE_NAME, null, cv);
        db.close();


    }

    //Retrieving Schedule by id

   public ScheduleEvent getSchedule(int id){

        ScheduleEvent se = new ScheduleEvent();
        String sId=""+id;
        String query = "SELECT * FROM "+TABLE_NAME+" WHERE "+KEY_ID +"="+sId;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            se.set_reqCode(Integer.parseInt(cursor.getString(1)));
            se.set_from(Integer.valueOf(cursor.getString(2)));
            se.set_toArr(se.get_ArrFromString(cursor.getString(3)));
            se.set_sub(cursor.getString(4));
            se.set_body(cursor.getString(5));
            se.set_trigg(Long.parseLong(cursor.getString(6)));
            se.setExpire(Integer.parseInt(cursor.getString(7)));
            se.set_attach(cursor.getString(8));

        }
       cursor.close();
       db.close();
        return se;
    }



    public List<Subject> getAllSubjects(){
        List<Subject> subjectList = new ArrayList<Subject>();
        subjectList.clear();
        String query = "SELECT "+KEY_ID+","+KEY_SUB+","+KEY_EXPIRE+","+KEY_TO+","+KEY_TRIG+" FROM "+TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query,null);
        if (cursor.moveToFirst()) {
            do {
                Subject subject = new Subject();
                subject.setId(Integer.parseInt(cursor.getString(0)));
                subject.setSubject(cursor.getString(1));
                subject.setExpiry(Integer.parseInt(cursor.getString(2)));
                subject.setTo(cursor.getString(3));
                subject.setTrig(Long.parseLong(cursor.getString(4)));
                subjectList.add(subject);
                          }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return subjectList;
    }

    public void deleteSchedule(int id){
        SQLiteDatabase db = this.getWritableDatabase();
       int i= db.delete(TABLE_NAME,KEY_ID+"=?",new String[]{""+id});
        db.close();
    }


    public int getId(int req_code,long trigg){
        SQLiteDatabase db = this.getReadableDatabase();
        int KeyId=-11;
        String query = "SELECT "+KEY_ID+" FROM "+TABLE_NAME+" WHERE "+KEY_RCODE+"="+String.valueOf(req_code)+" AND "+KEY_TRIG+"="+String.valueOf(trigg);
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            KeyId = Integer.parseInt(cursor.getString(0));
        }
        cursor.close();
        db.close();
    return KeyId;
    }

    public int getReqCode(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        int ReqId=-11;
        String query = "SELECT "+KEY_RCODE+" FROM "+TABLE_NAME+" WHERE "+KEY_ID+"="+String.valueOf(id);
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            ReqId = Integer.parseInt(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return ReqId;
    }

    public void setKeyExpire(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_EXPIRE,1);
        int i = db.update(TABLE_NAME,cv,KEY_ID+"=?",new String[]{""+id});
        db.close();
    }

    public List<Recreate> getRecreate(){
        SQLiteDatabase db = this.getReadableDatabase();
        List<Recreate> recreateList = new ArrayList<Recreate>();
        String query = "SELECT "+KEY_ID+","+KEY_RCODE+","+KEY_TRIG+" FROM "+TABLE_NAME+" WHERE "+KEY_EXPIRE+"="+String.valueOf(0);
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do {
                Recreate recreate =  new Recreate(Integer.parseInt(cursor.getString(0)),Integer.parseInt(cursor.getString(1)),Long.parseLong(cursor.getString(2)));
                recreateList.add(recreate);
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return recreateList;
    }

    public int getCount()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT  * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query,null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public int clearScheduleTable()
    {
        SQLiteDatabase database = this.getWritableDatabase();
        int r = database.delete(TABLE_NAME,""+1,null);
        return r;
    }
}
