package com.coms5540.calendarmemo.Container;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.coms5540.calendarmemo.CreateActivity;
import com.coms5540.calendarmemo.R;
import com.kizitonwose.calendar.core.CalendarDay;
import com.kizitonwose.calendar.core.DayPosition;
import com.kizitonwose.calendar.view.CalendarView;
import com.kizitonwose.calendar.view.ViewContainer;

import java.time.LocalDate;

//This class is almost identity to the DayViewContainer
//Except the difference onclick logic
//Was use in CreateActivity (Not in the SelectDayFragment)
public class selectDayViewContainer extends ViewContainer {
    //The textView that display the cell
    public final TextView t;
    //The day of this cell
    public CalendarDay day;
    //The CreateActivity where this calendar are drawn on
    public CreateActivity activity;
    //The reference of the calendar calendarView
    public CalendarView calendarView;
    public selectDayViewContainer(@NonNull View view) {
        super(view);
        t = view.findViewById(R.id.calendarDayText);
        view.setOnClickListener(v->{
            //Check if the day is in the current month (to avoid the end date for last month
            //and start date for next month that show on the calendar are selected
            if(day.getPosition() == DayPosition.MonthDate){
                //Track the dat that currently selected
                LocalDate currentSelected = activity.getSelectedDay();
                activity.setDate(day.getDate());
                //update the new selected day
                calendarView.notifyDateChanged(day.getDate());
                if(currentSelected != null){
                    //clean the background color of previous day
                    calendarView.notifyDateChanged(currentSelected);
                }
            }
        });
    }
}
