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
package org.openflexo.fib.model;

import java.awt.Color;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.tree.TreeSelectionModel;

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.ParameterizedTypeImpl;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.fib.controller.FIBBrowserDynamicModel;

public class FIBBrowser extends FIBWidget {

	private static final Logger logger = Logger.getLogger(FIBBrowser.class.getPackage().getName());

	@Deprecated
	private BindingDefinition SELECTED;
	@Deprecated
	private BindingDefinition ROOT;

	@Deprecated
	public BindingDefinition getSelectedBindingDefinition() {
		if (SELECTED == null) {
			SELECTED = new BindingDefinition("selected", Object.class, DataBinding.BindingDefinitionType.GET_SET, false);
		}
		return SELECTED;
	}

	@Deprecated
	public BindingDefinition getRootBindingDefinition() {
		if (ROOT == null) {
			ROOT = new BindingDefinition("root", getIteratorClass(), DataBinding.BindingDefinitionType.GET, false);
		}
		return ROOT;
	}

	public static enum Parameters implements FIBModelAttribute {
		root,
		iteratorClass,
		visibleRowCount,
		rowHeight,
		boundToSelectionManager,
		selectionMode,
		selected,
		elements,
		showFooter,
		rootVisible,
		showRootsHandle,
		textSelectionColor,
		textNonSelectionColor,
		backgroundSelectionColor,
		backgroundSecondarySelectionColor,
		backgroundNonSelectionColor,
		borderSelectionColor
	}

	public enum SelectionMode {
		SingleTreeSelection {
			@Override
			public int getMode() {
				return TreeSelectionModel.SINGLE_TREE_SELECTION;
			}
		},
		ContiguousTreeSelection {
			@Override
			public int getMode() {
				return TreeSelectionModel.CONTIGUOUS_TREE_SELECTION;
			}
		},
		DiscontiguousTreeSelection {
			@Override
			public int getMode() {
				return TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION;
			}
		};

		public abstract int getMode();
	}

	private DataBinding<Object> root;
	private DataBinding<Object> selected;

	private Integer visibleRowCount;
	private Integer rowHeight;
	private boolean boundToSelectionManager = false;

	private SelectionMode selectionMode = SelectionMode.DiscontiguousTreeSelection;

	private boolean showFooter = true;
	private boolean rootVisible = true;
	private boolean showRootsHandle = true;

	private Color textSelectionColor;
	private Color textNonSelectionColor;
	private Color backgroundSelectionColor;
	private Color backgroundSecondarySelectionColor;
	private Color backgroundNonSelectionColor;
	private Color borderSelectionColor;

	private Class iteratorClass;

	private Vector<FIBBrowserElement> elements;

	private Hashtable<Class, FIBBrowserElement> elementsForClasses;

	public FIBBrowser() {
		elements = new Vector<FIBBrowserElement>();
		elementsForClasses = new Hashtable<Class, FIBBrowserElement>();
	}

	@Override
	protected String getBaseName() {
		return "Browser";
	}

	public DataBinding<Object> getRoot() {
		if (root == null) {
			root = new DataBinding<Object>(this, getIteratorClass(), DataBinding.BindingDefinitionType.GET);
		}
		return root;
	}

	public void setRoot(DataBinding<Object> root) {
		if (root != null) {
			root.setOwner(this);
			root.setDeclaredType(getIteratorClass());
			root.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
		}
		this.root = root;
	}

	public DataBinding<Object> getSelected() {
		if (selected == null) {
			selected = new DataBinding<Object>(this, getIteratorClass(), DataBinding.BindingDefinitionType.GET_SET);
		}
		return selected;
	}

