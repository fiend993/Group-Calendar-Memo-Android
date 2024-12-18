package com.coms5540.calendarmemo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

//This class use to allow use to join new group
public class GroupActivity extends AppCompatActivity {
    TextView group;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        Button back = findViewById(R.id.groupGoBack);
        back.setOnClickListener(v -> finish());

        group = findViewById(R.id.groupCode);
    }

    //check the user input
    //if input is all good, then sent the request
    //this triggered when user click the join button
    public void joinGroup(View v) throws JSONException {
        String groupCode = group.getText().toString();
        if(groupCode.isEmpty()){
            Toast.makeText(getApplication(),"Group is required", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs",MODE_PRIVATE);
        String token = sharedPreferences.getString("token","error");
        if(token.equals("error")){
            throw new IllegalArgumentException("token not exists");
        }
        JSONObject body = new JSONObject();
        body.put("group",groupCode);

        OkHttpClient client = HttpClientSingleton.getInstance();

        RequestBody requestBody = RequestBody.create(body.toString(), MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder().url(Variable.host + Variable.joinGroup)
                .addHeader("Authorization", "Bearer " + token)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(GroupActivity.this, "Join failed!", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    runOnUiThread(() -> Toast.makeText(GroupActivity.this, "Join success", Toast.LENGTH_SHORT).show());
                }else{
                    runOnUiThread(() -> Toast.makeText(GroupActivity.this, "Join failed!", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}
