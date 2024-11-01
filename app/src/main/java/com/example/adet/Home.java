package com.example.adet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Home extends AppCompatActivity {

    private ImageView click_menu;

    private ImageView FlashCards, Notebook, Quizzes;

    Intent theIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        theIntent = getIntent();

        click_menu = findViewById(R.id.sidemenu);
        click_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Side_Menu.class);
                intent.putExtra("Fname", theIntent.getStringExtra("Fname"));
                startActivity(intent);
            }
        });

        FlashCards = findViewById(R.id.img_Flashcards);
        FlashCards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Topic_Selection.class);
                intent.putExtra("title", "Flashcards");
                intent.putExtra("Fname", theIntent.getStringExtra("Fname"));
                startActivity(intent);
            }
        });

        Notebook = findViewById(R.id.img_Notebook);
        Notebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Notebook.class);
                intent.putExtra("Fname", theIntent.getStringExtra("Fname"));
                startActivity(intent);
            }
        });

        Quizzes = findViewById(R.id.img_Quizzes);
        Quizzes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Topic_Selection.class);
                intent.putExtra("title", "Quizzes");
                intent.putExtra("Fname", theIntent.getStringExtra("Fname"));
                startActivity(intent);
            }
        });
    }
}