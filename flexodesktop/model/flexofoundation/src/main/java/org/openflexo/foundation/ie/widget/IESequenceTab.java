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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IETopComponent;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.IObject;
import org.openflexo.foundation.ie.OperationComponentInstance;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.ie.cl.TabComponentDefinition;
import org.openflexo.foundation.ie.dm.ComponentDeleteRequest;
import org.openflexo.foundation.ie.dm.ComponentDeleted;
import org.openflexo.foundation.ie.dm.ComponentNameChanged;
import org.openflexo.foundation.ie.dm.TabInserted;
import org.openflexo.foundation.ie.dm.TabRemoved;
import org.openflexo.foundation.ie.dm.TabReordered;
import org.openflexo.foundation.ie.dm.TabSelectionChanged;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.foundation.xml.FlexoComponentBuilder;

/**
 * Represents the container of some thumbnails
 * 
 * @author bmangez
 */
public class IESequenceTab extends IESequence<ITabWidget> implements IETopComponent, ButtonedWidgetInterface, ITabWidget {

	/**
     * 
     */
	public static final String TAB_CONTAINER_WIDGET = "tab_container_widget";

	private String _title;

	private IESequenceWidget buttons;

	/**
	 * @param woComponent
	 * @param parent
	 * @param prj
	 */
	public IESequenceTab(IEWOComponent woComponent, IEObject parent, FlexoProject prj) {
		super(woComponent, parent, prj);
		buttons = new IESequenceWidget(woComponent, this, prj);
		if (woComponent != null) {
			_title = woComponent.getName() + "Tabs";
		}
	}

	public IESequenceTab(FlexoComponentBuilder builder) {
		this(builder.woComponent, null, builder.getProject());
		initializeDeserialization(builder);
	}

	@Override
	public Vector<IETabWidget> getAllTabs() {
		Vector<IETabWidget> v = new Vector<IETabWidget>();
		Enumeration en = elements();
		while (en.hasMoreElements()) {
			ITabWidget t = (ITabWidget) en.nextElement();
			v.addAll(t.getAllTabs());
		}
		return v;
	}

	@Override
	public boolean isTopComponent() {
		return isRoot();
	}

	/**
	 * Return a Vector of embedded IEObjects at this level. NOTE that this is NOT a recursive method
	 * 
	 * @return a Vector of IEObject instances
	 */
	@Override
	public Vector<IObject> getEmbeddedIEObjects() {
		Vector<IObject> answer = super.getEmbeddedIEObjects();
		answer.add(buttons);
		return answer;
	}

	public int getTabCount() {
		return getAllTabs().size();
	}

	public boolean isTabContainer() {
		return isRoot();
	}

	/**
	 * Overrides elements
	 * 
	 * @see org.openflexo.foundation.ie.widget.IESequence#elements()
	 */
	@Override
	public Enumeration<ITabWidget> elements() {
		return super.elements();
	}

	/**
	 * Overrides iterator
	 * 
	 * @see org.openflexo.foundation.ie.widget.IESequence#iterator()
	 */
	@Override
	public Iterator<ITabWidget> iterator() {
		return super.iterator();
	}

	/**
	 * Overrides isSubsequence
	 * 
	 * @see org.openflexo.foundation.ie.widget.IESequence#isSubsequence()
	 */
	@Override
	public boolean isSubsequence() {
		return getParent() instanceof IESequenceTab;
	}

	/**
	 * Overrides getInspectorName
	 * 
	 * @see org.openflexo.foundation.ie.widget.IESequence#getInspectorName()
	 */
	@Override
	public String getInspectorName() {
		if (isRoot()) {
			return Inspectors.IE.TAB_CONTAINER_INSPECTOR;
		} else {
			return super.getInspectorName();
		}
	}

	@Override
	public void performOnDeleteOperations() {
		deleteTabWidgets();
		super.performOnDeleteOperations();
	}

	private void deleteTabWidgets() {
		Enumeration en = ((Vector) getAllTabs().clone()).elements();
		while (en.hasMoreElements()) {
			IETabWidget tab = (IETabWidget) en.nextElement();
			tab.delete();
		}
	}

	public void removeTab(IETabWidget tabToRemove) {
		unregisterTab(tabToRemove);
		removeFromInnerWidgets(tabToRemove);
	}

	/**
	 * Overrides insertElementAt
	 * 
	 * @see org.openflexo.foundation.ie.widget.IESequence#insertElementAt(org.openflexo.foundation.ie.widget.IWidget, int)
	 */
	@Override
	public void insertElementAt(ITabWidget o, int i) {
		super.insertElementAt(o, i);
		if (o instanceof IETabWidget) {

			// Generate process business data accessing method(s)
			for (OperationNode opNode : getComponentDefinition().getAllOperationNodesLinkedToThisComponent()) {
				((IETabWidget) o).getTabComponentDefinition().getComponentDMEntity()
						.addOrUpdateAccessingBusinessDataMethod(opNode.getProcess());
			}

			registerTab((IETabWidget) o);
			setChanged();
			notifyObservers(new TabInserted((IETabWidget) o));
		}
	}

