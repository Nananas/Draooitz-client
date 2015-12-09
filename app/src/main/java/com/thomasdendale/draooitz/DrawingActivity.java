package com.thomasdendale.draooitz;

import android.graphics.Point;
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




        //drawing.setOnTouchListener(this);

        //Display display = getWindowManager().getDefaultDisplay();
        //Point size = new Point();
        //display.getSize(size);
        //int width = size.x;
        //int height = size.y;

        //SurfaceView drawing_area = (SurfaceView) findViewById(R.id.drawing_area);
        //drawing_area.setOnTouchListener(this);

        //Log.i(TAG, "onCreate: " + Integer.toString(drawing_area.getWidth()));
        //drawing = new Drawing(drawing_area.getHolder(), width, height);

        //drawing = new Drawing(this);



        //setContentView(drawing);

        //drawing.setLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT);

    }

/*

    @Override
    public void onBackPressed() {
        Log.i(TAG, "onBackPressed: sening bitmap data");
        drawing.send_bitmap_data();
        super.onBackPressed();
    }*/

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
