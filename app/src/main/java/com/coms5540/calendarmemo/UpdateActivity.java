package com.coms5540.calendarmemo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.coms5540.calendarmemo.Utilities.Event;
import com.coms5540.calendarmemo.Utilities.HttpClientSingleton;
import com.coms5540.calendarmemo.Utilities.SharedMessageViewModel;
import com.coms5540.calendarmemo.Utilities.Variable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

//This activity allow user to update or delete a single
//memo, to do this, the user will be assign a token that
//expire in 5 min, once token is expire
//the change are not allow to commit
public class UpdateActivity extends AppCompatActivity {
    private Event event;
    private String token;

    private SharedMessageViewModel sm;
    TextView editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        event = (Event) getIntent().getSerializableExtra("event");

        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs",MODE_PRIVATE);
        token = sharedPreferences.getString("token","error");
        if(token.equals("error")){
            throw new IllegalArgumentException("token not exists");
        }

        Button back = findViewById(R.id.updateGoBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    releaseLock();
                } catch (JSONException e) {
                   e.printStackTrace();
                }
                finally {
                    finish();
                }
            }
        });
        editor = findViewById(R.id.updateDesciption);
        editor.setText(event.getDescription());

        sm = new ViewModelProvider(this).get(SharedMessageViewModel.class);
    }

    //This trigger when user click the delete button
    public void delete(View v){
        OkHttpClient client = HttpClientSingleton.getInstance();
        Request request = new Request.Builder().url(Variable.host + Variable.delete + event.getId())
                .addHeader("Authorization", "Bearer " + token)
                .delete()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    runOnUiThread(() -> Toast.makeText(UpdateActivity.this, "delete success " + response.message() + " code:" + response.code(),Toast.LENGTH_LONG).show());
                    sm.setMessage("refresh");
                    finish();
                }else{
                    runOnUiThread(() -> Toast.makeText(UpdateActivity.this, "delete Failed " + response.message() + " code:" + response.code(),Toast.LENGTH_LONG).show());
                }
            }
        });
    }

    //This trigger when user click the post button
    public void post(View v) throws JSONException {
        JSONObject body = new JSONObject();
        body.put("title",event.getTitle());
        String newDescription = editor.getText().toString();
        if(newDescription.isEmpty()){
            Toast.makeText(getApplication(),"Description can not be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        body.put("description", newDescription);

        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        String date = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));

        body.put("date",date);
        body.put("createdBy",event.getCreatedBy());
        body.put("group",event.getGroup());

        OkHttpClient client = HttpClientSingleton.getInstance();
        RequestBody requestBody = RequestBody.create(body.toString(), MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder().url(Variable.host + Variable.createEvent + "/" + event.getId())
                .addHeader("Authorization", "Bearer " + token)
                .put(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    runOnUiThread(() -> Toast.makeText(UpdateActivity.this, "update success " + response.message() + " code:" + response.code(),Toast.LENGTH_LONG).show());
                    sm.setMessage("refresh");
                    finish();
                }else{
                    runOnUiThread(() -> Toast.makeText(UpdateActivity.this, "update Failed " + response.message() + " code:" + response.code(),Toast.LENGTH_LONG).show());
                }
            }
        });
    }

    //when user finished update, or just leave this page, release the token earlier
    public void releaseLock() throws JSONException {
        JSONObject body = new JSONObject();
        body.put("eventId",event.getId());

        OkHttpClient client = HttpClientSingleton.getInstance();
        RequestBody requestBody = RequestBody.create(body.toString(), MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder().url(Variable.host + Variable.releaseLock)
                .addHeader("Authorization", "Bearer " + token)
                .post(requestBody)
                .build();

        //We don't care the response of release request.
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }
        });
    }
}
