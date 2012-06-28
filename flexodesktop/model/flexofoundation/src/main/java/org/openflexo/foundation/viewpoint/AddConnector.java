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
package org.openflexo.foundation.viewpoint;

import java.lang.reflect.Type;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.validation.FixProposal;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.view.ViewConnector;
import org.openflexo.foundation.view.ViewShape;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.view.action.LinkSchemeAction;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.foundation.viewpoint.binding.ViewPointDataBinding;

public class AddConnector extends AddShemaElementAction {

	private static final Logger logger = Logger.getLogger(LinkSchemeAction.class.getPackage().getName());

	public AddConnector(ViewPointBuilder builder) {
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
	public String getInspectorName() {
		return Inspectors.VPM.ADD_CONNECTOR_INSPECTOR;
	}

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

	public ViewShape getFromShape(EditionSchemeAction action) {
		if (getPatternRole() != null && !getPatternRole().getStartShapeAsDefinedInAction()) {
			FlexoModelObject returned = action.getEditionPatternInstance().getPatternActor(getPatternRole().getStartShapePatternRole());
			return (ViewShape) action.getEditionPatternInstance().getPatternActor(getPatternRole().getStartShapePatternRole());
		} else {
			return (ViewShape) getFromShape().getBindingValue(action);
		}
	}

	public ViewShape getToShape(EditionSchemeAction action) {
		if (getPatternRole() != null && !getPatternRole().getEndShapeAsDefinedInAction()) {
			FlexoModelObject returned = action.getEditionPatternInstance().getPatternActor(getPatternRole().getEndShapePatternRole());
			return (ViewShape) action.getEditionPatternInstance().getPatternActor(getPatternRole().getEndShapePatternRole());
		} else {
			return (ViewShape) getToShape().getBindingValue(action);
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

	private ViewPointDataBinding fromShape;
	private ViewPointDataBinding toShape;

	private BindingDefinition FROM_SHAPE = new BindingDefinition("fromShape", ViewShape.class, BindingDefinitionType.GET, true);

	public BindingDefinition getFromShapeBindingDefinition() {
		return FROM_SHAPE;
	}

	public ViewPointDataBinding getFromShape() {
		if (fromShape == null) {
			fromShape = new ViewPointDataBinding(this, EditionActionBindingAttribute.fromShape, getFromShapeBindingDefinition());
		}
		return fromShape;
	}

	public void setFromShape(ViewPointDataBinding fromShape) {
		if (fromShape != null) {
			fromShape.setOwner(this);
			fromShape.setBindingAttribute(EditionActionBindingAttribute.fromShape);
			fromShape.setBindingDefinition(getFromShapeBindingDefinition());
		}
		this.fromShape = fromShape;
		notifyBindingChanged(this.fromShape);
	}

	private BindingDefinition TO_SHAPE = new BindingDefinition("toShape", ViewShape.class, BindingDefinitionType.GET, true);

	public BindingDefinition getToShapeBindingDefinition() {
		return TO_SHAPE;
	}

	public ViewPointDataBinding getToShape() {
		if (toShape == null) {
			toShape = new ViewPointDataBinding(this, EditionActionBindingAttribute.toShape, getToShapeBindingDefinition());
		}
		return toShape;
	}

	public void setToShape(ViewPointDataBinding toShape) {
		if (toShape != null) {
			toShape.setOwner(this);
			toShape.setBindingAttribute(EditionActionBindingAttribute.toShape);
			toShape.setBindingDefinition(getToShapeBindingDefinition());
		}
		this.toShape = toShape;
		notifyBindingChanged(this.toShape);
	}

	@Override
	public Type getAssignableType() {
		return ViewConnector.class;
	}

	@Override
	public boolean isAssignationRequired() {
		return true;
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
				action.setAssignation(new ViewPointDataBinding(patternRole.getPatternRoleName()));
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
				action.setFromShape(new ViewPointDataBinding(patternRole.getPatternRoleName()));
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
				action.setFromShape(new ViewPointDataBinding(EditionScheme.FROM_TARGET + "." + patternRole.getPatternRoleName()));
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
				action.setToShape(new ViewPointDataBinding(patternRole.getPatternRoleName()));
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
				action.setToShape(new ViewPointDataBinding(EditionScheme.TO_TARGET + "." + patternRole.getPatternRoleName()));
			}
		}

	}

}
