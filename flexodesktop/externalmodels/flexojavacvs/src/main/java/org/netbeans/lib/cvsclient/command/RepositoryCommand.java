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
package org.netbeans.lib.cvsclient.command;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.netbeans.lib.cvsclient.ClientServices;
import org.netbeans.lib.cvsclient.connection.AuthenticationException;
import org.netbeans.lib.cvsclient.event.EventManager;
import org.netbeans.lib.cvsclient.event.ModuleExpansionEvent;
import org.netbeans.lib.cvsclient.request.ArgumentRequest;
import org.netbeans.lib.cvsclient.request.DirectoryRequest;
import org.netbeans.lib.cvsclient.request.ExpandModulesRequest;
import org.netbeans.lib.cvsclient.request.Request;
import org.netbeans.lib.cvsclient.request.RootRequest;

/**
 * A class that provides common functionality for CVS commands that operate upon the repository.
 * 
 * @author Martin Entlicher
 */
public abstract class RepositoryCommand extends BuildableCommand {
	/**
	 * The requests that are sent and processed.
	 */
	protected List requests = new LinkedList();

	/**
	 * The client services that are provided to this command.
	 */
	protected ClientServices clientServices;

	/**
	 * Whether to process recursively.
	 */
	private boolean recursive = true;

	/**
	 * The modules to process. These names are unexpanded and will be passed to a module-expansion request.
	 */
	protected final List modules = new LinkedList();

	/**
	 * The expanded modules.
	 */
	protected final List expandedModules = new LinkedList();

	/**
	 * Gets the value of the recursive option.
	 * 
	 * @return true if recursive, false if not
	 */
	public boolean isRecursive() {
		return recursive;
	}

	/**
	 * Sets the value of the recursive option.
	 * 
	 * @param r
	 *            true if the command should recurse, false otherwise
	 */
	public void setRecursive(boolean recursive) {
		this.recursive = recursive;
	}

	/**
	 * Add a module to process.
	 * 
	 * @param module
	 *            the name of the module to process
	 */
	public void addModule(String module) {
		modules.add(module);
	}

	/**
	 * Set the modules to process.
	 * 
	 * @param modules
	 *            the names of the modules to process
	 */
	public void setModules(String[] modules) {
		clearModules();
		if (modules == null) {
			return;
		}
		for (int i = 0; i < modules.length; i++) {
			String module = modules[i];
			this.modules.add(module);
		}
	}

	/**
	 * Get the array of modules that are set to be processed.
	 */
	public String[] getModules() {
		String[] mods = new String[modules.size()];
		mods = (String[]) modules.toArray(mods);
		return mods;
	}

	/**
	 * Clear the list of modules.
	 */
	public void clearModules() {
		this.modules.clear();
	}

	/**
	 * Add the argument requests. The argument requests are created using the expanded set of modules passed in. Subclasses of this class
	 * should call this method at the appropriate point in their postExpansionExecute() method. Note that arguments are appended to the
	 * list.
	 */
	protected final void addArgumentRequests() {
		if (expandedModules.size() == 0) {
			return;
		}

		for (Iterator it = expandedModules.iterator(); it.hasNext();) {
			final String module = (String) it.next();
			addRequest(new ArgumentRequest(module));
		}
	}

	/**
	 * This is called when the server has responded to an expand-modules request.
	 */
	@Override
	public final void moduleExpanded(ModuleExpansionEvent e) {
		expandedModules.add(e.getModule());
	}

	/**
	 * Execute this command. This method sends the ExpandModulesRequest in order to expand the modules that were set. The actual execution
	 * is performed by {@link #postExpansionExecute} method.
	 * 
	 * @param client
	 *            the client services object that provides any necessary services to this command, including the ability to actually process
	 *            all the requests
	 */
	@Override
	public final void execute(ClientServices client, EventManager em) throws CommandException, AuthenticationException {

		client.ensureConnection();

		requests.clear();
		super.execute(client, em);

		clientServices = client;

		if (client.isFirstCommand()) {
			requests.add(new RootRequest(client.getRepository()));
		}
		for (Iterator it = modules.iterator(); it.hasNext();) {
			String module = (String) it.next();
			requests.add(new ArgumentRequest(module));
		}
		expandedModules.clear();
		requests.add(new DirectoryRequest(".", client.getRepository())); // NOI18N
		requests.add(new ExpandModulesRequest());
		try {
			client.processRequests(requests);
		} catch (CommandException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new CommandException(ex, ex.getLocalizedMessage());
		}
		requests.clear();
		postExpansionExecute(client, em);
	}

	/**
	 * Execute this command
	 * 
	 * @param client
	 *            the client services object that provides any necessary services to this command, including the ability to actually process
	 *            all the requests
	 */
	protected abstract void postExpansionExecute(ClientServices client, EventManager em) throws CommandException, AuthenticationException;

	/**
	 * Adds the specified request to the request list.
	 */
	protected final void addRequest(Request request) {
		requests.add(request);
	}

	/**
	 * Adds the request for the current working directory.
	 */
	protected final void addRequestForWorkingDirectory(ClientServices clientServices) throws IOException {
		addRequest(new DirectoryRequest(".", // NOI18N
				clientServices.getRepositoryForDirectory(getLocalDirectory())));
	}

	/**
	 * If the specified value is true, add a ArgumentRequest for the specified argument.
	 */
	protected final void addArgumentRequest(boolean value, String argument) {
		if (!value) {
			return;
		}

		addRequest(new ArgumentRequest(argument));
	}

	/**
	 * Appends the file's names to the specified buffer.
	 */
	protected final void appendModuleArguments(StringBuffer buffer) {
		if (expandedModules.size() == 0) {
			return;
		}

		Iterator it = expandedModules.iterator();
		buffer.append((String) it.next());
		while (it.hasNext()) {
			buffer.append(' ');
			buffer.append((String) it.next());
		}
	}
}
