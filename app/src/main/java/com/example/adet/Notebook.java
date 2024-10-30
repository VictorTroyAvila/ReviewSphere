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

import javax.security.auth.Subject;

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
                content_Container.removeAllViews();
                //Subject
                for (DataSnapshot subjectSnapshot : snapshot.getChildren()) {
                    //Topic
                    for (DataSnapshot topicSnapshot : subjectSnapshot.getChildren()) {
                        //Items
                        for (DataSnapshot itemsnapshot : topicSnapshot.getChildren()) {
                            String Subject = subjectSnapshot.getKey();
                            String Topic = topicSnapshot.getKey();
                            long itemCount = itemsnapshot.getChildrenCount();

                            TextView textView = new TextView(Notebook.this);
                            textView.setText("Topic: " + Topic +
                                    "\n" + "Items: " + itemCount +
                                    "\n" + "Subject: " + Subject);

                            textView.setBackgroundColor(getResources().getColor(R.color.faded_purple));
                            textView.setPadding(30, 30, 30, 30);
                            textView.setTextSize(20);

                            LinearLayout.LayoutParams txtVParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );
                            txtVParams.setMargins(16, 20, 16, 20);
                            textView.setLayoutParams(txtVParams);

                            textView.setOnClickListener(v -> {
                                Intent intent = new Intent(Notebook.this, Notebook_Data.class);
                                intent.putExtra("Fname", theIntent.getStringExtra("Fname"));
                                intent.putExtra("Subject", Subject);
                                intent.putExtra("Topic", Topic);
                                startActivity(intent);
                            });

                            content_Container.addView(textView);

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Go to Side menu
        goto_sidemenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Notebook.this, Side_Menu.class);
                startActivity(intent);
            }
        });

        //Add Subject and Content
        Add.setOnClickListener(v -> {
            // Inflate the custom layout
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_addnote, null);

            // Create an AlertDialog.Builder
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            // Set the custom view
            builder.setView(dialogView);

            // Get references to UI elements
            EditText subjectTitle = dialogView.findViewById(R.id.dialog_add_subject);
            EditText topicTitle = dialogView.findViewById(R.id.dialog_add_topic);
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

                    Map<String, Object> data = new HashMap<>();
                    data.put("Term", "Definition");

                    Map<String, Object> item = new HashMap<>();
                    item.put("Items", data);

                    Map<String, Object> Topic = new HashMap<>();
                    Topic.put(TopicTitle, item);

                    Map<String, Object> subject = new HashMap<>();
                    subject.put(SubjectTitle, Topic);

                    NotebookChecking(theIntent.getStringExtra("Fname"), new CheckCallback() {
                        @Override
                        public void onCheckComplete(boolean exists) {
                            if (exists) {
                                //If Notebook Exists
                                SubjectChecking(theIntent.getStringExtra("Fname"), SubjectTitle, new CheckCallback() {
                                    @Override
                                    public void onCheckComplete(boolean exists) {
                                        //If Subject Exists
                                        if (exists) {
                                            TopicChecking(theIntent.getStringExtra("Fname"), SubjectTitle, TopicTitle, new CheckCallback() {
                                                @Override
                                                public void onCheckComplete(boolean exists) {
                                                    //If subject and topic exists
                                                    if (exists) {
                                                        AlertDialog alertDialog = new AlertDialog.Builder(Notebook.this).create();
                                                        alertDialog.setTitle("Error");
                                                        alertDialog.setMessage("Topic already exists");
                                                        alertDialog.show();
                                                    }
                                                    else {
                                                        myRef.child(theIntent.getStringExtra("Fname")).child("Notebook").child(SubjectTitle).child(TopicTitle).setValue(item);
                                                    }
                                                }
                                            });
                                        }
                                        //If Subject does not exist
                                        else {
                                            myRef.child(theIntent.getStringExtra("Fname")).child("Notebook").child(SubjectTitle).child(TopicTitle).setValue(item);
                                        }
                                    }
                                });
                            }
                            //If Notebook does not exist
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

        //Edit Subject and or Topic
        Edit.setOnClickListener(v -> {
            // Inflate the custom layout
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_editnote, null);

            // Create an AlertDialog.Builder
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            // Set the custom view
            builder.setView(dialogView);

            // Get references to UI elements
            EditText subjectTitle = dialogView.findViewById(R.id.dialog_edit_subject_title);
            EditText newSubjectTitle = dialogView.findViewById(R.id.dialog_edit_new_subject_title);
            EditText topicTitle = dialogView.findViewById(R.id.dialog_edit_topic_title);
            EditText newTopicTitle = dialogView.findViewById(R.id.dialog_edit_new_topic_title);
            Button EditButton = dialogView.findViewById(R.id.dialog_edit);

            // Show the dialog
            AlertDialog dialog = builder.create();
            dialog.show();

            EditButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String SubjectTitle = subjectTitle.getText().toString();
                    String NewSubjectTitle = newSubjectTitle.getText().toString();
                    String TopicTitle = topicTitle.getText().toString();
                    String NewTopicTitle = newTopicTitle.getText().toString();

                    SubjectChecking(theIntent.getStringExtra("Fname"), SubjectTitle, new CheckCallback() {
                        @Override
                        public void onCheckComplete(boolean exists) {
                            //If Subject Exists
                            if (exists) {
                                //If new subject title is empty
                                if(NewSubjectTitle.equalsIgnoreCase("")) {
                                    TopicChecking(theIntent.getStringExtra("Fname"), SubjectTitle, TopicTitle, new CheckCallback() {
                                        @Override
                                        public void onCheckComplete(boolean exists) {
                                            //If Subject and Topic Exists, but no new subject title
                                            if (exists) {
                                                if (NewTopicTitle.equalsIgnoreCase("")) {
                                                    AlertDialog alertDialog = new AlertDialog.Builder(Notebook.this).create();
                                                    alertDialog.setTitle("Error");
                                                    alertDialog.setMessage("Nothing to Edit");
                                                    alertDialog.show();
                                                }
                                                //If Subject, topic, and new title Exists
                                                else {
                                                    myRef.child(theIntent.getStringExtra("Fname")).child("Notebook").child(SubjectTitle).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                            if(task.isSuccessful())
                                                            {
                                                                Object data = task.getResult().child(TopicTitle).getValue();
                                                                myRef.child(theIntent.getStringExtra("Fname")).child("Notebook").child(SubjectTitle).child(NewTopicTitle).setValue(data);
                                                                myRef.child(theIntent.getStringExtra("Fname")).child("Notebook").child(SubjectTitle).child(TopicTitle).removeValue();
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                            //If Topic does not exist
                                            else {
                                                AlertDialog alertDialog = new AlertDialog.Builder(Notebook.this).create();
                                                alertDialog.setTitle("Error");
                                                alertDialog.setMessage("Nothing to Edit");
                                                alertDialog.show();
                                            }
                                        }
                                    });
                                }
                                //If new subject title is not empty
                                else {
                                    TopicChecking(theIntent.getStringExtra("Fname"), SubjectTitle, TopicTitle, new CheckCallback() {
                                        @Override
                                        public void onCheckComplete(boolean exists) {
                                            //If Subject and Topic Exists w/ subject title
                                            if (exists) {
                                                //If New Topic title is empty
                                                if(NewTopicTitle.equalsIgnoreCase("")) {
                                                    myRef.child(theIntent.getStringExtra("Fname")).child("Notebook").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                            if(task.isSuccessful())
                                                            {
                                                                Object data = task.getResult().child(SubjectTitle).getValue();
                                                                myRef.child(theIntent.getStringExtra("Fname")).child("Notebook").child(NewSubjectTitle).setValue(data);
                                                                myRef.child(theIntent.getStringExtra("Fname")).child("Notebook").child(SubjectTitle).removeValue();
                                                            }
                                                        }
                                                    });

                                                    AlertDialog alertDialog = new AlertDialog.Builder(Notebook.this).create();
                                                    alertDialog.setTitle("Alert");
                                                    alertDialog.setMessage("Only Subject title changed");
                                                    alertDialog.show();
                                                }
                                                //If Topic title is not empty
                                                else {
                                                    myRef.child(theIntent.getStringExtra("Fname")).child("Notebook").child(SubjectTitle).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                        if(task.isSuccessful())
                                                        {
                                                            Object data = task.getResult().child(SubjectTitle).child(TopicTitle).getValue();
                                                            myRef.child(theIntent.getStringExtra("Fname")).child("Notebook").child(SubjectTitle).child(NewTopicTitle).setValue(data);
                                                            myRef.child(theIntent.getStringExtra("Fname")).child("Notebook").child(SubjectTitle).child(TopicTitle).removeValue();
                                                        }
                                                    }
                                                });
                                                    myRef.child(theIntent.getStringExtra("Fname")).child("Notebook").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                        if(task.isSuccessful())
                                                        {
                                                            Object data = task.getResult().child(SubjectTitle).getValue();
                                                            myRef.child(theIntent.getStringExtra("Fname")).child("Notebook").child(NewSubjectTitle).setValue(data);
                                                            myRef.child(theIntent.getStringExtra("Fname")).child("Notebook").child(SubjectTitle).removeValue();
                                                        }
                                                    }
                                                });
                                                }
                                            }
                                            //If Subject exists but Topic does not exist
                                            else{
                                                //If Topic title is empty
                                                if (TopicTitle.equalsIgnoreCase("")) {
                                                    AlertDialog alertDialog = new AlertDialog.Builder(Notebook.this).create();
                                                    alertDialog.setTitle("Error");
                                                    alertDialog.setMessage("Topic does not exist");
                                                    alertDialog.show();
                                                }
                                                else {
                                                    myRef.child(theIntent.getStringExtra("Fname")).child("Notebook").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                            //If Subject and new subject title exists w/ no topic title and new topic title
                                                            if(task.isSuccessful())
                                                            {
                                                                Object data = task.getResult().child(SubjectTitle).getValue();
                                                                myRef.child(theIntent.getStringExtra("Fname")).child("Notebook").child(NewSubjectTitle).setValue(data);
                                                                myRef.child(theIntent.getStringExtra("Fname")).child("Notebook").child(SubjectTitle).removeValue();
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                            //If Subject does not exist
                            else{
                                AlertDialog alertDialog = new AlertDialog.Builder(Notebook.this).create();
                                alertDialog.setTitle("Error");
                                alertDialog.setMessage("Subject does not exist");
                                alertDialog.show();
                            }
                        }
                    });
                    dialog.dismiss();
                }
            });

        });

        //Delete Subject and or Topic
        Delete.setOnClickListener(v -> {
            // Inflate the custom layout
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_deletenote, null);

            // Create an AlertDialog.Builder
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            // Set the custom view
            builder.setView(dialogView);

            // Get references to UI elements
            EditText subjectTitle = dialogView.findViewById(R.id.dialog_delete_subject_title);
            EditText topicTitle = dialogView.findViewById(R.id.dialog_delete_topic_title);
            Button DeleteButton = dialogView.findViewById(R.id.dialog_delete);

            // Show the dialog
            AlertDialog dialog = builder.create();
            dialog.show();

            DeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String SubjectTitle = subjectTitle.getText().toString();
                    String TopicTitle = topicTitle.getText().toString();

                    SubjectChecking(theIntent.getStringExtra("Fname"), SubjectTitle, new CheckCallback() {
                        @Override
                        public void onCheckComplete(boolean exists) {
                            //If Subject Exists
                            if (exists) {
                               myRef.child(theIntent.getStringExtra("Fname")).child("Notebook").child(SubjectTitle).removeValue();
                            }
                            else {
                                TopicChecking(theIntent.getStringExtra("Fname"), SubjectTitle, TopicTitle, new CheckCallback() {
                                    @Override
                                    public void onCheckComplete(boolean exists) {
                                        if (exists) {
                                            myRef.child(theIntent.getStringExtra("Fname")).child("Notebook").child(SubjectTitle).child(TopicTitle).removeValue();
                                        }
                                        else {
                                            AlertDialog alertDialog = new AlertDialog.Builder(Notebook.this).create();
                                            alertDialog.setTitle("Error");
                                            alertDialog.setMessage("Nothing to Delete");
                                            alertDialog.show();
                                        }
                                    }
                                });
                            }
                        }
                    });
                    dialog.dismiss();
                }
            });
        });
    }

    private void NotebookChecking(String Acc, CheckCallback callback) {
        myRef.child(Acc).child("Notebook").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    boolean exist = task.getResult().exists();
                    callback.onCheckComplete(exist);
                } else {
                    callback.onCheckComplete(false); // Handle error
                }
            }
        });
    }

    private void SubjectChecking(String Acc, String Subject, CheckCallback callback) {
        myRef.child(Acc).child("Notebook").child(Subject).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    boolean exist = false;
                    if (myRef.child(Acc).child("Notebook").child(Subject).getKey().equalsIgnoreCase(Subject)){
                        exist = true;
                    }
                    else {
                        exist = false;
                    }
                    callback.onCheckComplete(exist);
                } else {
                    callback.onCheckComplete(false); // Handle error
                }
            }
        });
    }

    private void TopicChecking(String Acc, String Subject, String Topic, CheckCallback callback) {
        myRef.child(Acc).child("Notebook").child(Subject).child(Topic).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    boolean exist = false;
                    if (myRef.child(Acc).child("Notebook").child(Subject).child(Topic).getKey().equalsIgnoreCase(Topic)){
                        exist = true;
                    }
                    else {
                        exist = false;
                    }
                    callback.onCheckComplete(exist);
                } else {
                    callback.onCheckComplete(false); // Handle error
                }
            }
        });
    }

}