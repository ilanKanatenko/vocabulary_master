package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    CheckBox checkBoxes;
    CheckBox start_from_last_position;
    Button start_game;
    int quantity_of_user_units;
    String selected_unit;
    final String SHOW_KNOW_WORDS = "מילים ידועות";
    final String DONT_SHOW_KNOW_WORDS = "אל תכלול מילים ידועות";
    final String START_FROM_LAST_POSITION = "start_from_last_position";
    final String DONT_START_FROM_LAST_POSITION = "dont_start_from_last_position";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkBoxes = (CheckBox) findViewById(R.id.checkBox);
        checkBoxes.setChecked(true);
        start_from_last_position = (CheckBox) findViewById(R.id.start_from_last_position);
        start_from_last_position.setChecked(true);
        start_game = (Button) findViewById(R.id.start_game_button);
        start_game.setEnabled(false);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);

        SharedPreferences sp = getSharedPreferences("MyApplication", 0);
        final SharedPreferences.Editor sedt = sp.edit();
        quantity_of_user_units = sp.getInt("quantity_of_user_units", 0);


        List<String> categories = new ArrayList<String>();
        categories.add("אנא בחר יחידת לימוד:");
        for (int i = 0; i < quantity_of_user_units; i++) {
            categories.add("יחידה " + i);

        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);


        findViewById(R.id.learn_state).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Main6Activity.class);
                startActivity(intent);
                finish();
            }


        });


        findViewById(R.id.start_game_button).setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {

                                                                        //share
                                                                        sedt.putString("unit", selected_unit);

                                                                        if (checkBoxes.isChecked()) {

                                                                            sedt.putString("know_words", SHOW_KNOW_WORDS);

                                                                            if(start_from_last_position.isChecked()){
                                                                                sedt.putString("start_from_last_position", START_FROM_LAST_POSITION);
                                                                            }
                                                                            else{
                                                                                sedt.putString("start_from_last_position", DONT_START_FROM_LAST_POSITION);
                                                                            }
                                                                        } else {
                                                                            sedt.putString("know_words", DONT_SHOW_KNOW_WORDS);

                                                                        }


                                                                        sedt.commit();


                                                                        Intent intent = new Intent(MainActivity.this, Main3Activity.class);
                                                                        startActivity(intent);
                                                                        finish();

                                                                    }


                                                                }
        );


        checkBoxes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkBoxes.isChecked()){
                    start_from_last_position.setVisibility(View.INVISIBLE);
                }
                else{
                    start_from_last_position.setVisibility(View.VISIBLE);
                }

            }
        });


    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        start_game.setEnabled(false);
        selected_unit = parent.getItemAtPosition(position).toString();
        if (selected_unit != "אנא בחר יחידת לימוד:") {
            start_game.setEnabled(true);

        }
    }

    public void onNothingSelected(AdapterView<?> arg0) {

    }


    @Override
    public void onBackPressed() {
        final Context context = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage("האם אתה בטוח שברצונך לצאת מהאפליקציה?");
        builder.setCancelable(true);

        builder.setPositiveButton("כן", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Do something when user want to exit the app
                // Let allow the system to handle the event, such as exit the app
                MainActivity.super.onBackPressed();
            }
        });

        builder.setNegativeButton("לא", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Do something when want to stay in the app
            }
        });

        // Create the alert dialog using alert dialog builder
        AlertDialog dialog = builder.create();

        // Finally, display the dialog when user press back button
        dialog.show();

    }
}
