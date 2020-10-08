package hr.fer.ooup.lab3.texteditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class TextEditorModel {
	private List<String> lines;
	private LocationRange selectionRange;
	private Location cursorLocation;
	private List<CursorObserver> cursorObservers;
	private List<TextObserver> textObservers;

	public TextEditorModel(String text) {
		lines = Arrays.stream(text.split("\n")).collect(Collectors.toList());
		cursorLocation = new Location(lines.size() - 1, lines.get(lines.size() - 1).length());
		selectionRange = new LocationRange(cursorLocation, cursorLocation);

		cursorObservers = new ArrayList<>();
		textObservers = new ArrayList<>();
	}

	public Location getCursorLocation() {
		return cursorLocation;
	}

	public LocationRange getSelectionRange() {
		return new LocationRange(selectionRange);
	}

	public void clearDocument() {
		Location startLoc = new Location(0, 0);
		Location endLoc = new Location(lines.size() - 1, lines.get(lines.size() - 1).length());
		deleteRange(new LocationRange(startLoc, endLoc));
	}

	public void moveCursorToStart() {
		cursorLocation.setColumn(0);
		cursorLocation.setRow(0);
		selectionRange.setStart(cursorLocation);
		selectionRange.setEnd(cursorLocation);
		notifyCursorObservers();
	}

	public void moveCursorToEnd() {
		cursorLocation.setRow(lines.size() - 1);
		cursorLocation.setColumn(lines.get(cursorLocation.getRow()).length());
		selectionRange.setStart(cursorLocation);
		selectionRange.setEnd(cursorLocation);
		notifyCursorObservers();
	}

	public void setSelectionRange(LocationRange selectionRange) {
		this.selectionRange = new LocationRange(selectionRange);
	}

	public void addCursorObserver(CursorObserver observer) {
		cursorObservers.add(observer);
	}

	public void removeCursorObserver(CursorObserver observer) {
		cursorObservers.remove(observer);
	}

	private void notifyCursorObservers() {
		for (CursorObserver observer : cursorObservers) {
			observer.updateCursorLocation(cursorLocation);
		}
	}

	public void addTextObserver(TextObserver observer) {
		textObservers.add(observer);
	}

	public void removeTextObserver(TextObserver observer) {
		textObservers.remove(observer);
	}

	private void notifyTextObservers() {
		for (TextObserver observer : textObservers) {
			observer.updateText();
		}
	}

	public List<String> getLines() {
		return lines;
	}

	public Iterator<String> allLines() {
		return new TextIterator(0, lines.size());
	}

	public Iterator<String> linesRange(int index1, int index2) {
		return new TextIterator(index1, index2);
	}

	public void moveCursorLeft() {
		if (cursorLocation.getColumn() == 0) {
			if (cursorLocation.getRow() > 0) {
				cursorLocation.setRow(cursorLocation.getRow() - 1);
				cursorLocation.setColumn(lines.get(cursorLocation.getRow()).length());
				notifyCursorObservers();
			}
		} else {
			cursorLocation.setColumn(cursorLocation.getColumn() - 1);
			notifyCursorObservers();
		}

	}

	public void moveCursorRight() {
		if (cursorLocation.getColumn() == lines.get(cursorLocation.getRow()).length()) {
			if (cursorLocation.getRow() < lines.size() - 1) {
				cursorLocation.setRow(cursorLocation.getRow() + 1);
				cursorLocation.setColumn(0);
				notifyCursorObservers();
			}
		} else {
			cursorLocation.setColumn(cursorLocation.getColumn() + 1);
			notifyCursorObservers();
		}

	}

	public void moveCursorUp() {
		if (cursorLocation.getRow() > 0) {
			cursorLocation.setRow(cursorLocation.getRow() - 1);
			int newColumnNum = Math.min(cursorLocation.getColumn(), lines.get(cursorLocation.getRow()).length());
			cursorLocation.setColumn(newColumnNum);
			notifyCursorObservers();
		}

	}

	public void moveCursorDown() {
		if (cursorLocation.getRow() < lines.size() - 1) {
			cursorLocation.setRow(cursorLocation.getRow() + 1);
			int newColumnNum = Math.min(cursorLocation.getColumn(), lines.get(cursorLocation.getRow()).length());
			cursorLocation.setColumn(newColumnNum);
			notifyCursorObservers();
		}

	}

	public void deleteBefore() {
		if (cursorLocation.getColumn() == 0 && cursorLocation.getRow() == 0) {
			return;
		}
		moveCursorLeft();
		deleteAfter();
	}

	private LocationRange getRangeBetweenCurrentAndNextChar() {
		int lineNum = cursorLocation.getRow();
		int columnNum = cursorLocation.getColumn();
		if (lineNum == lines.size() - 1 && columnNum == lines.get(lineNum).length()) {
			return null;
		}
		Location loc = cursorLocation;
		Location locEnd;
		if (loc.getColumn() < lines.get(loc.getRow()).length()) {
			locEnd = new Location(loc.getRow(), loc.getColumn() + 1);
		} else {
			locEnd = new Location(loc.getRow() + 1, 0);
		}
		return new LocationRange(loc, locEnd);
	}

	public void deleteAfter() {
		LocationRange r = getRangeBetweenCurrentAndNextChar();
		if (r != null) {
			deleteText(r, true);
		}
	}

	private void deleteText(LocationRange r, boolean pushToUndoStack) {
		r.sortStartAndEnd();
		Location s = r.getStart();
		Location e = r.getEnd();
		Location oldCursorLoc = new Location(cursorLocation);
		String textToDelete = getTextInRange(new LocationRange(r));

		if (s.getRow() == e.getRow()) {
			String line = lines.get(s.getRow());
			lines.set(s.getRow(), line.substring(0, s.getColumn()) + line.substring(e.getColumn()));
		} else {
			String lineFirst = lines.get(s.getRow());
			String lineLast = lines.get(e.getRow());
			lines.set(s.getRow(), lineFirst.substring(0, s.getColumn()) + lineLast.substring(e.getColumn()));

			for (int i = s.getRow() + 1, j = i; i <= e.getRow(); i++) {
				lines.remove(j);
			}
		}
		cursorLocation = s;
		selectionRange = new LocationRange(cursorLocation, cursorLocation);

		notifyTextObservers();
		notifyCursorObservers();

		if (pushToUndoStack) {
			UndoManager.getInstance().push(new TextDeleteAction(new LocationRange(r), oldCursorLoc, textToDelete));
		}
	}

	private class TextDeleteAction implements EditAction {
		private LocationRange range;
		private Location oldCursorLoc;
		private String textToDelete;

		public TextDeleteAction(LocationRange range, Location oldCursorLoc, String textToDelete) {
			super();
			this.range = range;
			this.oldCursorLoc = oldCursorLoc;
			this.textToDelete = textToDelete;
		}

		@Override
		public void executeDo() {
			cursorLocation = oldCursorLoc;
			deleteText(range, false);
		}

		@Override
		public void executeUndo() {
			range.sortStartAndEnd();
			cursorLocation = range.getStart();
			insertText(textToDelete, false);
			cursorLocation = oldCursorLoc;
		}

	}

	public void deleteRange(LocationRange r) {
		deleteText(r, true);
	}

	private void insertText(String text, boolean pushToUndoStack) {
		if (!selectionRange.getStart().equals(selectionRange.getEnd())) {
			deleteRange(selectionRange);
		}
		Location oldCursorLoc = new Location(cursorLocation);
		int lineNum = cursorLocation.getRow();
		int columnNum = cursorLocation.getColumn();
		String line = lines.get(lineNum);

		int columnToLineEnd = line.length() - columnNum;
		String newContent = line.substring(0, columnNum) + text + line.substring(columnNum);
		List<String> newLines = Arrays.stream(newContent.split("\n")).collect(Collectors.toList());
		if (newContent.endsWith("\n")) {
			if (newContent.equals("\n")) {
				newLines.add("");
			}
			newLines.add("");
		}
		lines.set(lineNum, newLines.get(0));
		for (int i = newLines.size() - 1; i > 0; i--) {
			lines.add(lineNum + 1, newLines.get(i));
		}
		if (newLines.size() > 1) {
			cursorLocation.setRow(lineNum + newLines.size() - 1);
		}
		cursorLocation.setColumn(lines.get(cursorLocation.getRow()).length() - columnToLineEnd);
		selectionRange = new LocationRange(cursorLocation, cursorLocation);

		notifyTextObservers();
		notifyCursorObservers();

		if (pushToUndoStack) {
			Location newCursorLoc = new Location(cursorLocation);
			UndoManager.getInstance().push(new TextTypeAction(text, oldCursorLoc, newCursorLoc));
		}
	}

	private class TextTypeAction implements EditAction {
		private Location oldCursorLoc;
		private Location newCursorLoc;
		private String text;

		public TextTypeAction(String text, Location oldCursorLoc, Location newCursorLoc) {
			this.oldCursorLoc = oldCursorLoc;
			this.newCursorLoc = newCursorLoc;
			this.text = text;
		}

		@Override
		public void executeUndo() {
			deleteText(new LocationRange(oldCursorLoc, newCursorLoc), false);
		}

		@Override
		public void executeDo() {
			cursorLocation = new Location(oldCursorLoc);
			insertText(text, false);
		}
	}

	public void insert(char c) {
		insert(Character.toString(c));
	}

	public void insert(String text) {
		insertText(text, true);
	}

	public String getTextInRange(LocationRange r) {
		r.sortStartAndEnd();
		Location s = r.getStart();
		Location e = r.getEnd();

		if (s.getRow() == e.getRow()) {
			return lines.get(s.getRow()).substring(s.getColumn(), e.getColumn());
		} else {
			StringBuilder strB = new StringBuilder();
			strB.append(lines.get(s.getRow()).substring(s.getColumn()) + "\n");
			for (int i = s.getRow() + 1; i < e.getRow(); i++) {
				strB.append(lines.get(i) + "\n");
			}
			strB.append(lines.get(e.getRow()).substring(0, e.getColumn()));
			return strB.toString();
		}
	}

	public String getSelectedText() {
		LocationRange sel = getSelectionRange();
		return getTextInRange(sel);
	}

	public String getText() {
		return String.join("\r\n", lines);
	}

	public class TextIterator implements Iterator<String> {
		private int next;
		private int endIndex;

		public TextIterator(int startIndex, int endIndex) {
			next = startIndex;
			this.endIndex = endIndex;
		}

		@Override
		public boolean hasNext() {
			return next < endIndex;
		}

		@Override
		public String next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			next++;
			return lines.get(next - 1);
		}

	}

}
