# Java socket chat room

[![GitHub Release](https://img.shields.io/badge/release-v0.1_alpha-blue)](https://github.com/Tygovanommen/Java-socket-chatroom/tags)
[![MIT License](https://img.shields.io/badge/license-MIT-green.svg)](/LICENSE)

A simple java socket chatting application. Chat in different rooms with different people.

## How to use

Change the **port** and **host** property to desired value in [server.properties](/server.properties) and [user.properties](/user.properties)

``` 
port = 5000
host = localhost
```

Before you open the client executable, make sure the Server layer is up and running. This can be either on a lcal PC or on a server.

## User Stories
##### General
- [ ] Ability to view and edit user profile.
    - [ ] Popover to preview profile
- [x] User server commands
    - [ ] commands from database or text file (based on config)
    - [ ] Get list of online users
- [ ] Language file(s)
- [ ] Create Room class so not all threads have to be loop for each message
- [ ] User roles
 
##### Data storage
- [ ] Messages are saved in `database`.
- [ ] Let user create account.
    
##### User experience
- [ ] Loading screen
- [ ] Users can send Media and links.
- [ ] Add emoji's
- [ ] Custom top bar
- [ ] Room navigation
   