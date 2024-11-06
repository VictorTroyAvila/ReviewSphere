package com.example.adet;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
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
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Quiz extends AppCompatActivity {

    private ImageView ekis, sidemenu;
    private Button submit;
    private TextView toScore;
    private LinearLayout content_Container;

    List<String> correctAnswers = new ArrayList<>();
    List<String> selectedAnswers = new ArrayList<>();
    Random random = new Random();
    Intent theIntent;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Name List");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        theIntent = getIntent();
        ekis = findViewById(R.id.Eks);
        sidemenu = findViewById(R.id.sidemenu);
        submit = findViewById(R.id.submit);
        content_Container = findViewById(R.id.content_Container);
        toScore = findViewById(R.id.toScore);

        String Fname = theIntent.getStringExtra("Fname");
        String Subject = theIntent.getStringExtra("Subject");
        String Topic = theIntent.getStringExtra("Topic");

        if (Fname != null && Subject != null && Topic != null){
            myRef.child(theIntent.getStringExtra("Fname"))
                    .child("Notebook")
                    .child(theIntent.getStringExtra("Subject"))
                    .child(theIntent.getStringExtra("Topic"))
                    .child("Items")
                    .get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            int index = (int) task.getResult().getChildrenCount();
                            ArrayList <String> termList = new ArrayList<>();
                            ArrayList <String> definitionList = new ArrayList<>();
                            for (DataSnapshot snapshot : task.getResult().getChildren()) {
                                termList.add(snapshot.getKey());
                                definitionList.add(snapshot.getValue().toString());
                            }

                            ReturnValues(termList, definitionList, index);
                        }
                    });
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int correctCount = 0;
                for (int i = 0; i < correctAnswers.size(); i++) {
                    if (correctAnswers.get(i).equals(selectedAnswers.get(i))) {
                        correctCount++;
                        toScore.setText("Score: "+correctCount);
                    }
                    else
                    {
                        System.out.println("Ala mali");
                    }
                }
            }
        });
        ekis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inflate the custom layout
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_exit_notice, null);

                // Create an AlertDialog.Builder
                AlertDialog.Builder builder = new AlertDialog.Builder(Quiz.this);

                // Set the custom view
                builder.setView(dialogView);

                // Get references to UI elements
                TextView confirmButton = dialogView.findViewById(R.id.confrim);
                TextView cancelButton = dialogView.findViewById(R.id.cancel);

                // Show the dialog
                AlertDialog dialog = builder.create();
                dialog.show();

                confirmButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Quiz.this, Home.class);
                        intent.putExtra("Fname", theIntent.getStringExtra("Fname"));
                        startActivity(intent);
                        dialog.dismiss();
                        finish();
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });
        sidemenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Quiz.this, Side_Menu.class);
                startActivity(intent);
            }
        });
    }

    private void ReturnValues(ArrayList <String> termList, ArrayList <String> definitionList, int index) {

        ArrayList<String> Defs = definitionList;
        ArrayList<String> Term = termList;

            for (int i = 0; i < Defs.size(); i++) {
                Object element = Defs.get(i);
                Object element1 = Term.get(i);
                try {
                    if (element instanceof String) {
                        String stringDef = (String) element;
                        String stringTerm = (String) element1;

                        // Add TextView
                        TextView textView = new TextView(this);
                        textView.setId(View.generateViewId());
                        textView.setText(stringDef);
                        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        textView.setTextSize(18);
                        textView.setPadding(16, 16, 16, 16);
                        textView.setBackground(getResources().getDrawable(R.drawable.rounding_corner_lite));
                        textView.setBackgroundColor(getResources().getColor(R.color.faded_purple));
                        content_Container.addView(textView);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        params.setMargins(0, 16, 0, 16);
                        textView.setLayoutParams(params);

                        //Add Radio Group with Linear Layout Params
                        RadioGroup radioGroup = new RadioGroup(this);
                        LinearLayout.LayoutParams radioGroupLayoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        radioGroup.setLayoutParams(radioGroupLayoutParams);

                        // Create Radio Buttons
                        RadioButton radioButton1 = new RadioButton(this);
                        radioButton1.setId(View.generateViewId());
                        String RB1 = String.valueOf(radioButton1.getId());

                        RadioButton radioButton2 = new RadioButton(this);
                        radioButton2.setId(View.generateViewId());
                        String RB2 = String.valueOf(radioButton2.getId());

                        RadioButton radioButton3 = new RadioButton(this);
                        radioButton3.setId(View.generateViewId());
                        String RB3 = String.valueOf(radioButton3.getId());

                        RadioButton radioButton4 = new RadioButton(this);
                        radioButton4.setId(View.generateViewId());
                        String RB4 = String.valueOf(radioButton4.getId());

                        List<String> optionsList = Shuffle(Term);
                        int correctAnswerIndex = random.nextInt(4);
                        correctAnswers.add(stringTerm);

                        // Set radio button texts, placing correct answer randomly
                        Set<Integer> usedIndices = new HashSet<>();
                        for (int j = 0; j < 4; j++) {
                            int indexx;
                            do {
                                indexx = random.nextInt(optionsList.size());
                            } while (usedIndices.contains(indexx));
                            usedIndices.add(indexx);

                            RadioButton currentButton = null;
                            switch (j) {
                                case 0: currentButton = radioButton1; break;
                                case 1: currentButton = radioButton2; break;
                                case 2: currentButton = radioButton3; break;
                                case 3: currentButton = radioButton4; break;
                            }

                            if (j == correctAnswerIndex) {
                                currentButton.setText(stringTerm); // Set correct answer
                            } else {currentButton.setText(optionsList.get(index)); // Set other options
                            }
                        }

                        selectedAnswers.add("");

                        radioButton1.setTag(i);
                        radioButton2.setTag(i);
                        radioButton3.setTag(i);
                        radioButton4.setTag(i);

                        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                RadioButton selectedRadioButton = findViewById(checkedId);
                                int questionIndex = (int) selectedRadioButton.getTag();
                                selectedAnswers.set(questionIndex, selectedRadioButton.getText().toString());
                            }
                        });

                        RadioGroup.LayoutParams radioButtonLayoutParams = new RadioGroup.LayoutParams(
                                RadioGroup.LayoutParams.WRAP_CONTENT,
                                RadioGroup.LayoutParams.WRAP_CONTENT
                        );
                        radioButton1.setLayoutParams(radioButtonLayoutParams);
                        radioButton2.setLayoutParams(radioButtonLayoutParams);
                        radioButton3.setLayoutParams(radioButtonLayoutParams);
                        radioButton4.setLayoutParams(radioButtonLayoutParams);

                        // Add RadioButtons to RadioGroup
                        radioGroup.addView(radioButton1);
                        radioGroup.addView(radioButton2);
                        radioGroup.addView(radioButton3);
                        radioGroup.addView(radioButton4);

                        content_Container.addView(radioGroup);

                    }
                } catch (IndexOutOfBoundsException e){
                    e.printStackTrace();
                }
            }
    }

    private List<String> Shuffle (ArrayList Term) {
        ArrayList<String> optionsList =new ArrayList<>();
            for (int i = 0; i < Term.size(); i++) {
                optionsList.add(Term.get(i).toString());
            }

        Collections.shuffle(optionsList);

        return optionsList;
    }
}