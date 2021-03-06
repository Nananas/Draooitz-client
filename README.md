# Draooitz android app

This repository contains the code for the android based drawing app 'Draooitz'. This application is the client side, the server can be found [here](https://github.com/Nananas/Draooitz-server).


## How to run
Import the project in Android Studio, build and run. The device should have a working internet connection.

### How to play
Start the game [server](https://github.com/Nananas/Draooitz-server). Make sure the URI in `main/java/com/thomasdendale/draooitz/DraooitzApplication.java` corresponds to the server address. Default URI points to the server hosted by myself. This server might be down when not needed.

Log in using any username and password. If the user does not exist, it will be created. Create a room or join an existing one and start drawing!

## Application flow
### Application class
The application class was extended to contain the Autobahn Websocket connection, as to make it accessible to every activity. 
An `event_handler` interface is used to allow activities to access (by implementing) the callback events.

The main activity is the `LoginActivity`.

### LoginActivity
This activity handles the login attempts. When the Websocket disconnects, this activity will get focus.

When a successful login is achieved, the RoomListActivity is started.

### RoomListActivity
This activity displays the current active rooms and player number, received from the server.

By clicking on any room in the list, the DrawingActivity is started. 
By clicking on the floating action button, the NewRoomActivity is started, to allow the player to create a new room.

### NewRoomActivity
This activity will ask for a name for the new room, after which it will start the DrawingActivity.

### DrawingActivity
This is the playfield of the player, the canvas of the painter. In this activity, the player can (together with others) draw on a "shared" canvas.

## TODO

- Drawing Activity
    - [x] Display the room name 
    - [ ] Allow the player to change the color of his pencil


