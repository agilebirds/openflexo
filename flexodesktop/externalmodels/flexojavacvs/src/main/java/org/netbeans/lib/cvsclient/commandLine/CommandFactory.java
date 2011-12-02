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
package org.netbeans.lib.cvsclient.commandLine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.netbeans.lib.cvsclient.command.Command;
import org.netbeans.lib.cvsclient.command.GlobalOptions;
import org.netbeans.lib.cvsclient.commandLine.command.CommandProvider;

/**
 * A factory for commands. Given a command name, and any arguments passed to that command on the command line, it will return a configured
 * Command object, ready for execution.
 * 
 * @author Robert Greig
 * @see org.netbeans.lib.cvsclient.command.Command
 */
public class CommandFactory {

	private static final String[] COMMAND_CLASSES = new String[] { "Import", "add", "annotate", "checkout", "commit", "diff", "export",
			"locbundlecheck", "log", "rannotate", "remove", "rlog", "rtag", "status", "tag", "update" };

	private static CommandFactory instance;

	private Map commandProvidersByNames;

	private CommandFactory() {
		createCommandProviders();
	}

	private void createCommandProviders() {
		commandProvidersByNames = new HashMap();
		String packageName = CommandFactory.class.getPackage().getName() + ".command.";
		for (int i = 0; i < COMMAND_CLASSES.length; i++) {
			Class providerClass;
			try {
				providerClass = Class.forName(packageName + COMMAND_CLASSES[i]);
				CommandProvider provider = (CommandProvider) providerClass.newInstance();
				commandProvidersByNames.put(provider.getName(), provider);
				String[] synonyms = provider.getSynonyms();
				for (int j = 0; j < synonyms.length; j++) {
					commandProvidersByNames.put(synonyms[j], provider);
				}
			} catch (Exception e) {
				System.err.println("Creation of command '" + COMMAND_CLASSES[i] + "' failed:");
				e.printStackTrace(System.err);
				continue;
			}
		}
	}

	/**
	 * Get the default instance of CommandFactory.
	 */
	public static synchronized CommandFactory getDefault() {
		if (instance == null) {
			instance = new CommandFactory();
		}
		return instance;
	}

	/**
	 * Create a CVS command.
	 * 
	 * @param commandName
	 *            The name of the command to create
	 * @param args
	 *            The array of arguments
	 * @param startingIndex
	 *            The index of the first argument of the command in the array
	 * @param workingDir
	 *            The working directory
	 */
	public Command createCommand(String commandName, String[] args, int startingIndex, GlobalOptions gopt, String workingDir)
			throws IllegalArgumentException {
		CommandProvider provider = (CommandProvider) commandProvidersByNames.get(commandName);
		if (provider == null) {
			throw new IllegalArgumentException("Unknown command: '" + commandName + "'");
		}
		return provider.createCommand(args, startingIndex, gopt, workingDir);
	}

	/**
	 * Get the provider of a command.
	 * 
	 * @param name
	 *            The name of the command to get the provider for.
	 */
	public CommandProvider getCommandProvider(String name) {
		return (CommandProvider) commandProvidersByNames.get(name);
	}

	/**
	 * Get the array of all command providers.
	 */
	public CommandProvider[] getCommandProviders() {
		Set providers = new HashSet(commandProvidersByNames.values());
		return (CommandProvider[]) providers.toArray(new CommandProvider[0]);
	}

	/*
	public static Command getCommand(String commandName, String[] args,
	                                 int startingIndex, String workingDir)
	        throws IllegalArgumentException {
	    Class helper;
	    try {
	        helper = Class.forName("org.netbeans.lib.cvsclient.commandLine." +
	                               "command." + commandName);
	    }
	    catch (Exception e) {
	        commandName = Character.toUpperCase(commandName.charAt(0)) + commandName.substring(1);
	        try {
	            helper = Class.forName("org.netbeans.lib.cvsclient.commandLine." +
	                                   "command." + commandName);
	        }
	        catch (Exception ex) {
	            System.err.println("Exception is: " + ex);
	            throw new IllegalArgumentException("Unknown command " +
	                                               commandName);
	        }
	    }

	    // the method invoked can throw an exception
	    try {
	        Method m = helper.getMethod("createCommand", new Class[]{
	            String[].class,
	            Integer.class,
	            String.class});
	        return (Command) m.invoke(null, new Object[] { args,
	                new Integer(startingIndex), workingDir });
	    }
	    catch (IllegalArgumentException e) {
	        throw e;
	    }
	    catch (InvocationTargetException ite) {
	        Throwable t = ite.getCause();
	        if (t instanceof IllegalArgumentException) {
	            throw (IllegalArgumentException) t;
	        } else {
	            IllegalArgumentException iaex = new IllegalArgumentException(t.getMessage());
	            iaex.initCause(t);
	            throw iaex;
	        }
	    }
	    catch (Exception e) {
	        IllegalArgumentException iaex = new IllegalArgumentException(e.getMessage());
	        iaex.initCause(e);
	        throw iaex;
	    }
	}
	 */
}