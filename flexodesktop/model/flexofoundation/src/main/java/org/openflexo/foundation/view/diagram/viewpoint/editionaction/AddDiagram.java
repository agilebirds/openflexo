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
package org.openflexo.foundation.view.diagram.viewpoint.editionaction;

import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.view.action.AddView;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.view.diagram.model.View;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramPatternRole;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.foundation.viewpoint.binding.ViewPointDataBinding;

public class AddDiagram extends DiagramAction<View> {

	private static final Logger logger = Logger.getLogger(AddDiagram.class.getPackage().getName());

	public AddDiagram(ViewPointBuilder builder) {
		super(builder);
	}

	@Override
	public EditionActionType getEditionActionType() {
		return EditionActionType.AddDiagram;
	}

	/*@Override
	public List<DiagramPatternRole> getAvailablePatternRoles() {
		return getEditionPattern().getPatternRoles(DiagramPatternRole.class);
	}*/

	@Override
	public DiagramPatternRole getPatternRole() {
		PatternRole superPatternRole = super.getPatternRole();
		if (superPatternRole instanceof DiagramPatternRole) {
			return (DiagramPatternRole) superPatternRole;
		} else if (superPatternRole != null) {
			// logger.warning("Unexpected pattern role of type " + superPatternRole.getClass().getSimpleName());
			return null;
		}
		return null;
	}

	@Override
	public String getInspectorName() {
		return Inspectors.VPM.ADD_SHEMA_INSPECTOR;
	}

	public String getDiagramName(EditionSchemeAction action) {
		return (String) getDiagramName().getBindingValue(action);
	}

	/*@Override
	public DiagramPatternRole getPatternRole() {
		try {
			return super.getPatternRole();
		} catch (ClassCastException e) {
			logger.warning("Unexpected pattern role type");
			setPatternRole(null);
			return null;
		}
	}*/

	// FIXME: if we remove this useless code, some FIB won't work (see EditionPatternView.fib, inspect an AddIndividual)
	// Need to be fixed in KeyValueProperty.java
	/*@Override
	public void setPatternRole(DiagramPatternRole patternRole) {
		super.setPatternRole(patternRole);
	}*/

	private ViewPointDataBinding diagramName;

	private BindingDefinition DIAGRAM_NAME = new BindingDefinition("diagramName", String.class, BindingDefinitionType.GET, false);

	public BindingDefinition getDiagramNameBindingDefinition() {
		return DIAGRAM_NAME;
	}

	public ViewPointDataBinding getDiagramName() {
		if (diagramName == null) {
			diagramName = new ViewPointDataBinding(this, EditionActionBindingAttribute.diagramName, getDiagramNameBindingDefinition());
		}
		return diagramName;
	}

	public void setDiagramName(ViewPointDataBinding aDiagramName) {
		if (aDiagramName != null) {
			aDiagramName.setOwner(this);
			aDiagramName.setBindingAttribute(EditionActionBindingAttribute.diagramName);
			aDiagramName.setBindingDefinition(getDiagramNameBindingDefinition());
		}
		this.diagramName = aDiagramName;
	}

	public ViewPoint getViewpoint() {
		if (getPatternRole() instanceof DiagramPatternRole) {
			return getPatternRole().getViewpoint();
		}
		return null;
	}

	public void setViewpoint(ViewPoint viewpoint) {
		if (getPatternRole() instanceof DiagramPatternRole) {
			getPatternRole().setViewpoint(viewpoint);
		}
	}

	@Override
	public Type getAssignableType() {
		return View.class;
	}

	@Override
	public View performAction(EditionSchemeAction action) {
		View initialShema = action.retrieveOEShema();
		AddView addDiagramAction = AddView.actionType.makeNewEmbeddedAction(initialShema.getShemaDefinition().getFolder(), null, action);
		addDiagramAction.newViewTitle = getDiagramName(action);
		addDiagramAction.viewpoint = getPatternRole().getViewpoint();
		addDiagramAction.setFolder(initialShema.getShemaDefinition().getFolder());
		addDiagramAction.skipChoosePopup = true;
		addDiagramAction.doAction();
		if (addDiagramAction.hasActionExecutionSucceeded() && addDiagramAction.getNewDiagram() != null) {
			View newDiagram = addDiagramAction.getNewDiagram().getView();
			/*ShapePatternRole shapePatternRole = action.getShapePatternRole();
			if (shapePatternRole == null) {
				logger.warning("Sorry, shape pattern role is undefined");
				return newShema;
			}
			// logger.info("Shape pattern role: " + shapePatternRole);
			EditionPatternInstance newEditionPatternInstance = getProject().makeNewEditionPatternInstance(getEditionPattern());
			ViewShape newShape = new ViewShape(newShema);
			if (getEditionPatternInstance().getPatternActor(shapePatternRole) instanceof ViewShape) {
				ViewShape primaryShape = (ViewShape) getEditionPatternInstance().getPatternActor(shapePatternRole);
				newShape.setGraphicalRepresentation(primaryShape.getGraphicalRepresentation());
			} else if (shapePatternRole.getGraphicalRepresentation() != null) {
				newShape.setGraphicalRepresentation(shapePatternRole.getGraphicalRepresentation());
			}
			// Register reference
			newShape.registerEditionPatternReference(newEditionPatternInstance, shapePatternRole);
			newShema.addToChilds(newShape);
			newEditionPatternInstance.setObjectForPatternRole(newShape, shapePatternRole);
			// Duplicates all other pattern roles
			for (PatternRole role : getEditionPattern().getPatternRoles()) {
				if (role != action.getPatternRole() && role != shapePatternRole) {
					FlexoModelObject patternActor = getEditionPatternInstance().getPatternActor(role);
					logger.info("Duplicate pattern actor for role " + role + " value=" + patternActor);
					newEditionPatternInstance.setObjectForPatternRole(patternActor, role);
					patternActor.registerEditionPatternReference(newEditionPatternInstance, role);
				}
			}*/

			return newDiagram;
		}
		return null;
	}

	@Override
	public void finalizePerformAction(EditionSchemeAction action, View initialContext) {
	}

}
