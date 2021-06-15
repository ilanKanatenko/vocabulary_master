package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Main6Activity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Button back_to_main_menu;
    TextView num_of_words_in_unit_view;
    TextView know_current_view;
    TextView unknow_current_view;
    TextView half_know_current_view;
    TextView header_current_view;
    TextView know_words_header_view;
    TextView unknow_words_header_view;
    TextView half_know_words_header_view;
    String num_of_words_in_unit;
    String know_current;
    String unknow_current;
    String half_know_current;
    Spinner units;
    String unit_selected;
    FirebaseDatabase database;
    DatabaseReference myRef;
    int quantity_of_user_units;
    String user_name;
    SharedPreferences sp;
    boolean get_user_statistics_for_selected_unit_flag = false;
    boolean get_unit_size_flag = false;
    String unit_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main6);

        findViewById(R.id.back_to_main_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (Main6Activity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }


        });

        init_activity_views();
        init_spinner();

    }

    public void get_data_from_db() {
        get_user_statistics_for_selected_unit();
        get_unit_size();
    }

    public void set_activity_content() {
        if (get_unit_size_flag && get_user_statistics_for_selected_unit_flag) {
            num_of_words_in_unit_view.setText("כמות מילים ביחידה: "+num_of_words_in_unit);
            know_current_view.setText(know_current);
            unknow_current_view.setText(unknow_current);
            half_know_current_view.setText( half_know_current);
            show_unit_views_information();

        }
    }


    public void init_spinner() {
        hide_unit_views_information();
        units = (Spinner) findViewById(R.id.spinner);
        units.setOnItemSelectedListener(this);

        quantity_of_user_units = sp.getInt("quantity_of_user_units", 0);
        List<String> categories = new ArrayList<String>();
        categories.add("אנא בחר יחידה");
        for (int i = 0; i < quantity_of_user_units; i++) {
            categories.add("יחידה " + i);

        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        units.setAdapter(dataAdapter);


    }

    public void init_activity_views() {
        sp = getSharedPreferences("MyApplication", 0);
        num_of_words_in_unit_view = (TextView) findViewById(R.id.num_of_words_in_unit);
        know_current_view = (TextView) findViewById(R.id.know_current);
        unknow_current_view = (TextView) findViewById(R.id.unknow_current);
        half_know_current_view = (TextView) findViewById(R.id.half_know_current);
        header_current_view = (TextView) findViewById(R.id.header_current);
        half_know_words_header_view = (TextView) findViewById(R.id.half_know_words_header);
        know_words_header_view = (TextView) findViewById(R.id.know_words_header);
        unknow_words_header_view = (TextView) findViewById(R.id.unknow_words_header);
        back_to_main_menu=(Button)findViewById(R.id.back_to_main_menu);
        database = FirebaseDatabase.getInstance();
        user_name = sp.getString("user_name", null);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        unit_selected = parent.getItemAtPosition(position).toString();
        if (!(unit_selected.equals("אנא בחר יחידה"))) {
            unit_number = unit_selected.split(" ")[1];
            hide_unit_views_information();
            get_user_statistics_for_selected_unit_flag = false;
            get_unit_size_flag = false;
            get_data_from_db();
        } else {
            hide_unit_views_information();

        }
    }

    public void onNothingSelected(AdapterView<?> arg0) {
    }


    public void get_unit_size() {
        myRef = database.getReference("יחידות לימוד").child("מידע על יחידות לימוד").child(unit_number);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                num_of_words_in_unit = dataSnapshot.getValue(String.class);

                get_unit_size_flag = true;
                myRef.removeEventListener(this);
                set_activity_content();

            }

            @Override
            public void onCancelled(DatabaseError error) {
            }

        });

    }

    public void get_user_statistics_for_selected_unit() {
        myRef = database.getReference("Users").child(user_name).child("units").child(unit_number);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                know_current = dataSnapshot.child("know_status_counter").getValue(Integer.class).toString();
                unknow_current = dataSnapshot.child("unknow_status_counter").getValue(Integer.class).toString();
                half_know_current = dataSnapshot.child("half_know_status_counter").getValue(Integer.class).toString();


                get_user_statistics_for_selected_unit_flag = true;
                myRef.removeEventListener(this);
                set_activity_content();

            }

            @Override
            public void onCancelled(DatabaseError error) {
            }

        });

    }

    public void hide_unit_views_information() {

        num_of_words_in_unit_view.setVisibility(View.INVISIBLE);
        know_current_view.setVisibility(View.INVISIBLE);
        unknow_current_view.setVisibility(View.INVISIBLE);
        half_know_current_view.setVisibility(View.INVISIBLE);
        header_current_view.setVisibility(View.INVISIBLE);
        unknow_words_header_view.setVisibility(View.INVISIBLE);
        know_words_header_view.setVisibility(View.INVISIBLE);
        half_know_words_header_view.setVisibility(View.INVISIBLE);

    }

    public void show_unit_views_information() {

        num_of_words_in_unit_view.setVisibility(View.VISIBLE);
        know_current_view.setVisibility(View.VISIBLE);
        unknow_current_view.setVisibility(View.VISIBLE);
        half_know_current_view.setVisibility(View.VISIBLE);
        header_current_view.setVisibility(View.VISIBLE);
        unknow_words_header_view.setVisibility(View.VISIBLE);
        know_words_header_view.setVisibility(View.VISIBLE);
        half_know_words_header_view.setVisibility(View.VISIBLE);

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Main6Activity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
