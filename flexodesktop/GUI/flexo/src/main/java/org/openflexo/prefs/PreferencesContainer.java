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
package org.openflexo.prefs;

import java.util.List;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.FlexoProperty;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.prefs.PreferencesService.FlexoPreferencesFactory;

/**
 * This class represents a logical container of some preferences regarding a particular functional aspect (eg a module)
 * 
 * @author sguerin
 */
@ModelEntity(isAbstract = true)
@ImplementationClass(PreferencesContainer.PreferencesContainerImpl.class)
public interface PreferencesContainer extends FlexoObject {

	@PropertyIdentifier(type = PreferencesContainer.class, cardinality = Cardinality.LIST)
	public static final String CONTAINERS = "containers";

	@Getter(value = CONTAINERS, cardinality = Cardinality.LIST, inverse = FlexoProperty.OWNER_KEY)
	public List<PreferencesContainer> getContainers();

	@Setter(CONTAINERS)
	public void setContainers(List<PreferencesContainer> someContainers);

	@Adder(CONTAINERS)
	public void addToContainers(PreferencesContainer aContainer);

	@Remover(CONTAINERS)
	public void removeFromContainers(PreferencesContainer aContainer);

	public <P extends PreferencesContainer> P getPreferences(Class<P> containerType);

	public static abstract class PreferencesContainerImpl extends FlexoObjectImpl implements PreferencesContainer {

		protected FlexoProperty assertProperty(String propertyName) {
			FlexoProperty p = getPropertyNamed(propertyName);
			if (p == null) {
				// TODO create new
				p = getFlexoPreferencesFactory().newInstance(FlexoProperty.class);
				p.setName(propertyName);
				addToCustomProperties(p);
				return p;
			}
			return p;
		}

		private FlexoPreferencesFactory getFlexoPreferencesFactory() {
			// TODO: retrieve it from PreferencesService
			return null;
		}

		@Override
		public <P extends PreferencesContainer> P getPreferences(Class<P> containerType) {
			for (PreferencesContainer c : getContainers()) {
				if (containerType.isAssignableFrom(c.getClass())) {
					return (P) c;
				}
				P returned = c.getPreferences(containerType);
				if (returned != null) {
					return returned;
				}
			}
			return null;
		}
	}
}
