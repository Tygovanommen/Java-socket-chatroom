<h3 align="center">
	Simple java socket chat room
</h3>

<p align="center">
A simple java socket chatting application chat room. Chat in different rooms with different people.
</p>

<p align="center">
	<a href="https://github.com/Tygovanommen/Java-socket-chatroom/tags"><img src="https://img.shields.io/badge/release-v0.1_alpha-blue" alt="Version badge"></a>
	<a href="https://github.com/Tygovanommen/Java-socket-chatroom/blob/master/LICENSE"><img src="https://img.shields.io/badge/license-MIT-green.svg" alt="License badge"></a>
</p>

<p align="center">
    <a href="#how-to-use">How to use</a>
    ·
    <a href="#features">Features</a>
    ·
    <a href="#commands">Commands</a>
    ·
    <a href="#user-stories">User Stories</a>
 </p>

<p align="center">
	<img src="https://github.com/Tygovanommen/Java-socket-chatroom/blob/master/demo.gif" width="550" alt="screenshot">
</p>

## How to use

Change the **port** and **host** property to desired value in [server.properties](/server.properties) and [user.properties](/user.properties)

``` 
port = 5000
host = localhost
```

Before you open the client executable, make sure the Server layer is up and running. This can be either on a local PC or on a server.

#### User data storage

User accounts/data is stored inside the [users.json](/users.json) file. This file can be manually adjusted or be automatically generated through the profile editing form. It contains a json object of 1 or more users.
```json
[
  {
    "color": "#669966ff",
    "name": "User1",
    "display_picture": "",
    "notifications": "Yes"
  }
]
``` 

## Features
- Multithreaded chatting (with rooms)
- [User commands](#commands)
- Create and update user accounts
- Manage server and user properties

Etc.

## Commands
| Command | Description |
| ----- | --- |
| /help or /info | Get a list of all commands |
| /users | Get a list of all online users |
| /room `room_name`  | Change the room you are in. Keep `room_name` empty to see what room you are currently in |


## User Stories
#### General
- [ ] Language file(s)
- [ ] Create Room class so not all threads have to be loop for each message
- [ ] User roles
 
#### User experience
- [ ] Users can send Media.
- [ ] Users can send Links.
- [ ] Custom top bar
- [ ] Room navigation
- [ ] Set and change display picture
- [ ] DM User by name
