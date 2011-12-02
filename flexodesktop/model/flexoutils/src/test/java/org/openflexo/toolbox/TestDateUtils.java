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
package org.openflexo.toolbox;

import java.util.Date;

import junit.framework.TestCase;

public class TestDateUtils extends TestCase {

	public void testParseDate() {
		// Pattern d/M/y must be used
		Date[] dates = DateUtils.parseDate(new String[] { "1/1/2010", "2/2/2010", "3/2/10" });
		assertTrue(dates[0].toString().equals("Fri Jan 01 00:00:00 CET 2010"));
		assertTrue(dates[1].toString().equals("Tue Feb 02 00:00:00 CET 2010"));
		assertTrue(dates[2].toString().equals("Wed Feb 03 00:00:00 CET 2010"));

		// Pattern M/d/y must be used
		dates = DateUtils.parseDate(new String[] { "1/5/2010", "2/4/2010", "3/13/10" });
		assertTrue(dates[0].toString().equals("Tue Jan 05 00:00:00 CET 2010"));
		assertTrue(dates[1].toString().equals("Thu Feb 04 00:00:00 CET 2010"));
		assertTrue(dates[2].toString().equals("Sat Mar 13 00:00:00 CET 2010"));

		// Pattern d/M/y H'h'm must be used
		dates = DateUtils.parseDate(new String[] { "1/1/2010 12h24", "2/2/2010 21h56", "3/2/10 01h01" });
		assertTrue(dates[0].toString().equals("Fri Jan 01 12:24:00 CET 2010"));
		assertTrue(dates[1].toString().equals("Tue Feb 02 21:56:00 CET 2010"));
		assertTrue(dates[2].toString().equals("Wed Feb 03 01:01:00 CET 2010"));

		assertNull(DateUtils.parseDate(new String[] { "1/1/2010 12h24", "2/2/2010 21t56", "3/2/10 01h01" }));
	}
}
