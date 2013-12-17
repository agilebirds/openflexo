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
package org.openflexo.components.tabular.model;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.table.TableCellEditor;

import org.openflexo.FlexoCst;
import org.openflexo.components.widget.DMTypeSelector;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.dm.DMType;
import org.openflexo.foundation.dm.DMTypeOwner;
import org.openflexo.foundation.FlexoProject;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public abstract class TypeSelectorColumn<D extends FlexoModelObject> extends CustomColumn<D, DMType> {

	protected String STRING_REPRESENTATION_WHEN_NULL = "<null>";

	protected static final Logger logger = Logger.getLogger(TypeSelectorColumn.class.getPackage().getName());

	public TypeSelectorColumn(String title, int defaultWidth, FlexoProject project) {
		super(title, defaultWidth, project);
		_selectorCellEditor = new DMTypeSelectorEditor();
	}

	@Override
	public Class getValueClass() {
		return DMType.class;
	}

	public class DMTypeSelectorEditor extends CustomColumn.SelectorCellEditor {

		@Override
		public Object getCellEditorValue() {
			return _selector.getEditedObject() != null ? ((DMType) _selector.getEditedObject()).clone() : null;
		}
	}

	private DMTypeSelector _viewSelector;

	private DMTypeSelector _editSelector;

	private boolean _isReadOnly;

	@Override
	protected DMTypeSelector getViewSelector(D rowObject, DMType value) {
		if (_viewSelector == null) {
			_viewSelector = new DMTypeSelector(_project, null, true);
			_viewSelector.setFont(FlexoCst.MEDIUM_FONT);
		}
		_viewSelector.setEditedObject(value);
		_viewSelector.setOwner((DMTypeOwner) rowObject);
		return _viewSelector;
	}

	@Override
	protected DMTypeSelector getEditSelector(D rowObject, DMType value) {
		if (_editSelector == null) {
			_editSelector = new DMTypeSelector(_project, null, false) {
				@Override
				public void apply() {
					super.apply();
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Apply");
					}
					if (_editedRowObject != null) {
						if (getEditedObject() != null) {
							setValue(_editedRowObject, getEditedObject().clone());
						} else {
							setValue(_editedRowObject, null);
						}
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
			};
			_editSelector.setFont(FlexoCst.NORMAL_FONT);
		}
		_editSelector.setEditedObject(value);
		_editSelector.setRevertValue(value != null ? value.clone() : null);
		_editSelector.setOwner((DMTypeOwner) rowObject);
		return _editSelector;
	}

	@Override
	public TableCellEditor getCellEditor() {
		return super.getCellEditor();
	}

	public void setNullStringRepresentation(String aString) {
		STRING_REPRESENTATION_WHEN_NULL = aString;
	}

	public boolean isReadOnly() {
		return _isReadOnly;
	}

	// TODO: not implemented yet
	public void setReadOnly(boolean isReadOnly) {
		_isReadOnly = isReadOnly;
	}

	@Override
	protected void setEditedRowObject(D anObject) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("setEditedRowObject() with " + anObject);
		}
		super.setEditedRowObject(anObject);
		_viewSelector.setOwner((DMTypeOwner) _editedRowObject);
		_editSelector.setOwner((DMTypeOwner) _editedRowObject);
	}

}
