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

import java.io.File;

import org.netbeans.lib.cvsclient.command.Command;
import org.netbeans.lib.cvsclient.command.GlobalOptions;
import org.netbeans.lib.cvsclient.command.diff.DiffCommand;
import org.netbeans.lib.cvsclient.commandLine.GetOpt;

/**
 * The log command
 * 
 * @author Robert Greig
 */
public class diff extends AbstractCommandProvider {

	@Override
	public String[] getSynonyms() {
		return new String[] { "di", "dif" };
	}

	@Override
	public Command createCommand(String[] args, int index, GlobalOptions gopt, String workDir) {
		DiffCommand command = new DiffCommand();
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
			throw new IllegalArgumentException(getUsage());
		}
		int fileArgsIndex = go.optIndexGet();
		// test if we have been passed any file arguments
		if (fileArgsIndex < args.length) {
			File[] fileArgs = new File[args.length - fileArgsIndex];
			// send the arguments as absolute paths
			if (workDir == null) {
				workDir = System.getProperty("user.dir");
			}
			File workingDir = new File(workDir);
			for (int i = fileArgsIndex; i < args.length; i++) {
				fileArgs[i - fileArgsIndex] = new File(workingDir, args[i]);
			}
			command.setFiles(fileArgs);
		}
		return command;
	}

}