package com.coms5540.calendarmemo.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coms5540.calendarmemo.Adapter.EventAdapter;
import com.coms5540.calendarmemo.EventDetailActivity;
import com.coms5540.calendarmemo.R;
import com.coms5540.calendarmemo.Utilities.Event;
import com.coms5540.calendarmemo.Utilities.SharedViewModel;

import java.util.ArrayList;
import java.util.List;

//This selectDayFragment attached on MainActivity that use to take the input from
//CalendarFragment, an EventList from the day the user selected
//and display on the screen
public class SelectDayFragment extends Fragment {
    //RecyclerView use to draw the list
    private RecyclerView recyclerView;
    private EventAdapter adapter;
    SharedViewModel sharedViewModel;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        return inflater.inflate(R.layout.fragment_selectday, container, false);
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.recyclerView = view.findViewById(R.id.recyclerView);

        List<Event> eventList = new ArrayList<>();

        //if an item is click by the user
        //then open the detail page of that item
        adapter = new EventAdapter(eventList, event -> {
            Intent intent = new Intent(getActivity(), EventDetailActivity.class);
            intent.putExtra("event",event);
            startActivity(intent);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter);
        // Observe the LiveData
        //if new data come in, set the new list of RecycleView
        sharedViewModel.getEvents().observe(getViewLifecycleOwner(), List -> {
            if (List != null) {
                adapter.setEventList(List);
            }
        });
    }
}
