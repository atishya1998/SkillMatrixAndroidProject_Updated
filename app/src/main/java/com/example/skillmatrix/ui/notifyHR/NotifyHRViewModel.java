package com.example.skillmatrix.ui.notifyHR;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NotifyHRViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public NotifyHRViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Notify HR fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}