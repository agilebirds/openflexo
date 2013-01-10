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

import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.DataBinding.BindingDefinitionType;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.foundation.technologyadapter.FlexoModel;
import org.openflexo.foundation.validation.FixProposal;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.view.diagram.model.ViewConnector;
import org.openflexo.foundation.view.diagram.model.ViewElement;
import org.openflexo.foundation.view.diagram.model.ViewObject;
import org.openflexo.foundation.view.diagram.model.ViewShape;
import org.openflexo.foundation.view.diagram.viewpoint.ConnectorPatternRole;
import org.openflexo.foundation.view.diagram.viewpoint.GraphicalElementPatternRole;
import org.openflexo.foundation.view.diagram.viewpoint.GraphicalFeature;
import org.openflexo.foundation.view.diagram.viewpoint.ShapePatternRole;
import org.openflexo.foundation.viewpoint.EditionAction;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.foundation.viewpoint.binding.ViewPointDataBinding;

public class GraphicalAction<M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>> extends EditionAction<M, MM, ViewElement> {

	private static final Logger logger = Logger.getLogger(GraphicalAction.class.getPackage().getName());

	private GraphicalFeature<?, ?> graphicalFeature = null;
	private ViewPointDataBinding value;
	private BindingDefinition VALUE = new BindingDefinition("value", Object.class, DataBinding.BindingDefinitionType.GET, true) {
		@Override
		public java.lang.reflect.Type getType() {
			if (getGraphicalFeature() != null) {
				return getGraphicalFeature().getType();
			}
			return Object.class;
		}
	};

	public GraphicalAction(ViewPointBuilder builder) {
		super(builder);
	}

	@Override
	public EditionActionType getEditionActionType() {
		return EditionActionType.GraphicalAction;
	}

	public Object getValue(EditionSchemeAction action) {
		return getValue().getBindingValue(action);
	}

	public BindingDefinition getValueBindingDefinition() {
		return VALUE;
	}

	public ViewPointDataBinding getValue() {
		if (value == null) {
			value = new ViewPointDataBinding(this, EditionActionBindingAttribute.value, getValueBindingDefinition());
		}
		return value;
	}

	public void setValue(ViewPointDataBinding value) {
		value.setOwner(this);
		value.setBindingAttribute(EditionActionBindingAttribute.object);
		value.setBindingDefinition(getValueBindingDefinition());
		this.value = value;
	}

	public GraphicalFeature getGraphicalFeature() {
		if (graphicalFeature == null) {
			if (_graphicalFeatureName != null) {
				for (GraphicalFeature<?, ?> GF : getAvailableGraphicalFeatures()) {
					if (GF.getName().equals(_graphicalFeatureName)) {
						return GF;
					}
				}
			}
		}
		return graphicalFeature;
	}

	public void setGraphicalFeature(GraphicalFeature graphicalFeature) {
		this.graphicalFeature = graphicalFeature;
	}

	private List<GraphicalFeature<?, ?>> availableFeatures = null;

	/*@Override
	public GraphicalElementPatternRole getPatternRole() {
		try {
			return super.getPatternRole();
		} catch (ClassCastException e) {
			logger.warning("Unexpected pattern role type");
			setPatternRole(null);
			return null;
		}
	}

	@Override
	public void setPatternRole(GraphicalElementPatternRole patternRole) {
		System.out.println("set pattern role with " + patternRole);
		super.setPatternRole(patternRole);
		availableFeatures = null;
	}*/

	public List<GraphicalFeature<?, ?>> getAvailableGraphicalFeatures() {
		if (availableFeatures == null) {
			availableFeatures = new Vector<GraphicalFeature<?, ?>>();
			if (getSubject().isSet() && getSubject().isValid()) {
				Class accessedClass = TypeUtils.getBaseClass(getSubject().getBinding().getAccessedType());
				if (ViewObject.class.isAssignableFrom(accessedClass)) {
					for (GraphicalFeature<?, ?> GF : GraphicalElementPatternRole.AVAILABLE_FEATURES) {
						availableFeatures.add(GF);
					}
					if (ViewShape.class.isAssignableFrom(accessedClass)) {
						for (GraphicalFeature<?, ?> GF : ShapePatternRole.AVAILABLE_FEATURES) {
							availableFeatures.add(GF);
						}
					}
					if (ViewConnector.class.isAssignableFrom(accessedClass)) {
						for (GraphicalFeature<?, ?> GF : ConnectorPatternRole.AVAILABLE_FEATURES) {
							availableFeatures.add(GF);
						}
					}
				}
			}
		}
		return availableFeatures;
	}

	private String _graphicalFeatureName = null;

	public String _getGraphicalFeatureName() {
		if (getGraphicalFeature() == null) {
			return _graphicalFeatureName;
		}
		return getGraphicalFeature().getName();
	}

