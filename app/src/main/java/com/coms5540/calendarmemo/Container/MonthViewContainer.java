package com.coms5540.calendarmemo.Container;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.kizitonwose.calendar.view.ViewContainer;

//This use to pop the month and year title in the calendar
//was use in CalendarFragment and Create Activity
public class MonthViewContainer extends ViewContainer {

    public ViewGroup titlesContainer;
    public MonthViewContainer(@NonNull View view) {
        super(view);
        this.titlesContainer = (ViewGroup) view;
    }
}
