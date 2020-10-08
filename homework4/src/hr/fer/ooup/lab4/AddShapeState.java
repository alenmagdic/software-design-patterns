package hr.fer.ooup.lab4;

public class AddShapeState implements State {

	private GraphicalObject prototype;
	private DocumentModel model;

	public AddShapeState(DocumentModel model, GraphicalObject prototype) {
		this.model = model;
		this.prototype = prototype;
	}

	@Override
	public void mouseDown(Point mousePoint, boolean shiftDown, boolean ctrlDown) {
		GraphicalObject newObj = prototype.duplicate();
		newObj.translate(mousePoint);
		model.addGraphicalObject(newObj);
	}

	@Override
	public void mouseUp(Point mousePoint, boolean shiftDown, boolean ctrlDown) {
	}

	@Override
	public void mouseDragged(Point mousePoint) {
	}

	@Override
	public void keyPressed(int keyCode) {
	}

	@Override
	public void afterDraw(Renderer r, GraphicalObject go) {
	}

	@Override
	public void afterDraw(Renderer r) {
	}

	@Override
	public void onLeaving() {
	}

}