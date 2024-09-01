package com.example.adet;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Notebook_Data extends AppCompatActivity {

    private TextView sectionTitle;
    private LinearLayout content_Container;

    Intent theintent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notebook_data);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        theintent = getIntent();
        content_Container = findViewById(R.id.content_Container);
        sectionTitle = findViewById(R.id.section_Title);
        sectionTitle.setText(theintent.getStringExtra("title"));
        readJson();
    }

    private void readJson() {
        try {
            InputStream inputStream = getAssets().open("sample.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            String json;

            json = new String(buffer, StandardCharsets.UTF_8);

            parseJson(json);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void parseJson(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray contentArray = jsonObject.getJSONArray("Content");

            for (int i = 0; i < contentArray.length(); i++) {
                JSONObject sectionObject = contentArray.getJSONObject(i);
                String sectionName = sectionObject.getString("Section");

                if (sectionName.equals(theintent.getStringExtra("title"))) {
                    JSONArray itemArray = sectionObject.getJSONArray("Item");

                    for (int j = 0; j < itemArray.length(); j++) {
                        JSONObject itemObject = itemArray.getJSONObject(j);
                        String term = itemObject.getString("Term");
                        String definition = itemObject.getString("Definition");

                        TextView termView = new TextView(this);
                        TextView definitionView = new TextView(this);

                        //Edit term TextView
                        termView.setText(term);
                        termView.setId(View.generateViewId());
                        termView.setTextSize(20);
                        termView.setTypeface(null, android.graphics.Typeface.BOLD);
                        termView.setGravity(Gravity.CENTER);
                        termView.setBackground(getResources().getDrawable(R.drawable.rounding_corner));
                        termView.setBackgroundColor(getResources().getColor(R.color.faded_purple));
                        termView.setPadding(32, 32, 32, 32);

                        //Edit definition TextView
                        definitionView.setText(definition);
                        definitionView.setId(View.generateViewId());
                        definitionView.setTextSize(16);
                        definitionView.setBackground(getResources().getDrawable(R.drawable.rounding_corner));
                        definitionView.setBackgroundColor(getResources().getColor(R.color.faded_purple));
                        definitionView.setPadding(30,30,30,30);

                        //Set Term Parameters
                        LinearLayout.LayoutParams termparams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        termparams.setMargins(16, 16, 16, 0);
                        termView.setLayoutParams(termparams);

                        //Set Definition Parameters
                        LinearLayout.LayoutParams definitionparams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        definitionparams.setMargins(16, 0, 16, 16);
                        definitionView.setLayoutParams(definitionparams);

                        content_Container.addView(termView);
                        content_Container.addView(definitionView);
                    }
                    break; // Stop searching once the section is found
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}