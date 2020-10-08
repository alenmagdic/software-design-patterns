package hr.fer.ooup.lab4;

public class GeometryUtil {

	public static double distanceFromPoint(Point point1, Point point2) {
		return Math.sqrt(Math.pow(point2.getX() - point1.getX(), 2) + Math.pow(point2.getY() - point1.getY(), 2));
	}

	public static double distanceFromLineSegment(Point s, Point e, Point p) {
		Point leftPoint = s.getX() <= e.getX() ? s : e;
		Point rightPoint = s.getX() <= e.getX() ? e : s;
		if (p.getX() <= leftPoint.getX()) {
			return distanceFromPoint(p, leftPoint);
		} else if (p.getX() >= rightPoint.getX()) {
			return distanceFromPoint(p, rightPoint);
		}

		double k = -((double) rightPoint.getY() - leftPoint.getY()) / (rightPoint.getX() - leftPoint.getX());
		double l = -leftPoint.getY() - k * leftPoint.getX();
		return Math.abs(k * p.getX() + l + p.getY()) / Math.sqrt(k * k + 1);
	}
}