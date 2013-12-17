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
package org.openflexo.fps.view.component;

import java.awt.Dimension;

import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.components.widget.AbstractBrowserSelector;
import org.openflexo.components.widget.AbstractSelectorPanel;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.fps.CVSRepository;
import org.openflexo.fps.CVSRepositoryList;
import org.openflexo.fps.controller.browser.CVSRepositoriesBrowser;

/**
 * Widget allowing to select a CVSRepository
 * 
 * @author sguerin
 * 
 */
public class CVSRepositorySelector extends AbstractBrowserSelector<CVSRepository> {

	protected static final String EMPTY_STRING = "";

	private CVSRepositoryList _repositoryList;

	public CVSRepositorySelector(CVSRepositoryList repositoryList, CVSRepository repository) {
		super(null, repository, CVSRepository.class);
		_repositoryList = repositoryList;
	}

	public CVSRepositorySelector(FlexoProject project, CVSRepository repository, int cols) {
		super(project, repository, CVSRepository.class, cols);
	}

	@Override
	public void delete() {
		super.delete();
		_repositoryList = null;
	}

	public CVSRepositoryList getCVSRepositoryList() {
		return _repositoryList;
	}

	@Override
	protected CVSRepositorySelectorPanel makeCustomPanel(CVSRepository editedObject) {
		return new CVSRepositorySelectorPanel();
	}

	@Override
	public String renderedString(CVSRepository editedObject) {
		if (editedObject != null) {
			return editedObject.getName();
		}
		return EMPTY_STRING;
	}

	protected class CVSRepositorySelectorPanel extends AbstractSelectorPanel<CVSRepository> {
		protected CVSRepositorySelectorPanel() {
			super(CVSRepositorySelector.this);
		}

		@Override
		protected ProjectBrowser createBrowser(FlexoProject project) {
			return new CVSRepositoriesBrowser(_repositoryList);
		}

		@Override
		public Dimension getDefaultSize() {
			Dimension returned = _browserView.getDefaultSize();
			returned.height = returned.height - 100;
			return returned;
		}
	}

}
