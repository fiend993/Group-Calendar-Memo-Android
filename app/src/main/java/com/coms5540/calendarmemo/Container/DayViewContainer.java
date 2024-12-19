package com.coms5540.calendarmemo.Container;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.coms5540.calendarmemo.R;
import com.coms5540.calendarmemo.Utilities.Event;
import com.coms5540.calendarmemo.Utilities.SharedViewModel;
import com.kizitonwose.calendar.core.CalendarDay;
import com.kizitonwose.calendar.view.ViewContainer;

import java.util.Calendar;
import java.util.List;

//A class that use to pop each day cell in the clander
//Was used in CalendarFragment.java
public class DayViewContainer extends ViewContainer {
    //The textView set up date for single day
    public final TextView textView;
    //The day of this cell
    public CalendarDay day;
    //List of Event that in today
    public List<Event> list;
    //SharedViewModel use to create channel between CalendarFragment
    //and SelectDayFragment to exchange data
    public SharedViewModel sharedViewModel;

    public DayViewContainer(@NonNull View view) {
        super(view);
        textView = view.findViewById(R.id.calendarDayText);
        view.setOnClickListener(v -> {
            if (day != null){
                //if there is click on the cell, sent the list to the SelectedDayFragment
                sharedViewModel.setEvents(list);
            }
        });
    }
}
