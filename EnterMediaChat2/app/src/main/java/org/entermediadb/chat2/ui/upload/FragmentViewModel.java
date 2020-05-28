package org.entermediadb.chat2.ui.upload;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FragmentViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public FragmentViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Welcome!");

    }

    public LiveData<String> getText() {
        return mText;
    }
}