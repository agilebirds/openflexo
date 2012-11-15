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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.naming.InvalidNameException;

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.foundation.NameChanged;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.toolbox.HasPropertyChangeSupport;
import org.openflexo.xmlcode.XMLMapping;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

public abstract class ViewObject extends AbstractViewObject implements PropertyChangeListener {

	private static final Logger logger = Logger.getLogger(ViewObject.class.getPackage().getName());

	private View _shema;
	private String _name;
	private ViewObject parent = null;
	private Vector<ViewElement> childs;

	// We dont want to import graphical engine in foundation
	// But you can assert graphical representation is a org.openflexo.fge.GraphicalRepresentation.
	// For a OEShema, graphicalRepresentation is a DrawingGraphicalRepresentation
	// For a OEShape, graphicalRepresentation is a ShapeGraphicalRepresentation
	// For a OEConnector, graphicalRepresentation is a ConnectorGraphicalRepresentation
	private GraphicalRepresentation<?> _graphicalRepresentation;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	/**
	 * Never use this constructor except for ComponentLibrary
	 */
	public ViewObject(FlexoProject project) {
		super(project);
		childs = new Vector<ViewElement>();
	}

	/**
	 * Default constructor
	 */
	public ViewObject(View shema) {
		this(shema.getProject());
		setShema(shema);
	}

	public View getShema() {
		return _shema;
	}

	public void setShema(View shema) {
		_shema = shema;
	}

	/**
	 * Returns reference to the main object in which this XML-serializable object is contained relating to storing scheme: here it's the
	 * component library
	 * 
	 * @return the component library
	 */
	@Override
	public XMLStorageResourceData getXMLResourceData() {
		return getShema();
	}

