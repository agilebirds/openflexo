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

import org.openflexo.foundation.viewpoint.rm.VirtualModelResource;
import org.openflexo.model.ModelContextLibrary;
import org.openflexo.model.converter.RelativePathFileConverter;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;

/**
 * {@link ModelFactory} used to handle VirtualModel models<br>
 * One instance is declared for a {@link VirtualModelResource}
 * 
 * @author sylvain
 * 
 */
public class VirtualModelModelFactory extends ModelFactory {

	public VirtualModelModelFactory(VirtualModelResource virtualModelResource) throws ModelDefinitionException {
		super(ModelContextLibrary.getModelContext(VirtualModel.class));
		addConverter(new RelativePathFileConverter(virtualModelResource.getDirectory()));
	}

	public VirtualModel newVirtualModel() {
		return newInstance(VirtualModel.class);
	}

	public SynchronizationScheme newSynchronizationScheme() {
		return newInstance(SynchronizationScheme.class);
	}

	public EditionPatternConstraint newEditionPatternConstraint() {
		return newInstance(EditionPatternConstraint.class);
	}

	public CreationScheme newCreationScheme() {
		return newInstance(CreationScheme.class);
	}

	public CloningScheme newCloningScheme() {
		return newInstance(CloningScheme.class);
	}

	public ActionScheme newActionScheme() {
		return newInstance(ActionScheme.class);
	}

	public NavigationScheme newNavigationScheme() {
		return newInstance(NavigationScheme.class);
	}

	public DeletionScheme newDeletionScheme() {
		return newInstance(DeletionScheme.class);
	}

	public DeleteAction newDeleteAction() {
		return newInstance(DeleteAction.class);
	}
}
