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
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.openflexo.foundation.DataFlexoObserver;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.bindings.BindingValue;
import org.openflexo.foundation.bindings.BooleanStaticBinding;
import org.openflexo.foundation.bindings.ComponentBindingDefinition;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.foundation.ie.ComponentInstanceBinding;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IETabComponent;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.IObject;
import org.openflexo.foundation.ie.TabComponentInstance;
import org.openflexo.foundation.ie.action.AddTab;
import org.openflexo.foundation.ie.action.MoveTabLeft;
import org.openflexo.foundation.ie.action.MoveTabRight;
import org.openflexo.foundation.ie.action.SuroundWithRepetition;
import org.openflexo.foundation.ie.cl.TabComponentDefinition;
import org.openflexo.foundation.ie.dm.IEDataModification;
import org.openflexo.foundation.ie.operator.ConditionalOperator;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.foundation.xml.FlexoComponentBuilder;
import org.openflexo.logging.FlexoLogger;

/**
 * Represent a Tab inside a IETabContainerWidget
 * 
 * @author bmangez
 */
public class IETabWidget extends IEReusableWidget<TabComponentDefinition, TabComponentInstance> implements DataFlexoObserver, ITabWidget {
	/**
     * 
     */
	public static final String TAB_WIDGET = "tab_widget";

	private static final Logger logger = FlexoLogger.getLogger(IETabWidget.class.getPackage().getName());

	public static final String TAB_TITLE_ATTRIBUTE_NAME = "title";

	private String _title;

	private String _key;

	private String _bindings;

	@Override
	protected TabComponentInstance createComponentInstance(TabComponentDefinition componentDefinition, IEWOComponent woComponent) {
		return new TabComponentInstance(componentDefinition, this);
	}

	/**
	 * Overrides getSpecificActionListForThatClass
	 * 
	 * @see org.openflexo.foundation.ie.widget.IEWidget#getSpecificActionListForThatClass()
	 */
	@Override
	protected Vector<FlexoActionType> getSpecificActionListForThatClass() {
		Vector<FlexoActionType> returned = super.getSpecificActionListForThatClass();
		returned.add(AddTab.actionType);
		returned.add(MoveTabLeft.actionType);
		returned.add(MoveTabRight.actionType);
		returned.remove(SuroundWithRepetition.actionType);
		return returned;
	}

	/**
	 * Overrides getParent
	 * 
	 * @see org.openflexo.foundation.ie.widget.IEWidget#getParent()
	 */
	@Override
	public IEWidget getParent() {
		return (IEWidget) super.getParent();
	}

	@Override
	public IESequenceTab getRootParent() {
		IESequenceTab root = ((IESequenceTab) getParent());
		while (root.getParent() != null && root.getParent() instanceof IESequenceTab) {
			root = (IESequenceTab) root.getParent();
		}
		return root;
	}

	public IETabComponent getTabComponent() {
		return getTabComponentDefinition().getWOComponent();
	}

	public IETabWidget(FlexoComponentBuilder builder) {
		this(builder.woComponent, null, null, builder.getProject());
		initializeDeserialization(builder);
	}

	public IETabWidget(IEWOComponent woComponent, TabComponentDefinition def, IEObject parent, FlexoProject project) {
		super(woComponent, def, parent, project);
		if (getTitle() == null) {
			setTitle("no title");
		}
	}

	@Override
	public String getDefaultInspectorName() {
		return Inspectors.IE.TAB_INSPECTOR;
	}

	// ==========================================================================
	// ============================= Instance Methods
	// ===========================
	// ==========================================================================

	public void moveLeft() {
		ITabWidget w = null;
		if (((IESequenceTab) getParent()).isSubsequence()) {
			w = ((IESequenceTab) getParent());
		} else {
			w = this;
		}
		if (w.getIndex() == 0) {
			getRootParent().removeFromInnerWidgets(w);
			getRootParent().addToInnerWidgets(w);
		} else {
			getRootParent().swapTabs(w, getRootParent().get(w.getIndex() - 1));
		}
	}

	public void moveRight() {
		ITabWidget w = null;
		if (((IESequenceTab) getParent()).isSubsequence()) {
			w = ((IESequenceTab) getParent());
		} else {
			w = this;
		}
		if (w.getIndex() == getRootParent().size() - 1) {
			getRootParent().removeFromInnerWidgets(w);
			getRootParent().insertElementAt(w, 0);
		} else {
			getRootParent().swapTabs(w, getRootParent().get(w.getIndex() + 1));
		}
	}

	@Override
	public TabComponentInstance getReusableComponentInstance() {
		return super.getReusableComponentInstance();
	}

	@Override
	public String toString() {
		return "Tab-" + getTitle();
	}

	public TabComponentDefinition getTabComponentDefinition() {
		return getReusableComponentInstance().getComponentDefinition();
	}

	// ==========================================================================
	// ============================= Accessors
	// ==================================
	// ==========================================================================

	@Override
	public String getName() {
		return getTitle();
	}

	public String getTitle() {
		return _title;
	}

	public void setTitle(String title) {
		this._title = title;
		setChanged();
		notifyObservers(new DataModification(DataModification.ATTRIBUTE, TAB_TITLE_ATTRIBUTE_NAME, null, title));
	}

