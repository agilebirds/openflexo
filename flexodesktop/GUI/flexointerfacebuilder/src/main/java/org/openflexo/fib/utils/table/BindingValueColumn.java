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
package org.openflexo.fib.utils.table;

import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.fib.utils.BindingSelector;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public abstract class BindingValueColumn<D extends Observable> extends CustomColumn<D, DataBinding> {

	protected static final Logger logger = Logger.getLogger(BindingValueColumn.class.getPackage().getName());

	public BindingValueColumn(String title, int defaultWidth, boolean allowsCompoundBinding) {
		super(title, defaultWidth);
	}

	public abstract boolean allowsCompoundBinding(DataBinding<?> value);

	public abstract boolean allowsNewEntryCreation(DataBinding<?> value);

	@Override
	public Class<DataBinding> getValueClass() {
		return DataBinding.class;
	}

	private BindingSelector _viewSelector;

	private BindingSelector _editSelector;

	private void updateSelectorWith(BindingSelector selector, D rowObject, DataBinding<?> value) {
		DataBinding oldBV = selector.getEditedObject();
		if (oldBV == null || !oldBV.equals(value)) {
			// logger.info("updateSelectorWith value=" + (value != null ? value + " (" + value.getBindingName() + ")" : "null"));
			// selector.setEditedObjectAndUpdateBDAndOwner(value);
			selector.setEditedObject(value);
			selector.setRevertValue(value != null ? value.clone() : null);
			/*if (allowsNewEntryCreation(value)) {
			    selector.allowsNewEntryCreation();
			}
			else {
			    selector.denyNewEntryCreation();
			}*/
			selector.setBindable(getBindableFor(value, rowObject));
			selector.setBindingDefinition(getBindingDefinitionFor(value, rowObject));
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Selector: " + selector.toString() + " rowObject=" + rowObject + "" + " binding=" + value
						+ " with BindingDefinition " + getBindingDefinitionFor(value, rowObject));
			}
		}
	}

	public abstract Bindable getBindableFor(DataBinding<?> value, D rowObject);

	public abstract BindingDefinition getBindingDefinitionFor(DataBinding<?> value, D rowObject);

	@Override
	protected BindingSelector getViewSelector(D rowObject, DataBinding value) {
		if (_viewSelector == null) {
			_viewSelector = new BindingSelector(value) {
				@Override
				public String toString() {
					return "VIEW";
				}
			};
			_viewSelector.setFont(MEDIUM_FONT);
		}
		updateSelectorWith(_viewSelector, rowObject, value);
		return _viewSelector;
	}

	@Override
	protected BindingSelector getEditSelector(D rowObject, DataBinding value) {
		if (_editSelector == null) {
			_editSelector = new BindingSelector(value) {
				@Override
				public void apply() {
					super.apply();
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Apply");
					}
					if (_editedRowObject != null) {
						setValue(_editedRowObject, getEditedObject());
					}
				}

				@Override
				public void cancel() {
					super.cancel();
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Cancel");
					}
					if (_editedRowObject != null) {
						setValue(_editedRowObject, getRevertValue());
					}
				}

				/*@Override
				public void setEditedObject(AbstractBinding object) {
				   setBindable(getBindableFor(object,_editedRowObject));
				   setBindingDefinition(getBindingDefinitionFor(object,_editedRowObject));
					super.setEditedObject(object);
				}*/
				@Override
				public String toString() {
					return "EDIT";
				}
			};
			_editSelector.setFont(NORMAL_FONT);
		}
		logger.info("Build EditSelector for " + rowObject + " value=" + value);
		updateSelectorWith(_editSelector, rowObject, value);
		return _editSelector;
	}

}
