package com.example.skillmatrix.ui.home;

import android.annotation.SuppressLint;
import android.app.Person;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.anychart.APIlib;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.chart.common.listener.Event;
import com.anychart.chart.common.listener.ListenersInterface;
import com.anychart.charts.Cartesian;
import com.anychart.charts.Pie;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Align;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.LegendLayout;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.example.skillmatrix.AddandEditMyProfile;
import com.example.skillmatrix.Login;
import com.example.skillmatrix.Model.Skills;
import com.example.skillmatrix.R;
import com.example.skillmatrix.databinding.FragmentHomeBinding;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.protobuf.Any;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MyProfileFragment extends Fragment {

    TextView address, blood, phone, exp, project, projectAssigned, role;
    String addressString, bloodString, phoneString, expString, projectString, projectAssignedString, roleString;
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    Map<String, Object> rating;
    ArrayList<Skills> skills;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        //getting the data from the bundle
        Bundle bundle = getArguments();


        //creating chart view
        AnyChartView anyChartView = rootView.findViewById(R.id.any_chart_view);
        AnyChartView anyChartView2 = rootView.findViewById(R.id.any_chart_view2);
        //while using more than one chart view, we have to first activate the first one and data to that one
        //after that we can activate the second one and add data to it
        APIlib.getInstance().setActiveAnyChartView(anyChartView); //activating the first chart view
        Cartesian cartesian = AnyChart.column(); //creating the bar chart
        cartesian.animation(true);
        cartesian.title("ALL RATING");
        cartesian.xAxis(0).title("Skills");
        cartesian.yAxis(0).title("Rating");
        anyChartView.setChart(cartesian); //setting the chart to the first chart view

        APIlib.getInstance().setActiveAnyChartView(anyChartView2); //activating the second chart view
        Pie pie = AnyChart.pie(); //creating the pie chart
        pie.animation(true);
        pie.title("SKILLS RATING");
        anyChartView2.setChart(pie); //setting the chart to the second chart view

        address = rootView.findViewById(R.id.address);
        blood = rootView.findViewById(R.id.blood);
        phone = rootView.findViewById(R.id.phone);
        exp = rootView.findViewById(R.id.exp);
        project = rootView.findViewById(R.id.project);
        projectAssigned = rootView.findViewById(R.id.projectAssigned);
        role = rootView.findViewById(R.id.role);

        //fetch Map data from collection EmployeeSkillBase and document id 12346_Matthew
        if (bundle != null) {
            firestore.collection("EmployeeSkillBase").document(bundle.getString("id")).get().addOnSuccessListener(documentSnapshot -> {

                Map<String, Object> doc = documentSnapshot.getData();
                Map<String, Object> personalInformation = (Map<String, Object>) doc.get("personalInformation");
                addressString = Objects.requireNonNull(personalInformation.get("address")).toString();
                bloodString = Objects.requireNonNull(personalInformation.get("blood")).toString();
                phoneString = Objects.requireNonNull(personalInformation.get("phone")).toString();
                expString = Objects.requireNonNull(personalInformation.get("yearsOfExperience")).toString();
                address.setText("Address: " + addressString);
                blood.setText("Blood Group: " + bloodString);
                phone.setText("Phone: " + phoneString);
                exp.setText("Years of Exp.: " + expString);
                project.setText("Project: " + Objects.requireNonNull(doc.get("project")).toString());
                projectAssigned.setText("Assigned to project: " + Objects.requireNonNull(doc.get("project_assigned")).toString());
                //
                rating = (Map<String, Object>) doc.get("rating");
                //
                role.setText("Role: " + doc.get("role").toString());
                //
                APIlib.getInstance().setActiveAnyChartView(anyChartView);
                List<DataEntry> data = new ArrayList<>();
                data.add(new ValueDataEntry("Manager Rating", Integer.parseInt(Objects.requireNonNull(rating.get("managerRating")).toString())));
                data.add(new ValueDataEntry("My Rating", Integer.parseInt(Objects.requireNonNull(rating.get("myRating")).toString())));
                data.add(new ValueDataEntry("Overall Rating", Integer.parseInt(Objects.requireNonNull(rating.get("overallRating")).toString())));
                cartesian.column(data); //after fetching the data, we are adding it to the chart


                APIlib.getInstance().setActiveAnyChartView(anyChartView2);
                String[] pieChartData = Objects.requireNonNull(doc.get("skills")).toString().replace("{", "")
                        .replace("}", "").split(",");
                //iterate through the array and split the key and value with =

                // {"java",9}
                List<DataEntry> pieData = new ArrayList<>();
                skills = new ArrayList<>();
                try {
                    for (String s : pieChartData) {
                        String[] keyValue = s.split("=");
                        //add the key and value to the pie chart
                        pieData.add(new ValueDataEntry(keyValue[0], Integer.parseInt(keyValue[1])));
                        skills.add(new Skills(keyValue[0].trim(), Integer.parseInt(keyValue[1])));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                pie.data(pieData); //after fetching the data, we are adding it to the chart
            });
        }
            return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        com.example.skillmatrix.databinding.FragmentHomeBinding binding = null;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu); //inflating the menu to the toolbar for this fragment
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                //Start an activity…
                Log.d("Toolbar", "Version 2.4 by Atishya Reddy");
                Toast.makeText(requireContext(), "Version 2.4 by Atishya Reddy", Toast.LENGTH_SHORT).show();

                break;
            case R.id.action_logout:
                //Start an activity…

                Intent i = new Intent(requireContext(), Login.class);
                startActivity(i);
                requireActivity().finish();
                //going back to the login activity
                break;
            case R.id.action_edit_profile:
                //going to the edit profile activity
                //passing the data to the edit profile activity, which will be used to fill the edit text fields
                Intent i2 = new Intent(requireContext(), AddandEditMyProfile.class);
                i2.putExtra("id", getArguments().getString("id"));
                i2.putExtra("role", getArguments().getString("role"));
                i2.putExtra("address", addressString);
                i2.putExtra("blood", bloodString);
                i2.putExtra("phone", phoneString);
                i2.putExtra("exp", expString);
                i2.putExtra("rating", (Serializable) rating);
                i2.putParcelableArrayListExtra("skills", (ArrayList<? extends Parcelable>) skills);
                startActivity(i2);
                requireActivity().finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}