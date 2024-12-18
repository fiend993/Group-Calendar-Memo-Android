package com.coms5540.calendarmemo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.coms5540.calendarmemo.Utilities.HttpClientSingleton;
import com.coms5540.calendarmemo.Utilities.Variable;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
//This activity is for user register
public class RegisterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Button backButton = findViewById(R.id.registerGoBack);
        backButton.setOnClickListener(v -> finish());
    }

    //triggered when user click the register button
    //check the user input
    //sent request
    //if success, close this page return to login in page
    public void register(View v) throws JSONException {
        TextView userNameTextView = findViewById(R.id.registerUserName);
        TextView emailTextView = findViewById(R.id.registerEmail);
        TextView passwordTextView = findViewById(R.id.registerPassword);

        //check input
        if(TextUtils.isEmpty(userNameTextView.getText())){
            Toast.makeText(v.getContext(),"User name is required", Toast.LENGTH_SHORT).show();
            return;
        }
        String userName = userNameTextView.getText().toString().trim();
        if(TextUtils.isEmpty(emailTextView.getText())){
            Toast.makeText(v.getContext(),"Email is required", Toast.LENGTH_SHORT).show();
            return;
        }
        String email = emailTextView.getText().toString().trim();
        if(TextUtils.isEmpty(passwordTextView.getText())){
            Toast.makeText(v.getContext(),"Password is required", Toast.LENGTH_SHORT).show();
            return;
        }
        String password = passwordTextView.getText().toString().trim();

        JSONObject body = new JSONObject();
        body.put("username",userName);
        body.put("password",password);
        body.put("email",email);
        body.put("groups",email);

        OkHttpClient client = HttpClientSingleton.getInstance();
        RequestBody requestBody = RequestBody.create(body.toString(), MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder().url(Variable.host + Variable.register)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Registration failed!", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String responseBody = response.body().string();
                    try{
                        JSONObject jsonObject = new JSONObject(responseBody);
                        String message = jsonObject.getString("message");
                        runOnUiThread(() -> Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show());
                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "register response error", Toast.LENGTH_LONG).show());
                    }
                }else{
                    runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Registration Failed: " + response.message() + " code:" + response.code(), Toast.LENGTH_LONG).show());
                }
            }
        });

    }
}
