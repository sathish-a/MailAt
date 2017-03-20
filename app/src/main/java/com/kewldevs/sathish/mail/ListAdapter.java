package com.kewldevs.sathish.mail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sathish on 5/6/16.
 */
public class ListAdapter extends ArrayAdapter {
    private Context context;
    private List<Subject> subjectList = new ArrayList<Subject>();

    public ListAdapter(Context context, int resource) {
        super(context, resource);
        this.context = context;

    }

    public void addToList(List<Subject> list) {
        subjectList.clear();
        subjectList = list;

    }

    public int getCount() {
        return this.subjectList.size();
    }

    public Object getItem(int position) {
        return this.subjectList.get(position);
    }

    static class ListHolder {
        TextView SUBJECT;
        ImageView STATUS_IMG;
        TextView TO;
        TextView DATE;
        ImageView SUB_IMG;
    }


    public void removeItem(Subject subject) {
        if(subject!=null){
            this.subjectList.remove(subject);
            notifyDataSetChanged();
        }else {
            this.subjectList.clear();
            notifyDataSetChanged();
        }

    }





    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View list = convertView;
        ListHolder listHolder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            list = inflater.inflate(R.layout.list_view, parent, false);
            listHolder = new ListHolder();
            listHolder.SUBJECT = (TextView) list.findViewById(R.id.tvSub);
            listHolder.STATUS_IMG = (ImageView) list.findViewById(R.id.imgStatus);
            listHolder.TO = (TextView) list.findViewById(R.id.tvTo);
            listHolder.DATE = (TextView) list.findViewById(R.id.tvDate);
            listHolder.SUB_IMG = (ImageView) list.findViewById(R.id.circle);
            list.setTag(listHolder);

        } else {
            listHolder = (ListHolder) list.getTag();
        }
        Subject subject = this.subjectList.get(position);
        listHolder.SUBJECT.setText(subject.getSubject());
        String to = "To: "+subject.getTo();
        listHolder.TO.setText(to);
        listHolder.DATE.setText(subject.getDate());
        String firstLetter = String.valueOf(subject.getSubject().charAt(0));
        ColorGenerator generator = ColorGenerator.MATERIAL;
        TextDrawable drawable = TextDrawable.builder().buildRound(firstLetter,generator.getRandomColor());
        listHolder.SUB_IMG.setImageDrawable(drawable);
        if(subject.getExpiry()==0){
            listHolder.STATUS_IMG.setImageResource(R.drawable.active);
        }else {
            listHolder.STATUS_IMG.setImageResource(R.drawable.idle);
        }
        return list;
    }



}
