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
package org.openflexo.ie.view.palette;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.SystemFlavorMap;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.Reader;
import java.net.URI;
import java.util.Iterator;
import java.util.List;

import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.ie.action.ImportImage;
import org.openflexo.foundation.ie.palette.FlexoIEPalette;
import org.openflexo.foundation.ie.palette.FlexoIEPalette.FlexoIEPaletteWidget;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.FlexoController;

public class IEImagePalettePanel extends IEPalettePanel implements FlexoObserver {

	private static String ZERO_CHAR_STRING = "" + (char) 0;

	public IEImagePalettePanel(IEPalette palette, FlexoIEPalette<? extends FlexoIEPaletteWidget> model, String keyName) {
		super(palette, model, keyName);
		new DropTarget(this, DnDConstants.ACTION_COPY, new DropTargetListener() {

			@Override
			public void dropActionChanged(DropTargetDragEvent dtde) {

			}

			@Override
			public void drop(DropTargetDropEvent dtde) {
				try {
					Transferable tr = dtde.getTransferable();
					dtde.acceptDrop(DnDConstants.ACTION_COPY);
					StringBuilder sb = new StringBuilder();
					if (tr.isDataFlavorSupported(java.awt.datatransfer.DataFlavor.javaFileListFlavor)) {
						List<File> fileList = (List<File>) tr.getTransferData(java.awt.datatransfer.DataFlavor.javaFileListFlavor);
						Iterator<File> iterator = fileList.iterator();
						while (iterator.hasNext()) {
							File file = iterator.next();
							importImageFile(sb, file);
						}
					} else {
						DataFlavor[] flavors = tr.getTransferDataFlavors();
						boolean handled = false;
						for (int i = 0; i < flavors.length; i++) {
							if (flavors[i].isRepresentationClassReader()) {
								Reader reader = flavors[i].getReaderForText(tr);
								BufferedReader br = new BufferedReader(reader);
								java.lang.String line = null;
								while ((line = br.readLine()) != null) {
									try {
										// kde seems to append a 0 char to the end of the reader
										if (ZERO_CHAR_STRING.equals(line))
											continue;
										File file = new File(new URI(line));
										importImageFile(sb, file);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						}
					}
					dtde.getDropTargetContext().dropComplete(true);
					if (sb.length() > 0)
						FlexoController.notify(sb.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void dragOver(DropTargetDragEvent dtde) {

			}

			@Override
			public void dragExit(DropTargetEvent dte) {

			}

			@Override
			public void dragEnter(DropTargetDragEvent dtde) {
				for (DataFlavor flavor : dtde.getCurrentDataFlavors()) {
					if (flavor.isFlavorJavaFileListType() || flavor.isRepresentationClassReader()) {
						dtde.acceptDrag(DnDConstants.ACTION_COPY);
						return;
					}
				}
				dtde.rejectDrag();
			}
		}, true, SystemFlavorMap.getDefaultFlavorMap());
	}

	private void importImageFile(StringBuilder error, File file) {
		if (ImportImage.isValidImageFile(file)) {
			ImportImage importImage = ImportImage.actionType.makeNewAction(getModel().getProject(), null, _palette.getController()
					.getEditor());
			importImage.setFileToImport(file);
			importImage.doAction();
		} else {
			error.append(FlexoLocalization.localizedForKey("file") + " " + file.getName() + " "
					+ FlexoLocalization.localizedForKey("does_not_seem_to_be_an_image"));
		}
	}

}