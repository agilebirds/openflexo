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
package org.openflexo.fib.view.widget.browser;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.antar.binding.AbstractBinding;
import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.DataBinding;
import org.openflexo.fib.model.FIBAttributeNotification;
import org.openflexo.fib.model.FIBBrowser;
import org.openflexo.fib.model.FIBBrowserElement;
import org.openflexo.fib.model.FIBBrowserElement.FIBBrowserElementChildren;
import org.openflexo.fib.view.widget.FIBBrowserWidget;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.ToolBox;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class FIBBrowserElementType implements BindingEvaluationContext, Observer {

	private final class CastFunction implements Function<Object, Object>, BindingEvaluationContext {
		private final FIBBrowserElementChildren children;

		private Object child;

		private CastFunction(FIBBrowserElementChildren children) {
			this.children = children;
		}

		@Override
		public synchronized Object apply(Object arg0) {
			child = arg0;
			Object result = children.getCast().getBindingValue(this);
			child = null;
			return result;
		}

		@Override
		public Object getValue(BindingVariable variable) {
			if (variable.getVariableName().equals("child")) {
				return child;
			} else {
				return FIBBrowserElementType.this.getValue(variable);
			}
		}
	}

	private static final Logger logger = Logger.getLogger(FIBBrowserElementType.class.getPackage().getName());

	private FIBBrowserModel fibBrowserModel;
	private FIBBrowserElement browserElementDefinition;
	private boolean isFiltered = false;

	private FIBController controller;

	public FIBBrowserElementType(FIBBrowserElement browserElementDefinition, FIBBrowserModel browserModel, FIBController controller) {
		super();
		this.controller = controller;
		this.fibBrowserModel = browserModel;
		this.browserElementDefinition = browserElementDefinition;

		browserElementDefinition.addObserver(this);
	}

	public void delete() {
		if (browserElementDefinition != null) {
			browserElementDefinition.deleteObserver(this);
		}

		this.controller = null;
		this.browserElementDefinition = null;
	}

	public FIBBrowserModel getBrowserModel() {
		return fibBrowserModel;
	}

	public FIBBrowserWidget getBrowserWidget() {
		return getBrowserModel().getBrowserWidget();
	}

	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof FIBAttributeNotification && o == browserElementDefinition) {
			FIBAttributeNotification dataModification = (FIBAttributeNotification) arg;
			((FIBBrowserWidget) controller.viewForComponent(browserElementDefinition.getBrowser())).updateBrowser();
		}
	}

	public FIBController getController() {
		return controller;
	}

	public FIBBrowser getBrowser() {
		return browserElementDefinition.getBrowser();
	}

	public String getLocalized(String key) {
		return FlexoLocalization.localizedForKey(getController().getLocalizerForComponent(getBrowser()), key);
	}

	protected void setModel(FIBBrowserModel model) {
		fibBrowserModel = model;
	}

	protected FIBBrowserModel getModel() {
		return fibBrowserModel;
	}

	/*public Object elementAt(int row)
	{
	    return fibBrowserModel.elementAt(row);
	}*/

	private void appendToDependingObjects(DataBinding binding, List<AbstractBinding> returned) {
		if (binding.isSet()) {
			returned.add(binding.getBinding());
		}
	}

	public List<AbstractBinding> getDependencyBindings(final Object object) {
		if (browserElementDefinition == null) {
			return null;
		}
		iteratorObject = object;
		List<AbstractBinding> returned = new ArrayList<AbstractBinding>();
		appendToDependingObjects(browserElementDefinition.getLabel(), returned);
		appendToDependingObjects(browserElementDefinition.getIcon(), returned);
		appendToDependingObjects(browserElementDefinition.getTooltip(), returned);
		appendToDependingObjects(browserElementDefinition.getEnabled(), returned);
		appendToDependingObjects(browserElementDefinition.getVisible(), returned);
		for (FIBBrowserElementChildren children : browserElementDefinition.getChildren()) {
			appendToDependingObjects(children.getData(), returned);
			appendToDependingObjects(children.getCast(), returned);
			appendToDependingObjects(children.getVisible(), returned);
		}
		return returned;
	}

	public synchronized String getLabelFor(final Object object) {
		if (browserElementDefinition == null) {
			return "???" + object.toString();
		}
		if (browserElementDefinition.getLabel().isSet()) {
			iteratorObject = object;
			return (String) browserElementDefinition.getLabel().getBindingValue(this);
		}
		return object.toString();
	}

	public synchronized String getTooltipFor(final Object object) {
		if (browserElementDefinition == null) {
			return "???" + object.toString();
		}
		if (browserElementDefinition.getTooltip().isSet()) {
			iteratorObject = object;
			return (String) browserElementDefinition.getTooltip().getBindingValue(this);
		}
		return browserElementDefinition.getName();
	}

	public synchronized Icon getIconFor(final Object object) {
		if (browserElementDefinition == null) {
			return null;
		}
		if (browserElementDefinition.getIcon().isSet()) {
			iteratorObject = object;
			Object returned = browserElementDefinition.getIcon().getBindingValue(this);
			if (returned instanceof Icon) {
				return (Icon) returned;
			}
			return null;
		} else {
			return browserElementDefinition.getImageIcon();
		}
	}

	public synchronized boolean isEnabled(final Object object) {
		if (browserElementDefinition == null) {
			return false;
		}
		if (browserElementDefinition.getEnabled().isSet()) {
			iteratorObject = object;
			Object enabledValue = browserElementDefinition.getEnabled().getBindingValue(this);
			if (enabledValue != null) {
				return (Boolean) enabledValue;
			}
			return true;
		} else {
			return true;
		}
	}

	public synchronized boolean isVisible(final Object object) {
		if (browserElementDefinition == null) {
			return false;
		}
		if (isFiltered()) {
			return false;
		}
		if (browserElementDefinition.getVisible().isSet()) {
			iteratorObject = object;
			return (Boolean) browserElementDefinition.getVisible().getBindingValue(this);
		} else {
			return true;
		}
	}

	public synchronized List<?> getChildrenFor(final Object object) {
		if (browserElementDefinition == null) {
			return Collections.EMPTY_LIST;
		}
		List<Object> returned = new ArrayList<Object>();
		for (FIBBrowserElementChildren children : browserElementDefinition.getChildren()) {
			if (children.isMultipleAccess()) {
				// System.out.println("add all children for "+browserElementDefinition.getName()+" children "+children.getName()+" data="+children.getData());
				// System.out.println("Obtain "+getChildrenListFor(children, object));
				List<?> childrenObjects = getChildrenListFor(children, object);
				// Might be null if some visibility was declared
				if (childrenObjects != null) {
					returned.addAll(childrenObjects);
				}
				// System.out.println("For " + object + " of " + object.getClass().getSimpleName() + " children=" + children.getData()
				// + " values=" + object);
			} else {
				// System.out.println("add children for "+browserElementDefinition.getName()+" children "+children.getName()+" data="+children.getData());
				// System.out.println("Obtain "+getChildrenFor(children, object));
				// System.out.println("accessed type="+children.getAccessedType());
				Object childrenObject = getChildrenFor(children, object);
				// Might be null if some visibility was declared
				if (childrenObject != null) {
					returned.add(childrenObject);
				}
				// System.out.println("For " + object + " of " + object.getClass().getSimpleName() + " children=" + children.getData()
				// + " value=" + childrenObject);
			}
		}
		return returned;
	}

	protected synchronized Object getChildrenFor(FIBBrowserElementChildren children, final Object object) {
		if (children.getData().isSet()) {
			iteratorObject = object;
			if (children.getVisible().isSet()) {
				boolean visible = (Boolean) children.getVisible().getBindingValue(this);
				if (!visible) {
					// Finally we dont want to see it
					return null;
				}
			}
			Object result = children.getData().getBindingValue(this);
			if (children.getCast().isSet()) {
				return new CastFunction(children).apply(result);
			}
			return result;
		} else {
			return null;
		}
	}

	protected synchronized List<?> getChildrenListFor(final FIBBrowserElementChildren children, final Object object) {
		if (children.getData().isSet() && children.isMultipleAccess()) {
			iteratorObject = object;
			if (children.getVisible().isSet()) {
				boolean visible = (Boolean) children.getVisible().getBindingValue(this);
				if (!visible) {
					// Finally we dont want to see it
					return null;
				}
			}
			Object bindingValue = children.getData().getBindingValue(this);
			List<?> list = ToolBox.getListFromIterable(bindingValue);
			if (children.getCast().isSet()) {
				list = Lists.transform(list, new CastFunction(children));
			}
			return list;
		} else {
			return null;
		}
	}

	public boolean isLabelEditable() {
		return getBrowserElement().getIsEditable() && getBrowserElement().getEditableLabel().isSet()
				&& getBrowserElement().getEditableLabel().getBinding().isSettable();
	}

	public synchronized String getEditableLabelFor(final Object object) {
		if (isLabelEditable()) {
			iteratorObject = object;
			return (String) browserElementDefinition.getEditableLabel().getBindingValue(this);
		}
		return object.toString();
	}

	public synchronized void setEditableLabelFor(final Object object, String value) {
		if (isLabelEditable()) {
			iteratorObject = object;
			browserElementDefinition.getEditableLabel().setBindingValue(value, this);
		}
	}

	protected Object iteratorObject;

	@Override
	public synchronized Object getValue(BindingVariable variable) {
		if (variable.getVariableName().equals(browserElementDefinition.getName())) {
			return iteratorObject;
		} else if (variable.getVariableName().equals("object")) {
			return iteratorObject;
		} else {
			return getController().getValue(variable);
		}
	}

	public FIBBrowserElement getBrowserElement() {
		return browserElementDefinition;
	}

	public Font getFont(final Object object) {
		if (browserElementDefinition.getDynamicFont().isSet()) {
			iteratorObject = object;
			Object returned = browserElementDefinition.getDynamicFont().getBindingValue(this);
			if (returned instanceof Font) {
				return (Font) returned;
			}
		}
		if (getBrowserElement() != null) {
			return getBrowserElement().retrieveValidFont();
		}

		return null;
	}

	public boolean isFiltered() {
		return isFiltered;
	}

	public void setFiltered(boolean isFiltered) {
		System.out.println("Element " + getBrowserElement().getName() + " filtered: " + isFiltered);
		if (this.isFiltered != isFiltered) {
			this.isFiltered = isFiltered;
			// Later, try to implement a way to rebuild tree with same expanded nodes
			fibBrowserModel.fireTreeRestructured();
		}
	}

}
