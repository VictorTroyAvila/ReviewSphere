package com.example.adet;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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

public class TrueOrFalse extends AppCompatActivity {

    private ImageView ekis, sidemenu;
    private TextView QuestionAnswer, cCounter, wCounter;
    private Button Green, Red;

    private int counterC = 0;
    private int counterW = 0;
    String Term;
    String Definition;

    Random random = new Random();
    Intent theIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_true_or_false);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        theIntent = getIntent();
        ekis = findViewById(R.id.Eks);
        sidemenu = findViewById(R.id.sidemenu);
        cCounter = findViewById(R.id.cCounter);
        wCounter = findViewById(R.id.wCounter);
        QuestionAnswer = findViewById(R.id.questionNanswer);
        Green = findViewById(R.id.Green);
        Red = findViewById(R.id.Red);

        final int[] rndIndex = {readJsonRandomIndex()};
        final int[] rndIndex2 = {readJsonRandomIndex()};

        Term = readJsonRandomTerm(rndIndex2[0]);
        Definition = readJsonRandomDefinition(rndIndex[0]);

        QuestionAnswer.setText("Is "+Term+"\n \n"+Definition);

        Green.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (readJsonEqualTerm(rndIndex2[0], rndIndex[0], Term, Definition)) {
                    counterC++;
                    cCounter.setText(String.valueOf(counterC));
                    rndIndex[0] = readJsonRandomIndex();
                    rndIndex2[0] = readJsonRandomIndex();
                    Term = readJsonRandomTerm(rndIndex2[0]);
                    Definition = readJsonRandomDefinition(rndIndex[0]);
                    QuestionAnswer.setText("Is "+Term+"\n \n"+Definition);
                }
                else {
                    counterW++;
                    wCounter.setText(String.valueOf(counterW));
                    rndIndex[0] = readJsonRandomIndex();
                    rndIndex2[0] = readJsonRandomIndex();
                    Term = readJsonRandomTerm(rndIndex2[0]);
                    Definition = readJsonRandomDefinition(rndIndex[0]);
                    QuestionAnswer.setText("Is "+Term+"\n \n"+Definition);
                }
            }
        });
        Red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (readJsonEqualTerm(rndIndex2[0], rndIndex[0], Term, Definition)) {
                    counterW++;
                    wCounter.setText(String.valueOf(counterW));
                    rndIndex[0] = readJsonRandomIndex();
                    rndIndex2[0] = readJsonRandomIndex();
                    Term = readJsonRandomTerm(rndIndex2[0]);
                    Definition = readJsonRandomDefinition(rndIndex[0]);
                    QuestionAnswer.setText("Is "+Term+"\n \n"+Definition);
                }
                else {
                    counterC++;
                    cCounter.setText(String.valueOf(counterC));
                    rndIndex[0] = readJsonRandomIndex();
                    rndIndex2[0] = readJsonRandomIndex();
                    Term = readJsonRandomTerm(rndIndex2[0]);
                    Definition = readJsonRandomDefinition(rndIndex[0]);
                    QuestionAnswer.setText("Is "+Term+"\n \n"+Definition);
                }
            }
        });
        ekis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrueOrFalse.this, Exit_Notice.class);
                startActivity(intent);
            }
        });
        sidemenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrueOrFalse.this, Side_Menu.class);
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
    private boolean readJsonEqualTerm(int index2, int index, String Term, String Definition) {
        try {
            InputStream inputStream = getAssets().open("sample.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            String json;

            json = new String(buffer, StandardCharsets.UTF_8);

            return equalToIndex(json, index2, index, Term, Definition);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    private boolean equalToIndex(String jsonString, int index2, int index, String Term, String Definition) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray contentArray = jsonObject.getJSONArray("Content");

            JSONArray allDef = new JSONArray();
            JSONArray allTerm = new JSONArray();
            for(int i = 0; i < contentArray.length(); i++) {
                JSONObject sectionObject = contentArray.getJSONObject(i);
                String sectionName = sectionObject.getString("Section");

                if (sectionName.equals(theIntent.getStringExtra("title"))) {
                    JSONArray itemArray = sectionObject.getJSONArray("Item");
                    for (int j = 0; j < itemArray.length(); j++) {
                        JSONObject itemObject = itemArray.getJSONObject(j);
                        allDef.put(itemObject.getString("Definition"));
                        allTerm.put(itemObject.getString("Term"));
                    }
                    break;
                }
            }
            if(Term.equals(allTerm.getString(index)) && Definition.equals(allDef.getString(index2))){
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