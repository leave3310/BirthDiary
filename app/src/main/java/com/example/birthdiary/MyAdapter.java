package com.example.birthdiary;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class MyAdapter extends BaseAdapter {
    public SQLiteDatabase db;
    String tb_name = "person_info";
    private LayoutInflater myInflater;
    private List<Map<String, String>> infos;
    MaterialCalendarView calendarView;
    Context context;
    public MyAdapter(Context context, List<Map<String, String>> infos, SQLiteDatabase db, MaterialCalendarView calendarView){
        this.context = context;
        myInflater = LayoutInflater.from(context);
        this.infos = infos;
        this.db = db;
        this.calendarView = calendarView;
    }
    @Override
    public int getCount() {
        return infos.size();
    }

    @Override
    public Object getItem(int i) {
        return infos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return infos.indexOf(getItem(i));
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = myInflater.inflate(R.layout.listview_layout, null);
        TextView tv_name = (TextView) view.findViewById(R.id.tv_friend_name);
        Button btn_delete = (Button)view.findViewById(R.id.btn_delete);
        Button btn_edit = (Button)view.findViewById(R.id.btn_edit);
        Button btn_diary = (Button)view.findViewById(R.id.btn_diary);

        tv_name.setText(infos.get(i).get("name"));
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.delete(tb_name, "name=? and date=?", new String[]{infos.get(i).get("name"), infos.get(i).get("date")});
                String sql = "Select * from person_info where date = ? ";
                Cursor c = db.rawQuery(sql, new String[]{infos.get(i).get("date")});
                if(c.getCount()==0){
                    Collection<CalendarDay> clear = new ArrayList<>();
                    String date = infos.get(i).get("date");
                    String[] d = date.split("/", date.length());
                    clear.add(CalendarDay.from(2022, Integer.parseInt(d[0]), Integer.parseInt(d[1])));
                    calendarView.addDecorator(new EventDecorator(Color.BLUE, clear,context , 1));
                }
            }
        });

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EditPage.class);
                Bundle bundle = new Bundle();
                bundle.putString("name", infos.get(i).get("name"));
                bundle.putString("date", infos.get(i).get("date"));
                bundle.putString("phone", infos.get(i).get("phone"));
                bundle.putString("gender", infos.get(i).get("gender"));
                bundle.putString("interest", infos.get(i).get("interest"));
                intent.putExtras(bundle);
                context.startActivity(intent);


            }
        });

        btn_diary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DiaryEdit.class);
                Bundle bundle = new Bundle();
                bundle.putString("name", infos.get(i).get("name"));
                bundle.putString("date", infos.get(i).get("date"));
                bundle.putString("phone", infos.get(i).get("phone"));
                bundle.putString("gender", infos.get(i).get("gender"));
                bundle.putString("interest", infos.get(i).get("interest"));
                intent.putExtras(bundle);
                context.startActivity(intent);

            }
        });

        return view;
    }

}
