package com.example.adet;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Topic_Selection extends AppCompatActivity {

    private LinearLayout content_container;
    private ImageView sidemenu;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Name List");

    Intent theIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_topic_selection);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        theIntent = getIntent();
        sidemenu = findViewById(R.id.sidemenu);
        content_container = findViewById(R.id.content_container);

        myRef.child(theIntent.getStringExtra("Fname"))
                .child("Notebook")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        content_container.removeAllViews();
                        //Subject
                        for (DataSnapshot subjectSnapshot : snapshot.getChildren()) {
                            //Topic
                            for (DataSnapshot topicSnapshot : subjectSnapshot.getChildren()) {
                                String Subject = subjectSnapshot.getKey();
                                String Topic = topicSnapshot.getKey();

                                TextView textView = new TextView(Topic_Selection.this);
                                textView.setText(Subject +
                                        "\nTopic: " + Topic);
                                textView.setId(View.generateViewId());
                                textView.setTextSize(16);
                                textView.setBackground(getResources().getDrawable(R.drawable.rounding_corner));
                                textView.setBackgroundColor(getResources().getColor(R.color.faded_purple));
                                textView.setPadding(32, 32, 32, 32);

                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                );
                                params.setMargins(16, 16, 16, 16);
                                textView.setLayoutParams(params);

                                content_container.addView(textView);

                                textView.setOnClickListener(v -> {
                                    String val = theIntent.getStringExtra("title");
                                    switch (val) {
                                        case "Quizzes":
                                            Intent intent1 = new Intent(Topic_Selection.this, Quiz.class);
                                            intent1.putExtra("Fname", theIntent.getStringExtra("Fname"));
                                            intent1.putExtra("Subject", Subject);
                                            intent1.putExtra("Topic", Topic);
                                            startActivity(intent1);
                                            finish();
                                            break;
                                        case "Flashcards":
                                            Intent intent2 = new Intent(Topic_Selection.this, Flashcards.class);
                                            intent2.putExtra("Fname", theIntent.getStringExtra("Fname"));
                                            intent2.putExtra("Subject", Subject);
                                            intent2.putExtra("Topic", Topic);
                                            startActivity(intent2);
                                            finish();
                                            break;
                                        case "Strips":
                                            Intent intent3 = new Intent(Topic_Selection.this, Strips.class);
                                            intent3.putExtra("Fname", theIntent.getStringExtra("Fname"));
                                            intent3.putExtra("Subject", Subject);
                                            intent3.putExtra("Topic", Topic);
                                            startActivity(intent3);
                                            finish();
                                            break;
                                        case "ToF":
                                            Intent intent4 = new Intent(Topic_Selection.this, TrueOrFalse.class);
                                            intent4.putExtra("Fname", theIntent.getStringExtra("Fname"));
                                            intent4.putExtra("Subject", Subject);
                                            intent4.putExtra("Topic", Topic);
                                            startActivity(intent4);
                                            finish();
                                            break;
                                        case "Matching":
                                            Intent intent5 = new Intent(Topic_Selection.this, Matching_Type.class);
                                            intent5.putExtra("Fname", theIntent.getStringExtra("Fname"));
                                            intent5.putExtra("Subject", Subject);
                                            intent5.putExtra("Topic", Topic);
                                            startActivity(intent5);
                                            finish();
                                            break;
                                        default:
                                            break;
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        sidemenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Topic_Selection.this, Side_Menu.class);
                intent.putExtra("Fname", theIntent.getStringExtra("Fname"));
                startActivity(intent);
            }
        });
    }
}
