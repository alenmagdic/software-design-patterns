package hr.fer.ooup.lab4;

import java.awt.Color;
import java.awt.Graphics2D;

public class G2DRendererImpl implements Renderer {

	private Graphics2D g2d;

	public G2DRendererImpl(Graphics2D g2d) {
		this.g2d = g2d;
	}

	@Override
	public void drawLine(Point s, Point e) {
		g2d.setColor(Color.BLUE);
		g2d.drawLine(s.getX(), s.getY(), e.getX(), e.getY());
	}

	@Override
	public void fillPolygon(Point[] points) {
		g2d.setColor(Color.BLUE);
		int[] pointsX = new int[points.length];
		int[] pointsY = new int[points.length];
		for (int i = 0; i < points.length; i++) {
			pointsX[i] = points[i].getX();
			pointsY[i] = points[i].getY();
		}

		g2d.fillPolygon(pointsX, pointsY, points.length);
		g2d.setColor(Color.RED);
		g2d.drawPolygon(pointsX, pointsY, points.length);
	}

}