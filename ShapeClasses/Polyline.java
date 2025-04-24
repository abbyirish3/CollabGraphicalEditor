package editor;
import editor.Shape;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A multi-segment Shape, with straight lines connecting "joint" points -- (x1,y1) to (x2,y2) to (x3,y3) ...
 *
 * @author Cara Lewis Dartmouth CS 10, Winter 2025
 * @author Abby Irish
 */

public class Polyline implements Shape {
	private Color color;
	private List<Integer> xPoints;
	private List<Integer> yPoints;
	private Polygon polygon;

	public Polyline(Color color) {
		this.xPoints = new ArrayList<>();  // Initial capacity (can grow)
		this.yPoints = new ArrayList<>();
		this.color = color;
		this.polygon = new Polygon();
	}

	public void addPoint(int x, int y) {
		xPoints.add(x);
		yPoints.add(y);
		polygon.addPoint(x,y);
	}

	public void moveBy(int dx, int dy) {
		for(int i = 0; i<xPoints.size(); i++){
			xPoints.set(i, xPoints.get(i) +dx);
			yPoints.set(i, yPoints.get(i) + dy);
		}
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public boolean contains(int x, int y) {
			Polygon poly = new Polygon(
					xPoints.stream().mapToInt(i -> i).toArray(),
					yPoints.stream().mapToInt(i -> i).toArray(),
					xPoints.size()
			);
			return poly.contains(x, y);
		}


	public void draw(Graphics g) {
		g.setColor(color);
		if (xPoints.size() > 1) {
			int[] xArray = xPoints.stream().mapToInt(i->i).toArray();
			int[] yArray = yPoints.stream().mapToInt(i->i).toArray();
			g.drawPolyline(xArray, yArray, xPoints.size());
		}
	}

	public String toString() {
		return  "polyline " + xPoints.size() + " " +
				xPoints.toString().replaceAll("[\\[\\],]", "") + " " +
				yPoints.toString().replaceAll("[\\[\\],]", "") + " " +
				color.getRGB();
	}
}