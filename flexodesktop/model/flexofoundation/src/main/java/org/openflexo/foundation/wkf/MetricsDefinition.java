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
package org.openflexo.foundation.wkf;

import java.util.Collection;
import java.util.Vector;

import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.xml.FlexoWorkflowBuilder;
import org.openflexo.inspector.InspectableObject;

public class MetricsDefinition extends WorkflowModelObject implements InspectableObject {

	private String name;
	private MetricsType type;
	private String unit;
	private boolean alwaysDefined = false;

	public static enum MetricsType {
		TEXT, NUMBER, DOUBLE, TIME, TRUE_FALSE; /*,
												FREQUENCY*/
		public boolean isUnitEditable() {
			return this == MetricsType.NUMBER || this == MetricsType.DOUBLE || this == MetricsType.TEXT;
		}
	}

	public MetricsDefinition(FlexoProject project, FlexoWorkflow workflow) {
		super(project, workflow);
	}

	public MetricsDefinition(FlexoWorkflowBuilder builder) {
		this(builder.workflow.getProject(), builder.workflow);
	}

	@Override
	public Vector<Validable> getAllEmbeddedValidableObjects() {
		Vector<Validable> v = new Vector<Validable>();
		v.add(this);
		return v;
	}

	@Override
	public boolean delete() {
		getWorkflow().removeFromMetricsDefinition(this);
		return super.delete();
	}

	@Override
	public String getClassNameKey() {
		return "metrics_definition";
	}

	@Override
	public String getFullyQualifiedName() {
		return "METRICS_DEFINITION." + getName();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		String old = this.name;
		this.name = name;
		setChanged();
		notifyAttributeModification("name", old, name);
	}

	public MetricsType getType() {
		return type;
	}

	public void setType(MetricsType type) {
		MetricsType old = this.type;
		this.type = type;
		setChanged();
		notifyAttributeModification("type", old, type);
	}

	public boolean isUnitEditable() {
		return getType().isUnitEditable();
	}

	@Override
	public String getInspectorName() {
		return null;
	}

	public boolean getAlwaysDefined() {
		return alwaysDefined;
	}

	public void setAlwaysDefined(boolean alwaysDefined) {
		this.alwaysDefined = alwaysDefined;
		setChanged();
		notifyAttributeModification("alwaysDefined", !alwaysDefined, alwaysDefined);
		if (getWorkflow() != null && !isDeserializing()) {
			getWorkflow().updateProcessModelObjects(this);
		}
	}

	public String getUnit() {
		if (!isUnitEditable()) {
			return null;
		}
		return unit;
	}

	public void setUnit(String unit) {
		String old = this.unit;
		this.unit = unit;
		setChanged();
		notifyAttributeModification("unit", old, unit);
	}

	public boolean isProcessMetricsDefinition() {
		if (getWorkflow() != null) {
			return getWorkflow().getProcessMetricsDefinitions().contains(this);
		}
		return false;
	}

	public boolean isActivityMetricsDefinition() {
		if (getWorkflow() != null) {
			return getWorkflow().getActivityMetricsDefinitions().contains(this);
		}
		return false;
	}

	public boolean isOperationMetricsDefinition() {
		if (getWorkflow() != null) {
			return getWorkflow().getOperationMetricsDefinitions().contains(this);
		}
		return false;
	}

	public boolean isEdgeMetricsDefinition() {
		if (getWorkflow() != null) {
			return getWorkflow().getEdgeMetricsDefinitions().contains(this);
		}
		return false;
	}

	public boolean isArtefactMetricsDefinition() {
		if (getWorkflow() != null) {
			return getWorkflow().getArtefactMetricsDefinitions().contains(this);
		}
		return false;
	}

	@Override
	public Collection<? extends Validable> getEmbeddedValidableObjects() {
		return null;
	}
}
