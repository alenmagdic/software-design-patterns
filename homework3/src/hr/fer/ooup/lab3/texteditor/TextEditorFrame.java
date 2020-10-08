package hr.fer.ooup.lab3.texteditor;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class TextEditorFrame extends JFrame implements CursorObserver {
	private static final long serialVersionUID = 1L;
	private static final String PLUGINS_PATH = "./plugins";
	private TextEditor textEditor;
	private TextEditorModel textEditorModel;
	private StatusBar statusBar;
	private Path openedfilePath;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new TextEditorFrame().setVisible(true));
	}

	public TextEditorFrame() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Text editor");

		try {
			loadPlugins();
		} catch (IOException e) {
			e.printStackTrace();
		}
		initActions();
		initGUI();

		pack();
		setLocationRelativeTo(null);
	}

	private void loadPlugins() throws IOException {
		List<URL> pluginFiles = Files.list(Paths.get(PLUGINS_PATH)).filter(p -> p.toString().endsWith(".jar"))
				.map(p -> {
					try {
						return p.toUri().toURL();
					} catch (MalformedURLException e) {
						e.printStackTrace();
						return null;
					}
				}).collect(Collectors.toList());
		URLClassLoader newClassLoader = new URLClassLoader(pluginFiles.toArray(new URL[0]));

	}

	private void initGUI() {
		Container cp = getContentPane();
		textEditorModel = new TextEditorModel("evo jedna\ntestna recenica\nza moj tekst \n editor");
		textEditorModel.addCursorObserver(this);
		textEditor = new TextEditor(textEditorModel);
		JScrollPane editorPane = new JScrollPane(textEditor);
		add(editorPane, BorderLayout.CENTER);

		createMenus();
		createToolbars();

		statusBar = new StatusBar(textEditorModel);
		cp.add(statusBar, BorderLayout.PAGE_END);

	}

	private void createMenus() {
		JMenuBar menubar = new JMenuBar();

		JMenu fileMenu = new JMenu("File");
		menubar.add(fileMenu);

		fileMenu.add(new JMenuItem(openDocumentAction));
		fileMenu.add(new JMenuItem(saveDocumentAction));
		fileMenu.add(new JMenuItem(exitAction));

		JMenu editMenu = new JMenu("Edit");
		menubar.add(editMenu);

		editMenu.add(new JMenuItem(textEditor.getAction(TextEditor.UNDO_ACTION)));
		editMenu.add(new JMenuItem(textEditor.getAction(TextEditor.REDO_ACTION)));
		editMenu.add(new JMenuItem(textEditor.getAction(TextEditor.CUT_ACTION)));
		editMenu.add(new JMenuItem(textEditor.getAction(TextEditor.COPY_ACTION)));
		editMenu.add(new JMenuItem(textEditor.getAction(TextEditor.PASTE_ACTION)));
		editMenu.add(new JMenuItem(textEditor.getAction(TextEditor.PASTE_ONCE_ACTION)));
		editMenu.add(new JMenuItem(deleteAction));
		editMenu.add(new JMenuItem(clearAction));

		JMenu moveMenu = new JMenu("Move");
		menubar.add(moveMenu);

		moveMenu.add(new JMenuItem(cursorToStartAction));
		moveMenu.add(new JMenuItem(cursorToEndAction));

		setJMenuBar(menubar);
	}

	private void createToolbars() {
		JToolBar toolbar = new JToolBar();

		toolbar.add(new JButton(textEditor.getAction(TextEditor.UNDO_ACTION)));
		toolbar.add(new JButton(textEditor.getAction(TextEditor.REDO_ACTION)));
		toolbar.add(new JButton(textEditor.getAction(TextEditor.CUT_ACTION)));
		toolbar.add(new JButton(textEditor.getAction(TextEditor.COPY_ACTION)));
		toolbar.add(new JButton(textEditor.getAction(TextEditor.PASTE_ACTION)));

		getContentPane().add(toolbar, BorderLayout.PAGE_START);
	}

	private void initActions() {
		deleteAction.putValue(Action.NAME, "Delete selection");
		cursorToEndAction.putValue(Action.NAME, "Cursor to document end");
		cursorToStartAction.putValue(Action.NAME, "Cursor to document start");
		clearAction.putValue(Action.NAME, "Clear document");
		openDocumentAction.putValue(Action.NAME, "Open");
		saveDocumentAction.putValue(Action.NAME, "Save");
		exitAction.putValue(Action.NAME, "Exit");

		deleteAction.setEnabled(false);
	}

	@Override
	public void updateCursorLocation(Location loc) {
		LocationRange r = textEditorModel.getSelectionRange();
		if (r.getStart().equals(r.getEnd())) {
			deleteAction.setEnabled(false);
		} else {
			deleteAction.setEnabled(true);
		}
	}

	private Action openDocumentAction = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser fc = new JFileChooser();
			fc.setDialogTitle("Open file");

			if (fc.showOpenDialog(TextEditorFrame.this) != JFileChooser.APPROVE_OPTION) {
				return;
			}

			Path filePath = fc.getSelectedFile().toPath();

			if (!Files.isReadable(filePath)) {
				JOptionPane.showMessageDialog(TextEditorFrame.this,
						String.format("Odabranu datoteku nije moguće čitati.", filePath.toString()), "Pogreška",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			String text = null;
			try {
				text = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(TextEditorFrame.this, "Došlo je do pogreške pri čitanju datoteke.",
						"Pogreška", JOptionPane.ERROR_MESSAGE);
				return;
			}

			textEditorModel.clearDocument();
			textEditorModel.insert(text);
			openedfilePath = filePath;
		}
	};

	private Action exitAction = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			dispose();
		}
	};

	private Action deleteAction = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			LocationRange r = textEditorModel.getSelectionRange();
			if (!r.getStart().equals(r.getEnd())) {
				textEditorModel.deleteRange(r);
			}
		}

	};

	private Action clearAction = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			textEditorModel.clearDocument();
		}

	};

	private Action cursorToStartAction = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			textEditorModel.moveCursorToStart();
		}

	};

	private Action cursorToEndAction = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			textEditorModel.moveCursorToEnd();
		}

	};

	private Path getFilePathForSaving() {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Spremanje datoteke");
		if (fc.showSaveDialog(TextEditorFrame.this) != JFileChooser.APPROVE_OPTION) {
			return null;
		}
		return fc.getSelectedFile().toPath();
	}

	private Action saveDocumentAction = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			if (openedfilePath == null) {
				Path path = getFilePathForSaving();
				if (path == null) {
					return;
				} else {
					openedfilePath = path;
				}
			}

			try {
				Files.write(openedfilePath, textEditorModel.getText().getBytes(StandardCharsets.UTF_8));
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(TextEditorFrame.this, "Spremanje nije uspjelo.", "Pogreška",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			JOptionPane.showMessageDialog(TextEditorFrame.this, "Spremanje uspješno", "Informacija",
					JOptionPane.INFORMATION_MESSAGE);
		}

	};

}
