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
package org.openflexo.dgmodule.view;

import java.awt.BorderLayout;
import java.awt.event.FocusListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.openflexo.dgmodule.controller.DGController;
import org.openflexo.diff.ComputeDiff;
import org.openflexo.diff.ComputeDiff.DiffReport;
import org.openflexo.diff.merge.DefaultMergedDocumentType;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.rm.cg.ASCIIFile;
import org.openflexo.foundation.rm.cg.ASCIIFileResource;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.foundation.rm.cg.ContentSource;
import org.openflexo.foundation.rm.cg.GenerationStatus;
import org.openflexo.generator.rm.GenerationAvailableFileResource;
import org.openflexo.jedit.JEditTextArea.DisplayContext;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.swing.diff.DiffPanel;
import org.openflexo.toolbox.TokenMarkerStyle;

public class DiffCodeDisplayer extends DocDisplayer {

	protected static final Logger logger = Logger.getLogger(DiffCodeDisplayer.class.getPackage().getName());

	// protected DiffCodeDisplayerComponent _component;
	public DiffCodeDisplayer(GenerationAvailableFileResource resource, DGController controller) {
		super(resource, controller);
	}

	@Override
	public JComponent getComponent() {
		return (JComponent) _component;
	}

	protected interface DiffCodeDisplayerComponent extends CodeDisplayerComponent {
		@Override
		void update();
	}

	@Override
	protected DiffCodeDisplayerComponent buildComponent() {
		_component = null;

		if (getResource() instanceof ASCIIFileResource) {
			_component = new ASCIIFileDiffCodeDisplayer();
		}

		if (_component == null) {
			_component = new ErrorPanel();
		}

		return (DiffCodeDisplayerComponent) _component;
	}

	protected class ErrorPanel extends JTextArea implements DiffCodeDisplayerComponent {
		protected ErrorPanel() {
			super(FlexoLocalization.localizedForKey("problem_accessing_sources_data") + "\nResource: " + getResource() + "\nCode: "
					+ getGeneratedCode() + "\n");
		}

		@Override
		public void update() {
		}

		@Override
		public String getEditedContentForKey(String contentKey) {
			// Interface
			return null;
		}

		@Override
		public void setEditedContent(CGFile file) {
			// Interface
		}

		@Override
		public void setContentSource(ContentSource aContentSource) {
			// Interface
		}

		@Override
		public void addToFocusListener(FocusListener aFocusListener) {
			// Interface
		}

		@Override
		public void applyDisplayContext(DisplayContext context) {

		}

		@Override
		public DisplayContext getDisplayContext() {
			return null;
		}

	}

	@Override
	public void update() {
		if (_component != null) {
			_component.update();
		}
	}

	protected class ASCIIFileDiffCodeDisplayer extends JPanel implements DiffCodeDisplayerComponent {
		protected DiffPanel _diffPanel;
		protected DiffReport _diffReport;

		protected ASCIIFileDiffCodeDisplayer() {
			super(new BorderLayout());
			update();
		}

		@Override
		public void setEditable(boolean isEditable) {
			// Not editable anyway
		}

		@Override
		public void setEditedContent(CGFile file) {
			// Not editable anyway
		}

		@Override
		public String getEditedContentForKey(String contentKey) {
			// Interface: this component is not supposed to be editable
			return null;
		}

