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
package org.openflexo.fib.view.widget;

import java.awt.Component;
import java.awt.Dimension;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

import org.openflexo.antar.binding.BindingValueArrayChangeListener;
import org.openflexo.antar.binding.BindingValueListChangeListener;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBModelObject;
import org.openflexo.fib.model.FIBMultipleValues;
import org.openflexo.fib.view.FIBWidgetView;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.xmlcode.InvalidObjectSpecificationException;

/**
 * 
 * @author sylvain
 * 
 * @param <W>
 *            type of FIBComponent used as model (here a subclass of FIBMultipleValues)
 * @param <C>
 *            type of rendering component
 * @param <T>
 *            type of data being managed by this widget
 * @param <I>
 *            type of iterable beeing managed by this widget
 */
public abstract class FIBMultipleValueWidget<W extends FIBMultipleValues, C extends JComponent, T, I> extends FIBWidgetView<W, C, T> {

	static final Logger logger = Logger.getLogger(FIBMultipleValueWidget.class.getPackage().getName());

	public static final String SELECTED = "selected";
	public static final String SELECTED_INDEX = "selectedIndex";

	private T selected;
	private int selectedIndex;

	private FIBMultipleValueCellRenderer listCellRenderer;
	protected FIBMultipleValueModel<I> multipleValueModel;

	private BindingValueListChangeListener<Object, List<Object>> listBindingValueChangeListener;
	private BindingValueArrayChangeListener<Object> arrayBindingValueChangeListener;

	public FIBMultipleValueWidget(W model, FIBController controller) {
		super(model, controller);
		listenListValueChange();
		listenArrayValueChange();
		proceedToListModelUpdate();
	}

	private void listenListValueChange() {
		if (listBindingValueChangeListener != null) {
			listBindingValueChangeListener.stopObserving();
			listBindingValueChangeListener.delete();
		}
		if (getComponent().getList() != null && getComponent().getList().isValid()) {
			listBindingValueChangeListener = new BindingValueListChangeListener<Object, List<Object>>(
					(DataBinding<List<Object>>) ((DataBinding) getComponent().getList()), getBindingEvaluationContext()) {

				@Override
				public void bindingValueChanged(Object source, List<Object> newValue) {
					System.out.println(" bindingValueChanged() detected for list=" + getComponent().getEnable() + " with newValue="
							+ newValue + " source=" + source);
					proceedToListModelUpdate();
				}
			};
		}
	}

	private void listenArrayValueChange() {
		if (arrayBindingValueChangeListener != null) {
			arrayBindingValueChangeListener.stopObserving();
			arrayBindingValueChangeListener.delete();
		}
		if (getComponent().getArray() != null && getComponent().getArray().isValid()) {
			arrayBindingValueChangeListener = new BindingValueArrayChangeListener<Object>(getComponent().getArray(),
					getBindingEvaluationContext()) {
				@Override
				public void bindingValueChanged(Object source, Object[] newValue) {
					System.out.println(" bindingValueChanged() detected for array=" + getComponent().getArray() + " with newValue="
							+ newValue + " source=" + source);
					proceedToListModelUpdate();
				}
			};
		}
	}

	protected class FIBMultipleValueModel<T> implements ListModel {
		private List<T> list = null;
		private T[] array = null;

