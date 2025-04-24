package editor;

import java.awt.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;

/**
 * Handles communication to/from the server for the editor
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012
 * @author Chris Bailey-Kellogg; overall structure substantially revised Winter 2014
 * @author Travis Peters, Dartmouth CS 10, Winter 2015; remove EditorCommunicatorStandalone (use echo server for testing)
 * @author Tim Pierson Dartmouth CS 10, provided for Winter 2025
 */
public class EditorCommunicator extends Thread {
	private PrintWriter out;		// to server
	private BufferedReader in;		// from server
	protected Editor editor;		// handling communication for

	/**
	 * Establishes connection and in/out pair
	 */
	public EditorCommunicator(String serverIP, Editor editor) {
		this.editor = editor;
		System.out.println("connecting to " + serverIP + "...");
		try {
			//Socket sock = new Socket(serverIP, 4242);
			Socket sock = new Socket();
			sock.connect(new InetSocketAddress(serverIP, 4242), 2000);
			out = new PrintWriter(sock.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			System.out.println("...connected");
		}
		catch (IOException e) {
			System.err.println("couldn't connect");
			System.exit(-1);
		}
	}

	/**
	 * Sends message to the server
	 */
	public void send(String msg) {
		if (out != null) {
			System.out.println("sending: " + msg);
			out.println(msg);
		} else {
			System.out.println("error: not connected to server");
			;
		}
	}

	/**
	 * Keeps listening for and handling (your code) messages from the server
	 */
	public void run() {
		try {
			// Handle messages
			String msg;
			while ((msg = in.readLine()) != null) {
				System.out.println("received from server: " + msg);
				handleMessage(msg);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (in != null) in.close();
				if (out != null) out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("server hung up");
		}
	}

	// Send editor requests to the server

	private void handleMessage(String message) {
		String tokens[] = message.split(" ");
		String command = tokens[0];
		System.out.println("Parsed command: " + command);

		Sketch sketch = editor.getSketch();
		synchronized (sketch) {
			try {
				switch (command) {
					case "add":
						Shape shape = parseShape(tokens);
						if (shape != null) {
							sketch.add(shape);
							editor.repaint();
						}
						break;

					case "remove":
						int deleteID = Integer.parseInt(tokens[1]);
						sketch.remove(deleteID);
						editor.repaint();
						break;

					case "move":
						int moveID = Integer.parseInt(tokens[1]);
						int dx = Integer.parseInt(tokens[2]);
						int dy = Integer.parseInt(tokens[3]);
						sketch.move(moveID, dx, dy);
						editor.repaint();
						break;

					case "recolor":
						int recolorID = Integer.parseInt(tokens[1]);
						Color color = new Color(Integer.parseInt(tokens[2]));
						sketch.recolor(recolorID, color);
						editor.repaint();
						break;

					default:
						System.out.println("Unknown message");
						break;
				}
			} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
				System.err.println("Malformed message: "+message);
			}
		}
	}

	private Shape parseShape(String[] tokens) {
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
			int y1 = Integer.parseInt(tokens [3]);
			int x2 = Integer.parseInt(tokens[4]);
			int y2 = Integer.parseInt(tokens [5]);
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
}