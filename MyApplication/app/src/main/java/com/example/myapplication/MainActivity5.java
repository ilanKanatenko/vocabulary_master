package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity5 extends AppCompatActivity {

    TextView num_of_words_in_unit;
    TextView half_know_last;
    TextView know_last;
    TextView unknow_last;
    TextView know_current;
    TextView unknow_current;
    TextView half_know_current;
    TextView unit_number_header;
    Button move_to_main_menu;
    SharedPreferences sp;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);

        init_activity_views();
        init_from_sp();

        move_to_main_menu.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               Intent intent = new Intent(MainActivity5.this, MainActivity.class);
                                               startActivity(intent);
                                               finish();
                                           }

                                       }
        );


    }

    @SuppressLint("SetTextI18n")
    public void init_from_sp() {

        num_of_words_in_unit.setText("כמות המילים ביחידה זו: "+sp.getInt("unit_length", 0));
        half_know_last.setText(""+sp.getInt("half_know_status_counter_from_db", 0));
        know_last.setText(""+sp.getInt("know_status_counter_from_db", 0));
        unknow_last.setText(""+sp.getInt("unknow_status_counter_from_db", 0));
        know_current.setText(""+sp.getInt("know_status_counter", 0));
        unknow_current.setText(""+sp.getInt("unknow_status_counter", 0));
        half_know_current.setText(""+sp.getInt("half_know_status_counter", 0));
        if (sp.getInt("know_status_counter", 0)==sp.getInt("unit_length", 0)){
            unit_number_header.setText("כל הכבוד "+sp.getString("unit", null) +" הושלמה בהצלחה");
        }
        else{
            unit_number_header.setText("תוצאות הלמידה של "+sp.getString("unit", null));
        }


    }

    public void init_activity_views() {
        sp = getSharedPreferences("MyApplication", 0);
        num_of_words_in_unit = (TextView) findViewById(R.id.num_of_words_in_unit);
        half_know_last = (TextView) findViewById(R.id.half_know_last);
        know_last = (TextView) findViewById(R.id.know_last);
        unknow_last = (TextView) findViewById(R.id.unknow_last);
        know_current = (TextView) findViewById(R.id.know_current);
        unknow_current = (TextView) findViewById(R.id.unknow_current);
        half_know_current = (TextView) findViewById(R.id.half_know_current);
        unit_number_header = (TextView) findViewById(R.id.unit_number_header);
        move_to_main_menu = (Button) findViewById(R.id.move_to_main_menu);


    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MainActivity5.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
