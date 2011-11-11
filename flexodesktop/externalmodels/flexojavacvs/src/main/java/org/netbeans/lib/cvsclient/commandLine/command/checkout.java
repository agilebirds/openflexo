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
import org.netbeans.lib.cvsclient.command.checkout.CheckoutCommand;
import org.netbeans.lib.cvsclient.commandLine.GetOpt;

/**
 * A factory class for creating and configuring a checkout command
 * 
 * @author Robert Greig
 */
public class checkout extends AbstractCommandProvider {

	@Override
	public String[] getSynonyms() {
		return new String[] { "co", "get" };
	}

	@Override
	public Command createCommand(String[] args, int index, GlobalOptions gopt, String workDir) {
		CheckoutCommand command = new CheckoutCommand();
		command.setBuilder(null);
		final String getOptString = command.getOptString();
		GetOpt go = new GetOpt(args, getOptString);
		int ch = -1;
		go.optIndexSet(index);
		boolean usagePrint = false;
		while ((ch = go.getopt()) != GetOpt.optEOF) {
			boolean ok = command.setCVSCommand((char) ch, go.optArgGet());
			if (!ok) {
				usagePrint = true;
			}
		}
		if (usagePrint) {
			throw new IllegalArgumentException(getUsage());
		}
		int modulesArgsIndex = go.optIndexGet();
		// test if we have been passed any file arguments
		if (modulesArgsIndex < args.length) {
			String[] modulesArgs = new String[args.length - modulesArgsIndex];
			// send the arguments as absolute paths
			for (int i = modulesArgsIndex; i < args.length; i++) {
				modulesArgs[i - modulesArgsIndex] = args[i];
			}
			command.setModules(modulesArgs);
		}
		return command;
	}

}