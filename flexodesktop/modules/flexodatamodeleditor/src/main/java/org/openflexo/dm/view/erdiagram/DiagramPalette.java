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
package org.openflexo.dm.view.erdiagram;

import java.util.logging.Logger;

import org.openflexo.fge.controller.DrawingPalette;


public class DiagramPalette extends DrawingPalette {

	private static final Logger logger = Logger.getLogger(DiagramPalette.class.getPackage().getName());

	public DiagramPalette()
	{
		super(300,230,"default");
		/*int n = 1;
		for (int i=0; i<2; i++) {
			for (int j=0; j<4; j++) {	
				if (j<3)
					addElement(makeRoleElement(colorFor(i,j),i*(RoleGR.WIDTH+20)+25,j*(RoleGR.HEIGHT+20),"Role"+(n++),false));
				else 
					addElement(makeRoleElement(colorFor(i,j),i*(RoleGR.WIDTH+20)+25,j*(RoleGR.HEIGHT+20),"System",true));
			}
		}*/
		makePalettePanel();
	}
	
	/*private FlexoColor colorFor (int x, int y) 
	{
		if (x==0) {
			if (y==0) return new FlexoColor(Color.RED);
			if (y==1) return new FlexoColor(Color.BLUE);
			if (y==2) return new FlexoColor(Color.GREEN);
			if (y==3) return new FlexoColor(Color.LIGHT_GRAY);
		}
		else if (x==1) {
			if (y==0) return new FlexoColor(Color.MAGENTA);
			if (y==1) return new FlexoColor(Color.ORANGE);
			if (y==2) return new FlexoColor(Color.PINK);
			if (y==3) return new FlexoColor(Color.GRAY);
		}
		return new FlexoColor(Color.WHITE);
	}
	
	private PaletteElement makeRoleElement(final FlexoColor color, int x, int y, String roleName, final boolean isSystemRole)
	{
		Role role = new Role((FlexoProject)null,(FlexoWorkflow)null);
		role.setColor(color);
		role.setX(x,Role.DEFAULT);
		role.setY(y,Role.DEFAULT);
		role.setIsSystemRole(isSystemRole);
		try {
			role.setName(roleName);
		} catch (DuplicateRoleException e) {
			// Cannot happen since no related project
			e.printStackTrace();
		}
		final PaletteElementGraphicalRepresentation gr 
		= new PaletteElementGraphicalRepresentation(new RoleGR(role,getPaletteDrawing()),null,getPaletteDrawing());
		PaletteElement returned = new PaletteElement() {
			public boolean acceptDragging(GraphicalRepresentation gr)
			{
				return (gr instanceof DrawingGraphicalRepresentation);
			}
			public boolean elementDragged(GraphicalRepresentation gr, Point dropLocation)
			{
				//MyDrawingElement container = (MyDrawingElement)gr.getDrawable();
				//getController().addNewShape(new MyShape(getGraphicalRepresentation().getShapeType(), dropLocation, getController().getDrawing()),container);
				logger.info("Dropping new role for "+gr.getDrawable());
				FlexoWorkflow workflow = ((WorkflowModelObject)gr.getDrawable()).getWorkflow();
				AddRole addRole = AddRole.actionType.makeNewAction(workflow, null, getController().getWKFController().getEditor());
				addRole.setRoleAutomaticallyCreated(true);
				addRole.setLocation(dropLocation);
				if (isSystemRole) {
					addRole.setNewRoleName(workflow.getRoleList().getNextNewSystemRoleName());
				}
				else {
					addRole.setNewRoleName(workflow.getRoleList().getNextNewUserRoleName());
				}
				addRole.setNewColor(color);
				addRole.setIsSystemRole(isSystemRole);
				addRole.doAction();
				return true;
			}
			public PaletteElementGraphicalRepresentation getGraphicalRepresentation()
			{
				return gr;
			}		
			public DrawingPalette getPalette()
			{
				return DiagramPalette.this;
			}
		};
		gr.setDrawable(returned);
		return returned;
	}*/
	
	@Override
	public ERDiagramController getController()
	{
		return (ERDiagramController)super.getController();
	}
	
}
