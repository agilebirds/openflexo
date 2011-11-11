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
package org.openflexo.foundation.sg.implmodel.event;

import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.ImplementationModelResource;
import org.openflexo.foundation.rm.RMNotification;
import org.openflexo.foundation.sg.implmodel.ImplementationModelDefinition;

/**
 * Notify that a ImplementationModel has been renamed
 * 
 * @author sguerin
 * 
 */
public class ImplModelNameChanged extends SGDataModification implements RMNotification {

	public ImplementationModelDefinition implModel;

	public ImplModelNameChanged(ImplementationModelDefinition implModel, String oldName, String newName) {
		super(oldName, newName);
		this.implModel = implModel;
	}

	public ImplModelNameChanged(String propertyName, ImplementationModelDefinition implModel, String oldName, String newName) {
		super("name", oldName, newName);
		this.implModel = implModel;
	}

	@Override
	public boolean forceUpdateWhenUnload() {
		return true;
	}

	@Override
	public boolean isDeepNotification() {
		return true;
	}

	@Override
	public boolean propagateToSynchronizedResource(FlexoResource originResource, FlexoResource targetResource) {
		// return true;
		if (originResource == implModel.getGeneratedSources().getFlexoResource() && (targetResource instanceof ImplementationModelResource)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean propagateToAlteredResource(FlexoResource originResource, FlexoResource targetResource) {
		if (originResource == implModel.getImplementationModelResource()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return "ImplModelNameChanged " + oldValue() + "/" + newValue();
	}
}
