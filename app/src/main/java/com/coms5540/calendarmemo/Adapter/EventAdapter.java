package com.coms5540.calendarmemo.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coms5540.calendarmemo.R;
import com.coms5540.calendarmemo.Utilities.Event;

import java.util.List;

//This adapter for SelectDayFragment
//use to allow the recycleView to generate a list of the event
//show on the screen
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    //An eventList for a day
    private List<Event> eventList;
    private OnEventClickListener listener;

    public interface OnEventClickListener {
        void onEventClick(Event event);
    }

    public EventAdapter(List<Event> eventList, OnEventClickListener listener){
        this.eventList = eventList;
        this.listener = listener;
    }
    @NonNull
    @Override
    public EventAdapter.EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_layout,parent,false);
        return new EventViewHolder(view);
    }

    //Set up a single element in the list
    @Override
    public void onBindViewHolder(@NonNull EventAdapter.EventViewHolder holder, int position) {
        Event event = eventList.get(position);

        holder.titleTextView.setText(event.getTitle());
        holder.descriptionView.setText(event.getDescription());
        holder.dateView.setText(event.getDate());
        holder.createdBy.setText(event.getCreatedBy());
        holder.group.setText(event.getGroup());
        holder.itemView.setOnClickListener(v -> listener.onEventClick(event));
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    //to swap the old list with new list, and redraw the recycler view
    public void setEventList(List<Event> newEventList) {
        this.eventList = newEventList; // Replace the old list with the new list
        notifyDataSetChanged(); // Notify the adapter that the dataset has changed
    }


    //This view Holder define the single element in the RecyclerView
    public static class EventViewHolder extends RecyclerView.ViewHolder{
        TextView titleTextView;
        TextView descriptionView;
        TextView dateView;
        TextView createdBy;
        TextView group;
        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.eventTitle);
            descriptionView = itemView.findViewById(R.id.eventDescription);
            dateView = itemView.findViewById(R.id.date);
            createdBy = itemView.findViewById(R.id.createdBy);
            group = itemView.findViewById(R.id.group);
        }
    }
}