		protected FIBMultipleValueModel() {
			super();

			logger.fine("Build FIBListModel");

			if (getWidget().getList() != null && getWidget().getList().isValid() && getDataObject() != null) {

				Object accessedList = null;
				try {
					accessedList = getWidget().getList().getBindingValue(getBindingEvaluationContext());
				} catch (TypeMismatchException e) {
					e.printStackTrace();
				} catch (NullReferenceException e) {
					// e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (InvalidObjectSpecificationException e) {
					logger.warning("Unexpected InvalidObjectSpecificationException " + e);
					e.printStackTrace();
				}
				if (accessedList instanceof List) {
					list = (List) accessedList;
				}

			}

			else if (getWidget().getArray() != null && getWidget().getArray().isValid() && getDataObject() != null) {

				Object accessedArray = null;
				try {
					accessedArray = getWidget().getArray().getBindingValue(getBindingEvaluationContext());
				} catch (TypeMismatchException e1) {
					e1.printStackTrace();
				} catch (NullReferenceException e1) {
					e1.printStackTrace();
				} catch (InvocationTargetException e1) {
					e1.printStackTrace();
				}
				// System.out.println("accessedArray="+accessedArray);
				try {
					array = (T[]) accessedArray;
				} catch (ClassCastException e) {
					logger.warning("ClassCastException " + e.getMessage());
				}
				/*for (int i=0; i<array.length; i++) {
					System.out.println("> "+array[i]);
				}*/
			}

			else if (getWidget().getData() != null && getWidget().getData().isValid() && getDataObject() != null) {
				/*System.out.println("Binding: "+getWidget().getData().getBinding());
				System.out.println("isBindingValid: "+getWidget().getData().getBinding().isBindingValid());
				if (!getWidget().getData().getBinding().isBindingValid()) {
					System.out.println("Owner: "+getWidget().getData().getOwner());
					System.out.println("BindingModel: "+getWidget().getData().getOwner().getBindingModel());
					
					BindingExpression.logger.setLevel(Level.FINE);
					AbstractBinding binding = AbstractBinding.abstractBindingConverter.convertFromString(getWidget().getData().getBinding().toString());
					binding.isBindingValid();
				}*/
				Type type = getWidget().getData().getAnalyzedType();
				if (type instanceof Class && ((Class) type).isEnum()) {
					array = (T[]) ((Class) type).getEnumConstants();
				}
			}

			if (list == null && array == null && getWidget().getIteratorClass() != null && getWidget().getIteratorClass().isEnum()) {
				array = (T[]) getWidget().getIteratorClass().getEnumConstants();
			}

			if (list == null && array == null && StringUtils.isNotEmpty(getWidget().getStaticList())) {
				list = new ArrayList<T>();
				StringTokenizer st = new StringTokenizer(getWidget().getStaticList(), ",");
				while (st.hasMoreTokens()) {
					list.add((T) st.nextToken());
				}
			}

			// logger.info("Built list model: " + this);

		}

		private boolean requireChange = true;

