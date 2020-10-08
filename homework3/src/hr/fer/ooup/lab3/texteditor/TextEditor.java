package hr.fer.ooup.lab3.texteditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.Stack;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.Scrollable;

public class TextEditor extends JComponent
		implements KeyListener, CursorObserver, ClipboardObserver, UndoManagerListener, Scrollable {
	private static final long serialVersionUID = 1L;
	private static final Dimension PREFERRED_SIZE = new Dimension(800, 600);
	private Dimension componentSize;
	private static final int FONT_SIZE = 20;
	private static final int INDENTATION = 20;
	private static final int SPACE_BETWEEN_LINES = 5;
	public static final String COPY_ACTION = "copyAction";
	public static final String CUT_ACTION = "cutAction";
	public static final String PASTE_ACTION = "pasteAction";
	public static final String PASTE_ONCE_ACTION = "pasteOnceAction";
	public static final String UNDO_ACTION = "undoAction";
	public static final String REDO_ACTION = "redoAction";
	public static final String DELETE_ACTION = "deleteAction";
	public static final String CLEAR_ACTION = "clearAction";
	private TextEditorModel model;
	private boolean shiftPressed;
	private ClipboardStack clipboardStack;
	private Font font = new Font(Font.MONOSPACED, Font.PLAIN, FONT_SIZE);

	private Action copyAction = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			LocationRange r = model.getSelectionRange();
			if (!r.getStart().equals(r.getEnd())) {
				clipboardStack.push(model.getSelectedText());
			}
		}

	};

	private Action cutAction = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			LocationRange r = model.getSelectionRange();
			if (!r.getStart().equals(r.getEnd())) {
				clipboardStack.push(model.getSelectedText());
				model.deleteRange(r);
			}
		}

	};

	private Action pasteAction = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			if (clipboardStack.isEmpty()) {
				return;
			}

			model.insert(clipboardStack.peek());
		}

	};

	private Action pasteOnceAction = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			if (clipboardStack.isEmpty()) {
				return;
			}
			String text = clipboardStack.pop();
			model.insert(text);
		}

	};

	private Action undoAction = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			UndoManager.getInstance().undo();
		}

	};

	private Action redoAction = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			UndoManager.getInstance().redo();
		}

	};

	public TextEditor(TextEditorModel model) {
		this.model = model;
		addKeyListener(this);
		initFocusHandling();
		model.addCursorObserver(this);
		model.addTextObserver(() -> {
			repaint();
		});
		clipboardStack = new ClipboardStack();
		clipboardStack.addObserver(this);
		UndoManager.getInstance().addObserver(this);
		initActions();
		initKeyBinds();
		componentSize = PREFERRED_SIZE;
	}

	private void initFocusHandling() {
		setFocusable(true);
		requestFocus();
		addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				requestFocus();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

		});

	}

	@Override
	public void updateClipboard() {
		if (clipboardStack.isEmpty()) {
			pasteAction.setEnabled(false);
			pasteOnceAction.setEnabled(false);
		} else {
			pasteAction.setEnabled(true);
			pasteOnceAction.setEnabled(true);
		}

	}

	private void initActions() {
		cutAction.putValue(Action.NAME, "Cut");
		copyAction.putValue(Action.NAME, "Copy");
		pasteAction.putValue(Action.NAME, "Paste");
		pasteOnceAction.putValue(Action.NAME, "Paste and Take");
		undoAction.putValue(Action.NAME, "Undo");
		redoAction.putValue(Action.NAME, "Redo");

		cutAction.setEnabled(false);
		copyAction.setEnabled(false);
		pasteAction.setEnabled(false);
		pasteOnceAction.setEnabled(false);
		undoAction.setEnabled(false);
		redoAction.setEnabled(false);
	}

	private void initKeyBinds() {
		getInputMap().put(KeyStroke.getKeyStroke("control C"), COPY_ACTION);
		getActionMap().put(COPY_ACTION, copyAction);
		getInputMap().put(KeyStroke.getKeyStroke("control X"), CUT_ACTION);
		getActionMap().put(CUT_ACTION, cutAction);
		getInputMap().put(KeyStroke.getKeyStroke("control V"), PASTE_ACTION);
		getActionMap().put(PASTE_ACTION, pasteAction);
		getInputMap().put(KeyStroke.getKeyStroke("control shift pressed V"), PASTE_ONCE_ACTION);
		getActionMap().put(PASTE_ONCE_ACTION, pasteOnceAction);
		getInputMap().put(KeyStroke.getKeyStroke("control Z"), UNDO_ACTION);
		getActionMap().put(UNDO_ACTION, undoAction);
		getInputMap().put(KeyStroke.getKeyStroke("control Y"), REDO_ACTION);
		getActionMap().put(REDO_ACTION, redoAction);
	}

	public Action getAction(String actionKey) {
		return getActionMap().get(actionKey);
	}

	@Override
	public void paintComponent(Graphics g) {
		Insets ins = getInsets();
		int longestLineWidth = 0;

		g.setColor(Color.BLACK);

		g.setFont(font);
		FontMetrics fontMet = g.getFontMetrics();

		int lineY = ins.top + FONT_SIZE;
		int lineX = ins.left + INDENTATION;
		Iterator<String> lineIter = model.allLines();
		int lineNum = 0;
		Location cursLoc = model.getCursorLocation();
		while (lineIter.hasNext()) {
			String line = lineIter.next();

			LocationRange sel = model.getSelectionRange();
			sel.sortStartAndEnd();
			if (lineNum >= sel.getStart().getRow() && lineNum <= sel.getEnd().getRow()) {
				int start = sel.getStart().getRow() == lineNum ? sel.getStart().getColumn() : 0;
				int end = sel.getEnd().getRow() == lineNum ? sel.getEnd().getColumn() : line.length();
				String selection = "";
				try {
					selection = line.substring(start, end);
				} catch (StringIndexOutOfBoundsException ex) {
					ex.printStackTrace();
				}

				Rectangle2D rect = fontMet.getStringBounds(selection, g);
				int selStartPos = fontMet.stringWidth(line.substring(0, start));
				g.setColor(Color.BLUE);
				g.fillRect(lineX + selStartPos, lineY - FONT_SIZE, (int) rect.getWidth(), (int) rect.getHeight());
				g.setColor(Color.BLACK);
			}

			g.drawString(line, lineX, lineY);
			int lineWidth = fontMet.stringWidth(line);
			if (lineWidth > longestLineWidth) {
				longestLineWidth = lineWidth;
			}

			if (lineNum == cursLoc.getRow()) {
				int cursorPos = fontMet.stringWidth(line.substring(0, cursLoc.getColumn()));
				g.drawLine(lineX + cursorPos, lineY - FONT_SIZE, lineX + cursorPos, lineY);

				if (cursorPos >= longestLineWidth) {
					longestLineWidth = cursorPos + 1;
				}
			}

			lineY += FONT_SIZE + SPACE_BETWEEN_LINES;
			lineNum++;
		}

		componentSize.setSize(longestLineWidth + ins.left + INDENTATION, lineY);
		getParent().doLayout();
	}

	@Override
	public Dimension getPreferredSize() {
		return componentSize;
	}

	@Override
	public void keyPressed(KeyEvent ev) {
		int code = ev.getKeyCode();
		if (code == KeyEvent.VK_BACK_SPACE || code == KeyEvent.VK_DELETE) {
			LocationRange r = model.getSelectionRange();
			if (r.getStart().equals(r.getEnd())) {
				if (code == KeyEvent.VK_BACK_SPACE) {
					model.deleteBefore();
				} else {
					model.deleteAfter();
				}
			} else {
				model.deleteRange(r);
			}
		} else if (code == KeyEvent.VK_LEFT) {
			model.moveCursorLeft();
		} else if (code == KeyEvent.VK_RIGHT) {
			model.moveCursorRight();
		} else if (code == KeyEvent.VK_UP) {
			model.moveCursorUp();
		} else if (code == KeyEvent.VK_DOWN) {
			model.moveCursorDown();
		} else if (code == KeyEvent.VK_SHIFT && ev.getKeyLocation() == KeyEvent.KEY_LOCATION_LEFT) {
			shiftPressed = true;
		} else {
			char c = ev.getKeyChar();
			if (font.canDisplay(c)) {
				model.insert(c);
			}
		}
		repaint();
	}

	@Override
	public void keyReleased(KeyEvent ev) {
		if (ev.getKeyCode() == KeyEvent.VK_SHIFT) {
			shiftPressed = false;
		}
	}

	@Override
	public Color getBackground() {
		return Color.WHITE;
	}

	@Override
	public void keyTyped(KeyEvent ev) {
	}

	@Override
	public void updateCursorLocation(Location loc) {
		repaint();
		if (shiftPressed) {
			LocationRange r = model.getSelectionRange();
			r.setEnd(loc);
			model.setSelectionRange(r);
		} else {
			model.setSelectionRange(new LocationRange(loc, loc));
		}

		LocationRange r = model.getSelectionRange();
		if (r.getStart().equals(r.getEnd())) {
			cutAction.setEnabled(false);
			copyAction.setEnabled(false);
		} else {
			cutAction.setEnabled(true);
			copyAction.setEnabled(true);
		}
	}

	@Override
	public void onDataChanged(Stack<EditAction> undoStack, Stack<EditAction> redoStack) {
		if (undoStack.isEmpty()) {
			undoAction.setEnabled(false);
		} else {
			undoAction.setEnabled(true);
		}
		if (redoStack.isEmpty()) {
			redoAction.setEnabled(false);
		} else {
			redoAction.setEnabled(true);
		}

	}

	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return FONT_SIZE;
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return FONT_SIZE;
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

}
