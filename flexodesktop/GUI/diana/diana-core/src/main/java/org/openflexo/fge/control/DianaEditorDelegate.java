package org.openflexo.fge.control;

import java.awt.Point;
import java.util.Set;

import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.view.FGEView;

/**
 * This A.P.I defines an helper class used as a hook provider to implement technology-specific schemes<br>
 * A {@link DianaEditorDelegate} works with the {@link AbstractDianaEditor}
 * 
 * @author sylvain
 * 
 */
public interface DianaEditorDelegate {

	public void focusedObjectChanged(DrawingTreeNode<?, ?> oldFocusedObject, DrawingTreeNode<?, ?> newFocusedObject);

	public void objectStartMoving(DrawingTreeNode<?, ?> node);

	public void objectStopMoving(DrawingTreeNode<?, ?> node);

	public void objectsStartMoving(Set<? extends DrawingTreeNode<?, ?>> nodes);

	public void objectsStopMoving(Set<? extends DrawingTreeNode<?, ?>> nodes);

	public void repaintAll();

	public Point getPointInView(Object source, Point point, FGEView<?, ?> view);
}
