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

import java.io.File;
import java.io.FileFilter;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.Inspectors;
import org.openflexo.fps.dm.CVSRepositoryCreated;
import org.openflexo.fps.dm.CVSRepositoryDeleted;
import org.openflexo.localization.FlexoLocalization;

public class CVSRepositoryList extends FPSObject {

	private static final Logger logger = Logger.getLogger(CVSRepositoryList.class.getPackage().getName());

	private File _storedRepositoryDirectory;

	private Vector<CVSRepository> _repositories;

	public CVSRepositoryList() {
		_repositories = new Vector<CVSRepository>();
	}

	public Vector<CVSRepository> getCVSRepositories() {
		return _repositories;
	}

	public void setCVSRepositories(Vector<CVSRepository> repositories) {
		_repositories = repositories;
		setChanged();
	}

	public void addToCVSRepositories(CVSRepository repository) {
		_repositories.add(repository);
		repository.saveCVSRepositoryLocation();
		repository.setCVSRepositoryList(this);
		setChanged();
		notifyObservers(new CVSRepositoryCreated(repository));
	}

	public void removeFromCVSRepositories(CVSRepository repository) {
		repository.setCVSRepositoryList(null);
		_repositories.remove(repository);
		setChanged();
		notifyObservers(new CVSRepositoryDeleted(repository));
	}

	@Override
	public String getInspectorName() {
		return Inspectors.FPS.CVS_REPOSITORY_LIST_INSPECTOR;
	}

	@Override
	public String getHelpText() {
		return FlexoLocalization.localizedForKey("cvs_repository_list_help_text");
	}

	@Override
	public String getClassNameKey() {
		return "cvs_repository_list";
	}

	public File getStoredRepositoryDirectory() {
		return _storedRepositoryDirectory;
	}

	public void setStoredRepositoryDirectory(File storedRepositoryDirectory) {
		_storedRepositoryDirectory = storedRepositoryDirectory;
	}

	public void loadStoredRepositoryLocation(File storedRepositoryDirectory/*, FlexoEditor editor*/) {
		setStoredRepositoryDirectory(storedRepositoryDirectory);
		if (storedRepositoryDirectory == null || !storedRepositoryDirectory.exists()) {
			return;
		}
		File[] allLocationFiles = storedRepositoryDirectory.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(".cvs");
			}
		});
		for (File f : allLocationFiles) {
			CVSRepository rep = new CVSRepository(f);
			addToCVSRepositories(rep);
			// SwingUtilities.invokeLater(new RetrieveModuleRunnable(rep,editor));
		}
	}

	/*public static class RetrieveModuleRunnable implements Runnable
	{
		private CVSRepository _rep;
		private FlexoEditor _editor;

		public RetrieveModuleRunnable(CVSRepository rep, FlexoEditor editor)
		{
			_rep = rep;
			_editor = editor;
		}
		public void run() {
		   	logger.warning("This implementation is not correct: you should not use FlexoAction primitive from the model !");
	    	// TODO: Please implement this better later
	       	// Used editor will be null
			CVSRefresh.actionType.makeNewAction(_rep,null,_editor).doAction();
		}				
	}*/

	@Override
	public boolean isContainedIn(FPSObject obj) {
		return obj == this;
	}
}
