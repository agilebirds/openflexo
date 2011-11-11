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
package org.openflexo.dm.view;

import java.io.File;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JSplitPane;

import org.openflexo.AdvancedPrefs;
import org.openflexo.components.tabular.TabularViewAction;
import org.openflexo.dm.model.DMEOEntityTableModel;
import org.openflexo.dm.model.DMEOModelTableModel;
import org.openflexo.dm.model.DMReadOnlyEOEntityTableModel;
import org.openflexo.dm.view.controller.DMController;
import org.openflexo.dm.view.controller.DMSelectionManager;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.dm.action.CreateDMEOEntity;
import org.openflexo.foundation.dm.action.CreateDMEOModel;
import org.openflexo.foundation.dm.action.DMDelete;
import org.openflexo.foundation.dm.action.ImportDMEOModel;
import org.openflexo.foundation.dm.eo.DMEOEntity;
import org.openflexo.foundation.dm.eo.DMEOModel;
import org.openflexo.foundation.dm.eo.DMEORepository;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.ProjectRestructuration;
import org.openflexo.localization.FlexoLocalization;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class DMEORepositoryView extends DMView<DMEORepository> {

	private DMEOModelTableModel eoModelTableModel;

	protected DMTabularView eoModelTable;

	private DMEOEntityTableModel eoEntityTableModel;

	protected DMTabularView eoEntityTable;

	public DMEORepositoryView(DMEORepository repository, DMController controller) {
		super(repository, controller, "repository_($name)");

		addAction(new TabularViewAction(CreateDMEOModel.actionType, controller.getEditor()) {
			@Override
			protected Vector getGlobalSelection() {
				return getViewSelection();
			}

			@Override
			protected FlexoModelObject getFocusedObject() {
				return getDMEORepository();
			}
		});

		addAction(new TabularViewAction(ImportDMEOModel.actionType, controller.getEditor()) {
			@Override
			protected Vector getGlobalSelection() {
				return getViewSelection();
			}

			@Override
			protected FlexoModelObject getFocusedObject() {
				return getDMEORepository();
			}
		});

		addAction(new TabularViewAction(CreateDMEOEntity.actionType, controller.getEditor()) {
			@Override
			protected Vector getGlobalSelection() {
				return getViewSelection();
			}

			@Override
			protected FlexoModelObject getFocusedObject() {
				return getSelectedDMEOModel();
			}
		});

		addAction(new TabularViewAction(DMDelete.actionType, controller.getEditor()) {
			@Override
			protected Vector getGlobalSelection() {
				return getViewSelection();
			}

			@Override
			protected FlexoModelObject getFocusedObject() {
				return null;
			}
		});

		finalizeBuilding();
	}

	@Override
	protected JComponent buildContentPane() {
		eoModelTableModel = new DMEOModelTableModel(getDMEORepository(), getDMController().getProject());
		addToMasterTabularView(eoModelTable = new DMTabularView(getDMController(), eoModelTableModel, 10));

		if (getDMEORepository().isReadOnly())
			eoEntityTableModel = new DMReadOnlyEOEntityTableModel(null, getDMController().getProject());
		else
			eoEntityTableModel = new DMEOEntityTableModel(null, getDMController().getProject());
		addToSlaveTabularView(eoEntityTable = new DMTabularView(getDMController(), eoEntityTableModel, 15), eoModelTable);

		return new JSplitPane(JSplitPane.VERTICAL_SPLIT, eoModelTable, eoEntityTable);
	}

	public DMEORepository getDMEORepository() {
		return getDMObject();
	}

	public DMEOModel getSelectedDMEOModel() {
		DMSelectionManager sm = getDMController().getDMSelectionManager();
		Vector selection = sm.getSelection();
		if ((selection.size() == 1) && (selection.firstElement() instanceof DMEOModel)) {
			return (DMEOModel) selection.firstElement();
		}
		if (getSelectedDMEOEntity() != null) {
			return getSelectedDMEOEntity().getDMEOModel();
		}
		return null;
	}

	public DMEOEntity getSelectedDMEOEntity() {
		DMSelectionManager sm = getDMController().getDMSelectionManager();
		Vector selection = sm.getSelection();
		if ((selection.size() == 1) && (selection.firstElement() instanceof DMEOEntity)) {
			return (DMEOEntity) selection.firstElement();
		}
		return null;
	}

	public abstract static class EOModelChooserComponent extends JFileChooser {

		public EOModelChooserComponent() {
			super();
			setCurrentDirectory(AdvancedPrefs.getLastVisitedDirectory());
			setDialogTitle(FlexoLocalization.localizedForKey("select_an_eomodel"));
			setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			setFileFilter(new javax.swing.filechooser.FileFilter() {
				@Override
				public boolean accept(File f) {
					if (f.isDirectory()) {
						return true;
					}
					return false;
				}

				@Override
				public String getDescription() {
					return FlexoLocalization.localizedForKey("directories");
				}
			});

		}
	}

	public static class NewEOModelComponent extends EOModelChooserComponent {

		protected NewEOModelComponent(FlexoProject project) {
			super();
			setCurrentDirectory(ProjectRestructuration.getExpectedDataModelDirectory(project.getProjectDirectory()));
		}

		public static File getEOModelDirectory(FlexoProject project) {
			NewEOModelComponent chooser = new NewEOModelComponent(project);
			File newEOModelDir = null;
			int returnVal = chooser.showSaveDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				newEOModelDir = chooser.getSelectedFile();
				if (!newEOModelDir.getName().endsWith(".eomodeld")) {
					newEOModelDir = new File(newEOModelDir.getAbsolutePath() + ".eomodeld");
				}
			}
			return newEOModelDir;
		}

	}

	public static class OpenEOModelComponent extends EOModelChooserComponent {

		protected OpenEOModelComponent() {
			super();
			setFileFilter(DMEOModel.EOMODEL_FILE_FILTER);
			setFileView(DMEOModel.EOMODEL_FILE_VIEW);
		}

		public static File getEOModelDirectory() {
			OpenEOModelComponent chooser = new OpenEOModelComponent();
			File returned = null;
			int returnVal = chooser.showOpenDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				returned = chooser.getSelectedFile();
			}
			return returned;
		}
	}

	public DMTabularView getEoEntityTable() {
		return eoEntityTable;
	}

	public DMTabularView getEoModelTable() {
		return eoModelTable;
	}

	/**
	 * Overrides willShow
	 * 
	 * @see org.openflexo.view.ModuleView#willShow()
	 */
	@Override
	public void willShow() {
		// TODO Auto-generated method stub

	}

	/**
	 * Overrides willHide
	 * 
	 * @see org.openflexo.view.ModuleView#willHide()
	 */
	@Override
	public void willHide() {
		// TODO Auto-generated method stub

	}

}
