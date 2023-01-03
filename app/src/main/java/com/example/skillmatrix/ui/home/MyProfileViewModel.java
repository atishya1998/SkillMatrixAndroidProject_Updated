package com.example.skillmatrix.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

//public class HomeViewModel extends ViewModel {
public class MyProfileViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public MyProfileViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is My Profile fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}