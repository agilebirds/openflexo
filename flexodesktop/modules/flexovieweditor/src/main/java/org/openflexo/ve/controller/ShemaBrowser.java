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
package org.openflexo.ve.controller;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.view.View;

class ShemaBrowser extends OEBrowser {
	private View representedShema = null;

	protected ShemaBrowser(OEController controller) {
		super(controller);
	}

	protected View getRepresentedShema() {
		return representedShema;
	}

	protected void setRepresentedShema(View representedShema) {
		this.representedShema = representedShema;
	}

	@Override
	public FlexoModelObject getDefaultRootObject() {
		return representedShema;
	}

}