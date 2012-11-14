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
package org.openflexo.wkf.swleditor;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.controller.DrawingPalette;
import org.openflexo.fge.controller.PaletteElement;
import org.openflexo.fge.controller.PaletteElement.PaletteElementGraphicalRepresentation;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.wkf.FlexoPetriGraph;
import org.openflexo.foundation.wkf.Role;
import org.openflexo.foundation.wkf.WKFArtefact;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.action.AddPort;
import org.openflexo.foundation.wkf.action.DropWKFElement;
import org.openflexo.foundation.wkf.node.AbstractActivityNode;
import org.openflexo.foundation.wkf.node.AbstractNode;
import org.openflexo.foundation.wkf.node.EventNode;
import org.openflexo.foundation.wkf.ws.DeletePort;
import org.openflexo.foundation.wkf.ws.FlexoPort;
import org.openflexo.foundation.wkf.ws.InOutPort;
import org.openflexo.foundation.wkf.ws.InPort;
import org.openflexo.foundation.wkf.ws.NewPort;
import org.openflexo.foundation.wkf.ws.OutPort;
import org.openflexo.foundation.wkf.ws.PortRegistery;
import org.openflexo.wkf.swleditor.gr.SWLObjectGR;

public abstract class AbstractWKFPalette extends DrawingPalette {

	private static final Logger logger = Logger.getLogger(AbstractWKFPalette.class.getPackage().getName());

	public AbstractWKFPalette(String title) {
		super(300, 230, title);
	}

	public AbstractWKFPalette(int width, int height, String title) {
		super(width, height, title);
	}

	protected WKFPaletteElement makePaletteElement(final WKFObject object,
			ShapeGraphicalRepresentation<? extends WKFObject> graphicalRepresentation, final ContainerValidity containerValidity) {
		final PaletteElementGraphicalRepresentation gr = new PaletteElementGraphicalRepresentation(graphicalRepresentation, null,
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

	public class WKFPaletteElement implements PaletteElement {
		private final PaletteElementGraphicalRepresentation gr;
		private final WKFObject object;
		private final ContainerValidity containerValidity;

		private WKFPaletteElement(PaletteElementGraphicalRepresentation gr, WKFObject object, ContainerValidity containerValidity) {
			this.gr = gr;
			this.object = object;
			this.containerValidity = containerValidity;
		}

		@Override
		public boolean acceptDragging(GraphicalRepresentation gr) {
			if (gr.getDrawable() instanceof FlexoModelObject) {
				return containerValidity.isContainerValid((FlexoModelObject) gr.getDrawable());
			}
			return false;
		}

		@Override
		public boolean elementDragged(GraphicalRepresentation gr, FGEPoint dropLocation) {
			FlexoPetriGraph container = null;
			logger.info("Dropping new object " + object + " for " + gr.getDrawable() + " container=" + container + " " + dropLocation);
			// if (gr.getDrawable() instanceof Role) container = process
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

					action.setLocation(dropLocation.x, dropLocation.y);
					action.setEditNodeLabel(true);
					action.setGraphicalContext(SWLEditorConstants.SWIMMING_LANE_EDITOR);
					action.doAction();
					return action.hasActionExecutionSucceeded();
				} else {
					logger.warning("Unexpected container");
					return false;
				}
			}

			else if (gr.getDrawable() instanceof AbstractActivityNode && object instanceof EventNode) {
				container = ((AbstractActivityNode) gr.getDrawable()).getProcess().getActivityPetriGraph();
				((EventNode) object).setBoundaryOf((AbstractActivityNode) gr.getDrawable());
				DropWKFElement action = createAndExecuteDropElementAction(dropLocation, container, null, true);
				((EventNode) object).setBoundaryOf(null);
				return action.hasActionExecutionSucceeded();
			}

			else {
				Role roleWhereToDrop = null;
				SwimmingLaneRepresentation swlRepresentation = ((SWLObjectGR) gr).getDrawing();
				if (gr.getDrawable() instanceof FlexoPetriGraph) {
					container = (FlexoPetriGraph) gr.getDrawable();
				}
				if (gr.getDrawable() instanceof Role) {
					container = swlRepresentation.getProcess().getActivityPetriGraph();
					roleWhereToDrop = (Role) gr.getDrawable();
				}
				if (container == null) {
					logger.warning("Unexpected container: " + gr);
					return false;
				}
				if (object instanceof WKFArtefact && container == swlRepresentation.getProcess().getActivityPetriGraph()) {
					FGEPoint p = new FGEPoint();
					GraphicalRepresentation.convertFromDrawableToDrawingAT(gr, 1).transform(dropLocation, p);
					dropLocation = p;
					if (gr.getDrawing() != null && swlRepresentation.isVisible(swlRepresentation.getProcess().getPortRegistery())) {
						if (swlRepresentation.getGraphicalRepresentation(swlRepresentation.getProcess().getPortRegistery()) != null) {
							dropLocation.y -= swlRepresentation.getGraphicalRepresentation(
									swlRepresentation.getProcess().getPortRegistery()).getViewBounds(1.0).height;
						}
					}
				}
				DropWKFElement action = createAndExecuteDropElementAction(dropLocation, container, roleWhereToDrop, true);
				if (action.hasActionExecutionSucceeded()) {
					if (roleWhereToDrop != null && action.getObject() instanceof AbstractNode) {
						swlRepresentation.setRepresentationRole(roleWhereToDrop, (AbstractNode) action.getObject());
					}
					action.getObject().setX(dropLocation.x, SWLEditorConstants.SWIMMING_LANE_EDITOR);
					action.getObject().setY(dropLocation.y, SWLEditorConstants.SWIMMING_LANE_EDITOR);
					return true;
				} else {
					return false;
				}
			}
		}

		public DropWKFElement createAndExecuteDropElementAction(FGEPoint dropLocation, FlexoPetriGraph container, Role roleWhereToDrop,
				boolean handlePaletteOffset) {
			DropWKFElement action = DropWKFElement.actionType.makeNewAction(container, null, getController().getEditor());
			action.setHandlePaletteOffset(handlePaletteOffset);
			action.setObject((WKFObject) object.cloneUsingXMLMapping(container.getProcess().instanciateNewBuilder(), true, container
					.getProcess().getXMLMapping()));
			if (roleWhereToDrop != null) {
				action.setRoleToAssociate(roleWhereToDrop);
			}
			action.setGraphicalContext(SWLEditorConstants.SWIMMING_LANE_EDITOR);
			action.setHandlePaletteOffset(false);
			action.setLocation(dropLocation.x, dropLocation.y);
			action.setResetNodeName(true);
			action.setEditNodeLabel(true);
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

	protected interface ContainerValidity {
		public boolean isContainerValid(FlexoModelObject container);
	}

	@Override
	public SwimmingLaneEditorController getController() {
		return (SwimmingLaneEditorController) super.getController();
	}

}
