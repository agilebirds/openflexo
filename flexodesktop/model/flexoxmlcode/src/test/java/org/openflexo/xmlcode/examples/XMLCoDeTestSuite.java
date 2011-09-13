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

package org.openflexo.xmlcode.examples;

import org.openflexo.xmlcode.TestParameteredKVCoding;
import org.openflexo.xmlcode.examples.example1.Example1Test;
import org.openflexo.xmlcode.examples.example2.Example2Test;
import org.openflexo.xmlcode.examples.example3.Example3Test;
import org.openflexo.xmlcode.examples.example4.Example4Test;
import org.openflexo.xmlcode.examples.example5.Example5Test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class XMLCoDeTestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite("Tests for XMLCoDe library");
        
        //$JUnit-BEGIN$
        suite.addTestSuite(Example1Test.class);
        suite.addTestSuite(Example2Test.class);
        suite.addTestSuite(Example3Test.class);
        suite.addTestSuite(Example4Test.class);
        suite.addTestSuite(Example5Test.class);
        suite.addTestSuite(TestParameteredKVCoding.class);
         //$JUnit-END$
        return suite;
    }

}
