package com.example.adet;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
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
import java.lang.reflect.Method;
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
    List<String> termsList = new ArrayList<>();
    List<String> defsList = new ArrayList<>();
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
                AlertDialog.Builder builder = new AlertDialog.Builder(Matching_Type.this);

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
                        Intent intent = new Intent(Matching_Type.this, Home.class);
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
                Intent intent = new Intent(Matching_Type.this, Side_Menu.class);
                startActivity(intent);
            }
        });
    }
    private void ReturnValues(ArrayList <String> termList, ArrayList <String> definitionList, int index) {
        ArrayList<String> Terms = termList;
        ArrayList<String> Defs = definitionList;

            for (int i = 0; i < Terms.size(); i++) {
                termsList.add(Terms.get(i));
                allOptions.add(Terms.get(i));
            }
            for (int i = 0; i< Defs.size(); i++) {
                defsList.add(Defs.get(i));
                allOptions.add(Defs.get(i));
            }

        Collections.shuffle(allOptions); // Shuffle all options
        numMatches = Terms.size(); // Number of matches needed to win

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
                    button.setTag(allOptions.get(i * 5 + j)); // Store the actual text in the tag
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
}