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
package org.openflexo.foundation.cg.templates;

import org.openflexo.foundation.TemporaryFlexoModelObject;
import org.openflexo.foundation.cg.dm.CGStructureRefreshed;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.inspector.InspectableObject;

public abstract class CGTemplateObject extends TemporaryFlexoModelObject implements InspectableObject {

	@Override
	public abstract String getFullyQualifiedName();

	@Override
	public abstract String getClassNameKey();

	@Override
	public abstract FlexoProject getProject();

	public abstract CGTemplates getTemplates();

    public abstract void update();
    public abstract void update(boolean force);

	public void refresh() {
		update(true);
		setChanged();
		notifyObservers(new CGStructureRefreshed());
	}

}
