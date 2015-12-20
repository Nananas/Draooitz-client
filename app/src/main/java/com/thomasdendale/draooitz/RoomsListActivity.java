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

import com.google.gson.Gson;

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

    private Gson gson;

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

        gson = new Gson();

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

            int index = payload.indexOf(":");
            if (index > 0) {
                String first = payload.substring(0, index);
                String second= payload.substring(index+1);

                switch (first) {
                    case "LIST":
                        Log.i(TAG, "onTextMessage: second = "+second);
                        RoomListMsg msg = gson.fromJson(second, RoomListMsg.class);

                        ArrayList<Room> r = new ArrayList<>();

                        for (RoomMsg rm : msg.getRooms()) {
                            Room room = new Room(rm.getName());
                            room.setPlayers(rm.getPeople());

                            r.add(room);
                        }

                        adapter.setFrom(r);

                        hideWaiter();

                        break;

                    case "PUSH":
                        PushMsg m = gson.fromJson(second, PushMsg.class);
                        String type = m.getMsg();
                        switch (type) {
                            case "new_room":
                                //NewRoomMsgContent newRoomMsg = (NewRoomMsgContent) m.getContent();

                                NewRoomPushMsg nrmsg = gson.fromJson(second, NewRoomPushMsg.class);

                                Room room = new Room(nrmsg.getContent().getName());
                                adapter.add(room);
                                break;

                            case "update_room":
                                Log.i(TAG, "onTextMessage: content = " + m.getContent().toString());
                                UpdateRoomPushMsg urmsg = gson.fromJson(second, UpdateRoomPushMsg.class);

                                adapter.updateRoom(urmsg.getContent().getName(), urmsg.getContent().getPeople());

                                break;
                            default:
                                break;
                        }

                        break;

                    default:
                        break;
                }

            }

/*            try {
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
            }*/

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

class RoomListMsg {
    private RoomMsg[] rooms;

    public RoomMsg[] getRooms() {
        return rooms;
    }

    public void setMsgs(RoomMsg[] msgs) {
        this.rooms = msgs;
    }
}

class RoomMsg {
    private String name;
    private int people;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPeople() {
        return people;
    }

    public void setPeople(int people) {
        this.people = people;
    }
}

class PushMsg {
    private PushMsgContent content;
    private String msg;

    public PushMsgContent getContent() {
        return content;
    }

    public void setContent(PushMsgContent content) {
        this.content = content;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

class PushMsgContent {
}

class NewRoomPushMsg {
    private NewRoomPushMsgContent content;
    private String msg;

    public NewRoomPushMsgContent getContent() {
        return content;
    }

    public void setContent(NewRoomPushMsgContent content) {
        this.content = content;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

class NewRoomPushMsgContent {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

class UpdateRoomPushMsg {
    private UpdateRoomPushMsgContent content;
    private String msg;

    public UpdateRoomPushMsgContent getContent() {
        return content;
    }

    public void setContent(UpdateRoomPushMsgContent content) {
        this.content = content;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

class UpdateRoomPushMsgContent {
    private String name;
    private int people;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPeople() {
        return people;
    }

    public void setPeople(int people) {
        this.people = people;
    }
}