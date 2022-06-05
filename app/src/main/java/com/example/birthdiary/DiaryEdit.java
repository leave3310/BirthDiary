package com.example.birthdiary;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;


public class DiaryEdit extends AppCompatActivity {
    private LinearLayout contentLayout;
    private Button saveButton;
    private ArrayList<Pair<View, String>> contents = new ArrayList<>();
    public static SQLiteDatabase db;
    private String db_name = "myDB";
    private String tb_name = "person_info";
    TextView name, date, phone, sex, habit;
    String bundleName = "", bundleDate = "", bundlePhone = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diary_layout);
        findView();
        setView();
        setListener();
    }

    private void findView() {
        contentLayout = (LinearLayout) findViewById(R.id.content_layout);
        saveButton = (Button) findViewById((R.id.save_button));
        name = (TextView) findViewById(R.id.name);
        date = (TextView) findViewById(R.id.date);
        phone = (TextView) findViewById(R.id.tel);
        sex = (TextView) findViewById(R.id.sex);
        habit = (TextView) findViewById(R.id.habit);
    }

    private void setView() {
        Bundle bundle = getIntent().getExtras();
        bundleName = bundle.getString("name");
        bundleDate = bundle.getString("date");
        bundlePhone = bundle.getString("phone");
        name.setText(bundleName);
        date.setText(bundleDate);
        phone.setText(bundlePhone);
        sex.setText(bundle.getString("gender").equals("0") ? "男" : "女");

        SharedPreferences pref = getSharedPreferences("interest", MODE_MULTI_PROCESS);;
        habit.setText(pref.getString(bundle.getString("interest"), ""));
        setTitle(bundle.getString("name"));

        db = openOrCreateDatabase(db_name, Context.MODE_PRIVATE, null);

        String diaryRecord = String.format("SELECT * FROM %s;", tb_name);
        Cursor c = db.rawQuery("select * from " + tb_name + " where name=? AND date =? AND phone =?",
                new String[]{bundleName, bundleDate, bundlePhone});

        if (c.moveToFirst()) {
            addEditText(c.getString(5));
        }

    }

    //放入編輯文字介面
    private void addEditText(String text) {
        EditText editText = new EditText(this);
        editText.setTextColor(0xff4a4a4a);
        editText.setTextSize(18);
        editText.setText(text);
        editText.setBackgroundColor(Color.TRANSPARENT);
        editText.requestFocus();

        contents.add(new Pair<View, String>(editText, text));
        contentLayout.addView(editText);
    }

    private void setListener() {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    saveDiary();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void saveDiary() throws Exception {
        String tmp = "";
        for (int i = 0; i < contents.size(); i++) {
            Pair<View, String> content = contents.get(i);
            if (content.first instanceof EditText) {
                tmp += ((EditText) content.first).getText().toString();
            }
        }
        String updateContent = "UPDATE " + tb_name + " SET diary='" + tmp +
                "' WHERE name='" + bundleName + "' AND date='" + bundleDate +
                "' AND phone='" + bundlePhone + "';";

        db.execSQL(updateContent);
        finish();
    }
}
