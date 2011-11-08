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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.controller.FIBCustomDynamicModel;
import org.openflexo.fib.controller.FIBSelectable;
import org.openflexo.fib.model.DataBinding;
import org.openflexo.fib.model.FIBCustom;
import org.openflexo.fib.model.FIBCustom.FIBCustomAssignment;
import org.openflexo.fib.model.FIBCustom.FIBCustomComponent;
import org.openflexo.fib.view.FIBWidgetView;
import org.openflexo.swing.CustomPopup.ApplyCancelListener;


/**
 * Defines an abstract custom widget
 * 
 * @author sguerin
 * 
 */
public class FIBCustomWidget<J extends JComponent,T> extends FIBWidgetView<FIBCustom,J,T>
implements ApplyCancelListener, BindingEvaluationContext
{

    private static final Logger logger = Logger.getLogger(FIBCustomWidget.class.getPackage().getName());

    private FIBCustomComponent<T,J> customComponent;

    
    private final JLabel ERROR_LABEL = new JLabel("<Cannot instanciate component>");
    
    public FIBCustomWidget(FIBCustom model, FIBController controller)
    {
    	super(model,controller);
    	try {
    		customComponent = makeCustomComponent((Class<FIBCustomComponent<T,J>>)model.getComponentClass(),(Class<T>)model.getDataType(),controller);
    	}
    	catch(ClassCastException e) {
    		logger.warning("Could not instanciate component: ClassCastException, see logs for details");
    		e.printStackTrace();
    	}
        if (customComponent != null)
        	customComponent.addApplyCancelListener(this);

        updateFont();
    }
    
    
    
    private FIBCustomComponent<T,J> makeCustomComponent(Class<FIBCustomComponent<T,J>> customComponentClass, Class<T> dataClass, FIBController controller)
    {
		if (customComponentClass == null) {
			logger.warning("Could not instanciate custom component : no component class found");
				return new NotFoundComponent();
			}
    	Class[] types = new Class[1];
    	types[0] = dataClass;
    	try {
    		boolean found = false;
 			Constructor<FIBCustomComponent<T,J>> constructor = null;
 			while (!found && types[0] != null) {
 				try {
 					constructor = customComponentClass.getConstructor(types);
 					found = true;
 				}
 				catch (NoSuchMethodException e) {
 					types[0] = types[0].getSuperclass();
 				}
 			}
 			if (constructor == null) {
 				for (Constructor c : customComponentClass.getConstructors()) {
 					if (c.getGenericParameterTypes().length == 1) {
 						constructor = c;
 						break;
 					}
 				}
 			}
 			if (constructor == null) {
				logger.warning("Could not instanciate class "+customComponentClass+" : no valid constructor found, (searched "+customComponentClass.getSimpleName()+"("+dataClass.getSimpleName()+")...)");
				return new NotFoundComponent();
 			}
	    	Object[] args = new Object[1];
	    	args[0] = null;
	    	FIBCustomComponent<T,J> returned = constructor.newInstance(args);
	    	returned.init(getComponent(), controller);
	    	if (getDynamicModel() != null) {
	    		getDynamicModel().customComponent = returned;
	    	}
	    	return returned;
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
    }

     /**
     * Must be overriden in sub-classes if required
     * 
     * @param value
     */
    protected void performModelUpdating(Object value)
    {
    }

    public void performModelUpdating()
    {
        performModelUpdating(getValue());
    }

    public T getEditedValue()
    {
        return getValue();
    }

    /**
     * Update the model given the actual state of the widget
     */
    @Override
	public synchronized boolean updateModelFromWidget()
    {
    	if (notEquals(getValue(), customComponent.getEditedObject())) {
    		setValue(customComponent.getEditedObject());
    		return true;
    	}
    	// Notify anyway (in case CustomWidget modify the same object, no change
    	// will be detected and this notification is required)
    	if (getWidget().getValueChangedAction().isValid()) {
    		getWidget().getValueChangedAction().execute(getController());
    	}
   	return false;
    }

 	@Override
	public J getJComponent() 
	{
 		if (customComponent == null) return (J)ERROR_LABEL;
		return customComponent.getJComponent();
	}

	@Override
	public J getDynamicJComponent()
	{
 		if (customComponent != null) return customComponent.getJComponent();
 		return null;
	}

	@Override
	public boolean updateWidgetFromModel() 
	{
		// We need here to "force" update while some assignments may be required
		
		//if (notEquals(getValue(), customComponent.getEditedObject())) {

		//logger.info("updateWidgetFromModel() with "+getValue()+" for "+customComponent);
		
			if (customComponent != null) {

				try {
					customComponent.setEditedObject(getValue());
					customComponent.setRevertValue(getValue());	
				} 
				catch (ClassCastException e) {
					logger.warning("Unexpected exception in "+customComponent+": "+e.getMessage());
				}

				for (FIBCustomAssignment assign : getWidget().getAssignments()) {
					DataBinding variableDB = assign.getVariable();
					DataBinding valueDB = assign.getValue();
					if (valueDB != null && valueDB.getBinding() != null && valueDB.getBinding().isBindingValid()) {
						Object value = valueDB.getBinding().getBindingValue(getController());
						if (variableDB.getBinding().isBindingValid()) {
							//System.out.println("Assignment "+assign+" set value with "+value);
							variableDB.getBinding().setBindingValue(value, this);
						}
					}
				}
			}
			return true;
		//}
		//return false;
	}

	@Override
	public void fireApplyPerformed() 
	{
		//logger.info("fireApplyPerformed() in FIBCustomWidget, value="+customComponent.getEditedObject());
		updateModelFromWidget();
	}

	@Override
	public void fireCancelPerformed() {
	}

	@Override
	public Object getValue(BindingVariable variable) 
	{
		if (variable.getVariableName().equals("component")) return customComponent;
		return null;
	}

	@Override
	public boolean isSelectableComponent()
	{
		return customComponent instanceof FIBSelectable;
	}
	
	@Override
	public FIBSelectable getSelectableComponent()
	{
		if (isSelectableComponent()) return (FIBSelectable)customComponent;
		return null;
	}
	
	@Override
	public FIBCustomDynamicModel<T,FIBCustomComponent<T,J>> createDynamicModel()
	{
		return new FIBCustomDynamicModel<T,FIBCustomComponent<T,J>>(null);
	}

	@Override
	public FIBCustomDynamicModel<T,FIBCustomComponent<T,J>> getDynamicModel() 
	{
		return (FIBCustomDynamicModel<T,FIBCustomComponent<T,J>>)super.getDynamicModel();
	}



	protected class NotFoundComponent implements FIBCustomComponent
	{

		private JLabel label;
		
		public NotFoundComponent() {
			label = new JLabel("Component not defined");
		}
		
		@Override
		public void init(FIBCustom component, FIBController controller) {
		}
		
		@Override
		public void addApplyCancelListener(ApplyCancelListener l) {
		}

		@Override
		public Object getEditedObject() {
			return null;
		}

		@Override
		public JComponent getJComponent() 
		{
			return label;
		}

		@Override
		public Class<T> getRepresentedType() {
			return null;
		}

		@Override
		public T getRevertValue() {
			return null;
		}

		@Override
		public void removeApplyCancelListener(ApplyCancelListener l) {
		}

		@Override
		public void setEditedObject(Object object) {
		}

		@Override
		public void setRevertValue(Object object) {
		}
		
	}
}
