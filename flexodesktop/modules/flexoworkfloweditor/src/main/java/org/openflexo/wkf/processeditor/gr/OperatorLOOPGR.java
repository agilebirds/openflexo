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
package org.openflexo.wkf.processeditor.gr;

import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.controller.CustomClickControlAction;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.controller.MouseClickControl;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.wkf.action.OpenLoopedPetriGraph;
import org.openflexo.foundation.wkf.dm.GroupInserted;
import org.openflexo.foundation.wkf.dm.GroupRemoved;
import org.openflexo.foundation.wkf.dm.PetriGraphHasBeenClosed;
import org.openflexo.foundation.wkf.dm.PetriGraphHasBeenOpened;
import org.openflexo.foundation.wkf.dm.PetriGraphSet;
import org.openflexo.foundation.wkf.node.LOOPOperator;
import org.openflexo.icon.WKFIconLibrary;
import org.openflexo.wkf.processeditor.ProcessRepresentation;

public class OperatorLOOPGR extends OperatorGR<LOOPOperator> {

	static final Logger logger = Logger.getLogger(OperatorLOOPGR.class.getPackage().getName());

	public OperatorLOOPGR(LOOPOperator operatorNode, ProcessRepresentation aDrawing, boolean isInPalet) {
		super(operatorNode, aDrawing, isInPalet);
		addToMouseClickControls(new LoopedPetriGraphOpener(), true);
		if (getDrawable().hasExecutionPetriGraph()) {
			getDrawable().getExecutionPetriGraph().addObserver(this);
		}
	}

	@Override
	public void delete() {
		if (getDrawable().hasExecutionPetriGraph()) {
			getDrawable().getExecutionPetriGraph().deleteObserver(this);
		}
		super.delete();
	}

	@Override
	public ImageIcon getImageIcon() {
		return WKFIconLibrary.LOOP_OPERATOR_ICON;
	}

	public class LoopedPetriGraphOpener extends MouseClickControl {

		public LoopedPetriGraphOpener() {
			super("LoopedPetriGraphOpener", MouseButton.LEFT, 2, new CustomClickControlAction() {
				@Override
				public boolean handleClick(GraphicalRepresentation graphicalRepresentation, DrawingController controller,
						java.awt.event.MouseEvent event) {
					logger.info("Opening Execution petri graph by double-clicking");
					OpenLoopedPetriGraph.actionType.makeNewAction(getOperatorNode(), null, getDrawing().getEditor()).doAction();
					return true;
				}
			}, false, false, false, false);
		}

	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (observable == getDrawable()) {
			if (dataModification instanceof PetriGraphHasBeenOpened || dataModification instanceof PetriGraphHasBeenClosed) {
				getDrawing().updateGraphicalObjectsHierarchy();
			} else if (dataModification instanceof PetriGraphSet) {
				if (getDrawable().hasExecutionPetriGraph()) {
					getDrawable().getExecutionPetriGraph().addObserver(this);
				}
			}
		}
		if (getDrawable().hasExecutionPetriGraph()) {
			if (observable == getDrawable().getExecutionPetriGraph()) {
				if (dataModification instanceof GroupInserted || dataModification instanceof GroupRemoved) {
					getDrawing().invalidateGraphicalObjectsHierarchy(getDrawable().getProcess());
					getDrawing().updateGraphicalObjectsHierarchy();
				}
			}
		}
		super.update(observable, dataModification);
	}

}
