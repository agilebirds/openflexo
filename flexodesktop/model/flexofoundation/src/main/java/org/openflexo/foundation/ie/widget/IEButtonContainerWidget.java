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

import java.util.Enumeration;
import java.util.Vector;

import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.IObject;
import org.openflexo.foundation.ie.dm.ButtonAdded;
import org.openflexo.foundation.ie.dm.table.WidgetRemovedFromTable;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.xml.FlexoComponentBuilder;

/**
 * Represents an abstract container of buttons
 * 
 * @deprecated use ISequenceButton instead
 * @author bmangez
 */
@Deprecated
public class IEButtonContainerWidget extends AbstractInnerTableWidget implements ButtonContainerInterface {

	// ==========================================================================
	// ============================= Variables
	// ==================================
	// ==========================================================================

	private Vector<IEButtonWidget> _buttonList;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	public IEButtonContainerWidget(FlexoComponentBuilder builder) {
		this(builder.woComponent, null, builder.getProject());
		initializeDeserialization(builder);
	}

	public IEButtonContainerWidget(IEWOComponent woComponent, IEObject parent, FlexoProject prj) {
		super(woComponent, parent, prj);
		_buttonList = new Vector<IEButtonWidget>();
	}

	@Override
	public String getDefaultInspectorName() {
		return "ButtonContainer.inspector";
	}

	@Override
	public void performOnDeleteOperations() {
		deleteButtons();
		super.performOnDeleteOperations();
	}

	// ==========================================================================
	// ================================= Generic methods =======================
	// ==========================================================================

	public Vector<IEButtonWidget> getButtonList() {
		return _buttonList;
	}

	public void setButtonList(Vector<IEButtonWidget> list) {
		_buttonList = list;
	}

	public void addToButtonList(IEButtonWidget button) {
		addButton(button);
	}

	public void removeFromButtonList(IEButtonWidget button) {
		removeButton(button);
	}

	// ==========================================================================
	// ============================= Instance Methods
	// ===========================
	// ==========================================================================

	private void deleteButtons() {
		Enumeration en = _buttonList.elements();
		while (en.hasMoreElements()) {
			((IEWidget) en.nextElement()).delete();
		}
	}

	@Override
	public void removeButton(IEWidget buttonToRemove) {
		_buttonList.remove(buttonToRemove);
		if (_buttonList.size() == 0) {
			delete();
		} else {
			updateButtonIndex();
		}
		setChanged();
		notifyObservers(new WidgetRemovedFromTable(buttonToRemove));
	}

	public void addButton(IEButtonWidget button, int index) {
		_buttonList.insertElementAt(button, Math.max(Math.min(_buttonList.size(), index), 0));
		updateButtonIndex();
		setChanged();
		notifyObservers(new ButtonAdded(button));
	}

	public void addButton(IEButtonWidget button) {
		int insertionIndex = findInsertionIndex(_buttonList, ((Indexable) button).getIndex());
		_buttonList.insertElementAt(button, insertionIndex);
		button.setParent(this);
		setChanged();
		notifyObservers(new ButtonAdded(button));
	}

	public static int findInsertionIndex(Vector v, int wish) {
		int answer = 0;
		if (v == null || v.size() == 0)
			return answer;
		while (answer < v.size() && answer < wish && wish > wishFor(v.elementAt(answer)))
			answer++;
		return answer;
	}

	private static int wishFor(Object v) {
		return ((Indexable) v).getIndex();
	}

	public Enumeration buttons() {
		return _buttonList.elements();
	}

	public void updateButtonIndex() {
		Enumeration en = _buttonList.elements();
		int i = 0;
		while (en.hasMoreElements()) {
			((IEWidget) en.nextElement()).setIndex(i);
			i++;
		}
	}

	@Override
	public int getButtonIndex(IEWidget button) {
		return _buttonList.indexOf(button);
	}

	public int buttonCount() {
		return _buttonList.size();
	}

	/**
	 * Return a Vector of embedded IEObjects at this level. NOTE that this is NOT a recursive method
	 * 
	 * @return a Vector of IEObject instances
	 */
	@Override
	public Vector<IObject> getEmbeddedIEObjects() {
		Vector answer = new Vector();
		answer.addAll(getButtonList());
		return answer;
	}

	@Override
	public String getFullyQualifiedName() {
		return "Buttons";
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "button_container";
	}

	@Override
	public void setWOComponent(IEWOComponent woComponent) {
		if (noWOChange(woComponent))
			return;
		super.setWOComponent(woComponent);
		if (getButtonList() != null) {
			Enumeration<IEButtonWidget> en = getButtonList().elements();
			while (en.hasMoreElements()) {
				IEButtonWidget b = en.nextElement();
				b.setWOComponent(woComponent);// This call is very important because it will update the WOComponent components cache
			}
		}
	}

	@Override
	public boolean areComponentInstancesValid() {
		return true;
	}

	@Override
	public void removeInvalidComponentInstances() {
	}
}
