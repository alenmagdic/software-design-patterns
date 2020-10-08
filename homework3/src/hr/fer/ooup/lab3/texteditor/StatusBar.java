package hr.fer.ooup.lab3.texteditor;

import javax.swing.JLabel;

public class StatusBar extends JLabel implements CursorObserver, TextObserver {
	private static final long serialVersionUID = 1L;
	private static final String TEXT_FORMAT = "Row: %-10d Col: %-10d Rows: %d";
	private TextEditorModel model;

	public StatusBar(TextEditorModel model) {
		this.model = model;
		model.addCursorObserver(this);
		model.addTextObserver(this);
		update();
	}

	public void update() {
		setText(String.format(TEXT_FORMAT, model.getCursorLocation().getRow() + 1,
				model.getCursorLocation().getColumn() + 1, model.getLines().size()));
	}

	@Override
	public void updateCursorLocation(Location loc) {
		update();
	}

	@Override
	public void updateText() {
		update();
	}

}
