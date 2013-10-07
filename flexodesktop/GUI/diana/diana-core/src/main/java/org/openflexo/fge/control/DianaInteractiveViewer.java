/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.openflexo.fge.control;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.openflexo.fge.Drawing;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.DrawingTreeNodeIdentifier;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.ShapeGraphicalRepresentation.LocationConstraints;
import org.openflexo.fge.control.actions.MouseDragControlImpl;
import org.openflexo.fge.control.actions.MoveInfo;
import org.openflexo.fge.control.tools.DianaInspectors;
import org.openflexo.fge.cp.ConnectorAdjustingControlPoint;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.cp.ControlPoint;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.notifications.DrawingTreeNodeHierarchyRebuildEnded;
import org.openflexo.fge.notifications.DrawingTreeNodeHierarchyRebuildStarted;
import org.openflexo.fge.swing.JLabelView;
import org.openflexo.fge.view.DianaViewFactory;

/**
 * Represents a basic viewer of a {@link Drawing}<br>
 * 
 * The {@link Drawing} can only be edited with basic editing possibilities whose can be allowed or denied<br>
 * No interaction is provided on structural modifications of the drawing which should be externally controlled.<br>
 * 
 * A {@link DianaInteractiveViewer} manages a focused object and a selection.
 * 
 * @author sylvain
 * 
 * @param <M>
 */
public class DianaInteractiveViewer<M, F extends DianaViewFactory<F, C>, C> extends AbstractDianaEditor<M, F, C> {

	private static final Logger logger = Logger.getLogger(DianaInteractiveViewer.class.getPackage().getName());

	private boolean shapesAreMovable = true;
	private boolean labelsAreEditable = false;
	private boolean objectsAreFocusable = true;
	private boolean objectsAreSelectable = true;
	private boolean objectsAreInspectable = true;

	// private EditorToolbox toolbox;
	private DianaInspectors inspectors;

	private DrawingTreeNode<?, ?> focusedFloatingLabel;

	private List<DrawingTreeNode<?, ?>> focusedObjects;
	private List<DrawingTreeNode<?, ?>> selectedObjects;

	private JLabelView<?> currentlyEditedLabel;
	private ControlArea<?> focusedControlArea;

	private MouseDragControlImpl currentMouseDrag = null;

	private FGEPoint lastClickedPoint;
	private DrawingTreeNode<?, ?> lastSelectedNode;

	/**
	 * Stores selection as a persistent list of identified objects
	 */
	private List<DrawingTreeNodeIdentifier<?>> storedSelection;

	private MoveInfo keyDrivenMovingSession;
	private KeyDrivenMovingSessionTimer keyDrivenMovingSessionTimer = null;

	public DianaInteractiveViewer(Drawing<M> aDrawing, FGEModelFactory factory, F dianaFactory) {
		this(aDrawing, factory, dianaFactory, true, false, true, true, true);
	}

	public DianaInteractiveViewer(Drawing<M> aDrawing, FGEModelFactory factory, F dianaFactory, boolean shapesAreMovable,
			boolean labelsAreEditable, boolean objectsAreFocusable, boolean objectsAreSelectable, boolean objectsAreInspectable) {
		super(aDrawing, factory, dianaFactory);
		this.shapesAreMovable = shapesAreMovable;
		this.labelsAreEditable = labelsAreEditable;
		this.objectsAreFocusable = objectsAreFocusable;
		this.objectsAreSelectable = objectsAreSelectable;
		this.objectsAreInspectable = objectsAreInspectable;
		inspectors = new DianaInspectors(this);
		focusedObjects = new ArrayList<DrawingTreeNode<?, ?>>();
		selectedObjects = new ArrayList<DrawingTreeNode<?, ?>>();
	}

	public void delete() {
		if (inspectors != null) {
			inspectors.delete();
		}
		focusedObjects.clear();
		selectedObjects.clear();
		focusedControlArea = null;
		inspectors = null;
		storedSelection = null;
		super.delete();
	}

