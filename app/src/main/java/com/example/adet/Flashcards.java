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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Flashcards extends AppCompatActivity {

    private TextView QuestionToAnswer, invisTextView, cCounter, wCounter;
    private Button showAnswer;
    private ConstraintLayout invisConstrain;
    private ImageView wrong, correct, ekis, sidemenu;

    int counterC = 0;
    int counterW = 0;

    Random random = new Random();
    Intent theIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_flashcards);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        theIntent = getIntent();
        QuestionToAnswer = findViewById(R.id.questionToAnswer);
        showAnswer = findViewById(R.id.show_answer);
        invisConstrain = findViewById(R.id.invis_ConstLayout);
        invisTextView = findViewById(R.id.insvis_TextView);
        wrong = findViewById(R.id.img_wrong);
        correct = findViewById(R.id.img_correct);
        cCounter = findViewById(R.id.correct_counter);
        wCounter = findViewById(R.id.wrong_counter);
        ekis = findViewById(R.id.Eks);
        sidemenu = findViewById(R.id.sidemenu);

        final int[] rndIndex = {readJsonRandomIndex()};

        QuestionToAnswer.setText(""+readJsonRandomDefinition(rndIndex[0]));

        showAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String compare = QuestionToAnswer.getText().toString();
                invisConstrain.setVisibility(View.VISIBLE);

                invisTextView.setText(QuestionToAnswer.getText().toString());
                QuestionToAnswer.setText(readJsonRandomTerm(rndIndex[0]));

                if(readJsonEqualTerm(compare, rndIndex[0])){
                    wrong.setAlpha(1f);
                    correct.setAlpha(1f);
                    showAnswer.setAlpha(0.5f);
                    showAnswer.setEnabled(false);
                    wrong.setEnabled(true);
                    correct.setEnabled(true);
                }
                else {
                    System.out.println("Paano nag kamali toh");
                }
            }
        });

        wrong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counterW++;
                wCounter.setText(String.valueOf(counterW));
                rndIndex[0] = readJsonRandomIndex();
                QuestionToAnswer.setText(readJsonRandomDefinition(rndIndex[0]));
                invisConstrain.setVisibility(View.INVISIBLE);
                showAnswer.setAlpha(1f);
                wrong.setAlpha(0.5f);
                correct.setAlpha(0.5f);
                showAnswer.setEnabled(true);
                wrong.setEnabled(false);
                correct.setEnabled(false);
            }
        });

        correct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counterC++;
                cCounter.setText(String.valueOf(counterC));
                rndIndex[0] = readJsonRandomIndex();
                QuestionToAnswer.setText(readJsonRandomDefinition(rndIndex[0]));
                invisConstrain.setVisibility(View.INVISIBLE);
                showAnswer.setAlpha(1f);
                wrong.setAlpha(0.5f);
                correct.setAlpha(0.5f);
                showAnswer.setEnabled(true);
                wrong.setEnabled(false);
                correct.setEnabled(false);
            }
        });

        ekis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Flashcards.this, Exit_Notice.class);
                startActivity(intent);
            }
        });

        sidemenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Flashcards.this, Side_Menu.class);
                startActivity(intent);
                }
        });

    }
    private String readJsonRandomDefinition(int index) {
        try {
            InputStream inputStream = getAssets().open("sample.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            String json;

            json = new String(buffer, StandardCharsets.UTF_8);

            return getRandomDefinition(json, index);

        } catch (IOException e) {
            e.printStackTrace();
            return  "";
        }
    }
    private String getRandomDefinition(String jsonString, int index) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray contentArray = jsonObject.getJSONArray("Content");

            // 1. Collect all definitions into a single list
            JSONArray allDefinitions = new JSONArray();
            for (int i = 0; i < contentArray.length(); i++) {
                JSONObject sectionObject = contentArray.getJSONObject(i);
                String sectionName = sectionObject.getString("Section");

                if (sectionName.equals(theIntent.getStringExtra("title"))) {
                    JSONArray itemArray = sectionObject.getJSONArray("Item");

                    for (int j = 0; j < itemArray.length(); j++) {
                        JSONObject itemObject = itemArray.getJSONObject(j);
                        allDefinitions.put(itemObject.getString("Definition"));
                    }
                    break;
                }

            }

            // 2. Randomly select a definition from the combined list
            if (allDefinitions.length() > 0) {
                return allDefinitions.getString(index);
            } else {
                return "No definitions found.";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Error processing JSON.";
        }
    }
    private String readJsonRandomTerm(int index) {
        try {
            InputStream inputStream = getAssets().open("sample.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            String json;

            json = new String(buffer, StandardCharsets.UTF_8);

            return getRandomTerm(json, index);

        } catch (IOException e) {
            e.printStackTrace();
            return  "";
        }
    }
    private String getRandomTerm(String jsonString, int index) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray contentArray = jsonObject.getJSONArray("Content");

            // 1. Collect all definitions into a single list
            JSONArray allTerm = new JSONArray();
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

            // 2. Randomly select a definition from the combined list
            if (allTerm.length() > 0) {
                return allTerm .getString(index);
            } else {
                return "No definitions found.";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Error processing JSON.";
        }
    }
    private int readJsonRandomIndex() {
        try {
            InputStream inputStream = getAssets().open("sample.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            String json;

            json = new String(buffer, StandardCharsets.UTF_8);

            return getRandomDefinitionIndex(json);

        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }
    private int getRandomDefinitionIndex(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray contentArray = jsonObject.getJSONArray("Content");

            // 1. Collect indices of all definitions
            List<Integer> definitionIndices = new ArrayList<>();
            int indexCounter = 0;
            for (int i = 0; i < contentArray.length(); i++) {
                JSONObject sectionObject = contentArray.getJSONObject(i);
                String sectionName = sectionObject.getString("Section");

                if (sectionName.equals(theIntent.getStringExtra("title"))) {
                    JSONArray itemArray = sectionObject.getJSONArray("Item");
                    for (int j = 0; j < itemArray.length(); j++) {
                        definitionIndices.add(indexCounter);
                        indexCounter++;
                    }
                    break;
                }

            }

            // 2. Randomly select an index from the list
            if (definitionIndices.size() > 0) {
                int randomIndex = random.nextInt(definitionIndices.size());
                return definitionIndices.get(randomIndex);
            } else {
                return -1; // No definitions found
            }

        } catch (Exception e) {
            e.printStackTrace();
            return -1; // Error processing JSON
        }
    }
    private boolean readJsonEqualTerm(String ans, int index) {
        try {
            InputStream inputStream = getAssets().open("sample.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            String json;

            json = new String(buffer, StandardCharsets.UTF_8);

            return equalToIndex(json, ans, index);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    private boolean equalToIndex(String jsonString, String ans, int index) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray contentArray = jsonObject.getJSONArray("Content");

            JSONArray allItems = new JSONArray();
            for(int i = 0; i < contentArray.length(); i++) {
                JSONObject sectionObject = contentArray.getJSONObject(i);
                String sectionName = sectionObject.getString("Section");

                if (sectionName.equals(theIntent.getStringExtra("title"))) {
                    JSONArray itemArray = sectionObject.getJSONArray("Item");
                    for (int j = 0; j < itemArray.length(); j++) {
                        JSONObject itemObject = itemArray.getJSONObject(j);
                        allItems.put(itemObject.getString("Definition"));
                    }
                    break;
                }
            }
            if(ans.equalsIgnoreCase(allItems.getString(index))){
                return true;
            }
            else {
                return false;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }
}