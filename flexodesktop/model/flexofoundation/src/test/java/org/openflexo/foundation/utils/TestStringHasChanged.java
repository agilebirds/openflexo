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
package org.openflexo.foundation.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openflexo.foundation.FlexoObject.FlexoObjectImpl;

public class TestStringHasChanged {

	@Test
	public void testStringHasChanged() {
		assertTrue(FlexoObjectImpl.stringHasChanged(null, "Something"));
		assertTrue(FlexoObjectImpl.stringHasChanged("Something", null));
		assertTrue(FlexoObjectImpl.stringHasChanged("Something", "something"));
		assertTrue(FlexoObjectImpl.stringHasChanged("Something", "SomethingElse"));
		assertFalse(FlexoObjectImpl.stringHasChanged("Something", "Something"));
		assertFalse(FlexoObjectImpl.stringHasChanged(null, null));
	}

}
