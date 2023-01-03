package com.example.skillmatrix;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skillmatrix.Adapter.SkillsAdapter;
import com.example.skillmatrix.Model.Skills;
import com.google.android.material.slider.Slider;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class AddandEditMyProfile extends AppCompatActivity implements SkillsAdapter.OnContactClickListener {
    TextInputLayout addressLayout, bloodLayout, phoneLayout, expLayout;
    TextInputEditText address, blood, phone, exp;
    FirebaseFirestore fireStore;
    Slider MyRating;
    Map<String, Object> rating;
    RecyclerView recyclerView;
    String id , role;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addand_edit_my_profile);
        addressLayout = findViewById(R.id.addressLayout);
        address = findViewById(R.id.addressET);
        bloodLayout = findViewById(R.id.bloodLayout);
        blood = findViewById(R.id.bloodET);
        phoneLayout = findViewById(R.id.phoneLayout);
        phone = findViewById(R.id.phoneET);
        expLayout = findViewById(R.id.yearLayout);
        exp = findViewById(R.id.yearET);
        Button save = findViewById(R.id.saveBtn);
        MyRating = findViewById(R.id.seekBar);
        recyclerView = findViewById(R.id.skillsRV);
        Button addSkill = findViewById(R.id.addBtn);
        EditText skillName = findViewById(R.id.skillET);
        EditText skillRating = findViewById(R.id.levelET);
        //
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        fireStore = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        String addressText = intent.getStringExtra("address");
        String bloodText = intent.getStringExtra("blood");
        String phoneText = intent.getStringExtra("phone");
        String expText = intent.getStringExtra("exp");
        id = intent.getStringExtra("id");
        role = intent.getStringExtra("role");
        rating = (Map<String, Object>) intent.getSerializableExtra("rating");
        ArrayList<Skills> skills = intent.getParcelableArrayListExtra("skills");

        address.setText(addressText);
        blood.setText(bloodText);
        phone.setText(phoneText);
        exp.setText(expText);
        MyRating.setValue(Float.parseFloat(rating.get("myRating")+""));

        //Skills Adapter and Recycler View
        SkillsAdapter skillsAdapter = new SkillsAdapter(skills, this, this);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        //printing the divider line between the items
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                recyclerView.getContext(),
                linearLayoutManager.getOrientation()
        );
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(skillsAdapter);


        //this code is for the swipe to delete
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT ) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.LEFT) { //swipe left to delete
                    Dialog dialog = new Dialog(AddandEditMyProfile.this); //creating a dialog to confirm the delete
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialog.setContentView(R.layout.dialog);
                    Button delete = dialog.findViewById(R.id.delete);
                    Button cancel = dialog.findViewById(R.id.cancel);
                    TextView title = dialog.findViewById(R.id.skillName);
                    TextView dialogTitle = dialog.findViewById(R.id.dialogTitle);
                    dialogTitle.setText("Are you sure you want to delete this skill?"); //setting the title of the dialog
                    title.setText(skillsAdapter.getSkill(viewHolder.getAdapterPosition()).getSkillName());
                    delete.setOnClickListener(v -> {
                        skillsAdapter.deleteSkill(viewHolder.getAdapterPosition());
                        Snackbar.make(recyclerView, "Skill Deleted", Snackbar.LENGTH_SHORT).show();
                        dialog.dismiss();
                    });
                    cancel.setOnClickListener(v -> {
                        skillsAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    });
                    dialog.show();
                }
            }


            //this code is for displaying the icon and the text when the user swipes
            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(AddandEditMyProfile.this, R.color.transparent))
                        .addSwipeLeftActionIcon(R.drawable.delete)
                        .addSwipeLeftLabel("DELETE")
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(recyclerView);

        save.setOnClickListener(v -> {
            //fetching string from the edit text
            String addressText1 = address.getText().toString();
            String bloodText1 = blood.getText().toString();
            String phoneText1 = phone.getText().toString();
            String expText1 = exp.getText().toString();

            //checking if the user has entered the required fields
            if (addressText1.isEmpty() || bloodText1.isEmpty() || phoneText1.isEmpty() || expText1.isEmpty()) {
                if (addressText1.isEmpty()) {
                    addressLayout.setError("Address is required");
                }
                if (bloodText1.isEmpty()) {
                    bloodLayout.setError("Blood Group is required");
                }
                if (phoneText1.isEmpty()) {
                    phoneLayout.setError("Phone Number is required");
                }
                if (expText1.isEmpty()) {
                    expLayout.setError("Experience is required");
                }
            } else {
                //if the user has entered all the required fields then we will save the data to the database
                Map<String, Object> newProfile = Map.of(
                        "address", addressText1,
                        "blood", bloodText1,
                        "phone", phoneText1,
                        "yearsOfExperience", expText1
                );
                rating.replace("myRating", (int) MyRating.getValue()); //replacing the rating of the user as per slider value
                int myRating = (int) MyRating.getValue();
                rating.replace("overallRating", ((myRating + Integer.parseInt(""+rating.get("managerRating")))/2)); //calculating the overall rating
                ArrayList<Skills> skills1 = skillsAdapter.getSkills();
                Map<String, Object> skillsMap = new HashMap<>();
                for (Skills skill : skills1) {
                    //create map of skills arraylist
                    skillsMap.put(skill.getSkillName(), skill.getSkillLevel());
                }
                //updating the data to the database
                fireStore.collection("EmployeeSkillBase").document(id).update("skills", skillsMap);
                fireStore.collection("EmployeeSkillBase").document(id).update("rating", rating);
                fireStore.collection("EmployeeSkillBase").document(id).update("personalInformation",newProfile);
                //
                Snackbar.make(v, "Profile Updated", Snackbar.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Intent intent1 = new Intent(AddandEditMyProfile.this, MainActivity.class);
                        intent1.putExtra("id", id);
                        intent1.putExtra("role", role);
                        startActivity(intent1);
                        finish();
                    }
                }).start();
            }
        });
        //
        addSkill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String skillNameText = skillName.getText().toString();
                String skillRatingText = skillRating.getText().toString();
                if (skillNameText.isEmpty() || skillRatingText.isEmpty()){
                    Snackbar.make(v, "Please fill all the fields", Snackbar.LENGTH_LONG).show();
                }else {
                    if (Integer.parseInt(skillRatingText) > 10 || Integer.parseInt(skillRatingText) < 1) {
                        Snackbar.make(v, "Please enter a valid rating", Snackbar.LENGTH_LONG).show();
                    } else {
                        Skills newSkill = new Skills(skillNameText, Integer.parseInt(skillRatingText));
                        skills.add(newSkill);
                        skillsAdapter.notifyItemInserted(skills.size() - 1); //notifying the adapter that a new skill has been added
                        skillName.setText("");
                        skillRating.setText("");
                    }
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        //on back pressed we will go to the main activity and pass the id and role of the user to the main activity
        Intent intent1 = new Intent(AddandEditMyProfile.this, MainActivity.class);
        intent1.putExtra("id", id);
        intent1.putExtra("role", role);
        startActivity(intent1);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); //on up button pressed we execute same code as back button without any super methods.
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(Skills skills) {
        //used on click listener for the skills adapter
        Toast.makeText(this, skills.getSkillName()+" Clicked", Toast.LENGTH_SHORT).show();
    }
}