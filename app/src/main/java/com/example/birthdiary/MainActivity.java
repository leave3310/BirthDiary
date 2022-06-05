package com.example.birthdiary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    ViewPager vp;
    Spinner spinner_interest;
    EditText editText_name;
    EditText editText_phone;
    EditText editText_date;
    EditText editText_other;
    RadioGroup radioGroup_gender;
    Button btn_send;

    List<String> list;
    SharedPreferences pref;

    // calender
    MaterialCalendarView simpleCalendarView;
    Collection<CalendarDay> dates;

    // database
    public static SQLiteDatabase db;
    String db_name = "myDB";
    String tb_name = "person_info";
    List<Map<String, String>> items = new ArrayList<Map<String, String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        List<View> views = new ArrayList<View>();
        // 開啟資料庫
        openDB();
        // 得到現在的日期
        Calendar calendar = Calendar.getInstance();

        pref = getSharedPreferences("interest", MODE_MULTI_PROCESS);
        list = new ArrayList<String>();
        list.add("唱歌");
        list.add("跳舞");
        list.add("其他");

        int size = pref.getInt("size", 0);
        if(size>0){
            list.clear();
            for(int i=0;i<size;i++){
                list.add(pref.getString(String.valueOf(i), ""));
                Log.v("size", pref.getString(String.valueOf(i), ""));
            }
        }
        Log.v("size", String.valueOf(size));
        vp = (ViewPager) findViewById(R.id.view);
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);

        // date part
        View main_page = (inflater.inflate(R.layout.mainpage_layout, null));

        Button btn_date = (Button)main_page.findViewById(R.id.btn_calandar);
        EditText edit_date = (EditText)main_page.findViewById(R.id.edit_date);
        editText_name = (EditText)main_page.findViewById(R.id.edit_name);
        editText_phone = (EditText)main_page.findViewById(R.id.edit_phone);
        editText_date = (EditText)main_page.findViewById(R.id.edit_date);
        radioGroup_gender = (RadioGroup)main_page.findViewById(R.id.radio_group_gender);
        btn_send = (Button)main_page.findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name;
                String date;
                String phone;
                int gender = 0;
                int interest = 0;

                name = editText_name.getText().toString();
                phone = editText_phone.getText().toString();
                String pre_date = edit_date.getText().toString();
                date = pre_date.split("/", pre_date.length())[1] + "/" + pre_date.split("/", pre_date.length())[2];
                switch (radioGroup_gender.getCheckedRadioButtonId()){
                    case R.id.male:
                        gender = 0;
                        break;
                    case R.id.female:
                        gender = 1;
                        break;
                }

                interest = spinner_interest.getSelectedItemPosition();
                if(interest==list.size()-1){
                    list.remove(list.size()-1);
                    list.add(editText_other.getText().toString());
                    list.add("other");
                    editText_other.setText("");
                    pref.edit().clear().commit();
                    pref.edit().putInt("size", list.size()).commit();
                    for(int i=0;i<list.size();i++){
                        pref.edit().putString(String.valueOf(i), list.get(i)).commit();
                    }

                }
                addDB(name, date, phone, gender, interest);
                queryDB();
                editText_name.setText("");
                editText_date.setText("");
                editText_phone.setText("");

            }
        });



        DatePickerDialog.OnDateSetListener dateSetListener;
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                edit_date.setText(String.valueOf(i)+"/"+String.valueOf(i1+1)+"/"+String.valueOf(i2));
            }
        };
        DatePickerDialog dialog_date = new DatePickerDialog(MainActivity.this, dateSetListener,
                                                            calendar.get(Calendar.YEAR),
                                                            calendar.get(Calendar.MONDAY),
                                                            calendar.get(Calendar.DAY_OF_MONTH));


        btn_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_date.show();
            }
        });

        // interest part
        spinner_interest = (Spinner)main_page.findViewById(R.id.spinner_interest);
        editText_other = (EditText)main_page.findViewById(R.id.edit_other);