	public boolean areShapesMovable() {
		return shapesAreMovable;
	}

	public void setShapesAreMovable(boolean shapesAreMovable) {
		this.shapesAreMovable = shapesAreMovable;
	}

	public boolean areLabelsEditable() {
		return labelsAreEditable;
	}

	public void setLabelsAreEditable(boolean labelsAreEditable) {
		this.labelsAreEditable = labelsAreEditable;
	}

	public boolean areObjectsFocusable() {
		return objectsAreFocusable;
	}

	public void setObjectsAreFocusable(boolean objectsAreFocusable) {
		this.objectsAreFocusable = objectsAreFocusable;
	}

	public boolean areObjectsSelectable() {
		return objectsAreSelectable;
	}

	public void setObjectsAreSelectable(boolean objectsAreSelectable) {
		this.objectsAreSelectable = objectsAreSelectable;
	}

	public boolean areObjectInspectable() {
		return objectsAreInspectable;
	}

	public void setObjectsAreInspectable(boolean objectsAreInspectable) {
		this.objectsAreInspectable = objectsAreInspectable;
	}

	public DianaInspectors getInspectors() {
		return inspectors;
	}

	public DrawingTreeNode<?, ?> getFocusedFloatingLabel() {
		return focusedFloatingLabel;
	}

	public void setFocusedFloatingLabel(DrawingTreeNode<?, ?> aFocusedlabel) {
		// logger.info("setFocusedFloatingLabel() with "+aFocusedlabel);
		if (focusedFloatingLabel == null) {
			if (aFocusedlabel == null) {
				return;
			} else {
				focusedFloatingLabel = aFocusedlabel;
				if (getPaintManager().isPaintingCacheEnabled()) {
					// Just repaint connector
					drawingView.getPaintManager().repaint(focusedFloatingLabel);
				} else {
					// @brutal mode
					drawingView.getPaintManager().repaint(drawingView);
				}
			}
		} else {
			DrawingTreeNode<?, ?> oldFocusedFloatingLabel = focusedFloatingLabel;
			focusedFloatingLabel = aFocusedlabel;
			if (aFocusedlabel == null || focusedFloatingLabel != aFocusedlabel) {
				if (getPaintManager().isPaintingCacheEnabled()) {
					// Just repaint old and eventual new connector
					drawingView.getPaintManager().repaint(oldFocusedFloatingLabel);
					if (aFocusedlabel != null) {
						drawingView.getPaintManager().repaint(focusedFloatingLabel);
					}
				} else {
					// @brutal mode
					drawingView.getPaintManager().repaint(drawingView);
				}
			}
			/*
			 * if (aFocusedlabel == null) { focusedFloatingLabel = null;
			 * drawingView.getPaintManager().repaint(drawingView); } else if
			 * (focusedFloatingLabel != aFocusedlabel) { focusedFloatingLabel =
			 * aFocusedlabel;
			 * drawingView.getPaintManager().repaint(drawingView); }
			 */
		}
	}

	private ShapeNode<?> getFirstSelectedShape() {
		for (DrawingTreeNode<?, ?> node : getSelectedObjects()) {
			if (node instanceof ShapeNode) {
				return (ShapeNode<?>) node;
			}
		}
		return null;
	}

	public List<DrawingTreeNode<?, ?>> getSelectedObjects() {
		return selectedObjects;
	}

	public void setSelectedObjects(List<? extends DrawingTreeNode<?, ?>> someSelectedObjects) {
		stopEditionOfEditedLabelIfAny();
		if (someSelectedObjects == null) {
			setSelectedObjects(new ArrayList<DrawingTreeNode<?, ?>>());
			return;
		}

		if (!selectedObjects.equals(someSelectedObjects)) {
			clearSelection();
			for (DrawingTreeNode<?, ?> d : someSelectedObjects) {
				addToSelectedObjects(d);
			}
		}
	}

	public void setSelectedObject(DrawingTreeNode<?, ?> aNode) {
		stopEditionOfEditedLabelIfAny();
		setSelectedObjects(Collections.singletonList(aNode));
		if (getInspectors() != null) {
			getInspectors().update();
		}
	}

