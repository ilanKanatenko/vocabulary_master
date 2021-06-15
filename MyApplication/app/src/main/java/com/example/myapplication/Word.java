package com.example.myapplication;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Word {
    public String english_word ;
    public String hebrew_word ;

    public Word() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Word(String english_word,String hebrew_word){
        this.english_word = english_word;
        this.hebrew_word = hebrew_word;
    }
}
