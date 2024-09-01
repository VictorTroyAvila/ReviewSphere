package com.example.adet;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Topic_Selection extends AppCompatActivity {

    private LinearLayout content_container;
    private ImageView sidemenu;

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
        JSONArray section = readJsonSection();

        try {
            for (int i = 0; i < section.length(); i++) {
                Object element = section.get(i);
                if (element instanceof String) {
                    String stringValue = (String) element;

                    TextView textView = new TextView(this);
                    textView.setText(stringValue);
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

                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String val = theIntent.getStringExtra("title");
                            switch (val) {
                                case "Quizzes":
                                    Intent intent1 = new Intent(Topic_Selection.this, Quiz.class);
                                    intent1.putExtra("title", stringValue);
                                    startActivity(intent1);
                                    break;
                                case "Flashcards":
                                    Intent intent2 = new Intent(Topic_Selection.this, Flashcards.class);
                                    intent2.putExtra("title", stringValue);
                                    startActivity(intent2);
                                    break;
                                case "Strips":
                                    Intent intent4 = new Intent(Topic_Selection.this, Strips.class);
                                    intent4.putExtra("title", stringValue);
                                    startActivity(intent4);
                                    break;
                                case "ToF":
                                    Intent intent5 = new Intent(Topic_Selection.this, TrueOrFalse.class);
                                    intent5.putExtra("title", stringValue);
                                    startActivity(intent5);
                                    break;
                                case "Matching":
                                    Intent intent6 = new Intent(Topic_Selection.this, Matching_Type.class);
                                    intent6.putExtra("title", stringValue);
                                    startActivity(intent6);
                                    break;
                                default:
                                    break;
                            }
                        }
                    });
                }
            }
        } catch (Exception e) {

        }

        sidemenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Topic_Selection.this, Side_Menu.class);
                startActivity(intent);
            }
        });
    }
    private JSONArray readJsonSection() {
        try {
            InputStream inputStream = getAssets().open("sample.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            String json;

            json = new String(buffer, StandardCharsets.UTF_8);

            return parseJsonSection(json);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    private JSONArray parseJsonSection(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray allSections = new JSONArray();

            // Extract the "Title"
            String title = jsonObject.getString("Title");


            // Get the "Content" array
            JSONArray contentArray = jsonObject.getJSONArray("Content");

            // Iterate through the "Content" array
            for (int i = 0; i < contentArray.length(); i++) {
                JSONObject contentObject = contentArray.getJSONObject(i);

                // Extract the "Section"
                allSections.put(contentObject.getString("Section"));

            }
            return allSections;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}