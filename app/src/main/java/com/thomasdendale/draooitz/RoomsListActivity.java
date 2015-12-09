package com.thomasdendale.draooitz;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.tavendo.autobahn.WebSocketConnection;

public class RoomsListActivity extends AppCompatActivity {

    private static String TAG = "trala";

    ProgressBar waiter;
    ListView room_list;
    FloatingActionButton new_room_fab;

    private RoomListAdapter adapter;
    public WebSocketConnection wsConnection;

    private ArrayList<Room> rooms = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms_list);

        waiter = (ProgressBar) findViewById(R.id.dataload_waiter);
        room_list = (ListView) findViewById(R.id.room_list);
        new_room_fab = (FloatingActionButton) findViewById(R.id.new_room_button);

        new_room_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goto_create_new_room();
            }
        });

        adapter = new RoomListAdapter(this, rooms);

        room_list.setAdapter(adapter);

        ((DraooitzApplication) getApplication()).send_message("LEAVEROOM");

        showWaiter();

        load_data();

        room_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Room item = (Room) parent.getItemAtPosition(position);

                goto_open_room(item);
            }
        });


    }

    private void goto_open_room(Room r) {
        Intent intent = new Intent(this, DrawingActivity.class);

       // Log.i(TAG, "goto_open_room: room = " + r.getName());

        ((DraooitzApplication) getApplication()).send_message("ENTERROOM:"+r.getName());

        startActivity(intent);

        // TODO: request bitmap data
    }

    private void goto_create_new_room() {
        Intent intent = new Intent(this, NewRoomActivity.class);

        startActivity(intent);
    }

    private void load_data() {
        wsConnection = ((DraooitzApplication) getApplication()).wsConnection;

        load_data_event_handler handler = new load_data_event_handler(this);

        ((DraooitzApplication) getApplication()).set_event_handler(handler);

        // if connection was lost at some point
        ((DraooitzApplication) getApplication()).connect();

    }

    private void showWaiter() {
        waiter.setVisibility(View.VISIBLE);
        Log.i(TAG, "showWaiter: visible");
    }
    private void hideWaiter() {
        waiter.setVisibility(View.GONE);
        Log.i(TAG, "hideWaiter: gone");
    }


    /*
            CONNECTION HANDLER
     */
    public class load_data_event_handler implements EventHandler{

        Context context;

        load_data_event_handler(Context context) {
            this.context = context;

            Log.i(TAG, "login_event_handler: wsconnection");
            if (wsConnection.isConnected())
                Log.i(TAG, "login_event_handler: connected");
            else
                Log.i(TAG, "login_event_handler: not connected");
        }

        private void send_message() {
            // TODO: this gives an error sometimes
            wsConnection = ((DraooitzApplication) getApplication()).wsConnection;
            wsConnection.sendTextMessage("GETROOMLIST:ALL");

            Log.i(TAG, "onOpen: SPECIAL");
        }

        @Override
        public void onOpen() { send_message(); }

        @Override
        public void onAlreadyOpen() { send_message(); }

        @Override
        public void onTextMessage(String payload) {

            Log.i(TAG, "ONTEXTMESSAGE, Rooomlist loader");

            Log.i(TAG, "Got response: " + payload);

            try {
                JSONArray js;

                js = new JSONArray(payload);

                int s = js.length();

                Log.i(TAG, "onTextMessage: length" + Integer.toString(s));

                ArrayList<Room> r = new ArrayList<>();

                for (int i = 0; i < s; i ++) {
                    JSONObject obj = js.optJSONObject(i);
                    if (obj != null) {
                        Log.i(TAG, "onTextMessage: " + obj.toString());

                        String name = obj.getString("name");

                        r.add(new Room(name));
                    }
                    else {
                        Log.i(TAG, "onTextMessage: NULL");
                    }
                }

                adapter.setFrom(r);

                hideWaiter();

            } catch (JSONException e) {

                Log.i(TAG, e.toString());

                try {
                    JSONObject js;
                    js = new JSONObject(payload);

                    String msg = js.getString("msg");
                    switch (msg) {
                        case "new_room":
                            String room_name = js.getString("name");

                            adapter.add(new Room(room_name));

                            break;
                        default:
                            break;
                    }

                } catch (JSONException ee) {
                    Log.i(TAG, ee.toString());
                }
            }

        }

        @Override
        public void onClose(int code, String reason) {
            Log.i(TAG, "Connection lost: " + reason);
            Toast toast = Toast.makeText(getApplicationContext(), "Connection lost ;_;", Toast.LENGTH_LONG);

            toast.show();

            hideWaiter();
        }
    }
}
