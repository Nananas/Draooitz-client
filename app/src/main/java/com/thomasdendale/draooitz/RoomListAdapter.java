package com.thomasdendale.draooitz;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by thomas on 6/12/15.
 */
public class RoomListAdapter extends ArrayAdapter {

    static final String TAG = "trala";


    private ArrayList<Room> rooms = new ArrayList<>();

    private final Context context;
    //private final ArrayList<Room> items;

    public RoomListAdapter(Context context, ArrayList<Room> rooms) {
        super(context, R.layout.room_list_item, rooms);

        this.context = context;
        this.rooms = rooms;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.room_list_item, parent, false);

        // 3. Get the two text view from the rowView
        TextView roomname_view = (TextView) rowView.findViewById(R.id.room_name);
        TextView playernumber_view = (TextView) rowView.findViewById(R.id.room_playernumber);

        // 4. Set the text for textView
        roomname_view.setText(rooms.get(position).getName());
        playernumber_view.setText(String.valueOf(rooms.get(position).getPlayers()));

        return rowView;
    }

    public void setFrom(ArrayList<Room> list) {
        this.clear();
        this.addAll(list);
    }

    public void updateRoom(String roomname, int people) {
        for (int i = 0; i < rooms.size(); i++) {
            Room r = rooms.get(i);
            if (r.getName().equals(roomname)) {
                r.setPlayers(people);
            }
        }

        this.notifyDataSetChanged();

    }


}
