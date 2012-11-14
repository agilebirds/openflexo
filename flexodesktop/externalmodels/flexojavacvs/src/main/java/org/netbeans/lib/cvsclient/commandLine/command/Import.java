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

import org.netbeans.lib.cvsclient.command.Command;
import org.netbeans.lib.cvsclient.command.GlobalOptions;
import org.netbeans.lib.cvsclient.command.importcmd.ImportCommand;
import org.netbeans.lib.cvsclient.commandLine.GetOpt;

/**
 * Imports a file or directory to the repository.
 * 
 * @author Martin Entlicher
 */
public class Import extends AbstractCommandProvider {

	@Override
	public String getName() {
		return "import"; // NOI18N
	}

	@Override
	public String[] getSynonyms() {
		return new String[] { "im", "imp" }; // NOI18N
	}

	@Override
	public Command createCommand(String[] args, int index, GlobalOptions gopt, String workDir) {
		ImportCommand command = new ImportCommand();
		command.setBuilder(null);
		final String getOptString = command.getOptString();
		GetOpt go = new GetOpt(args, getOptString);
		int ch = -1;
		go.optIndexSet(index);
		boolean usagePrint = false;
		String arg;
		while ((ch = go.getopt()) != GetOpt.optEOF) {
			boolean ok = command.setCVSCommand((char) ch, go.optArgGet());
			if (!ok) {
				usagePrint = true;
			}
		}
		if (usagePrint) {
			throwUsage();
		}
		int argIndex = go.optIndexGet();
		// test if we have been passed the repository, vendor-tag and release-tag argument
		if (argIndex < args.length - 2) {
			command.setModule(args[argIndex]);
			command.setVendorTag(args[++argIndex]);
			command.setReleaseTag(args[++argIndex]);
		} else {
			throwUsage();
		}
		return command;

	}

	private void throwUsage() {
		throw new IllegalArgumentException(getUsage());
	}

}
