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
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.DataBinding.BindingDefinitionType;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.validation.FixProposal;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.FMLRepresentationContext;
import org.openflexo.foundation.viewpoint.FMLRepresentationContext.FMLRepresentationOutput;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.annotations.FIBPanel;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.diagram.fml.ConnectorPatternRole;
import org.openflexo.technologyadapter.diagram.fml.DiagramEditionScheme;
import org.openflexo.technologyadapter.diagram.fml.LinkScheme;
import org.openflexo.technologyadapter.diagram.fml.ShapePatternRole;
import org.openflexo.technologyadapter.diagram.model.Diagram;
import org.openflexo.technologyadapter.diagram.model.DiagramConnector;
import org.openflexo.technologyadapter.diagram.model.DiagramContainerElement;
import org.openflexo.technologyadapter.diagram.model.DiagramElementImpl;
import org.openflexo.technologyadapter.diagram.model.DiagramFactory;
import org.openflexo.technologyadapter.diagram.model.DiagramShape;
import org.openflexo.technologyadapter.diagram.model.action.LinkSchemeAction;
import org.openflexo.toolbox.StringUtils;

/**
 * This edition primitive addresses the creation of a new connector linking two shapes in a diagram
 * 
 * @author sylvain
 * 
 */
@FIBPanel("Fib/AddConnectorPanel.fib")
@ModelEntity
@ImplementationClass(AddConnector.AddConnectorImpl.class)
@XMLElement
public interface AddConnector extends AddDiagramElementAction<DiagramConnector> {

	@PropertyIdentifier(type = DataBinding.class)
	public static final String FROM_SHAPE_KEY = "fromShape";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String TO_SHAPE_KEY = "toShape";

	@Getter(value = FROM_SHAPE_KEY)
	@XMLAttribute
	public DataBinding<DiagramShape> getFromShape();

	@Setter(FROM_SHAPE_KEY)
	public void setFromShape(DataBinding<DiagramShape> fromShape);

	@Getter(value = TO_SHAPE_KEY)
	@XMLAttribute
	public DataBinding<DiagramShape> getToShape();

	@Setter(TO_SHAPE_KEY)
	public void setToShape(DataBinding<DiagramShape> toShape);

	@Override
	public ConnectorPatternRole getPatternRole();

	public static abstract class AddConnectorImpl extends AddDiagramElementActionImpl<DiagramConnector> implements AddConnector {

		private static final Logger logger = Logger.getLogger(LinkSchemeAction.class.getPackage().getName());

		public AddConnectorImpl() {
			super();
		}

		@Override
		public String getFMLRepresentation(FMLRepresentationContext context) {
			FMLRepresentationOutput out = new FMLRepresentationOutput(context);
			if (getAssignation().isSet()) {
				out.append(getAssignation().toString() + " = (", context);
			}
			out.append(getClass().getSimpleName() + " conformTo ConnectorSpecification from " + getModelSlot().getName() + " {"
					+ StringUtils.LINE_SEPARATOR, context);
			out.append(getGraphicalElementSpecificationFMLRepresentation(context), context);
			out.append("}", context);
			if (getAssignation().isSet()) {
				out.append(")", context);
			}
			return out.toString();
		}

		/*@Override
		public List<ConnectorPatternRole> getAvailablePatternRoles() {
			if (getEditionPattern() != null) {
				return getEditionPattern().getPatternRoles(ConnectorPatternRole.class);
			}
			return null;
		}*/

		@Override
		public ConnectorPatternRole getPatternRole() {
			PatternRole superPatternRole = super.getPatternRole();
			if (superPatternRole instanceof ConnectorPatternRole) {
				return (ConnectorPatternRole) superPatternRole;
			} else if (superPatternRole != null) {
				// logger.warning("Unexpected pattern role of type " + superPatternRole.getClass().getSimpleName());
				return null;
			}
			return null;
		}

