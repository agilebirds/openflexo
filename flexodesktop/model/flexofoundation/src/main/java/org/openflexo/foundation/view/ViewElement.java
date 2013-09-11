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
package org.openflexo.foundation.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.antar.binding.AbstractBinding.TargetObject;
import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.foundation.ontology.EditionPatternInstance;
import org.openflexo.foundation.ontology.EditionPatternReference;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.GraphicalElementPatternRole;
import org.openflexo.foundation.viewpoint.GraphicalElementSpecification;
import org.openflexo.foundation.viewpoint.binding.ViewPointDataBinding;
import org.openflexo.foundation.xml.VEShemaBuilder;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.HasPropertyChangeSupport;

public abstract class ViewElement extends ViewObject implements Bindable, PropertyChangeListener, Observer {

	private static final Logger logger = Logger.getLogger(ViewElement.class.getPackage().getName());

	private final Vector<TargetObject> dependingObjects = new Vector<TargetObject>();
	private boolean dependingObjectsAreComputed = false;

	/**
	 * Constructor invoked during deserialization
	 * 
	 * @param componentDefinition
	 */
	public ViewElement(VEShemaBuilder builder) {
		this(builder.shema);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor for OEShema
	 * 
	 * @param shemaDefinition
	 */
	public ViewElement(View shema) {
		super(shema);
	}

	@Override
	public void delete() {
		if (getEditionPatternInstance() != null && !getEditionPatternInstance().isDeleted()) {
			getEditionPatternInstance().delete();
		}
		for (TargetObject o : dependingObjects) {
			if (o.target instanceof HasPropertyChangeSupport) {
				PropertyChangeSupport pcSupport = ((HasPropertyChangeSupport) o.target).getPropertyChangeSupport();
				// logger.info("Widget "+getWidget()+" remove property change listener: "+o.target+" property:"+o.propertyName);
				pcSupport.removePropertyChangeListener(o.propertyName, this);
			} else if (o.target instanceof Observable) {
				// logger.info("Widget "+getWidget()+" remove observable: "+o);
				((Observable) o.target).deleteObserver(this);
			}
		}
		dependingObjects.clear();
		super.delete();
	}

	/*@Override
	public String getName() {
		if (isBoundInsideEditionPattern()) {
			return getLabelValue();
		}
		return super.getName();
	}

	@Override
	public void setName(String name) {
		// logger.info("setName of OEShemaElement with "+name);
		if (isBoundInsideEditionPattern()) {
			setLabelValue(name);
		} else {
			try {
				super.setName(name);
			} catch (DuplicateResourceException e) {
				// cannot happen
				e.printStackTrace();
			} catch (InvalidNameException e) {
				// cannot happen
				e.printStackTrace();
			}
		}
	}*/

	// public abstract AddShemaElementAction getEditionAction();

	/**
	 * Return a flag indicating if current graphical element is bound inside an edition pattern, ie if there is one edition pattern instance
	 * where this element plays a role as primary representation, or is included in an element declared as playing a role as primary
	 * representation
	 * 
	 * @return
	 */
	public boolean isBoundInsideEditionPattern() {
		return getPatternRole() != null;
	}

	@Override
	public GraphicalRepresentation<? extends ViewElement> getGraphicalRepresentation() {
		return (GraphicalRepresentation<? extends ViewElement>) super.getGraphicalRepresentation();
	}

	/**
	 * Return a flag indicating if current graphical element is bound inside an edition pattern, and if this element plays a role as primary
	 * representation,
	 * 
	 * @return
	 */
	public boolean playsPrimaryRole() {
		return getPatternRole() != null && getPatternRole().getIsPrimaryRepresentationRole();
	}

	/*public String getLabelValue() {
		if (getPatternRole() != null) {
			//if (!dependingObjectsAreComputed) {
			//	updateDependingObjects();
			//}
			return (String) getPatternRole().getLabel().getBindingValue(getEditionPatternInstance());
		}
		return null;
	}

	public void setLabelValue(String aValue) {
		if (getPatternRole() != null && !getPatternRole().getReadOnlyLabel()) {
			getPatternRole().getLabel().setBindingValue(aValue, getEditionPatternInstance());
		}
	}*/

	public EditionPattern getEditionPattern() {
		if (getEditionPatternInstance() != null) {
			return getEditionPatternInstance().getPattern();
		}
		return null;
	}

	public GraphicalElementPatternRole getPatternRole() {
		EditionPatternReference ref = getEditionPatternReference();
		if (ref != null && ref.getPatternRole() instanceof GraphicalElementPatternRole) {
			return (GraphicalElementPatternRole) ref.getPatternRole();
		}
		return null;
	}

	/**
	 * Return EditionPatternReference for that object<br>
	 * 
	 * If many EditionPatternReferences are defined for this object, return preferabely an EditionPatternReference where this object plays a
	 * primary role
	 * 
	 * @return
	 */
	public EditionPatternReference getEditionPatternReference() {
		// Default behaviour is to have only one EditionPattern where
		// this graphical element plays a representation primitive role
		// When many, big pbs may happen !!!!

		if (getEditionPatternReferences() != null) {
			if (getEditionPatternReferences().size() > 0) {
				EditionPatternReference returned = null;
				for (EditionPatternReference r : getEditionPatternReferences()) {
					if (r.getPatternRole() instanceof GraphicalElementPatternRole) {
						GraphicalElementPatternRole grPatternRole = (GraphicalElementPatternRole) r.getPatternRole();
						if (grPatternRole.getIsPrimaryRepresentationRole()) {
							if (returned != null) {
								if (logger.isLoggable(Level.WARNING)) {
									logger.warning("More than one edition pattern reference where element plays a primary role 1 !!!!");
								}
								for (EditionPatternReference r2 : getEditionPatternReferences()) {
									if (logger.isLoggable(Level.WARNING)) {
										logger.warning("> " + r2.getEditionPatternInstance().debug());
									}
								}
							}
							returned = r;
						} else if (grPatternRole.isIncludedInPrimaryRepresentationRole()) {
							if (returned != null) {
								if (logger.isLoggable(Level.WARNING)) {
									logger.warning("More than one edition pattern reference where element plays a primary role 2 !!!!");
								}
								for (EditionPatternReference r2 : getEditionPatternReferences()) {
									if (logger.isLoggable(Level.WARNING)) {
										logger.warning("> " + r2.getEditionPatternInstance().debug());
									}
								}
							}
							returned = r;
						}
					}
				}
				if (returned != null) {
					return returned;
				}
			}
		}

		if (getEditionPatternReferences() != null && getEditionPatternReferences().size() > 0) {
			return getEditionPatternReferences().get(0);
		}

		return null;
	}

	/**
	 * Return EditionPatternInstance for that object<br>
	 * 
	 * If many EditionPatternInstance are defined for this object, return preferably an EditionPatternReference where this object plays a
	 * primary role
	 * 
	 * @return
	 */
	public EditionPatternInstance getEditionPatternInstance() {
		if (getEditionPatternReference() != null) {
			return getEditionPatternReference().getEditionPatternInstance();
		}
		if (getEditionPatternReferences() != null && getEditionPatternReferences().size() > 0) {
			return getEditionPatternReferences().get(0).getEditionPatternInstance();
		}
		return null;
	}

	@Override
	public String getInspectorTitle() {
		for (EditionPatternReference ref : getEditionPatternReferences()) {
			return FlexoLocalization.localizedForKey(ref.getEditionPattern().getName());
		}
		// Otherwise, take default inspector name
		return super.getInspectorTitle();
	}

	@Override
	public BindingFactory getBindingFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BindingModel getBindingModel() {
		// TODO Auto-generated method stub
		return null;
	}

	private synchronized void updateDependingObjects() {
		ArrayList<TargetObject> newDependingObjects = new ArrayList<TargetObject>();
		ArrayList<TargetObject> deletedDependingObjects = new ArrayList<TargetObject>();
		deletedDependingObjects.addAll(dependingObjects);
		if (getDependingObjects() != null) {
			for (TargetObject o : getDependingObjects()) {
				if (deletedDependingObjects.contains(o)) {
					deletedDependingObjects.remove(o);
				} else {
					newDependingObjects.add(o);
				}
			}
		}
		for (TargetObject o : deletedDependingObjects) {
			dependingObjects.remove(o);
			if (o.target instanceof HasPropertyChangeSupport) {
				PropertyChangeSupport pcSupport = ((HasPropertyChangeSupport) o.target).getPropertyChangeSupport();
				// System.out.println("Element " + this + " remove property change listener: " + o.target + " property:" + o.propertyName);
				pcSupport.removePropertyChangeListener(o.propertyName, this);
			} else if (o.target instanceof Observable) {
				// System.out.println("Element " + this + " remove observable: " + o);
				((Observable) o.target).deleteObserver(this);
			} else {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Element " + this + " cannot stop observe: " + o);
				}
			}
		}
		for (TargetObject o : newDependingObjects) {
			dependingObjects.add(o);
			if (o.target instanceof HasPropertyChangeSupport) {
				PropertyChangeSupport pcSupport = ((HasPropertyChangeSupport) o.target).getPropertyChangeSupport();
				// System.out.println("Element " + this + " add property change listener: " + o.target + " property:" + o.propertyName);
				pcSupport.addPropertyChangeListener(o.propertyName, this);
			} else if (o.target instanceof Observable) {
				// System.out.println("Element " + this + " add observable: " + o);
				((Observable) o.target).addObserver(this);
			} else {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Element " + this + " cannot observe: " + o);
				}
			}
		}

