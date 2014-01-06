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
package org.openflexo.fps.view;

import java.awt.event.FocusListener;
import java.util.logging.Logger;

import javax.swing.JComponent;

import org.openflexo.diff.merge.DefaultMergedDocumentType;
import org.openflexo.fps.CVSFile;
import org.openflexo.fps.controller.FPSController;
import org.openflexo.jedit.cd.GenericCodeDisplayer;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.FileFormat;

public class CodeDisplayer {

	private static final Logger logger = Logger.getLogger(CodeDisplayer.class.getPackage().getName());

	private final CVSFile _cvsFile;
	protected CodeDisplayerComponent _component;
	private final FPSController _controller;

	public CodeDisplayer(CVSFile cvsFile, FPSController controller) {
		super();
		_controller = controller;
		_cvsFile = cvsFile;
		buildComponent();
		if (_controller != null) {
			addToFocusListener(_controller.getFooter());
		}
	}

	public CVSFile getCVSFile() {
		return _cvsFile;
	}

	public ResourceType getResourceType() {
		return _cvsFile.getResourceType();
	}

	public FileFormat getFileFormat() {
		if (getResourceType() != null) {
			return getResourceType().getFormat();
		}
		if (_cvsFile.isBinary()) {
			return FileFormat.UNKNOWN_BINARY_FILE;
		} else {
			return FileFormat.UNKNOWN_ASCII_FILE;
		}
	}

	public JComponent getComponent() {
		return (JComponent) _component;
	}

	protected CodeDisplayerComponent buildComponent() {
		if (getFileFormat().isBinary()) {
			_component = new BinaryFileCodePanel();
		} else {
			_component = new ASCIIFileCodePanel();
		}
		return _component;
	}

	public void update() {
		if (_component != null) {
			_component.update();
		}
	}

	public String getContentOnDisk() {
		String returned = getCVSFile().getContentOnDisk();
		if (returned == null) {
			return FlexoLocalization.localizedForKey("unable_to_retrieve_content_for") + " " + getCVSFile().getFile().getAbsolutePath();
		} else {
			return returned;
		}
	}

	protected String getASCIIContentForFile(CVSFile aFile) {
		if (aFile != null) {
			return aFile.getContentOnDisk();
		}
		return null;
	}

	protected interface CodeDisplayerComponent {
		public void update();

		public void setEditable(boolean isEditable);

		public String getEditedContent();

		public void setEditedContent(CVSFile file);

		public void addToFocusListener(FocusListener aFocusListener);
	}

	protected class ASCIIFileCodePanel extends GenericCodeDisplayer implements CodeDisplayerComponent {
		protected ASCIIFileCodePanel() {
			super(getContentOnDisk(), DefaultMergedDocumentType.getMergedDocumentType(getFileFormat()).getStyle());
		}

		@Override
		public void update() {
			setText(getContentOnDisk());
		}

		@Override
		public String getEditedContent() {
			return getText();
		}

		@Override
		public void setEditedContent(CVSFile file) {
			String newContent = getASCIIContentForFile(file);
			if (newContent != null) {
				setText(newContent);
			}
		}

		@Override
		public void addToFocusListener(FocusListener aFocusListener) {
			addFocusListener(aFocusListener);
		}

	}

	protected class BinaryFileCodePanel extends GenericCodeDisplayer implements CodeDisplayerComponent {
		protected BinaryFileCodePanel() {
			super(getCVSFile().getFile().getAbsolutePath() + "\n" + FlexoLocalization.localizedForKey("binary_file"), null);
		}

		@Override
		public void update() {
			setText(getCVSFile().getFile().getAbsolutePath() + "\n" + FlexoLocalization.localizedForKey("binary_file"));
		}

		@Override
		public String getEditedContent() {
			return getText();
		}

		@Override
		public void setEditedContent(CVSFile file) {
		}

		@Override
		public void addToFocusListener(FocusListener aFocusListener) {
			addFocusListener(aFocusListener);
		}

	}

	public void addToFocusListener(FocusListener aFocusListener) {
		_component.addToFocusListener(aFocusListener);
	}
}
