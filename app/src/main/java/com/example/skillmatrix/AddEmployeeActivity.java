package com.example.skillmatrix;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skillmatrix.ui.gallery.MyListAdapter;
import com.example.skillmatrix.ui.gallery.MyListData;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class AddEmployeeActivity extends AppCompatActivity {
    private String selectedRadioBtn;
    MyListAdapter adapter;
    ArrayList<String> unassignedList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Add Employee");
        actionBar.setDisplayHomeAsUpEnabled(true);
        //
        ArrayList<String> remainingPositions = getIntent().getStringArrayListExtra("remainingPositions");
        String projectStr = getIntent().getStringExtra("project");
        final RadioGroup rg = findViewById(R.id.radioGroup); //creating radio group
        RecyclerView recyclerView = findViewById(R.id.employeeList);
        //
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //
        rg.setOrientation(RadioGroup.HORIZONTAL);// or RadioGroup.VERTICAL
        if (remainingPositions != null) {
            //adding radio buttons to the radio group
            for (int i = 0; i < remainingPositions.size(); i++) {
                //creating radio buttons dynamically and adding them to the radio group
                RadioButton rb = new RadioButton(this);
                rb.setText(remainingPositions.get(i).toUpperCase(Locale.ROOT));
                rb.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.appBlue)));
                rg.addView(rb); //the RadioButtons are added to the radioGroup instead of the layout
            }
        }

        //getting the selected radio button from the radio group
        rg.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton rb = (RadioButton) findViewById(checkedId);
            selectedRadioBtn = rb.getText().toString().toLowerCase(Locale.ROOT);
            adapter.addStrings(selectedRadioBtn, projectStr); //adding the selected radio button to the adapter
            adapter.addRadioGroup(rg, String.valueOf(checkedId)); //adding radio group to the adapter
            adapter.addUnassignedList(unassignedList); //adding unassigned list to the adapter
        });
        //
        db.collection("EmployeeBase").document(
                "unassigned").get().addOnSuccessListener(documentSnapshot -> {
                    unassignedList = (ArrayList<String>) Objects.requireNonNull(documentSnapshot.getData()).get("unassgined");
                    ArrayList<MyListData> myListData = new ArrayList<>();
                    for (int i = 0; i < Objects.requireNonNull(unassignedList).size(); i++) {
                        myListData.add(new MyListData(unassignedList.get(i), R.drawable.icons8_reportee, "")); //adding data to the list
                    }
                    adapter = new MyListAdapter(AddEmployeeActivity.this, myListData, "unassigned");
                    recyclerView.setLayoutManager(new LinearLayoutManager(AddEmployeeActivity.this));
                    recyclerView.setAdapter(adapter);
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}