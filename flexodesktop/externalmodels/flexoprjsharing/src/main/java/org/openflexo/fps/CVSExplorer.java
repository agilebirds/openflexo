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
package org.openflexo.fps;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.netbeans.lib.cvsclient.command.CommandAbortedException;
import org.netbeans.lib.cvsclient.command.CommandException;
import org.netbeans.lib.cvsclient.connection.AuthenticationException;
import org.openflexo.fps.action.FlexoUnknownHostException;

public class CVSExplorer extends FPSObject {
	protected static final Logger logger = Logger.getLogger(CVSExplorer.class.getPackage().getName());

	protected CVSExplorerListener _listener;
	protected CVSExplorable _explorable;
	protected ExploringStatus _status;
	private ExplorerThread _explorerThread;

	public enum ExploringStatus {
		NOT_EXPLORED, EXPLORING, EXPLORED, ERROR,
	}

	public CVSExplorable getExplorable() {
		return _explorable;
	}

	protected CVSExplorer(CVSExplorable explorable, CVSExplorerListener listener) {
		super();
		_explorable = explorable;
		_listener = listener;
		_status = ExploringStatus.NOT_EXPLORED;
		if (logger.isLoggable(Level.FINE))
			logger.fine("Build new CVSExplorer for " + explorable + " controlled by " + listener);
	}

	@Override
	public String getInspectorName() {
		return null;
	}

	@Override
	public String getClassNameKey() {
		return null;
	}

	@Override
	public boolean isContainedIn(FPSObject obj) {
		if (obj instanceof CVSRepositoryList) {
			return _explorable.getCVSRepository().isContainedIn(obj);
		} else if (obj instanceof CVSRepository) {
			return _explorable.getCVSRepository() == obj;
		}
		return (obj == this);
	}

	public synchronized boolean isExploring() {
		return (_status == ExploringStatus.EXPLORING);
	}

	public synchronized boolean isExplored() {
		return (_status == ExploringStatus.EXPLORED);
	}

	public synchronized boolean wasExploringRequested() {
		return (_status == ExploringStatus.EXPLORING || _status == ExploringStatus.EXPLORED);
	}

	public synchronized boolean isError() {
		return (_status == ExploringStatus.ERROR);
	}

	public synchronized void explore() {
		if (logger.isLoggable(Level.FINE))
			logger.fine("Explore for CVSExplorer for " + _explorable + " controlled by " + _listener);
		_status = ExploringStatus.EXPLORING;
		_explorable.notifyWillExplore();
		_explorerThread = new ExplorerThread();
	}

	private class ExplorerThread extends Thread {
		protected ExplorerThread() {
			super("Exploring " + _explorable);
			start();
		}

		@Override
		public void run() {
			if (logger.isLoggable(Level.FINE))
				logger.fine("Start exploration of " + _explorable);
			if (_explorable instanceof CVSRepository) {
				try {
					((CVSRepository) _explorable)._retrieveModules();
					explorationSucceeded();
				} catch (CommandAbortedException e) {
					explorationFailed(e);
				} catch (IOException e) {
					explorationFailed(e);
				} catch (CommandException e) {
					explorationFailed(e);
				} catch (AuthenticationException e) {
					e.printStackTrace();
					if (e.getCause() instanceof UnknownHostException) {
						explorationFailed(new FlexoUnknownHostException((UnknownHostException) e.getCause(), _explorable.getCVSRepository()));
					} else
						explorationFailed(new FlexoAuthentificationException(_explorable.getCVSRepository()));
				}
			} else if (_explorable instanceof CVSModule) {
				try {
					((CVSModule) _explorable)._retrieveSubModules();
					explorationSucceeded();
				} catch (CommandAbortedException e) {
					explorationFailed(e);
				} catch (IOException e) {
					explorationFailed(e);
				} catch (CommandException e) {
					explorationFailed(e);
				} catch (AuthenticationException e) {
					explorationFailed(new FlexoAuthentificationException(_explorable.getCVSRepository()));
				}
			}
		}

		private void explorationSucceeded() {
			_status = ExploringStatus.EXPLORED;
			_explorable.notifyHasExplored();
			if (_listener != null)
				_listener.exploringSucceeded(_explorable, CVSExplorer.this);
		}

		private void explorationFailed(Exception e) {
			if (_listener != null)
				_listener.exploringFailed(_explorable, CVSExplorer.this, e);
			_status = ExploringStatus.ERROR;
			_explorable.notifyHasExplored();
			if (_listener != null)
				_listener.exploringFailed(_explorable, CVSExplorer.this, e);
		}
	}

}