		private boolean requireChange() {
			if (requireChange) {
				if (getWidget().getList() != null && !getWidget().getList().isSet() && getWidget().getArray() != null
						&& !getWidget().getArray().isSet()) {
					requireChange = false;
				}
				// Always return true first time
				return true;
			}
			requireChange = false;

			if (getWidget().getList() != null && getWidget().getList().isSet() && getDataObject() != null) {

				Object accessedList = null;
				try {
					accessedList = getWidget().getList().getBindingValue(getBindingEvaluationContext());
				} catch (TypeMismatchException e) {
					e.printStackTrace();
				} catch (NullReferenceException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
				return accessedList != null && !accessedList.equals(list);

			}

			else if (getWidget().getArray() != null && getWidget().getArray().isSet() && getDataObject() != null) {

				try {
					Object accessedArray = getWidget().getArray().getBindingValue(getBindingEvaluationContext());
				} catch (TypeMismatchException e) {
					e.printStackTrace();
				} catch (NullReferenceException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
				// TODO: you can do better
				return true;
			}

			else if (getWidget().getData() != null && getWidget().getData().isValid() && getDataObject() != null) {
				Type type = getWidget().getData().getAnalyzedType();
				if (type instanceof Class && ((Class<?>) type).isEnum()) {
					return false;
				}
			}

			else if (StringUtils.isNotEmpty(getWidget().getStaticList())) {
				return false;
			}

			if (getWidget().getList() != null && !getWidget().getList().isSet() && getWidget().getArray() != null
					&& !getWidget().getArray().isSet()) {
				return false;
			}

			return true;
		}

		@Override
		public int getSize() {
			if (list != null) {
				return list.size();
			}
			if (array != null) {
				return array.length;
			}
			return 0;
		}

		@Override
		public T getElementAt(int index) {
			if (list != null && index >= 0 && index < list.size()) {
				return list.get(index);
			}
			if (array != null && index >= 0 && index < array.length) {
				return array[index];
			}
			return null;
		}

		@Override
		public void addListDataListener(ListDataListener l) {
			// Interface
		}

		@Override
		public void removeListDataListener(ListDataListener l) {
			// Interface
		}

		public int indexOf(Object cur) {
			for (int i = 0; i < getSize(); i++) {
				if (getElementAt(i).equals(cur)) {
					return i;
				}
			}
			return -1;
		}

		/*@Override
		public FIBMultipleValueModel clone() {
			FIBMultipleValueModel returned = new FIBMultipleValueModel();
			if (list != null) {
				returned.list = new ArrayList(list);
			} else if (array != null) {
				returned.array = new Object[array.length];
				for (int i = 0; i < array.length; i++) {
					returned.array[i] = array[i];
				}
			}
			return returned;
		}*/

		@Override
		public String toString() {
			return getClass().getSimpleName() + "[" + (list != null ? list.size() + " " + list.toString() : "null") + "]";
		}

		protected ArrayList<Object> toArrayList() {
			ArrayList<Object> returned = new ArrayList<Object>();
			for (int i = 0; i < getSize(); i++) {
				returned.add(getElementAt(i));
			}
			return returned;
		}

		protected boolean equalsToList(List l) {
			if (l == null) {
				return getSize() == 0;
			}
			if (getSize() == l.size()) {
				for (int i = 0; i < getSize(); i++) {
					if (!getElementAt(i).equals(l.get(i))) {
						return false;
					}
				}
				return true;
			} else {
				return false;
			}
		}
	}

	protected boolean listModelRequireChange() {
		return getMultipleValueModel().requireChange();
	}

	protected class FIBMultipleValueCellRenderer extends DefaultListCellRenderer {
		private Dimension nullDimesion;

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			FIBMultipleValueCellRenderer label = (FIBMultipleValueCellRenderer) super.getListCellRendererComponent(list, value, index,
					isSelected, cellHasFocus);
			if (value != null && nullDimesion == null) {
				nullDimesion = ((JComponent) getListCellRendererComponent(list, null, -1, false, false)).getPreferredSize();
			}
			if (getWidget().getShowText()) {
				if (value != null) {
					String stringRepresentation = getStringRepresentation(value);
					if (stringRepresentation == null || stringRepresentation.length() == 0) {
						stringRepresentation = "<html><i>" + FlexoLocalization.localizedForKey(FIBModelObject.LOCALIZATION, "empty_string")
								+ "</i></html>";
					}
					label.setText(stringRepresentation);
				} else {
					label.setText(FlexoLocalization.localizedForKey(FIBModelObject.LOCALIZATION, "no_selection"));
				}
				if (FIBMultipleValueWidget.this.getFont() != null) {
					label.setFont(FIBMultipleValueWidget.this.getFont());
				}
			} else {
				label.setText(null);
			}
			// label.setBorder(BorderFactory.createEmptyBorder(1, 2, 1, 2));

			if (getWidget().getShowIcon()) {
				if (getWidget().getIcon() != null && getWidget().getIcon().isValid()) {
					label.setIcon(getIconRepresentation(value));
				}
			}

			// System.out.println("Actual preferred= "+list.getPreferredSize().getWidth());
			// System.out.println("I will prefer "+label.getPreferredSize().getWidth());

			return label;
		}

		@Override
		public Dimension getPreferredSize() {
			Dimension preferredSize = super.getPreferredSize();
			if (nullDimesion != null) {
				preferredSize.width = Math.max(preferredSize.width, nullDimesion.width);
				preferredSize.height = Math.max(preferredSize.height, nullDimesion.height);
			}
			return preferredSize;
		}

		@Override
		public void updateUI() {
			nullDimesion = null;
			super.updateUI();
		}
	}