	/**
	 * Overrides removeFromInnerWidgets
	 * 
	 * @see org.openflexo.foundation.ie.widget.IESequence#removeFromInnerWidgets(org.openflexo.foundation.ie.widget.IWidget, boolean)
	 */
	@Override
	public void removeFromInnerWidgets(ITabWidget w, boolean deleteIfEmpty) {
		super.removeFromInnerWidgets(w, deleteIfEmpty);
		if (w instanceof IETabWidget) {

			// Remove generated method(s) for accessing process business data on the tab
			Set<FlexoProcess> componentDefinitionProcesses = new HashSet<FlexoProcess>();
			for (OperationNode opNode : getComponentDefinition().getAllOperationNodesLinkedToThisComponent()) {
				componentDefinitionProcesses.add(opNode.getProcess());
			}

			for (OperationComponentInstance operationComponentInstance : ((IETabWidget) w).getTabComponentDefinition()
					.getAllOperationComponentInstances()) {
				if (operationComponentInstance.getComponentDefinition() != getComponentDefinition()) {
					for (OperationNode opNode : operationComponentInstance.getComponentDefinition()
							.getAllOperationNodesLinkedToThisComponent()) {
						componentDefinitionProcesses.remove(opNode.getProcess());
					}
				}
			}

			for (FlexoProcess process : componentDefinitionProcesses) {
				((IETabWidget) w).getTabComponentDefinition().getComponentDMEntity().removeAccessingBusinessDataMethod(process);
			}

			unregisterTab((IETabWidget) w);
			setChanged();
			notifyObservers(new TabRemoved((IETabWidget) w));
		}
	}

	@Override
	public IESequenceTab getRootParent() {
		IESequenceTab root = this;
		while (root.getParent() != null && root.getParent() instanceof IESequenceTab) {
			root = (IESequenceTab) root.getParent();
		}
		return root;
	}

	@Override
	public String toString() {
		return isSubsequence() ? "Sub-Tab" + getOperator() : "RootTab";
	}

	@Override
	public String getCalculatedLabel() {
		if (getTitle() != null && getTitle().trim().length() > 0) {
			return ensureCalculatedLabelIsAcceptable(getTitle());
		}
		return super.getCalculatedLabel();
	}

	public void swapTabs(ITabWidget w1, ITabWidget w2) {
		int i1 = indexOf(w1);
		int i2 = indexOf(w2);
		if (i1 > -1 && i2 > -1) {
			setElementAt(w1, i2);
			setElementAt(w2, i1);
			setChanged();
			notifyObservers(new TabReordered(this));
		}
	}

	// ==========================================================================
	// ============================= Accessors
	// ==================================
	// ==========================================================================

	public void setTitle(String title) {
		_title = title;
		setChanged();
		notifyObservers(new DataModification("title", null, title));
	}

	@Override
	public String getTitle() {
		return _title;
	}

	public String getTitleForGenerator() {
		String title = getTitle();
		if (title == null || title.trim().length() == 0) {
			title = getWOComponent().getName() + "Tabs";
		}
		return title;
	}

	public int getAbsoluteIndexOfTab(IETabWidget tabElement) {
		return getAllTabs().indexOf(tabElement);
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

	public void addNewTab(TabComponentDefinition tabComponentDefinition, String tabTitle, int index) {
		if (index > size() || index < 0) {
			index = size();
		}
		IETabWidget tabWidget = new IETabWidget(getWOComponent(), tabComponentDefinition, this, getProject());
		tabWidget.setTitle(tabTitle == null || tabTitle.trim().length() == 0 ? tabComponentDefinition.getComponentName() : tabTitle);
		tabWidget.setKey(tabComponentDefinition.getComponentName());
		tabWidget.setIndex(index);
		insertElementAt(tabWidget, index);
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
				Vector allTabsToDelete = findTabsForComponentDef((TabComponentDefinition) observable);
				Enumeration en = allTabsToDelete.elements();
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

	private Vector<IETabWidget> findTabsForComponentDef(ComponentDefinition compDef) {
		Vector<IETabWidget> answer = new Vector<IETabWidget>();
		Enumeration en = elements();
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
		return TAB_CONTAINER_WIDGET;
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

	public IESequenceWidget getButtons() {
		return buttons;
	}

	public void setButtons(IESequenceWidget buttons) {
		this.buttons = buttons;
		if (buttons != null) {
			buttons.setParent(this);
			buttons.setWOComponent(getWOComponent());
		}
	}

	public boolean hasButtons() {
		return buttons != null && !buttons.isEmpty();
	}

	public boolean hasTabs() {
		return !isEmpty();
	}

	/**
	 * Overrides getSequenceWidget
	 * 
	 * @see org.openflexo.foundation.ie.widget.ButtonedWidgetInterface#getSequenceWidget()
	 */
	@Override
	public IESequenceWidget getSequenceWidget() {
		return buttons;
	}

	/**
	 * Overrides insertButtonAtIndex
	 * 
	 * @see org.openflexo.foundation.ie.widget.ButtonedWidgetInterface#insertButtonAtIndex(org.openflexo.foundation.ie.widget.IEButtonWidget,
	 *      int)
	 */
	@Override
	public void insertButtonAtIndex(IEHyperlinkWidget button, int index) {
		buttons.insertElementAt(button, index);
	}

	/**
	 * Overrides removeButton
	 * 
	 * @see org.openflexo.foundation.ie.widget.ButtonedWidgetInterface#removeButton(org.openflexo.foundation.ie.widget.IEButtonWidget)
	 */
	@Override
	public void removeButton(IEHyperlinkWidget button) {
		buttons.removeFromInnerWidgets(button);
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
		Enumeration en = buttons.getAllNonSequenceWidget().elements();
		while (en.hasMoreElements()) {
			IWidget w = (IWidget) en.nextElement();
			if (w instanceof IEHyperlinkWidget) {
				v.add((IEHyperlinkWidget) w);
			}
		}
		return v;
	}

	public String getTabsTitleForGenerator() {
		return getWOComponent().getName() + getFlexoID() + "Tabs";
	}

	@Override
	public Vector<IEHyperlinkWidget> getAllButtonInterface() {
		Vector<IEHyperlinkWidget> v = super.getAllButtonInterface();
		v.addAll(getAllButtons());
		return v;
	}

}
