package org.openflexo.fib.editor.controller;

import java.awt.Point;
import java.util.logging.Logger;

import org.openflexo.fib.editor.view.FIBEditableViewDelegate.FIBDropTarget;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.FIBContainer;
import org.openflexo.logging.FlexoLogger;

public class DraggedFIBComponent implements FIBDraggable {

	private static final Logger logger = FlexoLogger.getLogger(FIBEditorController.class.getPackage().getName());

	private FIBComponent draggedComponent;

	public DraggedFIBComponent(FIBComponent draggedComponent) {
		this.draggedComponent = draggedComponent;
	}

	@Override
	public void enableDragging() {
		System.out.println("Enable dragging for " + draggedComponent);
	}

	@Override
	public void disableDragging() {
		System.out.println("Disable dragging for " + draggedComponent);
	}

	@Override
	public boolean acceptDragging(FIBDropTarget target) {
		// System.out.println("acceptDragging ? for component: " + target.getFIBComponent() + " place holder: " + target.getPlaceHolder());
		return true;
	}

	@Override
	public boolean elementDragged(FIBDropTarget target, Point pt) {
		logger.info("Nous y voila: element dragged with component: " + target.getFIBComponent() + " place holder: "
				+ target.getPlaceHolder());

		/*if (target.getPlaceHolder() == null) {
			boolean deleteIt = JOptionPane.showConfirmDialog(_palette.getEditorController().getEditor().getFrame(),
					target.getFIBComponent() + ": really delete this component (undoable operation) ?", "information",
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE) == JOptionPane.YES_OPTION;
			if (!deleteIt) {
				return false;
			}
		}*/

		try {
			if (target.getPlaceHolder() != null) {
				target.getPlaceHolder().willDelete();
				FIBContainer oldParent = draggedComponent.getParent();
				draggedComponent.getParent().removeFromSubComponentsNoNotification(draggedComponent);
				target.getPlaceHolder().insertComponent(draggedComponent);
				target.getPlaceHolder().hasDeleted();
				oldParent.notifyComponentMoved(draggedComponent);
				return true;
			}

			/*else {
				FIBComponent targetComponent = target.getFIBComponent();
				FIBContainer containerComponent = targetComponent.getParent();

				if (containerComponent == null)
					return false;

				if (targetComponent instanceof FIBTab && !(newComponent instanceof FIBPanel))
					return false;

				if (targetComponent.getParent() instanceof FIBTabPanel && newComponent instanceof FIBPanel) {
					// Special case where a new tab is added to a FIBTabPanel
					FIBTab newTabComponent = new FIBTab();
					newTabComponent.setTitle("NewTab");
					newTabComponent.setIndex(((FIBTabPanel) targetComponent.getParent()).getSubComponents().size());
					((FIBTabPanel) targetComponent.getParent()).addToSubComponents(newTabComponent);
					return true;
				} else {
					// Normal case, we replace targetComponent by newComponent
					ComponentConstraints constraints = targetComponent.getConstraints();
					containerComponent.removeFromSubComponentsNoNotification(targetComponent);
					// No notification, we will do it later, to avoid reindexing
					targetComponent.delete();
					containerComponent.addToSubComponents(newComponent, constraints);
					return true;
				}
			}*/

			return false;
		} catch (Exception e) {
			e.printStackTrace();
			logger.warning("Unexpected exception: " + e);
			return false;
		}

	}
}
