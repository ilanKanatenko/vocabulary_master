package com.example.myapplication;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;

@IgnoreExtraProperties
public class User {
    public String username;
    public String password;
    public List<Unit> units;


    public User(String username, String email,int[]units_information) {
        this.username = username;
        this.password = email;
        this.units=new ArrayList<>();
        for(int i=0;i<units_information.length;i++){
            this.units.add(new Unit(units_information[i]));
        }
    }

}
