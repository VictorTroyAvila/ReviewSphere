package com.example.adet;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    TextView Email, Password, FullName, toSignUp;
    Button Login;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Name List");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        toSignUp = findViewById(R.id.toSignUp);
        FullName = findViewById(R.id.loginFullName);
        Email = findViewById(R.id.loginEmail);
        Password = findViewById(R.id.loginPassword);
        Login = findViewById(R.id.loginButton);

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String NameText = FullName.getText().toString();
                String EmailText = Email.getText().toString();
                String PasswordText = Password.getText().toString();

                Checking(NameText, EmailText, PasswordText, new CheckCallback() {
                    @Override
                    public void onCheckComplete(boolean exists) {
                        // Handle the result here
                        if (exists) {
                            Intent intent = new Intent(MainActivity.this, Home.class);
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                            alertDialog.setTitle("Error");
                            alertDialog.setMessage("User does not exist");
                            alertDialog.show();
                        }
                    }
                });
            }
        });

        toSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Sign_Up.class);
                startActivity(intent);
                finish();
            }

        });
    }

    private void Checking(String NameText, String EmailText, String PasswordText, CheckCallback callback) {
        myRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    boolean exist = false;
                    for (DataSnapshot childSnapshot : task.getResult().getChildren()) {
                        if (childSnapshot.getKey().equalsIgnoreCase(NameText)) {
                            if (childSnapshot.child("Email").getValue().toString().equalsIgnoreCase(EmailText)) {
                                if (childSnapshot.child("Password").getValue().toString().equals(PasswordText)) {
                                    exist = true;
                                }
                            }
                        }
                    }
                    callback.onCheckComplete(exist);
                } else {
                    callback.onCheckComplete(false);
                }
            }
        });
    }
}