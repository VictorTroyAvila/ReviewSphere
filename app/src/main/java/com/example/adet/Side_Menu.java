package com.example.adet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Side_Menu extends AppCompatActivity {

    private TextView click_home, click_notebook, click_quizzes, click_flashcards, click_wheel, click_strips, click_ToF, click_matching;
    private ImageView sidemenu_return;

    Intent theIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_side_menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        theIntent = getIntent();
        Intent intent = new Intent(Side_Menu.this, Topic_Selection.class);

        click_home = findViewById(R.id.sidemenu_home);
        click_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeIntent = new Intent(Side_Menu.this, Home.class);
                homeIntent.putExtra("Fname", theIntent.getStringExtra("Fname"));
                startActivity(homeIntent);
                finish();
            }
        });

        click_notebook = findViewById(R.id.sidemenu_Notebook);
        click_notebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent notebookIntent = new Intent(Side_Menu.this, Notebook.class);
                notebookIntent.putExtra("Fname", theIntent.getStringExtra("Fname"));
                startActivity(notebookIntent);
                finish();
            }
        });

        click_quizzes = findViewById(R.id.sidemenu_Quizzes);
        click_quizzes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("title", "Quizzes");
                intent.putExtra("Fname", theIntent.getStringExtra("Fname"));
                startActivity(intent);
                finish();
            }
        });

        click_flashcards = findViewById(R.id.sidemenu_Flashcards);
        click_flashcards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("title", "Flashcards");
                intent.putExtra("Fname", theIntent.getStringExtra("Fname"));
                startActivity(intent);
                finish();
            }
        });

        click_strips = findViewById(R.id.sidemenu_Strips);
        click_strips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("title", "Strips");
                intent.putExtra("Fname", theIntent.getStringExtra("Fname"));
                startActivity(intent);
                finish();
            }
        });

        click_ToF = findViewById(R.id.sidemenu_ToF);
        click_ToF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("title", "ToF");
                intent.putExtra("Fname", theIntent.getStringExtra("Fname"));
                startActivity(intent);
                finish();
            }
        });

        click_matching = findViewById(R.id.sidemenu_Matching);
        click_matching.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("title", "Matching");
                intent.putExtra("Fname", theIntent.getStringExtra("Fname"));
                startActivity(intent);
                finish();
            }
        });

        sidemenu_return = findViewById(R.id.sidemenu);
        sidemenu_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}