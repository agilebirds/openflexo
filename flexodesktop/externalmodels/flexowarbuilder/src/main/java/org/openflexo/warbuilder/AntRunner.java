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
package org.openflexo.warbuilder;

import java.io.File;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;

import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.gen.GenerationProgressNotification;
import org.openflexo.logging.FlexoLogger;

/**
 * <PRE>
 * 
 * This class is designed to call Ant targets from any Java application. 1.
 * Initialize a new Project by calling "init" 2. Feed Ant with some properties
 * by calling "setProperties" (optional) 3. Run an Ant target by calling
 * "runTarget"
 * 
 * 
 * Example :
 * 
 * try { //init init("/home/me/build.xml","/home/me/"); //properties HashMap m =
 * new HashMap(); m.put("event", "test"); m.put("subject", "sujet java 3");
 * m.put("message", "message java 3"); setProperties(m, false); //run
 * runTarget("test"); } catch (Exception e) { e.printStackTrace(); }
 * 
 * </PRE>
 * 
 * @(protected) croisier
 */

public class AntRunner implements BuildListener {

	private static final Logger logger = FlexoLogger.getLogger(AntRunner.class.getPackage().getName());

	public static void initAndRunTask(AntRunner runner, String buildXmlPath, String runDirPath, HashMap m, String target,
			Vector<BuildListener> listeners) throws Exception {
		if (m == null)
			m = new HashMap();
		runner.init(buildXmlPath, runDirPath);
		runner.setProperties(m, false);
		runner.runTarget(target, listeners);
	}

	public AntRunner(FlexoObservable observable) {
		super();
		_observableWhereForwardingAntBuildMessage = observable;
	}

	private FlexoObservable _observableWhereForwardingAntBuildMessage;

	private Project project;

	/**
	 * Initializes a new Ant Project.
	 * 
	 * @(protected) _buildFile The build File to use. If none is provided, it will be defaulted to "build.xml".
	 * @(protected) _baseDir The project's base directory. If none is provided, will be defaulted to "." (the current directory).
	 * @(protected) Exception Exceptions are self-explanatory (read their Message)
	 */
	public void init(String _buildFile, String _baseDir) throws Exception {
		// Create a new project, and perform some default initialization
		project = new Project();
		try {
			project.init();
		} catch (BuildException e) {
			throw new Exception("The default task list could not be loaded.");
		}

		// Set the base directory. If none is given, "." is used.
		if (_baseDir == null)
			_baseDir = new String(".");
		try {
			project.setBasedir(_baseDir);
		} catch (BuildException e) {
			throw new Exception("The given basedir doesn't exist, or isn't a directory.");
		}

		// Parse the given buildfile. If none is given, "build.xml" is used.
		if (_buildFile == null)
			_buildFile = new String("build.xml");
		try {
			ProjectHelper.getProjectHelper().parse(project, new File(_buildFile));
		} catch (BuildException e) {
			throw new Exception("Configuration file " + _buildFile + " is invalid, or cannot be read.");
		}
	}

	/**
	 * Sets the project's properties. May be called to set project-wide properties, or just before a target call to set target-related
	 * properties only.
	 * 
	 * @(protected) _properties A map containing the properties' name/value couples
	 * @(protected) _overridable If set, the provided properties values may be overriden by the config file's values
	 * @(protected) Exception Exceptions are self-explanatory (read their Message)
	 */
	public void setProperties(Map _properties, boolean _overridable) throws Exception {
		// Test if the project exists
		if (project == null)
			throw new Exception(
					"Properties cannot be set because the project has not been initialized. Please call the 'init' method first !");

		// Property hashmap is null
		if (_properties == null)
			throw new Exception("The provided property map is null.");

		// Loop through the property map
		Set propertyNames = _properties.keySet();
		Iterator iter = propertyNames.iterator();
		while (iter.hasNext()) {
			// Get the property's name and value
			String propertyName = (String) iter.next();
			String propertyValue = (String) _properties.get(propertyName);
			if (propertyValue == null)
				continue;

			// Set the properties
			if (_overridable)
				project.setProperty(propertyName, propertyValue);
			else
				project.setUserProperty(propertyName, propertyValue);
		}
	}

	/**
	 * Runs the given Target.
	 * 
	 * @param listeners
	 * 
	 * @(protected) _target The name of the target to run. If null, the project's default target will be used.
	 * @(protected) Exception Exceptions are self-explanatory (read their Message)
	 */
	public void runTarget(String _target, Vector<BuildListener> listeners) throws Exception {
		// Test if the project exists
		if (project == null)
			throw new Exception(
					"No target can be launched because the project has not been initialized. Please call the 'init' method first !");
		project.addBuildListener(this);
		if (listeners != null)
			for (BuildListener buildListener : listeners) {
				project.addBuildListener(buildListener);
			}
		// If no target is specified, run the default one.
		if (_target == null)
			_target = project.getDefaultTarget();
		// Run the target
		try {
			project.executeTarget(_target);
		} catch (BuildException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public void buildFinished(BuildEvent arg0) {
	}

	@Override
	public void buildStarted(BuildEvent arg0) {
		if (logger.isLoggable(Level.FINE))
			logger.fine("Ant build started: " + arg0.getMessage());
	}

	@Override
	public void messageLogged(BuildEvent arg0) {
		if (logger.isLoggable(Level.FINE))
			logger.fine("Ant log: " + arg0.getMessage());
	}

	@Override
	public void targetFinished(BuildEvent arg0) {

	}

	String lastMessage = null;

	@Override
	public void targetStarted(BuildEvent arg0) {
		if (logger.isLoggable(Level.FINE))
			logger.fine("Ant target started: " + arg0.getTarget().getName());
		if (_observableWhereForwardingAntBuildMessage != null) {
			String message = formatMessage(arg0.getTarget());
			if ((message == null && lastMessage != null) || (message != null && !message.equals(lastMessage))) {
				_observableWhereForwardingAntBuildMessage.notifyObservers(new GenerationProgressNotification(message, true));
				lastMessage = message;
			}
		}
	}

	@Override
	public void taskFinished(BuildEvent arg0) {
	}

	@Override
	public void taskStarted(BuildEvent arg0) {
		if (logger.isLoggable(Level.FINEST))
			logger.finest("Ant task started: " + arg0.getTask().getTaskName());
	}

	private String formatMessage(Target targ) {
		if (targ.getName().equals("checkout"))
			return formatCheckout(targ.getTasks()[0]);
		if (targ.getName().equals("java"))
			return formatJava(targ.getTasks()[0]);
		if (targ.getName().equals("dist"))
			return formatDist(targ.getProject().getProperties());
		return null;
	}

	private String formatCheckout(Task t) {
		String module = t.getProject().getProperty("param.module");
		if (module != null)
			return "checkout : " + module;
		Object pack = t.getRuntimeConfigurableWrapper().getAttributeMap().get("package");
		return "checkout : " + pack;
	}

	private String formatJava(Task t) {
		if (t.getProject().getProperty("framework.name") != null)
			return "compile : " + t.getProject().getProperty("framework.name");
		return "compile generated source files";
	}

	private String formatDist(Hashtable table) {
		String basedir = (String) table.get("basedir");
		if (basedir != null) {
			return "install : " + basedir.substring(basedir.lastIndexOf(File.separator) + 1);
		}
		return "install";
	}
}