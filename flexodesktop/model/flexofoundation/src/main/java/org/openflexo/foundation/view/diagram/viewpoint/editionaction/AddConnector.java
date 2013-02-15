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
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.validation.FixProposal;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.view.diagram.action.LinkSchemeAction;
import org.openflexo.foundation.view.diagram.model.DiagramConnector;
import org.openflexo.foundation.view.diagram.model.DiagramElement;
import org.openflexo.foundation.view.diagram.model.DiagramShape;
import org.openflexo.foundation.view.diagram.viewpoint.ConnectorPatternRole;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramEditionScheme;
import org.openflexo.foundation.view.diagram.viewpoint.LinkScheme;
import org.openflexo.foundation.view.diagram.viewpoint.ShapePatternRole;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.VirtualModel;

/**
 * This edition primitive addresses the creation of a new connector linking two shapes in a diagram
 * 
 * @author sylvain
 * 
 */
public class AddConnector extends AddShemaElementAction<DiagramConnector> {

	private static final Logger logger = Logger.getLogger(LinkSchemeAction.class.getPackage().getName());

	public AddConnector(VirtualModel.VirtualModelBuilder builder) {
		super(builder);
	}

	@Override
	public EditionActionType getEditionActionType() {
		return EditionActionType.AddConnector;
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
			FlexoModelObject returned = action.getEditionPatternInstance().getPatternActor(getPatternRole().getStartShapePatternRole());
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
			FlexoModelObject returned = action.getEditionPatternInstance().getPatternActor(getPatternRole().getEndShapePatternRole());
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

	public DataBinding<DiagramShape> getFromShape() {
		if (fromShape == null) {
			fromShape = new DataBinding<DiagramShape>(this, DiagramShape.class, BindingDefinitionType.GET);
			fromShape.setBindingName("fromShape");
		}
		return fromShape;
	}

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

	public DataBinding<DiagramShape> getToShape() {
		if (toShape == null) {
			toShape = new DataBinding<DiagramShape>(this, DiagramShape.class, BindingDefinitionType.GET);
			toShape.setBindingName("toShape");
		}
		return toShape;
	}

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
		DiagramConnector newConnector = new DiagramConnector(fromShape.getDiagram(), fromShape, toShape);
		DiagramElement<?> parent = DiagramElement.getFirstCommonAncestor(fromShape, toShape);
		if (parent == null) {
			throw new IllegalArgumentException("No common ancestor");
		}

		GraphicalRepresentation<?> grToUse = null;

		// If an overriden graphical representation is defined, use it
		if (action.getOverridingGraphicalRepresentation(getPatternRole()) != null) {
			grToUse = action.getOverridingGraphicalRepresentation(getPatternRole());
		} else if (getPatternRole().getGraphicalRepresentation() != null) {
			grToUse = getPatternRole().getGraphicalRepresentation();
		}

		ConnectorGraphicalRepresentation<DiagramConnector> newGR = new ConnectorGraphicalRepresentation<DiagramConnector>();
		newGR.setsWith(grToUse);
		newConnector.setGraphicalRepresentation(newGR);

		parent.addToChilds(newConnector);

		// Register reference
		newConnector.registerEditionPatternReference(action.getEditionPatternInstance());

		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Added connector " + newConnector + " under " + parent);
		}
		return newConnector;
	}

	@Override
	public void finalizePerformAction(EditionSchemeAction action, DiagramConnector newConnector) {
		// Be sure that the newly created shape is updated
		newConnector.update();
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
				for (ConnectorPatternRole pr : action.getEditionPattern().getConnectorPatternRoles()) {
					v.add(new SetsPatternRole(pr));
				}
				return new ValidationError<AddConnectorActionMustAdressAValidConnectorPatternRole, AddConnector>(this, action,
						"add_connector_action_does_not_address_a_valid_connector_pattern_role", v);
			}
			return null;
		}

		protected static class SetsPatternRole extends FixProposal<AddConnectorActionMustAdressAValidConnectorPatternRole, AddConnector> {

			private ConnectorPatternRole patternRole;

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
			if (action.getPatternRole() != null && action.getPatternRole().getStartShapeAsDefinedInAction()
					&& !(action.getFromShape().isSet() && action.getFromShape().isValid())) {
				Vector<FixProposal<AddConnectorActionMustHaveAValidStartingShape, AddConnector>> v = new Vector<FixProposal<AddConnectorActionMustHaveAValidStartingShape, AddConnector>>();
				if (action.getEditionScheme() instanceof LinkScheme) {
					EditionPattern targetEditionPattern = ((LinkScheme) action.getEditionScheme()).getFromTargetEditionPattern();
					if (targetEditionPattern != null) {
						for (ShapePatternRole pr : action.getEditionPattern().getShapePatternRoles()) {
							v.add(new SetsStartingShapeToStartTargetShape(targetEditionPattern, pr));
						}
					}
				}
				for (ShapePatternRole pr : action.getEditionPattern().getShapePatternRoles()) {
					v.add(new SetsStartingShapeToShape(pr));
				}
				return new ValidationError<AddConnectorActionMustHaveAValidStartingShape, AddConnector>(this, action,
						"add_connector_action_does_not_have_a_valid_starting_shape", v);
			}
			return null;
		}

		protected static class SetsStartingShapeToShape extends FixProposal<AddConnectorActionMustHaveAValidStartingShape, AddConnector> {

			private ShapePatternRole patternRole;

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

			private EditionPattern target;
			private ShapePatternRole patternRole;

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
				action.setFromShape(new DataBinding<DiagramShape>(DiagramEditionScheme.FROM_TARGET + "."
						+ patternRole.getPatternRoleName()));
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
			if (action.getPatternRole() != null && action.getPatternRole().getEndShapeAsDefinedInAction()
					&& !(action.getToShape().isSet() && action.getToShape().isValid())) {
				Vector<FixProposal<AddConnectorActionMustHaveAValidEndingShape, AddConnector>> v = new Vector<FixProposal<AddConnectorActionMustHaveAValidEndingShape, AddConnector>>();
				if (action.getEditionScheme() instanceof LinkScheme) {
					EditionPattern targetEditionPattern = ((LinkScheme) action.getEditionScheme()).getToTargetEditionPattern();
					if (targetEditionPattern != null) {
						for (ShapePatternRole pr : action.getEditionPattern().getShapePatternRoles()) {
							v.add(new SetsEndingShapeToToTargetShape(targetEditionPattern, pr));
						}
					}
				}
				for (ShapePatternRole pr : action.getEditionPattern().getShapePatternRoles()) {
					v.add(new SetsEndingShapeToShape(pr));
				}
				return new ValidationError<AddConnectorActionMustHaveAValidEndingShape, AddConnector>(this, action,
						"add_connector_action_does_not_have_a_valid_ending_shape", v);
			}
			return null;
		}

		protected static class SetsEndingShapeToShape extends FixProposal<AddConnectorActionMustHaveAValidEndingShape, AddConnector> {

			private ShapePatternRole patternRole;

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

			private EditionPattern target;
			private ShapePatternRole patternRole;

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
