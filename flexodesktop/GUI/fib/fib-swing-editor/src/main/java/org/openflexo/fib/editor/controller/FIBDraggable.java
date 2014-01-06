package org.openflexo.fib.editor.controller;

import java.awt.Point;

import org.openflexo.fib.editor.view.FIBEditableViewDelegate.FIBDropTarget;

public interface FIBDraggable {

	public void enableDragging();

	public void disableDragging();

	public boolean acceptDragging(FIBDropTarget target);

	public boolean elementDragged(FIBDropTarget target, Point pt);
}
