This project implements a simple collaborative drawing application where multiple clients can simultaneously interact with and edit a shared sketch. It works similarly to Google Docs, but for graphics, where updates made by one user are instantly reflected for all connected users in real time.

System architecture: the editor follows a client-server model where the server maintains a global sketch state and handles all client communication, and the client handles user intersation and communications with the server via a background thread.

Editor.java – The main client-side class for the GUI-based drawing interface. Handles user mouse input and sends drawing actions to the server.

EditorCommunicator.java – Handles networking on the client side, sending and receiving messages to/from the sketch server.

SketchServer.java – The central server that maintains the global sketch and broadcasts updates to all clients.

SketchServerCommunicator.java – One instance per connected client; listens for messages from a client and relays commands to the server.

EchoServer.java – A simple test/debug server that echoes received messages, helpful for validating communication logic.

Shape.java – An interface for drawable shapes.
