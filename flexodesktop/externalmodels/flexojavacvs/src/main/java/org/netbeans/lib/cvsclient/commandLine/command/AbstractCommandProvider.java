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

package org.netbeans.lib.cvsclient.commandLine.command;

import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * The provider of CVS commands. The implementation of this interface knows how to create a CVS command from an array of arguments.
 * 
 * @author Martin Entlicher
 */
abstract class AbstractCommandProvider implements CommandProvider {

	/**
	 * Get the name of this command. The default implementation returns the name of the implementing class.
	 */
	@Override
	public String getName() {
		String className = getClass().getName();
		int dot = className.lastIndexOf('.');
		if (dot > 0) {
			return className.substring(dot + 1);
		} else {
			return className;
		}
	}

	@Override
	public String getUsage() {
		return ResourceBundle.getBundle(CommandProvider.class.getPackage().getName() + ".Bundle").getString(getName() + ".usage"); // NOI18N
	}

	@Override
	public void printShortDescription(PrintStream out) {
		String msg = ResourceBundle.getBundle(CommandProvider.class.getPackage().getName() + ".Bundle").getString(
				getName() + ".shortDescription"); // NOI18N
		out.print(msg);
	}

	@Override
	public void printLongDescription(PrintStream out) {
		String msg = ResourceBundle.getBundle(CommandProvider.class.getPackage().getName() + ".Bundle").getString(
				getName() + ".longDescription"); // NOI18N
		out.println(MessageFormat.format(msg, new Object[] { getUsage() }));
	}

}
