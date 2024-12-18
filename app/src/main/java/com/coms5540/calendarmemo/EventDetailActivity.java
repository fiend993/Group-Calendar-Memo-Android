package com.coms5540.calendarmemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.coms5540.calendarmemo.Utilities.Event;
import com.coms5540.calendarmemo.Utilities.HttpClientSingleton;
import com.coms5540.calendarmemo.Utilities.Variable;

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

//This event detail page display all the detail that hide
//in the list view
//it also provide option to delete or update the memo
//when user click on the edit option, it first will
//try to get a token for the selected memo to make
//sure it mutual exclusive
//if server approve the request, then start the UpdateActivity and close this activity
public class EventDetailActivity extends AppCompatActivity {
    private Event event;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventdetail);

        event = (Event) getIntent().getSerializableExtra("event");

        Button back = findViewById(R.id.eventDetailGoBack);
        back.setOnClickListener(v->finish());

        if(event != null){
            TextView title = findViewById(R.id.viewTitle);
            title.setText(event.getTitle());
            TextView des = findViewById(R.id.viewDescription);
            des.setText(event.getDescription());
            TextView group = findViewById(R.id.eventGroup);
            group.setText(event.getGroup());
            TextView create = findViewById(R.id.eventCreateBy);
            create.setText(event.getCreatedBy());
            TextView date = findViewById(R.id.eventDate);
            date.setText(event.getDate());
            TextView update = findViewById(R.id.eventUpdateAt);
            update.setText(event.getUpdateAt());
        }
    }

    //request token from server
    //if approve, start the update activity
    //this triggered when user click edit button
    public void edit(View v) throws JSONException {
        OkHttpClient client = HttpClientSingleton.getInstance();

        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs",MODE_PRIVATE);
        String token = sharedPreferences.getString("token","error");
        JSONObject body = new JSONObject();
        body.put("eventId",event.getId());
        if(token.equals("error")){
            throw new IllegalArgumentException("token not exists");
        }

        RequestBody requestBody = RequestBody.create(body.toString(), MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder().url(Variable.host + Variable.requireLock)
                .addHeader("Authorization", "Bearer " + token)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(EventDetailActivity.this, "Request failed!", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    runOnUiThread(() -> Toast.makeText(EventDetailActivity.this,"Request Success", Toast.LENGTH_LONG).show());
                    Intent intent = new Intent(EventDetailActivity.this,UpdateActivity.class);
                    intent.putExtra("event",event);
                    startActivity(intent);
                    finish();
                }else{
                    runOnUiThread(() -> Toast.makeText(EventDetailActivity.this, "Request Failed " + response.message() + " code:" + response.code(),Toast.LENGTH_LONG).show());
                }
            }
        });
    }

}
