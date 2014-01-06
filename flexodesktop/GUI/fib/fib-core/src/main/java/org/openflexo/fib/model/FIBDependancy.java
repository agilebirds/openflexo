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

import java.util.List;
import java.util.logging.Logger;

public class FIBDependancy extends FIBModelObject {

	private static final Logger logger = Logger.getLogger(FIBDependancy.class.getPackage().getName());

	// Owner depends of masterComponent

	private FIBComponent owner;
	private FIBComponent masterComponent;
	private String masterComponentName;

	public static enum Parameters implements FIBModelAttribute {
		owner, masterComponent;
	}

	@Override
	public FIBComponent getComponent() {
		return owner;
	}

	public FIBComponent getOwner() {
		return owner;
	}

	public void setOwner(FIBComponent owner) {
		FIBAttributeNotification<FIBComponent> notification = requireChange(Parameters.owner, owner);
		if (notification != null) {
			this.owner = owner;
			hasChanged(notification);
		}
	}

	public FIBComponent getMasterComponent() {
		return masterComponent;
	}

	public void setMasterComponent(FIBComponent masterComponent) {
		FIBAttributeNotification<FIBComponent> notification = requireChange(Parameters.masterComponent, masterComponent);
		if (notification != null) {
			this.masterComponent = masterComponent;
			// try {
			getOwner().declareDependantOf(masterComponent);
			/*} catch (DependancyLoopException e) {
				logger.warning("DependancyLoopException raised while applying explicit dependancy for " + getOwner() + " and "
						+ getMasterComponent() + " message: " + e.getMessage());
			}*/
			hasChanged(notification);
		}
	}

	public FIBDependancy() {
		super();
	}

	public FIBDependancy(FIBComponent masterComponent) {
		super();
		this.masterComponent = masterComponent;
	}

	public String getMasterComponentName() {
		if (getMasterComponent() != null) {
			return getMasterComponent().getName();
		}
		return masterComponentName;
	}

	public void setMasterComponentName(String masterComponentName) {
		this.masterComponentName = masterComponentName;
	}

	@Override
	public void finalizeDeserialization() {
		super.finalizeDeserialization();
		setMasterComponent(getComponent().getRootComponent().getComponentNamed(masterComponentName));
	}

	@Override
	public List<? extends FIBModelObject> getEmbeddedObjects() {
		return null;
	}

}
