package com.example.adet;

import static com.google.common.collect.ComparisonChain.start;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Name List");

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

        ekis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inflate the custom layout
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_exit_notice, null);

                // Create an AlertDialog.Builder
                AlertDialog.Builder builder = new AlertDialog.Builder(TrueOrFalse.this);

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
                        Intent intent = new Intent(TrueOrFalse.this, Home.class);
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
                Intent intent = new Intent(TrueOrFalse.this, Side_Menu.class);
                startActivity(intent);
            }
        });

    }
    private void ReturnValues(ArrayList <String> termList, ArrayList <String> definitionList, int index) {

        final int[] rndIndex4Def = {randomizingIndex(index)};
        final int[] rndIndex4Term = {randomizingIndex(index)};

        Term = termList.get(rndIndex4Term[0]);
        Definition = definitionList.get(rndIndex4Def[0]);

        QuestionAnswer.setText("Is "+Term+"\n \n"+Definition);

        Green.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkEqual(rndIndex4Term[0], rndIndex4Def[0], Term, Definition, termList, definitionList)) {
                    counterC++;
                    cCounter.setText(String.valueOf(counterC));
                    rndIndex4Def[0] = randomizingIndex(index);
                    rndIndex4Term[0] = randomizingIndex(index);
                    Term = termList.get(rndIndex4Term[0]);
                    Definition = definitionList.get(rndIndex4Def[0]);
                    QuestionAnswer.setText("Is "+Term+"\n \n"+Definition);
                }
                else {
                    counterW++;
                    wCounter.setText(String.valueOf(counterW));

                    //Show Correct Answer
                    showCorrectAns(rndIndex4Term[0], termList, definitionList);

                    rndIndex4Def[0] = randomizingIndex(index);
                    rndIndex4Term[0] = randomizingIndex(index);
                    Term = termList.get(rndIndex4Term[0]);
                    Definition = definitionList.get(rndIndex4Def[0]);
                    QuestionAnswer.setText("Is "+Term+"\n \n"+Definition);
                }
            }
        });
        Red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkEqual(rndIndex4Term[0], rndIndex4Def[0], Term, Definition, termList, definitionList)) {
                    counterW++;
                    wCounter.setText(String.valueOf(counterW));

                    //Show Correct Answer
                    showCorrectAns(rndIndex4Term[0], termList, definitionList);

                    rndIndex4Def[0] = randomizingIndex(index);
                    rndIndex4Term[0] = randomizingIndex(index);
                    Term = termList.get(rndIndex4Term[0]);
                    Definition = definitionList.get(rndIndex4Def[0]);
                    QuestionAnswer.setText("Is "+Term+"\n \n"+Definition);
                }
                else {
                    counterC++;
                    cCounter.setText(String.valueOf(counterC));
                    rndIndex4Def[0] = randomizingIndex(index);
                    rndIndex4Term[0] = randomizingIndex(index);
                    Term = termList.get(rndIndex4Term[0]);
                    Definition = definitionList.get(rndIndex4Def[0]);
                    QuestionAnswer.setText("Is "+Term+"\n \n"+Definition);
                }
            }
        });
    }

    private int randomizingIndex(int index) {
        Random random = new Random();
        return random.nextInt(index);
    }
    private boolean checkEqual(int rndIndex4Term, int rndIndex4Def, String Term, String Definition, ArrayList <String> termList, ArrayList <String> definitionList) {
        if (Term.equals(termList.get(rndIndex4Def)) && Definition.equals(definitionList.get(rndIndex4Term))) {
            return true;
        }
        else {
            return false;
        }
    }
    private void showCorrectAns(int rndIndex4Term, ArrayList <String> termList, ArrayList <String> definitionList) {
        // Inflate the custom layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_showcorrect, null);

        // Create an AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set the custom view
        builder.setView(dialogView);

        // Get references to UI elements
        TextView correctans = dialogView.findViewById(R.id.dialog_correctans);
        String correctAns = termList.get(rndIndex4Term);
        String correctDef = definitionList.get(rndIndex4Term);
        correctans.setText(correctAns + "\n\n" + correctDef);

        // Show the dialog
        AlertDialog dialog = builder.create();

        dialog.show();
    }

}