		public DiagramShape getFromShape(EditionSchemeAction action) {
			if (getPatternRole() != null && !getPatternRole().getStartShapeAsDefinedInAction()) {
				FlexoObject returned = action.getEditionPatternInstance().getPatternActor(getPatternRole().getStartShapePatternRole());
				return action.getEditionPatternInstance().getPatternActor(getPatternRole().getStartShapePatternRole());
			} else {
				try {
					return getFromShape().getBindingValue(action);
				} catch (TypeMismatchException e) {
					e.printStackTrace();
				} catch (NullReferenceException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
				return null;
			}
		}

		public DiagramShape getToShape(EditionSchemeAction action) {
			if (getPatternRole() != null && !getPatternRole().getEndShapeAsDefinedInAction()) {
				return action.getEditionPatternInstance().getPatternActor(getPatternRole().getEndShapePatternRole());
			} else {
				try {
					return getToShape().getBindingValue(action);
				} catch (TypeMismatchException e) {
					e.printStackTrace();
				} catch (NullReferenceException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
				return null;
			}
		}

		@Override
		public String toString() {
			return "AddConnector " + Integer.toHexString(hashCode()) + " patternRole=" + getPatternRole();
		}

		/*@Override
		public ConnectorPatternRole getPatternRole() {
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
		public void setPatternRole(ConnectorPatternRole patternRole) {
			super.setPatternRole(patternRole);
		}*/

		private DataBinding<DiagramShape> fromShape;
		private DataBinding<DiagramShape> toShape;

		@Override
		public DataBinding<DiagramShape> getFromShape() {
			if (fromShape == null) {
				fromShape = new DataBinding<DiagramShape>(this, DiagramShape.class, BindingDefinitionType.GET);
				fromShape.setBindingName("fromShape");
			}
			return fromShape;
		}

		@Override
		public void setFromShape(DataBinding<DiagramShape> fromShape) {
			if (fromShape != null) {
				fromShape.setOwner(this);
				fromShape.setBindingName("fromShape");
				fromShape.setDeclaredType(DiagramShape.class);
				fromShape.setBindingDefinitionType(BindingDefinitionType.GET);
			}
			this.fromShape = fromShape;
			notifiedBindingChanged(this.fromShape);
		}

		@Override
		public DataBinding<DiagramShape> getToShape() {
			if (toShape == null) {
				toShape = new DataBinding<DiagramShape>(this, DiagramShape.class, BindingDefinitionType.GET);
				toShape.setBindingName("toShape");
			}
			return toShape;
		}

		@Override
		public void setToShape(DataBinding<DiagramShape> toShape) {
			if (toShape != null) {
				toShape.setOwner(this);
				toShape.setBindingName("toShape");
				toShape.setDeclaredType(DiagramShape.class);
				toShape.setBindingDefinitionType(BindingDefinitionType.GET);
			}
			this.toShape = toShape;
			notifiedBindingChanged(this.toShape);
		}

		@Override
		public Type getAssignableType() {
			return DiagramConnector.class;
		}

		@Override
		public boolean isAssignationRequired() {
			return true;
		}

		@Override
		public DiagramConnector performAction(EditionSchemeAction action) {

			DiagramShape fromShape = getFromShape(action);
			DiagramShape toShape = getToShape(action);
			Diagram diagram = fromShape.getDiagram();
			DiagramFactory factory = diagram.getDiagramFactory();
			DiagramConnector newConnector = factory.newInstance(DiagramConnector.class);
			newConnector.setStartShape(fromShape);
			newConnector.setEndShape(toShape);
			DiagramContainerElement<?> parent = DiagramElementImpl.getFirstCommonAncestor(fromShape, toShape);
			if (parent == null) {
				throw new IllegalArgumentException("No common ancestor");
			}

			GraphicalRepresentation grToUse = null;

			// If an overriden graphical representation is defined, use it
			/*if (action.getOverridingGraphicalRepresentation(getPatternRole()) != null) {
				grToUse = action.getOverridingGraphicalRepresentation(getPatternRole());
			} else*/if (getPatternRole().getGraphicalRepresentation() != null) {
				grToUse = getPatternRole().getGraphicalRepresentation();
			}

			ConnectorGraphicalRepresentation newGR = factory.makeConnectorGraphicalRepresentation();
			newGR.setsWith(grToUse);
			newConnector.setGraphicalRepresentation(newGR);

			parent.addToConnectors(newConnector);

			// Register reference
			newConnector.registerEditionPatternReference(action.getEditionPatternInstance());

			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Added connector " + newConnector + " under " + parent);
			}
			return newConnector;
		}

		@Override
		public void finalizePerformAction(EditionSchemeAction action, DiagramConnector newConnector) {
			super.finalizePerformAction(action, newConnector);
			// Be sure that the newly created connector is updated
			// newConnector.update();
		}

	}

