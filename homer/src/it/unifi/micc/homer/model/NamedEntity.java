/**
 * 
 */
package it.unifi.micc.homer.model;

/**
 * @author bertini
 * 
 */
public class NamedEntity implements Comparable<Object> {

	private KeywordType type;
	private String value;
	/** Contains the start position of entity's value */
	private int start;
	/** Contains the start position of entity's value */
	private int end;

	public int compareTo(Object o) { // TODO check: is this really needed ?
		NamedEntity aux = (NamedEntity) o;
		if (this.getStart() < aux.getStart())
			return -1;
		else if (this.getStart() == aux.getStart())
			return 0;
		else
			return 1;
	}

	// Getter and Setter methods
	public KeywordType getType() {
		return type;
	}

	public void setType(KeywordType type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}
}
