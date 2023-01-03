package com.example.skillmatrix;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        //temporarily adding this code to make app start in light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
        //
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        EditText password = findViewById(R.id.password);
        EditText email = findViewById(R.id.email);
        TextView register = findViewById(R.id.registerTV);

        Button loginButton = findViewById(R.id.LoginButton);
        ProgressBar progressBar = findViewById(R.id.progressBar);

        loginButton.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE); //show progress bar

            //check if email and password are empty
            if ((!password.getText().toString().isEmpty()) || (!email.getText().toString().isEmpty() && email.getText().toString().contains("@"))) {

                    //fetch data from firestore

                                                                                   //remove this space after changing the database / currently you can not login with email whose id is without space
                                                                                   //I created this code as per manager id in database, I also checked it without space but it is not feasible to code both.
                    fireStore.collection("users").document(" "+email.getText().toString().split("@")[0]).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult().exists()) {
                                if (task.getResult().get("password").toString().equals(password.getText().toString())) { //check if password is correct
                                    //login successful
                                    Intent intent = new Intent(Login.this, MainActivity.class);
                                    intent.putExtra("email", email.getText().toString());
                                    intent.putExtra("id", task.getResult().get("id").toString());
                                    intent.putExtra("role", task.getResult().get("role").toString());

                                    startActivity(intent);
                                    finish();
                                } else {
                                    Snackbar.make(view, "Email or Password is incorrect", Snackbar.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            } else {
                                Snackbar.make(view, "User does not exist", Snackbar.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        } else {
                            Snackbar.make(view, "Error: " + task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }

                    });
            } else {
                if (password.getText().toString().isEmpty()) {
                    Snackbar.make(view, "Please enter password", Snackbar.LENGTH_LONG)
                            .show();
                }
                if (email.getText().toString().isEmpty()) {
                    Snackbar.make(view, "Please enter email", Snackbar.LENGTH_LONG)
                            .show();
                }
                progressBar.setVisibility(View.GONE);
            }

        });

        register.setOnClickListener(view -> {
            Intent intent = new Intent(Login.this, Register.class);
            startActivity(intent);
        });

    }
}
