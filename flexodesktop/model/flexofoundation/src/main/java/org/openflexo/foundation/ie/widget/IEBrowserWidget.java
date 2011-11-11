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

import java.util.Vector;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.openflexo.foundation.bindings.WidgetBindingDefinition;
import org.openflexo.foundation.bindings.BindingDefinition.BindingDefinitionType;
import org.openflexo.foundation.dkv.Domain;
import org.openflexo.foundation.dm.DMType;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.IObject;
import org.openflexo.foundation.ie.util.DropDownType;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.xml.FlexoComponentBuilder;

public class IEBrowserWidget extends IEAbstractListWidget implements ListModel {

	/**
     * 
     */
	public static final String BROWSER_WIDGET = "browser_widget";

	private Vector<ListDataListener> _listDataListener;

	private int visibleRows = 5;

	private WidgetBindingDefinition _bindingSelectionDefinition = null;

	public IEBrowserWidget(FlexoComponentBuilder builder) {
		this(builder.woComponent, null, builder.getProject());
		initializeDeserialization(builder);
	}

	public IEBrowserWidget(IEWOComponent woComponent, IEObject parent, FlexoProject prj) {
		super(woComponent, parent, prj);
		_listDataListener = new Vector<ListDataListener>();
	}

	@Override
	public String getDefaultInspectorName() {
		return "Browser.inspector";
	}

	@Override
	public Vector<IObject> getEmbeddedIEObjects() {
		return new Vector<IObject>();
	}

	@Override
	public String getFullyQualifiedName() {
		return "Browser-" + getName();
	}

	@Override
	public void addListDataListener(ListDataListener l) {
		if (!_listDataListener.contains(l))
			_listDataListener.add(l);
	}

	@Override
	public Object getElementAt(int index) {
		try {
			return getValueList().get(index);
		} catch (RuntimeException e) {
			return null;
		}
	}

	@Override
	public void setContentType(DMType contentType) {
		_bindingSelectionDefinition = null;
		super.setContentType(contentType);
	}

	@Override
	public WidgetBindingDefinition getBindingSelectionDefinition() {
		if (_bindingSelectionDefinition == null) {
			_bindingSelectionDefinition = new WidgetBindingDefinition("bindingSelection", DMType.makeListDMType(getContentType(),
					getProject()), this, BindingDefinitionType.GET_SET, true);
			if (getBindingSelection() != null)
				getBindingSelection().setBindingDefinition(_bindingSelectionDefinition);
		}
		return _bindingSelectionDefinition;
	}

	@Override
	public int getSize() {
		return getValueList().size();
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		_listDataListener.remove(l);
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return BROWSER_WIDGET;
	}

	/**
	 * Overrides setExampleList
	 * 
	 * @see org.openflexo.foundation.ie.widget.IEAbstractListWidget#setExampleList(java.lang.String)
	 */
	@Override
	public void setExampleList(String list) {
		super.setExampleList(list);
		notifyListDataListeners();
	}

	/**
	 * Overrides setDomain
	 * 
	 * @see org.openflexo.foundation.ie.widget.IEAbstractListWidget#setDomain(org.openflexo.foundation.dkv.Domain)
	 */
	@Override
	public void setDomain(Domain domain) {
		super.setDomain(domain);
		notifyListDataListeners();
	}

	/**
	 * Overrides setDropdownType
	 * 
	 * @see org.openflexo.foundation.ie.widget.IEAbstractListWidget#setDropdownType(org.openflexo.foundation.ie.util.DropDownType)
	 */
	@Override
	public void setDropdownType(DropDownType type) {
		super.setDropdownType(type);
		notifyListDataListeners();
	}

	private void notifyListDataListeners() {
		for (ListDataListener listener : _listDataListener) {
			listener.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, getValueList().size()));
		}
	}

	public int getVisibleRows() {
		return visibleRows;
	}

	public void setVisibleRows(int visibleRows) {
		int old = this.visibleRows;
		this.visibleRows = visibleRows;
		setChanged();
		notifyModification("visibleRows", old, visibleRows);
	}

}
