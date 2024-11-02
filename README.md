# todo-list-on-local-server

This repo contains source Java files which can be executed on your
desired IDE (package data may need to be adjusted)

# Description

A local server that allows clients to communicate via terminal.
The server allows clients to store a list of tasks for a specific date.
This list can be freely accessed by all clients while the server is running.

# How To Use

1. Ensure all files are downloaded.
2. Run TODOListServer.java.
3. Run Client.java located in the "client" directory
4. Use the commands listed bellow to communicate with the server using the terminal from Client.java

You can run as many clients as you wish.

# Commands
The structure for the commands is in the form of "action;date;task"

+ add;date;task - adds a task to the given date
+ list;date - lists all tasks for the given date
+ STOP; - terminates client connection with the server