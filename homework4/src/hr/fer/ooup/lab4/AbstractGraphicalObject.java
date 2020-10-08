package hr.fer.ooup.lab4;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractGraphicalObject implements GraphicalObject {
	private Point[] hotPoints;
	private boolean[] hotPointSelected;
	private boolean selected;
	private List<GraphicalObjectListener> listeners;

	public AbstractGraphicalObject(Point[] hotPoints) {
		this.hotPoints = hotPoints;
		hotPointSelected = new boolean[hotPoints.length];
		listeners = new ArrayList<>();
	}

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;

		if (selected == false) {
			for (int i = 0; i < hotPointSelected.length; i++) {
				hotPointSelected[i] = false;
			}
		}

		notifySelectionListeners();
	}

	@Override
	public int getNumberOfHotPoints() {
		return hotPoints.length;
	}

	@Override
	public Point getHotPoint(int index) {
		return hotPoints[index];
	}

	@Override
	public void setHotPoint(int index, Point point) {
		hotPoints[index] = point;
		notifyListeners();
	}

	@Override
	public boolean isHotPointSelected(int index) {
		return hotPointSelected[index];
	}

	@Override
	public void setHotPointSelected(int index, boolean selected) {
		hotPointSelected[index] = selected;
		notifyListeners();
	}

	@Override
	public double getHotPointDistance(int index, Point mousePoint) {
		return GeometryUtil.distanceFromPoint(hotPoints[index], mousePoint);
	}

	@Override
	public void translate(Point delta) {
		for (int i = 0; i < hotPoints.length; i++) {
			hotPoints[i] = hotPoints[i].translate(delta);
		}
		notifyListeners();
	}

	@Override
	public void addGraphicalObjectListener(GraphicalObjectListener l) {
		listeners.add(l);

	}

	@Override
	public void removeGraphicalObjectListener(GraphicalObjectListener l) {
		listeners.remove(l);
	}

	protected void notifyListeners() {
		for (GraphicalObjectListener l : listeners) {
			l.graphicalObjectChanged(this);
		}
	}

	protected void notifySelectionListeners() {
		for (GraphicalObjectListener l : listeners) {
			l.graphicalObjectSelectionChanged(this);
		}
	}

}
