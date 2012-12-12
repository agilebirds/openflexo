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
package org.openflexo.foundation.viewpoint;

import java.util.logging.Logger;

import org.openflexo.foundation.NameChanged;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;

/**
 * Represents an object which is part of the model of a ViewPoint, and has a name, a description and can be identified by an URI
 * 
 * @author sylvain
 * 
 */
public abstract class NamedViewPointObject extends ViewPointObject {

	private static final Logger logger = Logger.getLogger(NamedViewPointObject.class.getPackage().getName());

	private String name;
	private String description;

	public NamedViewPointObject(ViewPointBuilder builder) {
		super(builder);
	}

	public abstract String getURI();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (requireChange(this.name, name)) {
			String oldName = this.name;
			this.name = name;
			setChanged();
			notifyObservers(new NameChanged(oldName, name));
		}
	}

	@Override
	public final String getDescription() {
		return description;
	}

	@Override
	public final void setDescription(String description) {
		if (requireChange(this.description, description)) {
			String oldDescription = this.description;
			this.description = description;
			setChanged();
			notifyObservers(new NameChanged(oldDescription, description));
		}
	}

}
