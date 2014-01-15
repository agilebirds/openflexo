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

public interface EditionSchemeParameters extends EditionSchemeObject, FlexoFacet<EditionScheme> {

	@Override
	public EditionScheme getEditionScheme();

	public void setEditionScheme(EditionScheme editionScheme);

	public abstract class EditionSchemeParametersImpl extends EditionSchemeObjectImpl implements EditionSchemeParameters {

		private EditionScheme editionScheme;

		@Override
		public EditionScheme getEditionScheme() {
			return editionScheme;
		}

		@Override
		public void setEditionScheme(EditionScheme editionScheme) {
			this.editionScheme = editionScheme;
		}

		@Override
		public BindingModel getBindingModel() {
			return getEditionScheme().getBindingModel();
		}

		/*@Override
		public Collection<? extends Validable> getEmbeddedValidableObjects() {
			return getEditionScheme().getParameters();
		}*/

		@Override
		public EditionScheme getObject() {
			return getEditionScheme();
		}

		@Override
		public String getURI() {
			return getEditionScheme().getURI();
		}

		@Override
		public VirtualModel getVirtualModel() {
			return getEditionPattern().getVirtualModel();
		}

		@Override
		public EditionPattern getEditionPattern() {
			return getEditionScheme().getEditionPattern();
		}
	}
}
