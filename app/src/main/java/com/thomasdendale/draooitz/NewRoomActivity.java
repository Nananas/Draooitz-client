package com.thomasdendale.draooitz;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

public class NewRoomActivity extends AppCompatActivity {
    private static String TAG = "trala";

    FloatingActionButton create_new_room;
    EditText room_name_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_room);

        room_name_text = (EditText) findViewById(R.id.new_room_name_textfield);
        room_name_text.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        create_new_room = (FloatingActionButton) findViewById(R.id.new_room_create_button);
        create_new_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: START NEW ACTIVITY WITH NEW ROOM");

                open_room(room_name_text.getText().toString());
            }
        });
    }

    private void open_room(String room_name) {
        Intent intent = new Intent(this, DrawingActivity.class);
        intent.putExtra("roomname", room_name);

        ((DraooitzApplication) getApplication()).send_message("NEWROOM:"+room_name);

        // todo: check if "ok" received

        startActivity(intent);
    }
}
