package com.example.adet;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Notebook_Data extends AppCompatActivity {

    private TextView sectionTitle;
    private LinearLayout content_Container;
    private Button Add;
    private ImageView sidemenu;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Name List");

    Intent theIntent;
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

        theIntent = getIntent();
        sidemenu = findViewById(R.id.sidemenu);
        content_Container = findViewById(R.id.content_Container);
        sectionTitle = findViewById(R.id.section_Title);
        Add = findViewById(R.id.notebookDataAdd);

        sectionTitle.setText(myRef.child(theIntent.getStringExtra("Fname"))
                .child("Notebook")
                .child(theIntent.getStringExtra("Subject"))
                .child(theIntent.getStringExtra("Topic")).getKey());

        //Display all terms and definitions
        myRef.child(theIntent.getStringExtra("Fname"))
                .child("Notebook")
                .child(theIntent.getStringExtra("Subject"))
                .child(theIntent.getStringExtra("Topic"))
                .child("Items").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        content_Container.removeAllViews();
                     for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                         String Term = itemSnapshot.getKey();
                         String Definition = itemSnapshot.getValue(String.class);

                         TextView termView = new TextView(Notebook_Data.this);
                         TextView definitionView = new TextView(Notebook_Data.this);

                         //Edit term TextView
                         termView.setText(Term);
                         termView.setId(View.generateViewId());
                         termView.setTextSize(20);
                         termView.setTypeface(null, android.graphics.Typeface.BOLD);
                         termView.setGravity(Gravity.CENTER);
                         termView.setBackground(getResources().getDrawable(R.drawable.rounding_corner));
                         termView.setBackgroundColor(getResources().getColor(R.color.faded_purple));
                         termView.setPadding(32, 32, 32, 32);

                         //Edit definition TextView
                         definitionView.setText(Definition);
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

                         termView.setOnClickListener(new View.OnClickListener() {
                             @Override
                             public void onClick(View v) {
                                 EditDelete(Term, Definition);
                             }
                         });

                         definitionView.setOnClickListener(new View.OnClickListener() {
                             @Override
                             public void onClick(View v) {
                                 EditDelete(Term, Definition);
                             }
                         });
                     }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle error
                    }
                });

        sidemenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Notebook_Data.this, Side_Menu.class);
                intent.putExtra("Fname",theIntent.getStringExtra("Fname"));
                startActivity(intent);
            }
        });

        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inflate the custom layout
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_add_notedata, null);

                // Create an AlertDialog.Builder
                AlertDialog.Builder builder = new AlertDialog.Builder(Notebook_Data.this);

                // Set the custom view
                builder.setView(dialogView);

                // Get references to UI elements
                EditText Term = dialogView.findViewById(R.id.dialog_term);
                EditText Definition = dialogView.findViewById(R.id.dialog_definition);
                Button Submit = dialogView.findViewById(R.id.dialog_notedata_submit);

                // Show the dialog
                AlertDialog dialog = builder.create();
                dialog.show();

                Submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String TermText = Term.getText().toString().replaceAll("\\s$","");
                        String DefinitionText = Definition.getText().toString().replaceAll("\\s$","");

                        if (TermText.equals("") || DefinitionText.equals("")) {
                            AlertDialog alertDialog = new AlertDialog.Builder(Notebook_Data.this).create();
                            alertDialog.setTitle("Error");
                            alertDialog.setMessage("Please fill in all fields");
                            alertDialog.show();
                        }
                        else {
                            myRef.child(theIntent.getStringExtra("Fname"))
                                    .child("Notebook")
                                    .child(theIntent.getStringExtra("Subject"))
                                    .child(theIntent.getStringExtra("Topic"))
                                    .child("Items").child(TermText).setValue(DefinitionText);
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
    }

    public void EditDelete (String Term, String Definition) {
        // Inflate the custom layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_editdelete_notebook, null);

        // Create an AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(Notebook_Data.this);

        // Set the custom view
        builder.setView(dialogView);

        // Get references to UI elements
        EditText showTerm = dialogView.findViewById(R.id.dialog_editdelete_term);
        EditText showDefinition = dialogView.findViewById(R.id.dialog_editdelete_definition);
        Button Edit = dialogView.findViewById(R.id.dialog_editdelete_edit);
        Button Delete = dialogView.findViewById(R.id.dialog_editdelete_delete);

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        showTerm.setText(Term);
        showDefinition.setText(Definition);

        Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String setTerm = showTerm.getText().toString();
                String setDefinition = showDefinition.getText().toString();

                if (setTerm.equals("") || setDefinition.equals("")) {
                    AlertDialog alertDialog = new AlertDialog.Builder(Notebook_Data.this).create();
                    alertDialog.setTitle("Error");
                    alertDialog.setMessage("Please fill in all fields");
                    alertDialog.show();
                }
                else {
                    myRef.child(theIntent.getStringExtra("Fname"))
                            .child("Notebook")
                            .child(theIntent.getStringExtra("Subject"))
                            .child(theIntent.getStringExtra("Topic"))
                            .child("Items").child(Term).removeValue();

                    myRef.child(theIntent.getStringExtra("Fname"))
                            .child("Notebook")
                            .child(theIntent.getStringExtra("Subject"))
                            .child(theIntent.getStringExtra("Topic"))
                            .child("Items").child(setTerm).setValue(setDefinition);

                    dialog.dismiss();
                }
            }
        });

        Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String setTerm = showTerm.getText().toString();

                if (setTerm.equals("")) {
                    AlertDialog alertDialog = new AlertDialog.Builder(Notebook_Data.this).create();
                    alertDialog.setTitle("Error");
                    alertDialog.setMessage("Please fill in Term");
                    alertDialog.show();
                }
                else {
                    if(myRef.child(theIntent.getStringExtra("Fname"))
                            .child("Notebook")
                            .child(theIntent.getStringExtra("Subject"))
                            .child(theIntent.getStringExtra("Topic"))
                            .child(Term).getKey().equals(setTerm))
                    {
                        myRef.child(theIntent.getStringExtra("Fname"))
                                .child("Notebook")
                                .child(theIntent.getStringExtra("Subject"))
                                .child(theIntent.getStringExtra("Topic"))
                                .child("Items").child(setTerm).removeValue();
                        dialog.dismiss();
                    }
                    else {
                        AlertDialog alertDialog = new AlertDialog.Builder(Notebook_Data.this).create();
                        alertDialog.setTitle("Error");
                        alertDialog.setMessage("Please fill in correct Term");
                        alertDialog.show();
                    }
                }
            }
        });
    }
}