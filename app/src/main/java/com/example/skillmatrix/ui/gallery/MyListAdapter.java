package com.example.skillmatrix.ui.gallery;

//import android.support.v7.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skillmatrix.R;
import com.example.skillmatrix.ReporteesDetailActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


public class
MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder> {
    private final ArrayList<MyListData> listdata;
    private final Context context;
    private String activityStr;
    private String selectedRadioBtn;
    private String projectStr;
    private RadioGroup radioGroup;
    private String RadioId;
    private ArrayList<String> unassignedList;

    // RecyclerView recyclerView;
    public MyListAdapter(Context context, ArrayList<MyListData> listdata, String activityStr) {
        this.listdata = listdata;
        this.context = context;
        this.activityStr = activityStr;
    }

    //add radio button text and project name to the class for adding employees page
    public void addStrings(String selectedRadioBtn, String projectStr) {
        this.projectStr = projectStr;
        this.selectedRadioBtn = selectedRadioBtn;
    }

    //adding radio group to the class so that we can remove the radio button after adding employee to that particular role
    public void addRadioGroup(RadioGroup radioGroup, String RadioId) {
        this.radioGroup = radioGroup;
        this.RadioId = RadioId;
    }

    //adding unassigned list to the class so that we can remove the employee from the unassigned list after adding employee to that particular role
    public void addUnassignedList(ArrayList<String> unassignedList) {
        this.unassignedList = unassignedList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final MyListData myListData = listdata.get(position);
        holder.textView.setText(listdata.get(position).getDescription());
        holder.imageView.setImageResource(listdata.get(position).getImgId());
        
        holder.relativeLayout.setOnClickListener(view -> {
            //remove this if else after solving db spaces
            Intent intent = new Intent(context, ReporteesDetailActivity.class);
            if (!myListData.getDescription().startsWith(" ")) {
                intent.putExtra("empName", " " + myListData.getDescription()); //remove space after solving db spaces
                // intent.putExtra("empName", myListData.getDescription());

            } else {
                intent.putExtra("empName", myListData.getDescription().trim());
            }
            context.startActivity(intent);
        });


        //role textView is not visible while there is no role available in myListData model, role is only displayed on project and requirements page.
        if (myListData.getRole().isEmpty()){
            holder.role.setVisibility(View.GONE);
        } else {
            holder.role.setText(myListData.getRole());
        }
        //if the activity is add employee then we need to add the employee to the project and remove the radio button
        //so + sign will be visible and on click of + sign we will add the employee to the project
        if (activityStr.equals("unassigned")){
            holder.add.setVisibility(View.VISIBLE);
            holder.add.setOnClickListener(view -> {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("EmployeeBase").document("projects").update(projectStr+"."+selectedRadioBtn, myListData.getDescription());
                unassignedList.remove(myListData.getDescription());
                db.collection("EmployeeBase").document("unassigned").update("unassgined", unassignedList);
                //
                listdata.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, listdata.size());
                radioGroup.removeView(radioGroup.findViewById(Integer.parseInt(RadioId)));

            });
        }

        else {
            //otherwise we will not show the + sign
            holder.add.setVisibility(View.GONE);
        }
    }

    public MyListData getItem(int position) {
        return listdata.get(position);
    }

    //add a method to remove the item from the list
    public void removeItem(int position) {
        try {
            listdata.remove(position);
            notifyItemRemoved(position);
        } catch (Exception e) {
           notifyDataSetChanged();
        }

    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView, role;
        public CardView relativeLayout;
        public ImageButton add;
        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.imageView);
            this.textView = itemView.findViewById(R.id.navEmail);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
            this.role = itemView.findViewById(R.id.navRole);
            this.add = itemView.findViewById(R.id.imageButton);
        }
    }
}