package com.example.skillmatrix.ui.notifyHR;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skillmatrix.R;
import com.example.skillmatrix.databinding.FragmentNotifyhrBinding;
import com.example.skillmatrix.ui.gallery.MyListAdapter;
import com.example.skillmatrix.ui.gallery.MyListData;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NotifyHRFragment extends Fragment {

    public static String rootPath = "EmployeeBase";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public List<String> unassignedList = new ArrayList<String>();

    private FragmentNotifyhrBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotifyHRViewModel galleryViewModel =
                new ViewModelProvider(this).get(NotifyHRViewModel.class);


        binding = FragmentNotifyhrBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        //fetching the data from firebase and displaying it in recycler view
        //  db.collection("hr_management").document("unassigned").get().addOnSuccessListener(documentSnapshot -> {
        //      unassignedList = (ArrayList<String>) Objects.requireNonNull(documentSnapshot.getData()).get("unassigned");

        db.collection("hr_management").document(
                "unassigned").get().addOnSuccessListener(documentSnapshot -> {
            unassignedList = (ArrayList<String>) Objects.requireNonNull(documentSnapshot.getData()).get("unassgined");
            MyListDataHR[] myListData = new MyListDataHR[unassignedList.size()];
            int i = 0;
            for (String emp : unassignedList) {

                myListData[i] = new MyListDataHR(emp, R.drawable.icons8_reportee_1);
                i++;
            }
            // Toast.makeText(getContext(), myListData.length, Toast.LENGTH_LONG).show();
            RecyclerView recyclerView = getView().findViewById(R.id.recyclerView);
            MyListAdapterHR adapter = new MyListAdapterHR(myListData);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);
            Log.i("************************", String.valueOf(unassignedList.size()));
        });

        //   binding.textGallery.setText("Reportees of " + bundle.getString("id") + " are: ");


        return root;
    }

    public void populateEmployeeList() {
        MyListDataHR[] myListData = new MyListDataHR[unassignedList.size()];
        int i = 0;
        for (String emp : unassignedList) {

            myListData[i] = new MyListDataHR(emp, R.drawable.icons8_reportee_1);
            i++;
        }
        // Toast.makeText(getContext(), myListData.length, Toast.LENGTH_LONG).show();
        RecyclerView recyclerView = getView().findViewById(R.id.recyclerView);
        MyListAdapterHR adapter = new MyListAdapterHR(myListData);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}