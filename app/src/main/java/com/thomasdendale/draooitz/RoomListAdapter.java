package com.thomasdendale.draooitz;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by thomas on 6/12/15.
 */
public class RoomListAdapter extends ArrayAdapter {

    static final String TAG = "trala";


    private ArrayList<Room> rooms = new ArrayList<>();

    public RoomListAdapter(Context context, ArrayList<Room> rooms) {
        super(context, android.R.layout.simple_list_item_1, rooms);


        this.rooms = rooms;
    }

    public void setFrom(ArrayList<Room> list) {
        this.clear();
        this.addAll(list);
    }
}
