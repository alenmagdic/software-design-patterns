package hr.fer.ooup.lab4;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;

public class Canvas extends JComponent implements KeyListener, MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 1L;
	private DocumentModel documentModel;
	private GUI gui;
	private boolean shiftDown;
	private boolean ctrlDown;

	public Canvas(DocumentModel model, GUI gui) {
		this.documentModel = model;
		this.gui = gui;
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		documentModel.addDocumentModelListener(() -> repaint());
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		Renderer r = new G2DRendererImpl(g2d);
		for (GraphicalObject o : documentModel.getObjects()) {
			o.render(r);
			gui.getCurrentState().afterDraw(r, o);
		}
		gui.getCurrentState().afterDraw(r);
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent ev) {
		gui.getCurrentState().mouseDown(new Point(ev.getX(), ev.getY()), shiftDown, ctrlDown);
	}

	@Override
	public void mouseReleased(MouseEvent ev) {
		gui.getCurrentState().mouseUp(new Point(ev.getX(), ev.getY()), shiftDown, ctrlDown);
	}

	@Override
	public void keyPressed(KeyEvent ev) {
		gui.getCurrentState().keyPressed(ev.getKeyCode());

		if (ev.getKeyCode() == KeyEvent.VK_CONTROL) {
			ctrlDown = true;
		}
		if (ev.getKeyCode() == KeyEvent.VK_SHIFT) {
			shiftDown = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent ev) {
		if (ev.getKeyCode() == KeyEvent.VK_CONTROL) {
			ctrlDown = false;
		}
		if (ev.getKeyCode() == KeyEvent.VK_SHIFT) {
			shiftDown = false;
		}
	}

	@Override
	public void keyTyped(KeyEvent ev) {
	}

	@Override
	public void mouseDragged(MouseEvent ev) {
		gui.getCurrentState().mouseDragged(new Point(ev.getX(), ev.getY()));
	}

	@Override
	public void mouseMoved(MouseEvent ev) {
	}
}
