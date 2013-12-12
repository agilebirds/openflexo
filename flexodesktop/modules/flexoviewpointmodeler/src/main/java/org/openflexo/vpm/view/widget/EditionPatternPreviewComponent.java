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
package org.openflexo.vpm.view.widget;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.controller.FIBSelectable;
import org.openflexo.fib.model.FIBCustom;
import org.openflexo.fib.model.FIBCustom.FIBCustomComponent;
import org.openflexo.foundation.view.diagram.viewpoint.GraphicalElementPatternRole;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.selection.SelectionManager;
import org.openflexo.swing.CustomPopup.ApplyCancelListener;

// TODO: this should inherit from DefaultFIBCustomComponent
public class EditionPatternPreviewComponent extends JPanel implements FIBCustomComponent<EditionPattern, JPanel>,
		FIBSelectable<GraphicalElementPatternRole<?>> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(EditionPatternPreviewComponent.class.getPackage().getName());

	private EditionPattern editionPattern;

	private SelectionManager selectionManager;
	private EditionPatternPreviewController previewController;
	private final Vector<ApplyCancelListener> applyCancelListener;

	private final JLabel EMPTY_LABEL = new JLabel("<empty>");

	public EditionPatternPreviewComponent() {
		super();
		setLayout(new BorderLayout());
		// add(EMPTY_LABEL,BorderLayout.CENTER);
		applyCancelListener = new Vector<ApplyCancelListener>();
	}

	@Override
	public void delete() {
		if (previewController != null) {
			previewController.delete();
		}
	}

	public EditionPatternPreviewComponent(EditionPattern editionPattern) {
		this();
		setEditedObject(editionPattern);
	}

	@Override
	public void init(FIBCustom component, FIBController controller) {
	}

	@Override
	public EditionPattern getEditedObject() {
		return editionPattern;
	}

	@Override
	public void setEditedObject(EditionPattern object) {
		if (object != editionPattern) {
			logger.fine("EditionPatternPreview: setEditedObject: " + object);
			editionPattern = object;
			if (previewController != null && object != null) {
				if (previewController.getDrawingView() != null) {
					remove(previewController.getDrawingView());
				}
				previewController.delete();
				previewController = null;
			}
			if (object != null) {
				previewController = new EditionPatternPreviewController(object, selectionManager);
				add(previewController.getDrawingView(), BorderLayout.CENTER);
			}
			revalidate();
			repaint();
		}
	}

	public SelectionManager getSelectionManager() {
		return selectionManager;
	}

	public void setSelectionManager(SelectionManager selectionManager) {
		this.selectionManager = selectionManager;
		if (previewController != null) {
			previewController.setSelectionManager(selectionManager);
		}
	}

	@Override
	public JPanel getJComponent() {
		return this;
	}

	@Override
	public Class<EditionPattern> getRepresentedType() {
		return EditionPattern.class;
	}

	@Override
	public EditionPattern getRevertValue() {
		return editionPattern;
	}

	@Override
	public void setRevertValue(EditionPattern object) {
	}

	@Override
	public void addApplyCancelListener(ApplyCancelListener l) {
		applyCancelListener.add(l);
	}

	@Override
	public void removeApplyCancelListener(ApplyCancelListener l) {
		applyCancelListener.remove(l);
	}

	@Override
	public GraphicalElementPatternRole<?> getSelected() {
		if (previewController != null) {
			if (previewController.getSelectedObjects() == null) {
				return null;
			}
			if (previewController.getSelectedObjects().size() > 0) {
				return (GraphicalElementPatternRole<?>) previewController.getSelectedObjects().get(0).getDrawable();
			}
			return null;
		}
		return null;
	}

	@Override
	public List<GraphicalElementPatternRole<?>> getSelection() {
		if (previewController != null) {
			if (previewController.getSelectedObjects() == null) {
				return null;
			}
		}
		if (previewController.getSelectedObjects().size() > 0) {
			List<GraphicalElementPatternRole<?>> returned = new ArrayList<GraphicalElementPatternRole<?>>();
			for (DrawingTreeNode<?, ?> dtn : previewController.getSelectedObjects()) {
				returned.add((GraphicalElementPatternRole<?>) dtn.getDrawable());
			}
			return returned;
		}
		return null;
	}

	@Override
	public boolean mayRepresent(GraphicalElementPatternRole<?> o) {
		return o instanceof PatternRole && ((PatternRole) o).getEditionPattern() == editionPattern;
	}

	@Override
	public void objectAddedToSelection(GraphicalElementPatternRole<?> o) {
		if (previewController != null) {
			previewController.fireObjectSelected(o);
		}
	}

	@Override
	public void objectRemovedFromSelection(GraphicalElementPatternRole<?> o) {
		if (previewController != null) {
			previewController.fireObjectDeselected(o);
		}
	}

	@Override
	public void selectionResetted() {
		if (previewController != null) {
			previewController.fireResetSelection();
		}
	}

	@Override
	public void addToSelection(GraphicalElementPatternRole<?> o) {
		if (previewController != null) {
			previewController.addToSelectedObjects(previewController.getDrawing().getDrawingTreeNode(o));
		}
	}

	@Override
	public void removeFromSelection(GraphicalElementPatternRole<?> o) {
		if (previewController != null) {
			previewController.removeFromSelectedObjects(previewController.getDrawing().getDrawingTreeNode(o));
		}
	}

	@Override
	public void resetSelection() {
		if (previewController != null) {
			previewController.clearSelection();
		}
	}

	@Override
	public boolean synchronizedWithSelection() {
		return true;
	}

}
