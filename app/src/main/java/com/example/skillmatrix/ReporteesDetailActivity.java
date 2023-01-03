package com.example.skillmatrix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.anychart.APIlib;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.charts.Pie;
import com.example.skillmatrix.Model.Skills;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.slider.Slider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ReporteesDetailActivity extends AppCompatActivity {

    TextView address, blood, phone, exp, project, projectAssigned, role;
    Slider slider;
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    Map<String, Object> rating;
    List<DataEntry> data = new ArrayList<>();
    Cartesian cartesian;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportees_detail);

        AnyChartView anyChartView = findViewById(R.id.any_chart_view_rep);
        AnyChartView anyChartView2 = findViewById(R.id.any_chart_view2_rep);
        // create a new instance of the chart
        APIlib.getInstance().setActiveAnyChartView(anyChartView);
        cartesian = AnyChart.column();
        cartesian.animation(true);
        cartesian.title("ALL RATING");
        cartesian.xAxis(0).title("Skills");
        cartesian.yAxis(0).title("Rating");
        anyChartView.setChart(cartesian);

        APIlib.getInstance().setActiveAnyChartView(anyChartView2);
        Pie pie = AnyChart.pie();
        pie.animation(true);
        pie.title("SKILLS RATING");
        anyChartView2.setChart(pie);

        address = findViewById(R.id.addressRep);
        blood = findViewById(R.id.bloodRep);
        phone = findViewById(R.id.phoneRep);
        exp = findViewById(R.id.expRep);
        project = findViewById(R.id.projectRep);
        projectAssigned = findViewById(R.id.projectAssignedRep);
        role = findViewById(R.id.roleRep);
        slider = findViewById(R.id.managerRatingRep);

        Intent intent = getIntent();
        String id = intent.getStringExtra("empName");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(id);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        firestore.collection("EmployeeSkillBase").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                rating = (Map<String, Object>) documentSnapshot.get("rating");
                Map<String, Object> personalDetails = (Map<String, Object>) documentSnapshot.get("personalInformation");
                if (personalDetails != null) {
                    address.setText("Address: " + personalDetails.get("address"));
                    blood.setText("Blood: " + Objects.requireNonNull(personalDetails.get("blood")).toString());
                    phone.setText("Phone: " + Objects.requireNonNull(personalDetails.get("phone")).toString());
                    exp.setText("Years of Exp.: " + Objects.requireNonNull(personalDetails.get("yearsOfExperience")).toString());
                    project.setText("Project: " + documentSnapshot.getString("project"));
                    projectAssigned.setText("Assigned to project: " + documentSnapshot.getBoolean("projectAssigned"));
                    role.setText("Role: " + documentSnapshot.getString("role"));
                    slider.setValue(Float.parseFloat(Objects.requireNonNull(rating.get("managerRating")).toString()));
                    // same process as MyProfileActivity to display charts
                    APIlib.getInstance().setActiveAnyChartView(anyChartView);

                    data.add(new ValueDataEntry("Manager Rating", Integer.parseInt(rating.get("managerRating").toString())));
                    data.add(new ValueDataEntry("My Rating", Integer.parseInt(rating.get("myRating").toString())));
                    data.add(new ValueDataEntry("Overall Rating", Integer.parseInt(rating.get("overallRating").toString())));
                    cartesian.column(data);
                    //
                    APIlib.getInstance().setActiveAnyChartView(anyChartView2);


                    String[] pieChartData = documentSnapshot.getData().get("skills").toString().replace("{", "")
                            .replace("}", "").split(",");
                    //iterate through the array and split the key and value with =
                    List<DataEntry> pieData = new ArrayList<>();
                    ArrayList<Skills> skills = new ArrayList<>();
                    for (String s : pieChartData) {
                        String[] keyValue = s.split("=");
                        //add the key and value to the pie chart
                        pieData.add(new ValueDataEntry(keyValue[0], Integer.parseInt(keyValue[1])));
                        skills.add(new Skills(keyValue[0].trim(), Integer.parseInt(keyValue[1])));
                    }
                    pie.data(pieData);
                }

            }
        });


        //change manager rating and overall rating
        slider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                if (fromUser) {
                    //
                    APIlib.getInstance().setActiveAnyChartView(anyChartView);
                    data.clear();
                    cartesian.removeAllSeries();
//                    cartesian = AnyChart.column();
                    data.add(new ValueDataEntry("Manager Rating",(int) value));
                    data.add(new ValueDataEntry("My Rating", Integer.parseInt(rating.get("myRating").toString())));
                    data.add(new ValueDataEntry("Overall Rating", (int) ((value + Integer.parseInt(rating.get("myRating").toString())) / 2)));
                    cartesian.column(data);
                    //
                    firestore.collection("EmployeeSkillBase").document(id).update("rating.managerRating", (int) value);
                    //change overall rating
                    firestore.collection("EmployeeSkillBase").document(id).update("rating.overallRating", ((int) value + Integer.parseInt(rating.get("myRating").toString())) / 2);
                }
            }
        });





    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}