package hr.fer.ooup.lab4;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SVGRendererImpl implements Renderer {

	private List<String> lines = new ArrayList<>();
	private String fileName;

	public SVGRendererImpl(String fileName) {
		this.fileName = fileName;
		lines.add("<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">");
	}

	public void close() throws IOException {
		lines.add("</svg>");
		BufferedWriter wr = Files.newBufferedWriter(Paths.get(fileName));
		for (String l : lines) {
			wr.write(l + "\n");
		}
		wr.close();
	}

	@Override
	public void drawLine(Point s, Point e) {
		lines.add(String.format("<line x1=\"%d\" y1=\"%d\" x2=\"%d\" y2=\"%d\" style=\"stroke:#0000FF;\"/>", s.getX(),
				s.getY(), e.getX(), e.getY()));
	}

	@Override
	public void fillPolygon(Point[] points) {
		StringBuilder pointsStr = new StringBuilder();
		for (Point p : points) {
			pointsStr.append(String.format("%d,%d ", p.getX(), p.getY()));
		}
		lines.add(String.format("<polygon points=\"%s\" style=\"stroke:#FF0000; fill:#0000FF;\"/>",
				pointsStr.toString()));
	}

}