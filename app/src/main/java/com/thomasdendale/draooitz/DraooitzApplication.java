package com.thomasdendale.draooitz;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;
import de.tavendo.autobahn.WebSocketOptions;

/*
    @author Thomas Dendale
 */
public class DraooitzApplication extends Application {
    // debug stuff
    private static String TAG = "trala";

    // release URI
    private static String wsuri = "ws://thomasdendale.com:8080";

    // debug URI
    // private static String wsuri = "ws://192.168.1.1:8080";

    public WebSocketConnection wsConnection;

    // default event handler. Should be overwritten
    public EventHandler event_handler = new EventHandler() {
        @Override
        public void onOpen() {
            Log.i(TAG, "onOpen: default");
        }

        @Override
        public void onAlreadyOpen() {
            Log.i(TAG, "onAlreadyOpen: default");
        }

        @Override
        public void onTextMessage(String payload) {
            Log.i(TAG, "onTextMessage: default");
        }

        @Override
        public void onClose(int code, String reason) {
            Log.i(TAG, "onClose: default: " + reason);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        connect();
    }

    // open the Websocket connection
    public void connect() {

        if (wsConnection != null && wsConnection.isConnected())
            return;

        wsConnection = new WebSocketConnection();

        WebSocketOptions wsOptions = new WebSocketOptions();
        wsOptions.setSocketConnectTimeout(3000);
        wsOptions.setSocketReceiveTimeout(3000);

        Log.i(TAG, "Connecting to ..." + wsuri);

        final DraooitzApplication app = this;

        try {
            // the websocket handler uses callbacks to allow activities to change the behaviour
            wsConnection.connect(wsuri, new WebSocketHandler() {
                @Override
                public void onOpen() {
                    event_handler.onOpen();
                }

                @Override
                public void onTextMessage(String payload) {
                    event_handler.onTextMessage(payload);
                }

                @Override
                public void onClose(int code, String reason) {
                    // Default behaviour when disconnecting: go back to the login activity
                    Intent intent = new Intent(app, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                    event_handler.onClose(code, reason);
                }
            }, wsOptions);

        } catch (WebSocketException e) {
            Log.i(TAG, e.toString());
            // note: better error support?
        }

    }

    // Public method to change callback for websocket behaviour
    public void set_event_handler(EventHandler handler) {
        event_handler = handler;

        Log.i(TAG, "set_event_handler");
        
        if (wsConnection != null && wsConnection.isConnected()) {
            event_handler.onAlreadyOpen();
        }

    }

    // helper function to easily send messages to the server
    public void send_message(String s) {
        if (check_connection()) {
            Log.i(TAG, "sending message: " + s);
            wsConnection.sendTextMessage(s);
        }
    }

    // helper function to easily send messages to the server
    public void send_bytes(byte[] bytes) {
        if (check_connection()) {
            wsConnection.sendBinaryMessage(bytes);
        }
    }

    // helper function
    public boolean check_connection() {
        if (wsConnection != null && wsConnection.isConnected()) {
            return true;
        }

        return false;
    }

    // note: not used...?
    public void reconnect() {
        if (check_connection()) {
            wsConnection.disconnect();
        }

        connect();
    }
}


/*
    Interface for the Websocket event handler. 
    This interface is implemented to customise behabiour for WS events
 */
interface EventHandler {
    void onOpen();
    void onAlreadyOpen();
    void onTextMessage(String payload);
    void onClose(int code, String reason);
}