	public String getKey() {
		return _key;
	}

	public void setKey(String key) {
		String old = _key;
		this._key = key;
		setChanged();
		notifyObservers(new IEDataModification("key", old, key));
	}

	public String getBindings() {
		return _bindings;
	}

	public void setBindings(String value) {
		String old = _bindings;
		this._bindings = value;
		setChanged();
		notifyObservers(new IEDataModification("bindings", old, value));
	}

	public TabComponentInstance getComponentInstance() {
		return getReusableComponentInstance();
	}

	/**
	 * Return a Vector of embedded IEObjects at this level. NOTE that this is NOT a recursive method
	 * 
	 * @return a Vector of IEObject instances
	 */
	@Override
	public Vector<IObject> getEmbeddedIEObjects() {
		Vector<IObject> answer = new Vector<IObject>();
		if (getComponentInstance() != null) {
			answer.add(getComponentInstance());
		}
		return answer;
	}

	@Override
	public String getFullyQualifiedName() {
		return "TabReference";
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return TAB_WIDGET;
	}

	/**
	 * Overrides getAllTabs
	 * 
	 * @see org.openflexo.foundation.ie.widget.ITabWidget#getAllTabs()
	 */
	@Override
	public Vector<IETabWidget> getAllTabs() {
		Vector<IETabWidget> v = new Vector<IETabWidget>();
		v.add(this);
		return v;
	}

	private IESequenceTab getRootSequenceTab() {
		if (((IESequenceTab) getParent()).hasOperatorConditional() || ((IESequenceTab) getParent()).hasOperatorRepetition()) {
			return (IESequenceTab) ((IESequenceTab) getParent()).getParent();
		}
		return (IESequenceTab) getParent();
	}

	public String getTabKeyForGenerator() {
		if (_key != null && _key.trim().length() > 0) {
			return _key;
		}
		return getRootSequenceTab().getTitleForGenerator() + getRootSequenceTab().getAbsoluteIndexOfTab(this);
	}

	/**
	 * Overrides update
	 * 
	 * @see org.openflexo.foundation.ie.IEObject#update(org.openflexo.foundation.FlexoObservable, org.openflexo.foundation.DataModification)
	 */
	@Override
	public void update(FlexoObservable observable, DataModification obj) {
		/*if (observable == getTabComponent()) {
		    setChanged();
		    notifyObservers(obj);
		}*/
		super.update(observable, obj);
	}

	@Override
	public boolean areComponentInstancesValid() {
		return getComponentInstance() == null || getComponentInstance().isValidInstance();
	}

	@Override
	public void removeInvalidComponentInstances() {
		if (!getComponentInstance().isValidInstance()) {
			delete();
		}
	}

	public boolean isShownInOperation(OperationNode node) {
		ConditionalOperator operator = conditional();
		if (operator != null) {
			if (operator.getBindingConditional() instanceof BindingValue) {
				BindingValue bv = (BindingValue) operator.getBindingConditional();
				if (bv.getBindingPathElementCount() == 1) {
					if (bv.getBindingPathElementAtIndex(0) instanceof DMProperty) {
						String varName = ((DMProperty) bv.getBindingPathElementAtIndex(0)).getName();
						Enumeration<ComponentBindingDefinition> en = operator.getWOComponent().getComponentDMEntity()
								.getBindingDefinitions().elements();
						ComponentBindingDefinition cbd = null;
						while (en.hasMoreElements() && cbd == null) {
							cbd = en.nextElement();
							if (!cbd.getVariableName().equals(varName)) {
								cbd = null;
							}
						}
						if (cbd != null) {
							ComponentInstanceBinding cib = node.getComponentInstance().getBinding(cbd);
							if (cib != null && cib.getBindingValue() != null) {
								if (cib.getBindingValue() instanceof BooleanStaticBinding) {
									if (operator.getIsNegate()) {
										return !((BooleanStaticBinding) cib.getBindingValue()).getValue();
									} else {
										return ((BooleanStaticBinding) cib.getBindingValue()).getValue();
									}
								} else {
									if (logger.isLoggable(Level.INFO)) {
										logger.info("Binding of OperationNode " + node.getFullyQualifiedName()
												+ " is dynamic, I cannot evaluate it.");
									}
								}
							}
						}
					}
				}
			} else if (operator.getBindingConditional() instanceof BooleanStaticBinding) {
				BooleanStaticBinding bv = (BooleanStaticBinding) operator.getBindingConditional();
				if (bv.getValue() != null) {
					return bv.getValue();
				}
			}
		}
		return true; // default is true
	}

	@Override
	protected Hashtable<String, String> getLocalizableProperties(Hashtable<String, String> props) {
		if (StringUtils.isNotEmpty(getTitle())) {
			props.put("title", getTitle());
		}
		return super.getLocalizableProperties(props);
	}

	public String getTabsTitleForGenerator() {
		return getRootParent().getTabsTitleForGenerator();
	}

	@Override
	public Vector<IEHyperlinkWidget> getAllButtonInterface() {
		return getTabComponent().getAllButtonInterface();
	}
}
