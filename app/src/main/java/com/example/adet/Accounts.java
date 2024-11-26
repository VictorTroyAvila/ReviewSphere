package com.example.adet;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Accounts extends AppCompatActivity {

    TextView Flashcard_plays, Matching_plays, Quiz_plays, Strips_plays, ToF_plays, Name, School, Age, Gender;
    Button editProfile;

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
        editProfile = findViewById(R.id.dialog_edit_profile);

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

        myRef.child(theIntent.getStringExtra("Fname"))
                .child("User Info")
                .child("Performance")
                .child("FlashCards")
                .child("No Plays")
                .get().addOnSuccessListener(dataSnapshot -> {
                    Flashcard_plays.setText("" + dataSnapshot.getValue(Long.class));
                });

        myRef.child(theIntent.getStringExtra("Fname"))
                .child("User Info")
                .child("Performance")
                .child("Matching")
                .child("No Plays")
                .get().addOnSuccessListener(dataSnapshot -> {
                    Matching_plays.setText("" + dataSnapshot.getValue(Long.class));
                });

        myRef.child(theIntent.getStringExtra("Fname"))
                .child("User Info")
                .child("Performance")
                .child("Quizzes")
                .child("No Plays")
                .get().addOnSuccessListener(dataSnapshot -> {
                    Quiz_plays.setText("" + dataSnapshot.getValue(Long.class));
                });

        myRef.child(theIntent.getStringExtra("Fname"))
                .child("User Info")
                .child("Performance")
                .child("Strips")
                .child("No Plays")
                .get().addOnSuccessListener(dataSnapshot -> {
                    Strips_plays.setText("" + dataSnapshot.getValue(Long.class));
                });

        myRef.child(theIntent.getStringExtra("Fname"))
                .child("User Info")
                .child("Performance")
                .child("TrueFalse")
                .child("No Plays")
                .get().addOnSuccessListener(dataSnapshot -> {
                    ToF_plays.setText("" + dataSnapshot.getValue(Long.class));
                });

        editProfile.setOnClickListener(view -> {
            // Inflate the custom layout
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_editprofile, null);

            // Create an AlertDialog.Builder
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            // Set the custom view
            builder.setView(dialogView);

            // Get references to UI elements
            EditText fname = dialogView.findViewById(R.id.dialog_edit_fname);
            EditText school = dialogView.findViewById(R.id.dialog_edit_school);
            EditText age = dialogView.findViewById(R.id.dialog_edit_age);
            Spinner gender = dialogView.findViewById(R.id.dialog_edit_gender);
            Button save = dialogView.findViewById(R.id.dialog_edit_save);

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                    this,
                    R.array.gender,
                    R.layout.customtext_dropdownlist_selecteditem);
            adapter.setDropDownViewResource(R.layout.customtext_dropdownlist_item);
            gender.setAdapter(adapter);

            fname.setText("" + theIntent.getStringExtra("Fname"));

            myRef.child(theIntent.getStringExtra("Fname"))
                    .child("User Info")
                    .child("School")
                    .get().addOnSuccessListener(dataSnapshot -> {
                        school.setText("" + dataSnapshot.getValue(String.class));
                    });

            myRef.child(theIntent.getStringExtra("Fname"))
                    .child("User Info")
                    .child("Age")
                    .get().addOnSuccessListener(dataSnapshot -> {
                        age.setText("" + dataSnapshot.getValue(String.class));
                    });

            myRef.child(theIntent.getStringExtra("Fname"))
                    .child("User Info")
                    .child("Gender")
                    .get().addOnSuccessListener(dataSnapshot -> {

                    });


            // Show the dialog
            AlertDialog dialog = builder.create();
            dialog.show();

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String newFname = fname.getText().toString().replaceAll("\\s$","");
                    String newSchool = school.getText().toString().replaceAll("\\s$","");
                    String newAge = age.getText().toString().replaceAll("\\s$","");
                    String newGender = gender.getSelectedItem().toString().replaceAll("\\s$","");

                    myRef.child(theIntent.getStringExtra("Fname"))
                            .child("User Info")
                            .child("School")
                            .setValue(newSchool);

                    myRef.child(theIntent.getStringExtra("Fname"))
                            .child("User Info")
                            .child("Age")
                            .setValue(newAge);

                    myRef.child(theIntent.getStringExtra("Fname"))
                            .child("User Info")
                            .child("Gender")
                            .setValue(newGender);

                    myRef.child(theIntent.getStringExtra("Fname")).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (task.isSuccessful()) {
                                Object data = task.getResult().getValue();
                                myRef.child(newFname).setValue(data);
                            }
                        }
                    });



                    dialog.dismiss();
                }
            });
        });
    }
}