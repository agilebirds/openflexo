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
package org.openflexo.fib.view;

import java.awt.Dimension;
import java.awt.Font;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import org.openflexo.fib.controller.FIBComponentDynamicModel;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.controller.FIBSelectable;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.Language;
import org.openflexo.localization.LocalizationListener;

public abstract class FIBView<M extends FIBComponent, J extends JComponent> implements LocalizationListener {

	   private static final Logger logger = Logger
       .getLogger(FIBView.class.getPackage().getName());

	private M component;
	private FIBController controller;
    private final FIBComponentDynamicModel dynamicModel;
	
	protected Vector<FIBView> subViews;

    private boolean visible = true;
    
    private boolean isDeleted = false;
    
	private JScrollPane scrolledComponent;
		
	public FIBView(M model, FIBController controller)
	{
		super();
		this.controller = controller;
		component = model;

		subViews = new Vector<FIBView>();

		dynamicModel = createDynamicModel();
		
		controller.registerView(this);
		
	}
	
	public void delete()
	{
		logger.fine("Delete view for component "+getComponent());
		for (FIBView v : subViews) v.delete();
		subViews.clear();
		subViews = null;
		isDeleted = true;
		if (controller != null) controller.unregisterView(this);
		if (dynamicModel != null) dynamicModel.delete();
		component = null;
		controller = null;
	}

	public boolean isDeleted()
	{
		return isDeleted;
	}

	public FIBController getController() 
	{
		return controller;
	}

	public final Object getDataObject()
	{
		return getController().getDataObject();
	}

	public final M getComponent()
	{
		return component;
	}

	public abstract void updateDataObject(Object anObject);
	
	public abstract void updateLanguage();

	/**
	 * Return the effective base component to be added to swing hierarchy
	 * This component may be encapsulated in a JScrollPane
	 * 
	 * @return JComponent
	 */
	public abstract JComponent getJComponent();
	
	/**
	 * Return the dynamic JComponent, ie the component on which dynamic
	 * is applied, and were actions are effective
	 * 
	 * @return J
	 */
	public abstract J getDynamicJComponent(); 

	/**
	 * Return the effective component to be added to swing hierarchy
	 * This component may be the same as the one returned by {@link #getJComponent()}
	 * or a encapsulation in a JScrollPane
	 * 
	 * @return JComponent
	 */
	public JComponent getResultingJComponent()
	{
		if (getComponent().getUseScrollBar()) {
			if (scrolledComponent == null) {
				scrolledComponent = new JScrollPane(
						getJComponent(),
						getComponent().getVerticalScrollbarPolicy().getPolicy(), 
						getComponent().getHorizontalScrollbarPolicy().getPolicy());
			}
			return scrolledComponent;
		}
		else {
			return getJComponent();
		}
	}
	
	public void update()
	{
		updateVisibility(true);
	}
	
    protected abstract boolean checkValidDataPath();
    

    public final boolean isComponentVisible()
    {
    	/*boolean debug = false;
		if (getComponent().getName() != null && getComponent().getName().equals("ColorBackgroundPanel")) {
			debug=true;
		}*/

		if (getParentView() != null && !getParentView().isComponentVisible()) return false;

		boolean componentVisible = true;
		if (getComponent().getVisible() != null && getComponent().getVisible().isSet()) {
			Object isVisible = getComponent().getVisible().getBindingValue(getController());
			/*if (debug) {
				System.out.println("getComponent().getVisible()="+getComponent().getVisible());
				System.out.println("Eh bien isVisible="+isVisible);
			}*/
			if (isVisible instanceof Boolean) componentVisible = (Boolean)isVisible;
		}
		if (!componentVisible) return false;
    	//logger.info("Please look at this !!!");
    	//if (getParentView() != null) return getParentView().isComponentVisible();
     	return true;
    }
     
	public final boolean hasValue() 
	{
		return component.getData() != null && component.getData().isSet();
	}

	private final void updateVisibility(boolean revalidateAndRepaint)
	{
		if (isComponentVisible()) {
			if (visible == false) {
				// Becomes visible
				performSetIsVisible(true);
				// Also update visibility for sub-components
				for (FIBView view : subViews) view.updateVisibility(false);
				if (getResultingJComponent().getParent() instanceof JComponent) ((JComponent)getResultingJComponent().getParent()).revalidate();
				else getResultingJComponent().getParent().validate();
				getResultingJComponent().getParent().repaint();
				visible = true;
				if (getDynamicModel() != null) {
					getDynamicModel().isVisible = true;
				}
				updateDataObject(getDataObject());
			}
		}
		else {
			if (visible == true) {
				// Becomes invisible
				performSetIsVisible(false);
				if (getResultingJComponent().getParent() instanceof JComponent) {
					((JComponent)getResultingJComponent().getParent()).revalidate();
				}
				else if (getResultingJComponent().getParent() != null) {
					getResultingJComponent().getParent().validate();
				}
				if (getResultingJComponent().getParent() != null) {
					getResultingJComponent().getParent().repaint();
				}
				visible = false;
				if (getDynamicModel() != null) {
					getDynamicModel().isVisible = false;
				}
			}
		}
	}

