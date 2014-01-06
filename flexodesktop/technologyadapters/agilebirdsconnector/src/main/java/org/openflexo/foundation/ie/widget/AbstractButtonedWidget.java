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

/**
 * Abstract class containing buttons
 * 
 * @author bmangez
 */

import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.dm.ButtonAdded;
import org.openflexo.foundation.ie.dm.ButtonRemoved;
import org.openflexo.logging.FlexoLogger;

public abstract class AbstractButtonedWidget extends IEWidget implements ButtonContainerInterface, FlexoObserver, ButtonedWidgetInterface {

	private static final Logger logger = FlexoLogger.getLogger(AbstractButtonedWidget.class.getPackage().getName());

	private IESequenceWidget _sequenceWidget;

	public AbstractButtonedWidget(IEWOComponent woComponent, IEObject parent, FlexoProject prj) {
		super(woComponent, parent, prj);
		_sequenceWidget = new IESequenceWidget(woComponent, this, prj);
	}

	@Override
	public IESequenceWidget getSequenceWidget() {
		return _sequenceWidget;
	}

	public void setSequenceWidget(IESequenceWidget list) {
		_sequenceWidget = list;
		list.setParent(this);
		list.setWOComponent(getWOComponent());
	}

	public IESequenceWidget getButtonList() {
		return _sequenceWidget;
	}

	public void setButtonList(IESequenceWidget list) {
		_sequenceWidget = list;
	}

	@Deprecated
	public void addToDeprecatedButtonList(IEButtonWidget button) {
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("addToDeprecatedButtonList cannot be used");
		}
	}

	@Deprecated
	public void removeFromDeprecatedButtonList(IEButtonWidget button) {
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("removeFromDeprecatedButtonList cannot be used anymore");
		}
	}

	@Deprecated
	public Vector getDeprecatedButtonList() {
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("getDeprecatedButtonList cannot be used anymore");
		}
		return null;
	}

	@Deprecated
	public void setDeprecatedButtonList(Vector v) {
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("setDeprecatedButton cannnot be used except at startut");
		}
	}

	public void addToButtonList(IEHyperlinkWidget button) {
		addButton(button);
	}

	public void removeFromButtonList(IEHyperlinkWidget button) {
		removeButton(button);
	}

	@Override
	public void performOnDeleteOperations() {
		deleteButtons();
		super.performOnDeleteOperations();
	}

	private void deleteButtons() {
		Enumeration en = _sequenceWidget.elements();
		while (en.hasMoreElements()) {
			((IEWidget) en.nextElement()).delete();
		}
	}

	@Override
	public void removeButton(IEHyperlinkWidget buttonToRemove) {
		_sequenceWidget.removeFromInnerWidgets(buttonToRemove);
		updateButtonIndex();
		setChanged();
		notifyObservers(new ButtonRemoved(buttonToRemove));
	}

	/**
	 * Overrides removeButton
	 * 
	 * @see org.openflexo.foundation.ie.widget.ButtonContainerInterface#removeButton(org.openflexo.foundation.ie.widget.IEWidget)
	 */
	@Override
	public void removeButton(IEWidget button) {
		if (button instanceof IEHyperlinkWidget) {
			removeButton((IEHyperlinkWidget) button);
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not remove object of " + button.getClass().getName());
			}
		}
	}

	public void addButton(IEHyperlinkWidget button, int index) {
		button.setParent(_sequenceWidget);
		button.setWOComponent(getWOComponent());
		_sequenceWidget.insertElementAt(button, Math.max(Math.min(_sequenceWidget.size(), index), 0));
		updateButtonIndex();
		setChanged();
		notifyObservers(new ButtonAdded(button));
	}

	public void addButton(IEHyperlinkWidget button) {
		button.setParent(_sequenceWidget);
		int insertionIndex = findInsertionIndex(_sequenceWidget, ((Indexable) button).getIndex());
		_sequenceWidget.insertElementAt(button, insertionIndex);
		setChanged();
		notifyObservers(new ButtonAdded(button));
	}

	public static int findInsertionIndex(IESequenceWidget v, int wish) {
		int answer = 0;
		if (v == null || v.size() == 0) {
			return answer;
		}
		while (answer < v.size() && answer < wish && wish > wishFor(v.get(answer))) {
			answer++;
		}
		return answer;
	}

	private static int wishFor(Object v) {
		return ((Indexable) v).getIndex();
	}

	public Enumeration elements() {
		return _sequenceWidget.elements();
	}

	/**
	 * @deprecated use IESequence.refreshIndexes instead
	 */
	@Deprecated
	public void updateButtonIndex() {
		_sequenceWidget.refreshIndexes();
		/*
		 * Enumeration en = _buttonList.elements(); int i = 0; while
		 * (en.hasMoreElements()) { ((IEWidget) en.nextElement()).setIndex(i);
		 * i++; }
		 */
	}

	/**
	 * Overrides insertButtonAtIndex
	 * 
	 * @see org.openflexo.foundation.ie.widget.ButtonedWidgetInterface#insertButtonAtIndex(org.openflexo.foundation.ie.widget.IEButtonWidget,
	 *      int)
	 */
	@Override
	public void insertButtonAtIndex(IEHyperlinkWidget button, int index) {
		addButton(button, index);
	}

	@Override
	public int getButtonIndex(IEWidget button) {
		return _sequenceWidget.indexOf(button);
	}

	/**
	 * Overrides buttons
	 * 
	 * @see org.openflexo.foundation.ie.widget.ButtonedWidgetInterface#buttonWidgets()
	 */
	@Override
	public Enumeration<IEHyperlinkWidget> buttonWidgets() {
		return getAllButtons().elements();
	}

	/**
	 * Overrides getAllButtons
	 * 
	 * @see org.openflexo.foundation.ie.widget.ButtonedWidgetInterface#getAllButtons()
	 */
	@Override
	public Vector<IEHyperlinkWidget> getAllButtons() {
		Vector<IEHyperlinkWidget> v = new Vector<IEHyperlinkWidget>();
		Enumeration en = _sequenceWidget.getAllNonSequenceWidget().elements();
		while (en.hasMoreElements()) {
			IWidget w = (IWidget) en.nextElement();
			if (w instanceof IEHyperlinkWidget) {
				v.add((IEHyperlinkWidget) w);
			}
		}
		return v;
	}

	@Override
	public void setWOComponent(IEWOComponent woComponent) {
		if (noWOChange(woComponent)) {
			return;
		}
		super.setWOComponent(woComponent);// This call is very important because it will update the WOComponent components cache
		if (_sequenceWidget != null) {
			_sequenceWidget.setWOComponent(woComponent);
		}
	}

	@Override
	public boolean areComponentInstancesValid() {
		if (_sequenceWidget != null) {
			return _sequenceWidget.areComponentInstancesValid();
		} else {
			return true;
		}
	}

	@Override
	public void removeInvalidComponentInstances() {
		if (_sequenceWidget != null) {
			_sequenceWidget.removeInvalidComponentInstances();
		}
	}
}
