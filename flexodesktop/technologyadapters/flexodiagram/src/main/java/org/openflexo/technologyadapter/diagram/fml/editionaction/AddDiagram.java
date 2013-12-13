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
package org.openflexo.technologyadapter.diagram.fml.editionaction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.rm.ViewResource;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.ViewPointObject.FMLRepresentationContext.FMLRepresentationOutput;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.foundation.viewpoint.annotations.FIBPanel;
import org.openflexo.technologyadapter.diagram.fml.DiagramPatternRole;
import org.openflexo.technologyadapter.diagram.fml.DiagramSpecification;
import org.openflexo.technologyadapter.diagram.model.Diagram;
import org.openflexo.toolbox.StringUtils;

@FIBPanel("Fib/AddDiagramPanel.fib")
public class AddDiagram extends DiagramAction<Diagram> {

	private static final Logger logger = Logger.getLogger(AddDiagram.class.getPackage().getName());

	public AddDiagram(VirtualModel.VirtualModelBuilder builder) {
		super(builder);
	}

	@Override
	public String getFMLRepresentation(FMLRepresentationContext context) {
		FMLRepresentationOutput out = new FMLRepresentationOutput(context);
		if (getAssignation().isSet()) {
			out.append(getAssignation().toString() + " = (", context);
		}
		out.append(getClass().getSimpleName() + " conformTo " + getDiagramSpecification().getURI() + " from " + getModelSlot().getName()
				+ " {" + StringUtils.LINE_SEPARATOR, context);
		out.append("}", context);
		if (getAssignation().isSet()) {
			out.append(")", context);
		}
		return out.toString();
	}

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

	public String getDiagramName(EditionSchemeAction action) {
		try {
			return getDiagramName().getBindingValue(action);
		} catch (TypeMismatchException e) {
			e.printStackTrace();
		} catch (NullReferenceException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	private DataBinding<String> diagramName;

	public DataBinding<String> getDiagramName() {
		if (diagramName == null) {
			diagramName = new DataBinding<String>(this, String.class, DataBinding.BindingDefinitionType.GET);
			diagramName.setBindingName("diagramName");
		}
		return diagramName;
	}

	public void setDiagramName(DataBinding<String> diagramName) {
		if (diagramName != null) {
			diagramName.setOwner(this);
			diagramName.setDeclaredType(String.class);
			diagramName.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			diagramName.setBindingName("diagramName");
		}
		this.diagramName = diagramName;
	}

	public DiagramSpecification getDiagramSpecification() {
		if (getPatternRole() instanceof DiagramPatternRole) {
			return getPatternRole().getDiagramSpecification();
		}
		return null;
	}

	public void setDiagramSpecification(DiagramSpecification diagramSpecification) {
		if (getPatternRole() instanceof DiagramPatternRole) {
			getPatternRole().setDiagramSpecification(diagramSpecification);
		}
	}

	@Override
	public Type getAssignableType() {
		return View.class;
	}

	@Override
	public Diagram performAction(EditionSchemeAction action) {
		Diagram initialDiagram = (Diagram) action.retrieveVirtualModelInstance();
		ViewResource viewResource = initialDiagram.getView().getResource();
		org.openflexo.technologyadapter.diagram.model.action.CreateDiagram addDiagramAction = org.openflexo.technologyadapter.diagram.model.action.CreateDiagram.actionType
				.makeNewEmbeddedAction(initialDiagram.getView(), null, action);
		addDiagramAction.setNewVirtualModelInstanceName(getDiagramName(action));
		addDiagramAction.setDiagramSpecification(getPatternRole().getDiagramSpecification());
		addDiagramAction.skipChoosePopup = true;
		addDiagramAction.doAction();
		if (addDiagramAction.hasActionExecutionSucceeded() && addDiagramAction.getNewDiagram() != null) {
			Diagram newDiagram = addDiagramAction.getNewDiagram();
			/*ShapePatternRole shapePatternRole = action.getShapePatternRole();
			if (shapePatternRole == null) {
				logger.warning("Sorry, shape pattern role is undefined");
				return newShema;
			}
			// logger.info("ShapeSpecification pattern role: " + shapePatternRole);
			EditionPatternInstance newEditionPatternInstance = getProject().makeNewEditionPatternInstance(getEditionPattern());
			DiagramShape newShape = new DiagramShape(newShema);
			if (getEditionPatternInstance().getPatternActor(shapePatternRole) instanceof DiagramShape) {
				DiagramShape primaryShape = (DiagramShape) getEditionPatternInstance().getPatternActor(shapePatternRole);
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

}