	protected void performSetIsVisible (boolean isVisible)
	{
		getResultingJComponent().setVisible(isVisible);
	}
	
	public FIBComponentDynamicModel createDynamicModel()
	{
		if (getComponent().getDataType() != null) {
			logger.fine("Create dynamic model "+this+" for "+getComponent());
			return new FIBComponentDynamicModel(getDefaultData());
		}
		return null;
	}
	
	public Object getDefaultData()
	{
		return null;
	}
	
	public FIBComponentDynamicModel getDynamicModel()
	{
		return dynamicModel;
	}

	public void notifyDynamicModelChanged()
    {
		//System.out.println("notifyDynamicModelChanged()");
    	Iterator<FIBComponent> it = getComponent().getMayAltersIterator();
    	while(it.hasNext()) {
    		FIBComponent c = it.next();
    		logger.fine("Because dynamic model change, now update "+c);
    		FIBView view = getController().viewForComponent(c);
    		if (view != null) {
    			view.updateDataObject(getDataObject());
    		}
    		else {
    			logger.warning("Unexpected null view when retrieving view for "+c);
    		}
    	}
    }

	public FIBView getParentView()
	{
		if (getComponent().getParent() != null) {
			return getController().viewForComponent(getComponent().getParent());
		}
		return null;
	}

	protected Vector<FIBView> getSubViews()
	{
		return subViews;
	}

	public Font getFont()
	{
		if (getComponent() != null)
			return getComponent().retrieveValidFont();
		return null;
	}
	
	
	public abstract void updateFont();

	public String getLocalized(String key)
	{
		return FlexoLocalization.localizedForKey(getController().getLocalizer(),key);
	}
	
	public boolean isSelectableComponent()
	{
		return this instanceof FIBSelectable;
	}
	
	public FIBSelectable getSelectableComponent()
	{
		if (isSelectableComponent()) return (FIBSelectable)this;
		return null;
	}

	@Override
	public void languageChanged(Language language)
	{
		updateLanguage();
	}
	
	public void updateGraphicalProperties()
	{
		updatePreferredSize();
		updateMaximumSize();
		updateMinimumSize();
		updateBackgroundColor();
		updateForegroundColor();
	}
	
	protected void updatePreferredSize()
    {
    	if (getComponent().definePreferredDimensions()) {
    		Dimension preferredSize = getJComponent().getPreferredSize();
    		if (getComponent().getWidth() != null) preferredSize.width = getComponent().getWidth();
    		if (getComponent().getHeight() != null) preferredSize.height = getComponent().getHeight();
    		getJComponent().setPreferredSize(preferredSize);
    	}
   }
    
	protected void updateMinimumSize()
    {
    	if (getComponent().defineMinDimensions()) {
    		Dimension minSize = getJComponent().getMinimumSize();
    		if (getComponent().getMinWidth() != null) minSize.width = getComponent().getMinWidth();
    		if (getComponent().getMinHeight() != null) minSize.height = getComponent().getMinHeight();
    		getJComponent().setMinimumSize(minSize);
    	}
   }
    
	protected void updateMaximumSize()
    {
    	if (getComponent().defineMaxDimensions()) {
    		Dimension maxSize = getJComponent().getMaximumSize();
    		if (getComponent().getMaxWidth() != null) maxSize.width = getComponent().getMaxWidth();
    		if (getComponent().getMaxHeight() != null) maxSize.height = getComponent().getMaxHeight();
    		getJComponent().setMinimumSize(maxSize);
    	}
   }
    
	protected void updateBackgroundColor()
    {
    	if (getComponent().getHasSpecificBackgroundColor()) {
    		getJComponent().setBackground(getComponent().getBackgroundColor());
    	}
   }
    
	protected void updateForegroundColor()
    {
    	if (getComponent().getHasSpecificForegroundColor()) {
    		getJComponent().setForeground(getComponent().getForegroundColor());
    	}
   }
	
	public static boolean equals(Object o1, Object o2)
	{
		if (o1 == o2) return true;
		if (o1 == null) return (o2 == null);
		else return o1.equals(o2);
	}

	public static boolean notEquals(Object o1, Object o2)
	{
		return !equals(o1,o2);
	}
}
