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
package org.openflexo.generator;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author gpolet
 *
 */
public class AllCGTests
{

    public static Test suite()
    {
        TestSuite suite = new TestSuite("Tests for Code Generator");
         //$JUnit-BEGIN$
        suite.addTestSuite(TestCG.class);
        suite.addTestSuite(TestCG2.class);
        suite.addTestSuite(TestRoundTrip.class);
        //suite.addTestSuite(TestWar.class);
         //$JUnit-END$
        return suite;
    }

}
