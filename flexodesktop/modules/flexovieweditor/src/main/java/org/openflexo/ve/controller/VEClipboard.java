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
package org.openflexo.ve.controller;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JMenuItem;

import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.view.diagram.model.DiagramShape;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.selection.FlexoClipboard;
import org.openflexo.selection.PastingGraphicalContext;
import org.openflexo.view.controller.FlexoController;

/**
 * VEClipboard is intented to be the object working with the VESelectionManager and storing copied, cut and pasted objects.
 * 
 * @author sylvain
 */
public class VEClipboard extends FlexoClipboard {

	private static final Logger logger = Logger.getLogger(VEClipboard.class.getPackage().getName());

	protected VESelectionManager _veSelectionManager;

	private FlexoModelObject _clipboardData;

	public VEClipboard(VESelectionManager aSelectionManager, JMenuItem copyMenuItem, JMenuItem pasteMenuItem, JMenuItem cutMenuItem) {
		super(aSelectionManager, copyMenuItem, pasteMenuItem, cutMenuItem);
		_veSelectionManager = aSelectionManager;
		resetClipboard();
	}

	public VESelectionManager getSelectionManager() {
		return _veSelectionManager;
	}

	public VEController getVEController() {
		return getSelectionManager().getVEController();
	}

	/*	@Override
		public boolean performSelectionPaste() {
			if (_isPasteEnabled) {
				return super.performSelectionPaste();
			} else {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Sorry, PASTE disabled");
				}
				return false;
			}
		}*/

	@Override
	protected void performSelectionPaste(FlexoObject pastingContext, PastingGraphicalContext graphicalContext) {
		System.out.println("Pasting context = " + pastingContext);
		System.out.println("graphicalContext = " + graphicalContext);
		JComponent targetContainer = graphicalContext.targetContainer;
		System.out.println("targetContainer = " + targetContainer);
		System.out.println("pastingLocation=" + graphicalContext.pastingLocation);
		System.out.println("precisePastingLocation=" + graphicalContext.precisePastingLocation);
		if (isTargetValidForPasting(pastingContext)) {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Pasting " + _clipboardData + " in " + pastingContext);
				// Handle paste here
				DiagramShape newShape = (DiagramShape) _clipboardData.cloneUsingXMLMapping();
				((ShapeGraphicalRepresentation) newShape.getGraphicalRepresentation()).setLocation(new FGEPoint(
						graphicalContext.precisePastingLocation));
				((DiagramShape) pastingContext).addToChilds(newShape);

			}
		} else {
			FlexoController.notify(FlexoLocalization.localizedForKey("cannot_paste_at_this_place"));
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Paste is NOT legal");
			}
		}
	}

	@Override
	protected boolean isCurrentSelectionValidForCopy(Vector currentlySelectedObjects) {
		return getSelectionManager().getSelectionSize() > 0;
	}

	protected void resetClipboard() {
		_clipboardData = null;
	}

	/**
	 * Selection procedure for copy
	 */
	@Override
	protected boolean performCopyOfSelection(Vector<? extends FlexoObject> currentlySelectedObjects) {
		resetClipboard();
		if (currentlySelectedObjects.size() > 0) {
			FlexoObject obj = currentlySelectedObjects.get(0);
			if (obj instanceof FlexoModelObject) {
				FlexoModelObject o = (FlexoModelObject) obj;
				System.out.println("Copy for " + o + " XML=" + o.getXMLRepresentation());
				_clipboardData = (FlexoModelObject) o.cloneUsingXMLMapping();
				System.out.println("Copied data : " + _clipboardData + "XML=" + _clipboardData.getXMLRepresentation());
			}
		}

		return true;
	}

	protected boolean isTargetValidForPasting(FlexoObject pastingContext) {
		return _clipboardData instanceof DiagramShape && pastingContext instanceof DiagramShape;
	}
}