	@Override
	public XMLMapping getXMLMapping() {
		return getShema().getXMLMapping();
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public void setName(String name) throws DuplicateResourceException, InvalidNameException {
		if (requireChange(_name, name)) {
			String oldName = _name;
			_name = name;
			setChanged();
			notifyObservers(new NameChanged(oldName, name));
		}
	}

	public Vector<ViewElement> getChilds() {
		return childs;
	}

	public void setChilds(Vector<ViewElement> someChilds) {
		childs.addAll(someChilds);
	}

	public void addToChilds(ViewElement aChild) {
		// logger.info("****** addToChild() put "+aChild+" under "+this);
		childs.add(aChild);
		aChild.setParent(this);
		setChanged();
		if (aChild instanceof ViewShape) {
			notifyObservers(new ShapeInserted((ViewShape) aChild, this));
		}
		if (aChild instanceof ViewConnector) {
			notifyObservers(new ConnectorInserted((ViewConnector) aChild));
		}
	}

	public void removeFromChilds(ViewElement aChild) {
		childs.remove(aChild);
		setChanged();
		if (aChild instanceof ViewShape) {
			notifyObservers(new ShapeRemoved((ViewShape) aChild, this));
		}
		if (aChild instanceof ViewConnector) {
			notifyObservers(new ConnectorRemoved((ViewConnector) aChild));
		}
	}

	/**
	 * Re-index child, as it is defined in diagram hierarchy
	 * 
	 * @param aChild
	 * @param newIndex
	 */
	protected void setIndexForChild(ViewElement aChild, int newIndex) {
		if (childs.contains(aChild) && childs.indexOf(aChild) != newIndex) {
			childs.remove(aChild);
			childs.insertElementAt(aChild, newIndex);
			for (ViewElement o : childs) {
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
		if (getIndex() != index && !isDeserializing() && this instanceof ViewElement) {
			getParent().setIndexForChild((ViewElement) this, index);
		}
	}

	/**
	 * Re-index child, relative to its position in the list of ViewObject declared to be of same EditionPattern
	 * 
	 * @param aChild
	 * @param newIndex
	 */
	protected void setIndexForChildRelativeToEPType(ViewElement aChild, int newIndex) {
		List<ViewElement> childsOfRightType = getChildsOfType(aChild.getEditionPattern());
		if (childsOfRightType.contains(aChild) && childsOfRightType.indexOf(aChild) != newIndex) {
			if (newIndex > 0) {
				ViewElement previousElement = childsOfRightType.get(newIndex - 1);
				int previousElementIndex = childs.indexOf(previousElement);
				childs.remove(aChild);
				if (previousElementIndex + 1 <= childs.size()) {
					childs.insertElementAt(aChild, previousElementIndex + 1);
				} else {
					childs.insertElementAt(aChild, childs.size());
				}
			} else {
				ViewElement firstElement = childsOfRightType.get(0);
				int firstElementIndex = childs.indexOf(firstElement);
				childs.remove(aChild);
				childs.insertElementAt(aChild, firstElementIndex);
			}
			for (ViewElement o : childs) {
				o.notifyIndexChange();
			}
		}
	}

	public List<ViewElement> getChildsOfType(EditionPattern editionPattern) {
		ArrayList<ViewElement> returned = new ArrayList<ViewElement>();
		for (ViewObject o : getChilds()) {
			if (o instanceof ViewElement) {
				ViewElement e = (ViewElement) o;
				if (e.getEditionPattern() == editionPattern) {
					returned.add(e);
				}
			}
		}
		return returned;
	}

	public <T extends ViewObject> Collection<T> getChildrenOfType(final Class<T> type) {
		return getChildrenOfType(type, true);
	}

	@SuppressWarnings("unchecked")
	// We can remove the warning because the code performs the necessary checks
	public <T extends ViewObject> Collection<T> getChildrenOfType(final Class<T> type, boolean recursive) {
		Collection<T> objects = (Collection<T>) Collections2.filter(new ArrayList<ViewObject>(childs), new Predicate<ViewObject>() {
			@Override
			public boolean apply(ViewObject input) {
				return type.isAssignableFrom(input.getClass());
			}
		});
		if (recursive) {
			for (T object : new ArrayList<T>(objects)) {
				objects.addAll(object.getChildrenOfType(type, true));
			}
		}
		return objects;
	}

	public ViewShape getShapeNamed(String name) {
		for (ViewObject o : childs) {
			if (o instanceof ViewShape && o.getName() != null && o.getName().equals(name)) {
				return (ViewShape) o;
			}
		}
		return null;
	}

	public ViewConnector getConnectorNamed(String name) {
		for (ViewObject o : childs) {
			if (o instanceof ViewConnector && o.getName() != null && o.getName().equals(name)) {
				return (ViewConnector) o;
			}
		}
		return null;
	}

	@Override
	public void delete() {
		if (this._graphicalRepresentation instanceof HasPropertyChangeSupport) {
			((HasPropertyChangeSupport) this._graphicalRepresentation).getPropertyChangeSupport().removePropertyChangeListener(this);
		}
		super.delete();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// TODO: improve this.
		setChanged();
	}

	public GraphicalRepresentation<?> getGraphicalRepresentation() {
		return _graphicalRepresentation;
	}

	public void setGraphicalRepresentation(GraphicalRepresentation<?> graphicalRepresentation) {
		if (this._graphicalRepresentation instanceof HasPropertyChangeSupport) {
			((HasPropertyChangeSupport) this._graphicalRepresentation).getPropertyChangeSupport().removePropertyChangeListener(this);
		}
		_graphicalRepresentation = graphicalRepresentation;
		setChanged();
		if (this._graphicalRepresentation instanceof HasPropertyChangeSupport) {
			((HasPropertyChangeSupport) this._graphicalRepresentation).getPropertyChangeSupport().addPropertyChangeListener(this);
		}
	}

	private Vector<ViewObject> ancestors;

	public ViewObject getParent() {
		return parent;
	}

	protected void setParent(ViewObject parent) {
		this.parent = parent;
	}

	public Vector<ViewObject> getAncestors() {
		if (ancestors == null) {
			ancestors = new Vector<ViewObject>();
			ViewObject current = getParent();
			while (current != null) {
				ancestors.add(current);
				current = current.getParent();
			}
		}
		return ancestors;
	}

	public static ViewObject getFirstCommonAncestor(ViewObject child1, ViewObject child2) {
		Vector<ViewObject> ancestors1 = child1.getAncestors();
		Vector<ViewObject> ancestors2 = child2.getAncestors();
		for (int i = 0; i < ancestors1.size(); i++) {
			ViewObject o1 = ancestors1.elementAt(i);
			if (ancestors2.contains(o1)) {
				return o1;
			}
		}
		return null;
	}

	public abstract boolean isContainedIn(ViewObject o);

	public final boolean contains(ViewObject o) {
		return o.isContainedIn(this);
	}

	@Override
	public abstract String getDisplayableDescription();

}
