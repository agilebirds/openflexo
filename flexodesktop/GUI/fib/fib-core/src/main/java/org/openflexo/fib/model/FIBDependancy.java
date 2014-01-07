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

import java.util.logging.Logger;

import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(FIBDependancy.FIBDependancyImpl.class)
@XMLElement(xmlTag = "Dependancy")
public interface FIBDependancy extends FIBModelObject {

	@PropertyIdentifier(type = String.class)
	public static final String OWNER_KEY = "owner";

	@PropertyIdentifier(type = String.class)
	public static final String MASTER_COMPONENT_NAME_KEY = "masterComponentName";

	@Getter(value = OWNER_KEY, inverse = FIBComponent.EXPLICIT_DEPENDANCIES_KEY)
	public FIBComponent getOwner();

	@Setter(OWNER_KEY)
	public void setOwner(FIBComponent owner);

	@Getter(value = MASTER_COMPONENT_NAME_KEY)
	@XMLAttribute(xmlTag = "componentName")
	public String getMasterComponentName();

	@Setter(MASTER_COMPONENT_NAME_KEY)
	public void setMasterComponentName(String masterComponentName);

	public FIBComponent getMasterComponent();

	public void setMasterComponent(FIBComponent masterComponent);

	public static abstract class FIBDependancyImpl extends FIBModelObjectImpl implements FIBDependancy {

		private static final Logger logger = Logger.getLogger(FIBDependancy.class.getPackage().getName());

		// Owner depends of masterComponent

		private FIBComponent masterComponent;
		private String masterComponentName;

		@Override
		public FIBComponent getComponent() {
			return getOwner();
		}

		@Override
		public FIBComponent getMasterComponent() {
			return masterComponent;
		}

		@Override
		public void setMasterComponent(FIBComponent masterComponent) {
			FIBPropertyNotification<FIBComponent> notification = requireChange(MASTER_COMPONENT_NAME_KEY, masterComponent);
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

		public FIBDependancyImpl() {
			super();
		}

		public FIBDependancyImpl(FIBComponent masterComponent) {
			super();
			this.masterComponent = masterComponent;
		}

		@Override
		public String getMasterComponentName() {
			if (getMasterComponent() != null) {
				return getMasterComponent().getName();
			}
			return masterComponentName;
		}

		@Override
		public void setMasterComponentName(String masterComponentName) {
			this.masterComponentName = masterComponentName;
		}

		public void finalizeDeserialization() {
			setMasterComponent(getComponent().getRootComponent().getComponentNamed(masterComponentName));
		}

	}
}
