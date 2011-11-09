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
import java.lang.reflect.Type;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.controller.FIBMultipleValuesDynamicModel;
import org.openflexo.fib.model.FIBMultipleValues;
import org.openflexo.fib.view.FIBWidgetView;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.StringUtils;

public abstract class FIBMultipleValueWidget<W extends FIBMultipleValues, C extends JComponent, T> extends FIBWidgetView<W,C,T>
{

	static final Logger logger = Logger.getLogger(FIBMultipleValueWidget.class.getPackage().getName());

	public FIBMultipleValueWidget(W model, FIBController controller)
	{
		super(model,controller);
	}

	protected class FIBMultipleValueModel implements ListModel
	{
		private List list = null;
		private Object[] array = null;

		protected FIBMultipleValueModel()
		{
			super();

			logger.fine("Build FIBListModel");
						
			if (getWidget().getList() !=null
					&& getWidget().getList().isSet()
					&& getDataObject() != null) {
				
				Object accessedList = getWidget().getList().getBindingValue(getController());				
				if (accessedList instanceof List) list = (List)accessedList;

			}
			
			else if (getWidget().getArray() !=null
					&& getWidget().getArray().isSet()
					&& getDataObject() != null) {
				
				Object accessedArray = getWidget().getArray().getBindingValue(getController());
				//System.out.println("accessedArray="+accessedArray);
				try {
				array = (Object[])accessedArray;
				}
				catch(ClassCastException e) {
					logger.warning("ClassCastException "+e.getMessage());
				}
				/*for (int i=0; i<array.length; i++) {
					System.out.println("> "+array[i]);
				}*/				
			}
			
			else if (getWidget().getData() != null 
					&& getWidget().getData().getBinding() != null
					&& getDataObject() != null) {
				/*System.out.println("Binding: "+getWidget().getData().getBinding());
				System.out.println("isBindingValid: "+getWidget().getData().getBinding().isBindingValid());
				if (!getWidget().getData().getBinding().isBindingValid()) {
					System.out.println("Owner: "+getWidget().getData().getOwner());
					System.out.println("BindingModel: "+getWidget().getData().getOwner().getBindingModel());
					
					BindingExpression.logger.setLevel(Level.FINE);
					AbstractBinding binding = AbstractBinding.abstractBindingConverter.convertFromString(getWidget().getData().getBinding().toString());
					binding.isBindingValid();
				}*/
				Type type = getWidget().getData().getBinding().getAccessedType();
				if (type instanceof Class && ((Class)type).isEnum()) {
					array = ((Class)type).getEnumConstants();
				}
			}
			
			if (list == null && array == null && StringUtils.isNotEmpty(getWidget().getStaticList())) {
				list = new Vector();
				StringTokenizer st = new StringTokenizer(getWidget().getStaticList(),",");
				while (st.hasMoreTokens()) {
					list.add(st.nextToken());
				}
			}

		}

		private boolean requireChange = true;
		
		private boolean requireChange()
		{
			// Always return true first time
			if (requireChange) return true;
			requireChange = false;
			
			 if (getWidget().getList() !=null
					&& getWidget().getList().isSet()
					&& getDataObject() != null) {
				
				Object accessedList = getWidget().getList().getBindingValue(getController());				
				return (accessedList != null && !accessedList.equals(list));

			}
			
			else if (getWidget().getArray() !=null
					&& getWidget().getArray().isSet()
					&& getDataObject() != null) {
				
				Object accessedArray = getWidget().getArray().getBindingValue(getController());
				// TODO: you can do better
				return true;
			}
			
			else if (getWidget().getData() != null 
					&& getWidget().getData().getBinding() != null
					&& getDataObject() != null) {
				Type type = getWidget().getData().getBinding().getAccessedType();
				if (type instanceof Class && ((Class)type).isEnum()) {
					return false;
				}
			}
			
			else if (StringUtils.isNotEmpty(getWidget().getStaticList())) {
					return false;
				}

			return true;
		}
		
		@Override
		public int getSize()
		{
			if (list != null) return list.size();
			if (array != null) return array.length;
			return 0;
		}

