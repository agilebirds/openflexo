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

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IETopComponent;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.IObject;
import org.openflexo.foundation.ie.action.AddTab;
import org.openflexo.foundation.ie.action.MoveTabLeft;
import org.openflexo.foundation.ie.action.MoveTabRight;
import org.openflexo.foundation.ie.action.TopComponentDown;
import org.openflexo.foundation.ie.action.TopComponentUp;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.ie.cl.TabComponentDefinition;
import org.openflexo.foundation.ie.dm.ComponentDeleteRequest;
import org.openflexo.foundation.ie.dm.ComponentDeleted;
import org.openflexo.foundation.ie.dm.ComponentNameChanged;
import org.openflexo.foundation.ie.dm.TabInserted;
import org.openflexo.foundation.ie.dm.TabRemoved;
import org.openflexo.foundation.ie.dm.TabSelectionChanged;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.xml.FlexoComponentBuilder;
import org.openflexo.toolbox.EmptyVector;

/**
 * Represents the container of some thumbnails
 * 
 * @author bmangez
 * 
 */
@Deprecated
public class IETabContainerWidget extends AbstractButtonedWidget implements IETopComponent {

	public static final String BLOC_TITLE_ATTRIBUTE_NAME = "title";

	private String _title;

	private Vector<IETabWidget> _tabWidgetList;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================
	@Deprecated
	public IETabContainerWidget(FlexoComponentBuilder builder) {
		this(builder.woComponent, null, builder.getProject());
		initializeDeserialization(builder);
	}

	@Deprecated
	public IETabContainerWidget(IEWOComponent woComponent, IEObject parent, FlexoProject prj) {
		super(woComponent, parent, prj);
		_tabWidgetList = new Vector<IETabWidget>();
		if (woComponent != null) {
			_title = woComponent.getName() + "Tabs";
		}
	}

	@Override
	public String getDefaultInspectorName() {
		return Inspectors.IE.TAB_CONTAINER_INSPECTOR;
	}

	@Override
	protected Vector<FlexoActionType> getSpecificActionListForThatClass() {
		Vector<FlexoActionType> returned = super.getSpecificActionListForThatClass();
		returned.add(AddTab.actionType);
		returned.add(MoveTabLeft.actionType);
		returned.add(MoveTabRight.actionType);
		returned.add(TopComponentUp.actionType);
		returned.add(TopComponentDown.actionType);
		return returned;
	}

	private void deleteTabWidgets() {
		Enumeration en = ((Vector) _tabWidgetList.clone()).elements();
		while (en.hasMoreElements()) {
			IETabWidget tab = ((IETabWidget) en.nextElement());
			removeTab(tab);
			tab.delete();
		}
	}

	public void removeTab(IETabWidget tabToRemove) {
		unregisterTab(tabToRemove);
		_tabWidgetList.remove(tabToRemove);
		updateTabIndex();
		setChanged();
		notifyObservers(new TabRemoved(tabToRemove));
	}

	public void insertThumbnail(IETabWidget insertedTab) {
		if (insertedTab != null) {
			insertedTab.setParent(this);
			_tabWidgetList.insertElementAt(insertedTab,
					Math.min(insertedTab.getIndex() > -1 ? insertedTab.getIndex() : _tabWidgetList.size(), _tabWidgetList.size()));
			updateTabIndex();
			registerTab(insertedTab);
			setChanged();
			notifyObservers(new TabInserted(insertedTab));
		}
	}

	public void insertTabNoIndexComputation(IETabWidget insertedTab) {
		if (insertedTab != null) {
			int insertionIndex = findInsertionIndex(_tabWidgetList, insertedTab.getIndex());
			insertedTab.setParent(this);
			_tabWidgetList.insertElementAt(insertedTab, insertionIndex);
			registerTab(insertedTab);
			setChanged();
			notifyObservers(new TabInserted(insertedTab));
		}
	}

	public static int findInsertionIndex(Vector v, int wish) {
		int answer = 0;
		if (v == null || v.size() == 0) {
			return answer;
		}
		while (answer < v.size() && answer < wish && wish > wishFor(v.elementAt(answer))) {
			answer++;
		}
		return answer;
	}

	private static int wishFor(Object v) {
		return ((IETabWidget) v).getIndex();
	}

	public void insertTabAtIndex(IETabWidget insertedTab, int index) {
		insertedTab.setParent(this);
		_tabWidgetList.insertElementAt(insertedTab, Math.min(_tabWidgetList.size(), index));
		updateTabIndex();
		setChanged();
		notifyObservers(new TabInserted(insertedTab));
	}

	public void swapTabs(IETabWidget w1, IETabWidget w2) {
		int i1 = _tabWidgetList.indexOf(w1);
		int i2 = _tabWidgetList.indexOf(w2);
		if (i1 > -1 && i2 > -1) {
			_tabWidgetList.setElementAt(w1, i2);
			_tabWidgetList.setElementAt(w2, i1);
			updateTabIndex();
		}

	}

