package com.thomasdendale.draooitz;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
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


public class Drawing extends View {
    private static String TAG = "trala";

    private Path path;
    private Paint paint;
    private Paint canvaspaint;
    private int paintColor = 0xFF997777;
    private Canvas canvas;
    private Bitmap bitmap;
    private Gson gson;

    private ArrayList<Point> path_data;
    private ArrayList<Path> path_to_draw;

    private DraooitzApplication app;

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

        path = new Path();
        paint = new Paint();
        paint.setColor(paintColor);

        paint.setAntiAlias(true);
        paint.setStrokeWidth(20);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);

        canvaspaint = new Paint(Paint.DITHER_FLAG);

        path_data = new ArrayList<>();
        path_to_draw = new ArrayList<>();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        // TODO: copy content from old bitmap to new one
        canvas = new Canvas(bitmap);

    }

    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap, 0, 0, canvaspaint);
        canvas.drawPath(path, paint);       // is current drawing line


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
                canvas.drawPath(path, paint);
                //Log.i(TAG, "onTouchEvent: PATH:"+path_data.get(0).toString());
                app.send_message(serialize(path_data));
                break;
            default:
                return false;
        }

        invalidate();   // makes onDraw be called in the future
        return true;
    }

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

        String json = gson.toJson(bag);

        String msg = "DRAWPATH:"+json;
        Log.i(TAG, "serialize: JSON="+msg);

/*        for (int i = 0; i < xintarray.length-1; i++) {
            msg += String.valueOf(xintarray[i]) + ",";
        }

        msg += String.valueOf(xintarray[xintarray.length-1]);

        msg += "], \"pathy\":[";
        for (int i = 0; i < xintarray.length; i++) {
            msg += String.valueOf(xintarray[i]) + ",";
        }

        msg += String.valueOf(yintarray[yintarray.length-1]);
        msg += "]}";*/

        return msg;
    }

   /* public void send_bitmap_data() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bytes = stream.toByteArray();

        Log.i(TAG, "serialize: BYTES");

        app.send_bytes(bytes);
    }*/

    public class connection_event_handler implements EventHandler {

        @Override
        public void onOpen() { }

        @Override
        public void onAlreadyOpen() { }

        @Override
        public void onTextMessage(String payload) {
            Log.i(TAG, "=>"+payload);

            // first part:
            //sString[] split = payload.split(":");
            int splitindex = payload.indexOf(":");

            if (splitindex > 0) {

                String first = payload.substring(0, splitindex);

                if (first.equals("DRAWPATH")) {
                    String second = payload.substring(splitindex+1);

                    Log.i(TAG, "SECOND="+second);

                    JSONpath pathmsg = gson.fromJson(second, JSONpath.class);

                    Integer[] xx = pathmsg.getXx();
                    Integer[] yy = pathmsg.getYy();
                    Path p = new Path();
                    p.moveTo(xx[0], yy[0]);

                    for (int i = 1; i < pathmsg.getXx().length; i ++) {
                        p.lineTo(xx[i], yy[i]);
                    }

                    canvas.drawPath(p, paint);

                    invalidate();
                }
            }



            //JSONObject js;

            //try {
                //js = new JSONObject(payload);

                //String msg = js.getString("msg");


                //if (msg.equals("path")) {

                //    JSONpathmsg pathmsg = gson.fromJson(payload, JSONpathmsg.class);
                //    Log.i(TAG, "path length = " + String.valueOf(pathmsg.getPath().getXx().length));
/*                    Log.i(TAG, "got path");
                    JSONObject data = new JSONObject(js.getString("path"));
                    Log.i(TAG, "data = "+data.toString(1));
                    JSONArray test = data.getJSONArray("pathx");
                    Log.i(TAG, "test = " + test.toString());
                    JSONArray xarray = new JSONArray(data.getJSONArray("pathx"));
                    JSONArray yarray = new JSONArray(data.getJSONArray("pathy"));


                    Log.i(TAG, "array length: " + String.valueOf(xarray));
                    for (int i = 0; i < xarray.length(); i ++) {
                        Log.i(TAG, "pathx data: " + String.valueOf(xarray.getInt(i)));
                    }*/

                //} else {
                //    Log.i(TAG, "msg = "+msg);
                //}

            //} catch (JSONException e) {
            //    e.printStackTrace();
            //    Log.i(TAG, "onTextMessage: "+e.toString());
            //}
        }

        @Override
        public void onClose(int code, String reason) { }
    }

    class JSONpath {
        private Integer[] xx;
        private Integer[] yy;
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
        // TODO: convert float to ints
        return "{\"x\":"+String.valueOf(X)+", \"y\":"+String.valueOf(Y)+"}";
    }
}
