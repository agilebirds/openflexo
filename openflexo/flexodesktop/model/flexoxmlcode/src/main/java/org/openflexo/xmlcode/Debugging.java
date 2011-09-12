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

package org.openflexo.xmlcode;

/**
 * <p>
 * Utility class providing debugging facilities
 * </p>
 * 
 * @author <a href="mailto:Sylvain.Guerin@enst-bretagne.fr">Sylvain Guerin</a>
 */
public class Debugging
{

    /** Flag indicating if debug should be displayed or not */
    protected static boolean debugEnabled = false;

    /**
     * Enable debug
     */
    public static void enableDebug()
    {

        debugEnabled = true;
    }

    /**
     * Disable debug
     */
    public static void disableDebug()
    {

        debugEnabled = false;
    }

    /**
     * Prints to stdout specified string, if debug was enabled
     */
    public static void debug(String aDebugString)
    {

        if (debugEnabled) {
            System.out.println(aDebugString);
        }

    }

    /**
     * Prints to stderr specified string as a warning
     */
    public static void warn(String aWarningMessage)
    {

        System.err.println("!!! WARNING !!!");
        System.err.println("Message:\n" + aWarningMessage);
        System.err.println("StackTrace:\n");
        (new RuntimeException()).printStackTrace();

    }

}
