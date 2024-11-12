package com.example.adet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Accounts extends AppCompatActivity {

    TextView Flashcard_plays, Matching_plays, Quiz_plays, Strips_plays, ToF_plays, Name, School, Age, Gender;

    Intent theIntent;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Name List");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_accounts);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        theIntent = getIntent();
        Flashcard_plays = findViewById(R.id.Flashcard_plays);
        Matching_plays = findViewById(R.id.Matching_plays);
        Quiz_plays = findViewById(R.id.Quiz_plays);
        Strips_plays = findViewById(R.id.Strips_plays);
        ToF_plays = findViewById(R.id.ToF_plays);
        Name = findViewById(R.id.Name);
        School = findViewById(R.id.School);
        Age = findViewById(R.id.Age);
        Gender = findViewById(R.id.Gender);

        Name.setText("Name: " + theIntent.getStringExtra("Fname"));
        myRef.child(theIntent.getStringExtra("Fname"))
                .child("User Info")
                .child("School")
                .get().addOnSuccessListener(dataSnapshot -> {
                    School.setText("School: " + dataSnapshot.getValue(String.class));
                });
        myRef.child(theIntent.getStringExtra("Fname"))
                .child("User Info")
                .child("Age")
                .get().addOnSuccessListener(dataSnapshot -> {
                    Age.setText("Age: "+dataSnapshot.getValue(String.class));
                });
        myRef.child(theIntent.getStringExtra("Fname"))
                .child("User Info")
                .child("Gender")
                .get().addOnSuccessListener(dataSnapshot -> {
                    Gender.setText(dataSnapshot.getValue(String.class));
                });

//        myRef.child(theIntent.getStringExtra("Fname"))
//                .child("User Info")
//                .child("Performance")
//                .child("FlashCards")
//                .child("No Plays")
//                .get().addOnSuccessListener(dataSnapshot -> {
//                    Flashcard_plays.setText(dataSnapshot.getValue(String.class));
//                });
//
//        myRef.child(theIntent.getStringExtra("Fname"))
//                .child("User Info")
//                .child("Performance")
//                .child("Matching")
//                .child("No Plays")
//                .get().addOnSuccessListener(dataSnapshot -> {
//                    Matching_plays.setText(dataSnapshot.getValue(String.class));
//                });
//
//        myRef.child(theIntent.getStringExtra("Fname"))
//                .child("User Info")
//                .child("Performance")
//                .child("Quizzes")
//                .child("No Plays")
//                .get().addOnSuccessListener(dataSnapshot -> {
//                    Quiz_plays.setText(dataSnapshot.getValue(String.class));
//                });
//
//        myRef.child(theIntent.getStringExtra("Fname"))
//                .child("User Info")
//                .child("Performance")
//                .child("Strips")
//                .child("No Plays")
//                .get().addOnSuccessListener(dataSnapshot -> {
//                    Strips_plays.setText(dataSnapshot.getValue(String.class));
//                });
//
//        myRef.child(theIntent.getStringExtra("Fname"))
//                .child("User Info")
//                .child("Performance")
//                .child("TrueFalse")
//                .child("No Plays")
//                .get().addOnSuccessListener(dataSnapshot -> {
//                    ToF_plays.setText(dataSnapshot.getValue(String.class));
//                });
    }
}