		// debug
		/*if (getPatternRole() != null && getPatternRole().getPatternRoleName().equals("conceptLabel")) {
			System.out.println("Je suis le conceptLabel, je depends de: " + getPatternRole().getLabel());
			for (TargetObject o : dependingObjects) {
				System.out.println("> [" + o.propertyName + "] " + o.target);
			}
		}*/

		dependingObjectsAreComputed = true;
	}

	protected synchronized void appendToDependingObjects(ViewPointDataBinding binding, List<TargetObject> returned) {
		if (binding.isSet()) {
			List<TargetObject> list = binding.getBinding().getTargetObjects(getEditionPatternInstance());
			/*for (String patternRole : getEditionPatternInstance().getActors().keySet()) {
				list.add(new TargetObject(target, patternRole));
			}*/
			if (list != null) {
				for (TargetObject t : list) {
					if (!returned.contains(t)) {
						returned.add(t);
					}
				}
			}
		}
	}

	public synchronized List<TargetObject> getDependingObjects() {
		List<TargetObject> returned = new ArrayList<TargetObject>();
		if (getPatternRole() != null) {
			appendToDependingObjects(getPatternRole().getLabel(), returned);
		}
		return returned;
	}

	@Override
	public void update(Observable o, Object arg) {
		// System.out.println("**************> ViewElement " + this + " : receive notification " + arg + " observable=" + o);
		update();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// Note: this object observes two kind of objects
		// - objects that are relevant in the context of the computing of their representation, such as label
		// - they also observe their GR
		// If the case of their GR observing, update() should not be invoked, only invoke setChanged() in order
		// to resource to be flagged as modified
		// logger.info("**************> ViewElement " + this + " : receive PropertyChangeEvent " + evt.getPropertyName() + " source="
		// + evt.getSource().getClass().getSimpleName() + " evt=" + evt);
		if (evt.getSource() == getGraphicalRepresentation()) {
			// We just want here to track events such as object moving, just to flag the resource as modified
			// Ignore focused or selected events, because goal here is to mark resource as modified
			if (!evt.getPropertyName().equals(GraphicalRepresentation.Parameters.isFocused.name())
					&& !evt.getPropertyName().equals(GraphicalRepresentation.Parameters.isSelected.name())) {
				// System.out.println("setChanged() because of " + evt.getPropertyName() + " evt=" + evt);
				setChanged();
			}
		} else {
			// In this case, we really need to update object, because an object involved in the computing of
			// the label for instance has changed
			update();
		}
	}

	/**
	 * This method is called whenever a change has been detected potentially affecting underlying graphical representation Depending objects
	 * are recomputed, and notification of potential change is thrown, to be later caught by underlying GR (VEShapeGR or VEConnectorGR)
	 */
	public void update() {
		// System.out.println("Update in ViewElement " + this + ", text=" + getLabelValue());
		updateDependingObjects();
		setChanged();
		notifyObservers(new ElementUpdated(this));
	}

	/**
	 * Apply all graphical element specifications as it was defined in related pattern role
	 */
	protected void applyGraphicalElementSpecifications() {
		if (getPatternRole() != null) {
			for (GraphicalElementSpecification grSpec : getPatternRole().getGrSpecifications()) {
				if (grSpec.getValue().isValid()) {
					grSpec.applyToGraphicalRepresentation(getGraphicalRepresentation(), this);
				}
			}
		}

	}

	/**
	 * Reset graphical representation to be the one defined in related pattern role
	 */
	public abstract void resetGraphicalRepresentation();

	/**
	 * Refresh graphical representation
	 */
	public void refreshGraphicalRepresentation() {
		applyGraphicalElementSpecifications();
	}

	@Override
	public void setGraphicalRepresentation(GraphicalRepresentation<?> graphicalRepresentation) {
		super.setGraphicalRepresentation(graphicalRepresentation);
		update();
	}

	/**
	 * Return the index of this ViewElement, relative to its position in the list of ViewObject declared to be of same EditionPattern
	 * 
	 * @return
	 */
	public int getIndexRelativeToEPType() {
		if (getParent() == null) {
			return -1;
		}
		return getParent().getChildsOfType(getEditionPattern()).indexOf(this);
	}

	/**
	 * Sets the index of this ViewElement, relative to its position in the list of ViewObject declared to be of same EditionPattern
	 * 
	 * @param index
	 */
	public void setIndexRelativeToEPType(int index) {
		if (getIndexRelativeToEPType() != index && !isDeserializing()) {
			getParent().setIndexForChildRelativeToEPType(this, index);
		}
	}

	protected void notifyIndexChange() {
		refreshGraphicalRepresentation();
	}

}
