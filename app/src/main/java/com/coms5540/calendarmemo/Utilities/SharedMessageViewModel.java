package com.coms5540.calendarmemo.Utilities;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

//A data channel just use for pass simple message between activity
public class SharedMessageViewModel extends ViewModel {
    private final MutableLiveData<String> message = new MutableLiveData<>();

    public void setMessage(String msg) {
        message.postValue(msg);
    }

    public LiveData<String> getMessage() {
        return message;
    }
}
