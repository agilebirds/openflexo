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

import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.tree.TreeNode;

import org.openflexo.fib.model.FIBPanel.Layout;


public abstract class FIBContainer extends FIBComponent {

	private static final Logger logger = Logger.getLogger(FIBContainer.class.getPackage().getName());

	private Vector<FIBComponent> subComponents;
	
	public static enum Parameters implements FIBModelAttribute
	{
		subComponents
	}

	public FIBContainer()
	{
		subComponents = new Vector<FIBComponent>();
	}
	
	public Vector<FIBComponent> getSubComponents()
	{
		return subComponents;
	}
	
	public void setSubComponents(Vector<FIBComponent> someComponents)
	{
		FIBAttributeNotification<Vector<FIBComponent>> notification = requireChange(Parameters.subComponents,someComponents);
		if (notification != null) {
			subComponents = someComponents;
			hasChanged(notification);
		}
	}
	
	public void addToSubComponents(FIBComponent aComponent)
	{
		addToSubComponents(aComponent,null);
	}
	
	public void addToSubComponents(FIBComponent aComponent,ComponentConstraints someConstraints)
	{
		aComponent.setParent(this);
		if (someConstraints != null) {
			aComponent.getConstraints().ignoreNotif = true;
			for (String key : someConstraints.keySet()) {
				//System.out.println(aComponent.getConstraints().getClass().getName()+": Put constraint "+key+"="+someConstraints.get(key));
				aComponent.getConstraints().put(key,someConstraints.get(key));
			}
			aComponent.getConstraints().ignoreNotif = false;
		}
		subComponents.add(aComponent);
		if ((aComponent instanceof FIBWidget)
				&& ((FIBWidget)aComponent).getManageDynamicModel()) {
			if (deserializationPerformed) updateBindingModel();
		}
		setChanged();
		notifyObservers(new FIBAddingNotification<FIBComponent>(Parameters.subComponents, aComponent));
	}
	
	public FIBComponent getSubComponentNamed(String name)
	{
		for (FIBComponent c : subComponents) {
			if (c.getName() != null && c.getName().equals(name)) return c;
		}
		return null;
	}
	
	public void removeFromSubComponents(FIBComponent aComponent)
	{
		removeFromSubComponentsNoNotification(aComponent);
		setChanged();
		notifyObservers(new FIBRemovingNotification<FIBComponent>(Parameters.subComponents, aComponent));
	}
		
	public void removeFromSubComponentsNoNotification(FIBComponent aComponent)
	{
		aComponent.setParent(null);
		subComponents.remove(aComponent);
	}
		
	@Override
	public Enumeration<FIBComponent> children() 
	{
		return getSubComponents().elements();
	}

	@Override
	public boolean getAllowsChildren() 
	{
		return true;
	}

	@Override
	public FIBComponent getChildAt(int childIndex) 
	{
		return getSubComponents().get(childIndex);
	}

	@Override
	public int getChildCount() 
	{
		return getSubComponents().size();
	}

	@Override
	public int getIndex(TreeNode node) 
	{
		return getSubComponents().indexOf(node);
	}

	@Override
	public boolean isLeaf() 
	{
		return getSubComponents().size() == 0;
	}

	//public static final String INHERITED = "Inherited";
	
	public void append(FIBContainer container)
	{
		//logger.info(toString()+" append "+container);
		
		//if (this instanceof FIBTab && ())
		
		Vector<FIBComponent> addedComponents = new Vector<FIBComponent>();
		for (int i=container.getSubComponents().size()-1; i>=0; i--) {
			FIBComponent c2 = container.getSubComponents().get(i);
			boolean merged = false;
			if (c2.getName() != null && (c2 instanceof FIBContainer)) {
				for (FIBComponent c1 : getSubComponents()) {
					if (c2.getName().equals(c1.getName()) && (c1 instanceof FIBContainer)) {
						((FIBContainer)c1).append((FIBContainer)c2);
						merged = true;
						logger.fine("Merged "+c1+" and "+c2);
						break;
					}
				}
			}
			if (!merged) {
				// Is there a component already named same as the one to be added ?
				// (In this case, do NOT add it, the redefinition override parent behaviour)
				FIBComponent overridingComponent = getSubComponentNamed(c2.getName());
				if (overridingComponent == null) {
					c2.setParent(this);
					Integer previousIndex = null;
					if (subComponents != null 
							&& subComponents.size() > 0
							&& subComponents.firstElement().getConstraints() != null
							&& subComponents.firstElement().getConstraints().hasIndex())
						previousIndex = subComponents.firstElement().getConstraints().getIndex();
					//if (previousIndex == null) previousIndex=0;
					subComponents.insertElementAt(c2, 0);
					if (previousIndex != null && c2.getConstraints() != null && !c2.getConstraints().hasIndex()) c2.getConstraints().setIndexNoNotification(previousIndex-1);
					//logger.fine("Added "+c2+" into "+this+" index="+(previousIndex-1));
					addedComponents.add(c2);
					//c2.finalizeDeserialization();
				}
				else {
					if (overridingComponent.getParameter("hidden") != null
							&& overridingComponent.getParameter("hidden").equalsIgnoreCase("true")) {
						// Super property must be shadowed
						removeFromSubComponents(overridingComponent);
					}
				}
			}
		}
		
		if (container.getLocalizedDictionary() != null) {
			retrieveFIBLocalizedDictionary().append(container.getLocalizedDictionary());
		}
		
		updateBindingModel();
		for (FIBComponent c : addedComponents) recursivelyFinalizeDeserialization(c);
		finalizeDeserialization();
	}
	
