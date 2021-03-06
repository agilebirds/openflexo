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

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.foundation.viewpoint.inspector.InspectorBindingAttribute;

public class DeletionScheme extends AbstractActionScheme {

	public static enum DeletionSchemeBindingAttribute implements InspectorBindingAttribute {
		conditional
	}

	public DeletionScheme(ViewPointBuilder builder) {
		super(builder);
	}

	@Override
	public EditionSchemeType getEditionSchemeType() {
		return EditionSchemeType.DeletionScheme;
	}

	@Override
	public String getInspectorName() {
		return Inspectors.VPM.DELETION_SCHEME_INSPECTOR;
	}

}
