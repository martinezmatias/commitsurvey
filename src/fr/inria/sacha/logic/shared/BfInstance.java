package fr.inria.sacha.logic.shared;

import java.io.Serializable;

public class BfInstance implements Serializable {

	public BfInstance() {

	}

	public BfInstance(int id, String hunk, String data) {
		super();
		this.id = id;
		this.hunk = hunk;
		this.data = data;
	}

	public int id;
	public String hunk;
	public String data;
	public boolean selected;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getHunk() {
		return hunk;
	}

	public void setHunk(String hunk) {
		this.hunk = hunk;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public String toString() {
		return "BfInstance [data=" + data + ", selected=" + selected + "]";
	}

}
