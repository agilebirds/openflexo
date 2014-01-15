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

import org.openflexo.antar.binding.BindingModel;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;

@ModelEntity
@ImplementationClass(EditionPatternStructuralFacet.EditionPatternStructuralFacetImpl.class)
public interface EditionPatternStructuralFacet extends EditionPatternObject, FlexoFacet<EditionPattern> {

	@Override
	public EditionPattern getEditionPattern();

	public void setEditionPattern(EditionPattern editionPattern);

	public abstract class EditionPatternStructuralFacetImpl extends EditionPatternObjectImpl implements EditionPatternStructuralFacet {

		private EditionPattern editionPattern;

		@Override
		public EditionPattern getEditionPattern() {
			return editionPattern;
		}

		@Override
		public void setEditionPattern(EditionPattern editionPattern) {
			this.editionPattern = editionPattern;
		}

		@Override
		public BindingModel getBindingModel() {
			return getEditionPattern().getBindingModel();
		}

		/*@Override
		public Collection<? extends Validable> getEmbeddedValidableObjects() {
			return getEditionPattern().getPatternRoles();
		}*/

		@Override
		public EditionPattern getObject() {
			return getEditionPattern();
		}

		@Override
		public String getURI() {
			return getEditionPattern().getURI();
		}

		@Override
		public VirtualModel getVirtualModel() {
			return getEditionPattern().getVirtualModel();
		}
	}
}
