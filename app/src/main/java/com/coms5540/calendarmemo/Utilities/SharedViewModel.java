package com.coms5540.calendarmemo.Utilities;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

//Create a channel allow the CalendarFragment and SelectDayFragment to commute
public class SharedViewModel extends ViewModel {
    private final MutableLiveData<List<Event>> events = new MutableLiveData<>();

    public void setEvents(List<Event> eventList) {
        events.setValue(eventList);
    }

    public LiveData<List<Event>> getEvents() {
        return events;
    }
}
