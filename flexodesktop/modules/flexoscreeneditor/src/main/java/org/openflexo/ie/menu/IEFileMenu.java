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
package org.openflexo.ie.menu;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;

import javax.swing.AbstractAction;

import org.openflexo.components.NewPaletteComponent;
import org.openflexo.components.OpenPaletteComponent;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.ie.action.ImportImage;
import org.openflexo.ie.view.controller.IEController;
import org.openflexo.ie.view.print.PrintComponentAction;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.menu.FileMenu;
import org.openflexo.view.menu.FlexoMenuItem;

/**
 * TODO : Description for this file
 * 
 * @author benoit
 */
public class IEFileMenu extends FileMenu {

	// ==========================================================================
	// ============================= Instance Variables
	// =========================
	// ==========================================================================

	protected IEController _controller;
	public ExportPaletteItem exportPaletteItem;
	public ImportPaletteItem importPaletteItem;
	public ImportImageItem importImageItem;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	public IEFileMenu(IEController controller) {
		super(controller);
		_controller = controller;
	}

	@Override
	public void addSpecificItems() {
	}

	@Override
	protected boolean addExportItems() {
		super.addExportItems();
		addToExportItems(exportPaletteItem = new ExportPaletteItem());
		return true;
	}

	@Override
	protected boolean addImportItems() {
		super.addImportItems();
		addToImportItems(importPaletteItem = new ImportPaletteItem());
		addToImportItems(importImageItem = new ImportImageItem());
		return true;
	}

	@Override
	public void addPrintItems() {
		add(new FlexoMenuItem(PrintComponentAction.actionType, getController()) {
			@Override
			public FlexoModelObject getFocusedObject() {
				if (_controller.getCurrentEditedComponent() != null)
					return _controller.getCurrentEditedComponent().getComponentDefinition();
				else
					return _controller.getProject().getFlexoComponentLibrary();
			}

			@Override
			public Vector getGlobalSelection() {
				return null;
			}
		});
	}

	public IEController getIEController() {
		return _controller;
	}

	public class ExportPaletteItem extends FlexoMenuItem {

		public ExportPaletteItem() {
			super(new ExportPaletteAction(), "export_palette", null, getController(), true);
		}

	}

	public class ExportPaletteAction extends AbstractAction {
		public ExportPaletteAction() {
			super();
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			File paletteDir = NewPaletteComponent.getPaletteDirectory();
			if (paletteDir != null) {
				File[] fileList = getController().getProject().getCustomWidgetPalette().getPaletteDirectory().listFiles();
				for (int i = 0; i < fileList.length; i++) {
					try {
						FileUtils.copyFileToDir(new FileInputStream(fileList[i]), fileList[i].getName(), paletteDir);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public class ImportPaletteItem extends FlexoMenuItem {

		public ImportPaletteItem() {
			super(new ImportPaletteAction(), "import_palette", null, getController(), true);
		}

	}

	public class ImportPaletteAction extends AbstractAction {
		public ImportPaletteAction() {
			super();
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			File paletteDir = OpenPaletteComponent.getPaletteDirectory();
			if (paletteDir != null) {
				File[] fileList = paletteDir.listFiles();
				for (int i = 0; i < fileList.length; i++) {
					if (fileList[i].getName().endsWith(".woxml")) {
						File f = new File(getController().getProject().getIECustomPaletteDirectory(), fileList[i].getName());
						boolean performCopy = true;
						if (f.exists()) {
							performCopy = FlexoController.confirm("A widget named :"
									+ fileList[i].getName().substring(0, fileList[i].getName().length() - 6)
									+ " exists already.\nOverride it ?");
						}
						if (performCopy) {
							try {
								FileUtils.copyFileToDir(new FileInputStream(fileList[i]), fileList[i].getName(), getController()
										.getProject().getIECustomPaletteDirectory());
								File screenshot = new File(fileList[i].getParentFile(), fileList[i].getName().substring(0,
										fileList[i].getName().length() - ".woxml".length())
										+ ".screenshot");
								if (screenshot.exists())
									FileUtils.copyFileToDir(new FileInputStream(screenshot), screenshot.getName(), getController()
											.getProject().getIECustomPaletteDirectory());
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
				getController().getProject().getCustomWidgetPalette().refresh();
			}
		}
	}

	public class ImportImageItem extends FlexoMenuItem {

		public ImportImageItem() {
			super(new ImportImageAction(), "import_image", null, getController(), true);
		}

	}

	public class ImportImageAction extends AbstractAction {
		public ImportImageAction() {
			super();
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			ImportImage importImage = ImportImage.actionType.makeNewAction(null, null, getController().getEditor());
			importImage.doAction();
			// getController().getProject().notifyCustomWidgetPaletteNeedRefresh();
		}
	}

}
