package com.coms5540.calendarmemo;

import static com.kizitonwose.calendar.core.ExtensionsKt.firstDayOfWeekFromLocale;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.coms5540.calendarmemo.Container.MonthViewContainer;
import com.coms5540.calendarmemo.Container.selectDayViewContainer;
import com.coms5540.calendarmemo.Utilities.HttpClientSingleton;
import com.coms5540.calendarmemo.Utilities.Variable;
import com.kizitonwose.calendar.core.CalendarDay;
import com.kizitonwose.calendar.core.CalendarMonth;
import com.kizitonwose.calendar.core.DayPosition;
import com.kizitonwose.calendar.view.CalendarView;
import com.kizitonwose.calendar.view.MonthDayBinder;
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.kizitonwose.calendar.core.ExtensionsKt.daysOfWeek;

//This class use to create a new memo on selected day
public class CreateActivity extends AppCompatActivity {

    //The date user selected
    LocalDate selectedDay;
    //The user decide witch group to post the memo
    String selectedGroup;
    //The title of the new post
    TextView title;
    //The description of the new post
    TextView description;
    //user id
    String id;
    //The calendar for use to select day
    CalendarView select;
    //Point to the month that calendar current display
    YearMonth currentMonth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        //go back button
        Button backButton = findViewById(R.id.createGoBack);
        backButton.setOnClickListener(v->finish());

        //read user profile to load up the group that user have access
        //and the userId
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs",MODE_PRIVATE);
        Set<String> groupSet = sharedPreferences.getStringSet("user_groups", new HashSet<>());
        id = sharedPreferences.getString("userId", "nulllllll");
        Spinner dropdownMenu = findViewById(R.id.groupDrownDown);

        //set up the dropdown menu for user choice the group
        List<String> groupList = new ArrayList<>(groupSet);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, groupList);
        dropdownMenu.setAdapter(adapter);
        dropdownMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGroup = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedGroup = parent.getItemAtPosition(0).toString();
            }
        });
        title = findViewById(R.id.newTitle);
        description = findViewById(R.id.newDescription);
        select = findViewById(R.id.daySelectCalendar);

        //set up the calendar
        currentMonth = YearMonth.now();
        YearMonth startMonth = currentMonth.minusMonths(100);
        YearMonth endMonth = currentMonth.plusMonths(100);
        DayOfWeek firstDayOfWeek = firstDayOfWeekFromLocale();

        select.setDayBinder(new MonthDayBinder<selectDayViewContainer>() {
            @NonNull
            @Override
            public selectDayViewContainer create(@NonNull View view) {
                return new selectDayViewContainer(view);
            }

            @Override
            public void bind(@NonNull selectDayViewContainer container, CalendarDay calendarDay) {
                //mark the select day as green, all other day is white
                container.t.setText(String.valueOf(calendarDay.getDate().getDayOfMonth()));
                container.day = calendarDay;
                container.activity = CreateActivity.this;
                container.calendarView = select;
                if(calendarDay.getPosition() == DayPosition.MonthDate){
                    if(calendarDay.getDate() == selectedDay) {
                        container.t.setBackgroundColor(android.graphics.Color.GREEN);
                    }else{
                        container.t.setBackgroundColor(Color.WHITE);
                    }
                }else{
                    container.t.setBackgroundColor(Color.WHITE);
                }
            }
        });

        select.setup(startMonth,endMonth,firstDayOfWeek);
        select.scrollToMonth(currentMonth);

        List<DayOfWeek> daysOfWeek = daysOfWeek();

        select.setMonthHeaderBinder(new MonthHeaderFooterBinder<MonthViewContainer>() {
            @NonNull
            @Override
            public MonthViewContainer create(@NonNull View view) {
                return new MonthViewContainer(view);
            }

            @Override
            public void bind(@NonNull MonthViewContainer container, CalendarMonth calendarMonth) {
                if(container.titlesContainer.getTag() == null){
                    container.titlesContainer.setTag(calendarMonth.getYearMonth());
                    for(int index = 0; index < container.titlesContainer.getChildCount(); index++){
                        TextView textView = (TextView) container.titlesContainer.getChildAt(index);
                        DayOfWeek dayOfWeek = daysOfWeek.get(index);
                        String title = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault());
                        textView.setText(title);
                    }
                }
            }
        });

        select.setMonthScrollListener(new Function1<CalendarMonth, Unit>() {
            @Override
            public Unit invoke(CalendarMonth calendarMonth) {
                updateTitle();
                return Unit.INSTANCE;
            }
        });
    }

    //update calendar month and year tile when scroll to new month
    public void updateTitle(){
        currentMonth = Objects.requireNonNull(select.findFirstVisibleMonth()).getYearMonth();
        TextView yearTitle = findViewById(R.id.year);
        TextView monthTitle = findViewById(R.id.month);
        yearTitle.setText(String.valueOf(currentMonth.getYear()));
        monthTitle.setText(currentMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()));
    }

    //check the user input, if user input is all good
    //sent the request to the server
    //if success, close this activity return to main activity
    //else panic error
    //this triggered when user click post button
    public void post(View v) throws JSONException {
        String newTitle = title.getText().toString();
        if(newTitle.isEmpty()){
            Toast.makeText(getApplication(),"Title is required", Toast.LENGTH_SHORT).show();
            return;
        }
        String newDescription = description.getText().toString();
        if(newDescription.isEmpty()){
            Toast.makeText(getApplication(),"Description is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if(selectedDay == null){
            Toast.makeText(getApplication(),"Date is required", Toast.LENGTH_SHORT).show();
            return;
        }

        String date = selectedDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));

        JSONObject body = new JSONObject();
        body.put("title",newTitle);
        body.put("description",newDescription);
        body.put("date",date);
        body.put("createdBy",id);
        body.put("group",selectedGroup);

        OkHttpClient client = HttpClientSingleton.getInstance();

        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs",MODE_PRIVATE);
        String token = sharedPreferences.getString("token","error");
        if(token.equals("error")){
            throw new IllegalArgumentException("token not exists");
        }

        RequestBody requestBody = RequestBody.create(body.toString(), MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder().url(Variable.host + Variable.createEvent)
                .addHeader("Authorization", "Bearer " + token)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(CreateActivity.this, "Post failed!", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    runOnUiThread(() -> Toast.makeText(CreateActivity.this,"Post Success", Toast.LENGTH_LONG).show());
                    finish();
                }else{
                    runOnUiThread(() -> Toast.makeText(CreateActivity.this, "Post Failed " + response.message() + " code:" + response.code(),Toast.LENGTH_LONG).show());
                }
            }
        });
    }

    public void setDate(LocalDate day){
        this.selectedDay = day;
    }

    public LocalDate getSelectedDay() {
        return selectedDay;
    }
}
