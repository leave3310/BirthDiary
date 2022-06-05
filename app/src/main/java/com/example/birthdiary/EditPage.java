package com.example.birthdiary;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditPage extends AppCompatActivity {
    EditText editText_name;
    EditText editText_date;
    EditText editText_phone;
    RadioGroup radioGroup_gender;
    RadioButton male;
    RadioButton female;
    Spinner spinner_interest;
    Button btn_back;
    Button btn_update;
    Button btn_date;

    String name;
    String date;
    String phone;
    String gender;
    String interest;
    public static SQLiteDatabase db;
    String db_name = "myDB";
    String tb_name = "person_info";

    List<String> list;
    SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_page);
        db = openOrCreateDatabase(db_name, Context.MODE_PRIVATE, null);
        editText_name = (EditText) findViewById(R.id.edit_name);
        editText_date = (EditText) findViewById(R.id.edit_date);
        editText_phone = (EditText) findViewById(R.id.edit_phone);
        radioGroup_gender = (RadioGroup) findViewById(R.id.radio_group_gender);
        male = (RadioButton)findViewById(R.id.male);
        female = (RadioButton)findViewById(R.id.female);
        spinner_interest = (Spinner) findViewById(R.id.spinner_interest);
        btn_back = (Button)findViewById(R.id.btn_back);
        btn_update = (Button)findViewById(R.id.btn_update);
        btn_date = (Button)findViewById(R.id.btn_calandar);
        Bundle bundle = getIntent().getExtras();
        name = bundle.getString("name");
        date = bundle.getString("date");phone = bundle.getString("phone");
        gender = bundle.getString("gender");
        interest = bundle.getString("interest");
        Log.v("test3", date);
        Log.v("test3", name);
        Log.v("test3", phone);
        Log.v("test3", gender);
        Log.v("test3", interest);

        String sql = "Select * from person_info where name = ? and date = ? and phone = ?";
        Cursor c = db.rawQuery(sql, new String[]{name, date, phone});
        if ( c.getCount()!=0){
            if (c.moveToFirst()){
                do{
                    editText_name.setText(c.getString(0));
                    editText_date.setText("2022/"+c.getString(1));
                    editText_phone.setText(c.getString(2));
                    spinner_interest = (Spinner)findViewById(R.id.spinner_interest);
                    switch (Integer.parseInt(gender)){
                        case 0:
                            male.setChecked(true);
                            break;
                        case 1:
                            female.setChecked(true);
                    }
//                    ArrayAdapter adapter = ArrayAdapter.createFromResource(this,
//                            R.array.interest,
//                            android.R.layout.simple_dropdown_item_1line);
                    list = new ArrayList<String>();

                    pref = getSharedPreferences("interest", MODE_MULTI_PROCESS);
                    int size = pref.getInt("size", 0);
                    if(size>0){
                        list.clear();
                        for(int i=0;i<size;i++){
                            list.add(pref.getString(String.valueOf(i), ""));
                            Log.v("size", pref.getString(String.valueOf(i), ""));
                        }
                    }
                    ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, list);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_interest.setAdapter(adapter);
                    spinner_interest.setSelection(Integer.parseInt(interest));
                }while(c.moveToNext());
            }
        }

        // 得到現在的日期
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener;
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                editText_date.setText(String.valueOf(i)+"/"+String.valueOf(i1+1)+"/"+String.valueOf(i2));
            }
        };
        DatePickerDialog dialog_date = new DatePickerDialog(EditPage.this, dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONDAY),
                calendar.get(Calendar.DAY_OF_MONTH));


        btn_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_date.show();
            }
        });



        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name1 = editText_name.getText().toString();
                String phone1 = editText_phone.getText().toString();
                String pre_date = editText_date.getText().toString();
                String date1 = pre_date.split("/", pre_date.length())[1] + "/" + pre_date.split("/", pre_date.length())[2];
                String gender1 = "0";
                switch (radioGroup_gender.getCheckedRadioButtonId()){
                    case R.id.male:
                        gender1 = "0";
                        break;
                    case R.id.female:
                        gender1 = "1";
                        break;
                }
                String interest1 = String.valueOf(spinner_interest.getSelectedItemPosition());

                String sql = "UPDATE person_info SET name = '" + name1 +
                                                "', date = '" + date1 +
                                                "', phone = '" + phone1 +
                                                "', gender = '" + gender1 +
                                                "', interest = '" + interest1 +
                                                "' WHERE name = '"+name+"' and phone = '"+phone+"' and date = '"+date+"'";
                Log.v("SQL",sql);
                db.execSQL(sql);

//                ContentValues contentValues = new ContentValues();
//                contentValues.put("name", name);
//                contentValues.put("date", date);
//                contentValues.put("phone", phone1);
//                contentValues.put("gender", gender);
//                contentValues.put("interest", interest);
//                db.update(tb_name, contentValues, "phone="+phone, null);

                finish();


            }
        });
    }
}