package editor;

import java.awt.*;
import java.io.*;
import java.net.Socket;

/**
 * Handles communication between the server and one client, for SketchServer
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012; revised Winter 2014 to separate SketchServerCommunicator
 * @author Tim Pierson Dartmouth CS 10, provided for Winter 2025
 */
public class SketchServerCommunicator extends Thread {
	private Socket sock;                    // to talk with client
	private BufferedReader in;                // from client
	private PrintWriter out;                // to client
	private SketchServer server;// handling communication for

	public SketchServerCommunicator(Socket sock, SketchServer server) {
		this.sock = sock;
		this.server = server;
	}

	/**
	 * Sends a message to the client
	 *
	 * @param msg
	 */
	public void send(String msg) {
		out.println(msg);
	}

	/**
	 * Keeps listening for and handling (your code) messages from the client
	 */
	public void run() {
		try {
			System.out.println("someone connected");

			// Communication channel
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			out = new PrintWriter(sock.getOutputStream(), true);

			sendInitialSketch();

			// Keep getting and handling messages from the client

			String line;
			while ((line = in.readLine()) != null) {
				System.out.println("Received: " + line);
				handleMessage(line);
			}
		} catch (IOException e) {
			System.out.println("Client disconnected");
		} finally {
			server.removeCommunicator(this);

			try {
				out.close();
				in.close();
				sock.close();
			} catch (IOException e) {
				System.out.println("Error closing connection");
			}
		}
	}

	private void sendInitialSketch() {
		Sketch sketch = server.getSketch();
		synchronized (sketch) {
			for (Integer id : sketch.getShapes().keySet()) {
				Shape shape = sketch.getShapes().get(id);
				send("add " + id + " " + shape.toString());
			}
		}
	}


	private void handleMessage(String message) {
		String tokens[] = message.split(" ");
		String command = tokens[0];

		Sketch sketch = server.getSketch();
		synchronized (sketch) {
			try {
				switch (command) {
					case "add":
						Shape shape = server.parseShape(tokens);
						if (shape != null) {
							sketch.add(shape);
							server.broadcast(message);
						}
						break;

					case "remove":
						int deleteID = Integer.parseInt(tokens[1]);
						sketch.remove(deleteID);
						server.broadcast(message);
						break;

					case "move":
						int moveID = Integer.parseInt(tokens[1]);
						int dx = Integer.parseInt(tokens[2]);
						int dy = Integer.parseInt(tokens[3]);
						sketch.move(moveID, dx, dy);
						server.broadcast(message);
						break;

					case "recolor":
						int recolorID = Integer.parseInt(tokens[1]);
						Color color = new Color(Integer.parseInt(tokens[2]));
						sketch.recolor(recolorID, color);
						server.broadcast(message);
						break;

					default:
						System.out.println("Unknown message");
						break;
				}
			} catch (Exception e) {
				System.err.println("Error processing message: " + message);
				e.printStackTrace();
			}
		}
	}
}