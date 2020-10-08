package hr.fer.ooup.lab4;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class LineSegment extends AbstractGraphicalObject {

	public LineSegment() {
		super(new Point[] { new Point(0, 0), new Point(10, 0) });

	}

	public LineSegment(Point start, Point end) {
		super(new Point[] { start, end });
	}

	@Override
	public Rectangle getBoundingBox() {
		int x1 = Math.min(getHotPoint(0).getX(), getHotPoint(1).getX());
		int y1 = Math.min(getHotPoint(0).getY(), getHotPoint(1).getY());
		int x2 = Math.max(getHotPoint(0).getX(), getHotPoint(1).getX());
		int y2 = Math.max(getHotPoint(0).getY(), getHotPoint(1).getY());
		return new Rectangle(x1, y1, x2 - x1, y2 - y1);
	}

	@Override
	public double selectionDistance(Point mousePoint) {
		return GeometryUtil.distanceFromLineSegment(getHotPoint(0), getHotPoint(1), mousePoint);
	}

	@Override
	public String getShapeName() {
		return "Linija";
	}

	@Override
	public GraphicalObject duplicate() {
		LineSegment dupl = new LineSegment(getHotPoint(0), getHotPoint(1));
		dupl.setSelected(isSelected());
		dupl.setHotPointSelected(0, isHotPointSelected(0));
		dupl.setHotPointSelected(1, isHotPointSelected(1));
		return dupl;
	}

	@Override
	public void render(Renderer r) {
		r.drawLine(getHotPoint(0), getHotPoint(1));
	}

	@Override
	public String getShapeID() {
		return "LINE";
	}

	@Override
	public void save(List<String> rows) {
		rows.add(String.format("@%s %d %d %d %d", getShapeID(), getHotPoint(0).getX(), getHotPoint(0).getY(),
				getHotPoint(1).getX(), getHotPoint(1).getY()));
	}

	@Override
	public void load(Stack<GraphicalObject> stack, String data) {
		List<Integer> params = Arrays.asList(data.split(" ")).stream().map(num -> Integer.parseInt(num))
				.collect(Collectors.toList());
		LineSegment ln = new LineSegment(new Point(params.get(0), params.get(1)),
				new Point(params.get(2), params.get(3)));
		stack.push(ln);
	}

}
