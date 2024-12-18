package com.coms5540.calendarmemo.Utilities;

import org.json.JSONArray;

//This interface for event list callback
public interface EventCallback {
    void onSuccess(JSONArray events);
}
