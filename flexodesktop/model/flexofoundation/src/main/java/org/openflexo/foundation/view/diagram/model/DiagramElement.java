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
package org.openflexo.foundation.view.diagram.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InvalidNameException;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.TargetObject;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.foundation.NameChanged;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.utils.FlexoModelObjectReference;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.view.VirtualModelInstance;
import org.openflexo.foundation.view.VirtualModelInstanceObject;
import org.openflexo.foundation.view.diagram.model.dm.ConnectorInserted;
import org.openflexo.foundation.view.diagram.model.dm.ConnectorRemoved;
import org.openflexo.foundation.view.diagram.model.dm.ElementUpdated;
import org.openflexo.foundation.view.diagram.model.dm.ShapeInserted;
import org.openflexo.foundation.view.diagram.model.dm.ShapeRemoved;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramSpecification;
import org.openflexo.foundation.view.diagram.viewpoint.GraphicalElementPatternRole;
import org.openflexo.foundation.view.diagram.viewpoint.GraphicalElementSpecification;
import org.openflexo.foundation.view.diagram.viewpoint.action.GRTemplate;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.HasPropertyChangeSupport;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

public abstract class DiagramElement<GR extends GraphicalRepresentation> extends VirtualModelInstanceObject implements GRTemplate,
		Bindable, PropertyChangeListener, Observer {

	private static final Logger logger = Logger.getLogger(DiagramElement.class.getPackage().getName());

	private final Vector<TargetObject> dependingObjects = new Vector<TargetObject>();
	private boolean dependingObjectsAreComputed = false;

	private Diagram diagram;
	private String name;
	private DiagramElement<?> parent = null;
	private Vector<DiagramElement<?>> childs;
	private Vector<DiagramElement<?>> ancestors;

	private GR graphicalRepresentation;

	// =======================================================
	// ================== Constructor =======================
	// =======================================================

	/**
	 * Default constructor
	 */
	public DiagramElement(Diagram diagram) {
		super(diagram.getProject());
		this.diagram = diagram;
		childs = new Vector<DiagramElement<?>>();
		ancestors = new Vector<DiagramElement<?>>();
	}

	@Override
	public VirtualModelInstance<?, ?> getVirtualModelInstance() {
		return getDiagram();
	}

	public Diagram getDiagram() {
		return diagram;
	}

	public DiagramFactory getFactory() {
		return diagram.getFactory();
	}

	@Override
	public DiagramSpecification getDiagramSpecification() {
		return getDiagram().getDiagramSpecification();
	}

	/**
	 * Returns reference to the main object in which this XML-serializable object is contained relating to storing scheme: here it's the
	 * component library
	 * 
	 * @return the component library
	 */
	@Override
	public XMLStorageResourceData getXMLResourceData() {
		return getDiagram();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) throws DuplicateResourceException, InvalidNameException {
		if (requireChange(this.name, name)) {
			String oldName = name;
			this.name = name;
			graphicalRepresentation.setText(name);
			setChanged();
			notifyObservers(new NameChanged(oldName, name));
		}
		
	}

	public Vector<DiagramElement<?>> getChilds() {
		return childs;
	}

	public void setChilds(Vector<DiagramElement<?>> someChilds) {
		ancestors = null;
		childs.addAll(someChilds);
	}

	public void addToChilds(DiagramElement<?> aChild) {
		// logger.info("****** addToChild() put "+aChild+" under "+this);
		ancestors = null;
		childs.add(aChild);
		aChild.setParent(this);
		setChanged();
		if (aChild instanceof DiagramShape) {
			notifyObservers(new ShapeInserted((DiagramShape) aChild, this));
		}
		if (aChild instanceof DiagramConnector) {
			notifyObservers(new ConnectorInserted((DiagramConnector) aChild));
		}
	}

	public void removeFromChilds(DiagramElement<?> aChild) {
		ancestors = null;
		childs.remove(aChild);
		setChanged();
		if (aChild instanceof DiagramShape) {
			notifyObservers(new ShapeRemoved((DiagramShape) aChild, this));
		}
		if (aChild instanceof DiagramConnector) {
			notifyObservers(new ConnectorRemoved((DiagramConnector) aChild));
		}
	}

	/**
	 * Re-index child, as it is defined in diagram hierarchy
	 * 
	 * @param aChild
	 * @param newIndex
	 */
	protected void setIndexForChild(DiagramElement<?> aChild, int newIndex) {
		if (childs.contains(aChild) && childs.indexOf(aChild) != newIndex) {
			childs.remove(aChild);
			childs.insertElementAt(aChild, newIndex);
			for (DiagramElement<?> o : childs) {
				o.notifyIndexChange();
			}
		}
	}

	/**
	 * Return the index of this ViewElement, as it is defined in diagram hierarchy
	 * 
	 * @return
	 */
	public int getIndex() {
		if (getParent() == null) {
			return -1;
		}
		return getParent().getChilds().indexOf(this);
	}

	/**
	 * Sets the index of this ViewElement, as it is defined in diagram hierarchy
	 * 
	 * @param index
	 */
	public void setIndex(int index) {
		if (getIndex() != index && !isDeserializing() && this instanceof DiagramElement<?>) {
			getParent().setIndexForChild(this, index);
		}
	}

	/**
	 * Re-index child, relative to its position in the list of DiagramElement declared to be of same EditionPattern
	 * 
	 * @param aChild
	 * @param newIndex
	 */
	protected void setIndexForChildRelativeToEPType(DiagramElement<?> aChild, int newIndex) {
		List<DiagramElement<?>> childsOfRightType = getChildsOfType(aChild.getEditionPattern());
		if (childsOfRightType.contains(aChild) && childsOfRightType.indexOf(aChild) != newIndex) {
			if (newIndex > 0) {
				DiagramElement<?> previousElement = childsOfRightType.get(newIndex - 1);
				int previousElementIndex = childs.indexOf(previousElement);
				childs.remove(aChild);
				if (previousElementIndex + 1 <= childs.size()) {
					childs.insertElementAt(aChild, previousElementIndex + 1);
				} else {
					childs.insertElementAt(aChild, childs.size());
				}
			} else {
				DiagramElement<?> firstElement = childsOfRightType.get(0);
				int firstElementIndex = childs.indexOf(firstElement);
				childs.remove(aChild);
				childs.insertElementAt(aChild, firstElementIndex);
			}
			for (DiagramElement<?> o : childs) {
				o.notifyIndexChange();
			}
		}
	}

	public List<DiagramElement<?>> getChildsOfType(EditionPattern editionPattern) {
		ArrayList<DiagramElement<?>> returned = new ArrayList<DiagramElement<?>>();
		for (DiagramElement<?> e : getChilds()) {
			if (e.getEditionPattern() == editionPattern) {
				returned.add(e);
			}
		}
		return returned;
	}

	public <T extends DiagramElement<?>> Collection<T> getChildrenOfType(final Class<T> type) {
		return getChildrenOfType(type, true);
	}

	@SuppressWarnings("unchecked")
	// We can remove the warning because the code performs the necessary checks
	public <T extends DiagramElement<?>> Collection<T> getChildrenOfType(final Class<T> type, boolean recursive) {
		Collection<T> objects = (Collection<T>) Collections2.filter(new ArrayList<DiagramElement<?>>(childs),
				new Predicate<DiagramElement<?>>() {
					@Override
					public boolean apply(DiagramElement<?> input) {
						return type.isAssignableFrom(input.getClass());
					}
				});
		if (recursive) {
			for (DiagramElement<?> object : childs) {
				objects.addAll(object.getChildrenOfType(type, true));
			}
		}
		return objects;
	}

	public DiagramShape getShapeNamed(String name) {
		for (DiagramElement<?> o : childs) {
			if (o instanceof DiagramShape && o.getName() != null && o.getName().equals(name)) {
				return (DiagramShape) o;
			}
		}
		return null;
	}

	public DiagramConnector getConnectorNamed(String name) {
		for (DiagramElement<?> o : childs) {
			if (o instanceof DiagramConnector && o.getName() != null && o.getName().equals(name)) {
				return (DiagramConnector) o;
			}
		}
		return null;
	}

	@Override
	public boolean delete() {
		if (getGraphicalRepresentation() != null && getGraphicalRepresentation().getPropertyChangeSupport() != null) {
			getGraphicalRepresentation().getPropertyChangeSupport().removePropertyChangeListener(this);
		}
		// Vincent: I remove this lines because if the delete is called from a deletion scheme of the edition pattern instance,
		// then the edition pattern instance id still not marked to deleted at this point.
		// The the next lines cause an infinite loop!!
		// It might be a possibility to have a "deleting" status.
		/*if (getEditionPatternInstance() != null && !getEditionPatternInstance().isDeleted()) {
			getEditionPatternInstance().delete();
		}*/
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
		return super.delete();
	}

	@Override
	public GR getGraphicalRepresentation() {
		return graphicalRepresentation;
	}

	public void setGraphicalRepresentation(GR graphicalRepresentation) {
		// logger.info("************************* setGraphicalRepresentation() dans " + this + " of " + getClass());
		if (this.graphicalRepresentation != null) {
			this.graphicalRepresentation.getPropertyChangeSupport().removePropertyChangeListener(this);
		}
		this.graphicalRepresentation = graphicalRepresentation;
		setChanged();
		if (this.graphicalRepresentation != null) {
			this.graphicalRepresentation.getPropertyChangeSupport().addPropertyChangeListener(this);
		}
		update();
	}

	@Override
	public DiagramElement<?> getParent() {
		return parent;
	}

	protected void setParent(DiagramElement<?> parent) {
		ancestors = null;
		this.parent = parent;
	}

	public Vector<DiagramElement<?>> getAncestors() {
		if (ancestors == null) {
			ancestors = new Vector<DiagramElement<?>>();
			DiagramElement<?> current = getParent();
			while (current != null) {
				ancestors.add(current);
				current = current.getParent();
			}
		}
		return ancestors;
	}

	public static DiagramElement<?> getFirstCommonAncestor(DiagramElement<?> child1, DiagramElement<?> child2) {
		Vector<DiagramElement<?>> ancestors1 = child1.getAncestors();
		Vector<DiagramElement<?>> ancestors2 = child2.getAncestors();
		for (int i = 0; i < ancestors1.size(); i++) {
			DiagramElement<?> o1 = ancestors1.elementAt(i);
			if (ancestors2.contains(o1)) {
				return o1;
			}
		}
		return null;
	}

	public abstract boolean isContainedIn(DiagramElement<?> o);

	public final boolean contains(DiagramElement<?> o) {
		return o.isContainedIn(this);
	}

	@Override
	public abstract String getDisplayableDescription();

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

	/**
	 * Return a flag indicating if current graphical element is bound inside an edition pattern, and if this element plays a role as primary
	 * representation,
	 * 
	 * @return
	 */
	public boolean playsPrimaryRole() {
		return getPatternRole() != null && getPatternRole().getIsPrimaryRepresentationRole();
	}

	public EditionPattern getEditionPattern() {
		if (getEditionPatternInstance() != null) {
			return getEditionPatternInstance().getEditionPattern();
		}
		return null;
	}

	public GraphicalElementPatternRole<?> getPatternRole() {
		EditionPatternInstance epi = getEditionPatternInstance();
		if (epi != null && epi.getRoleForActor(this) instanceof GraphicalElementPatternRole) {
			return (GraphicalElementPatternRole) epi.getRoleForActor(this);
		}
		return null;
	}

	/**
	 * Return EditionPatternInstance for that object<br>
	 * 
	 * If many EditionPatternInstance are defined for this object, return preferabely an EditionPatternReference where this object plays a
	 * primary role
	 * 
	 * @return
	 */
	public EditionPatternInstance getEditionPatternInstance() {
		// Default behaviour is to have only one EditionPattern where
		// this graphical element plays a representation primitive role
		// When many, big pbs may happen !!!!

		if (getEditionPatternReferences() != null) {
			if (getEditionPatternReferences().size() > 0) {
				EditionPatternInstance returned = null;
				for (FlexoModelObjectReference<EditionPatternInstance> ref : getEditionPatternReferences()) {
					EditionPatternInstance epi = ref.getObject();
					if (epi != null) {
						PatternRole<?> pr = epi.getRoleForActor(this);
						if (pr instanceof GraphicalElementPatternRole) {
							GraphicalElementPatternRole grPatternRole = (GraphicalElementPatternRole) pr;
							if (grPatternRole.getIsPrimaryRepresentationRole()) {
								if (returned != null) {
									if (logger.isLoggable(Level.WARNING)) {
										logger.warning("More than one edition pattern reference where element plays a primary role 1 !!!!");
									}
								}
								returned = epi;
							} else if (grPatternRole.isIncludedInPrimaryRepresentationRole()) {
								if (returned != null) {
									if (logger.isLoggable(Level.WARNING)) {
										logger.warning("More than one edition pattern reference where element plays a primary role 2 !!!!");
									}
								}
								returned = epi;
							}
						}
					} else {
						logger.warning("Cannot find EditionPatternInstance for " + ref);
					}
				}
				if (returned != null) {
					return returned;
				}
			}
		}

		if (getEditionPatternReferences() != null && getEditionPatternReferences().size() > 0) {
			return getEditionPatternReferences().get(0).getObject();
		}

		return null;
	}

	@Override
	public String getInspectorTitle() {
		if (getEditionPatternInstance() != null) {
			return FlexoLocalization.localizedForKey(getEditionPatternInstance().getEditionPattern().getName());
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

	protected synchronized void appendToDependingObjects(DataBinding<?> binding, List<TargetObject> returned) {
		if (binding.isSet()) {
			List<TargetObject> list = binding.getTargetObjects(getEditionPatternInstance());
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
		// System.out.println("**************> DiagramElement " + this + " : receive notification " + arg + " observable=" + o);
		update();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// Note: this object observes two kind of objects
		// - objects that are relevant in the context of the computing of their representation, such as label
		// - they also observe their GR
		// If the case of their GR observing, update() should not be invoked, only invoke setChanged() in order
		// to resource to be flagged as modified
		// logger.info("**************> DiagramElement " + this + " : receive PropertyChangeEvent " + evt.getPropertyName() + " source="
		// + evt.getSource().getClass().getSimpleName() + " evt=" + evt);
		if (evt.getSource() == getGraphicalRepresentation()) {
			// We just want here to track events such as object moving, just to flag the resource as modified
			// Ignore focused or selected events, because goal here is to mark resource as modified
			if (!evt.getPropertyName().equals(DrawingTreeNode.IS_FOCUSED.getName())
					&& !evt.getPropertyName().equals(DrawingTreeNode.IS_SELECTED.getName())) {
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
		// System.out.println("Update in DiagramElement " + this + ", text=" + getLabelValue());
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

	/**
	 * Return the index of this DiagramElement, relative to its position in the list of DiagramElement<?> declared to be of same
	 * EditionPattern
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
	 * Sets the index of this DiagramElement, relative to its position in the list of DiagramElement<?> declared to be of same
	 * EditionPattern
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

	@Override
	public void notifiedBindingChanged(DataBinding<?> dataBinding) {
	}

	@Override
	public void notifiedBindingDecoded(DataBinding<?> dataBinding) {
	}

	private List<DiagramElement<?>> descendants;

	private void appendDescendants(DiagramElement<?> current, List<DiagramElement<?>> descendants) {
		descendants.add(current);
		for (DiagramElement<?> child : current.getChilds()) {
			if (child != current) {
				appendDescendants(child, descendants);
			}
		}
	}

	@Override
	public List<DiagramElement<?>> getDescendants() {
		if (descendants == null) {
			descendants = new ArrayList<DiagramElement<?>>();
			appendDescendants(this, descendants);
		}
		return descendants;
	}

}