	public static class AddConnectorActionMustAdressAValidConnectorPatternRole extends
			ValidationRule<AddConnectorActionMustAdressAValidConnectorPatternRole, AddConnector> {
		public AddConnectorActionMustAdressAValidConnectorPatternRole() {
			super(AddConnector.class, "add_connector_action_must_address_a_valid_connector_pattern_role");
		}

		@Override
		public ValidationIssue<AddConnectorActionMustAdressAValidConnectorPatternRole, AddConnector> applyValidation(AddConnector action) {
			if (action.getPatternRole() == null) {
				Vector<FixProposal<AddConnectorActionMustAdressAValidConnectorPatternRole, AddConnector>> v = new Vector<FixProposal<AddConnectorActionMustAdressAValidConnectorPatternRole, AddConnector>>();
				for (ConnectorPatternRole pr : action.getEditionPattern().getPatternRoles(ConnectorPatternRole.class)) {
					v.add(new SetsPatternRole(pr));
				}
				return new ValidationError<AddConnectorActionMustAdressAValidConnectorPatternRole, AddConnector>(this, action,
						"add_connector_action_does_not_address_a_valid_connector_pattern_role", v);
			}
			return null;
		}

		protected static class SetsPatternRole extends FixProposal<AddConnectorActionMustAdressAValidConnectorPatternRole, AddConnector> {

			private final ConnectorPatternRole patternRole;

			public SetsPatternRole(ConnectorPatternRole patternRole) {
				super("assign_action_to_pattern_role_($patternRole.patternRoleName)");
				this.patternRole = patternRole;
			}

			public ConnectorPatternRole getPatternRole() {
				return patternRole;
			}

			@Override
			protected void fixAction() {
				AddConnector action = getObject();
				action.setAssignation(new DataBinding<Object>(patternRole.getPatternRoleName()));
			}

		}
	}

	public static class AddConnectorActionMustHaveAValidStartingShape extends
			ValidationRule<AddConnectorActionMustHaveAValidStartingShape, AddConnector> {
		public AddConnectorActionMustHaveAValidStartingShape() {
			super(AddConnector.class, "add_connector_action_must_have_a_valid_starting_shape");
		}

		@Override
		public ValidationIssue<AddConnectorActionMustHaveAValidStartingShape, AddConnector> applyValidation(AddConnector action) {
			ConnectorPatternRole pr = action.getPatternRole();
			DataBinding<DiagramShape> db = action.getFromShape();
			if (pr != null && pr.getStartShapeAsDefinedInAction() && !(db.isSet() && db.isValid())) {
				Vector<FixProposal<AddConnectorActionMustHaveAValidStartingShape, AddConnector>> v = new Vector<FixProposal<AddConnectorActionMustHaveAValidStartingShape, AddConnector>>();
				if (action.getEditionScheme() instanceof LinkScheme) {
					EditionPattern targetEditionPattern = ((LinkScheme) action.getEditionScheme()).getFromTargetEditionPattern();
					if (targetEditionPattern != null) {
						for (ShapePatternRole spr : action.getEditionPattern().getPatternRoles(ShapePatternRole.class)) {
							v.add(new SetsStartingShapeToStartTargetShape(targetEditionPattern, spr));
						}
					}
				}
				for (ShapePatternRole spr : action.getEditionPattern().getPatternRoles(ShapePatternRole.class)) {
					v.add(new SetsStartingShapeToShape(spr));
				}
				return new ValidationError<AddConnectorActionMustHaveAValidStartingShape, AddConnector>(this, action,
						"add_connector_action_does_not_have_a_valid_starting_shape", v);
			}
			return null;
		}

		protected static class SetsStartingShapeToShape extends FixProposal<AddConnectorActionMustHaveAValidStartingShape, AddConnector> {

			private final ShapePatternRole patternRole;

			public SetsStartingShapeToShape(ShapePatternRole patternRole) {
				super("sets_starting_shape_to_($patternRole.patternRoleName)");
				this.patternRole = patternRole;
			}

			public ShapePatternRole getPatternRole() {
				return patternRole;
			}

			@Override
			protected void fixAction() {
				AddConnector action = getObject();
				action.setFromShape(new DataBinding<DiagramShape>(patternRole.getPatternRoleName()));
			}
		}

		protected static class SetsStartingShapeToStartTargetShape extends
				FixProposal<AddConnectorActionMustHaveAValidStartingShape, AddConnector> {

			private final EditionPattern target;
			private final ShapePatternRole patternRole;

			public SetsStartingShapeToStartTargetShape(EditionPattern target, ShapePatternRole patternRole) {
				super("sets_starting_shape_to_fromTarget.($patternRole.patternRoleName)");
				this.target = target;
				this.patternRole = patternRole;
			}

			public ShapePatternRole getPatternRole() {
				return patternRole;
			}

			public EditionPattern getTarget() {
				return target;
			}

			@Override
			protected void fixAction() {
				AddConnector action = getObject();
				action.setFromShape(new DataBinding<DiagramShape>(DiagramEditionScheme.FROM_TARGET + "." + patternRole.getPatternRoleName()));
			}
		}

	}

