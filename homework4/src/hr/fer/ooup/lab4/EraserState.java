package hr.fer.ooup.lab4;

import java.util.ArrayList;
import java.util.List;

public class EraserState implements State {
	private List<Point> points;
	private DocumentModel model;

	public EraserState(DocumentModel documentModel) {
		this.model = documentModel;
		points = new ArrayList<>();
	}

	@Override
	public void mouseDown(Point mousePoint, boolean shiftDown, boolean ctrlDown) {
	}

	@Override
	public void mouseUp(Point mousePoint, boolean shiftDown, boolean ctrlDown) {
		for (Point p : points) {
			GraphicalObject go = model.findSelectedGraphicalObject(p);
			if (go != null) {
				model.removeGraphicalObject(go);
			}
		}
		points.clear();
		model.notifyListeners();
	}

	@Override
	public void mouseDragged(Point mousePoint) {
		points.add(mousePoint);
		model.notifyListeners();
	}

	@Override
	public void keyPressed(int keyCode) {
	}

	@Override
	public void afterDraw(Renderer r, GraphicalObject go) {
	}

	@Override
	public void afterDraw(Renderer r) {
		for (int i = 0; i < points.size() - 1; i++) {
			r.drawLine(points.get(i), points.get(i + 1));
		}
	}

	@Override
	public void onLeaving() {
	}

}
