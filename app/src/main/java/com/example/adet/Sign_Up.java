package com.example.adet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
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

public class Sign_Up extends AppCompatActivity {

    TextView Email, Password, FullName, tologin;
    Button SignUp;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Name List");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Email = findViewById(R.id.signUpEmail);
        Password = findViewById(R.id.signUpPassword);
        FullName = findViewById(R.id.signUpFullName);
        SignUp = findViewById(R.id.signUp);
        tologin = findViewById(R.id.toLogin);

        SignUp.setOnClickListener(v -> {
            String EmailText = Email.getText().toString().replaceAll("\\s$", "");
            String PasswordText = Password.getText().toString().replaceAll("\\s$", "");
            String FullNameText = FullName.getText().toString().replaceAll("\\s$", "");

            Checking(FullNameText, EmailText, PasswordText, new BooleanCallback() {
                @Override
                public void onCheckComplete(boolean exists) {
                    // Handle the result here
                    if (exists) {
                        AlertDialog alertDialog = new AlertDialog.Builder(Sign_Up.this).create();
                        alertDialog.setTitle("Error");
                        alertDialog.setMessage("User already exists");
                        alertDialog.show();
                    }
                    else
                    {
                        Intent intent = new Intent(Sign_Up.this, AddInfo.class);
                        intent.putExtra("email", Email.getText().toString().replaceAll("\\s$", ""));
                        intent.putExtra("password", Password.getText().toString().replaceAll("\\s$", ""));
                        intent.putExtra("fullName", FullName.getText().toString().replaceAll("\\s$", ""));
                        startActivity(intent);
                    }
                }
            });
        });

        tologin.setOnClickListener(v -> {
            Intent intent = new Intent(Sign_Up.this, MainActivity.class);
            startActivity(intent);
            finish();
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