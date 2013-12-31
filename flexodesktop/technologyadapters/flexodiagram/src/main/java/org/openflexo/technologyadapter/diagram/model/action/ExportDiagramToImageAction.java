/*
 * (c) Copyright 2013 Openflexo
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
package org.openflexo.technologyadapter.diagram.model.action;

import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoObject.FlexoObjectImpl;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoGUIAction;
import org.openflexo.foundation.resource.ScreenshotBuilder.ScreenshotImage;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.swing.ImageUtils;
import org.openflexo.swing.ImageUtils.ImageType;
import org.openflexo.technologyadapter.diagram.model.DiagramElement;

/**
 * @author vincent leilde
 * 
 */
public class ExportDiagramToImageAction extends FlexoGUIAction<ExportDiagramToImageAction, DiagramElement<?>, DiagramElement<?>> {

	private static final Logger logger = Logger.getLogger(ExportDiagramToImageAction.class.getPackage().getName());

	public static final FlexoActionType<ExportDiagramToImageAction, DiagramElement<?>, DiagramElement<?>> actionType = new FlexoActionType<ExportDiagramToImageAction, DiagramElement<?>, DiagramElement<?>>(
			"export_diagram_to_image", FlexoActionType.docGroup) {

		@Override
		public boolean isEnabledForSelection(DiagramElement<?> object, Vector<DiagramElement<?>> globalSelection) {
			return true;
		}

		@Override
		public boolean isVisibleForSelection(DiagramElement<?> object, Vector<DiagramElement<?>> globalSelection) {
			return true;
		}

		@Override
		public ExportDiagramToImageAction makeNewAction(DiagramElement<?> focusedObject, Vector<DiagramElement<?>> globalSelection,
				FlexoEditor editor) {
			return new ExportDiagramToImageAction(focusedObject, globalSelection, editor);
		}

	};

	static {
		FlexoObjectImpl.addActionForClass(ExportDiagramToImageAction.actionType, DiagramElement.class);
	}

	/**
	 * @param actionType
	 * @param focusedObject
	 * @param globalSelection
	 * @param editor
	 */
	protected ExportDiagramToImageAction(DiagramElement<?> focusedObject, Vector<DiagramElement<?>> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	private ScreenshotImage screenshot;

	private File dest;

	public boolean saveAsJpeg() {
		dest = null;
		JFileChooser chooser = new JFileChooser() {
			@Override
			public void approveSelection() {
				File f = getSelectedFile();
				if (f.exists() && getDialogType() == SAVE_DIALOG) {
					int result = JOptionPane.showConfirmDialog(this, "The file exists, overwrite?", "Existing file",
							JOptionPane.YES_NO_CANCEL_OPTION);
					switch (result) {
					case JOptionPane.YES_OPTION:
						super.approveSelection();
						return;
					case JOptionPane.NO_OPTION:
						return;
					case JOptionPane.CLOSED_OPTION:
						return;
					case JOptionPane.CANCEL_OPTION:
						cancelSelection();
						return;
					}
				}
				if (!f.exists() && getDialogType() == SAVE_DIALOG) {
					super.approveSelection();
					return;
				}
			}
		};
		chooser.setDialogType(JFileChooser.SAVE_DIALOG);
		chooser.setDialogTitle(FlexoLocalization.localizedForKey("save_as_image", chooser));
		FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG Images", "png");
		chooser.setFileFilter(filter);
		int returnVal = chooser.showSaveDialog(null);
		if (returnVal == JFileChooser.CANCEL_OPTION) {
			return false;
		}
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			if (!chooser.getSelectedFile().getName().toLowerCase().endsWith(".png")) {
				dest = new File(chooser.getSelectedFile().getAbsolutePath() + ".png");
			} else {
				dest = chooser.getSelectedFile();
				// dest = new File(chooser.getSelectedFile().getAbsolutePath());
			}
		}
		if (dest == null) {
			return false;
		}
		if (saveScreenshot() != null) {
			return true;
		} else {
			return false;
		}
	}

	public ScreenshotImage getScreenshot() {
		return screenshot;
	}

	public void setScreenshot(ScreenshotImage screenshot) {
		this.screenshot = screenshot;
	}

	public File saveScreenshot() {
		logger.info("Saving " + dest);
		try {
			ImageUtils.saveImageToFile(getScreenshot().image, dest, ImageType.PNG);
			return dest;
		} catch (IOException e) {
			e.printStackTrace();
			logger.warning("Could not save " + dest.getAbsolutePath());
			return null;
		}
	}

}
