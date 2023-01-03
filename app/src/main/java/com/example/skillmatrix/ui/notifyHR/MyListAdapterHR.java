package com.example.skillmatrix.ui.notifyHR;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skillmatrix.MainActivity;
import com.example.skillmatrix.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class MyListAdapterHR extends  RecyclerView.Adapter<MyListAdapterHR.ViewHolder> {
    private final MyListDataHR[] listdata;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String[] mngr = { "12346_Matthew","12352_Andrew" };
    // RecyclerView recyclerView;
    public MyListAdapterHR(MyListDataHR[] listdata) {
        this.listdata = listdata;
    }

  
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.list_item_1, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final MyListDataHR myListData = listdata[position];
        holder.textView.setText(listdata[position].getDescription());
        holder.imageView.setImageResource(listdata[position].getImgId());
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "click on Employee: " + myListData.getDescription(), Toast.LENGTH_LONG).show();

                final String body,subject,cc;
                AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
                alert.setTitle("Assign Manager");

                LinearLayout layout = new LinearLayout(view.getContext());
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText manager_id = new EditText(view.getContext());
                manager_id.setHint("Enter Manager ID");
                layout.addView(manager_id);



                alert.setView(layout);
                alert.setPositiveButton("Assign", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String manager_name = manager_id.getText().toString();
                        Toast.makeText(view.getContext(), "click on Employee:"+manager_name+ "--"+ myListData.getDescription(), Toast.LENGTH_LONG).show();
                        db.collection("EmployeeSkillBase").document(" "+myListData.getDescription()).update("manager", manager_name);

                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // what ever you want to do with No option.
                    }
                });

                alert.show();



                // Intent i = new Intent(this.getApplicationContext(), MyProfileViewModel.class);
                // startActivity(i);
            }
        });
    }




    public int getItemCount() {
        return listdata.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;
        public RelativeLayout relativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.imageView);
            this.textView = itemView.findViewById(R.id.textView);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
        }
    }
}
