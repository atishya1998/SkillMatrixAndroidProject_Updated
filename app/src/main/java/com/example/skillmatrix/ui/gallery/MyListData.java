package com.example.skillmatrix.ui.gallery;

public class MyListData {
    private String description;
    private int imgId;
    private String role; //added role to the class for displaying that on project and requirements page

    public MyListData(String description, int imgId, String role) {
        this.description = description;
        this.imgId = imgId;
        this.role = role;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}