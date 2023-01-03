package com.example.skillmatrix.ui.scheduler;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SchedulerViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public SchedulerViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Scheduler fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}