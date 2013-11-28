package org.openflexo.fib.editor.controller;

import java.awt.Point;

public class ExistingElementDrag extends ElementDrag<DraggedFIBComponent> {

	public ExistingElementDrag(DraggedFIBComponent component, Point dragOrigin) {
		super(component, dragOrigin);
	}
}