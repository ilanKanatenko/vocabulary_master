package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;

public class MainActivity4 extends AppCompatActivity {

    EditText user_name_view;
    EditText password_view;
    Button register;
    Button login;
    FirebaseDatabase database;
    DatabaseReference myRef;
    User new_user;
    int[] units_information;
    String user_name;
    String user_password;
    boolean user_exist = false;
    boolean user_register = false;
    String user_login_db_password = "";
    boolean valid_data_entered = false;
    TextView loading_text;
    ImageView loading_img;
    ObjectAnimator mover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        user_name_view = (EditText) findViewById(R.id.username);
        password_view = (EditText) findViewById(R.id.password);
        register = (Button) findViewById(R.id.register);
        login = (Button) findViewById(R.id.login);
        loading_img = findViewById(R.id.loading_view);
        loading_text = findViewById(R.id.loading_text);
        loading_img.setVisibility(View.INVISIBLE);
        loading_text.setVisibility(View.INVISIBLE);
        database = FirebaseDatabase.getInstance();

        //option 2
        //get_units_information();


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_name = user_name_view.getText().toString();
                user_register = true;
                user_password = password_view.getText().toString();
                checkDataEntered();
            }


        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_name = user_name_view.getText().toString();
                user_register = false;
                user_password = password_view.getText().toString();
                checkDataEntered();
            }


        });

    }


    public void prepare_data_for_main_menu() {

        myRef = database.getReference("Users").child(user_name).child("units");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SharedPreferences sp = getSharedPreferences("MyApplication", 0);
                final SharedPreferences.Editor sedt = sp.edit();
                sedt.putInt("quantity_of_user_units", (int) dataSnapshot.getChildrenCount());
                sedt.putString("user_name", user_name);
                sedt.commit();
                myRef.removeEventListener(this);
                move_to_main_menu();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
    }

    public void move_to_main_menu() {
        loading_img.setVisibility(View.INVISIBLE);
        loading_text.setVisibility(View.INVISIBLE);
        mover.cancel();
        Intent intent = new Intent(MainActivity4.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    public void validate_user_name_in_db() {
        myRef = database.getReference("Users").child(user_name);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user_exist = dataSnapshot.hasChildren();
                if (user_exist) {
                    user_login_db_password = dataSnapshot.child("password").getValue(String.class);
                }
                ///סידר את הבעיה של מחיקת ההאזנה רק ככה למחוק מאזין
                myRef.removeEventListener(this);
                validate_user_name_result(user_exist, user_register);
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }

        });

    }

    public void validate_user_name_result(boolean user_exist, boolean user_register) {
        if (user_register) {
            if (user_exist) {
                loading_img.setVisibility(View.INVISIBLE);
                loading_text.setVisibility(View.INVISIBLE);
                mover.cancel();
                Toast.makeText(MainActivity4.this, "שם המשתמש: " + user_name + " כבר תפוס\nאנא בחר אחד אחר", Toast.LENGTH_LONG).show();
            } else {
                //option1
                get_units_information();
                //option 2
                //register_new_user();
            }
        } else if (!user_register) {
            if (user_exist && user_password.equals(user_login_db_password)) {
                Toast.makeText(MainActivity4.this, "התחברת בהצלחה", Toast.LENGTH_SHORT).show();
                prepare_data_for_main_menu();
            } else {
                loading_img.setVisibility(View.INVISIBLE);
                loading_text.setVisibility(View.INVISIBLE);
                mover.cancel();
                Toast.makeText(MainActivity4.this, "שם משתמש או סיסמא שגויים", Toast.LENGTH_LONG).show();

            }
        }
    }


    public void register_new_user() {

        new_user = new User(user_name, password_view.getText().toString(), units_information);
        myRef = database.getReference("Users").child(user_name);
        myRef.setValue(new_user);
        Toast.makeText(MainActivity4.this, "נרשמת בהצלחה", Toast.LENGTH_SHORT).show();
        prepare_data_for_main_menu();
    }


    public void get_units_information() {
        // Read from the database
        myRef = database.getReference("יחידות לימוד").child("מידע על יחידות לימוד");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int amount_of_units = (int) dataSnapshot.getChildrenCount();
                String builder_string = "יחידה ";
                units_information = new int[amount_of_units];
                for (int i = 0; i < amount_of_units; i++) {
                    units_information[i] = Integer.parseInt(dataSnapshot.child("" + i).getValue(String.class));
                }
                myRef.removeEventListener(this);
                //option1
                register_new_user();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
    }


    public boolean is_user_name(String user_name) {
        return (!TextUtils.isEmpty(user_name) && Pattern.compile("[a-zA-Z0-9א-ת]+(_[a-zA-Z0-9א-ת]+)*").matcher(user_name).matches());
    }

    public boolean is_empty(String str) {
        return TextUtils.isEmpty(str);
    }

    public void checkDataEntered() {
        valid_data_entered = true;

        if (is_empty(user_password) == true) {
            password_view.setError("אנא הזן סיסמא!");
            valid_data_entered = false;
        }

        if (is_user_name(user_name) == false) {
            user_name_view.setError("אנא הזן שם משתמש שמכיל אותיות ומספרים בלבד\nבאפשרותך להוסיף _ בין מילים");
            valid_data_entered = false;
        }

        if (valid_data_entered) {
            start_animation();
            validate_user_name_in_db();
        }
    }


    @Override
    public void onBackPressed() {
        final Context context = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

//        builder.setTitle("האם אתה בטוח שברצונך לצאת מהאפליקציה? ");
        builder.setMessage("האם אתה בטוח שברצונך לצאת מהאפליקציה?");
        builder.setCancelable(true);

        builder.setPositiveButton("כן", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Do something when user want to exit the app
                // Let allow the system to handle the event, such as exit the app
                MainActivity4.super.onBackPressed();
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


    public void start_animation() {
        loading_img.setVisibility(View.VISIBLE);
        loading_text.setVisibility(View.VISIBLE);
        mover = ObjectAnimator.ofFloat(loading_img, "rotation", 0f, 360f);
        mover.setDuration(1500);
        mover.setRepeatCount(ValueAnimator.INFINITE);
        mover.start();

//        RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        rotate.setDuration(1500);
//        rotate.setInterpolator(new LinearInterpolator());
//        rotate.setRepeatCount(-1);
//        loading_img.startAnimation(rotate);
////        animatorSet.cancel();

    }


}