	public void updateTabIndex() {
		Enumeration en = _tabWidgetList.elements();
		int i = 0;
		while (en.hasMoreElements()) {
			((IEWidget) en.nextElement()).setIndex(i);
			i++;
		}
	}

	// ==========================================================================
	// ============================= Accessors
	// ==================================
	// ==========================================================================

	public void setTitle(String title) {
		_title = title;
		setChanged();
		notifyObservers(new DataModification(DataModification.ATTRIBUTE, BLOC_TITLE_ATTRIBUTE_NAME, null, title));
	}

	@Override
	public String getTitle() {
		return _title;
	}

	public Enumeration tabs() {
		return _tabWidgetList.elements();
	}

	public boolean hasThumbnails() {
		return getThumbnailList().size() > 0;
	}

	public Vector<IETabWidget> getThumbnailList() {
		return _tabWidgetList;
	}

	public void setThumbnailList(Vector<IETabWidget> list) {
		_tabWidgetList = list;
		Enumeration en = list.elements();
		while (en.hasMoreElements()) {
			registerTab((IETabWidget) en.nextElement());
		}
	}

	public void addToThumbnailList(IETabWidget thumbnail) {
		insertTabNoIndexComputation(thumbnail);
	}

	public void removeFromThumbnailList(IETabWidget thumbnail) {
		removeTab(thumbnail);
	}

	private void registerTab(IETabWidget tab) {
		if (tab.getTabComponentDefinition() != null) {
			tab.getTabComponentDefinition().addObserver(this);
		}
	}

	private void unregisterTab(IETabWidget tab) {
		if (tab.getTabComponentDefinition() != null) {
			tab.getTabComponentDefinition().deleteObserver(this);
		}
	}

	/**
	 * Return a Vector of embedded IEObjects at this level. NOTE that this is NOT a recursive method
	 * 
	 * @return a Vector of IEObject instances
	 */
	@Override
	public Vector<IObject> getEmbeddedIEObjects() {
		Vector<IObject> returned = new Vector<IObject>();
		returned.addAll(getThumbnailList());
		Enumeration en = getThumbnailList().elements();
		while (en.hasMoreElements()) {
			returned.addAll(((IEObject) en.nextElement()).getAllEmbeddedIEObjects());
		}
		return returned;
	}

	@Override
	public String getFullyQualifiedName() {
		return "TabsContainer in " + getWOComponent().getName();
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (observable instanceof TabComponentDefinition) {
			if (dataModification instanceof ComponentDeleteRequest) {
				((ComponentDeleteRequest) dataModification).addToWarnings("used by " + getFullyQualifiedName());
			} else if (dataModification instanceof ComponentDeleted) {
				Vector allThumbsToDelete = findTabsForComponentDef((TabComponentDefinition) observable);
				Enumeration en = allThumbsToDelete.elements();
				while (en.hasMoreElements()) {
					IETabWidget tabToDelete = (IETabWidget) en.nextElement();
					removeTab(tabToDelete);
					tabToDelete.delete();
				}
			} else if (dataModification instanceof ComponentNameChanged) {
				setChanged();
			}
		}
	}

	private Vector findTabsForComponentDef(ComponentDefinition compDef) {
		Vector answer = new Vector();
		Enumeration en = tabs();
		while (en.hasMoreElements()) {
			IETabWidget potential = (IETabWidget) en.nextElement();
			if (potential.getTabComponentDefinition().equals(compDef)) {
				answer.add(potential);
			}
		}
		return answer;
	}

	public void setSelectedTab(IETabWidget widget) {
		setChanged();
		notifyObservers(new TabSelectionChanged(widget));
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "tab_container_widget";
	}

	/**
	 * Overrides setWOComponent
	 * 
	 * @see org.openflexo.foundation.ie.widget.IEWidget#setWOComponent(org.openflexo.foundation.ie.IEWOComponent)
	 */
	@Override
	public void setWOComponent(IEWOComponent woComponent) {
		if (noWOChange(woComponent)) {
			return;
		}
		super.setWOComponent(woComponent);
		if (woComponent != null) {
			setTitle(woComponent.getName() + "Tabs");
		}
	}

	@Override
	public Vector<IESequenceTab> getAllTabContainers() {
		return new Vector<IESequenceTab>();
	}

	/**
	 * Overrides getAllButtons
	 * 
	 * @see org.openflexo.foundation.ie.widget.ButtonedWidgetInterface#getAllButtons()
	 */
	@Override
	public Vector<IEHyperlinkWidget> getAllButtons() {
		return null;
	}

	@Override
	public Vector<IEHyperlinkWidget> getAllButtonInterface() {
		return EmptyVector.EMPTY_VECTOR(IEHyperlinkWidget.class);
	}
}
