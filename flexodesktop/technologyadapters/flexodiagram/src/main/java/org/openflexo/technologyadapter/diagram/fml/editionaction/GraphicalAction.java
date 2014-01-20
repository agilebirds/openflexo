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
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.DataBinding.BindingDefinitionType;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.validation.FixProposal;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.EditionAction;
import org.openflexo.foundation.viewpoint.annotations.FIBPanel;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.diagram.TypedDiagramModelSlot;
import org.openflexo.technologyadapter.diagram.fml.ConnectorPatternRole;
import org.openflexo.technologyadapter.diagram.fml.GraphicalElementPatternRole;
import org.openflexo.technologyadapter.diagram.fml.GraphicalFeature;
import org.openflexo.technologyadapter.diagram.fml.ShapePatternRole;
import org.openflexo.technologyadapter.diagram.model.DiagramConnector;
import org.openflexo.technologyadapter.diagram.model.DiagramElement;
import org.openflexo.technologyadapter.diagram.model.DiagramShape;

@FIBPanel("Fib/GraphicalActionPanel.fib")
@ModelEntity
@ImplementationClass(GraphicalAction.GraphicalActionImpl.class)
@XMLElement
public interface GraphicalAction extends EditionAction<TypedDiagramModelSlot, DiagramElement<?>> {

	@PropertyIdentifier(type = DataBinding.class)
	public static final String SUBJECT_KEY = "subject";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String VALUE_KEY = "value";
	@PropertyIdentifier(type = String.class)
	public static final String GRAPHICAL_FEATURE_NAME_KEY = "graphicalFeatureName";

	@Getter(value = SUBJECT_KEY)
	@XMLAttribute
	public DataBinding<DiagramElement<?>> getSubject();

	@Setter(SUBJECT_KEY)
	public void setSubject(DataBinding<DiagramElement<?>> subject);

	@Getter(value = VALUE_KEY)
	@XMLAttribute
	public DataBinding<Object> getValue();

	@Setter(VALUE_KEY)
	public void setValue(DataBinding<Object> value);

	@Getter(value = GRAPHICAL_FEATURE_NAME_KEY)
	@XMLAttribute(xmlTag = "feature")
	public String _getGraphicalFeatureName();

	@Setter(GRAPHICAL_FEATURE_NAME_KEY)
	public void _setGraphicalFeatureName(String graphicalFeatureName);

	public static abstract class GraphicalActionImpl extends EditionActionImpl<TypedDiagramModelSlot, DiagramElement<?>> implements
			GraphicalAction {

		private static final Logger logger = Logger.getLogger(GraphicalAction.class.getPackage().getName());

		private GraphicalFeature<?, ?> graphicalFeature = null;
		private DataBinding<Object> value;

		public GraphicalActionImpl() {
			super();
		}

		public java.lang.reflect.Type getGraphicalFeatureType() {
			if (getGraphicalFeature() != null) {
				return getGraphicalFeature().getType();
			}
			return Object.class;
		}

		public Object getValue(EditionSchemeAction action) {
			try {
				return getValue().getBindingValue(action);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		public DataBinding<Object> getValue() {
			if (value == null) {
				value = new DataBinding<Object>(this, getGraphicalFeatureType(), BindingDefinitionType.GET);
				value.setBindingName("value");
			}
			return value;
		}

		@Override
		public void setValue(DataBinding<Object> value) {
			if (value != null) {
				value.setOwner(this);
				value.setBindingName("value");
				value.setDeclaredType(getGraphicalFeatureType());
				value.setBindingDefinitionType(BindingDefinitionType.GET);
			}
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
					Class accessedClass = TypeUtils.getBaseClass(getSubject().getAnalyzedType());
					if (DiagramElement.class.isAssignableFrom(accessedClass)) {
						for (GraphicalFeature<?, ?> GF : GraphicalElementPatternRole.AVAILABLE_FEATURES) {
							availableFeatures.add(GF);
						}
						if (DiagramShape.class.isAssignableFrom(accessedClass)) {
							for (GraphicalFeature<?, ?> GF : ShapePatternRole.AVAILABLE_FEATURES) {
								availableFeatures.add(GF);
							}
						}
						if (DiagramConnector.class.isAssignableFrom(accessedClass)) {
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

		@Override
		public String _getGraphicalFeatureName() {
			if (getGraphicalFeature() == null) {
				return _graphicalFeatureName;
			}
			return getGraphicalFeature().getName();
		}

		@Override
		public void _setGraphicalFeatureName(String featureName) {
			_graphicalFeatureName = featureName;
		}

		private DataBinding<DiagramElement<?>> subject;

		@Override
		public DataBinding<DiagramElement<?>> getSubject() {
			if (subject == null) {
				subject = new DataBinding<DiagramElement<?>>(this, DiagramElement.class, DataBinding.BindingDefinitionType.GET);
				subject.setBindingName("subject");
			}
			return subject;
		}

		@Override
		public void setSubject(DataBinding<DiagramElement<?>> subject) {
			if (subject != null) {
				subject.setOwner(this);
				subject.setBindingName("subject");
				subject.setDeclaredType(DiagramElement.class);
				subject.setBindingDefinitionType(BindingDefinitionType.GET);
			}
			this.subject = subject;
		}

		public DiagramElement getSubject(EditionSchemeAction action) {
			try {
				return getSubject().getBindingValue(action);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		public void notifiedBindingChanged(DataBinding<?> binding) {
			super.notifiedBindingChanged(binding);
			if (binding == getSubject()) {
				availableFeatures = null;
			}
		}

		@Override
		public String getStringRepresentation() {
			return getClass().getSimpleName() + " (" + getSubject() + "." + _getGraphicalFeatureName() + "=" + getValue() + ")";
		}

		@Override
		public DiagramElement performAction(EditionSchemeAction action) {
			logger.info("Perform graphical action " + action);
			DiagramElement graphicalElement = getSubject(action);
			Object value = null;
			try {
				value = getValue().getBindingValue(action);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Element is " + graphicalElement);
				logger.fine("Feature is " + getGraphicalFeature());
				logger.fine("Value is " + value);
			}
			getGraphicalFeature().applyToGraphicalRepresentation(graphicalElement.getGraphicalRepresentation(), value);
			return graphicalElement;
		}

		@Override
		public void finalizePerformAction(EditionSchemeAction action, DiagramElement initialContext) {
		}

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
				for (ShapePatternRole pr : graphicalAction.getEditionPattern().getPatternRoles(ShapePatternRole.class)) {
					v.add(new SetsPatternRoleForSubject(pr));
				}
				for (ConnectorPatternRole pr : graphicalAction.getEditionPattern().getPatternRoles(ConnectorPatternRole.class)) {
					v.add(new SetsPatternRoleForSubject(pr));
				}
				return new ValidationError<GraphicalActionMustHaveASubject, GraphicalAction>(this, graphicalAction,
						"graphical_action_has_no_valid_subject", v);
			}
		}

		protected static class SetsPatternRoleForSubject extends FixProposal<GraphicalActionMustHaveASubject, GraphicalAction> {

			private final GraphicalElementPatternRole patternRole;

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
				graphicalAction.setSubject(new DataBinding<DiagramElement<?>>(patternRole.getPatternRoleName()));
			}

		}
	}

	public static class GraphicalActionMustDefineAValue extends BindingIsRequiredAndMustBeValid<GraphicalAction> {
		public GraphicalActionMustDefineAValue() {
			super("'value'_binding_is_not_valid", GraphicalAction.class);
		}

		@Override
		public DataBinding<Object> getBinding(GraphicalAction object) {
			return object.getValue();
		}

	}

}
