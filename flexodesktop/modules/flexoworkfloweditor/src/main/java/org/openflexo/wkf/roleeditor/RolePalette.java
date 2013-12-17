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
package org.openflexo.wkf.roleeditor;

import java.awt.Color;
import java.util.logging.Logger;

import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.FGEUtils;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.controller.DrawingPalette;
import org.openflexo.fge.controller.PaletteElement;
import org.openflexo.fge.controller.PaletteElement.PaletteElementGraphicalRepresentation;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.foundation.RepresentableFlexoModelObject;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.wkf.DuplicateRoleException;
import org.openflexo.foundation.wkf.FlexoWorkflow;
import org.openflexo.foundation.wkf.Role;
import org.openflexo.foundation.wkf.WorkflowModelObject;
import org.openflexo.foundation.wkf.action.AddRole;

public class RolePalette extends DrawingPalette {

	private static final Logger logger = Logger.getLogger(RolePalette.class.getPackage().getName());

	public RolePalette() {
		super(300, 230, "default");
		int n = 1;
		int m = 1;
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 4; j++) {
				if (j < 3) {
					addElement(makeRoleElement(colorFor(i, j), i * (RoleGR.WIDTH + 20) + 25, j * (RoleGR.HEIGHT + 20), "Role" + n++, false));
				} else {
					addElement(makeRoleElement(colorFor(i, j), i * (RoleGR.WIDTH + 20) + 25, j * (RoleGR.HEIGHT + 20), "System" + m++, true));
				}
			}
		}
		makePalettePanel();
	}

	private Color colorFor(int x, int y) {
		if (x == 0) {
			if (y == 0) {
				return FGEUtils.NICE_RED;
			}
			if (y == 1) {
				return FGEUtils.NICE_BLUE;
			}
			if (y == 2) {
				return FGEUtils.NICE_YELLOW;
			}
			if (y == 3) {
				return Color.LIGHT_GRAY;
			}
		} else if (x == 1) {
			if (y == 0) {
				return FGEUtils.NICE_PINK;
			}
			if (y == 1) {
				return FGEUtils.NICE_GREEN;
			}
			if (y == 2) {
				return FGEUtils.NICE_TURQUOISE;
			}
			if (y == 3) {
				return Color.GRAY;
			}
		}
		return Color.WHITE;
	}

	private PaletteElement makeRoleElement(final Color color, int x, int y, String roleName, final boolean isSystemRole) {
		Role role = new Role((FlexoProject) null, (FlexoWorkflow) null);
		role.setColor(color);
		role.setX(x, RepresentableFlexoModelObject.DEFAULT);
		role.setY(y, RepresentableFlexoModelObject.DEFAULT);
		role.setIsSystemRole(isSystemRole);
		try {
			role.setName(roleName);
		} catch (DuplicateRoleException e) {
			// Cannot happen since no related project
			e.printStackTrace();
		}
		final PaletteElementGraphicalRepresentation gr = new PaletteElementGraphicalRepresentation(new RoleGR(role, getPaletteDrawing()),
				null, getPaletteDrawing());
		PaletteElement returned = new PaletteElement() {
			@Override
			public boolean acceptDragging(GraphicalRepresentation gr) {
				return gr instanceof DrawingGraphicalRepresentation;
			}

			@Override
			public boolean elementDragged(GraphicalRepresentation gr, FGEPoint dropLocation) {
				// DiagramElement container = (DiagramElement)gr.getDrawable();
				// getController().addNewShape(new Shape(getGraphicalRepresentation().getShapeType(), dropLocation,
				// getController().getDrawing()),container);
				logger.info("Dropping new role for " + gr.getDrawable());
				FlexoWorkflow workflow = ((WorkflowModelObject) gr.getDrawable()).getWorkflow();
				AddRole addRole = AddRole.actionType.makeNewAction(workflow, null, getController().getWKFController().getEditor());
				addRole.setRoleAutomaticallyCreated(true);
				addRole.setLocation(dropLocation.x, dropLocation.y);
				if (isSystemRole) {
					addRole.setNewRoleName(workflow.getRoleList().getNextNewSystemRoleName());
				} else {
					addRole.setNewRoleName(workflow.getRoleList().getNextNewUserRoleName());
				}
				addRole.setNewColor(color);
				addRole.setIsSystemRole(isSystemRole);
				// addRole.doAction();
				addRole.doAction();
				return addRole.hasActionExecutionSucceeded();
			}

			@Override
			public PaletteElementGraphicalRepresentation getGraphicalRepresentation() {
				return gr;
			}

			@Override
			public DrawingPalette getPalette() {
				return RolePalette.this;
			}
		};
		gr.setDrawable(returned);
		return returned;
	}

	@Override
	public RoleEditorController getController() {
		return (RoleEditorController) super.getController();
	}

}
