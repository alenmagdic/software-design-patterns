package hr.fer.ooup.lab3.texteditor;

public class LocationRange {
	private Location start;
	private Location end;

	public LocationRange(Location start, Location end) {
		super();
		setStart(start);
		setEnd(end);
	}

	public LocationRange(LocationRange range) {
		super();
		this.start = range.start;
		this.end = range.end;
	}

	public Location getStart() {
		return new Location(start);
	}

	public void setStart(Location start) {
		this.start = new Location(start);
	}

	public Location getEnd() {
		return new Location(end);
	}

	public void setEnd(Location end) {
		this.end = new Location(end);
	}

	public void sortStartAndEnd() {
		if (getStart().getRow() > getEnd().getRow()
				|| getStart().getRow() == getEnd().getRow() && getStart().getColumn() > getEnd().getColumn()) {
			Location temp = getStart();
			setStart(getEnd());
			setEnd(temp);
		}

	}

}
