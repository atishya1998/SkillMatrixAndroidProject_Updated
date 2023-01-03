package com.example.skillmatrix.ui.gallery;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skillmatrix.R;
import com.example.skillmatrix.databinding.FragmentGalleryBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//public class ManagersReporteesFragment extends Fragment {
public class ManagersReporteesFragment extends Fragment {
    protected static final String ACTIVITY_NAME = "ManagersReporteesFragment";
    public static String rootPath = "EmployeeBase";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<String> empList = new ArrayList<String>();
    private FragmentGalleryBinding binding;
// ...

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // setHasOptionsMenu(true);
        ManagersReporteesViewModel galleryViewModel =
                new ViewModelProvider(this).get(ManagersReporteesViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        Bundle bundle = NavHostFragment.findNavController(this).getGraph().getArguments();

        final TextView textView = binding.textGallery;
        galleryViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);


        //getting the data from bundle
        Bundle bundle = this.getArguments();
        //if bundle is not null
        if (bundle != null) {
            binding.textGallery.setText(bundle.getString("id"));
        } else {
            binding.textGallery.setText("No data");
        }


        //fetching the data from firebase and displaying it in recycler view
        db.collection(rootPath).document("managers")
                .get().addOnSuccessListener(documentSnapshot -> {
                    Map<String, Object> obj = documentSnapshot.getData();
                    Map<String, Object> obj1 = (Map<String, Object>) obj.get(bundle.getString("id").trim());
                    ArrayList<String> arr = (ArrayList<String>) obj1.get("reportees");

                    binding.textGallery.setText("Reportees of " + bundle.getString("id") + " are: ");

                    for (String s : arr) {
                        empList.add(s);
                    }
                    populateReporteeList();
                });


//
        return root;
    }

    public void populateReporteeList() {
        ArrayList<MyListData> myListData = new ArrayList<MyListData>();
        for (int i = 0; i < empList.size(); i++) {
            myListData.add(new MyListData(empList.get(i), R.drawable.icons8_reportee_1, "reportee"));
        }
        RecyclerView recyclerView = requireView().findViewById(R.id.recyclerView);
        MyListAdapter adapter = new MyListAdapter(requireContext(), myListData, "reportee");
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