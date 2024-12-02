package com.example.adet;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import kotlin.sequences.Sequence;

public class Achievements {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Name List");

    int plays;
    String Fname;

    public Achievements (String game, Intent theIntent) {
        Fname = theIntent.getStringExtra("Fname");



        if (Fname != null) {
            AchievementsChecking(Fname, new BooleanCallback() {
                @Override
                public void onCheckComplete(boolean exist) {
                    if (!exist) {

                        //Matching Achievements
                        Map<String, Object> MatchingAchievements= new HashMap<>();
                        MatchingAchievements.put("Matchmaker Extraordinaire", false);
                        MatchingAchievements.put("Pairing Prodigy", false);
                        MatchingAchievements.put("Connection Royalty", false);
                        MatchingAchievements.put("Link Legend", false);
                        MatchingAchievements.put("Alignment Ace", false);

                        //True or False Achievements
                        Map<String, Object> TrueFalseAchievements = new HashMap<>();
                        TrueFalseAchievements.put("Truth Seeker", false);
                        TrueFalseAchievements.put("False Buster", false);
                        TrueFalseAchievements.put("Logic Ace", false);
                        TrueFalseAchievements.put("Fact or Fiction Pro", false);
                        TrueFalseAchievements.put("Reality Checker", false);

                        //Strips Achievements
                        Map<String, Object> StripstAchievements = new HashMap<>();
                        StripstAchievements.put("Puzzle Solver", false);
                        StripstAchievements.put("Strip Sensei",false);
                        StripstAchievements.put("Sequence Master",false);
                        StripstAchievements.put("Strip Specialist",false);
                        StripstAchievements.put("Order Architect",false);

                        //Quizzes Achievements
                        Map<String, Object> QuizzesAchievements = new HashMap<>();
                        QuizzesAchievements.put("Quiz Whiz",false);
                        QuizzesAchievements.put("Knowledge Ninja",false);
                        QuizzesAchievements.put("Brainstorm Champ",false);
                        QuizzesAchievements.put("Fact Finder",false);
                        QuizzesAchievements.put("Genius Guru",false);

                        //Flashcards Achievements
                        Map<String, Object> FlashcardsAchievements = new HashMap<>();
                        FlashcardsAchievements.put("Memory Master", false);
                        FlashcardsAchievements.put("Flash Prodigy", false);
                        FlashcardsAchievements.put("Recall Rockstar", false);
                        FlashcardsAchievements.put("Brain Boost Champion", false);
                        FlashcardsAchievements.put("Card Conqueror", false);

                        Map<String, Object> achievements = new HashMap<>();
                        achievements.put("Flashcards", FlashcardsAchievements);
                        achievements.put("Quizzes", QuizzesAchievements);
                        achievements.put("Strips", StripstAchievements);
                        achievements.put("TrueFalse", TrueFalseAchievements);
                        achievements.put("Matching", MatchingAchievements);

                        myRef.child(Fname).child("Achievements").setValue(achievements);
                    }
                    else {

                    }
                }
            });
        }
    }

    public void getAchievement(String game, String Fname, StringCallback callback) {
        myRef.child(Fname)
                .child("User Info")
                .child("Performance")
                .child(game)
                .child("No Plays")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int plays = snapshot.getValue(Integer.class);

                        String achievement = null;
                        switch (game) {
                            case "FlashCards":
                                if (plays == 10) {
                                    achievement = "Memory Master";
                                } else if (plays == 30) {
                                    achievement = "Flash Prodigy";
                                } else if (plays == 60) {
                                    achievement = "Recall Rockstar";
                                } else if (plays == 100) {
                                    achievement = "Brain Boost Champion";
                                } else if (plays == 150) {
                                    achievement = "Card Conqueror";
                                }
                                break;
                            case "Quizzes":
                                if (plays == 10) {
                                    achievement = "Quiz Whiz";
                                } else if (plays == 30) {
                                    achievement = "Knowledge Ninja";
                                } else if (plays == 60) {
                                    achievement = "Brainstorm Champ";
                                } else if (plays == 100) {
                                    achievement = "Fact Finder";
                                } else if (plays == 150) {
                                    achievement = "Genius Guru";
                                }
                                break;
                            case "Strips":
                                if (plays == 10) {
                                    achievement = "Puzzle Solver";
                                } else if (plays == 30) {
                                    achievement = "Strip Sensei";
                                } else if (plays == 60) {
                                    achievement = "Sequence Master";
                                } else if (plays == 100) {
                                    achievement = "Strip Specialist";
                                } else if (plays == 150) {
                                    achievement = "Order Architect";
                                }
                                break;
                            case "TrueFalse":
                                if (plays == 10) {
                                    achievement = "Truth Seeker";
                                } else if (plays == 30) {
                                    achievement = "False Buster";
                                } else if (plays == 60) {
                                    achievement = "Logic Ace";
                                } else if (plays == 100) {
                                    achievement = "Fact or Fiction Pro";
                                } else if (plays == 150) {
                                    achievement = "Reality Checker";
                                }
                                break;
                            case "Matching":
                                if (plays == 10) {
                                    achievement = "Matchmaker Extraordinaire";
                                } else if (plays == 30) {
                                    achievement = "Pairing Prodigy";
                                } else if (plays == 60) {
                                    achievement = "Connection Royalty";
                                } else if (plays == 100) {
                                    achievement = "Link Legend";
                                } else if (plays == 150) {
                                    achievement = "Alignment Ace";
                                }
                                break;
                        }

                        // Call the callback with the achievement
                        callback.onStringRetrieved(achievement);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle errors
                        callback.onStringRetrieved(null); // Or handle error appropriately
                    }
                });
    }

    private void AchievementsChecking(String Acc, BooleanCallback booleanCallback) {
        myRef.child(Acc).child("Achievements").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    boolean exist = task.getResult().exists();
                    booleanCallback.onCheckComplete(exist);
                } else {
                    booleanCallback.onCheckComplete(false); // Handle error
                }
            }
        });
    }
}
