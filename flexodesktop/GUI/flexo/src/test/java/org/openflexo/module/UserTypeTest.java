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

import junit.framework.Assert;

public class UserTypeTest extends AbstractGuiTest {

	public void testGetUserTypeNotNull() {
		UserType.setCurrentUserType(UserType.ANALYST);
		UserType currentUserType = UserType.getCurrentUserType();
		Assert.assertEquals("UserType must be analyst.", UserType.ANALYST, currentUserType);
	}

	public void testSetUserTypeNullThrowIllegalArgumentException() {
		try {
			UserType.setCurrentUserType(null);
			Assert.fail("Should have fail with an IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			// that's expected
		}
	}

	public void testSetSameUserTypeTwoTimesDontFail() {
		UserType.setCurrentUserType(UserType.ANALYST);
		UserType firstCurrentUserType = UserType.getCurrentUserType();
		Assert.assertEquals("UserType must be analyst.", UserType.ANALYST, firstCurrentUserType);
		UserType.setCurrentUserType(UserType.ANALYST);
		UserType secondCurrentUserType = UserType.getCurrentUserType();
		Assert.assertEquals("UserType must be analyst.", UserType.ANALYST, secondCurrentUserType);
	}

	public void testChangeUserTypeFailWithAnIllegalStateException() {
		UserType.setCurrentUserType(UserType.ANALYST);
		try {
			UserType.setCurrentUserType(UserType.DEVELOPER);
			Assert.fail("Should have fail with an IllegalStateException.");
		} catch (IllegalStateException e) {
			// that's expected.
		}
	}

	public void testIsCustomerRelease() {
		UserType.setCurrentUserType(UserType.CUSTOMER);
		assertUserTypeIs(UserType.CUSTOMER);
	}

	public void testIsAnalystRelease() {
		UserType.setCurrentUserType(UserType.ANALYST);
		assertUserTypeIs(UserType.ANALYST);
	}

	public void testIsDeveloperRelease() {
		UserType.setCurrentUserType(UserType.DEVELOPER);
		assertUserTypeIs(UserType.DEVELOPER);
	}

	public void testIsMaintainerRelease() {
		UserType.setCurrentUserType(UserType.MAINTAINER);
		assertUserTypeIs(UserType.MAINTAINER);
	}

	public void testGetUserTypeNamed() {
		String[] nameMatchingCustomerUserType = new String[] { "customer_release", "CustOmer_release", "CUSTOMER", "customer", "CustoMer" };
		assertAllNamesMatch(nameMatchingCustomerUserType, UserType.CUSTOMER);
		String[] nameMatchingAnalystUserType = new String[] { "analyst_release", "AnalYst_release", "ANALYST", "analyst", "AnaLyst" };
		assertAllNamesMatch(nameMatchingAnalystUserType, UserType.ANALYST);
		String[] nameMatchingDeveloperUserType = new String[] { "developer_release", "DevelOpEr_release", "DEVELOPER", "developer",
				"DeVelOper" };
		assertAllNamesMatch(nameMatchingDeveloperUserType, UserType.DEVELOPER);
		String[] nameMatchingMaintainerUserType = new String[] { "maintainer_release", "MaiNtaiNer_release", "MAINTAINER", "maintainer",
				"MainTaiNer" };
		assertAllNamesMatch(nameMatchingMaintainerUserType, UserType.MAINTAINER);
		String[] nameMatchingNoUserType = new String[] { "truc_release", "Dev", "COUCOU", "machin", "string" };
		assertAllNamesMatch(nameMatchingNoUserType, UserType.MAINTAINER);
		assertEquals("user type name null must return maintainer.", UserType.MAINTAINER, UserType.getUserTypeNamed(null));
	}

	public void testIsCurrentUserTypeDefined() {
		Assert.assertFalse("UserType must be null. Did you add something to put a default value ?", UserType.isCurrentUserTypeDefined());
		UserType.setCurrentUserType(UserType.CUSTOMER);
		Assert.assertTrue("UserType must be defined after a call to UserType.setCurrentUserType.", UserType.isCurrentUserTypeDefined());

	}

	private void assertAllNamesMatch(String[] names, UserType expected) {
		for (String name : names) {
			assertEquals(name + " must match the UserType " + expected.getName(), expected, UserType.getUserTypeNamed(name));
		}
	}

	private void assertUserTypeIs(UserType expected) {
		if (UserType.CUSTOMER == expected) {
			Assert.assertTrue("UserType.isCustomerRelease() must be true", UserType.isCustomerRelease());
		} else {
			Assert.assertFalse("UserType.isCustomerRelease() must be false", UserType.isCustomerRelease());
		}
		if (UserType.ANALYST == expected) {
			Assert.assertTrue("UserType.isAnalystRelease() must be true", UserType.isAnalystRelease());
		} else {
			Assert.assertFalse("UserType.isAnalystRelease() must be false", UserType.isAnalystRelease());
		}
		if (UserType.DEVELOPER == expected) {
			Assert.assertTrue("UserType.isDevelopperRelease() must be true", UserType.isDevelopperRelease());
		} else {
			Assert.assertFalse("UserType.isDevelopperRelease() must be false", UserType.isDevelopperRelease());
		}
		if (UserType.MAINTAINER == expected) {
			Assert.assertTrue("UserType.isMaintainerRelease() must be true", UserType.isMaintainerRelease());
		} else {
			Assert.assertFalse("UserType.isMaintainerRelease() must be false", UserType.isMaintainerRelease());
		}
	}

}
