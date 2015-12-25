package com.thomasdendale.draooitz;

import android.graphics.Point;
import android.graphics.PorterDuff;
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

        FloatingActionButton clearbutton = (FloatingActionButton) findViewById(R.id.clear_drawing_button);
        clearbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawing.clear_drawing();
            }
        });

        final FloatingActionButton randombutton = (FloatingActionButton) findViewById(R.id.random_color_button);
        randombutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = drawing.random_color();
                //randombutton.setBackgroundColor(color);
                randombutton.setColorFilter(color);


            }
        });

        randombutton.setColorFilter(drawing.get_color());
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
