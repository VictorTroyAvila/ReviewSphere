package com.example.adet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import java.util.ArrayList;
import java.util.List;

public class Notebook extends AppCompatActivity {

    private LinearLayout content_Container;
    private ImageView goto_sidemenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notebook);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        content_Container = findViewById(R.id.item_list);
        goto_sidemenu = findViewById(R.id.sidemenu);
        goto_sidemenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Notebook.this, Side_Menu.class);
                startActivity(intent);
            }
        });

        String title = readJsonTitle();
        JSONArray section = readJsonSection();
        int noTerms;

        try {
            for (int i = 0; i < section.length(); i++) {
                Object element = section.get(i);
                noTerms = readJsonNoTerms(i);
                if (element instanceof String) {
                    String stringValue = (String) element;

                    TextView textView = new TextView(this);
                    textView.setText("Title: " + stringValue +
                            "\n Items: " + noTerms +
                            "\n Tag: " + title);
                    textView.setId(View.generateViewId());
                    textView.setTextSize(16);
                    textView.setBackground(getResources().getDrawable(R.drawable.rounding_corner));
                    textView.setBackgroundColor(getResources().getColor(R.color.faded_purple));
                    textView.setPadding(32, 32, 32, 32);
                    content_Container.addView(textView);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(16, 16, 16, 16);
                    textView.setLayoutParams(params);

                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Notebook.this, Notebook_Data.class);
                            intent.putExtra("title", stringValue);
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            }
        } catch (JSONException e) {
            // Handle exception
        }


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
    private int readJsonNoTerms(int index) {
        try {
            InputStream inputStream = getAssets().open("sample.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            String json;

            json = new String(buffer, StandardCharsets.UTF_8);

            return parseJsonNoTerms(json, index);

        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }
    private int parseJsonNoTerms(String jsonString, int index) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray contentArray = jsonObject.getJSONArray("Content");

            List<Integer> definitionIndices = new ArrayList<>();
            int indexCounter = 0;
            for (int i = 0; i < contentArray.length(); i++) {
                JSONObject sectionObject = contentArray.getJSONObject(i);
                String sectionName = sectionObject.getString("Section");
                JSONArray itemArray = sectionObject.getJSONArray("Item");
                int termCount = itemArray.length();
                definitionIndices.add(termCount);
                indexCounter ++;
            }

            return definitionIndices.get(index);

        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }
    }
    private String readJsonTitle() {
        try {
            InputStream inputStream = getAssets().open("sample.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            String json;

            json = new String(buffer, StandardCharsets.UTF_8);

            return parseJsonTitle(json);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    private String parseJsonTitle(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);

            // Extract the "Title"
            String title = jsonObject.getString("Title");

            return title;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}