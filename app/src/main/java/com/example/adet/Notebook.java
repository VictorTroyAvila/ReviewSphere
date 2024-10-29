package com.example.adet;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Notebook extends AppCompatActivity {

    private LinearLayout content_Container;
    private ImageView goto_sidemenu;
    private Button Add, Edit, Delete;
    private Intent theIntent;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Name List");

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

        theIntent = getIntent();
        content_Container = findViewById(R.id.item_list);
        goto_sidemenu = findViewById(R.id.sidemenu);
        Add = findViewById(R.id.notebookAdd);
        Edit = findViewById(R.id.notebookEdit);
        Delete = findViewById(R.id.notebookDelete);

        //Display all data
        myRef.child(theIntent.getStringExtra("Fname")).child("Notebook").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren())
                {
                    String key = childSnapshot.getKey();

                    TextView textView = new TextView(Notebook.this);
                    textView.setText("Subject: " + key);

                    textView.setBackground(getResources().getDrawable(R.drawable.rounding_corner_lite));
                    textView.setBackgroundColor(getResources().getColor(R.color.faded_purple));
                    textView.setPadding(10, 10, 10, 10);
                    textView.setTextSize(20);

                    content_Container.addView(textView);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        goto_sidemenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Notebook.this, Side_Menu.class);
                startActivity(intent);
            }
        });

        Add.setOnClickListener(v -> {
            // Inflate the custom layout
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_addnote, null);

            // Create an AlertDialog.Builder
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            // Set the custom view
            builder.setView(dialogView);

            // Get references to UI elements
            EditText subjectTitle = dialogView.findViewById(R.id.dialog_edit_text);
            EditText topicTitle = dialogView.findViewById(R.id.dialog_edit_text2);
            Button submitButton = dialogView.findViewById(R.id.dialog_submit);

            // Show the dialog
            AlertDialog dialog = builder.create();
            dialog.show();

            // Set button click listener
            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String SubjectTitle = subjectTitle.getText().toString();
                    String TopicTitle = topicTitle.getText().toString();

                    Map<String, Object> definition = new HashMap<>();
                    definition.put("Filler", "Filler");

                    Map<String, Object> data = new HashMap<>();
                    data.put("Filler", definition);

                    Map<String, Object> item = new HashMap<>();
                    item.put("Items", data);

                    Map<String, Object> Topic = new HashMap<>();
                    Topic.put(TopicTitle, item);

                    Map<String, Object> subject = new HashMap<>();
                    subject.put(SubjectTitle, Topic);

                    Checking( new CheckCallback() {
                        @Override
                        public void onCheckComplete(boolean exists) {
                            // Handle the result here
                            if (exists) {
                                myRef.child(theIntent.getStringExtra("Fname")).child("Notebook").child(SubjectTitle).setValue(Topic);
                            }
                            else
                            {
                                myRef.child(theIntent.getStringExtra("Fname")).child("Notebook").setValue(subject);
                            }
                        }
                    });

                    dialog.dismiss();
                }
            });
        });

        Edit.setOnClickListener(v -> {

        });

        Delete.setOnClickListener(v -> {

        });
    }

    private void Checking(CheckCallback callback) {
        myRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    boolean exist = false;
                    for (DataSnapshot childSnapshot : task.getResult().getChildren()) {
                        if (childSnapshot.child("Notebook").exists())
                        {
                            exist = true;
                        }
                    }
                    callback.onCheckComplete(exist);
                } else {
                    callback.onCheckComplete(false); // Handle error
                }
            }
        });
    }

}