	private void recursivelyFinalizeDeserialization(FIBComponent c) 
	{
		c.finalizeDeserialization();
		if (c instanceof FIBContainer) {
			for (FIBComponent c2 : ((FIBContainer) c).getSubComponents()) recursivelyFinalizeDeserialization(c2);
		}
	}

	// Default layout is built-in: only FIBPanel manage a custom layout, 
	// where this method is overriden
	public Layout getLayout() 
	{
		return null;
	}

	// Not permitted since default layout is built-in: only FIBPanel 
	// manage a custom layout, where this method is overriden
	public void setLayout(Layout layout) {
	}

	@Override
	public void finalizeDeserialization()
	{
		super.finalizeDeserialization();

		//int currentIndex = 0;

		/*System.out.println("*********************************************");

		System.out.println("Avant le bazar: ");
		for (FIBComponent c : subComponents) {
			if (c.getConstraints() != null) {
				if (!c.getConstraints().hasIndex()) {
					System.out.println("> Index: ? "+c);
				}
				else {
					System.out.println("> Index: "+c.getConstraints().getIndex()+" "+c);
				}
			}
		}*/
		
		/*for (FIBComponent c : subComponents) {
			if (c.getConstraints() != null) {
				if (!c.getConstraints().hasIndex()) {
					c.getConstraints().setIndex(currentIndex);
					currentIndex++;
				}
				else {
					currentIndex = c.getConstraints().getIndex()+1;
				}
			}
		}*/
		
		/*System.out.println("Apres le bazar: ");
		for (FIBComponent c : subComponents) {
			if (c.getConstraints() != null) {
				if (!c.getConstraints().hasIndex()) {
					System.out.println("> Index: ? "+c);
				}
				else {
					System.out.println("> Index: "+c.getConstraints().getIndex()+" "+c);
				}
			}
		}
		
		System.out.println("*********************************************");*/

	}

	public void oldFinalizeDeserialization()
	{
		super.finalizeDeserialization();

		int currentIndex = 0;

		for (FIBComponent c : subComponents) {
			if (c.getConstraints() != null) {
				if (!c.getConstraints().hasIndex()) {
					c.getConstraints().setIndex(currentIndex);
					currentIndex++;
				}
				else {
					int desiredIndex = c.getConstraints().getIndex();
					if (desiredIndex >= currentIndex) {
						currentIndex = desiredIndex+1;						
					}
					else {
						//System.out.println("Ah ca chie, on a un index de "+desiredIndex+" alors qu'on est a: "+currentIndex+" pour "+c);
						c.getConstraints().setIndex(currentIndex);
						currentIndex++;
					}
					//currentIndex = c.getConstraints().getIndex()+1;
				}
			}
		}
	}

	public void componentFirst(FIBComponent c)
	{
		sortComponentsUsingIndex();
		subComponents.remove(c);
		subComponents.insertElementAt(c, 0);
		reindexComponents();
		notifyComponentIndexChanged(c);
	}
	
	public void componentUp(FIBComponent c)
	{
		sortComponentsUsingIndex();
		int index = subComponents.indexOf(c);
		subComponents.remove(c);
		subComponents.insertElementAt(c,index-1);
		reindexComponents();
		notifyComponentIndexChanged(c);
	}
	
	public void componentDown(FIBComponent c)
	{
		sortComponentsUsingIndex();
		int index = subComponents.indexOf(c);
		subComponents.remove(c);
		subComponents.insertElementAt(c,index+1);
		reindexComponents();
		notifyComponentIndexChanged(c);
	}
	
	public void componentLast(FIBComponent c)
	{
		sortComponentsUsingIndex();
		subComponents.remove(c);
		subComponents.add(c);
		reindexComponents();
		notifyComponentIndexChanged(c);
	}
	
	public void recursivelyReorderComponents()
	{
		reorderComponents();
		for (FIBComponent c : getSubComponents()) {
			if (c instanceof FIBContainer) ((FIBContainer)c).recursivelyReorderComponents();
		}
	}

	public void reorderComponents()
	{
		sortComponentsUsingIndex();
		reindexComponents();
	}
	
	private void notifyComponentIndexChanged(FIBComponent component)
	{
		FIBAttributeNotification<ComponentConstraints> notification 
		= new FIBAttributeNotification<ComponentConstraints>(FIBComponent.Parameters.constraints, component.getConstraints(), component.getConstraints());
		component.notify(notification);
		setChanged();
		notifyObservers(new FIBAttributeNotification<Vector<FIBComponent>>(Parameters.subComponents, subComponents));
	}

	private void sortComponentsUsingIndex()
	{
		Collections.sort(subComponents,new Comparator<FIBComponent>() {
			@Override
			public int compare(FIBComponent c1, FIBComponent c2) {
				if (c1.getConstraints() == null || c2.getConstraints() == null) return 0;
				return c1.getConstraints().getIndex()-c2.getConstraints().getIndex();
			};
		});
	}

	private void reindexComponents()
	{
		int index = 0;
		for (FIBComponent c : subComponents) {
			if (c.getConstraints() != null) {
				c.getConstraints().setIndexNoNotification(index++);
			}
		}
	}

}
