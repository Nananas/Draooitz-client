# Draooitz android app

This repository contains the code for the android based drawing app 'Draooitz'. This application is the client side, the server can be found [here](https://github.com/Nananas/Draooitz-server).

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
    - [ ] Display the room name 
    - [ ] Allow the player to change the color of this pencil
    - [ ] Display the player list 
    - [ ] Make a player master of a room
    - [ ] Allow the room master to kick players