	public FIBMultipleValueCellRenderer getListCellRenderer() {
		if (listCellRenderer == null) {
			listCellRenderer = new FIBMultipleValueCellRenderer();
		}
		return listCellRenderer;
	}

	public FIBMultipleValueModel<I> getMultipleValueModel() {
		if (multipleValueModel == null) {
			multipleValueModel = createMultipleValueModel();
		}
		return multipleValueModel;
	}

	protected abstract FIBMultipleValueModel<I> createMultipleValueModel();

	protected abstract void proceedToListModelUpdate();

	/*protected FIBMultipleValueModel updateListModelWhenRequired() {
		if (multipleValueModel == null) {
			multipleValueModel = new FIBMultipleValueModel();
		} else {
			FIBMultipleValueModel aNewListModel = new FIBMultipleValueModel();
			if (!aNewListModel.equals(multipleValueModel) || didLastKnownValuesChange()) {
				multipleValueModel = aNewListModel;
			}
		}
		return multipleValueModel;
	}*/

	/*protected FIBMultipleValueModel updateListModel() {
		multipleValueModel = null;
		updateListModelWhenRequired();
		return multipleValueModel;
	}*/

	// private ArrayList<Object> lastKnownValues = null;

	/**
	 * Return a flag indicating if last known values declared as ListModel have changed since the last time this method was called.
	 * 
	 * @return
	 */
	/*protected boolean didLastKnownValuesChange() {
		boolean returned;
		if (multipleValueModel != null) {
			returned = !multipleValueModel.equalsToList(lastKnownValues);
			lastKnownValues = multipleValueModel.toArrayList();
		} else {
			returned = lastKnownValues != null;
			lastKnownValues = null;
		}
		return returned;
	}*/

	/*protected final FIBListModel rebuildListModel()
	{
		return multipleValueModel = buildListModel();
	}
	
	protected final FIBListModel buildListModel()
	{
		return new FIBListModel();
	}*/

	/*@Override
	public final void updateDataObject(final Object dataObject) {
		if (!SwingUtilities.isEventDispatchThread()) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Update data object invoked outside the EDT!!! please investigate and make sure this is no longer the case. \n\tThis is a very SERIOUS problem! Do not let this pass.");
			}
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					updateDataObject(dataObject);
				}
			});
			return;
		}
		updateListModelWhenRequired();
		super.updateDataObject(dataObject);
	}*/

	@Override
	public boolean update() {
		super.update();
		proceedToListModelUpdate();
		return true;
	}

	@Override
	public void updateLanguage() {
		super.updateLanguage();
		if (getComponent().getLocalize()) {
			FIBMultipleValueModel mvModel = getMultipleValueModel();
			for (int i = 0; i < mvModel.getSize(); i++) {
				String s = getStringRepresentation(mvModel.getElementAt(i));
				getLocalized(s);
			}
		}
	}

	public T getSelected() {
		return selected;
	}

	public void setSelected(T selected) {
		T oldSelected = this.selected;
		if (oldSelected != selected) {
			this.selected = selected;
			getPropertyChangeSupport().firePropertyChange(SELECTED, oldSelected, selected);
		}
	}

	public int getSelectedIndex() {
		return selectedIndex;
	}

	public void setSelectedIndex(int selectedIndex) {
		int oldSelectedIndex = this.selectedIndex;
		if (oldSelectedIndex != selectedIndex) {
			this.selectedIndex = selectedIndex;
			getPropertyChangeSupport().firePropertyChange(SELECTED_INDEX, oldSelectedIndex, selectedIndex);
		}
	}

}
