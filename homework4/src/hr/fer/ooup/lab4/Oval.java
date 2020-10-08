package hr.fer.ooup.lab4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class Oval extends AbstractGraphicalObject {

	public Oval(Point bottomPoint, Point rightPoint) {
		super(new Point[] { bottomPoint, rightPoint });
	}

	public Oval() {
		super(new Point[] { new Point(0, 10), new Point(10, 0) });
	}

	@Override
	public Rectangle getBoundingBox() {
		int rightX = getHotPoint(1).getX();
		int bottomX = getHotPoint(0).getX();
		int width = 2 * (rightX - bottomX);

		int rightY = getHotPoint(1).getY();
		int bottomY = getHotPoint(0).getY();
		int height = 2 * (bottomY - rightY);
		return new Rectangle(rightX - width, bottomY - height, width, height);
	}

	@Override
	public double selectionDistance(Point mousePoint) {
		Rectangle r = getBoundingBox();
		int a = r.getWidth() / 2;
		int b = r.getHeight() / 2;
		double minDistance = Double.MAX_VALUE;
		Point cnt = getCenterPoint();
		for (int x = r.getX(); x <= r.getX() + r.getWidth(); x += 1) {
			double var = b * b - b * b * Math.pow(x - cnt.getX(), 2) / (a * a);
			double sol1 = Math.sqrt(var);
			double sol2 = -sol1;
			double y1 = sol1 + cnt.getY();
			double y2 = sol2 + cnt.getY();
			if (mousePoint.getX() == x && isInRange(mousePoint.getY(), y1, y2)) {
				return 0;
			}

			double dist1 = GeometryUtil.distanceFromPoint(mousePoint, new Point(x, (int) y1));
			double dist2 = GeometryUtil.distanceFromPoint(mousePoint, new Point(x, (int) y2));
			if (dist1 < minDistance) {
				minDistance = dist1;
			}
			if (dist2 < minDistance) {
				minDistance = dist2;
			}
		}
		return minDistance;
	}

	private boolean isInRange(int y, double y1, double y2) {
		int rangeStart = (int) Math.min(y1, y2);
		int rangeEnd = (int) Math.max(y1, y2);
		return y >= rangeStart && y <= rangeEnd;
	}

	private Point getCenterPoint() {
		Rectangle r = getBoundingBox();
		return new Point(r.getX() + r.getWidth() / 2, r.getY() + r.getHeight() / 2);
	}

	@Override
	public String getShapeName() {
		return "Oval";
	}

	@Override
	public GraphicalObject duplicate() {
		Oval dupl = new Oval(getHotPoint(0), getHotPoint(1));
		dupl.setSelected(isSelected());
		dupl.setHotPointSelected(0, isHotPointSelected(0));
		dupl.setHotPointSelected(1, isHotPointSelected(1));
		return dupl;
	}

	@Override
	public void render(Renderer rend) {
		Rectangle r = getBoundingBox();
		int a = r.getWidth() / 2;
		int b = r.getHeight() / 2;
		List<Point> points1 = new ArrayList<>();
		List<Point> points2 = new ArrayList<>();
		Point cnt = getCenterPoint();
		for (int x = r.getX(); x <= r.getX() + r.getWidth(); x += 1) {
			double var = b * b - b * b * Math.pow(x - cnt.getX(), 2) / (a * a);
			double sol1 = Math.sqrt(var);
			double sol2 = -sol1;
			double y1 = sol1 + cnt.getY();
			double y2 = sol2 + cnt.getY();
			points1.add(new Point(x, (int) y1));
			points2.add(new Point(x, (int) y2));
		}
		Collections.reverse(points2);
		if (points2.size() > 0) {
			points2.remove(0);
		}
		points1.addAll(points2);
		rend.fillPolygon(points1.toArray(new Point[0]));
	}

	@Override
	public String getShapeID() {
		return "OVAL";
	}

	@Override
	public void save(List<String> rows) {
		rows.add(String.format("@%s %d %d %d %d", getShapeID(), getHotPoint(1).getX(), getHotPoint(1).getY(),
				getHotPoint(0).getX(), getHotPoint(0).getY()));
	}

	@Override
	public void load(Stack<GraphicalObject> stack, String data) {
		List<Integer> params = Arrays.asList(data.split(" ")).stream().map(num -> Integer.parseInt(num))
				.collect(Collectors.toList());
		Oval ov = new Oval(new Point(params.get(2), params.get(3)), new Point(params.get(0), params.get(1)));
		stack.push(ov);
	}

}
