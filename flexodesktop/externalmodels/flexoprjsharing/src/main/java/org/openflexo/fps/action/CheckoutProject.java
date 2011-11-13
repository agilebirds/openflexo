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
package org.openflexo.fps.action;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.netbeans.lib.cvsclient.command.CommandException;
import org.netbeans.lib.cvsclient.connection.AuthenticationException;
import org.netbeans.lib.cvsclient.event.CVSAdapter;
import org.netbeans.lib.cvsclient.event.FileAddedEvent;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.IOFlexoException;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.fps.CVSConstants;
import org.openflexo.fps.CVSExplorable;
import org.openflexo.fps.CVSExplorer;
import org.openflexo.fps.CVSExplorerListener;
import org.openflexo.fps.CVSFile;
import org.openflexo.fps.CVSModule;
import org.openflexo.fps.FPSObject;
import org.openflexo.fps.FlexoAuthentificationException;
import org.openflexo.fps.SharedProject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.FileUtils;

public class CheckoutProject extends CVSAction<CheckoutProject, CVSModule> {

	protected static final Logger logger = Logger.getLogger(CheckoutProject.class.getPackage().getName());

	public static FlexoActionType<CheckoutProject, CVSModule, FPSObject> actionType = new FlexoActionType<CheckoutProject, CVSModule, FPSObject>(
			"checkout_project", FlexoActionType.defaultGroup, FlexoActionType.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public CheckoutProject makeNewAction(CVSModule focusedObject, Vector<FPSObject> globalSelection, FlexoEditor editor) {
			return new CheckoutProject(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(CVSModule object, Vector<FPSObject> globalSelection) {
			return true;
		}

		@Override
		protected boolean isEnabledForSelection(CVSModule object, Vector<FPSObject> globalSelection) {
			return true;
		}

	};

	static {
		FlexoModelObject.addActionForClass(actionType, CVSModule.class);
	}

	@Override
	public String getLocalizedName() {
		if (getFocusedObject() != null && getFocusedObject().getModuleName().endsWith(".prj"))
			return FlexoLocalization.localizedForKey("checkout_project");
		else
			return FlexoLocalization.localizedForKey("checkout_directory");
	}

	private SharedProject _checkoutedProject;

	CheckoutProject(CVSModule focusedObject, Vector<FPSObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws IOFlexoException, FlexoAuthentificationException {
		CheckoutProgressController progressController = new CheckoutProgressController();

		logger.info("ProjectCheckout to " + getLocalDirectory());

		progressController.start();

		try {
			_checkoutedProject = SharedProject.checkoutProject(getFocusedObject().getCVSRepository(), getFocusedObject(),
					getLocalDirectory(), getLocalName(), progressController);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CommandException e) {
			e.printStackTrace();
		} catch (AuthenticationException e) {
			throw new FlexoAuthentificationException(getFocusedObject().getCVSRepository());
		} finally {
			progressController.stop();
		}
	}

	public SharedProject getCheckoutedProject() {
		return _checkoutedProject;
	}

	private File _localDirectory;
	private String _localName;

	public File getLocalDirectory() {
		return _localDirectory;
	}

	public void setLocalDirectory(File localDirectory) {
		_localDirectory = localDirectory;
	}

	public String getLocalName() {
		if (_localName == null)
			_localName = getFocusedObject().getModuleName();
		return _localName;
	}

	public void setLocalName(String localName) {
		_localName = localName;
	}

	protected class CheckoutProgressController extends CVSAdapter {
		private Hashtable<FPSObject, Vector<FPSObject>> projectHierarchy;
		private long lastReception;
		private Hashtable<CVSModule, Boolean> _hierarchyHasBeenRetrieved;

		protected CheckoutProgressController() {
			if (logger.isLoggable(Level.FINE))
				logger.fine("Retrieving project hierarchy...");

			projectHierarchy = retrieveProjectHerarchy();

			if (logger.isLoggable(Level.FINE))
				for (FPSObject o : projectHierarchy.keySet()) {
					if (o instanceof CVSModule) {
						logger.fine(((CVSModule) o).getFullQualifiedModuleName());
						for (FPSObject o2 : projectHierarchy.get(o)) {
							if (o2 instanceof CVSModule) {
								logger.fine(((CVSModule) o2).getFullQualifiedModuleName());
							} else if (o2 instanceof CVSFile)
								logger.fine(((CVSModule) o).getFullQualifiedModuleName() + '/' + ((CVSFile) o2).getFileName());
						}
					} else if (o instanceof CVSFile)
						logger.fine(((CVSFile) o).getFileName());
				}

			if (logger.isLoggable(Level.FINE))
				logger.fine("Retrieving project hierarchy...Done");

		}

		private FPSObject _currentCheckoutedObject = null;
		private Vector<FPSObject> _alreadyCheckouted = new Vector<FPSObject>();

		private FPSObject objectForString(String name) {
			for (FPSObject o : projectHierarchy.keySet()) {
				if (o instanceof CVSModule && ((CVSModule) o).getModuleName().equals(name))
					return o;
				if (o instanceof CVSFile && ((CVSFile) o).getFileName().equals(name))
					return o;
			}
			return null;
		}

		@Override
		public void fileAdded(FileAddedEvent e) {
			String relativePath;
			try {
				relativePath = FileUtils.makeFilePathRelativeToDir(new File(e.getFilePath()), getLocalDirectory());
				if (logger.isLoggable(Level.FINE))
					logger.fine("FileAdded: " + e.getFilePath() + " relative path=" + relativePath);

				StringTokenizer st = new StringTokenizer(relativePath, "/" + "\\");
				if (st.hasMoreTokens())
					st.nextToken(); // Skip root location
				else
					return;

				if (st.hasMoreTokens()) {
					String current = st.nextToken();
					FPSObject currentCheckoutedObject = objectForString(current);
					if (currentCheckoutedObject != _currentCheckoutedObject) {
						_currentCheckoutedObject = currentCheckoutedObject;
						setProgress(FlexoLocalization.localizedForKeyWithParams("checkouting_($0)", current));
						resetSecondaryProgress(projectHierarchy.get(currentCheckoutedObject).size() + 1);
					} else {
						setSecondaryProgress(FlexoLocalization.localizedForKeyWithParams("checkouting_($0)", relativePath));
					}
				} else
					return;

			} catch (IOException e1) {
				e1.printStackTrace();
			}

		}

		public void start() {
			makeFlexoProgress(FlexoLocalization.localizedForKeyWithParams("checkouting_($0)_to_($1)", getFocusedObject()
					.getFullQualifiedModuleName(), getLocalDirectory().getAbsolutePath()), projectHierarchy.keySet().size() + 1);
			setProgress(FlexoLocalization.localizedForKey("sending_checkout_request"));
		}

		public void stop() {
			hideFlexoProgress();
		}

		private Hashtable<FPSObject, Vector<FPSObject>> retrieveProjectHerarchy() {
			makeFlexoProgress(FlexoLocalization.localizedForKey("preparing_checkout"), 4);
			setProgress(FlexoLocalization.localizedForKey("explore_cvs_module"));

			final Hashtable<FPSObject, Vector<FPSObject>> response = new Hashtable<FPSObject, Vector<FPSObject>>();
			_hierarchyHasBeenRetrieved = new Hashtable<CVSModule, Boolean>();

			exploreModule(getFocusedObject(), response);

			waitProjectHierarchyRetrievingResponses();

			hideFlexoProgress();

			return response;
		}

		private void exploreModule(final CVSModule module, final Hashtable<FPSObject, Vector<FPSObject>> response) {
			if (logger.isLoggable(Level.FINE))
				logger.fine("Exploring module " + module.getFullyQualifiedName() + " parent=" + module.getParent());

			_hierarchyHasBeenRetrieved.put(module, false);
			module.exploreModule(new CVSExplorerListener() {
				@Override
				public void exploringFailed(CVSExplorable explorable, CVSExplorer explorer, Exception exception) {
					lastReception = System.currentTimeMillis();
					if (logger.isLoggable(Level.FINE))
						logger.fine("exploring FAILED");
					_hierarchyHasBeenRetrieved.put(module, true);
				}

				@Override
				public void exploringSucceeded(CVSExplorable explorable, CVSExplorer explorer) {
					lastReception = System.currentTimeMillis();
					setProgress(FlexoLocalization.localizedForKey("explore_sub_modules"));
					if (logger.isLoggable(Level.FINE))
						logger.fine(module.getFullQualifiedModuleName() + " : exploring SUCCEEDED " + module.getCVSModules());
					if (module == getFocusedObject()) {
						modulesToNotify.add(module);
						for (CVSFile f : module.getCVSFiles()) {
							response.put(f, new Vector<FPSObject>());
						}
						for (CVSModule m : module.getCVSModules()) {
							response.put(m, new Vector<FPSObject>());
							exploreModule(m, response);
						}
					} else if (response.get(module) != null) {
						for (CVSFile f : module.getCVSFiles()) {
							response.get(module).add(f);
						}
						for (CVSModule m : module.getCVSModules()) {
							response.get(module).add(m);
						}
						modulesToNotify.add(module);
					}
					_hierarchyHasBeenRetrieved.put(module, true);
				}
			});

		}

		private boolean someModulesAreStillToWait() {
			for (Boolean receivedResponse : _hierarchyHasBeenRetrieved.values()) {
				if (!receivedResponse)
					return true;
			}
			return false;
		}

		private static final long TIME_OUT = CVSConstants.TIME_OUT; // 60 s

		protected Vector<CVSModule> modulesToNotify = new Vector<CVSModule>();

		private synchronized void waitProjectHierarchyRetrievingResponses() {
			lastReception = System.currentTimeMillis();

			while (someModulesAreStillToWait() && System.currentTimeMillis() - lastReception < TIME_OUT) {
				synchronized (this) {
					try {
						wait(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				while (modulesToNotify.size() > 0) {
					CVSModule module = modulesToNotify.firstElement();
					modulesToNotify.removeElementAt(0);
					if (module == getFocusedObject())
						resetSecondaryProgress(module.getCVSModules().size() + 1);
					else
						setSecondaryProgress(FlexoLocalization.localizedForKey("explored") + " " + module.getFullQualifiedModuleName());
				}
			}

			if (someModulesAreStillToWait()) {
				// timeOutReceived = true;
				logger.warning("Module exploring finished with time-out expired");
			}

		}
	}

}
