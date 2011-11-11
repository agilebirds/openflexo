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

import org.netbeans.lib.cvsclient.command.Command;
import org.netbeans.lib.cvsclient.command.GlobalOptions;

/**
 * The provider of CVS commands. The implementation of this interface knows how to create a CVS command from an array of arguments.
 * 
 * @author Martin Entlicher
 */
public interface CommandProvider {

	/**
	 * Get the name of this command. The default implementation returns the name of the implementing class.
	 */
	public String getName();

	/**
	 * Get the list of synonyms of names of this command.
	 */
	public abstract String[] getSynonyms();

	/**
	 * Create the CVS command from an array of arguments.
	 * 
	 * @param args
	 *            The array of arguments passed to the command.
	 * @param index
	 *            The index in the array where the command's arguments start.
	 * @param workDir
	 *            The working directory.
	 * @return The implementation of the {@link org.netbeans.lib.cvsclient.command.Command} class, which have set the passed arguments.
	 */
	public abstract Command createCommand(String[] args, int index, GlobalOptions gopt, String workDir);

	/**
	 * Get a short string describibg the usage of the command.
	 */
	public String getUsage();

	/**
	 * Print a short help description (one-line only) for this command to the provided print stream.
	 * 
	 * @param out
	 *            The print stream.
	 */
	public void printShortDescription(PrintStream out);

	/**
	 * Print a long help description (multi-line with all supported switches and their description) of this command to the provided print
	 * stream.
	 * 
	 * @param out
	 *            The print stream.
	 */
	public void printLongDescription(PrintStream out);

}
