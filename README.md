# Java socket chatroom
A desktop socket chat application **(Currently under construction)**

**[Server](/server)** and **[User](/user)** layer are separate applications. The Server layer only sets up the connection to the socket port and the Users layer connects to this created socket server.

## How to use

Change the **port** and **host** property to desired value in [server.properties](/server.properties) and [user.properties](/user.properties)

``` Java
port = 5000
host = localhost
```

1. Execute Server layer, either on local PC or on a server. Make sure the layer is active throughout the chatting process.
2. Start the User layer on one or more environments.
3. Start chatting in the User layer application!


## User Stories
##### General
- [x] Give user ability to choose username.
- [x] User can send messages.
- [x] User can see messages of others.
- [x] Show when messages was sent in format: _08:00PM_
- [x] When a new user joins a welcome message is shown to all users.
- [x] Different `rooms`.
    - [x] Let user pick room
- [ ] Ability to view and edit user profile.
    - [ ] Popover to preview profile
- [x] Server/client config file
- [x] User server.commands
    - [ ] commands from database or text file (based on config)
    - [ ] Get list of online users
- [ ] Language file(s)
- [ ] Create Room class so not all threads have to be loop for each message
 
##### Data storage
- [ ] All messages are saved in a `database`.
    - [ ] Previous **X** messages are visible when joining chat.
- [ ] Let user create account.
    
##### User experience
Executable file only work on console at the moment.
- [x] convert from swing to javafx
- [x] `User interface`
    - [x] Read messages.
    - [x] Send messages.
- [ ] Loading screen
- [ ] Users can send Media.
- [ ] Users can send links.
- [x] Adding styling to messagebox
- [ ] Add emoji's
- [ ] Custom top bar
- [ ] Room navigation
    
##### Roles
- Mods:
    - [ ] Mute a user for everyone.
    - [ ] Ban user account.
- Users/ Guests:
    - [ ] Create account
    - [ ] Mute others.
    - [ ] Start 1 on 1 chat.
