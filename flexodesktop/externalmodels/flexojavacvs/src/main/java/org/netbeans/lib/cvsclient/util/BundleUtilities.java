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
package org.netbeans.lib.cvsclient.util;

import java.util.ResourceBundle;

/**
 * @author Thomas Singer
 * @version Sep 26, 2001
 */
public class BundleUtilities {

	/**
	 * Returns the package name of the specified class. An empty String is returned, if the class is in the default package.
	 */
	public static String getPackageName(Class clazz) {
		String fullClassName = clazz.getName();
		int lastDotIndex = fullClassName.lastIndexOf('.');
		if (lastDotIndex < 0) {
			return ""; // NOI18N
		}
		return fullClassName.substring(0, lastDotIndex);
	}

	/**
	 * Returns the resourcename for the resource' shortName relative to the classInSamePackage.
	 */
	public static String getResourceName(Class classInSamePackage, String shortName) {
		String packageName = getPackageName(classInSamePackage);
		String resourceName = packageName.replace('.', '/') + '/' + shortName;
		return resourceName;
	}

	/**
	 * Returns the resource bundle for the specified resource' shortName relative to classInSamePackage.
	 */
	public static ResourceBundle getResourceBundle(Class classInSamePackage, String shortName) {
		String resourceName = getResourceName(classInSamePackage, shortName);
		return ResourceBundle.getBundle(resourceName);
	}
}
