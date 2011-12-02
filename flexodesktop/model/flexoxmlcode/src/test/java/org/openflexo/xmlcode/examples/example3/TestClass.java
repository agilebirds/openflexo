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

package org.openflexo.xmlcode.examples.example3;

import java.io.File;
import java.net.URL;
import java.util.Vector;

import org.openflexo.xmlcode.XMLSerializable;

/**
 * Class <code>TestClass</code> is intented to represent a command object in XML coding/decoding example.
 * 
 * @author <a href="mailto:Sylvain.Guerin@enst-bretagne.fr">Sylvain Guerin</a>
 */
public class TestClass implements XMLSerializable {

	public File testFile;

	public URL testURL;

	public Vector<String> testVector;

	public File[] fileArray;

	public Foo[] fooArray;

	@Override
	public String toString() {

		String returnedString = "TestClass\n";
		returnedString += "testFile= " + testFile + "\n";
		returnedString += "testURL= " + testURL + "\n";
		returnedString += "testVector= " + testVector + "\n";
		returnedString += "fileArray=";
		if (fileArray != null) {
			for (int i = 0; i < fileArray.length; i++) {
				returnedString += " " + fileArray[i];
			}
		} else {
			returnedString += "null\n";
		}
		returnedString += "\nfooArray=";
		if (fooArray != null) {
			for (int i = 0; i < fooArray.length; i++) {
				returnedString += " " + fooArray[i];
			}
		} else {
			returnedString += "null\n";
		}
		return returnedString;
	}

}
