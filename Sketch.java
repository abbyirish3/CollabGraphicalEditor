package editor;

import com.sun.source.tree.Tree;

import java.awt.*;
import java.util.Map;
import java.util.TreeMap;

/**
 * Represents the sketch of shapes
 * Manages mapping between unique shape ids and their respective shapes
 *
 * @author Abby Irish, CS10 Winter 2025, PS-6
 * @author Cara Lewis
 */

public class Sketch {
    static int total;
    private static int nextId = 0;
    private TreeMap<Integer, Shape> shapes;

    /**
     * constructor
     */
    public Sketch() {
        shapes = new TreeMap<>();
    }

    /**
     * Getter for a copy of the shapes map
     * @return a copy of map containing shape ids and info
     */
    public synchronized Map<Integer, Shape> getShapes() {
        return new TreeMap<>(shapes);
    }

    /**
     * returns the next available unique id for the next shape
     * used by server when assigning ids
     * @return the next unique shape id
     */
    public int getNextId() {
        return nextId++;
    }

    /**
     * adds a shape to the sketch
     * @param shape
     * @return
     */
    public synchronized int add(Shape shape) {
        int id = nextId++;
        shapes.put(id, shape);
        return id;
    }

    /**
     * removes a shape from the sketch
     * @param id of the shape to be removed
     */
    public synchronized void remove(int id) {
        if (id >= 0 && id < shapes.size()) {
            shapes.remove(id);
            System.out.println("shape (" + id + ") removed.");
        } else {
            System.out.println("Invalid ID: " + id);
        }
    }

    /**
     * updates the color of a shape
     * @param id the id of the shape to be recolored
     * @param newColor the color to change it to
     */
    public synchronized void recolor(int id, Color newColor) {
        Shape recoloredShape = shapes.get(id);
        recoloredShape.setColor(newColor);
    }

    /**
     * moves the shape in the sketch
     */
    public void move(int id, int dx, int dy) {
        Shape movedShape = shapes.get(id);
        movedShape.moveBy(dx, dy);
    }

    /**
     * Find the topmost shape that contains the given point
     * @param p the Point given
     * @return the id of the topmost shape containing the point, or -1 if not found
     */
    public synchronized int findTopmostShape(Point p) {
        for (int id : shapes.descendingKeySet()) {
            if (shapes.get(id).contains(p.x, p.y)) {
                return id;
            }
        }
        return -1;
    }

    public void draw(Graphics g) {
        for (Shape s : shapes.values()) {
            s.draw(g);
        }
    }

}