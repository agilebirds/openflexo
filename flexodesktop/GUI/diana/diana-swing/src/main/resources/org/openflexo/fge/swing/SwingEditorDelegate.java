package org.openflexo.fge.swing;

import java.awt.Point;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.openflexo.fge.Drawing;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.ShapeGraphicalRepresentation.LocationConstraints;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.DianaEditorDelegate;
import org.openflexo.fge.control.DianaInteractiveViewer;
import org.openflexo.fge.swing.actions.MoveInfo;
import org.openflexo.fge.swing.paint.FGEPaintManager;
import org.openflexo.fge.swing.view.JDrawingView;

public class SwingEditorDelegate implements DianaEditorDelegate {

	private AbstractDianaEditor<?, SwingFactory, JComponent> controller;

	private MoveInfo keyDrivenMovingSession;
	private KeyDrivenMovingSessionTimer keyDrivenMovingSessionTimer = null;

	public SwingEditorDelegate(AbstractDianaEditor<?, SwingFactory, JComponent> controller) {
		super();
		this.controller = controller;
	}

	public AbstractDianaEditor<?, SwingFactory, JComponent> getController() {
		return controller;
	}

	public Drawing<?> getDrawing() {
		return controller.getDrawing();
	}

	public JDrawingView<?> getDrawingView() {
		return (JDrawingView<?>) controller.getDrawingView();
	}

	public FGEPaintManager getPaintManager() {
		if (getDrawingView() != null) {
			return getDrawingView().getPaintManager();
		}
		return null;
	}

	public void objectStartMoving(DrawingTreeNode<?, ?> node) {
		if (getPaintManager().isPaintingCacheEnabled()) {
			getPaintManager().addToTemporaryObjects(node);
			getPaintManager().invalidate(node);
		}

	}

	public void objectStopMoving(DrawingTreeNode<?, ?> node) {
		if (getPaintManager().isPaintingCacheEnabled()) {
			getPaintManager().removeFromTemporaryObjects(node);
			getPaintManager().invalidate(node);
			getPaintManager().repaint(getDrawingView());
		}
	}

	@Override
	public void objectsStartMoving(Set<? extends DrawingTreeNode<?, ?>> nodes) {
		if (getPaintManager().isPaintingCacheEnabled()) {
			for (DrawingTreeNode<?, ?> node : nodes) {
				getPaintManager().addToTemporaryObjects(node);
				getPaintManager().invalidate(node);
			}
		}
	}

	@Override
	public void objectsStopMoving(Set<? extends DrawingTreeNode<?, ?>> nodes) {
		if (getPaintManager().isPaintingCacheEnabled()) {
			for (DrawingTreeNode<?, ?> node : nodes) {
				getPaintManager().removeFromTemporaryObjects(node);
				getPaintManager().invalidate(node);
			}
			getPaintManager().repaint(getDrawingView());
		}
	}

	public void focusedObjectChanged(DrawingTreeNode<?, ?> oldFocusedObject, DrawingTreeNode<?, ?> newFocusedObject) {
		if (getPaintManager().isPaintingCacheEnabled()) {
			// Just repaint old and eventual new focused object
			if (oldFocusedObject != null) {
				getPaintManager().repaint(oldFocusedObject);
			}
			if (newFocusedObject != null) {
				getPaintManager().repaint(newFocusedObject);
			}
		} else {
			// @brutal mode
			getPaintManager().repaint(getDrawingView());
		}

	}

	public void repaintAll() {
		getPaintManager().repaint(getDrawingView());
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

	private ShapeNode<?> getMovableShape() {
		if (getController() instanceof DianaInteractiveViewer) {
			return ((DianaInteractiveViewer<?, ?, ?>) getController()).getFirstSelectedShape();
		}
		return null;
	}

	private synchronized boolean keyDrivenMove(int deltaX, int deltaY) {
		if (keyDrivenMovingSessionTimer == null && getMovableShape() != null) {
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

		if (getMovableShape() != null
				&& getMovableShape().getGraphicalRepresentation().getLocationConstraints() != LocationConstraints.UNMOVABLE) {

			keyDrivenMovingSessionTimer = new KeyDrivenMovingSessionTimer();
			keyDrivenMovingSessionTimer.start();
			ShapeNode<?> movedObject = getMovableShape();
			keyDrivenMovingSession = new MoveInfo(movedObject, (DianaInteractiveViewer<?, ?, ?>) getController());
			// notifyWillMove(keyDrivenMovingSession);
			objectsStartMoving(keyDrivenMovingSession.getMovedObjects());
			return true;
		} else {
			return false;
		}
	}

	private synchronized void stopKeyDrivenMovingSession() {
		keyDrivenMovingSessionTimer = null;
		objectsStopMoving(keyDrivenMovingSession.getMovedObjects());
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