	public void _setGraphicalFeatureName(String featureName) {
		_graphicalFeatureName = featureName;
	}

	private ViewPointDataBinding subject;

	private BindingDefinition SUBJECT = new BindingDefinition("subject", ViewElement.class, DataBinding.BindingDefinitionType.GET, true);

	public BindingDefinition getSubjectBindingDefinition() {
		return SUBJECT;
	}

	public ViewPointDataBinding getSubject() {
		if (subject == null) {
			subject = new ViewPointDataBinding(this, EditionActionBindingAttribute.subject, getSubjectBindingDefinition());
		}
		return subject;
	}

	public void setSubject(ViewPointDataBinding subject) {
		if (subject != null) {
			subject.setOwner(this);
			subject.setBindingAttribute(EditionActionBindingAttribute.subject);
			subject.setBindingDefinition(getSubjectBindingDefinition());
		}
		this.subject = subject;
	}

	public ViewElement getSubject(EditionSchemeAction action) {
		return (ViewElement) getSubject().getBindingValue(action);
	}

	/*@Deprecated
	public String _getPatternRoleName() {
		return getSubject().toString();
	}

	@Deprecated
	public void _setPatternRoleName(String patternRole) {
		getSubject().setUnparsedBinding(patternRole);
	}*/

	@Override
	public void notifyBindingChanged(ViewPointDataBinding binding) {
		super.notifyBindingChanged(binding);
		if (binding == getSubject()) {
			availableFeatures = null;
		}
	}

	@Override
	public String getStringRepresentation() {
		return getClass().getSimpleName() + " (" + getSubject() + "." + _getGraphicalFeatureName() + "=" + getValue() + ")";
	}

	@Override
	public ViewElement performAction(EditionSchemeAction action) {
		logger.info("Perform graphical action " + action);
		ViewElement graphicalElement = getSubject(action);
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Element is " + graphicalElement);
			logger.fine("Feature is " + getGraphicalFeature());
			logger.fine("Value is " + getValue().getBindingValue(action));
		}
		getGraphicalFeature().applyToGraphicalRepresentation(graphicalElement.getGraphicalRepresentation(),
				getValue().getBindingValue(action));
		return graphicalElement;
	}

	@Override
	public void finalizePerformAction(EditionSchemeAction action, ViewElement initialContext) {
	}

	public static class GraphicalActionMustHaveASubject extends ValidationRule<GraphicalActionMustHaveASubject, GraphicalAction> {
		public GraphicalActionMustHaveASubject() {
			super(GraphicalAction.class, "graphical_action_must_have_a_subject");
		}

		@Override
		public ValidationIssue<GraphicalActionMustHaveASubject, GraphicalAction> applyValidation(GraphicalAction graphicalAction) {
			if (graphicalAction.getSubject().isSet() && graphicalAction.getSubject().isValid()) {
				return null;
			} else {
				Vector<FixProposal<GraphicalActionMustHaveASubject, GraphicalAction>> v = new Vector<FixProposal<GraphicalActionMustHaveASubject, GraphicalAction>>();
				for (ShapePatternRole pr : graphicalAction.getEditionPattern().getShapePatternRoles()) {
					v.add(new SetsPatternRoleForSubject(pr));
				}
				for (ConnectorPatternRole pr : graphicalAction.getEditionPattern().getConnectorPatternRoles()) {
					v.add(new SetsPatternRoleForSubject(pr));
				}
				return new ValidationError<GraphicalActionMustHaveASubject, GraphicalAction>(this, graphicalAction,
						"graphical_action_has_no_valid_subject", v);
			}
		}

		protected static class SetsPatternRoleForSubject extends FixProposal<GraphicalActionMustHaveASubject, GraphicalAction> {

			private GraphicalElementPatternRole patternRole;

			public SetsPatternRoleForSubject(GraphicalElementPatternRole patternRole) {
				super("set_subject_to_($patternRole.patternRoleName)");
				this.patternRole = patternRole;
			}

			public GraphicalElementPatternRole getPatternRole() {
				return patternRole;
			}

			@Override
			protected void fixAction() {
				GraphicalAction graphicalAction = getObject();
				graphicalAction.setSubject(new ViewPointDataBinding(patternRole.getPatternRoleName()));
			}

		}
	}

	public static class GraphicalActionMustDefineAValue extends BindingIsRequiredAndMustBeValid<GraphicalAction> {
		public GraphicalActionMustDefineAValue() {
			super("'value'_binding_is_not_valid", GraphicalAction.class);
		}

		@Override
		public ViewPointDataBinding getBinding(GraphicalAction object) {
			return object.getValue();
		}

		@Override
		public BindingDefinition getBindingDefinition(GraphicalAction object) {
			return object.getValueBindingDefinition();
		}

	}

}