		@Override
		public Object getElementAt(int index)
		{
			if (list != null && index >= 0 && index < list.size()) return list.get(index);
			if (array != null && index >= 0 && index < array.length) return array[index];
			return null;
		}

		@Override
		public void addListDataListener(ListDataListener l)
		{
			// Interface
		}

		@Override
		public void removeListDataListener(ListDataListener l)
		{
			// Interface
		}

		public int indexOf(Object cur)
		{
			for (int i = 0; i < getSize(); i++) {
				if (getElementAt(i).equals(cur)) {
					return i;
				}
			}
			return -1;
		}
		
		@Override
		public boolean equals(Object object) 
		{
			if (object instanceof FIBMultipleValueWidget<?,?,?>.FIBMultipleValueModel) {
				FIBMultipleValueModel object2 = (FIBMultipleValueModel)object;
				if (list != null) {
					if (object2.list == null) return false;
					else return list.equals(object2.list);
				}
				else if (array != null) {
					if (object2.array == null) return false;
					else return array.equals(object2.array);
				}
			}
			return super.equals(object);
		}
		
		@Override
		public String toString() 
		{
			return getClass().getSimpleName()+"["+(list!=null?list.toString():"null")+"]";
		}
	}

	protected boolean listModelRequireChange()
	{
		return getListModel().requireChange();
	}


	protected class FIBMultipleValueCellRenderer extends DefaultListCellRenderer
	{
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
		{
			FIBMultipleValueCellRenderer label = (FIBMultipleValueCellRenderer) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

			if (getWidget().getShowText()) {
				if (value != null) {
					String stringRepresentation = getStringRepresentation(value);
					if (stringRepresentation == null || stringRepresentation.length()==0)
						stringRepresentation = "<html><i>"+FlexoLocalization.localizedForKey("empty_string")+"</i></html>";
					label.setText(stringRepresentation);
				} else {
					label.setText(FlexoLocalization.localizedForKey("no_selection"));
				}
				label.setFont(FIBMultipleValueWidget.this.getFont());
			}
			else {
				label.setText(null);
			}
			//label.setBorder(BorderFactory.createEmptyBorder(1, 2, 1, 2));
			
			if (getWidget().getShowIcon()) {
				if (getWidget().getIcon() != null && getWidget().getIcon().isValid()) {
					label.setIcon(getIconRepresentation(value));
				}
			}
			
		   	//System.out.println("Actual preferred= "+list.getPreferredSize().getWidth());
		   	//System.out.println("I will prefer "+label.getPreferredSize().getWidth());
		   	
			return label;
		}
	}

	private FIBMultipleValueCellRenderer listCellRenderer;
	protected FIBMultipleValueModel listModel;

	public FIBMultipleValueCellRenderer getListCellRenderer()
	{
		if (listCellRenderer == null) {
			listCellRenderer = new FIBMultipleValueCellRenderer();
		}
		return listCellRenderer;
	}

	public FIBMultipleValueModel getListModel()
	{
		updateListModelWhenRequired();
		return listModel;
	}

	protected FIBMultipleValueModel updateListModelWhenRequired()
	{
		if (listModel == null) {
			listModel = new FIBMultipleValueModel();
		}
		return listModel;
	}
	
	protected FIBMultipleValueModel updateListModel()
	{
		listModel = null;
		updateListModelWhenRequired();
		return listModel;
	}
	
	/*protected final FIBListModel rebuildListModel()
	{
		return listModel = buildListModel();
	}
	
	protected final FIBListModel buildListModel()
	{
		return new FIBListModel();
	}*/
	
	@Override
	public final void updateDataObject(Object value)
	{
		updateListModel();
		super.updateDataObject(value);
	}

	@Override
	public FIBMultipleValuesDynamicModel<T,?> createDynamicModel()
	{
		return buildDynamicModel(getWidget().getIteratorClass());
	}
	
	private <O> FIBMultipleValuesDynamicModel<T,O> buildDynamicModel(Class<O> aClass)
	{
		return new FIBMultipleValuesDynamicModel<T,O>(null);
	}

	@Override
	public FIBMultipleValuesDynamicModel<T,Object> getDynamicModel()
	{
		return (FIBMultipleValuesDynamicModel<T,Object>)super.getDynamicModel();
	}



}
