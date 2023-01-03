package com.example.skillmatrix.ui.slideshow;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProjectsRequirementsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ProjectsRequirementsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Projects/Requirements & Risks fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}