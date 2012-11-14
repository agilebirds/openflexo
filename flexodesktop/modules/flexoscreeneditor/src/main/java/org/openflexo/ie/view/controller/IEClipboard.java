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
package org.openflexo.ie.view.controller;

import java.awt.Point;
import java.io.File;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JMenuItem;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.ie.widget.IETDWidget;
import org.openflexo.foundation.ie.widget.IEWidget;
import org.openflexo.ie.view.IEContainer;
import org.openflexo.ie.view.controller.dnd.IEDTListener;
import org.openflexo.selection.FlexoClipboard;
import org.openflexo.selection.PastingGraphicalContext;

/**
 * IEClipboard is intented to be the object working with the IESelectionManager and storing copied, cutted and pasted objects. Handled
 * objects are instances implementing {@link org.openflexo.selection.SelectableView}.
 * 
 * @author sguerin
 */
public class IEClipboard extends FlexoClipboard {

	private static final Logger logger = Logger.getLogger(IEClipboard.class.getPackage().getName());

	private IEWidget _clipboardData;

	private IEController _ieController;

	public IEClipboard(IESelectionManager aSelectionManager, JMenuItem copyMenuItem, JMenuItem pasteMenuItem, JMenuItem cutMenuItem) {
		super(aSelectionManager, copyMenuItem, pasteMenuItem, cutMenuItem);
		_ieController = aSelectionManager.getIEController();
	}

	/**
	 * Overrides hasCopiedData
	 * 
	 * @see org.openflexo.selection.FlexoClipboard#hasCopiedData()
	 */
	@Override
	public boolean hasCopiedData() {
		return super.hasCopiedData() && !_clipboardData.isDeleted();
	}

	@Override
	protected void performSelectionPaste(FlexoModelObject pastingContext, PastingGraphicalContext graphicalContext) {
		JComponent container = graphicalContext.targetContainer;
		Point location = graphicalContext.pastingLocation;

		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Pasting " + _clipboardData + " in container " + container);
		}
		if (isTargetValidForPasting(container)) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Target is valid");
			}
			IEContainer targetContainer = (IEContainer) container;
			if (!IEDTListener.isValidDropTargetContainer(targetContainer.getContainerModel(), _clipboardData)) {
				JComponent subTarget = (JComponent) container.getComponentAt(location);
				if (subTarget instanceof IEContainer
						&& IEDTListener.isValidDropTargetContainer(((IEContainer) subTarget).getContainerModel(), _clipboardData)) {
					targetContainer = (IEContainer) subTarget;
				} else {
					return;
				}
			}
			// Now we create the object to insert in the dropContainer.
			IEWidget targetModel = targetContainer.getContainerModel();
			if (targetModel instanceof IETDWidget) {
				targetModel = ((IETDWidget) targetModel).getSequenceWidget();
			}
			IEWidget newWidget = IEDTListener.createModelFromMovedWidget(_clipboardData, targetModel, false);
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Widget has been created");
			}
			// IEWidgetView newView =
			// IEDTListener.createView(_ieController,newWidget,targetModel);
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("View has been created");
			}

			if (newWidget != null) {
				IEDTListener.insertView(newWidget, targetContainer, location);
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("View has been inserted");
					// _ieController.getIESelectionManager().processMouseClicked(newView,newView.getCenter(),1,false);
				}

			} else {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Could not create view for model " + newWidget);
				}
			}
		} else {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Target is NOT valid");
			}
		}
	}

	@Override
	protected boolean isCurrentSelectionValidForCopy(Vector currentlySelectedObjects) {
		if (currentlySelectedObjects == null || currentlySelectedObjects.size() == 0) {
			return false;
		}
		return true;
	}

	@Override
	protected boolean performCopyOfSelection(Vector currentlySelectedObjects) {
		if (currentlySelectedObjects.elementAt(0) instanceof IEWidget) {
			if (currentlySelectedObjects.elementAt(0) instanceof IETDWidget) {
				_clipboardData = ((IETDWidget) currentlySelectedObjects.elementAt(0)).getSequenceWidget();
			} else {
				IEWidget copiedWidget = (IEWidget) currentlySelectedObjects.elementAt(0);
				_clipboardData = copiedWidget;
			}
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Copying " + _clipboardData);
			}
			return true;
		}
		return false;
	}

	protected boolean isTargetValidForPasting(JComponent targetContainer) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("isTargetValidForPasting(): container is a " + targetContainer.getClass().getName());
		}
		return targetContainer instanceof IEContainer;
	}

	protected void exportClipboardToPalette(File paletteDirectory, String newPaletteElementName) {
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Sorry, exporting clipboard to IE palette not implemented.");
		}
	}
}
