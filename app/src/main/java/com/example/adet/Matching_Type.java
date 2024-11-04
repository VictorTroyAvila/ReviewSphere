package com.example.adet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Matching_Type extends AppCompatActivity {

    private ImageView ekis, sidemenu;
    private TableLayout tableLayout;
    private TextView Selected1, Selected2;
    private Button selectedButton1 = null;
    private Button selectedButton2 = null;

    private int numMatches = 0;
    List<String> termsList = new ArrayList<>();List<String> defsList = new ArrayList<>();
    List<String> allOptions = new ArrayList<>();

    Intent theIntent;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Name List");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_matching_type);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        theIntent = getIntent();
        ekis = findViewById(R.id.Eks);
        sidemenu = findViewById(R.id.sidemenu);
        tableLayout = findViewById(R.id.tableLayout);
        Selected1 = findViewById(R.id.Selected1);
        Selected2 = findViewById(R.id.Selected2);

        JSONArray Terms = readJsonTerm();
        JSONArray Defs = readJsonDef();

        try {
            for (int i = 0; i < Terms.length(); i++) {
                termsList.add(Terms.getString(i));
                allOptions.add(Terms.getString(i));
            }
            for (int i = 0; i< Defs.length(); i++) {
                defsList.add(Defs.getString(i));
                allOptions.add(Defs.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Collections.shuffle(allOptions); // Shuffle all options
        numMatches = Terms.length(); // Number of matches needed to win

        int numButtons = allOptions.size();
        int numRows = (int) Math.ceil((double) numButtons / 5); // Calculate number of rows

        try {
            for (int i = 0; i < numRows; i++) {
                TableRow tableRow = new TableRow(this);
                tableRow.setPadding(16, 16, 16, 16);
                tableLayout.addView(tableRow);

                for (int j = 0; j < 5 && (i * 5 + j) < numButtons; j++) {
                    Button button = new Button(this);
                    button.setText(" "); // Initially hide the text
                    button.setTag(allOptions.get(i * 5 +j)); // Store the actual text in the tag
                    button.setBackground(getResources().getDrawable(R.drawable.planet_3__1_));

                    int widthInDp = 75;
                    int heightInDp = 75;
                    float scale = getResources().getDisplayMetrics().density;
                    int widthInPixels = (int) (widthInDp * scale + 0.5f);
                    int heightInPixels = (int) (heightInDp * scale + 0.5f);
                    TableRow.LayoutParams buttonLayoutParams = new TableRow.LayoutParams(widthInPixels, heightInPixels);
                    button.setLayoutParams(buttonLayoutParams);

                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            handleButtonClick((Button) v);
                        }
                    });

                    tableRow.addView(button);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ekis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Matching_Type.this, Exit_Notice.class);
                startActivity(intent);
                finish();
            }
        });
        sidemenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Matching_Type.this, Side_Menu.class);
                startActivity(intent);
            }
        });
    }
    private void handleButtonClick(Button button) {
        Selected1.setVisibility(View.VISIBLE);
        Selected2.setVisibility(View.VISIBLE);
        Selected2.setText(Selected1.getText());
        Selected1.setText(button.getTag().toString());

        if (selectedButton1 == null) {
            selectedButton1 = button;
        } else {
            selectedButton2 = button;
            checkMatch();
        }
    }

    private void checkMatch() {
        String text1 = selectedButton1.getTag().toString();
        String text2 = selectedButton2.getTag().toString();

        if ((termsList.contains(text1) && defsList.contains(text2) && defsList.indexOf(text2) == termsList.indexOf(text1)) ||
                (termsList.contains(text2) && defsList.contains(text1) && defsList.indexOf(text1) == termsList.indexOf(text2))) {

            selectedButton1.setEnabled(false);
            selectedButton2.setEnabled(false);
            selectedButton1.setAlpha(0.5f);
            selectedButton2.setAlpha(0.5f);
            numMatches--;

            if (numMatches == 0) {
                Selected1.setText("All");
                Selected2.setText("Complete");
            }
        } else {
            selectedButton1.postDelayed(new Runnable() {
                @Override
                public void run() {
                    selectedButton1 = null;
                    selectedButton2 = null;
                }
            }, 1000); // 1-second delay
        }
    }
    private JSONArray readJsonTerm() {
        try {
            InputStream inputStream = getAssets().open("sample.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            String json;

            json = new String(buffer, StandardCharsets.UTF_8);

            return parseJsonTerm(json);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    private JSONArray parseJsonTerm(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray allTerm = new JSONArray();

            // Extract the "Title"
            String title = jsonObject.getString("Title");


            // Get the "Content" array
            JSONArray contentArray = jsonObject.getJSONArray("Content");

            // Iterate through the "Content" array
            for (int i = 0; i < contentArray.length(); i++) {
                JSONObject sectionObject = contentArray.getJSONObject(i);
                String sectionName = sectionObject.getString("Section");

                if (sectionName.equals(theIntent.getStringExtra("title"))) {
                    JSONArray itemArray = sectionObject.getJSONArray("Item");
                    for (int j = 0; j < itemArray.length(); j++) {
                        JSONObject itemObject = itemArray.getJSONObject(j);
                        allTerm.put(itemObject.getString("Term"));
                    }
                    break;
                }


            }
            return allTerm;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private JSONArray readJsonDef() {
        try {
            InputStream inputStream = getAssets().open("sample.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            String json;

            json = new String(buffer, StandardCharsets.UTF_8);

            return parseJsonDef(json);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    private JSONArray parseJsonDef(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray allDef = new JSONArray();

            // Extract the "Title"
            String title = jsonObject.getString("Title");


            // Get the "Content" array
            JSONArray contentArray = jsonObject.getJSONArray("Content");

            // Iterate through the "Content" array
            for (int i = 0; i < contentArray.length(); i++) {
                JSONObject sectionObject = contentArray.getJSONObject(i);
                String sectionName = sectionObject.getString("Section");

                if (sectionName.equals(theIntent.getStringExtra("title"))) {
                    JSONArray itemArray = sectionObject.getJSONArray("Item");
                    for (int j = 0; j < itemArray.length(); j++) {
                        JSONObject itemObject = itemArray.getJSONObject(j);
                        allDef.put(itemObject.getString("Definition"));
                    }
                    break;
                }


            }
            return allDef;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private List<String> Shuffle (JSONArray jsonArray) {
        List<String> optionsList =new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                optionsList.add(jsonArray.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Collections.shuffle(optionsList);

        return optionsList;
    }

}