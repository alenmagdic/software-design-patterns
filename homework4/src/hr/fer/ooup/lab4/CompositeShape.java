package hr.fer.ooup.lab4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.stream.Stream;

public class CompositeShape extends AbstractGraphicalObject {
	private List<GraphicalObject> children;

	public CompositeShape(List<GraphicalObject> children) {
		super(new Point[0]);
		this.children = new ArrayList<>(children);
	}

	@Override
	public void translate(Point delta) {
		for (GraphicalObject o : children) {
			o.translate(delta);
		}
		notifyListeners();
	}

	@Override
	public Rectangle getBoundingBox() {
		int x1 = getRectsStream().map(b -> b.getX()).min(Integer::compare).get();
		int y1 = getRectsStream().map(b -> b.getY()).min(Integer::compare).get();
		int x2 = getRectsStream().map(b -> b.getX() + b.getWidth()).max(Integer::compare).get();
		int y2 = getRectsStream().map(b -> b.getY() + b.getHeight()).max(Integer::compare).get();
		return new Rectangle(x1, y1, x2 - x1, y2 - y1);
	}

	private Stream<Rectangle> getRectsStream() {
		return children.stream().map(c -> c.getBoundingBox());
	}

	@Override
	public double selectionDistance(Point mousePoint) {
		return children.stream().map(c -> c.selectionDistance(mousePoint)).min(Double::compare).get();
	}

	@Override
	public void render(Renderer r) {
		for (GraphicalObject c : children) {
			c.render(r);
		}
	}

	@Override
	public String getShapeName() {
		return "Kompozit";
	}

	@Override
	public GraphicalObject duplicate() {
		List<GraphicalObject> childrenDupl = new ArrayList<>();
		for (GraphicalObject ch : children) {
			childrenDupl.add(ch.duplicate());
		}
		CompositeShape dupl = new CompositeShape(childrenDupl);
		dupl.setSelected(isSelected());
		return dupl;
	}

	public List<GraphicalObject> getChildren() {
		return Collections.unmodifiableList(children);
	}

	@Override
	public String getShapeID() {
		return "COMP";
	}

	@Override
	public void save(List<String> rows) {
		for (GraphicalObject go : children) {
			go.save(rows);
		}
		rows.add(String.format("@%s %d", getShapeID(), children.size()));
	}

	@Override
	public void load(Stack<GraphicalObject> stack, String data) {
		int num = Integer.parseInt(data.trim());
		List<GraphicalObject> chl = new ArrayList<>();
		for (int i = 0; i < num; i++) {
			chl.add(stack.pop());
		}
		CompositeShape cs = new CompositeShape(chl);
		stack.push(cs);
	}

}
