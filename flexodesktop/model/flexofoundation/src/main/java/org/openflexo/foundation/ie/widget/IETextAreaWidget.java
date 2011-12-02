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

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.openflexo.foundation.InvalidArgumentException;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.IObject;
import org.openflexo.foundation.ie.dm.IEDataModification;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.xml.FlexoComponentBuilder;
import org.openflexo.toolbox.StringUtils;

/**
 * Represents a 'TextArea' widget
 * 
 * @author bmangez
 */
public class IETextAreaWidget extends IEEditableTextWidget implements ExtensibleWidget {
	/**
     * 
     */
	public static final String TEXTAREA_WIDGET = "textarea_widget";
	private int _rows = 4;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	public IETextAreaWidget(FlexoComponentBuilder builder) {
		this(builder.woComponent, null, builder.getProject());
		initializeDeserialization(builder);
	}

	public IETextAreaWidget(IEWOComponent woComponent, IEObject parent, FlexoProject prj) {
		super(woComponent, parent, prj);
	}

	@Override
	public String getDefaultInspectorName() {
		return "TextArea.inspector";
	}

	public int getRows() {
		return _rows;
	}

	public void setRows(int _rows) throws InvalidArgumentException {
		if (_rows < 1) {
			throw new InvalidArgumentException("Negative numbers are not valid for rows number: " + _rows, null);
		}
		this._rows = _rows;
		setChanged();
		notifyObservers(new IEDataModification("rows", null, new Integer(_rows)));
	}

	/**
	 * Return a Vector of embedded IEObjects at this level. NOTE that this is NOT a recursive method
	 * 
	 * @return a Vector of IEObject instances
	 */
	@Override
	public Vector<IObject> getEmbeddedIEObjects() {
		return EMPTY_IOBJECT_VECTOR;
	}

	@Override
	public String getFullyQualifiedName() {
		return getWOComponent().getName() + "." + getName() + "(TextArea)";
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return TEXTAREA_WIDGET;
	}

	/**
	 * Overrides isDate
	 * 
	 * @see org.openflexo.foundation.ie.widget.IEEditableTextWidget#isDate()
	 */
	@Override
	public boolean isDate() {
		return false;
	}

	/**
	 * Overrides isNumber
	 * 
	 * @see org.openflexo.foundation.ie.widget.IEEditableTextWidget#isNumber()
	 */
	@Override
	public boolean isNumber() {
		return false;
	}

	/**
	 * Overrides isText
	 * 
	 * @see org.openflexo.foundation.ie.widget.IEEditableTextWidget#isText()
	 */
	@Override
	public boolean isText() {
		return true;
	}

	/**
	 * @see org.openflexo.foundation.ie.widget.IEWidgetWithValueList#getValueList(org.openflexo.foundation.wkf.FlexoProcess)
	 */
	@Override
	public List<Object> getValueList(FlexoProcess process) {
		List<Object> result = new ArrayList<Object>();
		if (!StringUtils.isEmpty(getValue())) {
			result.add(getValue());
		}
		return result;
	}

}
