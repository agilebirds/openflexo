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

import java.util.logging.Logger;

import org.openflexo.fge.DefaultDrawing;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.wkf.Role;
import org.openflexo.foundation.wkf.RoleList;
import org.openflexo.foundation.wkf.RoleSpecialization;
import org.openflexo.foundation.wkf.dm.RoleInserted;
import org.openflexo.foundation.wkf.dm.RoleRemoved;


public class RoleListRepresentation extends DefaultDrawing<RoleList> implements GraphicalFlexoObserver {

	private static final Logger logger = Logger.getLogger(RoleListRepresentation.class.getPackage().getName());

	private DrawingGraphicalRepresentation<RoleList> graphicalRepresentation;
	private FlexoEditor editor;
	
	public RoleListRepresentation(RoleList aRoleList, FlexoEditor editor)
	{
		super(aRoleList);
		this.editor = editor;
		graphicalRepresentation = new DrawingGraphicalRepresentation<RoleList>(this);
		graphicalRepresentation.addToMouseClickControls(new RoleEditorController.ShowContextualMenuControl());
		
		aRoleList.addObserver(this);
		
		updateGraphicalObjectsHierarchy();

	}
	
	@Override
	protected void buildGraphicalObjectsHierarchy()
	{
		for (Role r : getRoleList().getRoles()) {
			addDrawable(r, getRoleList());
		}

		for (Role r : getRoleList().getRoles()) {
			for (RoleSpecialization rs : r.getRoleSpecializations()) {
				addDrawable(rs, getRoleList());
			}
		}
	}
	
	public RoleList getRoleList()
	{
		return getModel();
	}

	@Override
	public DrawingGraphicalRepresentation<RoleList> getDrawingGraphicalRepresentation()
	{
		return graphicalRepresentation;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <O> GraphicalRepresentation<O> retrieveGraphicalRepresentation(O aDrawable)
	{
		return (GraphicalRepresentation<O>)buildGraphicalRepresentation(aDrawable);
	}

	private GraphicalRepresentation<?> buildGraphicalRepresentation(Object aDrawable)
	{
		if (aDrawable instanceof Role) {
			return new RoleGR((Role)aDrawable,this);
		}
		if (aDrawable instanceof RoleSpecialization) {
			return new RoleSpecializationGR((RoleSpecialization)aDrawable,this);
		}
		logger.warning("Cannot build GraphicalRepresentation for "+aDrawable);
		return null;
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) 
	{
		if (observable == getRoleList()) {
			//logger.info("Notified "+dataModification);
			if (dataModification instanceof RoleInserted) {
				updateGraphicalObjectsHierarchy();
			}
			else if (dataModification instanceof RoleRemoved) {
				updateGraphicalObjectsHierarchy();
				//removeDrawable(((RoleRemoved)dataModification).getRemovedRole(), getRoleList());
			}
		}
	}
	
	public FlexoEditor getEditor() {
		return editor;
	}
}