	public void setSelected(DataBinding<Object> selected) {
		if (selected != null) {
			selected.setOwner(this);
			selected.setDeclaredType(getIteratorClass());
			selected.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET_SET);
		}
		this.selected = selected;
	}

	@Override
	public void finalizeDeserialization() {
		logger.fine("finalizeDeserialization() for FIBTable " + getName());
		super.finalizeDeserialization();
		// Give a chance to the iterator to be typed
		for (FIBBrowserElement element : getElements()) {
			element.finalizeBrowserDeserialization();
		}
		if (selected != null) {
			selected.decode();
		}
	}

	public Class getIteratorClass() {
		if (iteratorClass == null) {
			iteratorClass = Object.class;
		}
		return iteratorClass;

	}

	public void setIteratorClass(Class iteratorClass) {
		FIBAttributeNotification<Class> notification = requireChange(Parameters.iteratorClass, iteratorClass);
		if (notification != null) {
			this.iteratorClass = iteratorClass;
			hasChanged(notification);
		}
	}

	@Override
	public Type getDefaultDataClass() {
		return Object.class;
	}

	@Override
	public Type getDynamicAccessType() {
		Type[] args = new Type[2];
		args[0] = getIteratorClass();
		args[1] = getIteratorClass();
		return new ParameterizedTypeImpl(FIBBrowserDynamicModel.class, args);
	}

	@Override
	public Boolean getManageDynamicModel() {
		return true;
	}

	/*public String getIteratorClassName()
	{
		return iteratorClassName;
	}
	
	public void setIteratorClassName(String iteratorClassName)
	{
		FIBAttributeNotification<String> notification = requireChange(
				Parameters.iteratorClassName, iteratorClassName);
		if (notification != null) {
			this.iteratorClassName = iteratorClassName;
			iteratorClass = null;
			hasChanged(notification);
		}
	}*/

	public Integer getVisibleRowCount() {
		return visibleRowCount;
	}

	public void setVisibleRowCount(Integer visibleRowCount) {
		FIBAttributeNotification<Integer> notification = requireChange(Parameters.visibleRowCount, visibleRowCount);
		if (notification != null) {
			this.visibleRowCount = visibleRowCount;
			hasChanged(notification);
		}
	}

	public Integer getRowHeight() {
		return rowHeight;
	}

	public void setRowHeight(Integer rowHeight) {
		FIBAttributeNotification<Integer> notification = requireChange(Parameters.rowHeight, rowHeight);
		if (notification != null) {
			this.rowHeight = rowHeight;
			hasChanged(notification);
		}
	}

	public boolean getBoundToSelectionManager() {
		return boundToSelectionManager;
	}

	public void setBoundToSelectionManager(boolean boundToSelectionManager) {
		FIBAttributeNotification<Boolean> notification = requireChange(Parameters.boundToSelectionManager, boundToSelectionManager);
		if (notification != null) {
			this.boundToSelectionManager = boundToSelectionManager;
			hasChanged(notification);
		}
	}

	public Vector<FIBBrowserElement> getElements() {
		return elements;
	}

	public void setElements(Vector<FIBBrowserElement> elements) {
		this.elements = elements;
		updateElementsForClasses();
	}

	public void addToElements(FIBBrowserElement anElement) {
		anElement.setBrowser(this);
		elements.add(anElement);
		updateElementsForClasses();
		setChanged();
		notifyObservers(new FIBAddingNotification<FIBBrowserElement>(Parameters.elements, anElement));
	}

	public void removeFromElements(FIBBrowserElement anElement) {
		anElement.setBrowser(null);
		elements.remove(anElement);
		updateElementsForClasses();
		setChanged();
		notifyObservers(new FIBRemovingNotification<FIBBrowserElement>(Parameters.elements, anElement));
	}

	public FIBBrowserElement createElement() {
		logger.info("Called createElement()");
		FIBBrowserElement newElement = new FIBBrowserElement();
		newElement.setName("element" + (elements.size() > 0 ? elements.size() : ""));
		addToElements(newElement);
		return newElement;
	}

	public FIBBrowserElement deleteElement(FIBBrowserElement elementToDelete) {
		logger.info("Called elementToDelete() with " + elementToDelete);
		removeFromElements(elementToDelete);
		return elementToDelete;
	}

	public void moveToTop(FIBBrowserElement e) {
		if (e == null) {
			return;
		}
		elements.remove(e);
		elements.insertElementAt(e, 0);
		setChanged();
		notifyObservers(new FIBAddingNotification<FIBBrowserElement>(Parameters.elements, e));
	}

	public void moveUp(FIBBrowserElement e) {
		if (e == null) {
			return;
		}
		int index = elements.indexOf(e);
		elements.remove(e);
		elements.insertElementAt(e, index - 1);
		setChanged();
		notifyObservers(new FIBAddingNotification<FIBBrowserElement>(Parameters.elements, e));
	}

	public void moveDown(FIBBrowserElement e) {
		if (e == null) {
			return;
		}
		int index = elements.indexOf(e);
		elements.remove(e);
		elements.insertElementAt(e, index + 1);
		setChanged();
		notifyObservers(new FIBAddingNotification<FIBBrowserElement>(Parameters.elements, e));
	}

	public void moveToBottom(FIBBrowserElement e) {
		if (e == null) {
			return;
		}
		elements.remove(e);
		elements.add(e);
		setChanged();
		notifyObservers(new FIBAddingNotification<FIBBrowserElement>(Parameters.elements, e));
	}

	public SelectionMode getSelectionMode() {
		return selectionMode;
	}

	public void setSelectionMode(SelectionMode selectionMode) {
		FIBAttributeNotification<SelectionMode> notification = requireChange(Parameters.selectionMode, selectionMode);
		if (notification != null) {
			this.selectionMode = selectionMode;
			hasChanged(notification);
		}
	}

	protected void updateElementsForClasses() {
		elementsForClasses.clear();
		for (FIBBrowserElement e : elements) {
			if (e.getDataClass() instanceof Class) {
				elementsForClasses.put(e.getDataClass(), e);
			}
		}
	}

	public FIBBrowserElement elementForClass(Class<?> aClass) {
		FIBBrowserElement returned = elementsForClasses.get(aClass);
		if (returned != null) {
			return returned;
		} else {
			Class<?> superclass = aClass.getSuperclass();
			if (superclass != null) {
				returned = elementsForClasses.get(aClass);
				if (returned != null) {
					return returned;
				} else {
					for (Class<?> superInterface : aClass.getInterfaces()) {
						returned = elementsForClasses.get(superInterface);
						if (returned != null) {
							return returned;
						}
					}
					returned = elementForClass(superclass);
					if (returned != null) {
						elementsForClasses.put(aClass, returned);
						return returned;
					} else {
						for (Class<?> superInterface : aClass.getInterfaces()) {
							returned = elementForClass(superInterface);
							if (returned != null) {
								elementsForClasses.put(aClass, returned);
								return returned;
							}
						}
					}
				}
			}
		}
		List<Class<?>> matchingClasses = new ArrayList<Class<?>>();
		for (Class<?> cl : elementsForClasses.keySet()) {
			if (cl.isAssignableFrom(aClass)) {
				matchingClasses.add(cl);
			}
		}
		if (matchingClasses.size() > 0) {
			return elementsForClasses.get(TypeUtils.getMostSpecializedClass(matchingClasses));
		}
		return null;
	}

	public boolean getShowFooter() {
		return showFooter;
	}

	public void setShowFooter(boolean showFooter) {
		FIBAttributeNotification<Boolean> notification = requireChange(Parameters.showFooter, showFooter);
		if (notification != null) {
			this.showFooter = showFooter;
			hasChanged(notification);
		}
	}

	public boolean getRootVisible() {
		return rootVisible;
	}

	public void setRootVisible(boolean rootVisible) {
		FIBAttributeNotification<Boolean> notification = requireChange(Parameters.rootVisible, rootVisible);
		if (notification != null) {
			this.rootVisible = rootVisible;
			hasChanged(notification);
		}
	}

	public boolean getShowRootsHandle() {
		return showRootsHandle;
	}

	public void setShowRootsHandle(boolean showRootsHandle) {
		FIBAttributeNotification<Boolean> notification = requireChange(Parameters.showRootsHandle, showRootsHandle);
		if (notification != null) {
			this.showRootsHandle = showRootsHandle;
			hasChanged(notification);
		}
	}

	public Color getTextSelectionColor() {
		return textSelectionColor;
	}

	public void setTextSelectionColor(Color textSelectionColor) {
		FIBAttributeNotification<Color> notification = requireChange(Parameters.textSelectionColor, textSelectionColor);
		if (notification != null) {
			this.textSelectionColor = textSelectionColor;
			hasChanged(notification);
		}
	}

	public Color getTextNonSelectionColor() {
		return textNonSelectionColor;
	}

	public void setTextNonSelectionColor(Color textNonSelectionColor) {
		FIBAttributeNotification<Color> notification = requireChange(Parameters.textNonSelectionColor, textNonSelectionColor);
		if (notification != null) {
			this.textNonSelectionColor = textNonSelectionColor;
			hasChanged(notification);
		}
	}

	public Color getBackgroundSelectionColor() {
		return backgroundSelectionColor;
	}

	public void setBackgroundSelectionColor(Color backgroundSelectionColor) {
		FIBAttributeNotification<Color> notification = requireChange(Parameters.backgroundSelectionColor, backgroundSelectionColor);
		if (notification != null) {
			this.backgroundSelectionColor = backgroundSelectionColor;
			hasChanged(notification);
		}
	}

	public Color getBackgroundSecondarySelectionColor() {
		return backgroundSecondarySelectionColor;
	}

	public void setBackgroundSecondarySelectionColor(Color backgroundSecondarySelectionColor) {
		FIBAttributeNotification<Color> notification = requireChange(Parameters.backgroundSecondarySelectionColor,
				backgroundSecondarySelectionColor);
		if (notification != null) {
			this.backgroundSecondarySelectionColor = backgroundSecondarySelectionColor;
			hasChanged(notification);
		}
	}

	public Color getBackgroundNonSelectionColor() {
		return backgroundNonSelectionColor;
	}

	public void setBackgroundNonSelectionColor(Color backgroundNonSelectionColor) {
		FIBAttributeNotification<Color> notification = requireChange(Parameters.backgroundNonSelectionColor, backgroundNonSelectionColor);
		if (notification != null) {
			this.backgroundNonSelectionColor = backgroundNonSelectionColor;
			hasChanged(notification);
		}
	}

	public Color getBorderSelectionColor() {
		return borderSelectionColor;
	}

	public void setBorderSelectionColor(Color borderSelectionColor) {
		FIBAttributeNotification<Color> notification = requireChange(Parameters.borderSelectionColor, borderSelectionColor);
		if (notification != null) {
			this.borderSelectionColor = borderSelectionColor;
			hasChanged(notification);
		}
	}

	@Override
	public void notifiedBindingModelRecreated() {
		super.notifiedBindingModelRecreated();
		for (FIBBrowserElement e : getElements()) {
			e.notifiedBindingModelRecreated();
		}
	}

}
