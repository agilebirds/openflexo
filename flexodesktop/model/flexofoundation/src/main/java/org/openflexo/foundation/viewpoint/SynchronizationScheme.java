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

import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.view.VirtualModelInstance;
import org.openflexo.foundation.viewpoint.annotations.FIBPanel;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;

/**
 * A {@link SynchronizationScheme} is applied to a {@link VirtualModelInstance} to automatically manage contained
 * {@link EditionPatternInstance}
 * 
 * @author sylvain
 * 
 */
@FIBPanel("Fib/SynchronizationSchemePanel.fib")
@ModelEntity
@ImplementationClass(SynchronizationScheme.SynchronizationSchemeImpl.class)
@XMLElement
public interface SynchronizationScheme extends AbstractActionScheme {

	public VirtualModel getSynchronizedVirtualModel();

	public void setSynchronizedVirtualModel(VirtualModel virtualModel);

	public static abstract class SynchronizationSchemeImpl extends AbstractActionSchemeImpl implements SynchronizationScheme {

		public SynchronizationSchemeImpl() {
			super();
		}

		@Override
		public VirtualModel getSynchronizedVirtualModel() {
			return (VirtualModel) getEditionPattern();
		}

		@Override
		public void setSynchronizedVirtualModel(VirtualModel virtualModel) {
			setEditionPattern(virtualModel);
		}

	}
}
