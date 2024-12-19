package com.coms5540.calendarmemo.Fragment;

import static android.content.Context.MODE_PRIVATE;
import static com.kizitonwose.calendar.core.ExtensionsKt.daysOfWeek;
import static com.kizitonwose.calendar.core.ExtensionsKt.firstDayOfWeekFromLocale;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.coms5540.calendarmemo.Container.DayViewContainer;
import com.coms5540.calendarmemo.Container.MonthViewContainer;
import com.coms5540.calendarmemo.R;
import com.coms5540.calendarmemo.Utilities.Event;
import com.coms5540.calendarmemo.Utilities.EventCallback;
import com.coms5540.calendarmemo.Utilities.HttpClientSingleton;
import com.coms5540.calendarmemo.Utilities.SharedMessageViewModel;
import com.coms5540.calendarmemo.Utilities.SharedViewModel;
import com.coms5540.calendarmemo.Utilities.Variable;
import com.kizitonwose.calendar.core.CalendarDay;
import com.kizitonwose.calendar.core.CalendarMonth;
import com.kizitonwose.calendar.core.DayPosition;
import com.kizitonwose.calendar.view.CalendarView;
import com.kizitonwose.calendar.view.MonthDayBinder;
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

//This fragment is attached on the MainActivity
//This class provide a calendar view that will mark
//the day with green color if that day have any memo
//exist
public class CalendarFragment extends Fragment {

    //Reference for the calendar
    CalendarView calendarView;
    //This HashMap store all the memo mark on this month
    //the key is two digit int for the day of the month ex.01, 12, 25
    //and the List<Event> is a list of corresponding events with that day
    private HashMap<String, List<Event>> monthEvent;

    //This SharedViewModel create a channel allow the
    //CalendarFragment sent the event list of select day
    //to SelectDayFragment for use to view
    private SharedViewModel sharedViewModel;
    //This display the current month of that calender current view at
    private YearMonth currentMonth;
    private SharedMessageViewModel sm;

    //Prepare View
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        //init the global variable
        calendarView = view.findViewById(R.id.calendarView);
        monthEvent = new HashMap<>();

        currentMonth = YearMonth.now();
        YearMonth startMonth = currentMonth.minusMonths(100);
        YearMonth endMonth = currentMonth.plusMonths(100);
        DayOfWeek firstDayOfWeek = firstDayOfWeekFromLocale();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        //Set up cell of each day in calendar
        calendarView.setDayBinder(new MonthDayBinder<DayViewContainer>() {
            @NonNull
            @Override
            public DayViewContainer create(@NonNull View view){
                return new DayViewContainer(view);
            }


            @Override
            public void bind(@NonNull DayViewContainer container, CalendarDay data) {
                //pass all the reference that DayViewContain require
                container.textView.setText(String.valueOf(data.getDate().getDayOfMonth()));
                container.sharedViewModel = sharedViewModel;
                container.day = data;
                String whichDay = data.getDate().format(formatter).substring(8,10);
                //check if the day is in the current month
                //if yes, check each day if that have a event list
                //if yes, mark that day with green
                //else mark white
                if(data.getPosition() == DayPosition.MonthDate){
                    if(getMonthEvent().containsKey(whichDay)){
                        container.textView.setBackgroundColor(android.graphics.Color.GREEN);
                        container.list = getMonthEvent().get(whichDay);
                    }else{
                        container.textView.setBackgroundColor(Color.WHITE);
                        container.list = new ArrayList<>();
                    }
                }else{
                    container.textView.setBackgroundColor(Color.WHITE);
                    container.list = new ArrayList<>();
                }
            }
        });

        calendarView.setup(startMonth,endMonth,firstDayOfWeek);
        calendarView.scrollToMonth(currentMonth);

        List<DayOfWeek> daysOfWeek = daysOfWeek();

