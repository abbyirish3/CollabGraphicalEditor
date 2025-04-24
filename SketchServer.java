package editor;

import java.awt.*;
import java.net.*;
import java.util.*;
import java.io.*;

/**
 * A server to handle sketches: getting requests from the clients,
 * updating the overall state, and passing them on to the clients
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012; revised Winter 2014 to separate SketchServerCommunicator
 * @author Tim Pierson Dartmouth CS 10, provided for Winter 2025
 */
public class SketchServer {
	private ServerSocket listen;						// for accepting connections
	private ArrayList<SketchServerCommunicator> comms;	// all the connections with clients
	private Sketch sketch;								// the state of the world
	
	public SketchServer(ServerSocket listen) {
		this.listen = listen;
		sketch = new Sketch();
		comms = new ArrayList<SketchServerCommunicator>();
	}

	public Sketch getSketch() {
		return sketch;
	}
	
	/**
	 * The usual loop of accepting connections and firing off new threads to handle them
	 */
	public void getConnections() throws IOException {
		System.out.println("server ready for connections");
		while (true) {
			SketchServerCommunicator comm = new SketchServerCommunicator(listen.accept(), this);
			comm.setDaemon(true);
			comm.start();
			addCommunicator(comm);
		}
	}

	/**
	 * Adds the communicator to the list of current communicators
	 */
	public synchronized void addCommunicator(SketchServerCommunicator comm) {
		comms.add(comm);
	}

	/**
	 * Removes the communicator from the list of current communicators
	 */
	public synchronized void removeCommunicator(SketchServerCommunicator comm) {
		comms.remove(comm);
	}

	/**
	 * Sends the message from the one communicator to all (including the originator)
	 */
	public synchronized void broadcast(String msg) {
		for (SketchServerCommunicator comm : comms) {
			comm.send(msg);
		}
	}

	public Shape parseShape(String[] tokens) {
		String type = tokens[1];

		if (type.equals("rectangle")) {
			int x1 = Integer.parseInt(tokens[2]);
			int y1 = Integer.parseInt(tokens[3]);
			int x2 = Integer.parseInt(tokens[4]);
			int y2 = Integer.parseInt(tokens[5]);
			Color color = new Color(Integer.parseInt(tokens[6]));
			Rectangle rect = new Rectangle(x1, y1, color);
			rect.setCorners(x1, y1, x2, y2);
			return rect;

		} else if (type.equals("segment")) {
			int x1 = Integer.parseInt(tokens[2]);
			int y1 = Integer.parseInt(tokens[3]);
			int x2 = Integer.parseInt(tokens[4]);
			int y2 = Integer.parseInt(tokens[5]);
			Color color = new Color(Integer.parseInt(tokens[6]));
			Segment segment = new Segment(x1, y1, x2, y2, color);
			return segment;

		} else if (type.equals("ellipse")) {
			int x1 = Integer.parseInt(tokens[2]);
			int y1 = Integer.parseInt(tokens[3]);
			int x2 = Integer.parseInt(tokens[4]);
			int y2 = Integer.parseInt(tokens[5]);
			Color color = new Color(Integer.parseInt(tokens[6]));
			Ellipse ellipse = new Ellipse(x1, y1, color);
			ellipse.setCorners(x1, y1, x2, y2);
			return ellipse;

		} else if (type.equals("polyline")) {
			int pointCount = Integer.parseInt(tokens[2]); // Get number of points
			int lastIndex = tokens.length - 1;
			Color color = new Color(Integer.parseInt(tokens[lastIndex]));

			Polyline poly = new Polyline(color);
			int xStart = 3;  // X-coordinates start here
			int yStart = 3 + pointCount;  // Y-coordinates start after all Xs

			for (int i = 0; i < pointCount; i++) {
				int x = Integer.parseInt(tokens[xStart + i]);
				int y = Integer.parseInt(tokens[yStart + i]);
				poly.addPoint(x, y);
			}

			System.out.println("Successfully parsed polyline: " + poly);
			return poly;
		}
		return null;
	}

	public static void main(String[] args) throws Exception {
		new SketchServer(new ServerSocket(4242)).getConnections();
	}
}