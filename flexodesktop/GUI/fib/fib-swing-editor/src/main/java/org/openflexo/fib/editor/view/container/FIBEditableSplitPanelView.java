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
package org.openflexo.fib.editor.view.container;

import java.beans.PropertyChangeEvent;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.fib.editor.controller.FIBEditorController;
import org.openflexo.fib.editor.view.FIBEditableView;
import org.openflexo.fib.editor.view.FIBEditableViewDelegate;
import org.openflexo.fib.editor.view.FIBEditableViewDelegate.FIBDropTarget;
import org.openflexo.fib.editor.view.PlaceHolder;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.FIBModelObject;
import org.openflexo.fib.model.FIBMultiSplitLayoutFactory.FIBNode;
import org.openflexo.fib.model.FIBMultiSplitLayoutFactory.FIBSplit;
import org.openflexo.fib.model.FIBSplitPanel;
import org.openflexo.fib.model.SplitLayoutConstraints;
import org.openflexo.fib.view.container.FIBSplitPanelView;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.swing.layout.JXMultiSplitPane;
import org.openflexo.swing.layout.MultiSplitLayout.Leaf;
import org.openflexo.swing.layout.MultiSplitLayout.Node;
import org.openflexo.swing.layout.MultiSplitLayout.Split;

public class FIBEditableSplitPanelView<T> extends FIBSplitPanelView<T> implements FIBEditableView<FIBSplitPanel, JXMultiSplitPane> {

	private static final Logger logger = FlexoLogger.getLogger(FIBEditableSplitPanelView.class.getPackage().getName());

	private final FIBEditableViewDelegate<FIBSplitPanel, JXMultiSplitPane> delegate;

	private Vector<PlaceHolder> placeholders;

	private final FIBEditorController editorController;

	@Override
	public FIBEditorController getEditorController() {
		return editorController;
	}

	public FIBEditableSplitPanelView(FIBSplitPanel model, FIBEditorController editorController) {
		super(model, editorController.getController());
		this.editorController = editorController;

		delegate = new FIBEditableViewDelegate<FIBSplitPanel, JXMultiSplitPane>(this);
		model.getPropertyChangeSupport().addPropertyChangeListener(this);
	}

	@Override
	public void delete() {
		placeholders.clear();
		placeholders = null;
		delegate.delete();
		getComponent().getPropertyChangeSupport().removePropertyChangeListener(this);
		super.delete();
	}

	private void appendPlaceHolder(final Leaf n) {
		boolean found = false;
		for (FIBComponent subComponent : getComponent().getSubComponents()) {
			if (n.getName().equals(((SplitLayoutConstraints) subComponent.getConstraints()).getSplitIdentifier())) {
				found = true;
			}
		}
		if (!found) {

			final SplitLayoutConstraints splitLayoutConstraints = SplitLayoutConstraints.makeSplitLayoutConstraints(n.getName());
			PlaceHolder newPlaceHolder = new PlaceHolder(this, "<" + n.getName() + ">") {
				@Override
				public void insertComponent(FIBComponent newComponent) {
					System.out.println("getComponent=" + FIBEditableSplitPanelView.this.getComponent());
					FIBEditableSplitPanelView.this.getComponent().addToSubComponents(newComponent, splitLayoutConstraints);
				}
			};
			registerComponentWithConstraints(newPlaceHolder, n.getName());
			newPlaceHolder.setVisible(false);
			placeholders.add(newPlaceHolder);
			// logger.info("Made placeholder for " + n.getName());
		}

	}

	private void appendPlaceHolders(FIBSplit<?> s) {
		for (FIBNode n : s.getChildren()) {
			appendPlaceHolders(n);
		}
	}

	private void appendPlaceHolders(Node n) {
		if (n instanceof Split) {
			appendPlaceHolders((Split) n);
		} else if (n instanceof Leaf) {
			appendPlaceHolder((Leaf) n);
		}
	}

	@Override
	protected void retrieveContainedJComponentsAndConstraints() {
		if (placeholders == null) {
			placeholders = new Vector<PlaceHolder>();
		}
		placeholders.removeAllElements();

		super.retrieveContainedJComponentsAndConstraints();

		appendPlaceHolders(getLayout().getModel());

		// logger.info("******** Set DropTargets");
		if (getEditorController() != null) {
			for (PlaceHolder ph : placeholders) {
				logger.fine("Set DropTarget for " + ph);
				// Put right drop target
				new FIBDropTarget(ph);
			}
		}
		/*else {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						updateLayout();
					}
				});
			}*/
	}

	@Override
	public Vector<PlaceHolder> getPlaceHolders() {
		return placeholders;
	}

	@Override
	public FIBEditableViewDelegate<FIBSplitPanel, JXMultiSplitPane> getDelegate() {
		return delegate;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		delegate.receivedModelNotifications((FIBModelObject) evt.getSource(), evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
	}

}