		@Override
		public void update() {
			removeAll();
			String leftLabel;
			String rightLabel;
			boolean isLeftOriented;
			if (getCGFile().getGenerationStatus() == GenerationStatus.GenerationRemoved) {
				_diffReport = ComputeDiff.diff(FlexoLocalization.localizedForKey("file_marked_for_deletion"), getContentOnDisk());
				leftLabel = FlexoLocalization.localizedForKey("current_generation");
				rightLabel = FlexoLocalization.localizedForKey("file_to_remove_from_disk");
				isLeftOriented = true;
			} else if (getCGFile().getGenerationStatus() == GenerationStatus.GenerationAdded) {
				_diffReport = ComputeDiff.diff(getPureGeneration(), FlexoLocalization.localizedForKey("file_marked_for_addition"));
				leftLabel = FlexoLocalization.localizedForKey("current_generation");
				rightLabel = FlexoLocalization.localizedForKey("file_to_add_on_disk");
				isLeftOriented = true;
			} else if (getCGFile().getGenerationStatus() == GenerationStatus.GenerationModified) {
				_diffReport = ComputeDiff.diff(getGeneratedMergeContent(), getContentOnDisk());
				leftLabel = FlexoLocalization.localizedForKey("current_generation");
				rightLabel = FlexoLocalization.localizedForKey("file_on_disk");
				isLeftOriented = true;
			} else if (getCGFile().getGenerationStatus() == GenerationStatus.DiskModified) {
				_diffReport = ComputeDiff.diff(getLastAccepted(), getContentOnDisk());
				leftLabel = FlexoLocalization.localizedForKey("last_accepted_version");
				rightLabel = FlexoLocalization.localizedForKey("file_on_disk");
				isLeftOriented = false;
			} else if (getCGFile().getGenerationStatus() == GenerationStatus.DiskRemoved) {
				_diffReport = ComputeDiff.diff(getLastAccepted(), FlexoLocalization.localizedForKey("missing_file"));
				leftLabel = FlexoLocalization.localizedForKey("last_accepted_version");
				rightLabel = FlexoLocalization.localizedForKey("file_on_disk");
				isLeftOriented = false;
			} else if (getCGFile().getGenerationStatus() == GenerationStatus.OverrideScheduled) {
				_diffReport = ComputeDiff.diff(getContentToWriteOnDisk(), getContentOnDisk());
				leftLabel = FlexoLocalization.localizedForKey("overriden_version") + " ["
						+ getCGFile().getScheduledOverrideVersion().getStringRepresentation() + "]";
				rightLabel = FlexoLocalization.localizedForKey("file_on_disk");
				isLeftOriented = true;
			} else {
				logger.warning("I should never access here: status=" + getCGFile().getGenerationStatus());
				return;
			}
			_diffPanel = new DiffPanel(_diffReport, getTokenMarkerStyle(), leftLabel, rightLabel,
					FlexoLocalization.localizedForKey("no_structural_changes"), isLeftOriented);
			_diffPanel.validate();
			add(_diffPanel, BorderLayout.CENTER);
			validate();
		}

		protected TokenMarkerStyle getTokenMarkerStyle() {
			return DefaultMergedDocumentType.getMergedDocumentType(getFileFormat()).getStyle();
		}

		public String getPureGeneration() {
			if (getResource() instanceof ASCIIFileResource) {
				return ((ASCIIFileResource) getResource()).getCurrentGeneration();
			}
			return "???";
		}

		public String getGeneratedMergeContent() {
			return ((ASCIIFile) getResourceData()).getContent(ContentSource.GENERATED_MERGE);
		}

		public String getContentOnDisk() {
			if (getResourceData() == null) {
				return FlexoLocalization.localizedForKey("cannot_find_file") + "\n"
						+ ((CGRepositoryFileResource) getResource()).getResourceFile().getFile().getAbsolutePath();
			}
			return ((ASCIIFile) getResourceData()).getContent(ContentSource.CONTENT_ON_DISK);
		}

		public String getLastAccepted() {
			return ((ASCIIFile) getResourceData()).getContent(ContentSource.LAST_ACCEPTED);
		}

		public String getContentToWriteOnDisk() {
			return ((ASCIIFile) getResourceData()).getContentToWriteOnDisk();
		}

		@Override
		public void setContentSource(ContentSource aContentSource) {
			// Interface
		}

		@Override
		public void addToFocusListener(FocusListener aFocusListener) {
			_diffPanel.getLeftTextArea().addFocusListener(aFocusListener);
			_diffPanel.getRightTextArea().addFocusListener(aFocusListener);
		}

		@Override
		public void applyDisplayContext(DisplayContext context) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Not implemented");
			}
		}

		@Override
		public DisplayContext getDisplayContext() {
			return null;
		}
	}
}
