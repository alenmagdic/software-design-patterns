package hr.fer.ooup.lab3.texteditor;

import java.util.Stack;

public interface UndoManagerListener {

	public void onDataChanged(Stack<EditAction> undoStack, Stack<EditAction> redoStack);
}
