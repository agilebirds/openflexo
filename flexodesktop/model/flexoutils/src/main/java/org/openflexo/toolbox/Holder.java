package org.openflexo.toolbox;

public class Holder<T> {
	private T value;

	public T get() {
		return value;
	}

	public T getValue() {
		return get();
	}

	public void set(T value) {
		this.value = value;
	}

	public void setValue(T value) {
		set(value);
	}
}