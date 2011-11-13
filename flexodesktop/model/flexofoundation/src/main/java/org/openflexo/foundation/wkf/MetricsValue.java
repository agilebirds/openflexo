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

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.toolbox.Duration;
import org.openflexo.xmlcode.StringRepresentable;

import org.openflexo.foundation.utils.FlexoModelObjectReference;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.wkf.MetricsDefinition.MetricsType;
import org.openflexo.foundation.xml.FlexoProcessBuilder;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.logging.FlexoLogger;

public class MetricsValue extends WKFObject implements InspectableObject, FlexoModelObjectReference.ReferenceOwner, StringRepresentable {

	private static final Logger logger = FlexoLogger.getLogger(MetricsValue.class.getPackage().getName());

	public static interface MetricsValueOwner {
		public FlexoProcess getProcess();

		public Vector<MetricsValue> getMetricsValues();

		public void addToMetricsValues(MetricsValue value);

		public void removeFromMetricsValues(MetricsValue value);

		public void updateMetricsValues();
	}

	private FlexoModelObjectReference<MetricsDefinition> metricsDefinitionReference;

	private String stringValue;
	private Integer intValue;
	private Duration durationValue;
	private Boolean booleanValue;
	private Double doubleValue;

	private String unit;

	private MetricsValueOwner owner;

	public MetricsValue(FlexoProcess process) {
		super(process);
	}

	public MetricsValue(FlexoProcessBuilder builder) {
		this(builder.process);
	}

	@Override
	public void delete() {
		if (metricsDefinitionReference != null)
			metricsDefinitionReference.delete();
		if (owner != null)
			owner.removeFromMetricsValues(this);
		metricsDefinitionReference = null;
		owner = null;
		super.delete();
		deleteObservers();
	}

	public boolean hasValue() {
		if (getValue() == null)
			return false;
		if (getMetricsDefinition() != null && getMetricsDefinition().getType() == MetricsType.TIME) {
			return getDurationValue().isValid();
		}
		return true;
	}

	public Object getValue() {
		if (getMetricsDefinition() != null) {
			switch (getMetricsDefinition().getType()) {
			case TEXT:
				return getStringValue();
			case NUMBER:
				return getIntValue();
			case DOUBLE:
				return getDoubleValue();
			case TIME:
				return getDurationValue();
			case TRUE_FALSE:
				return getBooleanValue();
			default:
				break;
			}
		} else {
			if (logger.isLoggable(Level.WARNING))
				logger.warning("Metrics value has no definition");
		}
		return null;
	}

	public void setValue(Object value) {
		Object old = getValue();
		switch (getMetricsDefinition().getType()) {
		case TEXT:
			if (value instanceof String)
				setStringValue((String) value);
			break;
		case NUMBER:
			if (value instanceof Integer)
				setIntValue((Integer) value);
			break;
		case DOUBLE:
			if (value instanceof Double)
				setDoubleValue((Double) value);
			break;
		case TIME:
			if (value instanceof Duration)
				setDurationValue((Duration) value);
			break;
		case TRUE_FALSE:
			if (value instanceof Boolean)
				setBooleanValue((Boolean) value);
			break;
		default:
			break;
		}
		setChanged();
		notifyAttributeModification("value", old, value);
	}

	@Override
	public Vector<Validable> getAllEmbeddedValidableObjects() {
		Vector<Validable> v = new Vector<Validable>();
		v.add(this);
		return v;
	}

	public FlexoModelObjectReference<MetricsDefinition> getMetricsDefinitionReference() {
		return metricsDefinitionReference;
	}

	public void setMetricsDefinitionReference(FlexoModelObjectReference<MetricsDefinition> objectReference) {
		this.metricsDefinitionReference = objectReference;
		if (this.metricsDefinitionReference != null)
			this.metricsDefinitionReference.setOwner(this);
	}

	public MetricsDefinition getMetricsDefinition() {
		return getMetricsDefinition(true);
	}

	public MetricsDefinition getMetricsDefinition(boolean forceResourceLoad) {
		if (getMetricsDefinitionReference() != null)
			return getMetricsDefinitionReference().getObject(forceResourceLoad);
		else
			return null;
	}

