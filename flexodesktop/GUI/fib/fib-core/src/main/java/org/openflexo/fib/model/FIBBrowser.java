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

import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.ParameterizedTypeImpl;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.fib.model.validation.ValidationReport;
import org.openflexo.fib.view.widget.FIBBrowserWidget;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(FIBBrowser.FIBBrowserImpl.class)
@XMLElement(xmlTag = "Browser")
public interface FIBBrowser extends FIBWidget {

	/*public enum SelectionMode {
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
	}*/

	@PropertyIdentifier(type = DataBinding.class)
	public static final String ROOT_KEY = "root";
	@PropertyIdentifier(type = Class.class)
	public static final String ITERATOR_CLASS_KEY = "iteratorClass";
	@PropertyIdentifier(type = Integer.class)
	public static final String VISIBLE_ROW_COUNT_KEY = "visibleRowCount";
	@PropertyIdentifier(type = Integer.class)
	public static final String ROW_HEIGHT_KEY = "rowHeight";
	@PropertyIdentifier(type = boolean.class)
	public static final String BOUND_TO_SELECTION_MANAGER_KEY = "boundToSelectionManager";
	@PropertyIdentifier(type = SelectionMode.class)
	public static final String SELECTION_MODE_KEY = "selectionMode";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String SELECTED_KEY = "selected";
	@PropertyIdentifier(type = Vector.class)
	public static final String ELEMENTS_KEY = "elements";
	@PropertyIdentifier(type = boolean.class)
	public static final String SHOW_FOOTER_KEY = "showFooter";
	@PropertyIdentifier(type = boolean.class)
	public static final String ROOT_VISIBLE_KEY = "rootVisible";
	@PropertyIdentifier(type = boolean.class)
	public static final String SHOW_ROOTS_HANDLE_KEY = "showRootsHandle";
	@PropertyIdentifier(type = Color.class)
	public static final String TEXT_SELECTION_COLOR_KEY = "textSelectionColor";
	@PropertyIdentifier(type = Color.class)
	public static final String TEXT_NON_SELECTION_COLOR_KEY = "textNonSelectionColor";
	@PropertyIdentifier(type = Color.class)
	public static final String BACKGROUND_SELECTION_COLOR_KEY = "backgroundSelectionColor";
	@PropertyIdentifier(type = Color.class)
	public static final String BACKGROUND_SECONDARY_SELECTION_COLOR_KEY = "backgroundSecondarySelectionColor";
	@PropertyIdentifier(type = Color.class)
	public static final String BACKGROUND_NON_SELECTION_COLOR_KEY = "backgroundNonSelectionColor";
	@PropertyIdentifier(type = Color.class)
	public static final String BORDER_SELECTION_COLOR_KEY = "borderSelectionColor";

	@Getter(value = ROOT_KEY)
	@XMLAttribute
	public DataBinding<Object> getRoot();

	@Setter(ROOT_KEY)
	public void setRoot(DataBinding<Object> root);

	@Getter(value = ITERATOR_CLASS_KEY)
	@XMLAttribute(xmlTag = "iteratorClassName")
	public Class getIteratorClass();

	@Setter(ITERATOR_CLASS_KEY)
	public void setIteratorClass(Class iteratorClass);

	@Getter(value = VISIBLE_ROW_COUNT_KEY)
	@XMLAttribute
	public Integer getVisibleRowCount();

	@Setter(VISIBLE_ROW_COUNT_KEY)
	public void setVisibleRowCount(Integer visibleRowCount);

	@Getter(value = ROW_HEIGHT_KEY)
	@XMLAttribute
	public Integer getRowHeight();

	@Setter(ROW_HEIGHT_KEY)
	public void setRowHeight(Integer rowHeight);

