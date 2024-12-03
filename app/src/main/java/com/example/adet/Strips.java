package com.example.adet;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Strips extends AppCompatActivity {

    private ImageView ekis, sidemenu, correct, wrong;
    private ImageView s1, s2, s3, s4, s5, s6, s7;
    private TextView test, stripQuestion, shownAnswer, WCounter, CCounter;
    private ConstraintLayout invisConstrain;
    private Button showAnswer;

    private int cCounter = 0;
    private int wCounter = 0;
    int counting = 0;
    int limit = 0;
    Intent theIntent;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Name List");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_strips);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        theIntent = getIntent();
        ekis = findViewById(R.id.Eks);
        sidemenu = findViewById(R.id.sidemenu);
        test = findViewById(R.id.textView4);
        invisConstrain = findViewById(R.id.constraintLayout4);
        stripQuestion = findViewById(R.id.stripQuestion);
        showAnswer = findViewById(R.id.showAnswer);
        shownAnswer = findViewById(R.id.shownAnswer);
        correct = findViewById(R.id.correct1);
        wrong = findViewById(R.id.wrong1);
        CCounter = findViewById(R.id.Ccounter);
        WCounter = findViewById(R.id.Wcounter);

        s1 = findViewById(R.id.strip1);
        s2 = findViewById(R.id.strip2);
        s3 = findViewById(R.id.strip3);
        s4 = findViewById(R.id.strip4);
        s5 = findViewById(R.id.strip5);
        s6 = findViewById(R.id.strip6);
        s7 = findViewById(R.id.strip7);

        ekis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inflate the custom layout
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_exit_notice, null);

                // Create an AlertDialog.Builder
                AlertDialog.Builder builder = new AlertDialog.Builder(Strips.this);

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
                        Intent intent = new Intent(Strips.this, Home.class);
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
                Intent intent = new Intent(Strips.this, Side_Menu.class);
                startActivity(intent);
            }
        });

        String Fname = theIntent.getStringExtra("Fname");
        String Subject = theIntent.getStringExtra("Subject");
        String Topic = theIntent.getStringExtra("Topic");

        if (Fname != null && Subject != null && Topic != null){
            String game = "Strips";
            Achievements achievements = new Achievements(game, theIntent);
            achievements.getAchievement(game, Fname, new StringCallback() {
                @Override
                public void onStringRetrieved(String achievement) {
                    if (achievement != null) {
                        myRef.child(Fname)
                                .child("Achievements")
                                .child(game)
                                .child(achievement)
                                .setValue(true);

                        // Inflate the custom layout
                        LayoutInflater inflater = getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.dialog_msg, null);

                        // Create an AlertDialog.Builder
                        AlertDialog.Builder builder = new AlertDialog.Builder(Strips.this);

                        // Set the custom view
                        builder.setView(dialogView);

                        // Get references to UI elements
                        TextView msg = dialogView.findViewById(R.id.textView25);
                        msg.setText("Congratulations! You have earned the " + achievement + " Title!");

                        // Show the dialog
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
            });

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

        correct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cCounter++;
                CCounter.setText(String.valueOf(cCounter));
                invisConstrain.setVisibility(View.INVISIBLE);
                showAnswer.setVisibility(View.INVISIBLE);
                shownAnswer.setVisibility(View.INVISIBLE);
                correct.setAlpha(0.5f);
                wrong.setAlpha(0.5f);
                correct.setClickable(false);
                wrong.setClickable(false);
                s1.setClickable(true);
                s2.setClickable(true);
                s3.setClickable(true);
                s4.setClickable(true);
                s5.setClickable(true);
                s6.setClickable(true);
                s7.setClickable(true);
                increaseCount(limit);
            }
        });
        wrong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wCounter++;
                WCounter.setText(String.valueOf(wCounter));
                invisConstrain.setVisibility(View.INVISIBLE);
                showAnswer.setVisibility(View.INVISIBLE);
                shownAnswer.setVisibility(View.INVISIBLE);
                correct.setAlpha(0.5f);
                wrong.setAlpha(0.5f);
                correct.setClickable(false);
                wrong.setClickable(false);
                s1.setClickable(true);
                s2.setClickable(true);
                s3.setClickable(true);
                s4.setClickable(true);
                s5.setClickable(true);
                s6.setClickable(true);
                s7.setClickable(true);
                increaseCount(limit);
            }
        });

    }
    private void ReturnValues(ArrayList<String> termList, ArrayList<String> defList, int index) {
        ArrayList Defs = getDefs(defList);
        ArrayList Term = getTerms(termList);

        try {
            for (int i = 0; i < Defs.size(); i++) {
                Object element = Defs.get(i);
                Object element1 = Term.get(i);
                limit++;
                if (element instanceof String) {
                    String stringValue = (String) element;
                    String stringValue1 = (String) element1;
                    if (i == 0) {
                        s1.setVisibility(View.VISIBLE);
                        s1.setClickable(true);
                        s1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                invisConstrain.setVisibility(View.VISIBLE);
                                test.setVisibility(View.INVISIBLE);
                                showAnswer.setVisibility(View.VISIBLE);
                                s1.setVisibility(View.INVISIBLE);
                                s1.setClickable(false);
                                s2.setClickable(false);
                                s3.setClickable(false);
                                s4.setClickable(false);
                                s5.setClickable(false);
                                s6.setClickable(false);
                                s7.setClickable(false);
                                showAnswer.setClickable(true);
                                showAnswer.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showAnswer.setBackgroundColor(getResources().getColor(R.color.blue_green));
                                        showAnswer.setClickable(false);
                                        shownAnswer.setVisibility(View.VISIBLE);
                                        correct.setAlpha(1f);
                                        wrong.setAlpha(1f);
                                        correct.setClickable(true);
                                        wrong.setClickable(true);
                                        shownAnswer.setText(stringValue1);

                                    }
                                });
                                stripQuestion.setText(stringValue);
                            }
                        });
                    }
                    else if (i == 1) {
                        s2.setVisibility(View.VISIBLE);
                        s2.setClickable(true);
                        s2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                invisConstrain.setVisibility(View.VISIBLE);
                                test.setVisibility(View.INVISIBLE);
                                showAnswer.setVisibility(View.VISIBLE);
                                s2.setVisibility(View.INVISIBLE);
                                s2.setClickable(false);
                                s1.setClickable(false);
                                s3.setClickable(false);
                                s4.setClickable(false);
                                s5.setClickable(false);
                                s6.setClickable(false);
                                s7.setClickable(false);
                                showAnswer.setClickable(true);
                                showAnswer.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showAnswer.setBackgroundColor(getResources().getColor(R.color.blue_green));
                                        showAnswer.setClickable(false);
                                        shownAnswer.setVisibility(View.VISIBLE);
                                        correct.setAlpha(1f);
                                        wrong.setAlpha(1f);
                                        correct.setClickable(true);
                                        wrong.setClickable(true);
                                        shownAnswer.setText(stringValue1);
                                    }
                                });
                                stripQuestion.setText(stringValue);
                            }
                        });
                    }
                    else if (i == 2) {
                        s3.setVisibility(View.VISIBLE);
                        s3.setClickable(true);
                        s3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                invisConstrain.setVisibility(View.VISIBLE);
                                test.setVisibility(View.INVISIBLE);
                                showAnswer.setVisibility(View.VISIBLE);
                                s3.setVisibility(View.INVISIBLE);
                                s3.setClickable(false);
                                s1.setClickable(false);
                                s2.setClickable(false);
                                s4.setClickable(false);
                                s5.setClickable(false);
                                s6.setClickable(false);
                                s7.setClickable(false);
                                showAnswer.setClickable(true);
                                showAnswer.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showAnswer.setBackgroundColor(getResources().getColor(R.color.blue_green));
                                        showAnswer.setClickable(false);
                                        shownAnswer.setVisibility(View.VISIBLE);
                                        correct.setAlpha(1f);
                                        wrong.setAlpha(1f);
                                        correct.setClickable(true);
                                        wrong.setClickable(true);
                                        shownAnswer.setText(stringValue1);

                                    }
                                });
                                stripQuestion.setText(stringValue);
                            }
                        });
                    }
                    else if (i == 3) {
                        s4.setVisibility(View.VISIBLE);
                        s4.setClickable(true);
                        s4.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                invisConstrain.setVisibility(View.VISIBLE);
                                test.setVisibility(View.INVISIBLE);
                                showAnswer.setVisibility(View.VISIBLE);
                                s4.setVisibility(View.INVISIBLE);
                                s4.setClickable(false);
                                s1.setClickable(false);
                                s2.setClickable(false);
                                s3.setClickable(false);
                                s5.setClickable(false);
                                s6.setClickable(false);
                                s7.setClickable(false);
                                showAnswer.setClickable(true);
                                showAnswer.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showAnswer.setBackgroundColor(getResources().getColor(R.color.blue_green));
                                        showAnswer.setClickable(false);
                                        shownAnswer.setVisibility(View.VISIBLE);
                                        correct.setAlpha(1f);
                                        wrong.setAlpha(1f);
                                        correct.setClickable(true);
                                        wrong.setClickable(true);
                                        shownAnswer.setText(stringValue1);

                                    }
                                });
                                stripQuestion.setText(stringValue);
                            }
                        });
                    }
                    else if (i == 4) {
                        s5.setVisibility(View.VISIBLE);
                        s5.setClickable(true);
                        s5.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                invisConstrain.setVisibility(View.VISIBLE);
                                test.setVisibility(View.INVISIBLE);
                                showAnswer.setVisibility(View.VISIBLE);
                                s5.setVisibility(View.INVISIBLE);
                                s5.setClickable(false);
                                s1.setClickable(false);
                                s2.setClickable(false);
                                s3.setClickable(false);
                                s4.setClickable(false);
                                s6.setClickable(false);
                                s7.setClickable(false);
                                showAnswer.setClickable(true);
                                showAnswer.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showAnswer.setBackgroundColor(getResources().getColor(R.color.blue_green));
                                        showAnswer.setClickable(false);
                                        shownAnswer.setVisibility(View.VISIBLE);
                                        correct.setAlpha(1f);
                                        wrong.setAlpha(1f);
                                        correct.setClickable(true);
                                        wrong.setClickable(true);
                                        shownAnswer.setText(stringValue1);

                                    }
                                });
                                stripQuestion.setText(stringValue);
                            }
                        });
                    }
                    else if (i == 5) {
                        s6.setVisibility(View.VISIBLE);
                        s6.setClickable(true);
                        s6.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                invisConstrain.setVisibility(View.VISIBLE);
                                test.setVisibility(View.INVISIBLE);
                                showAnswer.setVisibility(View.VISIBLE);
                                s6.setVisibility(View.INVISIBLE);
                                s6.setClickable(false);
                                s1.setClickable(false);
                                s2.setClickable(false);
                                s3.setClickable(false);
                                s4.setClickable(false);
                                s5.setClickable(false);
                                s7.setClickable(false);
                                showAnswer.setClickable(true);
                                showAnswer.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showAnswer.setBackgroundColor(getResources().getColor(R.color.blue_green));
                                        showAnswer.setClickable(false);
                                        shownAnswer.setVisibility(View.VISIBLE);
                                        correct.setAlpha(1f);
                                        wrong.setAlpha(1f);
                                        correct.setClickable(true);
                                        wrong.setClickable(true);
                                        shownAnswer.setText(stringValue1);

                                    }
                                });
                                stripQuestion.setText(stringValue);
                            }
                        });
                    }
                    else if (i == 6) {
                        s7.setVisibility(View.VISIBLE);
                        s7.setClickable(true);
                        s7.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                invisConstrain.setVisibility(View.VISIBLE);
                                test.setVisibility(View.INVISIBLE);
                                showAnswer.setVisibility(View.VISIBLE);
                                s7.setVisibility(View.INVISIBLE);
                                s7.setClickable(false);
                                s1.setClickable(false);
                                s2.setClickable(false);
                                s3.setClickable(false);
                                s4.setClickable(false);
                                s5.setClickable(false);
                                s6.setClickable(false);
                                showAnswer.setClickable(true);
                                showAnswer.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showAnswer.setBackgroundColor(getResources().getColor(R.color.blue_green));
                                        showAnswer.setClickable(false);
                                        shownAnswer.setVisibility(View.VISIBLE);
                                        correct.setAlpha(1f);
                                        wrong.setAlpha(1f);
                                        correct.setClickable(true);
                                        wrong.setClickable(true);
                                        shownAnswer.setText(stringValue1);

                                    }
                                });
                                stripQuestion.setText(stringValue);
                            }
                        });
                    }
                    else {
                        break;
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void increaseCount (int limit) {
        counting++;
        if (counting == limit) {
            invisConstrain.setVisibility(View.VISIBLE);
            stripQuestion.setText("You have answered all the questions!");
        }
    }

    private ArrayList<String> getTerms(ArrayList<String> termList) {
        return termList;
    }
    private ArrayList<String> getDefs(ArrayList<String> defList) {
        return defList;
    }
}