package com.example.skillmatrix.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Skills implements Parcelable {
    private String skillName;
    private int skillLevel;

    //constructor
    public Skills(String skillName, int skillLevel) {
        this.skillName = skillName;
        this.skillLevel = skillLevel;
    }

    protected Skills(Parcel in) {
        skillName = in.readString();
        skillLevel = in.readInt();
    }

    //getters and setters
    public static final Creator<Skills> CREATOR = new Creator<Skills>() {
        @Override
        public Skills createFromParcel(Parcel in) {
            return new Skills(in);
        }

        @Override
        public Skills[] newArray(int size) {
            return new Skills[size];
        }
    };

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public int getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(int skillLevel) {
        this.skillLevel = skillLevel;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(skillName);
        dest.writeInt(skillLevel);
    }
}
