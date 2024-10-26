package com.example.adet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Sign_Up extends AppCompatActivity {

    TextView Email, Password, FullName, toAddInfo;
    Button SignUp;

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
        toAddInfo = findViewById(R.id.toAddInfo);

        SignUp.setOnClickListener(v -> {
            Intent intent = new Intent(Sign_Up.this, MainActivity.class);
            intent.putExtra("email", Email.getText().toString());
            intent.putExtra("password", Password.getText().toString());
            intent.putExtra("fullName", FullName.getText().toString());
            startActivity(intent);
            finish();
        });

        toAddInfo.setOnClickListener(v -> {
            Intent intent = new Intent(Sign_Up.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}