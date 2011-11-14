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
package org.openflexo.wkf.processeditor;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.controller.DrawingPalette;
import org.openflexo.fge.controller.PaletteElement;
import org.openflexo.fge.controller.PaletteElement.PaletteElementGraphicalRepresentation;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.foundation.wkf.ActivityGroup;
import org.openflexo.foundation.wkf.FlexoPetriGraph;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.WKFArtefact;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.action.AddPort;
import org.openflexo.foundation.wkf.action.DropWKFElement;
import org.openflexo.foundation.wkf.node.AbstractActivityNode;
import org.openflexo.foundation.wkf.node.EventNode;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.foundation.wkf.node.SelfExecutableNode;
import org.openflexo.foundation.wkf.ws.DeletePort;
import org.openflexo.foundation.wkf.ws.FlexoPort;
import org.openflexo.foundation.wkf.ws.InOutPort;
import org.openflexo.foundation.wkf.ws.InPort;
import org.openflexo.foundation.wkf.ws.NewPort;
import org.openflexo.foundation.wkf.ws.OutPort;
import org.openflexo.foundation.wkf.ws.PortRegistery;

public abstract class AbstractWKFPalette extends DrawingPalette {

	private static final Logger logger = Logger.getLogger(AbstractWKFPalette.class.getPackage().getName());

	public AbstractWKFPalette(String title) {
		super(300, 230, title);
	}

	public AbstractWKFPalette(int width, int height, String title) {
		super(width, height, title);
	}

	public class WKFPaletteElement implements PaletteElement {
		protected WKFObject object;
		protected ContainerValidity containerValidity;
		private PaletteElementGraphicalRepresentation gr;

		public WKFPaletteElement(PaletteElementGraphicalRepresentation gr, WKFObject object, ContainerValidity containerValidity) {
			this.gr = gr;
			this.object = object;
			this.containerValidity = containerValidity;
		}

		@Override
		public boolean acceptDragging(GraphicalRepresentation gr) {
			if (gr.getDrawable() instanceof WKFObject) {
				return containerValidity.isContainerValid((WKFObject) gr.getDrawable());
			}
			return false;
		}