	public void addToSelectedObjects(DrawingTreeNode<?, ?> aNode) {
		stopEditionOfEditedLabelIfAny();
		if (aNode == null) {
			logger.warning("Cannot add null object");
			return;
		}
		if (!selectedObjects.contains(aNode)) {
			selectedObjects.add(aNode);
			aNode.setIsSelected(true);
		}
		if (getInspectors() != null) {
			getInspectors().update();
		}
	}

	public void removeFromSelectedObjects(DrawingTreeNode<?, ?> aNode) {
		stopEditionOfEditedLabelIfAny();
		if (aNode == null) {
			logger.warning("Cannot remove null object");
			return;
		}
		if (selectedObjects.contains(aNode)) {
			selectedObjects.remove(aNode);
		}
		aNode.setIsSelected(false);
		if (getInspectors() != null) {
			getInspectors().update();
		}
	}

	public void toggleSelection(DrawingTreeNode<?, ?> aNode) {
		// logger.info("BEGIN toggle selection with "+aGraphicalRepresentation+" with selection="+selectedObjects);
		stopEditionOfEditedLabelIfAny();
		if (aNode.getIsSelected()) {
			removeFromSelectedObjects(aNode);
		} else {
			addToSelectedObjects(aNode);
		}
		// logger.info("END toggle selection with "+aGraphicalRepresentation+" with selection="+selectedObjects);
	}

	public void clearSelection() {
		// logger.info("Clear selection");
		stopEditionOfEditedLabelIfAny();
		for (DrawingTreeNode<?, ?> s : selectedObjects) {
			s.setIsSelected(false);
		}
		selectedObjects.clear();
		if (getInspectors() != null) {
			getInspectors().update();
		}
	}

	public List<DrawingTreeNode<?, ?>> getFocusedObjects() {
		return focusedObjects;
	}

	public void setFocusedObjects(List<? extends DrawingTreeNode<?, ?>> someFocusedObjects) {
		if (someFocusedObjects == null) {
			setFocusedObjects(Collections.<DrawingTreeNode<?, ?>> emptyList());
			return;
		}

		if (!focusedObjects.equals(someFocusedObjects)) {
			clearFocusSelection();
			for (DrawingTreeNode<?, ?> d : someFocusedObjects) {
				addToFocusedObjects(d);
			}
		}
	}

	public void setFocusedObject(DrawingTreeNode<?, ?> aNode) {
		if (aNode == null) {
			clearFocusSelection();
			return;
		}

		setFocusedObjects(Collections.singletonList(aNode));
	}