	public static class AddConnectorActionMustHaveAValidEndingShape extends
			ValidationRule<AddConnectorActionMustHaveAValidEndingShape, AddConnector> {
		public AddConnectorActionMustHaveAValidEndingShape() {
			super(AddConnector.class, "add_connector_action_must_have_a_valid_ending_shape");
		}

		@Override
		public ValidationIssue<AddConnectorActionMustHaveAValidEndingShape, AddConnector> applyValidation(AddConnector action) {
			ConnectorPatternRole pr = action.getPatternRole();
			DataBinding<DiagramShape> shape = action.getToShape();
			if (pr != null && pr.getEndShapeAsDefinedInAction() && !(shape.isSet() && shape.isValid())) {
				Vector<FixProposal<AddConnectorActionMustHaveAValidEndingShape, AddConnector>> v = new Vector<FixProposal<AddConnectorActionMustHaveAValidEndingShape, AddConnector>>();
				if (action.getEditionScheme() instanceof LinkScheme) {
					EditionPattern targetEditionPattern = ((LinkScheme) action.getEditionScheme()).getToTargetEditionPattern();
					if (targetEditionPattern != null) {
						for (ShapePatternRole spr : action.getEditionPattern().getPatternRoles(ShapePatternRole.class)) {
							v.add(new SetsEndingShapeToToTargetShape(targetEditionPattern, spr));
						}
					}
				}
				for (ShapePatternRole spr : action.getEditionPattern().getPatternRoles(ShapePatternRole.class)) {
					v.add(new SetsEndingShapeToShape(spr));
				}
				return new ValidationError<AddConnectorActionMustHaveAValidEndingShape, AddConnector>(this, action,
						"add_connector_action_does_not_have_a_valid_ending_shape", v);
			}
			return null;
		}

		protected static class SetsEndingShapeToShape extends FixProposal<AddConnectorActionMustHaveAValidEndingShape, AddConnector> {

			private final ShapePatternRole patternRole;

			public SetsEndingShapeToShape(ShapePatternRole patternRole) {
				super("sets_ending_shape_to_($patternRole.patternRoleName)");
				this.patternRole = patternRole;
			}

			public ShapePatternRole getPatternRole() {
				return patternRole;
			}

			@Override
			protected void fixAction() {
				AddConnector action = getObject();
				action.setToShape(new DataBinding<DiagramShape>(patternRole.getPatternRoleName()));
			}
		}

		protected static class SetsEndingShapeToToTargetShape extends
				FixProposal<AddConnectorActionMustHaveAValidEndingShape, AddConnector> {

			private final EditionPattern target;
			private final ShapePatternRole patternRole;

			public SetsEndingShapeToToTargetShape(EditionPattern target, ShapePatternRole patternRole) {
				super("sets_ending_shape_to_toTarget.($patternRole.patternRoleName)");
				this.target = target;
				this.patternRole = patternRole;
			}

			public ShapePatternRole getPatternRole() {
				return patternRole;
			}

			public EditionPattern getTarget() {
				return target;
			}

			@Override
			protected void fixAction() {
				AddConnector action = getObject();
				action.setToShape(new DataBinding<DiagramShape>(DiagramEditionScheme.TO_TARGET + "." + patternRole.getPatternRoleName()));
			}
		}

	}
}
