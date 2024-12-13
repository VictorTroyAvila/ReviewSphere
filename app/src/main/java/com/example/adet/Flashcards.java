package com.example.adet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.snapshot.Index;

import java.util.ArrayList;
import java.util.Random;

public class Flashcards extends AppCompatActivity {

    private TextView QuestionToAnswer, invisTextView, cCounter, wCounter, wrong, correct;
    private Button showAnswer;
    private ConstraintLayout invisConstrain;
    private ImageView ekis, sidemenu;

    int counterC = 0;
    int counterW = 0;

    Random random = new Random();
    Intent theIntent;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Name List");

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
        wrong = findViewById(R.id.txt_wrong);
        correct = findViewById(R.id.txt_correct);
        cCounter = findViewById(R.id.correct_counter);
        wCounter = findViewById(R.id.wrong_counter);
        ekis = findViewById(R.id.Eks);
        sidemenu = findViewById(R.id.sidemenu);

        String Fname = theIntent.getStringExtra("Fname");
        String Subject = theIntent.getStringExtra("Subject");
        String Topic = theIntent.getStringExtra("Topic");

        if (Fname != null && Subject != null && Topic != null){
            String game = "FlashCards";
            Achievements achievements = new  Achievements(game, theIntent);
            achievements.getAchievement(game, Fname, new StringCallback() {
                @Override
                public void onStringRetrieved(String achievement) {
                    if (achievement != null) {
                        myRef.child(Fname)
                                .child("Achievements")
                                .child("Flashcards")
                                .child(achievement)
                                .setValue(true);

                        // Inflate the custom layout
                        LayoutInflater inflater = getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.dialog_msg, null);

                        // Create an AlertDialog.Builder
                        AlertDialog.Builder builder = new AlertDialog.Builder(Flashcards.this);

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

        ekis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inflate the custom layout
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_exit_notice, null);

                // Create an AlertDialog.Builder
                AlertDialog.Builder builder = new AlertDialog.Builder(Flashcards.this);

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
                        Intent intent = new Intent(Flashcards.this, Home.class);
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
                Intent intent = new Intent(Flashcards.this, Side_Menu.class);
                intent.putExtra("Fname", theIntent.getStringExtra("Fname"));
                startActivity(intent);
                }
        });

    }
    private void ReturnValues(ArrayList <String> termList, ArrayList <String> definitionList, int index) {
        int[] rndIndex = new int[1];
        final int[] nIndex = {index};
        rndIndex[0] = randomizingIndex(index);

        QuestionToAnswer.setText(definitionList.get(rndIndex[0]));
        showAnswer.setAlpha(1f);
        wrong.setAlpha(0.5f);
        correct.setAlpha(0.5f);
        showAnswer.setEnabled(true);
        wrong.setEnabled(false);
        correct.setEnabled(false);

        showAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String compare = QuestionToAnswer.getText().toString();
                invisConstrain.setVisibility(View.VISIBLE);

                invisTextView.setText(QuestionToAnswer.getText().toString());
                QuestionToAnswer.setText(termList.get(rndIndex[0]));

                if(checkEqual(compare, definitionList, rndIndex[0])){
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
                if (nIndex[0] == 0 && definitionList == null && termList == null) {
                    QuestionToAnswer.setText("You have mastered all the flashcards!");
                    showAnswer.setEnabled(false);
                    wrong.setEnabled(false);
                    correct.setEnabled(false);
                }
                else {
                    counterW++;
                    wCounter.setText(String.valueOf(counterW));
                    rndIndex[0] = randomizingIndex(nIndex[0]);
                    QuestionToAnswer.setText(definitionList.get(rndIndex[0]));
                    invisConstrain.setVisibility(View.INVISIBLE);
                    showAnswer.setAlpha(1f);
                    wrong.setAlpha(0.5f);
                    correct.setAlpha(0.5f);
                    showAnswer.setEnabled(true);
                    wrong.setEnabled(false);
                    correct.setEnabled(false);
                }
            }
        });

        correct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nIndex[0] == 1) {
                    QuestionToAnswer.setText("You have mastered all the flashcards!");
                    invisConstrain.setVisibility(View.INVISIBLE);
                    showAnswer.setAlpha(0.5f);
                    wrong.setAlpha(0.5f);
                    correct.setAlpha(0.5f);
                    showAnswer.setEnabled(false);
                    wrong.setEnabled(false);
                    correct.setEnabled(false);
                }
                else {
                    counterC++;
                    cCounter.setText(String.valueOf(counterC));

                    definitionList.remove(rndIndex[0]);
                    termList.remove(rndIndex[0]);
                    nIndex[0]--;

                    rndIndex[0] = randomizingIndex(nIndex[0]);
                    QuestionToAnswer.setText(definitionList.get(rndIndex[0]));
                    invisConstrain.setVisibility(View.INVISIBLE);
                    showAnswer.setAlpha(1f);
                    wrong.setAlpha(0.5f);
                    correct.setAlpha(0.5f);
                    showAnswer.setEnabled(true);
                    wrong.setEnabled(false);
                    correct.setEnabled(false);
                }
            }
        });
    }
    private int randomizingIndex(int index) {
        if (index == 0) {
            return 0;
        }
        return random.nextInt(index);
    }
    private boolean checkEqual(String ans, ArrayList <String> definitionList, int index) {
        if (ans.equalsIgnoreCase(definitionList.get(index))) {
            return true;
        } else {
            System.out.println("Paano nag kamali toh");
        }
        return false;
    }
}