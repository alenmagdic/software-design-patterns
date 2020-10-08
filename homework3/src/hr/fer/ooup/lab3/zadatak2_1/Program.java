package hr.fer.ooup.lab3.zadatak2_1;

import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class Program extends JFrame {
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new Program().setVisible(true));
	}

	public Program() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Moj program");

		initGUI();

		pack();
		setLocationRelativeTo(null);
	}

	private void initGUI() {
		Container cp = getContentPane();

		cp.add(new MojaKomponenta(this));

	}
}
