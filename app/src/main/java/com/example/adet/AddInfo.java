package com.example.adet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

import java.util.HashMap;
import java.util.Map;

public class AddInfo extends AppCompatActivity {

    TextView Age, School;
    Spinner Gender, GradeLevel;
    Button GoSignUp;

    Intent theIntent;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Name List");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_addinfo);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Age = findViewById(R.id.age);
        School = findViewById(R.id.school);
        Gender = findViewById(R.id.gender);
        GradeLevel = findViewById(R.id.grade_level);
        GoSignUp = findViewById(R.id.goSignUp);
        theIntent = getIntent();

        //Gender spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.gender,
                R.layout.customtext_dropdownlist_selecteditem);
        adapter.setDropDownViewResource(R.layout.customtext_dropdownlist_item);
        Gender.setAdapter(adapter);

        //Grade Level spinner
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
                this,
                R.array.gradelevel,
                R.layout.customtext_dropdownlist_selecteditem);
        adapter2.setDropDownViewResource(R.layout.customtext_dropdownlist_item);
        GradeLevel.setAdapter(adapter2);

        GoSignUp.setOnClickListener(view -> {

            String EmailText = theIntent.getStringExtra("email");
            String PasswordText = theIntent.getStringExtra("password");
            String FullNameText = theIntent.getStringExtra("fullName");
            String AgeText = Age.getText().toString();
            String SchoolText = School.getText().toString();
            String GenderText = Gender.getSelectedItem().toString();
            String GradeLevelText = GradeLevel.getSelectedItem().toString();

            Map<String, Object> gameStats = new HashMap<>();
            gameStats.put("No Plays", 0);
            gameStats.put("Avg  Score", 0);

            Map<String, Object> games = new HashMap<>();
            games.put("Quizzes", gameStats);
            games.put("FlashCards", gameStats);
            games.put("Strips", gameStats);
            games.put("TrueFalse", gameStats);
            games.put("Matching", gameStats);

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("Age", AgeText);
            userInfo.put("Gender", GenderText);
            userInfo.put("School", SchoolText);
            userInfo.put("Grade Level", GradeLevelText);
            userInfo.put("Performance", games);

            Map<String, Object> userCreds = new HashMap<>();
            userCreds.put("Email", EmailText);
            userCreds.put("Password", PasswordText);
            userCreds.put("User Info", userInfo);

            Checking(FullNameText, EmailText, PasswordText, new BooleanCallback() {
                @Override
                public void onCheckComplete(boolean exists) {
                    // Handle the result here
                    if (exists) {
                        AlertDialog alertDialog = new AlertDialog.Builder(AddInfo.this).create();
                        alertDialog.setTitle("Error");
                        alertDialog.setMessage("User already exists");
                        alertDialog.show();
                    }
                    else
                    {
                        myRef.child(FullNameText).setValue(userCreds);
                        Intent intent = new Intent(AddInfo.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        });
    }

    private void Checking(String NameText, String EmailText, String PasswordText, BooleanCallback booleanCallback) {
        myRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    boolean exist = false;
                    for (DataSnapshot childSnapshot : task.getResult().getChildren()) {
                        if (childSnapshot.getKey().equalsIgnoreCase(NameText) || childSnapshot.child("Email").getValue().toString().equalsIgnoreCase(EmailText) || childSnapshot.child("Password").getValue().toString().equalsIgnoreCase(PasswordText))
                        {
                            exist = true;
                        }
                    }
                    booleanCallback.onCheckComplete(exist);
                } else {
                    booleanCallback.onCheckComplete(false); // Handle error
                }
            }
        });
    }
}