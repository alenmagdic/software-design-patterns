package hr.fer.ooup.lab3.zadatak2_1;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JComponent;
import javax.swing.JFrame;

public class MojaKomponenta extends JComponent implements KeyListener {
	private static final long serialVersionUID = 1L;
	private static final Dimension PREFERRED_SIZE = new Dimension(800, 600);
	private JFrame frame;

	public MojaKomponenta(JFrame parentFrame) {
		parentFrame.addKeyListener(this);
		this.frame = parentFrame;
	}

	@Override
	protected void paintComponent(Graphics g) {
		Dimension dim = getSize();

		g.setColor(Color.RED);
		g.drawLine(dim.width / 2, 0, dim.width / 2, dim.height);
		g.drawLine(0, dim.height / 2, dim.width, dim.height / 2);
		g.drawString("Prvi redak proizvoljnog teksta", 0, 30);
		g.drawString("Drugi redak proizvoljnog teksta", 0, 80);
	}

	@Override
	public Dimension getPreferredSize() {
		return PREFERRED_SIZE;

	}

	@Override
	public void keyPressed(KeyEvent ev) {
		if (ev.getKeyCode() == KeyEvent.VK_ENTER) {
			frame.dispose();
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}
}
