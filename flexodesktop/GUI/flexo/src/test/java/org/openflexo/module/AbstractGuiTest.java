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
package org.openflexo.module;

import java.lang.reflect.Field;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * set UserType to null before each test and restore to it's original value after each test.
 */
public abstract class AbstractGuiTest extends TestCase {

	private UserType userTypeBackUp = null;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		userTypeBackUp = setUserTypeFieldByReflection(null);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		setUserTypeFieldByReflection(userTypeBackUp);
	}

	private UserType setUserTypeFieldByReflection(UserType valueToSet) {
		Field currentUserTypeField = null;
		try {
			currentUserTypeField = UserType.class.getDeclaredField("currentUserType");
		} catch (NoSuchFieldException e) {
			Assert.fail("Class UserType must have a static field named 'currentUserType'.");
		}
		UserType reply = null;
		boolean isAccessible = currentUserTypeField.isAccessible();
		try {
			currentUserTypeField.setAccessible(true);
			reply = (UserType) currentUserTypeField.get(UserType.class);
			currentUserTypeField.set(UserType.class, valueToSet);
		} catch (IllegalAccessException e) {
			Assert.fail("UserType.currentUserType should be accessible.");
		} finally {
			currentUserTypeField.setAccessible(isAccessible);
		}
		return reply;

	}

}
