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
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.binding.ViewPointDataBinding;

public class GraphicalAction extends EditionAction<GraphicalElementPatternRole> {

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

	@Override
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
	}

	public List<GraphicalFeature<?, ?>> getAvailableGraphicalFeatures() {
		if (availableFeatures == null) {
			availableFeatures = new Vector<GraphicalFeature<?, ?>>();
			if (getPatternRole() instanceof GraphicalElementPatternRole) {
				for (GraphicalFeature<?, ?> GF : GraphicalElementPatternRole.AVAILABLE_FEATURES) {
					availableFeatures.add(GF);
				}
				if (getPatternRole() instanceof ShapePatternRole) {
					for (GraphicalFeature<?, ?> GF : ShapePatternRole.AVAILABLE_FEATURES) {
						availableFeatures.add(GF);
					}
				}
				if (getPatternRole() instanceof ConnectorPatternRole) {
					for (GraphicalFeature<?, ?> GF : ConnectorPatternRole.AVAILABLE_FEATURES) {
						availableFeatures.add(GF);
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

}
