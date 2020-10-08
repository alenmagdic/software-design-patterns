package hr.fer.ooup.lab3.texteditor;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ClipboardStack {
	private Stack<String> texts;
	private List<ClipboardObserver> observers;

	public ClipboardStack() {
		texts = new Stack<>();
		observers = new ArrayList<>();
	}

	public void addObserver(ClipboardObserver observer) {
		observers.add(observer);
	}

	public void removeObserver(ClipboardObserver observer) {
		observers.remove(observer);
	}

	private void notifyObservers() {
		for (ClipboardObserver observer : observers) {
			observer.updateClipboard();
		}
	}

	public void push(String text) {
		texts.push(text);
		notifyObservers();
	}

	public String pop() {
		String text = texts.pop();
		notifyObservers();
		return text;
	}

	public String peek() {
		return texts.peek();
	}

	public boolean isEmpty() {
		return texts.isEmpty();
	}

	public void clear() {
		texts.clear();
		notifyObservers();
	}

}
