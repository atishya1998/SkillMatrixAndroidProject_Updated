package com.example.skillmatrix.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skillmatrix.Model.Skills;
import com.example.skillmatrix.R;

import java.util.ArrayList;

//this adapter is used to display the skills in the edit profile activity
public class SkillsAdapter extends RecyclerView.Adapter<SkillsAdapter.ViewHolder> {

    private static ArrayList<Skills> skillsArrayList;
    private Context context;
    private OnContactClickListener onContactClickListener;
    ArrayList<Skills> temp;


    //takes the skills arraylist and the context, and the onContactClickListener as parameters in the constructor
    public SkillsAdapter(ArrayList<Skills> skills, Context context, OnContactClickListener onContactClickListener) {
        SkillsAdapter.skillsArrayList = skills;
        this.context = context;
        this.onContactClickListener = onContactClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflates the view
        View view = LayoutInflater.from(context).inflate(R.layout.skill_recycler_view, parent, false);
        return new ViewHolder(view, onContactClickListener);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //sets the skill name and the skill level
        Skills skills = skillsArrayList.get(position);
        holder.Skill.setText(skills.getSkillName());
        holder.Level.setText(skills.getSkillLevel()+"");
    }

    public void addSkill(Skills skills){
        //adds a skill to the skills arraylist
        skillsArrayList.add(skills);
        notifyItemInserted(skillsArrayList.size());
    }

    //removes a skill from the skills arraylist
    public void deleteSkill(int position){
        skillsArrayList.remove(position);
        notifyItemRemoved(position);
    }

    //returns the particular skill from the skills arraylist
    public Skills getSkill(int position){
        return skillsArrayList.get(position);
    }


    public ArrayList<Skills> getSkills(){
        return skillsArrayList;
    }


    @Override
    public int getItemCount() {
        return skillsArrayList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView Skill;
        TextView Level;
        OnContactClickListener onContactClickListener;

        public ViewHolder(@NonNull View itemView, OnContactClickListener onContactClickListener) {
            super(itemView);
            Skill = itemView.findViewById(R.id.skillTV);
            Level = itemView.findViewById(R.id.levelTV);
            this.onContactClickListener = onContactClickListener; //initializes the onContactClickListener
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onContactClickListener.onItemClick(SkillsAdapter.skillsArrayList.get(getAdapterPosition()));
        }
    }

    //this interface is used to implement the on click listener
    public interface OnContactClickListener {
        void onItemClick(Skills skills);
    }
}
