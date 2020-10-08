package hr.fer.ooup.lab4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DocumentModel {
	public final static double SELECTION_PROXIMITY = 10;
	private List<GraphicalObject> objects = new ArrayList<>();
	private List<GraphicalObject> roObjects = Collections.unmodifiableList(objects);
	private List<DocumentModelListener> listeners = new ArrayList<>();
	private List<GraphicalObject> selectedObjects = new ArrayList<>();
	private List<GraphicalObject> roSelectedObjects = Collections.unmodifiableList(selectedObjects);
	private final GraphicalObjectListener goListener = new GraphicalObjectListener() {

		@Override
		public void graphicalObjectChanged(GraphicalObject go) {
			notifyListeners();
		}

		@Override
		public void graphicalObjectSelectionChanged(GraphicalObject go) {
			notifyListeners();
			if (go.isSelected()) {
				if (!selectedObjects.contains(go)) {
					selectedObjects.add(go);
				}
			} else {
				if (selectedObjects.contains(go)) {
					selectedObjects.remove(go);
				}
			}
		}

	};

	public DocumentModel() {
	}

	public void clear() {
		for (GraphicalObject o : objects) {
			o.removeGraphicalObjectListener(goListener);
		}
		objects.clear();
		selectedObjects.clear();
		notifyListeners();
	}

	public void addGraphicalObject(GraphicalObject obj) {
		if (obj.isSelected()) {
			selectedObjects.add(obj);
		}
		objects.add(obj);
		obj.addGraphicalObjectListener(goListener);
		notifyListeners();
	}

	public void removeGraphicalObject(GraphicalObject obj) {
		if (obj.isSelected()) {
			selectedObjects.remove(obj);
		}
		obj.removeGraphicalObjectListener(goListener);
		objects.remove(obj);
		notifyListeners();
	}

	public List<GraphicalObject> list() {
		return roObjects;
	}

	public void addDocumentModelListener(DocumentModelListener l) {
		listeners.add(l);
	}

	public void removeDocumentModelListener(DocumentModelListener l) {
		listeners.remove(l);
	}

	public void notifyListeners() {
		for (DocumentModelListener l : listeners) {
			l.documentChange();
		}
	}

	public List<GraphicalObject> getSelectedObjects() {
		return roSelectedObjects;
	}

	public void increaseZ(GraphicalObject go) {
		int ind = objects.indexOf(go);
		if (ind != -1 && ind < objects.size() - 1) {
			GraphicalObject next = objects.get(ind + 1);
			objects.set(ind + 1, go);
			objects.set(ind, next);
		}
		notifyListeners();
	}

	public void decreaseZ(GraphicalObject go) {
		int ind = objects.indexOf(go);
		if (ind > 0) {
			GraphicalObject previous = objects.get(ind - 1);
			objects.set(ind - 1, go);
			objects.set(ind, previous);
		}
		notifyListeners();
	}

	public GraphicalObject findSelectedGraphicalObject(Point mousePoint) {
		if (objects.size() == 0) {
			return null;
		}
		GraphicalObject closestGo = Collections.min(objects,
				(a, b) -> Double.compare(a.selectionDistance(mousePoint), b.selectionDistance(mousePoint)));
		if (closestGo.selectionDistance(mousePoint) <= SELECTION_PROXIMITY) {
			return closestGo;
		}
		return null;
	}

	public int findSelectedHotPoint(GraphicalObject object, Point mousePoint) {
		double minDistance = object.getHotPointDistance(0, mousePoint);
		int closestPointInd = 0;
		for (int i = 1; i < object.getNumberOfHotPoints(); i++) {
			double dist = object.getHotPointDistance(i, mousePoint);
			if (dist < minDistance) {
				closestPointInd = i;
				minDistance = dist;
			}
		}
		if (minDistance <= SELECTION_PROXIMITY) {
			return closestPointInd;
		}

		return -1;
	}

	public List<GraphicalObject> getObjects() {
		return roObjects;
	}

}