	public void addToFocusedObjects(DrawingTreeNode<?, ?> aNode) {
		if (aNode == null) {
			logger.warning("Cannot add null object");
			return;
		}
		if (!focusedObjects.contains(aNode)) {
			focusedObjects.add(aNode);
			aNode.setIsFocused(true);
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Focusing on " + aNode);
			}
		}
	}

	public void removeFromFocusedObjects(DrawingTreeNode<?, ?> aNode) {
		if (aNode == null) {
			logger.warning("Cannot remove null object");
			return;
		}
		if (focusedObjects.contains(aNode)) {
			focusedObjects.remove(aNode);
		}
		aNode.setIsFocused(false);
	}

	public void toggleFocusSelection(DrawingTreeNode<?, ?> aNode) {
		if (aNode.getIsFocused()) {
			removeFromFocusedObjects(aNode);
		} else {
			addToFocusedObjects(aNode);
		}
	}

	public void clearFocusSelection() {
		// stopEditionOfEditedLabelIfAny();
		for (DrawingTreeNode<?, ?> node : focusedObjects) {
			node.setIsFocused(false);
		}
		focusedObjects.clear();
	}

	public void selectDrawing() {
		stopEditionOfEditedLabelIfAny();
		// Override when required
	}

	public void setEditedLabel(JLabelView<?> aLabel) {
		stopEditionOfEditedLabelIfAny();
		currentlyEditedLabel = aLabel;
	}

	public void resetEditedLabel(JLabelView<?> editedLabel) {
		if (currentlyEditedLabel == editedLabel) {
			currentlyEditedLabel = null;
		}
	}

	public boolean hasEditedLabel() {
		return currentlyEditedLabel != null;
	}

	public JLabelView<?> getEditedLabel() {
		return currentlyEditedLabel;
	}

	public void stopEditionOfEditedLabelIfAny() {
		if (currentlyEditedLabel != null) {
			currentlyEditedLabel.stopEdition();
		}

	}

	public MouseDragControlImpl getCurrentMouseDrag() {
		return currentMouseDrag;
	}

	public void setCurrentMouseDrag(MouseDragControlImpl aMouseDrag) {
		currentMouseDrag = aMouseDrag;
	}

	/**
	 * Implements strategy to preferencially choose a control point or an other during focus retrieving strategy
	 * 
	 * @param cp1
	 * @param cp2
	 * @return
	 */
	public ControlArea<?> preferredFocusedControlArea(ControlArea<?> ca1, ControlArea<?> ca2) {
		if (ca1.getNode().getGraphicalRepresentation().getLayer() == ca2.getNode().getGraphicalRepresentation().getLayer()) {
			// ControlPoint have priority on other ControlArea
			if (ca1 instanceof ConnectorAdjustingControlPoint) {
				return ca1;
			} else if (ca2 instanceof ConnectorAdjustingControlPoint) {
				return ca2;
			}
			if (ca1 instanceof ControlPoint) {
				return ca1;
			} else if (ca2 instanceof ControlPoint) {
				return ca2;
			}
		}
		return ca1.getNode().getGraphicalRepresentation().getLayer() > ca2.getNode().getGraphicalRepresentation().getLayer() ? ca1 : ca2;
	}

	public ControlArea<?> getFocusedControlArea() {
		return focusedControlArea;
	}

	public void _setFocusedControlArea(ControlArea<?> aControlArea) {
		if (focusedControlArea != aControlArea) {
			this.focusedControlArea = aControlArea;
			// getDrawingView().getPaintManager().repaint(getDrawingView());
		}
	}

	public String getToolTipText() {
		if (getFocusedObjects().size() > 0) {
			DrawingTreeNode<?, ?> node = getFocusedObjects().get(0);
			if (node.getGraphicalRepresentation().getToolTipText() != null) {
				// logger.info("getToolTipText() ? return "+gr.getToolTipText());
				return node.getGraphicalRepresentation().getToolTipText();
			}
		}
		// logger.info("getToolTipText() ? return null");
		return null;
	}

	public FGEPoint getLastClickedPoint() {
		return lastClickedPoint;
	}

	public void setLastClickedPoint(FGEPoint lastClickedPoint) {
		this.lastClickedPoint = lastClickedPoint;
	}

	public DrawingTreeNode<?, ?> getLastSelectedNode() {
		return lastSelectedNode;
	}

	public void setLastSelectedGR(DrawingTreeNode<?, ?> lastSelectedGR) {
		this.lastSelectedNode = lastSelectedGR;
	}

	/**
	 * Retrieve stored selection as a persistent list of identified objects
	 */
	private void restoreStoredSelection() {
		if (storedSelection == null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Cannot restore null selection");
			}
			return;
		}
		try {
			for (DrawingTreeNodeIdentifier<?> identifier : storedSelection) {
				DrawingTreeNode<?, ?> node = getDrawing().getDrawingTreeNode(identifier);
				if (node != null) {
					addToSelectedObjects(node);
				}
			}
		} finally {
			storedSelection = null;
		}
	}

	/**
	 * Stores the current selection as a persistent list of identified objects<br>
	 * Guarantee persistence over tree restructurations
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void storeCurrentSelection() {
		if (storedSelection != null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Cannot store selection when there is already a stored selection");
			}
			return;
		}
		storedSelection = new ArrayList<DrawingTreeNodeIdentifier<?>>();
		for (DrawingTreeNode<?, ?> node : getSelectedObjects()) {
			storedSelection.add(new DrawingTreeNodeIdentifier(node.getDrawable(), node.getGRBinding()));
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		super.update(o, arg);
		if (o == getDrawing()) {
			if (arg instanceof DrawingTreeNodeHierarchyRebuildStarted) {
				storeCurrentSelection();
			} else if (arg instanceof DrawingTreeNodeHierarchyRebuildEnded) {
				restoreStoredSelection();
			}
		}
	}

	// Override when required
	public void notifyWillMove(MoveInfo currentMove) {
	}

	// Override when required
	public void notifyHasMoved(MoveInfo currentMove) {
	}

	/**
	 * Process 'UP' key pressed
	 * 
	 * @return boolean indicating if event was successfully processed
	 */
	public boolean upKeyPressed() {
		// System.out.println("Up");
		return getDrawing().isEditable() && keyDrivenMove(0, -1);
	}

	/**
	 * Process 'DOWN' key pressed
	 * 
	 * @return boolean indicating if event was successfully processed
	 */
	public boolean downKeyPressed() {
		// System.out.println("Down");
		return getDrawing().isEditable() && keyDrivenMove(0, 1);
	}

	/**
	 * Process 'LEFT' key pressed
	 * 
	 * @return boolean indicating if event was successfully processed
	 */
	public boolean leftKeyPressed() {
		// System.out.println("Left");
		return getDrawing().isEditable() && keyDrivenMove(-1, 0);
	}

	/**
	 * Process 'RIGHT' key pressed
	 * 
	 * @return boolean indicating if event was successfully processed
	 */
	public boolean rightKeyPressed() {
		// System.out.println("Right");
		return getDrawing().isEditable() && keyDrivenMove(1, 0);
	}

	private synchronized boolean keyDrivenMove(int deltaX, int deltaY) {
		if (keyDrivenMovingSessionTimer == null && getFirstSelectedShape() != null) {
			// System.out.println("BEGIN to move with keyboard");
			if (startKeyDrivenMovingSession()) {
				doMoveInSession(deltaX, deltaY);
				return true;
			}
			return false;
		} else if (keyDrivenMovingSessionTimer != null) {
			doMoveInSession(deltaX, deltaY);
			return true;
		}
		return false;
	}

	public void doMoveInSession(int deltaX, int deltaY) {
		keyDrivenMovingSessionTimer.typed();
		Point newLocation = keyDrivenMovingSession.getCurrentLocationInDrawingView();
		newLocation.x += deltaX;
		newLocation.y += deltaY;
		keyDrivenMovingSession.moveTo(newLocation);
	}

	private synchronized boolean startKeyDrivenMovingSession() {

		if (getFirstSelectedShape().getGraphicalRepresentation().getLocationConstraints() != LocationConstraints.UNMOVABLE) {

			keyDrivenMovingSessionTimer = new KeyDrivenMovingSessionTimer();
			keyDrivenMovingSessionTimer.start();
			ShapeNode<?> movedObject = getFirstSelectedShape();
			keyDrivenMovingSession = new MoveInfo(movedObject, this);
			notifyWillMove(keyDrivenMovingSession);
			return true;
		} else {
			return false;
		}
	}

	private synchronized void stopKeyDrivenMovingSession() {
		keyDrivenMovingSessionTimer = null;
		notifyHasMoved(keyDrivenMovingSession);
		keyDrivenMovingSession = null;
	}

	private class KeyDrivenMovingSessionTimer extends Thread {
		volatile boolean typed = false;

		public KeyDrivenMovingSessionTimer() {
			typed = true;
		}

		@Override
		public void run() {
			while (typed) {
				typed = false;
				try {
					sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					stopKeyDrivenMovingSession();
				}
			});
		}

		public synchronized void typed() {
			typed = true;
			// System.out.println("Tiens on retape sur le clavier");
		}
	}
}
