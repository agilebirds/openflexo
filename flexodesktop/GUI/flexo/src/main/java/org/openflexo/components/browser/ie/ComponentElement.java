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
package org.openflexo.components.browser.ie;

import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InvalidNameException;
import javax.swing.tree.TreePath;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ExpansionSynchronizedElement;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.dm.DuplicateClassNameException;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.ie.dm.ComponentLoaded;
import org.openflexo.foundation.ie.widget.IEWidget;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.view.controller.FlexoController;

/**
 * Browser element representing a WOComponent
 * 
 * @author sguerin
 * 
 */
public abstract class ComponentElement extends BrowserElement implements ExpansionSynchronizedElement {

	private static final Logger logger = Logger.getLogger(ComponentElement.class.getPackage().getName());

	public ComponentElement(ComponentDefinition component, BrowserElementType elementType, ProjectBrowser browser, BrowserElement parent) {
		super(component, elementType, browser, parent);
		if (component.isLoaded()) {
			component.getWOComponent().addObserver(this);
			component.getWOComponent().getRootSequence().addObserver(this);
		}
	}

	@Override
	public TreePath getTreePath() {
		return super.getTreePath();
	}

	@Override
	public void delete() {
		if (getComponentDefinition() != null && getComponentDefinition().isLoaded()) {
			getComponentDefinition().getWOComponent().deleteObserver(this);
			getComponentDefinition().getWOComponent().getRootSequence().deleteObserver(this);
		}
		super.delete();
	}

	@Override
	protected void buildChildrenVector() {
		// We add the structure of WOComponent (if loaded)
		if (getComponentDefinition().isLoaded()) {
			FlexoModelObject child = null;
			for (Enumeration<IEWidget> e = getComponentDefinition().getWOComponent().getRootSequence().elements(); e.hasMoreElements();) {
				child = e.nextElement();
				addToChilds(child);
			}
		}
	}

	@Override
	public String getName() {
		return getComponentDefinition().getName();
	}

	public ComponentDefinition getComponentDefinition() {
		return (ComponentDefinition) getObject();
	}

	@Override
	protected BrowserElementType getFilteredElementType() {
		return BrowserElementType.COMPONENT;
	}

	@Override
	public boolean isExpansionSynchronizedWithData() {
		if (_browser.getSelectionManager() != null) {
			return (getComponentDefinition() == _browser.getSelectionManager().getRootFocusedObject());
		}
		return false;
	}

	@Override
	public boolean isExpanded() {
		return (getComponentDefinition().isLoaded());
	}

	@Override
	public void expand() {
		// Does nothing, let the user axpand or collabse
	}

	@Override
	public void collapse() {
		// Does nothing, let the user axpand or collabse
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (_browser != null) {
			if (dataModification instanceof ComponentLoaded) {
				if (logger.isLoggable(Level.FINE))
					logger.fine("Notify expansion because WO has been loaded");
				refreshWhenPossible();
				_browser.notifyExpansionChanged(this);
				getComponentDefinition().getWOComponent().addObserver(this);
				getComponentDefinition().getWOComponent().getRootSequence().addObserver(this);
			} else {
				super.update(observable, dataModification);
			}
		}
	}

	@Override
	public boolean isNameEditable() {
		return true;
	}

	@Override
	public void setName(String aName) {
		try {
			getComponentDefinition().setName(aName);
		} catch (DuplicateResourceException e) {
			// Abort
		} catch (DuplicateClassNameException e) {
			FlexoController.notify(e.getLocalizedMessage());
		} catch (InvalidNameException e) {
			FlexoController.notify("invalid_component_name");
		}
	}

	@Override
	public boolean requiresExpansionFor(BrowserElement next) {
		return true;
	}

	@Override
	public FlexoModelObject getSelectableObject() {
		if ((getComponentDefinition() != null) && (getComponentDefinition().isLoaded())) {
			return getComponentDefinition().getWOComponent();
		} else {
			return getObject();
		}
	}

}
