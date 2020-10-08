package hr.fer.ooup.lab3.texteditor;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class UndoManager {
	private static UndoManager instance;
	private Stack<EditAction> undoStack;
	private Stack<EditAction> redoStack;
	private List<UndoManagerListener> observers;

	private UndoManager() {
		undoStack = new Stack<>();
		redoStack = new Stack<>();
		observers = new ArrayList<>();
	}

	public void addObserver(UndoManagerListener observer) {
		observers.add(observer);
	}

	public void removeObserver(UndoManagerListener observer) {
		observers.remove(observer);
	}

	@SuppressWarnings("unchecked")
	private void notifyObservers() {
		for (UndoManagerListener observer : observers) {
			observer.onDataChanged((Stack<EditAction>) undoStack.clone(), (Stack<EditAction>) redoStack.clone());
		}
	}

	public synchronized static UndoManager getInstance() {
		if (instance == null) {
			instance = new UndoManager();
		}
		return instance;
	}

	public void undo() {
		if (undoStack.isEmpty()) {
			return;
		}
		EditAction action = undoStack.pop();
		redoStack.push(action);
		action.executeUndo();
		notifyObservers();
	}

	public void redo() {
		if (redoStack.isEmpty()) {
			return;
		}
		EditAction action = redoStack.pop();
		action.executeDo();
		undoStack.push(action);
		notifyObservers();
	}

	public void push(EditAction c) {
		redoStack.clear();
		undoStack.push(c);
		notifyObservers();
	}
}
