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

package org.netbeans.lib.cvsclient.command.checkout;

import java.util.StringTokenizer;

import org.netbeans.lib.cvsclient.command.Builder;
import org.netbeans.lib.cvsclient.event.EventManager;
import org.netbeans.lib.cvsclient.event.FileInfoEvent;

/**
 * Handles the building of module list information object and the firing of events when complete objects are built.
 * 
 * @author Milos Kleint
 */
public class ModuleListBuilder implements Builder {
	/**
	 * The module object that is currently being built.
	 */
	private ModuleListInformation moduleInformation;

	/**
	 * The event manager to use.
	 */
	private final EventManager eventManager;

	private final CheckoutCommand checkoutCommand;

	public ModuleListBuilder(EventManager eventMan, CheckoutCommand comm) {
		eventManager = eventMan;
		checkoutCommand = comm;
	}

	@Override
	public void outputDone() {
		if (moduleInformation != null) {
			eventManager.fireCVSEvent(new FileInfoEvent(this, moduleInformation));
			moduleInformation = null;
		}
	}

	@Override
	public void parseLine(String line, boolean isErrorMessage) {
		line = line.replace('\t', ' ');
		if (!line.startsWith(" ")) { // NOI18N
			processModule(line, true);
		} else {
			processModule(line, false);
		}
	}

	protected void processModule(String line, boolean firstLine) {
		StringTokenizer tok = new StringTokenizer(line, " ", false); // NOI18N
		if (firstLine) {
			outputDone();
			moduleInformation = new ModuleListInformation();
			String modName = tok.nextToken();
			moduleInformation.setModuleName(modName);
			if (checkoutCommand.isShowModulesWithStatus()) {
				String stat = tok.nextToken();
				moduleInformation.setModuleStatus(stat);
			}
		}
		while (tok.hasMoreTokens()) {
			String nextTok = tok.nextToken();
			if (nextTok.startsWith("-")) { // NOI18N
				moduleInformation.setType(nextTok);
				continue;
			}
			moduleInformation.addPath(nextTok);
		}
	}

	@Override
	public void parseEnhancedMessage(String key, Object value) {
	}
}
