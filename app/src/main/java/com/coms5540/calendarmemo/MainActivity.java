package com.coms5540.calendarmemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.coms5540.calendarmemo.Fragment.CalendarFragment;
import com.coms5540.calendarmemo.Fragment.SelectDayFragment;
import com.coms5540.calendarmemo.Utilities.HttpClientSingleton;
import com.coms5540.calendarmemo.Utilities.SharedMessageViewModel;
import com.coms5540.calendarmemo.Utilities.SharedViewModel;
import com.coms5540.calendarmemo.Utilities.Variable;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.HashSet;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

//This activity is the main activity for user
//if user already login in, this will be the
//first page they see
//This page was combine two fragment
//CalendarFragment and SelectDayFragment
public class MainActivity extends AppCompatActivity {

    //a floating menu that give use three option
    //logout
    //create new memo
    //join group
    private LinearLayout floatingMenu;
    //an invisible overlay that will catch user
    //tap on the screen that allow user
    //close the floating menu by tap the
    //non menu space on the screen
    private View overlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn",false);

        //if user not logged in, then start the login activity
        if(!isLoggedIn) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        //load up the calendarFragment and selectDayFragment
        if(savedInstanceState == null){
            CalendarFragment calendarFragment = new CalendarFragment();
            SelectDayFragment selectDayFragment = new SelectDayFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction calendarFragmentTransaction = fragmentManager.beginTransaction();
            calendarFragmentTransaction.replace(R.id.calendarFragmentContainer,calendarFragment);
            calendarFragmentTransaction.commit();
            FragmentTransaction selectDayFragmentTransaction = fragmentManager.beginTransaction();
            selectDayFragmentTransaction.replace(R.id.selectDayFragmentContainer,selectDayFragment);
            selectDayFragmentTransaction.commit();
        }

        floatingMenu = findViewById(R.id.floatingMenu);
        overlay = findViewById(R.id.overlay);
    }

    //Show menu when user click the button at bottom right conner
    public void showMenu(View v){
        floatingMenu.setVisibility(View.VISIBLE);
        overlay.setVisibility(View.VISIBLE);
    }

    //hide menu if user click other part of the scrren
    public void hideMenu(View v){
        floatingMenu.setVisibility(View.GONE);
        overlay.setVisibility(View.GONE);
    }

    //when user click logout
    //remove the user profile
    //and close the app
    public void logout(View v){
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userId","");
        editor.putString("token", "");
        editor.putBoolean("isLoggedIn",false);
        editor.commit();
        finishAffinity();
        System.exit(0);
    }

    //trigger when user click the new memo button
    //start the CreateActivity
    public void createNewMemo(View v){
        Intent intent = new Intent(MainActivity.this,CreateActivity.class);
        startActivity(intent);
    }

    //trigger when user click the join group button
    //start the groupActivity
    public void joinGroup(View v){
        Intent intent = new Intent(MainActivity.this,GroupActivity.class);
        startActivity(intent);
    }
}
