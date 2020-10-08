package hr.fer.ooup.lab4;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

public class Main {

	public static void main(String[] args) {
		List<GraphicalObject> objects = new ArrayList<>();

		objects.add(new LineSegment());
		objects.add(new Oval());

		SwingUtilities.invokeLater(() -> new GUI(objects).setVisible(true));
	}
}
