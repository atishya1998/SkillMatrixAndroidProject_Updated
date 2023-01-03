package com.example.skillmatrix;


import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        EditText password = findViewById(R.id.passwordET);
        EditText email = findViewById(R.id.emailET);
        EditText FullName = findViewById(R.id.FullnameET);
        EditText userid = findViewById(R.id.useridET);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Button registerButton = findViewById(R.id.RegisterButton);

       registerButton.setOnClickListener(v->{
           if (!password.getText().toString().isEmpty() || !email.getText().toString().isEmpty() || !FullName.getText().toString().isEmpty() || !userid.getText().toString().isEmpty()) {
               //check if email ends with skillmatrix.com
               if (email.getText().toString().split("@")[0].equals(userid.getText().toString())) {

                   //check if email is already registered
                   db.collection("users").document(" "+email.getText().toString().split("@")[0]).get().addOnCompleteListener(task -> {
                       if (task.isSuccessful()) {
                           if (task.getResult().exists()) {
                               //create snackbar to show that email already exists
                               Snackbar.make(findViewById(android.R.id.content), "Email already exists", Snackbar.LENGTH_LONG).show();
                           } else {
                               //if not create new user
                               db.collection("users").document(" "+email.getText().toString().split("@")[0]).set(Map.of("email", email.getText().toString(), "password", password.getText().toString(), "fullname", FullName.getText().toString(), "id"," "+ userid.getText().toString(), "role", "employee")).addOnCompleteListener(task1 -> {
                                   if (task1.isSuccessful()) {
                                       //create snackbar to show that user has been registered
                                       Snackbar.make(findViewById(android.R.id.content), "User has been registered", Snackbar.LENGTH_LONG).show();
                                       //add empty fields to EmployeeSkillBase and EmployeeBase so code doesn't break when trying to fetch the code for the first time
                                       Map<String, Object> personalInfo = Map.of("address", "", "blood", "", "phone", "", "yearsOfExperience", "");
                                        Map<String, Object> skills = new HashMap<>();
                                        Map<String, Object> rating = Map.of("myRating", 0, "managerRating", 0, "overallRating", 0);
                                       db.collection("EmployeeSkillBase").document(" "+userid.getText().toString()).set(Map.of("personalInformation", personalInfo, "project", "", "project_assigned", false, "role", "employee", "rating", rating, "skills", skills)).addOnCompleteListener(task2 -> {
                                           if (task2.isSuccessful()) {
                                               db.collection("EmployeeBase").document("unassigned").update("unassgined", FieldValue.arrayUnion(userid.getText().toString()));
                                               new Handler().postDelayed(() -> {
                                                   finish();
                                               }, 1000); //basic delay to show the snackBar before finishing the activity
                                           }
                                       });
                                   } else {
                                       //create snackbar to show that user has not been registered
                                       Snackbar.make(findViewById(android.R.id.content), "User has not been registered", Snackbar.LENGTH_LONG).show();
                                   }
                               });
                           }
                       }
                   });
               } else {
                   //create snackbar to show that email is not valid
                   Snackbar.make(findViewById(android.R.id.content), "userID is not valid", Snackbar.LENGTH_LONG).show();
               }
           } else {
               //create snackbar to show that all fields are required
               Snackbar.make(findViewById(android.R.id.content), "All fields are required", Snackbar.LENGTH_LONG).show();
           }
       });

    }
}