		@Override
		public boolean elementDragged(GraphicalRepresentation gr, FGEPoint dropLocation) {
			FlexoPetriGraph container = null;
			logger.info("Dropping new object " + object + " for " + gr.getDrawable() + " container=" + container);
			if (object instanceof FlexoPort) {
				if (gr.getDrawable() instanceof PortRegistery) {
					AddPort action = null;
					if (object instanceof NewPort) {
						action = AddPort.createNewPort.makeNewAction((PortRegistery) gr.getDrawable(), null, getController().getEditor());
					} else if (object instanceof DeletePort) {
						action = AddPort.createDeletePort
								.makeNewAction((PortRegistery) gr.getDrawable(), null, getController().getEditor());
					} else if (object instanceof InPort) {
						action = AddPort.createInPort.makeNewAction((PortRegistery) gr.getDrawable(), null, getController().getEditor());
					} else if (object instanceof OutPort) {
						action = AddPort.createOutPort.makeNewAction((PortRegistery) gr.getDrawable(), null, getController().getEditor());
					} else if (object instanceof InOutPort) {
						action = AddPort.createInOutPort.makeNewAction((PortRegistery) gr.getDrawable(), null, getController().getEditor());
					}
					action.setNewPortName(((FlexoPort) object).getDefaultName());

					action.setLocation((dropLocation.x), (dropLocation.y));
					action.setEditNodeLabel(true);
					action.setGraphicalContext(ProcessEditorConstants.BASIC_PROCESS_EDITOR);
					action.doAction();
					return action.hasActionExecutionSucceeded();
				} else {
					logger.warning("Unexpected container");
					return false;
				}
			} else if (gr.getDrawable() instanceof AbstractActivityNode && (object instanceof EventNode)) {
				container = ((AbstractActivityNode) gr.getDrawable()).getProcess().getActivityPetriGraph();
				((EventNode) object).setBoundaryOf((AbstractActivityNode) gr.getDrawable());
				DropWKFElement action = createAndExecuteDropElementAction(dropLocation, container, null, true);
				((EventNode) object).setBoundaryOf(null);
				return action.hasActionExecutionSucceeded();
			} else {
				if (gr.getDrawable() instanceof FlexoProcess) {
					container = ((FlexoProcess) gr.getDrawable()).getActivityPetriGraph();
				}
				if (gr.getDrawable() instanceof AbstractActivityNode) {
					container = ((AbstractActivityNode) gr.getDrawable()).getOperationPetriGraph();
				}
				if (gr.getDrawable() instanceof OperationNode) {
					container = ((OperationNode) gr.getDrawable()).getActionPetriGraph();
				}
				if (gr.getDrawable() instanceof SelfExecutableNode) {
					container = ((SelfExecutableNode) gr.getDrawable()).getExecutionPetriGraph();
				}
				if (gr.getDrawable() instanceof FlexoPetriGraph) {
					container = (FlexoPetriGraph) gr.getDrawable();
				}
				if (gr.getDrawable() instanceof ActivityGroup) {
					container = ((ActivityGroup) gr.getDrawable()).getParentPetriGraph();
				}
				if (gr.getDrawable() instanceof WKFArtefact) {
					container = ((WKFArtefact) gr.getDrawable()).getParentPetriGraph();
					dropLocation.x += ((ShapeGraphicalRepresentation<?>) gr).getX();
					dropLocation.y += ((ShapeGraphicalRepresentation<?>) gr).getY();
				}
				if (container == null) {
					logger.warning("Unexpected container");
					return false;
				}
				ActivityGroup group = null;
				if (gr.getDrawable() instanceof ActivityGroup) {
					group = (ActivityGroup) gr.getDrawable();
				}
				DropWKFElement action = createAndExecuteDropElementAction(dropLocation, container, group, true);
				return action.hasActionExecutionSucceeded();
			}
		}

		public DropWKFElement createAndExecuteDropElementAction(FGEPoint dropLocation, FlexoPetriGraph container, ActivityGroup group,
				boolean handlePaletteOffset) {
			DropWKFElement action = DropWKFElement.actionType.makeNewAction(container, null, getController().getEditor());
			action.setHandlePaletteOffset(handlePaletteOffset);
			action.setObject((WKFObject) object.cloneUsingXMLMapping(container.getProcess().instanciateNewBuilder(), true, container
					.getProcess().getXMLMapping()));
			action.setLocation(dropLocation.x, dropLocation.y);
			action.setResetNodeName(true);
			action.setEditNodeLabel(true);
			action.setGraphicalContext(ProcessEditorConstants.BASIC_PROCESS_EDITOR);
			action.setGroup(group);
			action.doAction();
			return action;
		}

		@Override
		public PaletteElementGraphicalRepresentation getGraphicalRepresentation() {
			return gr;
		}

		@Override
		public DrawingPalette getPalette() {
			return AbstractWKFPalette.this;
		}
	}

	protected WKFPaletteElement makePaletteElement(WKFObject object,
			ShapeGraphicalRepresentation<? extends WKFObject> graphicalRepresentation, ContainerValidity containerValidity) {
		PaletteElementGraphicalRepresentation gr = new PaletteElementGraphicalRepresentation(graphicalRepresentation, null,
				getPaletteDrawing());

		if (object.getShortHelpText() != null) {
			gr.setToolTipText(object.getShortHelpText());
		} else {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("No help text defined for palette element: " + gr);
			}
		}

		WKFPaletteElement returned = new WKFPaletteElement(gr, object, containerValidity);
		gr.setDrawable(returned);
		addElement(returned);
		return returned;
	}

	protected interface ContainerValidity {
		public boolean isContainerValid(WKFObject container);
	}

	@Override
	public ProcessEditorController getController() {
		return (ProcessEditorController) super.getController();
	}

}
