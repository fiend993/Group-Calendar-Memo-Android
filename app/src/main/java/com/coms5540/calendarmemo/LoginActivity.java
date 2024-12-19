package com.coms5540.calendarmemo;

import static com.coms5540.calendarmemo.Utilities.Variable.login;
import static com.coms5540.calendarmemo.Utilities.Variable.webSocket;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.coms5540.calendarmemo.Utilities.HttpClientSingleton;
import com.coms5540.calendarmemo.Utilities.Variable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.WebSocket;

//This activity is for user to login in or start
//register activity
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    //when user click the register button
    public void startRegister(View v){
        Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(intent);
    }

    //when user click login
    //it first check the user input
    //if not empty
    //sent request
    //if server response success
    //save the user profile
    public void saveInput(View v) throws JSONException {
        TextView emailTextView = findViewById(R.id.loginEmail);
        TextView passwordTextView = findViewById(R.id.loginPassword);
        String email = emailTextView.getText().toString();
        if(email.isEmpty()){
            Toast.makeText(v.getContext(),"Email is required", Toast.LENGTH_SHORT).show();
            return;
        }
        String password = passwordTextView.getText().toString();
        if(password.isEmpty()){
            Toast.makeText(v.getContext(),"Password is required", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject body = new JSONObject();
        body.put("password",password);
        body.put("email",email);

        OkHttpClient client = HttpClientSingleton.getInstance();

        RequestBody requestBody = RequestBody.create(body.toString(), MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder().url(Variable.host + login)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Login failed!", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String responseBody = response.body().string();
                    try{
                        JSONObject jsonObject = new JSONObject(responseBody);
                        //extract the important information save for later.
                        JSONArray groupsArray = jsonObject.getJSONArray("groups");
                        List<String> groupsList = new ArrayList<>();
                        for(int i =0; i < groupsArray.length(); i ++){
                            groupsList.add(groupsArray.getString(i));
                        }
                        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs",MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("userId",jsonObject.getString("_id"));
                        editor.putString("token", jsonObject.getString("token"));
                        editor.putStringSet("user_groups", new HashSet<>(groupsList));
                        editor.putBoolean("isLoggedIn",true);
                        editor.putString("name",jsonObject.getString("username"));
                        editor.apply();
                        runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show());
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }catch (JSONException | NullPointerException e){
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Login response error", Toast.LENGTH_LONG).show());
                    }
                }else{
                    runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Login failed!" + response.message() + " code: " +response.code() , Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}
