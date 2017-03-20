package com.kewldevs.sathish.mail;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

/**
 * Created by sathish on 4/30/16.
 */
public class GetSub extends AsyncTask<Void,Void,List<Subject>> {
    Context mContext;
    DataBase base;
    public GetSub(Context context) {
        mContext = context;
        base = new DataBase(mContext);
    }

    @Override
    protected List<Subject> doInBackground(Void... params) {
        return base.getAllSubjects();
    }


}
