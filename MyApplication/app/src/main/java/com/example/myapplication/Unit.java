package com.example.myapplication;

        import com.google.firebase.database.IgnoreExtraProperties;

        import java.util.ArrayList;
        import java.util.List;

@IgnoreExtraProperties
public class Unit {
    public List<String> status;
    public int unknow_status_counter;
    public int know_status_counter;
    public int half_know_status_counter;
    public int last_word_classified_position_show_know_words;

//    public User() {
//        // Default constructor required for calls to DataSnapshot.getValue(User.class)
//    }

    public Unit(int unit_length) {
        this.status=new ArrayList<>();
        for(int i=0;i<unit_length;i++){
            this.status.add("לא סווג");
        }
    }

}
