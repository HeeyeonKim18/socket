#!/bin/sh

# Compile the Java files
javac Main.java Player.java

# Start the server and client in separate processes
java Main &
java Main

