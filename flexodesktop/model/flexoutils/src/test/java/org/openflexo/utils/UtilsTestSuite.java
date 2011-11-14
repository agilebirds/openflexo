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
package org.openflexo.utils;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openflexo.diff.TestDiff;
import org.openflexo.diff.merge.TestMerge;
import org.openflexo.diff.merge.TestMerge2;
import org.openflexo.letparser.ParseTest;

public class UtilsTestSuite {

	public static Test suite() {
		TestSuite suite = new TestSuite("UtilsTests");
		suite.addTestSuite(TestDiff.class);
		suite.addTestSuite(TestMerge.class);
		suite.addTestSuite(TestMerge2.class);
		suite.addTestSuite(ParseTest.class);
		suite.addTestSuite(ZipTest.class);
		suite.addTestSuite(FileUtilTest.class);
		suite.addTestSuite(HTMLUtilsTest.class);
		return suite;
	}
}
