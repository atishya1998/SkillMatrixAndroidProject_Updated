package com.example.skillmatrix.ui.gallery;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

//public class GalleryViewModel extends ViewModel {
public class ManagersReporteesViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public ManagersReporteesViewModel() {
        mText = new MutableLiveData<>();
        //  mText.setValue("This is Reportees fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}