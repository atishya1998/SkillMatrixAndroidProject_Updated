package com.example.skillmatrix.ui.slideshow;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skillmatrix.AddEmployeeActivity;
import com.example.skillmatrix.R;
import com.example.skillmatrix.databinding.FragmentSlideshowBinding;
import com.example.skillmatrix.ui.gallery.MyListAdapter;
import com.example.skillmatrix.ui.gallery.MyListData;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class ProjectsRequirementsFragment extends Fragment {

    private FragmentSlideshowBinding binding;
    String projectStr = "";
    ArrayList<String> remainingPositions;
    ArrayList<String> unassignedList;
    Map<String, Object> positions;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    RecyclerView recyclerView;
    MyListAdapter adapter;
    ArrayList<MyListData> myListData;
    TextView textView;
    @Override
    public void onResume() {
        super.onResume();
        assert getArguments() != null;
        fetchData(getArguments().getString("id").trim());
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        recyclerView = binding.requirementsList;
        //

        //
        Bundle bundle = this.getArguments();
        assert bundle != null;
        String id = bundle.getString("id");
        textView = binding.remainingPositions;
        //
        fetchData(id);


        //this code is responsible for the swipe to delete functionality in recycler view
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT ) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
            @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAbsoluteAdapterPosition();
                if (direction == ItemTouchHelper.LEFT) { // Swiped left
                    if (!adapter.getItem(position).getRole().equals("manager")) {
                        //create a dialog box to confirm the deletion
                        Dialog dialog = new Dialog(requireActivity());
                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        dialog.setContentView(R.layout.dialog);
                        Button delete = dialog.findViewById(R.id.delete);
                        Button cancel = dialog.findViewById(R.id.cancel);
                        TextView title = dialog.findViewById(R.id.skillName);
                        TextView dialogTitle = dialog.findViewById(R.id.dialogTitle);
                        dialogTitle.setText("Remove Employee"); //set the title of the dialog box
                        title.setText("Remove " + adapter.getItem(position).getDescription() + " as a " + adapter.getItem(position).getRole());
                        delete.setOnClickListener(v -> {
                            Snackbar.make(recyclerView, "Employee removed from the role", Snackbar.LENGTH_SHORT).show();
                            unassignedList.add(myListData.get(position).getDescription()); //add the employee to the unassigned list
                            db.collection("EmployeeBase").document("unassigned").update("unassgined", unassignedList);
                            positions.replace(adapter.getItem(position).getRole(), ""); //remove the employee from the role
                            db.collection("EmployeeBase").document("projects").update(projectStr, positions);
                            remainingPositions.add(adapter.getItem(position).getRole()); //add the role to the remaining positions
                            adapter.removeItem(position); //remove the employee from the recycler view
                            dialog.dismiss(); //dismiss the dialog box
                        });
                        cancel.setOnClickListener(v -> {
                            adapter.notifyDataSetChanged(); //refresh the recycler view
                            dialog.dismiss();
                        });
                        dialog.show();
                    } else {
                        Snackbar.make(recyclerView, "Can't remove the manager", Snackbar.LENGTH_SHORT).show(); //show a snackBar if the manager is swiped
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            //this code is responsible for showing the background color and delete icon when the item is swiped
            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                //using the library to show the background color and delete icon
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transparent))
                        .addSwipeLeftActionIcon(R.drawable.delete)
                        .addSwipeLeftLabel("DELETE")
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(recyclerView); //attaching the swipe to delete functionality to the recycler view

        binding.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open new activity and in this activity display unassigned employees and user can add them to the project
                Intent intent = new Intent(getContext(), AddEmployeeActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("project", projectStr);
                intent.putStringArrayListExtra("remainingPositions", remainingPositions);
                startActivity(intent);
            }
        });
        return root;
    }

    @SuppressLint("SetTextI18n")
    //this method is responsible for fetching the data from the database
    public void fetchData(String id){
        //first fetch the project name from the managers document
        db.collection("EmployeeBase").document("managers").get().addOnSuccessListener(documentSnapshot0 -> {
            Map<String, Object> project = documentSnapshot0.getData();
            assert project != null;
            Map<String, Object> project1 = (Map<String, Object>) project.get(id.trim());
            assert project1 != null;
            textView.setText(Objects.requireNonNull(project1.get("project")).toString()+"\nBudget: $"+project1.get("projectBudget").toString());
            projectStr = Objects.requireNonNull(project1.get("project")).toString();
            binding.desc.setText("Description: "+ Objects.requireNonNull(project1.get("projectDesc")));
            db.collection("EmployeeBase").document(
                    "unassigned").get().addOnSuccessListener(documentSnapshot -> {
                unassignedList = (ArrayList<String>) Objects.requireNonNull(documentSnapshot.getData()).get("unassgined");
            });

            //after fetching the project name, fetch the remaining positions from the projects document
            db.collection("EmployeeBase").document("projects").get().addOnSuccessListener(documentSnapshot1 -> {
                positions = (Map<String, Object>) Objects.requireNonNull(documentSnapshot1.getData()).get(project1.get("project"));
                String[] requiredPositions = {"analyst", "dba", "developer", "lead", "manager", "tester"};
                remainingPositions = new ArrayList<>();
                myListData = new ArrayList<>();
                for (String position : requiredPositions) {
                    assert positions != null;
                    if (Objects.requireNonNull(positions.get(position)).toString().isEmpty()) {
                        remainingPositions.add(position);
                    } else {
                        myListData.add(new MyListData(Objects.requireNonNull(positions.get(position)).toString(), R.drawable.icons8_reportee, position));
                    }
                }
                //after fetching the remaining positions, fetch the employees from the employees document
                adapter = new MyListAdapter(getContext(), myListData, "project");
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(adapter);

                //show the remaining positions in the text view as Error
                if (remainingPositions.size() != 0) {
                    textView.setError("Remaining Positions: " + remainingPositions.size());
                    textView.setOnClickListener(v -> {
                        textView.setError(null);
                        new Handler().postDelayed(() -> textView.setError("Remaining Positions: "+remainingPositions.size()), 3000); //show the error for 3 seconds
                    });
                }

            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}