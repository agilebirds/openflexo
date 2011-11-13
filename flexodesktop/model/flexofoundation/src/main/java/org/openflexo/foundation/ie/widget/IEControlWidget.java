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
package org.openflexo.foundation.ie.widget;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.bindings.AbstractBinding;
import org.openflexo.foundation.bindings.WidgetBindingDefinition;
import org.openflexo.foundation.bindings.BindingDefinition.BindingDefinitionType;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.dm.IEDataModification;
import org.openflexo.foundation.ie.operator.RepetitionOperator;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.logging.FlexoLogger;

/**
 * Represent a dynamic controlled widget
 * 
 * @author sguerin
 * 
 */
public abstract class IEControlWidget extends AbstractInnerTableWidget {

	private static final Logger logger = FlexoLogger.getLogger(IEControlWidget.class.getPackage().getName());

	private boolean _exampleValueIsDefaultValue = false;

	private boolean _disabled = false;

	private AbstractBinding _bindingKeypath;

	private RepetitionOperator isFilterForRepetition;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	public IEControlWidget(IEWOComponent woComponent, IEObject parent, FlexoProject prj) {
		super(woComponent, parent, prj);
	}

	public boolean getDisabled() {
		return _disabled;
	}

	public void setDisabled(boolean disabled) {
		this._disabled = disabled;
		setChanged();
		notifyObservers(new IEDataModification("disabled", null, new Boolean(disabled)));
	}

	public boolean getExampleValueIsDefaultValue() {
		return _exampleValueIsDefaultValue;
	}

	public void setExampleValueIsDefaultValue(boolean exampleValueIsDefaultValue) {
		_exampleValueIsDefaultValue = exampleValueIsDefaultValue;
		setChanged();
		notifyObservers(new IEDataModification("exampleValueIsDefaultValue", null, new Boolean(exampleValueIsDefaultValue)));
	}

	public RepetitionOperator getIsFilterForRepetition() {
		if (isFilterForRepetition != null && isFilterForRepetition.isDeleted())
			isFilterForRepetition = null;
		return isFilterForRepetition;
	}

	public void setIsFilterForRepetition(RepetitionOperator repetition) {
		isFilterForRepetition = repetition;
		setChanged();
		notifyModification("isFilterForRepetition", null, repetition);
	}

	public AbstractBinding getBindingKeypath() {
		if (isBeingCloned())
			return null;
		return _bindingKeypath;
	}

	public void setBindingKeypath(AbstractBinding name) {
		_bindingKeypath = name;
		if (_bindingKeypath != null) {
			_bindingKeypath.setOwner(this);
			_bindingKeypath.setBindingDefinition(getBindingKeypathDefinition());
		}
		setChanged();
		notifyObservers(new IEDataModification("bindingKeypath", null, _bindingKeypath));
	}

	public WidgetBindingDefinition getBindingKeypathDefinition() {
		return WidgetBindingDefinition.get(this, "bindingKeypath", Object.class, BindingDefinitionType.GET, false);
	}

	@Override
	public boolean areComponentInstancesValid() {
		return true;
	}

	@Override
	public void removeInvalidComponentInstances() {
		if (logger.isLoggable(Level.FINEST))
			logger.finest("Verifying component instances for " + getClass().getName());
	}

	@Override
	public boolean generateJavascriptID() {
		return true;
	}
}
