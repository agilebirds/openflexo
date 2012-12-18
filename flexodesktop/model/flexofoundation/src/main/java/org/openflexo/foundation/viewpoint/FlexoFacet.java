package org.openflexo.foundation.viewpoint;

public class FlexoFacet<T> {

	private T object;

	public FlexoFacet(T object) {
		this.object = object;
	}

	public T getObject() {
		return object;
	}
}