//        spinner_interest
//        ArrayAdapter adapter = ArrayAdapter.createFromResource(this,
//                                                                R.array.interest,
//                                                               android.R.layout.simple_dropdown_item_1line);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_interest.setAdapter(adapter);
        spinner_interest.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==(list.size()-1)){
                    editText_other.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // calendar
        View view_calendar = (inflater.inflate(R.layout.calendar_layout, null));

        simpleCalendarView = (MaterialCalendarView)view_calendar.findViewById(R.id.calendarView);
        dates = new ArrayList<>();

        queryDB();

        // set Alarm
        List<Long> times = new ArrayList<Long>();
        List<String> names = new ArrayList<String>();

        for(Map<String, String>m : items){
            Calendar calendar1 = Calendar.getInstance();
            String str_date = m.get("date");
            String[] d = str_date.split("/");
            int year=2022, month, date;
            month = Integer.parseInt(d[0])-1;
            date = Integer.parseInt(d[1])-1;
            calendar1.set(year, month, date);
            times.add(calendar1.getTimeInMillis());
            names.add(m.get("name"));
//            Log.v("aaa", String.valueOf(calendar1.getTimeInMillis()));
        }
        setAlarmTimer(times, names);

        simpleCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                String m = String.valueOf(date.getMonth());
                String d = String.valueOf(date.getDay());

                List<Map<String, String>> info = pitch_info(m+"/"+d);

                Log.v("test2", m+"/"+d);
                ListView lv_info = (ListView) view_calendar.findViewById(R.id.listview_person_info);
                MyAdapter myAdapter = new MyAdapter(MainActivity.this, info, db, simpleCalendarView);
                lv_info.setAdapter(myAdapter);


            }
        });
        views.add(main_page);
        views.add(view_calendar);
        vp.setAdapter(new Adapter_viewpager(views));
        vp.setCurrentItem(0);

    }

    public void openDB(){
        db = openOrCreateDatabase(db_name, Context.MODE_PRIVATE, null);
        String createTable = "CREATE TABLE IF NOT EXISTS " + tb_name + "(name VARCHAR(32), " +
                "date VARCHAR(10)," + "phone VARCHAR(20)," + "gender INTRGER(1)," + "interest INTEGER(1),"+ "diary NVARCHAR(100));";
        db.execSQL(createTable);
    }
    public void addDB(String name, String date, String phone, int gender, int interest){
        ContentValues values = new ContentValues(5);
        values.put("name", name);
        values.put("date", date);
        values.put("phone", phone);
        values.put("gender", gender);
        values.put("interest", interest);
        values.put("diary", "");
        db.insert(tb_name,null, values);
    }
    public void queryDB(){
        items = new ArrayList<Map<String, String>>();
        Cursor c = db.rawQuery("SELECT * FROM " + tb_name, null);
        if ( c.getCount()!=0){
            if (c.moveToFirst()){
                do{
                    Map<String, String> item = new HashMap<>();
                    item.put("name", c.getString(0));
                    Log.v("nameSQL", c.getString(0));
                    item.put("date", c.getString(1));
                    item.put("phone", c.getString(2));
                    item.put("gender", String.valueOf(c.getInt(3)));
                    item.put("interest", String.valueOf(c.getInt(4)));
                    items.add(item);
                }while(c.moveToNext());
            }
        }
        for(Map<String, String> m : items){
            String date = m.get("date");
            String[] d = date.split("/", date.length());
            Log.v("test", date);
            Log.v("test", m.get("name"));
            Log.v("test", m.get("phone"));
            Log.v("test", m.get("gender"));
            Log.v("test", m.get("interest"));
            dates.add(CalendarDay.from(2022, Integer.parseInt(d[0]), Integer.parseInt(d[1])));
            simpleCalendarView.addDecorator(new EventDecorator(Color.BLUE, dates, this, 0));
        }

    }
    public List<Map<String, String>> pitch_info(String date){
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        String sql = "Select * from person_info where date = ? ";
        Cursor c = db.rawQuery(sql, new String[]{date});
        if ( c.getCount()!=0){
            if (c.moveToFirst()){
                do{
                    Map<String, String> item = new HashMap<>();
                    item.put("name", c.getString(0));
                    item.put("date", c.getString(1));
                    item.put("phone", c.getString(2));
                    item.put("gender", String.valueOf(c.getInt(3)));
                    item.put("interest", String.valueOf(c.getInt(4)));
                    list.add(item);
                }while(c.moveToNext());
            }
        }
        for(Map<String, String> m : list){
            String date1 = m.get("date");
            String[] d = date.split("/", date.length());
            Log.v("test2", date);
            Log.v("test2", m.get("name"));
            Log.v("test2", m.get("phone"));
            Log.v("test2", m.get("gender"));
            Log.v("test2", m.get("interest"));
        }

        return list;
    }

    void setAlarmTimer(List<Long> times, List<String> names){
        Calendar calendar = Calendar.getInstance();
        for(int i=0;i<times.size();i++){
            if(times.get(i)>calendar.getTimeInMillis()){
                Intent alarm_intent = new Intent(this, AlarmReceiver.class);
                alarm_intent.putExtra("name", names.get(i));
                alarm_intent.setAction(GlobalValues.TIMER_ACTION);
                int alarmId = SharedPreUtils.getInt(this, "alarm_id", 0);
                SharedPreUtils.setInt(this, "alarm_id", ++alarmId);
                PendingIntent sender = PendingIntent.getBroadcast(this, alarmId, alarm_intent, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarm.set(AlarmManager.RTC_WAKEUP, times.get(i), sender);
            }

        }

    }

}