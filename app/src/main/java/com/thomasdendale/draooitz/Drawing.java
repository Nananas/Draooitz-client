package com.thomasdendale.draooitz;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Random;

/*
    This view handles all drawing functions.
    It also contains the WS event handler for drawing.
 */
public class Drawing extends View {
    // debug stuff
    private static String TAG = "trala";

    private Path path;
    private Paint paint;
    private Paint canvaspaint;
    private int paintColor = 0xFF997777;
    //private int backgroundColor = 0xFFFBFBFB;
    private Canvas drawCanvas;
    private Bitmap bitmap;
    private Gson gson;

    private ArrayList<Point> path_data;
    //private ArrayList<Path> path_to_draw;

    private DraooitzApplication app; // for easy access;

    public Drawing(Context context, AttributeSet attrs) {
        super(context, attrs);

        gson = new Gson();

        setupDrawing();

    }

    public void setApp(DraooitzApplication app) {
        this.app = app;

        connection_event_handler handler = new connection_event_handler();

        app.set_event_handler(handler);

    }

    private void setupDrawing() {

        Random r = new Random();
        path = new Path();
        paint = new Paint();
        paintColor = Color.rgb(r.nextInt(255), r.nextInt(255), r.nextInt(255));

        paint.setColor(paintColor);


        paint.setAntiAlias(true);
        paint.setStrokeWidth(20);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);

        canvaspaint = new Paint(Paint.DITHER_FLAG);

        path_data = new ArrayList<>();
        //path_to_draw = new ArrayList<>();
   }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(bitmap);

   }

    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap, 0, 0, canvaspaint);
        canvas.drawPath(path, paint);       // is current drawing line when touching
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float X = event.getX();
        float Y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(X, Y);
                path_data.clear();
                break;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(X, Y);
                path_data.add(new Point(X, Y));
                break;
            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(path, paint);
                path.reset();
                //Log.i(TAG, "onTouchEvent: PATH:"+path_data.get(0).toString());
                app.send_message(serialize(path_data));
                break;
            default:
                return false;
        }

        invalidate();   // makes onDraw be called in the future
        return true;
    }

    // converts a certain array of data points to the correct server message
    private String serialize(ArrayList<Point> path_data) {

        if (path_data.size() == 0) {
            return "DRAWPATH:none";
        }
        ArrayList<Integer> xx = new ArrayList<>();
        ArrayList<Integer> yy = new ArrayList<>();

        for (int i = 0; i < path_data.size(); i ++) {

            Point p = path_data.get(i);

            xx.add(p.getX());
            yy.add(p.getY());
        }

        Integer[] xintarray = xx.toArray(new Integer[xx.size()]);
        Integer[] yintarray = yy.toArray(new Integer[yy.size()]);

        JSONpath bag = new JSONpath();

        bag.setXx(xintarray);
        bag.setYy(yintarray);

        bag.setC(paintColor);

        String json = gson.toJson(bag);
        String msg = "DRAWPATH:"+json;
        Log.i(TAG, "serialize: JSON=" + msg);

        return msg;
    }

    public void clear_drawing() {
        drawCanvas.drawColor(Color.BLACK, PorterDuff.Mode.CLEAR);
        invalidate();

        app.send_message("CLEARDRAWING");
    }

    public int get_color() {
        return paintColor;
    }

    public int random_color() {
        Random r = new Random();
        paintColor = Color.rgb(r.nextInt(255), r.nextInt(255), r.nextInt(255));

        paint.setColor(paintColor);
        return paintColor;
    }

    public class connection_event_handler implements EventHandler {

        @Override
        public void onOpen() { }

        @Override
        public void onAlreadyOpen() { }

        @Override
        public void onTextMessage(String payload) {
            Log.i(TAG, "=>"+payload);

            /*
                Parse messages:
                    - CLEARDRAWING
                    - PUSH:{"msg":"update_room","content":{"people":?,"name":"???"}}
                    - PUSH:{"msg":"new_room","content":{"name":"???"}}
                    - DRAWPATH:{"xx":[?, ...],"yy":[?, ...],"c":?}
                    - Others: "ok"
             */

            if (payload.equals("CLEARDRAWING")) {
                clear_drawing();
            } else {
                int splitindex = payload.indexOf(":");

                if (splitindex > 0) {

                    String first = payload.substring(0, splitindex);

                    if (first.equals("DRAWPATH")) {

                        int oldColor = paint.getColor();
                        String second = payload.substring(splitindex+1);

                        Log.i(TAG, "SECOND="+second);

                        JSONpath pathmsg = gson.fromJson(second, JSONpath.class);

                        Integer[] xx = pathmsg.getXx();
                        Integer[] yy = pathmsg.getYy();
                        Path p = new Path();

                        paint.setColor(pathmsg.getC());
                        p.moveTo(xx[0], yy[0]);

                        for (int i = 1; i < pathmsg.getXx().length; i ++) {
                            p.lineTo(xx[i], yy[i]);
                        }

                        drawCanvas.drawPath(p, paint);

                        paint.setColor(oldColor);

                        invalidate();
                    }
                }

            }

        }

        @Override
        public void onClose(int code, String reason) { }
    }

}


class JSONpath {
    private Integer[] xx;
    private Integer[] yy;

    private int c;

    JSONpath(){}

    public Integer[] getXx() {
        return xx;
    }

    public void setXx(Integer[] xx) {
        this.xx = xx;
    }

    public Integer[] getYy() {
        return yy;
    }

    public void setYy(Integer[] yy) {
        this.yy = yy;
    }

    public int getC() {
        return c;
    }

    public void setC(int c) {
        this.c = c;
    }
}

class Point {
    private float X;
    private float Y;

    Point(float X, float Y) {
        this.X = X; this.Y = Y;
    }

    public int getX(){
        return Math.round(X);
    }

    public int getY(){
        return Math.round(Y);
    }
    @Override
    public String toString() {

        return "{\"x\":"+String.valueOf(getX())+", \"y\":"+String.valueOf(getY())+"}";
    }
}