        //set up the title of week day, title of month, title of year
        calendarView.setMonthHeaderBinder(new MonthHeaderFooterBinder<MonthViewContainer>() {
            @NonNull
            @Override
            public MonthViewContainer create(@NonNull View view) {
                return new MonthViewContainer(view);
            }

            @Override
            public void bind(@NonNull MonthViewContainer container, CalendarMonth data) {
                if(container.titlesContainer.getTag() == null){
                    container.titlesContainer.setTag(data.getYearMonth());
                    for(int index = 0; index < container.titlesContainer.getChildCount(); index++){
                        TextView textView = (TextView) container.titlesContainer.getChildAt(index);
                        DayOfWeek dayOfWeek = daysOfWeek.get(index);
                        String title = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault());
                        textView.setText(title);
                    }
                }
            }
        });

        //update the title when scroll to other month
        calendarView.setMonthScrollListener(new Function1<CalendarMonth, Unit>() {
            @Override
            public Unit invoke(CalendarMonth calendarMonth) {
                updateTitle();
                return Unit.INSTANCE;
            }
        });
        ImageButton imageButton = view.findViewById(R.id.imageButton);
        imageButton.setOnClickListener(this::refresh);
        return view;
    }

    //Observe if there is update on any item in the list
    //refresh the calendar
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sm = new ViewModelProvider(this).get(SharedMessageViewModel.class);
        sm.getMessage().observe(this, msg ->{
            if(msg != null){
                if(msg.equals("refresh")){
                    refresh();
                }
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        //This prepare the calendar for user that open the application
        //pull the update from the cloud
        LocalDate firstDay= currentMonth.atDay(1);
        LocalDate lastDay = currentMonth.atEndOfMonth();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String startDate = firstDay.format(formatter);
        String endDate = lastDay.format(formatter);
        getEvent(startDate, endDate, new EventCallback() {
            @Override
            public void onSuccess(JSONArray events) {
                try{
                    processEventList(events);
                    calendarView.notifyCalendarChanged();
                }catch (JSONException e){
                    Log.d("JSON","Fetch list error");
                }
            }
        });
    }

    //This is call every time when we scoll to a new month
    //pull the event of that month
    public void updateTitle(){
        currentMonth = Objects.requireNonNull(calendarView.findFirstVisibleMonth()).getYearMonth();
        TextView yearTitle = requireView().findViewById(R.id.year);
        TextView monthTitle = requireView().findViewById(R.id.month);
        yearTitle.setText(String.valueOf(currentMonth.getYear()));
        monthTitle.setText(currentMonth.getMonth().getDisplayName(TextStyle.FULL,Locale.getDefault()));
        monthEvent = new HashMap<>();
        LocalDate firstDay= currentMonth.atDay(1);
        LocalDate lastDay = currentMonth.atEndOfMonth();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String startDate = firstDay.format(formatter);
        String endDate = lastDay.format(formatter);
        getEvent(startDate, endDate, new EventCallback() {
            @Override
            public void onSuccess(JSONArray events) {
                try {
                    processEventList(events);
                    calendarView.notifyMonthChanged(currentMonth);
                } catch (JSONException e) {
                    requireActivity().runOnUiThread(() ->Toast.makeText(requireContext(),"List Process Error", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    //This method use to get the list of event that with the range from startDate
    //to endDate, and an EventCallback use to call back when the List is available
    //the list that get is a JSONArray
    public void getEvent(String startDate, String endDate, EventCallback callback){
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("AppPrefs",MODE_PRIVATE);
        String token = sharedPreferences.getString("token","error");
        if(token.equals("error")){
            throw new IllegalArgumentException("token not exists");
        }
        OkHttpClient client = HttpClientSingleton.getInstance();
        Request request = new Request.Builder()
                .url(Variable.host + Variable.event + startDate + "&endDate=" + endDate)
                .addHeader("Authorization", "Bearer " + token)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Fetch Events Failed", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String rsp = response.body().string();
                    try {
                        JSONArray rawList = new JSONArray(rsp);
                        requireActivity().runOnUiThread(() -> callback.onSuccess(rawList));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireContext(), "Error parsing events", Toast.LENGTH_SHORT).show()
                        );
                    }
                }else{
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), "Fetch Events Failed: " + response.message(), Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }

    //This method take in rawList to process and prepare the
    //HashSet for calendar
    //it will put event in different list base on the event date
    public void processEventList(JSONArray rawList) throws JSONException {
        for(int i = 0; i < rawList.length(); i++){
            JSONObject rawEvent = rawList.getJSONObject(i);
            //2022-12-01
            Event event = new Event(
                    rawEvent.getString("title"),
                    rawEvent.getString("description"),
                    rawEvent.getString("date").substring(0,10),
                    rawEvent.getString("createdBy"),
                    rawEvent.getString("group"),
                    rawEvent.getString("createdAt"),
                    rawEvent.getString("updatedAt"),
                    rawEvent.getString("_id")
            );
            String day = event.getDate().substring(8,10);
            if(!monthEvent.containsKey(day)){
                List<Event> dayEvents = new ArrayList<>();
                monthEvent.put(day,dayEvents);
            }
            boolean isDuplicate = false;
            for(int j = 0; j < Objects.requireNonNull(monthEvent.get(day)).size(); j++){
                if(Objects.requireNonNull(monthEvent.get(day)).get(j).getId().equals(event.getId())){
                    isDuplicate = true;
                }
            }
            if(!isDuplicate){
                Objects.requireNonNull(monthEvent.get(day)).add(event);
            }
        }
    }

    public HashMap<String, List<Event>> getMonthEvent(){
        return monthEvent;
    }

    //This use to refresh the calendar
    //this trigger when user click the top right conner refresh button
    public void refresh(View v){
        LocalDate firstDay= currentMonth.atDay(1);
        LocalDate lastDay = currentMonth.atEndOfMonth();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String startDate = firstDay.format(formatter);
        String endDate = lastDay.format(formatter);
        getEvent(startDate, endDate, new EventCallback() {
            @Override
            public void onSuccess(JSONArray events) {
                try {
                    processEventList(events);
                    calendarView.notifyMonthChanged(currentMonth);
                } catch (JSONException e) {
                    Toast.makeText(getActivity(),"List Process Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void refresh(){
        LocalDate firstDay= currentMonth.atDay(1);
        LocalDate lastDay = currentMonth.atEndOfMonth();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String startDate = firstDay.format(formatter);
        String endDate = lastDay.format(formatter);
        getEvent(startDate, endDate, new EventCallback() {
            @Override
            public void onSuccess(JSONArray events) {
                try {
                    processEventList(events);
                    calendarView.notifyMonthChanged(currentMonth);
                } catch (JSONException e) {
                    Toast.makeText(getActivity(),"List Process Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}