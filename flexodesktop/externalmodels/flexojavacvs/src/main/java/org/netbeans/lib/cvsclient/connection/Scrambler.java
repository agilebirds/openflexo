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
package org.netbeans.lib.cvsclient.connection;

/**
 * Provides methods used to scramble text. A concrete implementation will use a particular encoding scheme to scramble the text.
 * 
 * @author Robert Greig
 */
public interface Scrambler {
	/**
	 * Scramble text, turning it into a String of scrambled data
	 * 
	 * @return a String containing the scrambled data
	 */
	String scramble(String text);
}