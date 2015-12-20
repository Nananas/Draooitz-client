package com.thomasdendale.draooitz;

import android.graphics.Point;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

public class DrawingActivity extends AppCompatActivity implements View.OnTouchListener {

    private static String TAG="trala";


    Drawing drawing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);

        drawing = (Drawing) findViewById(R.id.drawing);
        drawing.setApp((DraooitzApplication) getApplication());


        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.open_menu_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawing.clear_drawing();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        //drawing.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //drawing.resume();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true; //drawing.onTouch(v, event);
    }

}
