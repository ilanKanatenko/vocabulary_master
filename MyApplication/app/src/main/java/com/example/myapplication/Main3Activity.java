package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//נשאר להציג את הפירוש של המילה הנכונה
//נשאר לעדשות את כפתור הסיום משחק
//לעשות counter  שיספור כמה מילים סווגו מהמערך של המילים המסווגות וכאשר המונה מגיע לגודל המערך אפשר להציג את כפתור הסיום משחק
public class Main3Activity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference refDb;
    SharedPreferences sp;
    SharedPreferences.Editor sedt;
    public Word[] words_db;
    int words_db1_lengh = 0;
    int words_db_pointer = 0;
    TextView outcome;
    ImageView know_image_view;
    ImageView unknow_image_view;
    ImageView half_know_image_view;
    TextView know_text_view;
    TextView unknow_text_view;
    TextView half_know_text_view;
    TextView wordToShow;
    TextView word_pionter;
    TextView showAnwer;
    ImageView arrow_right;
    ImageView arrow_left;
    Button end_game;
    ConstraintLayout body;
    String unit;
    String know_words;
    String user_name;
    List<String> user_statuses = new ArrayList<>();
    final String KNOWN_WORDS = "מילים ידועות";
    final String UNKNOWN_WORDS = "מילים לא ידועות";
    final String HALF_KNOWN_WORDS = "מילים ידועות חלקית";
    final String NOT_CLASSIFIED = "לא סווג";
    String unit_number;
    int unknow_status_counter = 0;
    int know_status_counter = 0;
    int half_know_status_counter = 0;
    int unknow_status_counter_from_db;
    int know_status_counter_from_db;
    int half_know_status_counter_from_db;
    int know_words_to_decrease;
    final String SHOW_KNOW_WORDS = "מילים ידועות";
    final String DONT_SHOW_KNOW_WORDS = "אל תכלול מילים ידועות";
    OnSwipeTouchListener onSwipeTouchListener;
    final String START_FROM_LAST_POSITION = "start_from_last_position";
    int[] words_db_indexes_for_this_session;
    int last_word_classified_position_show_know_words = 0;
    String start_from_last_position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        database = FirebaseDatabase.getInstance();
        view_initialization();
        hide_all_views();


        sp = getSharedPreferences("MyApplication", 0);
        sedt = sp.edit();
        unit = sp.getString("unit", null);
        know_words = sp.getString("know_words", null);
        user_name = sp.getString("user_name", null);
        start_from_last_position = sp.getString("start_from_last_position", null);

        get_user_statuses_of_unit_from_db();


        arrow_right.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               next_word();
                                               hide_objects();
                                               showAnwer.setVisibility(View.VISIBLE);


                                           }

                                       }
        );

        arrow_left.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              previous_word();
                                              hide_objects();
                                              showAnwer.setVisibility(View.VISIBLE);

                                          }

                                      }
        );

        showAnwer.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             show_objects();
                                             outcome.setVisibility(View.VISIBLE);
                                             outcome.setText(words_db[words_db_indexes_for_this_session[words_db_pointer]].hebrew_word);
                                             showAnwer.setVisibility(View.INVISIBLE);

                                             set_selected_image_view_color(user_statuses.get(words_db_indexes_for_this_session[words_db_pointer]));


                                         }

                                     }
        );


        know_image_view.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View v) {
                                                   change_statistics_counters(KNOWN_WORDS);

//    animation
                                                   ObjectAnimator mover = ObjectAnimator.ofFloat(know_image_view, "translationY", -25f, 25f);
                                                   mover.setDuration(350);
                                                   mover.setRepeatCount(1);
                                                   mover.setRepeatMode(ValueAnimator.REVERSE);

                                                   ObjectAnimator back_to_mid = ObjectAnimator.ofFloat(know_image_view, "translationY", -25f, 0f);
                                                   back_to_mid.setDuration(350);
                                                   AnimatorSet animatorSet = new AnimatorSet();
                                                   animatorSet.play(mover).before(back_to_mid);
                                                   animatorSet.start();
                                               }
                                           }
        );

        unknow_image_view.setOnClickListener(new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View v) {
                                                     change_statistics_counters(UNKNOWN_WORDS);

                                                     ObjectAnimator mover = ObjectAnimator.ofFloat(unknow_image_view, "translationX", -25f, 25f);
                                                     mover.setDuration(350);
                                                     mover.setRepeatCount(1);
                                                     mover.setRepeatMode(ValueAnimator.REVERSE);

                                                     ObjectAnimator back_to_mid = ObjectAnimator.ofFloat(unknow_image_view, "translationX", -25f, 0f);
                                                     back_to_mid.setDuration(350);

//
                                                     AnimatorSet animatorSet = new AnimatorSet();


                                                     animatorSet.play(mover).before(back_to_mid);
                                                     animatorSet.start();






                                                 }
                                             }
        );
        half_know_image_view.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        change_statistics_counters(HALF_KNOWN_WORDS);

                                                        ObjectAnimator mover = ObjectAnimator.ofFloat(half_know_image_view, "rotation", 360f, 0f);
                                                        mover.setDuration(1000);


                                                        AnimatorSet animatorSet = new AnimatorSet();
                                                        animatorSet.play(mover);
                                                        animatorSet.start();
                                                    }
                                                }
        );

        end_game.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
