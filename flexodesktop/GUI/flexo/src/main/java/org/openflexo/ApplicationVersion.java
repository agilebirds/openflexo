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

package org.openflexo;

public class ApplicationVersion {
	public static String BUSINESS_APPLICATION_VERSION = "1.5.2";
	// Must be like x.x.x or x.x or x.xalpha or x.xbeta or x.x.xRCxx or x.x.xalpha x.x.xbeta
	// This field must be non-final because it will be generated during the build procedure.
	// If you declare this field final, then the compiler will copy it's value directly and you will not see the value of the build
	public static String BUILD_ID = "dev";

	public static String COMMIT_ID = "dev";
}