	public void setMetricsDefinition(MetricsDefinition object) {
		if (metricsDefinitionReference != null) {
			metricsDefinitionReference.delete();
			metricsDefinitionReference = null;
		}
		if (object != null) {
			metricsDefinitionReference = new FlexoModelObjectReference<MetricsDefinition>(getProject(), object);
			metricsDefinitionReference.setOwner(this);
		} else
			metricsDefinitionReference = null;
	}

	public MetricsValue getThis() {
		return this;
	}

	public void setThis(MetricsValue value) {
		// Little hack for inspector
	}

	@Override
	public String getClassNameKey() {
		return "metrics_value";
	}

	@Override
	public String getFullyQualifiedName() {
		return "METRICS_VALUE." + getName();
	}

	@Override
	public Vector<? extends WKFObject> getAllEmbeddedDeleted() {
		return getAllEmbeddedWKFObjects();
	}

	@Override
	public Vector<? extends WKFObject> getAllEmbeddedWKFObjects() {
		Vector<WKFObject> v = new Vector<WKFObject>();
		v.add(this);
		return v;
	}

	@Override
	public String getInspectorName() {
		return null;
	}

	@Override
	public void notifyObjectLoaded(FlexoModelObjectReference reference) {

	}

	@Override
	public void objectCantBeFound(FlexoModelObjectReference reference) {

	}

	@Override
	public void objectDeleted(FlexoModelObjectReference reference) {
		if (reference == metricsDefinitionReference) {
			if (owner != null)
				owner.removeFromMetricsValues(this);
			reference.delete();
		}
	}

	public MetricsValueOwner getOwner() {
		return owner;
	}

	public void setOwner(MetricsValueOwner owner) {
		this.owner = owner;
	}

	public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
		setChanged();
		notifyAttributeModification("stringValue", null, stringValue);
	}

	public Integer getIntValue() {
		if (!isSerializing()) {
			if (intValue == null && doubleValue != null)
				intValue = doubleValue.intValue();
		}
		return intValue;
	}

	public void setIntValue(Integer intValue) {
		if (this.intValue == null && intValue != null) {
			if (!isDeserializing() && getMetricsDefinition() != null)
				setUnit(getMetricsDefinition().getUnit());
		}
		this.intValue = intValue;
		setChanged();
		notifyAttributeModification("intValue", null, intValue);
	}

	public Double getDoubleValue() {
		if (!isSerializing()) {
			if (doubleValue == null && intValue != null)
				doubleValue = intValue.doubleValue();
		}
		return doubleValue;
	}

	public void setDoubleValue(Double doubleValue) {
		if (this.doubleValue == null && doubleValue != null) {
			if (!isDeserializing() && getMetricsDefinition() != null)
				setUnit(getMetricsDefinition().getUnit());
		}
		this.doubleValue = doubleValue;
		setChanged();
		notifyAttributeModification("doubleValue", null, doubleValue);
	}

	public Duration getDurationValue() {
		return durationValue;
	}

	public void setDurationValue(Duration durationValue) {
		this.durationValue = durationValue;
		setChanged();
		notifyAttributeModification("durationValue", null, durationValue);
	}

	public Boolean getBooleanValue() {
		return booleanValue;
	}

	public void setBooleanValue(Boolean booleanValue) {
		this.booleanValue = booleanValue;
		setChanged();
		notifyAttributeModification("booleanValue", null, booleanValue);
	}

	public String getUnit() {
		if (!isSerializing()) {
			if (getMetricsDefinition().getType().isUnitEditable()) {
				if (unit == null)
					return getMetricsDefinition().getUnit();
				else
					return unit;
			} else
				return null;
		}
		return unit;
	}

	public void setUnit(String unit) {
		String old = unit;
		this.unit = unit;
		setChanged();
		notifyAttributeModification("unit", old, unit);
	}

	@Override
	public String toString() {
		if (getValue() == null)
			return "";
		switch (getMetricsDefinition().getType()) {
		case NUMBER:
			return getIntValue().toString() + getUnit() != null ? (" " + getUnit()) : "";
		case DOUBLE:
			return getDoubleValue().toString() + getUnit() != null ? (" " + getUnit()) : "";
		case TEXT:
			return getStringValue();
		case TIME:
			return getDurationValue().toString();
		case TRUE_FALSE:
			return getBooleanValue().toString();
		default:
			break;
		}
		return super.toString();
	}

	@Override
	public void objectSerializationIdChanged(FlexoModelObjectReference reference) {
		setChanged();
	}
}