//                                            refDb.removeEventListener(listener);

                                            sedt.putInt("new_know_words", know_status_counter);
                                            sedt.commit();
                                            end_game.setVisibility(View.INVISIBLE);
                                            update_user_statuses();

                                        }

                                    }
        );


        //

        onSwipeTouchListener = new OnSwipeTouchListener(Main3Activity.this) {
            public void onSwipeLeft() {
                next_word();
                hide_objects();
                showAnwer.setVisibility(View.VISIBLE);
            }

            public void onSwipeRight() {
                previous_word();
                hide_objects();
                showAnwer.setVisibility(View.VISIBLE);

            }
        };


        body.setOnTouchListener(onSwipeTouchListener);


    }

    public void update_user_statuses() {


        unknow_status_counter += unknow_status_counter_from_db;
        know_status_counter += know_status_counter_from_db;
        half_know_status_counter += half_know_status_counter_from_db;

        sedt.putInt("unknow_status_counter", unknow_status_counter);
        sedt.putInt("know_status_counter", know_status_counter);
        sedt.putInt("half_know_status_counter", half_know_status_counter);
        sedt.putInt("unknow_status_counter_from_db", unknow_status_counter_from_db);
        sedt.putInt("know_status_counter_from_db", know_status_counter_from_db);
        sedt.putInt("half_know_status_counter_from_db", half_know_status_counter_from_db);

        sedt.commit();


        refDb = database.getReference("Users").child(user_name).child("units").child(unit_number).child("status");
        refDb.setValue(user_statuses);


        refDb = database.getReference("Users").child(user_name).child("units").child(unit_number).child("unknow_status_counter");
        refDb.setValue(unknow_status_counter);
        refDb = database.getReference("Users").child(user_name).child("units").child(unit_number).child("know_status_counter");
        refDb.setValue(know_status_counter);
        refDb = database.getReference("Users").child(user_name).child("units").child(unit_number).child("half_know_status_counter");
        refDb.setValue(half_know_status_counter);

        refDb = database.getReference("Users").child(user_name).child("units").child(unit_number).child("last_word_classified_position_show_know_words");
        refDb.setValue(last_word_classified_position_show_know_words);

        Intent intent = new Intent(Main3Activity.this, MainActivity5.class);
        startActivity(intent);
        finish();
    }

    public void show_objects() {
        outcome.setVisibility(View.VISIBLE);
        know_image_view.setVisibility(View.VISIBLE);
        unknow_image_view.setVisibility(View.VISIBLE);
        half_know_image_view.setVisibility(View.VISIBLE);
        know_text_view.setVisibility(View.VISIBLE);
        unknow_text_view.setVisibility(View.VISIBLE);
        half_know_text_view.setVisibility(View.VISIBLE);

    }

    public void hide_objects() {
        outcome.setVisibility(View.INVISIBLE);
        know_image_view.setVisibility(View.INVISIBLE);
        unknow_image_view.setVisibility(View.INVISIBLE);
        half_know_image_view.setVisibility(View.INVISIBLE);
        know_text_view.setVisibility(View.INVISIBLE);
        unknow_text_view.setVisibility(View.INVISIBLE);
        half_know_text_view.setVisibility(View.INVISIBLE);
    }

    public void next_word() {
        int words_db_pointer_plus_one;
        if (words_db_pointer == words_db_indexes_for_this_session.length - 1) {
            words_db_pointer = 0;
            words_db_pointer_plus_one = 1;
        } else {
            words_db_pointer++;
            words_db_pointer_plus_one = words_db_pointer + 1;
        }

        word_pionter.setText("" + words_db_pointer_plus_one + "/" + words_db_indexes_for_this_session.length);

        wordToShow.setText(words_db[words_db_indexes_for_this_session[words_db_pointer]].english_word);

    }

    public void previous_word() {
        int words_db_pointer_plus_one = words_db_pointer;
        if (words_db_pointer == 0) {
            words_db_pointer = words_db_indexes_for_this_session.length - 1;
            words_db_pointer_plus_one = words_db_indexes_for_this_session.length;
        } else {
            words_db_pointer -= 1;
        }


        word_pionter.setText("" + words_db_pointer_plus_one + "/" + words_db_indexes_for_this_session.length);

        wordToShow.setText(words_db[words_db_indexes_for_this_session[words_db_pointer]].english_word);


    }


    public void set_selected_image_view_color(String status) {
        unknow_image_view.setBackgroundColor(Color.WHITE);
        know_image_view.setBackgroundColor(Color.WHITE);
        half_know_image_view.setBackgroundColor(Color.WHITE);
        if (status.equals(UNKNOWN_WORDS)) {
            unknow_image_view.setBackgroundColor(Color.RED);
        } else if (status.equals(KNOWN_WORDS)) {
            know_image_view.setBackgroundColor(Color.GREEN);
        } else if (status.equals(HALF_KNOWN_WORDS)) {
            half_know_image_view.setBackgroundColor(Color.YELLOW);
        } else {
            unknow_image_view.setBackgroundColor(Color.WHITE);
            know_image_view.setBackgroundColor(Color.WHITE);
            half_know_image_view.setBackgroundColor(Color.WHITE);
        }
    }

    public void getUnitWordFromDb() {
        refDb = database.getReference("יחידות לימוד").child(unit);
        refDb.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                words_db1_lengh = (int) dataSnapshot.getChildrenCount();
                words_db = new Word[words_db1_lengh];
                for (int i = 0; i < words_db1_lengh; i++) {

                    words_db[i] = new Word(dataSnapshot.child("" + i).child("english").getValue(String.class),
                            dataSnapshot.child("" + i).child("hebrew").getValue(String.class));


                }
                refDb.removeEventListener(this);
                initApp(words_db.length);

            }

            @Override
            public void onCancelled(DatabaseError error) {
// Failed to read value
                Toast.makeText(getApplicationContext(), "Failed to read value." + error.toException(),
                        Toast.LENGTH_SHORT).show();
            }

        });
    }

    public void initApp(int length) {
        sedt.putInt("unit_length", words_db1_lengh);
        sedt.commit();


        if (know_words.equals(SHOW_KNOW_WORDS)) {
            know_words_to_decrease = 0;
            words_db_indexes_for_this_session = new int[words_db.length];
            for (int i = 0; i < words_db.length; i++) {
                words_db_indexes_for_this_session[i] = i;
            }

        } else {
            if (know_status_counter_from_db == words_db.length) {
                Intent intent = new Intent(Main3Activity.this, MainActivity5.class);
                startActivity(intent);
                finish();
                return;
            }
            know_words_to_decrease = know_status_counter_from_db;
            words_db_indexes_for_this_session = new int[words_db.length - know_words_to_decrease];
            int index = 0;
            for (int i = 0; i < words_db.length; i++) {
                if (!(user_statuses.get(i).equals(KNOWN_WORDS))) {
                    words_db_indexes_for_this_session[index] = i;
                    index++;
                }
            }
        }

        if (know_words.equals(SHOW_KNOW_WORDS) && start_from_last_position.equals(START_FROM_LAST_POSITION)) {
            words_db_pointer = last_word_classified_position_show_know_words;
        }

        word_pionter.setText("" + (words_db_pointer + 1) + "/" + (length - know_words_to_decrease));
        wordToShow.setText(words_db[words_db_indexes_for_this_session[words_db_pointer]].english_word);

//        if (know_words.equals(SHOW_KNOW_WORDS) && user_statuses.get(words_db_pointer).equals(KNOWN_WORDS)) {
//            next_word();
//        } else {
//            wordToShow.setText(words_db[words_db_pointer].english_word);
//
//        }
        wordToShow.setVisibility(View.VISIBLE);
        word_pionter.setVisibility(View.VISIBLE);
        showAnwer.setVisibility(View.VISIBLE);
        arrow_right.setVisibility(View.VISIBLE);
        arrow_left.setVisibility(View.VISIBLE);
        body.setBackgroundResource(R.drawable.a8);
        end_game.setVisibility(View.VISIBLE);

    }


    public void hide_all_views() {

        outcome.setVisibility(View.INVISIBLE);
        know_image_view.setVisibility(View.INVISIBLE);
        unknow_image_view.setVisibility(View.INVISIBLE);
        half_know_image_view.setVisibility(View.INVISIBLE);
        know_text_view.setVisibility(View.INVISIBLE);
        unknow_text_view.setVisibility(View.INVISIBLE);
        half_know_text_view.setVisibility(View.INVISIBLE);
        wordToShow.setVisibility(View.INVISIBLE);
        word_pionter.setVisibility(View.INVISIBLE);
        showAnwer.setVisibility(View.INVISIBLE);
        arrow_right.setVisibility(View.INVISIBLE);
        arrow_left.setVisibility(View.INVISIBLE);
        end_game.setVisibility(View.INVISIBLE);

    }

    public void view_initialization() {
        outcome = (TextView) findViewById(R.id.outcome);
        know_image_view = (ImageView) findViewById(R.id.know);
        unknow_image_view = (ImageView) findViewById(R.id.unknow);
        half_know_image_view = (ImageView) findViewById(R.id.half_know);
        know_text_view = (TextView) findViewById(R.id.text_know);
        unknow_text_view = (TextView) findViewById(R.id.text_unknow);
        half_know_text_view = (TextView) findViewById(R.id.text_half_know);
        wordToShow = (TextView) findViewById(R.id.wordToShow);
        word_pionter = (TextView) findViewById(R.id.word_db_pionter);
        showAnwer = (TextView) findViewById(R.id.showAnwer);
        arrow_right = (ImageView) findViewById(R.id.arrow_right);
        arrow_left = (ImageView) findViewById(R.id.arrow_left);
        end_game = (Button) findViewById(R.id.end_game);
        body = (ConstraintLayout) findViewById(R.id.body);
    }

    public void get_user_statuses_of_unit_from_db() {
        String[] num_of_unit = unit.split(" ");
        unit_number = num_of_unit[1];
        refDb = database.getReference("Users").child(user_name).child("units").child(unit_number);
        refDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
                user_statuses = (List<String>) objectMap.get("status");
                unknow_status_counter_from_db = dataSnapshot.child("unknow_status_counter").getValue(Integer.class);
                know_status_counter_from_db = dataSnapshot.child("know_status_counter").getValue(Integer.class);
                half_know_status_counter_from_db = dataSnapshot.child("half_know_status_counter").getValue(Integer.class);
                last_word_classified_position_show_know_words = dataSnapshot.child("last_word_classified_position_show_know_words").getValue(Integer.class);
                refDb.removeEventListener(this);
                getUnitWordFromDb();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

    }

    public void change_statistics_counters(String new_status) {

        if (know_words.equals(SHOW_KNOW_WORDS)) {
            last_word_classified_position_show_know_words = words_db_pointer;

        }

        String current_status = user_statuses.get(words_db_indexes_for_this_session[words_db_pointer]);
        if (!(current_status.equals(new_status))) {
            switch (current_status) {
                case KNOWN_WORDS:
                    know_status_counter--;
                    break;
                case UNKNOWN_WORDS:
                    unknow_status_counter--;
                    break;
                case HALF_KNOWN_WORDS:
                    half_know_status_counter--;
                    break;

            }
            switch (new_status) {
                case KNOWN_WORDS:
                    know_status_counter++;
                    break;
                case UNKNOWN_WORDS:
                    unknow_status_counter++;
                    break;
                case HALF_KNOWN_WORDS:
                    half_know_status_counter++;
                    break;

            }
            user_statuses.set(words_db_indexes_for_this_session[words_db_pointer], new_status);
            set_selected_image_view_color(new_status);
        }


    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Main3Activity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


}

