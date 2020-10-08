package hr.fer.ooup.lab4;

public interface Renderer {
	void drawLine(Point s, Point e);

	void fillPolygon(Point[] points);
}