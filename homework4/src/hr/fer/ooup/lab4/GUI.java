package hr.fer.ooup.lab4;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Container;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

public class GUI extends JFrame {
	private static final long serialVersionUID = 1L;
	private List<GraphicalObject> objects;
	private DocumentModel documentModel;
	private Canvas canvas;
	private State currentState;

	public GUI(List<GraphicalObject> objects) {
		this.objects = new ArrayList<>(objects);
		this.documentModel = new DocumentModel();
		this.canvas = new Canvas(documentModel, this);
		this.currentState = new IdleState();

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(800, 600);
		setTitle("Paint");
		setLocationRelativeTo(null);

		initGUI();

		canvas.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent ev) {
				if (ev.getKeyCode() == KeyEvent.VK_ESCAPE) {
					setState(new IdleState());
				}
			}
		});
	}

	private void initGUI() {
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());

		cp.add(canvas);
		createToolbars();

	}

	private void setState(State s) {
		currentState.onLeaving();
		currentState = s;
	}

	private void createToolbars() {
		JToolBar toolbar = new JToolBar();
		for (GraphicalObject o : objects) {
			Button b = new Button(o.getShapeName());
			b.addActionListener(ev -> {
				setState(new AddShapeState(documentModel, o));
				canvas.requestFocus();
			});
			toolbar.add(b);
		}

		Button b = new Button("Selektiraj");
		b.addActionListener(ev -> {
			setState(new SelectShapeState(documentModel));
			canvas.requestFocus();
		});
		toolbar.add(b);

		b = new Button("Briši");
		b.addActionListener(ev -> {
			setState(new EraserState(documentModel));
			canvas.requestFocus();
		});
		toolbar.add(b);

		b = new Button("SVG Export");
		b.addActionListener(ev -> {
			svgExport();
			canvas.requestFocus();
		});
		toolbar.add(b);

		b = new Button("Pohrani");
		b.addActionListener(ev -> {
			save();
			canvas.requestFocus();
		});
		toolbar.add(b);

		b = new Button("Ucitaj");
		b.addActionListener(ev -> {
			load();
			canvas.requestFocus();
		});
		toolbar.add(b);

		getContentPane().add(toolbar, BorderLayout.PAGE_START);
	}

	private void load() {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Load");

		if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
			return;
		}

		Path filePath = fc.getSelectedFile().toPath();

		String text = null;
		try {
			text = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		}

		List<String> lines = new ArrayList<>(Arrays.asList(text.split("\n")));

		Map<String, GraphicalObject> mapOfObjects = getMapOfObjects();
		Stack<GraphicalObject> stack = new Stack<>();

		for (String l : lines) {
			String id = l.split(" ")[0].substring(1);
			mapOfObjects.get(id).load(stack, l.substring(id.length() + 2));
		}

		documentModel.clear();
		for (GraphicalObject o : stack) {
			documentModel.addGraphicalObject(o);
		}
	}

	private Map<String, GraphicalObject> getMapOfObjects() {
		Map<String, GraphicalObject> map = new HashMap<>();
		for (GraphicalObject o : objects) {
			map.put(o.getShapeID(), o);
		}
		CompositeShape cmp = new CompositeShape(new ArrayList<>());
		map.put(cmp.getShapeID(), cmp);
		return map;
	}

	private void save() {
		Path path = getFilePathForSaving();
		if (path == null) {
			return;
		}
		List<String> rows = new ArrayList<>();
		for (GraphicalObject go : documentModel.getObjects()) {
			go.save(rows);
		}
		BufferedWriter wr = null;
		try {
			wr = Files.newBufferedWriter(path);
			for (String l : rows) {
				wr.write(l + "\n");
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				wr.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private void svgExport() {
		Path path = getFilePathForSaving();
		if (path == null) {
			return;
		}
		SVGRendererImpl r = new SVGRendererImpl(path.toString());
		for (GraphicalObject o : documentModel.getObjects()) {
			o.render(r);
		}
		try {
			r.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Path getFilePathForSaving() {
		while (true) {
			JFileChooser fc = new JFileChooser();
			fc.setDialogTitle("SVG Export");
			if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
				return null;
			}
			return fc.getSelectedFile().toPath();
		}
	}

	public State getCurrentState() {
		return currentState;
	}

}
