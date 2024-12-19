package com.coms5540.calendarmemo.Utilities;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import kotlinx.coroutines.CoroutineScope;

//A data channel just use for pass simple message between activity
public class SharedMessageViewModel extends AndroidViewModel {
    private final MutableLiveData<String> message = new MutableLiveData<>();

    public SharedMessageViewModel(@NonNull Application application){
        super(application);
    }

    public void setMessage(String msg) {
        message.postValue(msg);
    }

    public LiveData<String> getMessage() {
        return message;
    }
}
