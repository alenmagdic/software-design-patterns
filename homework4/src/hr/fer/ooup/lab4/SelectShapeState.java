package hr.fer.ooup.lab4;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class SelectShapeState implements State {
	private DocumentModel model;

	public SelectShapeState(DocumentModel documentModel) {
		this.model = documentModel;
	}

	@Override
	public void mouseDown(Point mousePoint, boolean shiftDown, boolean ctrlDown) {
		GraphicalObject sel = model.findSelectedGraphicalObject(mousePoint);
		if (model.getSelectedObjects().size() == 1 && model.getSelectedObjects().get(0) == sel) {
			int hotpointInd = model.findSelectedHotPoint(sel, mousePoint);
			if (hotpointInd != -1) {
				deselectHotpoints(sel);
				sel.setHotPointSelected(hotpointInd, true);
			}
		} else {
			if (sel != null && !ctrlDown) {
				deselectSelected();
			}
			if (sel != null) {
				sel.setSelected(true);
			}
		}

	}

	private void deselectHotpoints(GraphicalObject obj) {
		for (int i = 0; i < obj.getNumberOfHotPoints(); i++) {
			obj.setHotPointSelected(i, false);
		}
	}

	@Override
	public void mouseUp(Point mousePoint, boolean shiftDown, boolean ctrlDown) {
		if (model.getSelectedObjects().size() == 1) {
			deselectHotpoints(model.getSelectedObjects().get(0));
		}
	}

	@Override
	public void mouseDragged(Point mousePoint) {
		if (model.getSelectedObjects().size() == 1) {
			GraphicalObject sel = model.getSelectedObjects().get(0);
			for (int i = 0; i < sel.getNumberOfHotPoints(); i++) {
				if (sel.isHotPointSelected(i)) {
					sel.setHotPoint(i, mousePoint);
					return;
				}
			}
		}
	}

	@Override
	public void keyPressed(int keyCode) {
		List<GraphicalObject> selObjs = model.getSelectedObjects();
		if (keyCode == KeyEvent.VK_LEFT) {
			translateAll(selObjs, new Point(-1, 0));
		} else if (keyCode == KeyEvent.VK_RIGHT) {
			translateAll(selObjs, new Point(1, 0));
		} else if (keyCode == KeyEvent.VK_UP) {
			translateAll(selObjs, new Point(0, -1));
		} else if (keyCode == KeyEvent.VK_DOWN) {
			translateAll(selObjs, new Point(0, 1));
		} else if (keyCode == KeyEvent.VK_PLUS) {
			if (selObjs.size() == 1) {
				model.increaseZ(selObjs.get(0));
			}
		} else if (keyCode == KeyEvent.VK_MINUS) {
			if (selObjs.size() == 1) {
				model.decreaseZ(selObjs.get(0));
			}
		} else if (keyCode == KeyEvent.VK_G && selObjs.size() > 1) {
			List<GraphicalObject> children = new ArrayList<>(selObjs);
			children.forEach(ch -> model.removeGraphicalObject(ch));
			CompositeShape comp = new CompositeShape(children);
			comp.setSelected(true);
			model.addGraphicalObject(comp);
		} else if (keyCode == KeyEvent.VK_U && selObjs.size() == 1 && selObjs.get(0) instanceof CompositeShape) {
			CompositeShape comp = (CompositeShape) selObjs.get(0);
			model.removeGraphicalObject(comp);
			for (GraphicalObject ch : comp.getChildren()) {
				model.addGraphicalObject(ch);
				ch.setSelected(true);
			}
		}

	}

	private void translateAll(List<GraphicalObject> selObjs, Point delta) {
		for (GraphicalObject obj : selObjs) {
			obj.translate(delta);
		}

	}

	@Override
	public void afterDraw(Renderer r, GraphicalObject go) {
		if (go.isSelected()) {
			Rectangle rect = go.getBoundingBox();
			drawRect(r, rect);
			if (model.getSelectedObjects().size() == 1) {
				for (int i = 0; i < go.getNumberOfHotPoints(); i++) {
					Point hp = go.getHotPoint(i);
					drawRect(r, new Rectangle(hp.getX() - 3, hp.getY() - 3, 6, 6));
				}
			}
		}

	}

	private void drawRect(Renderer r, Rectangle rect) {
		r.drawLine(new Point(rect.getX(), rect.getY()), new Point(rect.getX() + rect.getWidth(), rect.getY()));
		r.drawLine(new Point(rect.getX(), rect.getY() + rect.getHeight()),
				new Point(rect.getX() + rect.getWidth(), rect.getY() + rect.getHeight()));
		r.drawLine(new Point(rect.getX(), rect.getY()), new Point(rect.getX(), rect.getY() + rect.getHeight()));
		r.drawLine(new Point(rect.getX() + rect.getWidth(), rect.getY()),
				new Point(rect.getX() + rect.getWidth(), rect.getY() + rect.getHeight()));

	}

	@Override
	public void afterDraw(Renderer r) {
	}

	private void deselectSelected() {
		List<GraphicalObject> selected = new ArrayList<>(model.getSelectedObjects());
		selected.forEach(o -> o.setSelected(false));
	}

	@Override
	public void onLeaving() {
		deselectSelected();
	}

}
