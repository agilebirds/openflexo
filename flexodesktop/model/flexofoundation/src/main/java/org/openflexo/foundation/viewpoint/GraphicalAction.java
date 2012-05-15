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

import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.foundation.view.ViewConnector;
import org.openflexo.foundation.view.ViewElement;
import org.openflexo.foundation.view.ViewObject;
import org.openflexo.foundation.view.ViewShape;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.binding.ViewPointDataBinding;

public class GraphicalAction extends EditionAction {

	private static final Logger logger = Logger.getLogger(GraphicalAction.class.getPackage().getName());

	private GraphicalFeature<?, ?> graphicalFeature = null;
	private ViewPointDataBinding value;
	private BindingDefinition VALUE = new BindingDefinition("value", Object.class, BindingDefinitionType.GET, false) {
		@Override
		public java.lang.reflect.Type getType() {
			if (getGraphicalFeature() != null) {
				return getGraphicalFeature().getType();
			}
			return Object.class;
		}
	};

	public GraphicalAction() {
	}

	@Override
	public EditionActionType getEditionActionType() {
		return EditionActionType.GraphicalAction;
	}

	/*@Override
	public List<GraphicalElementPatternRole> getAvailablePatternRoles() {
		return getEditionPattern().getPatternRoles(GraphicalElementPatternRole.class);
	}*/

	@Override
	public String getInspectorName() {
		// TODO Auto-generated method stub
		return null;
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

	private BindingDefinition SUBJECT = new BindingDefinition("subject", ViewElement.class, BindingDefinitionType.GET, false);

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

	@Override
	public void notifyBindingChanged(ViewPointDataBinding binding) {
		super.notifyBindingChanged(binding);
		if (binding == getSubject()) {
			availableFeatures = null;
		}
	}

}