	@Getter(value = BOUND_TO_SELECTION_MANAGER_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getBoundToSelectionManager();

	@Setter(BOUND_TO_SELECTION_MANAGER_KEY)
	public void setBoundToSelectionManager(boolean boundToSelectionManager);

	@Getter(value = SELECTION_MODE_KEY)
	@XMLAttribute
	public SelectionMode getSelectionMode();

	@Setter(SELECTION_MODE_KEY)
	public void setSelectionMode(SelectionMode selectionMode);

	@Getter(value = SELECTED_KEY)
	@XMLAttribute
	public DataBinding<Object> getSelected();

	@Setter(SELECTED_KEY)
	public void setSelected(DataBinding<Object> selected);

	@Getter(value = ELEMENTS_KEY, cardinality = Cardinality.LIST, inverse = FIBBrowserElement.OWNER_KEY)
	public List<FIBBrowserElement> getElements();

	@Setter(ELEMENTS_KEY)
	public void setElements(List<FIBBrowserElement> elements);

	@Adder(ELEMENTS_KEY)
	public void addToElements(FIBBrowserElement aElement);

	@Remover(ELEMENTS_KEY)
	public void removeFromElements(FIBBrowserElement aElement);

	@Getter(value = SHOW_FOOTER_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getShowFooter();

	@Setter(SHOW_FOOTER_KEY)
	public void setShowFooter(boolean showFooter);

	@Getter(value = ROOT_VISIBLE_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getRootVisible();

	@Setter(ROOT_VISIBLE_KEY)
	public void setRootVisible(boolean rootVisible);

	@Getter(value = SHOW_ROOTS_HANDLE_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getShowRootsHandle();

	@Setter(SHOW_ROOTS_HANDLE_KEY)
	public void setShowRootsHandle(boolean showRootsHandle);

	@Getter(value = TEXT_SELECTION_COLOR_KEY)
	@XMLAttribute
	public Color getTextSelectionColor();

	@Setter(TEXT_SELECTION_COLOR_KEY)
	public void setTextSelectionColor(Color textSelectionColor);

	@Getter(value = TEXT_NON_SELECTION_COLOR_KEY)
	@XMLAttribute
	public Color getTextNonSelectionColor();

	@Setter(TEXT_NON_SELECTION_COLOR_KEY)
	public void setTextNonSelectionColor(Color textNonSelectionColor);

	@Getter(value = BACKGROUND_SELECTION_COLOR_KEY)
	@XMLAttribute
	public Color getBackgroundSelectionColor();

	@Setter(BACKGROUND_SELECTION_COLOR_KEY)
	public void setBackgroundSelectionColor(Color backgroundSelectionColor);

	@Getter(value = BACKGROUND_SECONDARY_SELECTION_COLOR_KEY)
	@XMLAttribute
	public Color getBackgroundSecondarySelectionColor();

	@Setter(BACKGROUND_SECONDARY_SELECTION_COLOR_KEY)
	public void setBackgroundSecondarySelectionColor(Color backgroundSecondarySelectionColor);

	@Getter(value = BACKGROUND_NON_SELECTION_COLOR_KEY)
	@XMLAttribute
	public Color getBackgroundNonSelectionColor();

	@Setter(BACKGROUND_NON_SELECTION_COLOR_KEY)
	public void setBackgroundNonSelectionColor(Color backgroundNonSelectionColor);

	@Getter(value = BORDER_SELECTION_COLOR_KEY)
	@XMLAttribute
	public Color getBorderSelectionColor();

	@Setter(BORDER_SELECTION_COLOR_KEY)
	public void setBorderSelectionColor(Color borderSelectionColor);

	public FIBBrowserElement elementForClass(Class<?> aClass);

	public static abstract class FIBBrowserImpl extends FIBWidgetImpl implements FIBBrowser {

		private static final Logger logger = Logger.getLogger(FIBBrowser.class.getPackage().getName());

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

		// private Vector<FIBBrowserElement> elements;

		private final Hashtable<Class, FIBBrowserElement> elementsForClasses;

		public FIBBrowserImpl() {
			// elements = new Vector<FIBBrowserElement>();
			elementsForClasses = new Hashtable<Class, FIBBrowserElement>();
		}

		@Override
		public String getBaseName() {
			return "Browser";
		}

		@Override
		public DataBinding<Object> getRoot() {
			if (root == null) {
				root = new DataBinding<Object>(this, Object.class, DataBinding.BindingDefinitionType.GET);
			}
			return root;
		}

		@Override
		public void setRoot(DataBinding<Object> root) {
			if (root != null) {
				root.setOwner(this);
				root.setDeclaredType(Object.class);
				root.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			}
			this.root = root;
		}

		@Override
		public DataBinding<Object> getSelected() {
			if (selected == null) {
				selected = new DataBinding<Object>(this, getIteratorClass(), DataBinding.BindingDefinitionType.GET_SET);
			}
			return selected;
		}

		@Override
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

		@Override
		public void updateBindingModel() {
			super.updateBindingModel();
			for (FIBBrowserElement e : getElements()) {
				e.updateBindingModel();
			}
		}

		@Override
		public Class getIteratorClass() {
			if (iteratorClass == null) {
				iteratorClass = Object.class;
			}
			return iteratorClass;

		}

		@Override
		public void setIteratorClass(Class iteratorClass) {
			FIBPropertyNotification<Class> notification = requireChange(ITERATOR_CLASS_KEY, iteratorClass);
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
			return new ParameterizedTypeImpl(FIBBrowserWidget.class, args);
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

		@Override
		public Integer getVisibleRowCount() {
			return visibleRowCount;
		}

		@Override
		public void setVisibleRowCount(Integer visibleRowCount) {
			FIBPropertyNotification<Integer> notification = requireChange(VISIBLE_ROW_COUNT_KEY, visibleRowCount);
			if (notification != null) {
				this.visibleRowCount = visibleRowCount;
				hasChanged(notification);
			}
		}

		@Override
		public Integer getRowHeight() {
			return rowHeight;
		}

		@Override
		public void setRowHeight(Integer rowHeight) {
			FIBPropertyNotification<Integer> notification = requireChange(ROW_HEIGHT_KEY, rowHeight);
			if (notification != null) {
				this.rowHeight = rowHeight;
				hasChanged(notification);
			}
		}

		@Override
		public boolean getBoundToSelectionManager() {
			return boundToSelectionManager;
		}

		@Override
		public void setBoundToSelectionManager(boolean boundToSelectionManager) {
			FIBPropertyNotification<Boolean> notification = requireChange(BOUND_TO_SELECTION_MANAGER_KEY, boundToSelectionManager);
			if (notification != null) {
				this.boundToSelectionManager = boundToSelectionManager;
				hasChanged(notification);
			}
		}

		/*@Override
		public Vector<FIBBrowserElement> getElements() {
			return elements;
		}*/

		@Override
		public void setElements(List<FIBBrowserElement> elements) {
			performSuperSetter(ELEMENTS_KEY, elements);
			updateElementsForClasses();
		}

		@Override
		public void addToElements(FIBBrowserElement anElement) {
			performSuperAdder(ELEMENTS_KEY, anElement);
			updateElementsForClasses();
		}

		@Override
		public void removeFromElements(FIBBrowserElement anElement) {
			performSuperRemover(ELEMENTS_KEY, anElement);
			updateElementsForClasses();
		}

		public FIBBrowserElement createElement() {
			logger.info("Called createElement()");
			FIBBrowserElement newElement = getFactory().newInstance(FIBBrowserElement.class);
			newElement.setName("element" + (getElements().size() > 0 ? getElements().size() : ""));
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
			getElements().remove(e);
			getElements().add(0, e);
			getPropertyChangeSupport().firePropertyChange(ELEMENTS_KEY, null, getElements());
		}

		public void moveUp(FIBBrowserElement e) {
			if (e == null) {
				return;
			}
			int index = getElements().indexOf(e);
			getElements().remove(e);
			getElements().add(index - 1, e);
			getPropertyChangeSupport().firePropertyChange(ELEMENTS_KEY, null, getElements());
		}

		public void moveDown(FIBBrowserElement e) {
			if (e == null) {
				return;
			}
			int index = getElements().indexOf(e);
			getElements().remove(e);
			getElements().add(index + 1, e);
			getPropertyChangeSupport().firePropertyChange(ELEMENTS_KEY, null, getElements());
		}

		public void moveToBottom(FIBBrowserElement e) {
			if (e == null) {
				return;
			}
			getElements().remove(e);
			getElements().add(e);
			getPropertyChangeSupport().firePropertyChange(ELEMENTS_KEY, null, getElements());
		}

		@Override
		public SelectionMode getSelectionMode() {
			return selectionMode;
		}

		@Override
		public void setSelectionMode(SelectionMode selectionMode) {
			FIBPropertyNotification<SelectionMode> notification = requireChange(SELECTION_MODE_KEY, selectionMode);
			if (notification != null) {
				this.selectionMode = selectionMode;
				hasChanged(notification);
			}
		}

		protected void updateElementsForClasses() {
			elementsForClasses.clear();
			for (FIBBrowserElement e : getElements()) {
				if (e.getDataClass() instanceof Class) {
					elementsForClasses.put(e.getDataClass(), e);
				}
			}
		}

		@Override
		public FIBBrowserElement elementForClass(Class<?> aClass) {
			FIBBrowserElement returned = elementsForClasses.get(aClass);
			if (returned != null) {
				return returned;
			} else {
				Class<?> superclass = aClass.getSuperclass();
				if (superclass != null) {
					returned = elementsForClasses.get(superclass);
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

		@Override
		public boolean getShowFooter() {
			return showFooter;
		}

		@Override
		public void setShowFooter(boolean showFooter) {
			FIBPropertyNotification<Boolean> notification = requireChange(SHOW_FOOTER_KEY, showFooter);
			if (notification != null) {
				this.showFooter = showFooter;
				hasChanged(notification);
			}
		}

		@Override
		public boolean getRootVisible() {
			return rootVisible;
		}

		@Override
		public void setRootVisible(boolean rootVisible) {
			FIBPropertyNotification<Boolean> notification = requireChange(ROOT_VISIBLE_KEY, rootVisible);
			if (notification != null) {
				this.rootVisible = rootVisible;
				hasChanged(notification);
			}
		}

		@Override
		public boolean getShowRootsHandle() {
			return showRootsHandle;
		}

		@Override
		public void setShowRootsHandle(boolean showRootsHandle) {
			FIBPropertyNotification<Boolean> notification = requireChange(SHOW_ROOTS_HANDLE_KEY, showRootsHandle);
			if (notification != null) {
				this.showRootsHandle = showRootsHandle;
				hasChanged(notification);
			}
		}

		@Override
		public Color getTextSelectionColor() {
			return textSelectionColor;
		}

		@Override
		public void setTextSelectionColor(Color textSelectionColor) {
			FIBPropertyNotification<Color> notification = requireChange(TEXT_SELECTION_COLOR_KEY, textSelectionColor);
			if (notification != null) {
				this.textSelectionColor = textSelectionColor;
				hasChanged(notification);
			}
		}

		@Override
		public Color getTextNonSelectionColor() {
			return textNonSelectionColor;
		}

		@Override
		public void setTextNonSelectionColor(Color textNonSelectionColor) {
			FIBPropertyNotification<Color> notification = requireChange(TEXT_NON_SELECTION_COLOR_KEY, textNonSelectionColor);
			if (notification != null) {
				this.textNonSelectionColor = textNonSelectionColor;
				hasChanged(notification);
			}
		}

		@Override
		public Color getBackgroundSelectionColor() {
			return backgroundSelectionColor;
		}

		@Override
		public void setBackgroundSelectionColor(Color backgroundSelectionColor) {
			FIBPropertyNotification<Color> notification = requireChange(BACKGROUND_SELECTION_COLOR_KEY, backgroundSelectionColor);
			if (notification != null) {
				this.backgroundSelectionColor = backgroundSelectionColor;
				hasChanged(notification);
			}
		}

		@Override
		public Color getBackgroundSecondarySelectionColor() {
			return backgroundSecondarySelectionColor;
		}

		@Override
		public void setBackgroundSecondarySelectionColor(Color backgroundSecondarySelectionColor) {
			FIBPropertyNotification<Color> notification = requireChange(BACKGROUND_SECONDARY_SELECTION_COLOR_KEY,
					backgroundSecondarySelectionColor);
			if (notification != null) {
				this.backgroundSecondarySelectionColor = backgroundSecondarySelectionColor;
				hasChanged(notification);
			}
		}

		@Override
		public Color getBackgroundNonSelectionColor() {
			return backgroundNonSelectionColor;
		}

		@Override
		public void setBackgroundNonSelectionColor(Color backgroundNonSelectionColor) {
			FIBPropertyNotification<Color> notification = requireChange(BACKGROUND_NON_SELECTION_COLOR_KEY, backgroundNonSelectionColor);
			if (notification != null) {
				this.backgroundNonSelectionColor = backgroundNonSelectionColor;
				hasChanged(notification);
			}
		}

		@Override
		public Color getBorderSelectionColor() {
			return borderSelectionColor;
		}

		@Override
		public void setBorderSelectionColor(Color borderSelectionColor) {
			FIBPropertyNotification<Color> notification = requireChange(BORDER_SELECTION_COLOR_KEY, borderSelectionColor);
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

		@Override
		protected void applyValidation(ValidationReport report) {
			super.applyValidation(report);
			performValidation(RootBindingMustBeValid.class, report);
		}

		/**
		 * Return a list of all bindings declared in the context of this component
		 * 
		 * @return
		 */
		@Override
		public List<DataBinding<?>> getDeclaredBindings() {
			List<DataBinding<?>> returned = super.getDeclaredBindings();
			returned.add(getSelected());
			returned.add(getRoot());
			return returned;
		}

		public static class RootBindingMustBeValid extends BindingMustBeValid<FIBBrowser> {
			public RootBindingMustBeValid() {
				super("'root'_binding_is_not_valid", FIBBrowser.class);
			}

			@Override
			public DataBinding<?> getBinding(FIBBrowser object) {
				return object.getRoot();
			}

		}

		public static class SelectedBindingMustBeValid extends BindingMustBeValid<FIBBrowser> {
			public SelectedBindingMustBeValid() {
				super("'selected'_binding_is_not_valid", FIBBrowser.class);
			}

			@Override
			public DataBinding<?> getBinding(FIBBrowser object) {
				return object.getSelected();
			}

		}

	}
}
